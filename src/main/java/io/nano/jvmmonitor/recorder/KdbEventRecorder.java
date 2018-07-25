package io.nano.jvmmonitor.recorder;

import io.nano.jvmmonitor.Connectable;
import io.nano.jvmmonitor.GcEvent;
import io.nano.jvmmonitor.JvmEvent;
import io.nano.jvmmonitor.kdb.KxConnection;

import java.io.IOException;

public class KdbEventRecorder implements EventRecorder, Connectable {

    private static final String GCEVENT_TABLE_NAME = "gcevent";

    private final KxConnection kdb;

    public KdbEventRecorder(String host, int port) {
        this.kdb = new KxConnection(host, port);
    }

    @Override
    public void record(JvmEvent event) {
        if (event instanceof GcEvent) {
            recordGcEvent((GcEvent) event);
        }
        else {
            throw new IllegalArgumentException("JvmEvent not supported " + event.getClass());
        }
    }

    private void recordGcEvent(GcEvent event) {
        // timestamp, host, pid, mainClass, cause, name, pauseTime
    }

    @Override
    public void connect() {
        kdb.connect();
    }

    @Override
    public void close() throws IOException {
        kdb.close();
    }
}
