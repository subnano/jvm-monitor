package io.nano.jvmmonitor;

import sun.jvmstat.monitor.Monitor;

public interface LongMonitor extends Monitor {

    long getLongValue();

    long currentValue();

    long previousValue();

    boolean changed();
}
