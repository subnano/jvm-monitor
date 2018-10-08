package net.subnano.jtop2;

import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.software.os.OperatingSystem.ProcessSort;
import oshi.util.FormatUtil;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * TODO PID USER PRI MAIN TIME MEM CMD etc
 * <p>
 * Application to provide a listing of monitorable java processes.
 *
 * @author Brian Doherty
 * @since 1.5
 */
public class JTop2 {

    private static Arguments arguments;

    public static void main(String[] args) {
        try {
            arguments = new Arguments(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            Arguments.printUsage(System.err);
            return;
        }

        if (arguments.isHelp()) {
            Arguments.printUsage(System.out);
            System.exit(0);
        }

        try {
            HostIdentifier hostId = arguments.hostId();
            MonitoredHost monitoredHost =
                    MonitoredHost.getMonitoredHost(hostId);

            // get the set active JVMs on the specified host.
            Set jvms = monitoredHost.activeVms();


            // get the os related info
            SystemInfo si = new SystemInfo();
            HardwareAbstractionLayer hal = si.getHardware();
            OperatingSystem os = si.getOperatingSystem();
            List<OSProcess> procs = Arrays.asList(os.getProcesses(5, ProcessSort.PID));

            for (Iterator j = jvms.iterator(); j.hasNext(); /* empty */) {
                StringBuilder output = new StringBuilder();
                Throwable lastError = null;

                int lvmid = ((Integer) j.next()).intValue();

                output.append(String.valueOf(lvmid));

                if (arguments.isQuiet()) {
                    System.out.println(output);
                    continue;
                }

                MonitoredVm vm = null;
                String vmidString = "//" + lvmid + "?mode=r";

                String errorString = null;

                try {
                    // Note: The VM associated with the current VM id may
                    // no longer be running so these queries may fail. We
                    // already added the VM id to the output stream above.
                    // If one of the queries fails, then we try to add a
                    // reasonable message to indicate that the requested
                    // info is not available.

                    errorString = " -- process information unavailable";
                    VmIdentifier id = new VmIdentifier(vmidString);
                    vm = monitoredHost.getMonitoredVm(id, 0);

                    errorString = " -- main class information unavailable";
                    output.append(" " + MonitoredVmUtil.mainClass(vm,
                            arguments.showLongPaths()));

                    // add the cpu info
                    OSProcess p = getOSProcess(lvmid, procs);
                    GlobalMemory memory = hal.getMemory();
                    output.append(
                            String.format(" %5.1f %4.1f %9s %9s %s%n",
                                    100d * (p.getKernelTime() + p.getUserTime()) / p.getUpTime(),
                                    100d * p.getResidentSetSize() / memory.getTotal(), FormatUtil.formatBytes(p.getVirtualSize()),
                                    FormatUtil.formatBytes(p.getResidentSetSize()), p.getName())
                    );

                    if (arguments.showMainArgs()) {
                        errorString = " -- main args information unavailable";
                        String mainArgs = MonitoredVmUtil.mainArgs(vm);
                        if (mainArgs != null && mainArgs.length() > 0) {
                            output.append(" " + mainArgs);
                        }
                    }
                    if (arguments.showVmArgs()) {
                        errorString = " -- jvm args information unavailable";
                        String jvmArgs = MonitoredVmUtil.jvmArgs(vm);
                        if (jvmArgs != null && jvmArgs.length() > 0) {
                            output.append(" " + jvmArgs);
                        }
                    }
                    if (arguments.showVmFlags()) {
                        errorString = " -- jvm flags information unavailable";
                        String jvmFlags = MonitoredVmUtil.jvmFlags(vm);
                        if (jvmFlags != null && jvmFlags.length() > 0) {
                            output.append(" " + jvmFlags);
                        }
                    }
                    errorString = " -- detach failed";
                    monitoredHost.detach(vm);

                    System.out.println(output);

                    errorString = null;
                } catch (URISyntaxException e) {
                    // unexpected as vmidString is based on a validated hostid
                    lastError = e;
                    assert false;
                } catch (Exception e) {
                    lastError = e;
                } finally {
                    if (errorString != null) {
                        /*
                         * we ignore most exceptions, as there are race
                         * conditions where a JVM in 'jvms' may terminate
                         * before we get a chance to list its information.
                         * Other errors, such as access and I/O exceptions
                         * should stop us from iterating over the complete set.
                         */
                        output.append(errorString);
                        if (arguments.isDebug()) {
                            if ((lastError != null)
                                    && (lastError.getMessage() != null)) {
                                output.append("\n\t");
                                output.append(lastError.getMessage());
                            }
                        }
                        System.out.println(output);
                        if (arguments.printStackTrace()) {
                            lastError.printStackTrace();
                        }
                        continue;
                    }
                }
            }
        } catch (MonitorException e) {
            if (e.getMessage() != null) {
                System.err.println(e.getMessage());
            } else {
                Throwable cause = e.getCause();
                if ((cause != null) && (cause.getMessage() != null)) {
                    System.err.println(cause.getMessage());
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    private static OSProcess getOSProcess(int pid, List<OSProcess> osProcesses) {
        for (int i = 0; i < osProcesses.size(); i++) {
            OSProcess p = osProcesses.get(i);
            if (p.getProcessID() == pid)
                return p;
        }
        return null;
    }
}
