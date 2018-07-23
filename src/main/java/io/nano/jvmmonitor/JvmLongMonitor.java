package io.nano.jvmmonitor;

import sun.jvmstat.monitor.Monitor;

public class JvmLongMonitor extends AbstractDelegateMonitor implements LongMonitor {

    private final Monitor underlying;
    private long previousValue;
    private long currentValue;

    public JvmLongMonitor(Monitor underlying) {
        super(underlying);
        this.underlying = underlying;
    }

    @Override
    public long getLongValue() {
        previousValue = currentValue;
        currentValue = (long) underlying.getValue();
        return currentValue;
    }


    @Override
    public long currentValue() {
        return currentValue;
    }

    @Override
    public long previousValue() {
        return previousValue;
    }

    @Override
    public boolean changed() {
        return currentValue != previousValue;
    }
}
