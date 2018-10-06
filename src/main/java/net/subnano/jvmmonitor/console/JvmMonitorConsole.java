package net.subnano.jvmmonitor.console;

import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;
import net.subnano.console.Ansi;
import net.subnano.console.Ansi.Color;
import net.subnano.jvmmonitor.model.GcEvent;
import net.subnano.jvmmonitor.model.HeapSample;
import net.subnano.jvmmonitor.model.JvmEvent;
import net.subnano.jvmmonitor.monitor.HeapNames;
import net.subnano.jvmmonitor.monitor.HostMonitor;
import net.subnano.jvmmonitor.recorder.EventRecorder;
import net.subnano.jvmmonitor.settings.DefaultMonitorSettings;
import net.subnano.jvmmonitor.settings.MonitorSettings;
import net.subnano.jvmmonitor.util.ByteUtil;
import net.subnano.jvmmonitor.util.Strings;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.concurrent.locks.LockSupport;

/**
 * TODO
 *
 * - Need to handled stopped processes better
 * - Keep an update time to only update the rows that have changed
 * - Figure out how to avoid conflict with log messages
 * - Need to avoid creating objects
 * - add input key support
 * - cell colors should be function based
 * - alloc rate should be red when > 100 (or whatever value)
 *
 * @author Mark Wardell
 */
public class JvmMonitorConsole implements EventRecorder<JvmEvent> {

    private static final long DEFAULT_CONSOLE_REFRESH_INTERVAL = 1500;

    private final HostMonitor hostMonitor;
    private final long consoleRefreshInterval = DEFAULT_CONSOLE_REFRESH_INTERVAL;
    private final ProcessInfoArray processInfos = new ProcessInfoArray(8);
    private final HardwareAbstractionLayer hal;
    private final OperatingSystem os;
    private final JvmInfoGrid grid;

    private long nextConsoleRefreshTime = 0;

    JvmMonitorConsole(MonitorSettings settings) {
        this.hostMonitor = new HostMonitor(settings, this);
        SystemInfo si = new SystemInfo();
        this.hal = si.getHardware();
        this.os = si.getOperatingSystem();
        this.grid = new JvmInfoGrid(8, 13);
    }

    public static void main(String[] args) {
//        if (!JvmMonitorConsole.isSupported()) {
//            System.err.println("Not running in a supported console!");
//            System.exit(-1);
//        }
        MonitorSettings monitorSettings = DefaultMonitorSettings.newInstance(args);
        JvmMonitorConsole jvmMonitorConsole = new JvmMonitorConsole(monitorSettings);
        try {
            jvmMonitorConsole.start();
        } catch (IOException e) {
            System.err.println("Error starting Jvm monitor: " + e.toString());
            e.printStackTrace();
        }
    }

    public static boolean isSupported() {
        POSIX posix = POSIXFactory.getPOSIX();
        return posix.isatty(FileDescriptor.out);
    }

