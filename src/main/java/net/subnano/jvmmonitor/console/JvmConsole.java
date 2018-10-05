package net.subnano.jvmmonitor.console;

import io.nano.core.collection.IntIterator;
import io.nano.core.collection.IntObjectMap;
import io.nano.core.collection.NanoIntObjectMap;
import jnr.posix.POSIX;
import jnr.posix.POSIXFactory;
import net.subnano.console.Ansi;
import net.subnano.console.ConsoleWriter;
import net.subnano.jvmmonitor.model.GcEvent;
import net.subnano.jvmmonitor.model.HeapSample;
import net.subnano.jvmmonitor.model.JvmEvent;
import net.subnano.jvmmonitor.recorder.EventRecorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * TODO
   PID CPU% Threads HeapMax HeapUsed Heap% GCs Alloc AllocRate MainClass
 22787                                     58                  MemoryHog
 23076                                     2                   Launcher
 21260                                     1                   Launcher
 22781                                     1                   Launcher
 *
 * 1 - Keep an update time to only update the rows that have changed
 * 2 - Need to listen to VM events to remove rows from list
 * 3 - Space out columns correctly
 * 4 - Figure out how to avoid conflict with log messages
 * 5 - Need to display columns right aligned
 * 6 - Need to avoid creating objects
 * 7 - Need to pad value to overwrite older data
 *
 * @author Mark Wardell
 */
public class JvmConsole implements EventRecorder<JvmEvent> {

    private static final Logger LOGGER = LogManager.getLogger(JvmConsole.class);

    private final EventRecorder<JvmEvent> delegateRecorder;
    private final ConsoleWriter console;
    private final IntObjectMap<ProcessInfo> processInfos = new NanoIntObjectMap<>(16, 0.75f);
    private final boolean isSupported;

    public JvmConsole(EventRecorder<JvmEvent> delegateRecorder) {
        this.delegateRecorder = delegateRecorder;
        this.isSupported = isSupported();
        this.console = isSupported? new ConsoleWriter(System.out) : null;
        if (isSupported) {
            LOGGER.info("Console IS supported");
            initConsole();
        }
        else {
            LOGGER.info("Console is NOT supported");
        }
    }

    @Override
    public void record(JvmEvent event) throws IOException {
        // pass event on to delegate
        delegateRecorder.record(event);
        if (isSupported) {
            if (event instanceof GcEvent) {
                handle((GcEvent)event);
            } else if (event instanceof HeapSample) {
                handle((HeapSample)event);
            }
        }
    }

    public static boolean isSupported() {
        POSIX posix = POSIXFactory.getPOSIX();
        return posix.isatty(FileDescriptor.out);
    }

    private void initConsole() {
        // now update console
        console.clearScreen();
        // TODO decorate heading
        console.cursor(1, 1);
        console.print("  PID CPU% Threads HeapMax HeapUsed Heap% GCs Alloc AllocRate MainClass");
        console.fg(Ansi.Color.Yellow);
        for (int row = 2; row <= 8; row++) {
            console.cursor(row, 1);
            console.print("");
        }
        //console.line();
        console.display();
    }

    private void updateConsole() {
        IntIterator iterator = processInfos.iterator();
        int key;
        int row = 2;
        while ((key = iterator.nextKey()) != -1) {
            ProcessInfo info = processInfos.get(key);
            //System.out.println(info);
            console.cursor(row, 1);
            console.print(String.valueOf(info.pid));

            console.cursor(row, 43);
            console.print(String.valueOf(info.collections));

            console.cursor(row, 63);
            console.print(String.valueOf(info.mainClass));
            row++;
        }
        console.display();
    }

    private void handle(GcEvent event) {
        int pid = event.pid();
        ProcessInfo info = processInfos.get(pid);
        if (info == null) {
            info = new ProcessInfo(pid, event.mainClass(), event.timestamp());
            processInfos.put(pid, info);
        }
        info.add(event.pauseTime());

        updateConsole();

    }

    private void handle(HeapSample sample) {

    }

    @Override
    public void connect() throws IOException {
        // NO-OP
    }

    @Override
    public void close() throws IOException {
        // NO-OP
    }

    private class ProcessInfo {

        final int pid;
        final String mainClass;
        final long originTime;

        double totalPauseTime = 0.0d;
        int collections;

        ProcessInfo(int pid, String mainClass, long timestamp) {
            this.pid = pid;
            this.mainClass = mainClass;
            this.originTime = timestamp;
        }

        public void add(float pauseTime) {
            totalPauseTime += pauseTime;
            collections++;
        }

        @Override
        public String toString() {
            return "ProcessInfo{"
                    + "pid="
                    + pid
                    + ", mainClass='"
                    + mainClass
                    + '\''
                    + ", originTime="
                    + originTime
                    + ", totalPauseTime="
                    + totalPauseTime
                    + ", collections="
                    + collections
                    + '}';
        }
    }
}
