package io.nano.jvmmonitor;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;

public final class MonitorUtil {

    public static long getLongValue(MonitoredVm vm, String name) {
        Monitor monitor = getMonitor(vm, name);
        return (long) monitor.getValue();
    }

    public static String getStringValue(MonitoredVm vm, String name) {
        Monitor monitor = getMonitor(vm, name);
        return (String) monitor.getValue();
    }

    static Monitor getMonitor(MonitoredVm vm, String name) {
        try {
            return vm.findByName(name);
        } catch (MonitorException e) {
            throw new IllegalArgumentException("Error obtaining monitor '" + name + "' ", e);
        }
    }

    static LongMonitor getLongMonitor(MonitoredVm monitoredVm, String name) {
        return new JvmLongMonitor(getMonitor(monitoredVm, name));
    }
}
