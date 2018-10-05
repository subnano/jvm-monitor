package net.subnano.jvmmonitor;

import net.subnano.jvmmonitor.console.JvmConsole;
import net.subnano.jvmmonitor.monitor.HostMonitor;
import net.subnano.jvmmonitor.recorder.KdbEventRecorder;
import net.subnano.jvmmonitor.settings.DefaultMonitorSettings;
import net.subnano.jvmmonitor.settings.MonitorSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.locks.LockSupport;

/**
 * @author Mark Wardell
 */
public class JvmMonitor {

    private static final Logger LOGGER = LogManager.getLogger(JvmMonitor.class);
    private final HostMonitor hostMonitor;

    JvmMonitor(MonitorSettings settings) {
        KdbEventRecorder kdbRecorder = new KdbEventRecorder(settings.kdbHost(), settings.kdbPort());
        // TODO add toggle to display console
        if (settings.isConsoleEnabled() && JvmConsole.isSupported()) {
            JvmConsole jvmConsole = new JvmConsole(kdbRecorder);
            this.hostMonitor = new HostMonitor(settings, jvmConsole);
        }
        else {
            this.hostMonitor = new HostMonitor(settings, kdbRecorder);
        }
    }

    public static void main(String[] args) {
        MonitorSettings monitorSettings = DefaultMonitorSettings.newInstance(args);
        JvmMonitor jvmMonitor = new JvmMonitor(monitorSettings);
        try {
            jvmMonitor.start();
        } catch (IOException e) {
            LOGGER.error("Error starting Jvm monitor: ", e);
        }
    }

    private void start() throws IOException {
        LOGGER.info("Starting ..");
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        hostMonitor.start();
        while (Thread.currentThread().isAlive()) {
            LockSupport.parkNanos(1_000_000L);
        }
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
