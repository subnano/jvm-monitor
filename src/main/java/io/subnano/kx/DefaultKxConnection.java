package io.subnano.kx;

import kx.c;
import kx.c.KException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.TimeZone;

public class DefaultKxConnection implements KxConnection {

    private static final Logger LOGGER = LogManager.getLogger(DefaultKxConnection.class);

    private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String userPassword;

    private c c;

    public DefaultKxConnection(String host, int port) {
        this(host, port, null, null);
    }

    public DefaultKxConnection(String host, int port, String user, String password) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.userPassword = userPassword();
    }

    @Override
    public void connect() throws IOException {
        LOGGER.info("Connecting to {}:{} ..", host, port);
        try {
            this.c = new c(host, port);
            this.c.tz = UTC_TIME_ZONE;
            LOGGER.info("Connected to {}:{}", host, port);
        } catch (KException e) {
            throw new IOException(e);
        }
    }

    @Override
    public <T> SyncKxTableWriter<T> newTableWriter(KxSchema kxSchema, KxEncoder<T> encoder) {
        return new SyncKxTableWriter<>(
                this,
                kxSchema.tableName(),
                kxSchema.columnNames(),
                kxSchema.data(),
                encoder
        );
    }

    @Override
    public void invoke(String table, String command, kx.c.Flip flip) throws IOException {
        try {
            // TODO send asynchronously
            Object result = c.k(command, table, flip);
            //LOGGER.debug("Successfully wrote record to kx server");
        } catch (IOException e) {
            LOGGER.error("IO Error writing record to kx", e);
            throw e;
        } catch (KException e) {
            LOGGER.error("Error writing record to kx", e);
        }
    }

    @Override
    public boolean isConnected() {
        return c != null && c.s != null && c.s.isConnected();
    }

    @Override
    public void close() {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                LOGGER.warn("Error closing socket connection: {}", e.getMessage());
            }
            finally {
                c = null;
            }
        }
    }

    String host() {
        return host;
    }

    int port() {
        return port;
    }

    private String userPassword() {
        if (user == null || user.length() == 0)
            return null;
        if (password == null || password.length() == 0)
            return user;
        return user + ":" + password;
    }
}
