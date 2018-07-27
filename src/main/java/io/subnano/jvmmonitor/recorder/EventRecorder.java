package io.subnano.jvmmonitor.recorder;

import io.subnano.jvmmonitor.Connectable;
import io.subnano.jvmmonitor.JvmEvent;

public interface EventRecorder extends Connectable {

    void record(JvmEvent event);
}
