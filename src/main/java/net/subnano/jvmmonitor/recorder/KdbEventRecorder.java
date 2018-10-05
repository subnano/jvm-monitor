package net.subnano.jvmmonitor.recorder;

import net.subnano.jvmmonitor.model.GcEvent;
import net.subnano.jvmmonitor.model.HeapSample;
import net.subnano.jvmmonitor.model.JvmEvent;
import net.subnano.kx.ConnectState;
import net.subnano.kx.KxConnection;
import net.subnano.kx.KxConnectionManager;
import net.subnano.kx.KxListener;
import net.subnano.kx.KxTableWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Need to be able to enable kdb recorder by config
 *
 * @author Mark Wardell
 */
public class KdbEventRecorder implements EventRecorder<JvmEvent> {

    private static final Logger LOGGER = LogManager.getLogger(KdbEventRecorder.class);

    private final KxConnection connection;
    private final KxTableWriter<GcEvent> gcEventWriter;
    private final KxTableWriter<HeapSample> heapSampleWriter;

    public KdbEventRecorder(String host, int port) {
        this.connection = new KxConnectionManager(host, port, new KdbListener());
        this.gcEventWriter = connection.newTableWriter(new GcEventWriterSource());
        this.heapSampleWriter = connection.newTableWriter(new HeapSampleWriterSource());
    }

    @Override
    public void record(JvmEvent event) {
        return;
//        if (event instanceof GcEvent) {
//            gcEventWriter.write((GcEvent) event);
//        } else if (event instanceof HeapSample) {
//            heapSampleWriter.write((HeapSample) event);
//        } else {
//            throw new IllegalArgumentException("JvmEvent not supported " + event.getClass());
//        }
    }

    @Override
    public void connect() {
        connection.connect();
    }

    @Override
    public void close() {
        connection.close();
    }

    private class KdbListener implements KxListener {
        @Override
        public void onStateUpdated(ConnectState state) {
            LOGGER.info("Connection state of kdb process: {}", state);
        }

        @Override
        public void onError(Throwable cause) {
            LOGGER.error(cause);
        }

        @Override
        public void onMessage(Object message) {
            // NO-OP
        }
    }
}
