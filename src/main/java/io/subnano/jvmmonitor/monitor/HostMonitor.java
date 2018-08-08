package io.subnano.jvmmonitor.monitor;

import io.subnano.jvmmonitor.recorder.EventRecorder;
import io.subnano.jvmmonitor.recorder.KdbEventRecorder;
import io.subnano.jvmmonitor.settings.MonitorSettings;
import io.subnano.jvmmonitor.util.MonitorUtil;
import io.subnano.jvmmonitor.util.SystemUtil;
import io.subnano.jvmmonitor.util.ThreadFactories;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.monitor.event.HostEvent;
import sun.jvmstat.monitor.event.HostListener;
import sun.jvmstat.monitor.event.VmStatusChangeEvent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HostMonitor {

    private static final Logger LOGGER = LogManager.getLogger(HostMonitor.class);
    private static final int YOUNG_GEN = 0;
    private static final int OLD_GEN = 1;

    private String hostName;
    private final MonitoredHost monitoredHost;
    private final MonitorSettings settings;
    private final ScheduledExecutorService executor;
    private final List<VmMonitor> monitoredVMs = new CopyOnWriteArrayList<>();
    private final EventRecorder eventRecorder;

    public HostMonitor(MonitorSettings settings, KdbEventRecorder eventRecorder) {
        this.eventRecorder = eventRecorder;
        this.hostName = SystemUtil.getHostName();
        this.settings = settings;
        this.monitoredHost = newMonitoredHost();
        try {
            this.monitoredHost.addHostListener(new MonitorHostListener());
        } catch (MonitorException e) {
            // local MonitoredHostProvider does not implement this exception
        }
        this.executor = Executors.newSingleThreadScheduledExecutor(
                ThreadFactories.newThreadFactory("JvmMonitor", false)
        );
    }

    private MonitoredHost newMonitoredHost() {
        HostIdentifier hostIdentifier;
        try {
            hostIdentifier = new HostIdentifier((String) null);
            return MonitoredHost.getMonitoredHost(hostIdentifier);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void start() throws IOException {
        // get the set of active JVMs
        // decided against using this as it isn't ideal
        // really need to write own efficient way of monitoring list of VMs
        //Set<Integer> jvms = monitoredHost.activeVms();
        long minInterval = getMinInterval(settings);
        LOGGER.info("Scheduling primary monitor thread every {} ms", minInterval);
        executor.scheduleAtFixedRate(new VMCheck(), 10, minInterval, TimeUnit.MILLISECONDS);
        eventRecorder.connect();
    }

    private static long getMinInterval(MonitorSettings settings) {
        long gcMin = Math.min(settings.gcIntervalYoungGen(), settings.gcIntervalOldGen());
        long heapMin = Math.min(settings.heapSampleIntervalYoungGen(), settings.heapSampleIntervalOldGen());
        return Math.min(gcMin, heapMin);
    }

    public void stop() throws IOException {
        eventRecorder.close();
        executor.shutdown();
    }

    private class MonitorHostListener implements HostListener {
        @Override
        public void vmStatusChanged(VmStatusChangeEvent event) {
            event.getStarted().forEach(this::addVm);
            event.getTerminated().forEach(this::removeVm);
        }

        /**
         * Needs optimizing - creates lots of garbage if a processes are created rapidly
         */
        private void addVm(Object o) {
            int vmId = (int) o;
            String vmIdString = "//" + vmId + "?mode=r";
            try {
                VmIdentifier vmIdentifier = new VmIdentifier(vmIdString);
//                if (vmId == 23754)
//                    PerfDataReader.dump2(vmIdentifier);
//                PerfDataBuffer perfDataBuffer = new PerfDataBuffer(vmIdentifier);
//                perfDataBuffer.findByName();
                // autoboxing int not ideal
                MonitoredVm vm = monitoredHost.getMonitoredVm(vmIdentifier, 0);
                LOGGER.info("VM started {} ({})", MonitorUtil.mainClass(vm), vmId);
                monitoredVMs.add(new GcEventMonitor(vm, hostName, settings.gcIntervalYoungGen(), eventRecorder, YOUNG_GEN));
                monitoredVMs.add(new GcEventMonitor(vm, hostName, settings.gcIntervalOldGen(), eventRecorder, OLD_GEN));
                monitoredVMs.add(new HeapSampleMonitor(vm, hostName, settings.heapSampleIntervalYoungGen(), eventRecorder, YOUNG_GEN));
                monitoredVMs.add(new HeapSampleMonitor(vm, hostName, settings.heapSampleIntervalOldGen(), eventRecorder, OLD_GEN));

            } catch (URISyntaxException e) {
                LOGGER.warn("Unable to create VmIdentifier from {}", vmIdString);
            } catch (MonitorException e) {
                LOGGER.warn("Unable to create MonitoredVm from {}", vmIdString);
            }
        }

        private void removeVm(Object o) {
            int vmId = (int) o;
            LOGGER.info("VM terminated pid={}", vmId);
            monitoredVMs.removeIf(vmMonitor -> vmMonitor.pid() == vmId);
        }

        @Override
        public void disconnected(HostEvent event) {
            LOGGER.error("Disconnected from LocalMonitoredHostProvider - impossible!");
        }
    }

    private class VMCheck implements Runnable {
        @Override
        public void run() {
            monitoredVMs.forEach(vmMonitor -> {
                try {
                    vmMonitor.invoke();
                } catch (Exception e) {
                    LOGGER.warn("Exception in monitor: {}", e.toString(), e);
                }
            });
        }
    }
}
