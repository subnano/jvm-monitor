package io.subnano.jvmmonitor;

import io.subnano.jvmmonitor.recorder.KdbEventRecorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.locks.LockSupport;

public class JvmMonitor {

    private static final Logger LOGGER = LogManager.getLogger(JvmMonitor.class);
    private final HostMonitor hostMonitor;

    public JvmMonitor(MonitorSettings monitorSettings) {
        KdbEventRecorder eventRecorder = new KdbEventRecorder("localhost", 5001);
        this.hostMonitor = new HostMonitor(monitorSettings, eventRecorder);
    }

    public static void main(String[] args) {
        JvmMonitor jvmMonitor = new JvmMonitor(new MonitorSettings());
        try {
            jvmMonitor.start();
        } catch (IOException e) {
            LOGGER.error("Error starting Kvm monitor: ", e);
        }
    }

    private void start() throws IOException {
        LOGGER.info("Starting ..");
        hostMonitor.start();
        while(Thread.currentThread().isAlive()) {
            LockSupport.parkNanos(1_000_000L);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
//
//
//        try {
//            MonitoredVm vm = null;
//            for (Integer jvmId : jvms) {
//                String vmidString = "//" + jvmId + "?mode=r";
//                VmIdentifier vmIdentifier = new VmIdentifier(vmidString);
//
//                vm = monitoredHost.getMonitoredVm(vmIdentifier, 0);
//                System.out.printf("%-6d %s%n", jvmId, MonitoredVmUtil.mainClass(vm, true));
//                List<Monitor> gc = vm.findByPattern("-gc");
//
//                //System.out.println(gc);
//                //System.out.println(vm.getVmIdentifier());
//
//                PerfDataReader.dump2(vmIdentifier);
//                break;
//
//            }
//            //MonitoredVm monitoredVm = monitoredHost.getMonitoredVm(vmId, -1);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (MonitorException e) {
//            e.printStackTrace();
//        }
    }

    void stop() {
        LOGGER.info("Stopping ..");
        try {
            hostMonitor.stop();
        } catch (IOException e) {
            LOGGER.warn("Error caught stopping Jvm monitor.", e);
        }
    }
}
