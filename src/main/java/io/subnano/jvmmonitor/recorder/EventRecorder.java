package io.subnano.jvmmonitor.recorder;

import io.subnano.jvmmonitor.Connectable;

public interface EventRecorder<T> extends Connectable {

    void record(T event);

}
