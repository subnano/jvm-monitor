package io.subnano.jvmmonitor.recorder;

import io.subnano.jvmmonitor.Connectable;

import java.io.IOException;

public interface EventRecorder<T> extends Connectable {

    void record(T event) throws IOException;

}
