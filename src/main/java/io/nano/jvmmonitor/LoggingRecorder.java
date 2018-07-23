package io.nano.jvmmonitor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingRecorder implements EventRecorder {

    private static final Logger LOGGER = LogManager.getLogger(LoggingRecorder.class);

    @Override
    public void record(JvmEvent event) {
        LOGGER.debug(event);
    }
}
