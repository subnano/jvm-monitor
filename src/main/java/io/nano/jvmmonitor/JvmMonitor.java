package io.nano.jvmmonitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.LockSupport;

public class JvmMonitor {

    private static final Logger LOGGER = LogManager.getLogger(JvmMonitor.class);
    private final HostMonitor hostMonitor;

    public JvmMonitor(MonitorSettings monitorSettings) {
        this.hostMonitor = new HostMonitor(monitorSettings);
    }

    public static void main(String[] args) {
        JvmMonitor jvmMonitor = new JvmMonitor(new MonitorSettings());
        jvmMonitor.start();
    }

    private void start() {
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
        hostMonitor.stop();
    }
}
