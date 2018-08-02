package io.subnano.jvmmonitor.monitor;

import io.subnano.jvmmonitor.util.MonitorUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.jvmstat.monitor.MonitoredVm;

/**
 * @author Mark Wardell
 */
abstract class AbstractVmMonitor implements VmMonitor {

    private static final Logger LOGGER = LogManager.getLogger(AbstractVmMonitor.class);

    private final String hostName;
    private final String mainClass;
    private final int pid;
    private final long monitorInterval;

    private long nextExecutionTime = Long.MIN_VALUE;

    AbstractVmMonitor(final MonitoredVm vm,
                      final String hostName,
                      final long monitorInterval) {

        this.hostName = hostName;
        this.mainClass = MonitorUtil.mainClass(vm);
        this.pid = Integer.parseInt(vm.getVmIdentifier().getUserInfo());
        this.monitorInterval = monitorInterval;
        LOGGER.info("Running {} for {} ({}) every {} ms", getClass().getSimpleName(), mainClass, pid, monitorInterval);
    }

    @Override
    public String hostName() {
        return hostName;
    }

    @Override
    public String mainClass() {
        return mainClass;
    }

    @Override
    public int pid() {
        return pid;
    }

    @Override
    public final void invoke() {
        if (System.currentTimeMillis() >= nextExecutionTime) {
            monitorFunction();
            nextExecutionTime += monitorInterval;
        }

    }

    protected abstract void monitorFunction();

}
