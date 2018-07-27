package io.subnano.jvmmonitor.recorder;

import io.subnano.jvmmonitor.GcEvent;
import io.subnano.jvmmonitor.JvmEvent;
import io.subnano.kdb.ColumnType;
import io.subnano.kdb.KxConnection;
import io.subnano.kdb.KxTableWriter;
import io.subnano.kdb.TableDataBuffer;

import java.io.IOException;

public class KdbEventRecorder implements EventRecorder {

    private static final String GCEVENT_TABLE_NAME = "gcevent";

    private final KxConnection connection;
    private final KxTableWriter tableWriter;

    public KdbEventRecorder(String host, int port) {
        this.connection = new KxConnection(host, port);
        this.tableWriter = connection.newTableWriterBuilder()
                .forTable(GCEVENT_TABLE_NAME)
                .addColumn("timestamp", ColumnType.Timestamp)
                .addColumn("host", ColumnType.String)
                .addColumn("mainClass", ColumnType.String)
                .addColumn("pid", ColumnType.Int)
                .addColumn("collector", ColumnType.String)
                .addColumn("pauseTime", ColumnType.Long)
                .build();

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
        // TODO improve column bindings as index will get messed quickly column
        // timestamp, host, pid, mainClass, cause, name, pauseTime
        TableDataBuffer buffer = tableWriter.getTableDataBuffer();
        buffer.setTimestamp(0, event.timestamp());
        buffer.setString(1, event.host());
        buffer.setString(2, event.mainClass());
        buffer.setInt(3, event.pid());
        buffer.setString(4, event.collector());
        buffer.setLong(5, event.pauseTime());
        tableWriter.invoke();
    }

    @Override
    public void connect() throws IOException {
        connection.connect();
    }

    @Override
    public void close() throws IOException {
        connection.close();
    }
}
