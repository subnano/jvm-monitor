package io.subnano.kdb;

import kx.c;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.TimeZone;

public class KxConnection {

    private static final Logger LOGGER = LogManager.getLogger(KxConnection.class);

    private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String userPassword;

    private c c;

    public KxConnection(String host, int port) {
        this(host, port, null, null);
    }

    public KxConnection(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.userPassword = userPassword();
    }

    public void connect() throws IOException {
        // TODO add reconnection logic possibly at a higher level
        try {
            this.c = new c(host, port);
            this.c.tz = UTC_TIME_ZONE;
        } catch (kx.c.KException e) {
            throw new IOException(e);
        }
    }

    public KxTableWriterBuilder newTableWriterBuilder() {
        return new KxTableWriterBuilder(this);
    }

    public void invoke(String table, String command, kx.c.Flip flip) {
        try {
            // TODO send asynchronously
            Object result = c.k(command, table, flip);
            LOGGER.debug("Successfully wrote record to kx server");
        } catch (IOException e) {
            LOGGER.error("Error writing record", e);
        } catch (kx.c.KException e) {
            LOGGER.error("Error writing record", e);
        }
    }

    public void close() throws IOException {
        c.close();
    }

    private String userPassword() {
        if (user == null || user.length() == 0)
            return null;
        if (password == null || password.length() == 0)
            return user;
        return user + ":" + password;
    }
}