    private void start() throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        hostMonitor.start();
        while (Thread.currentThread().isAlive()) {
            LockSupport.parkNanos(1_000_000L);
        }
    }

    void stop() {
        grid.reset();
        try {
            hostMonitor.stop();
        } catch (IOException e) {
            System.err.println("Error caught stopping Jvm monitor.");
            e.printStackTrace();
        }
    }

    @Override
    public void record(JvmEvent event) throws IOException {
        if (event instanceof GcEvent) {
            handle((GcEvent)event);
        } else if (event instanceof HeapSample) {
            handle((HeapSample)event);
        }
    }

    @Override
    public void connect() {
        // NO-OP
    }

    @Override
    public void close() {
        // NO-OP
    }

    private void updateConsole(long timestamp) {
        if (timestamp < nextConsoleRefreshTime) {
            return;
        }
        //IntIterator iterator = processInfos.iterator();
        //int key;
        int row = 0;
        ProcessInfo info;
        OSProcess osProcess;
        while ((info = processInfos.get(row)) != null) {
            osProcess = os.getProcess(info.pid);

            // detect dead processes
            if (osProcess == null) {
                processInfos.remove(row);
                // now get the next row
                info = processInfos.get(row);
                osProcess = os.getProcess(info.pid);
                // TODO what if the next process has also just stopped?
                // TODO need to paint last empty row
            }
            double cpuPercent = osProcess == null ? 0 : osProcess.calculateCpuPercent() * 100.0D;
            int threadCount = osProcess == null ? 0 : osProcess.getThreadCount();
            grid.setValue(row, 0, info.pid);
            grid.setValue(row, 1, cpuPercent, 1);
            grid.setValue(row, 2, threadCount);

            grid.setColor(row, 3, Color.Cyan);
            grid.setColor(row, 4, Color.Cyan);
            grid.setColor(row, 5, Color.Cyan);

            // OldGen Collections
            grid.setColor(row, 7, info.oldCollections == 0 ? Color.White : Color.Red);

            grid.setColor(row, 9, Color.Yellow);
            grid.setColor(row, 10, Color.Yellow);


            grid.setValue(row, 3, ByteUtil.toMB(info.heapUsed), 1);
            grid.setValue(row, 4, getScaledByteText(info.heapCapacity, 1));
            grid.setValue(row, 5, info.heapPercent(), 1);

            grid.setValue(row, 6, info.collections);
            grid.setValue(row, 7, info.oldCollections);
            grid.setValue(row, 8, info.totalPauseTime, 1);
            grid.setValue(row, 9, info.avgPauseTime(), 2);
            grid.setValue(row, 10, getScaledByteText(info.alloc, 1));
            grid.setValue(row, 11, ByteUtil.toMB(info.allocatedBytesPerSec()), 1);
            grid.setValue(row, 12, Strings.padRight(info.mainClass, 30));
            row++;
        }
        grid.refresh();

        // calculate time for next console update
        nextConsoleRefreshTime = timestamp + consoleRefreshInterval;
    }

    private String getScaledByteText(long bytes, int precision) {
        if (bytes < ByteUtil.GB) {
            return String.format("%dM", (int) ByteUtil.toMB(bytes));
        }
        return String.format("%." + precision + "fG", ByteUtil.toGB(bytes));
    }

    private void handle(GcEvent event) {
        ProcessInfo info = getProcessInfo(event);
        info.addCollection(event);
        updateConsole(event.timestamp());
    }

    private void handle(HeapSample event) {
        ProcessInfo info = getProcessInfo(event);
        if (HeapNames.YOUNG_GEN.equals(event.name())) {
            info.updateHeap(event);
        }
        updateConsole(event.timestamp());
    }

    private ProcessInfo getProcessInfo(JvmEvent event) {
        ProcessInfo info = processInfos.findByPid(event.pid());
        if (info == null) {
            info = new ProcessInfo(event.pid(), event.mainClass(), event.timestamp());
            processInfos.add(info);
        }
        return info;
    }

    class ProcessInfoArray {
        private final ProcessInfo[] array;
        private final int maxIndex;
        int count = 0;

        ProcessInfoArray(int size) {
            array = new ProcessInfo[size];
            maxIndex = size - 1;
        }

        void add(ProcessInfo info) {
            if (count == maxIndex)
                throw new ArrayIndexOutOfBoundsException("Array already contains " + count + " items");
            array[count++] = info;
        }

        ProcessInfo get(int index) {
            if (count > maxIndex )
                throw new ArrayIndexOutOfBoundsException("Index exceeds array capacity " + maxIndex);
            return array[index];
        }

        void remove(int index) {
            if (count > maxIndex )
                throw new ArrayIndexOutOfBoundsException("Index exceeds array capacity " + maxIndex);
            int row = index;
            while (row < maxIndex && array[row] != null) {
                array[row] = array[row+1];
            }
            count--;
        }

        ProcessInfo findByPid(int pid) {
            for (int i=0; i<count; i++) {
                if (array[i].pid == pid) {
                    return array[i];
                }
            }
            return null;
        }
    }

    public class ProcessInfo {

        final int pid;
        final String mainClass;
        final long timeCreated;

        long collectionTime;
        long heapTime;
        double totalPauseTime = 0.0d;
        int collections;
        int oldCollections;
        long heapUsed;
        long heapCapacity;
        long alloc;

        ProcessInfo(int pid, String mainClass, long timestamp) {
            this.pid = pid;
            this.mainClass = mainClass;
            this.timeCreated = timestamp;
        }

        public void addCollection(GcEvent event) {
            if (HeapNames.YOUNG_GEN.equals(event.space())) {
                collections++;
            } else {
                oldCollections++;
            }
            totalPauseTime += event.pauseTime();
            collectionTime = event.timestamp();
        }

        public void updateHeap(HeapSample event) {
            heapTime = event.timestamp();
            long used = event.heapUsed();
            long capacity = event.heapCapacity();
            // simple case when heap is greater than last reading
            if (used > heapUsed) {
                alloc += (used - heapUsed);
            }

            // harder case last heap capacity - last heap used + current heap used
            else {
                alloc += ((heapCapacity - heapUsed) + used);
            }
            heapUsed = used;
            heapCapacity = capacity;
        }

        double heapPercent() {
            return (heapUsed * 100) / (double) heapCapacity;
        }

        long allocatedBytesPerSec() {
            long elapsedTimeSecs = (heapTime - timeCreated) / 1000;
            return elapsedTimeSecs == 0 ? 0 : alloc / elapsedTimeSecs;
        }

        double avgPauseTime() {
            return collections == 0 ? 0.0 : totalPauseTime / (double) collections;
        }

    }
}
