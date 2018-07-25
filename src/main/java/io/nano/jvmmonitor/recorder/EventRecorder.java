package io.nano.jvmmonitor.recorder;

import io.nano.jvmmonitor.JvmEvent;

public interface EventRecorder {

    void record(JvmEvent event);
}
