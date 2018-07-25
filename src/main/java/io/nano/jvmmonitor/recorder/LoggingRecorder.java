package io.nano.jvmmonitor.recorder;

import io.nano.jvmmonitor.JvmEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
}
