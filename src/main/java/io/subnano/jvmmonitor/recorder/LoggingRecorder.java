package io.subnano.jvmmonitor.recorder;

import io.subnano.jvmmonitor.JvmEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class LoggingRecorder implements EventRecorder {

    private static final Logger LOGGER = LogManager.getLogger(LoggingRecorder.class);

    private final StringBuilder sb;

    public LoggingRecorder() {
        this.sb = new StringBuilder(100);
    }

    @Override
    public void record(JvmEvent event) {
        LOGGER.info(event);
    }

    @Override
    public void connect() throws IOException {
        // NO-OP
    }

    @Override
    public void close() throws IOException {
        // NO-OP
    }
}
