package io.subnano.kdb;

import io.subnano.jvmmonitor.GcEvent;
import io.subnano.jvmmonitor.MutableGcEvent;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class KxConnectionTest {

    @Test
    void testKxConnection() throws IOException {
        KxConnection connection = new KxConnection("localhost", 5010);
        connection.connect();

        KxTableWriterBuilder builder = connection.newTableWriterBuilder();
        KxTableWriter tableWriter = builder
                .forTable("gcevents")
                .addColumn("timestamp", ColumnType.Timestamp)
                .addColumn("host", ColumnType.String)
                .addColumn("mainClass", ColumnType.String)
                .addColumn("pid", ColumnType.Int)
                .addColumn("collector", ColumnType.String)
                .addColumn("pauseTime", ColumnType.Long)
                .build();

        GcEvent event = getGcEvent();

        TableDataBuffer buffer = tableWriter.getTableDataBuffer();
        buffer.setTimestamp(0, event.timestamp());
        buffer.setString(1, event.host());
        buffer.setString(2, event.mainClass());
        buffer.setInt(3, event.pid());
        buffer.setString(4, event.collector());
        buffer.setLong(5, event.pauseTime());

        tableWriter.invoke();

        connection.close();
    }

    private MutableGcEvent getGcEvent() {
        MutableGcEvent event = new MutableGcEvent();
        event.timestamp(System.currentTimeMillis());
        event.host("host1");
        event.mainClass("io.nano.FakeClass");
        event.collector("Dumb Collector");
        event.pid(1748);
        event.pauseTime(864112L);
        return event;
    }


}