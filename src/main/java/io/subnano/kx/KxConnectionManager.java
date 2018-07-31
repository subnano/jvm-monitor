package io.subnano.kx;

import kx.c.Flip;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;

/**
 * This class is responsible for reconnection to a Kx system when the socket connection is lost.
 * <p>
 * TODO add support for multiple Kx server processes for fault tolerance.
 * TODO create a builder to create a custom connection manager with required properties
 *
 * @author Mark Wardell
 */
public class KxConnectionManager implements KxConnection {

    private static final Logger LOGGER = LogManager.getLogger(KxConnectionManager.class);

    private static final int DEFAULT_RECONNECT_INTERVAL_MS = 5_000;
    private static final int DEFAULT_MAX_RECONNECT_ATTEMPTS = Integer.MAX_VALUE;

    private final DefaultKxConnection connection;
    private final int reconnectInterval;
    private final int maxReconnectAttempts;

    public KxConnectionManager(String host, int port) {
        this(host, port, DEFAULT_RECONNECT_INTERVAL_MS, DEFAULT_MAX_RECONNECT_ATTEMPTS);
    }

    public KxConnectionManager(String host, int port, int reconnectInterval, int maxReconnectAttempts) {
        this.connection = new DefaultKxConnection(host, port);
        this.reconnectInterval = reconnectInterval;
        this.maxReconnectAttempts = maxReconnectAttempts;
    }

    @Override
    public void connect() throws IOException {
        int connectionAttempt = 0;
        do {
            tryConnect(++connectionAttempt);
            if (!isConnected()) {
                try {
                    Thread.sleep(reconnectInterval);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } while (connectionAttempt < maxReconnectAttempts && !isConnected());
    }

    private void tryConnect(int connectionAttempt) {
        try {
            connection.connect();
        } catch (IOException e) {
            LOGGER.info("Error connecting to {}:{} - ", connection.host(), connection.port(), e.getMessage());
            if (connectionAttempt == 1)
                LOGGER.info("Will attempt to reconnect every {} ms", reconnectInterval);
        }
    }

    @Override
    public boolean isConnected() {
        return connection.isConnected();
    }

    @Override
    public void close() {
        connection.close();
    }

    @Override
    public <T> KxTableWriter<T> newTableWriter(KxSchema kxSchema, KxEncoder<T> encoder) {
        // TODO we could around this code dupe if we extended DefaultKxConnection
        // originally had a problem with that - need to investigate again further
        return new SyncKxTableWriter<>(
                this,
                kxSchema.tableName(),
                kxSchema.columnNames(),
                kxSchema.data(),
                encoder
        );
    }

    @Override
    public void invoke(String table, String command, Flip flip) throws IOException {
        try {
            connection.invoke(table, command, flip);
        } catch (EOFException e) {
            // we see an EOFException when we lose the socket connection
            LOGGER.info("Lost connection to remote process - will re-establish connection", reconnectInterval);

            // currently blocks on the application thread writing data - ew!
            // would need locking of some kind if re-connectivity was on another thread
            connection.close();
            connect();
        } catch (Exception e) {
            LOGGER.info("Error writing to kx process - connected={}", this::isConnected);
        }
    }

}
