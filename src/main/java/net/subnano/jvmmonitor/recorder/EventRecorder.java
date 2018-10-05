package net.subnano.jvmmonitor.recorder;

import net.subnano.jvmmonitor.util.Connectable;

import java.io.IOException;

public interface EventRecorder<T> extends Connectable {

    void record(T event) throws IOException;

}
