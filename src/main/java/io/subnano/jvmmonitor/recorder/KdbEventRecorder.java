package io.subnano.jvmmonitor.recorder;

import io.subnano.jvmmonitor.model.GcEvent;
import io.subnano.jvmmonitor.model.HeapSample;
import io.subnano.jvmmonitor.model.JvmEvent;
import io.subnano.kx.KxConnection;
import io.subnano.kx.KxConnectionManager;
import io.subnano.kx.KxTableWriter;

import java.io.IOException;

public class KdbEventRecorder implements EventRecorder<JvmEvent> {

    private final KxConnection connection;
    private final KxTableWriter<GcEvent> gcEventWriter;
    private final KxTableWriter<HeapSample> heapSampleWriter;

    public KdbEventRecorder(String host, int port) {
        this.connection = new KxConnectionManager(host, port);
        this.gcEventWriter = new GcEventWriter(connection);
        this.heapSampleWriter = new HeapSampleWriter(connection);
    }

    @Override
    public void record(JvmEvent event) throws IOException {
        if (event instanceof GcEvent) {
            gcEventWriter.write((GcEvent) event);
        } else if (event instanceof HeapSample) {
            heapSampleWriter.write((HeapSample) event);
        } else {
            throw new IllegalArgumentException("JvmEvent not supported " + event.getClass());
        }
    }

    @Override
    public void connect() throws IOException {
        connection.connect();
    }

    @Override
    public void close() {
        connection.close();
    }

}
