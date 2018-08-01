package io.subnano.jvmmonitor;

import io.subnano.jvmmonitor.recorder.EventRecorder;
import io.subnano.jvmmonitor.recorder.KdbEventRecorder;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
    private final ConcurrentMap<Integer, VmMonitor> monitoredVMs = new ConcurrentHashMap<>();
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
        eventRecorder.connect();
        executor.scheduleAtFixedRate(new VMCheck(), 10, 10, TimeUnit.MILLISECONDS);
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
            LOGGER.info("VM Started: {}", vmId);
            String vmIdString = "//" + vmId + "?mode=r";
            try {
                VmIdentifier vmIdentifier = new VmIdentifier(vmIdString);
//                if (vmId == 23754)
//                    PerfDataReader.dump2(vmIdentifier);
//                PerfDataBuffer perfDataBuffer = new PerfDataBuffer(vmIdentifier);
//                perfDataBuffer.findByName();
                // autoboxing int not ideal
                MonitoredVm vm = monitoredHost.getMonitoredVm(vmIdentifier, 0);
                monitoredVMs.putIfAbsent(vmId, new GcEventMonitor(hostName, vm, settings, eventRecorder, YOUNG_GEN));
                monitoredVMs.putIfAbsent(vmId, new GcEventMonitor(hostName, vm, settings, eventRecorder, OLD_GEN));
                monitoredVMs.putIfAbsent(vmId, new HeapSampleMonitor(hostName, vm, settings, eventRecorder, YOUNG_GEN));
                monitoredVMs.putIfAbsent(vmId, new HeapSampleMonitor(hostName, vm, settings, eventRecorder, OLD_GEN));

            } catch (URISyntaxException e) {
                LOGGER.warn("Unable to create VmIdentifier from {}", vmIdString);
            } catch (MonitorException e) {
                LOGGER.warn("Unable to create MonitoredVm from {}", vmIdString);
            }
        }

        private void removeVm(Object o) {
            int vmId = (int) o;
            LOGGER.info("VM Terminated: {}", vmId);
            monitoredVMs.remove(vmId);
            // TODO check and warn if not present?
        }

        @Override
        public void disconnected(HostEvent event) {
            LOGGER.error("Disconnected from LocalMonitoredHostProvider - impossible!");
        }
    }

    private class VMCheck implements Runnable {
        @Override
        public void run() {
            monitoredVMs.forEach((id, monitor) -> {
                try {
                    monitor.invoke();
                } catch (Exception e) {
                    LOGGER.warn("Exception in monitor: {}", e.toString(), e);
                }
            });
        }
    }
}
