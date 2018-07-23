package io.nano.jvmmonitor;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.Units;
import sun.jvmstat.monitor.Variability;

abstract class AbstractDelegateMonitor implements Monitor {

    private final Monitor delegate;

    AbstractDelegateMonitor(Monitor delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getBaseName() {
        return delegate.getBaseName();
    }

    @Override
    public Units getUnits() {
        return delegate.getUnits();
    }

    @Override
    public Variability getVariability() {
        return delegate.getVariability();
    }

    @Override
    public boolean isVector() {
        return delegate.isVector();
    }

    @Override
    public int getVectorLength() {
        return delegate.getVectorLength();
    }

    @Override
    public boolean isSupported() {
        return delegate.isSupported();
    }

    @Override
    public Object getValue() {
        return delegate.getValue();
    }
}
