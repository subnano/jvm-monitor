package io.nano.jvmmonitor.kdb;

import io.nano.jvmmonitor.GcEvent;
import io.nano.jvmmonitor.MutableGcEvent;
import io.nano.jvmmonitor.recorder.KxTableWriter;
import io.nano.jvmmonitor.recorder.TableDataBuffer;
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
                .addTimestamp("timestamp")
                .addString("host")
                .addString("mainClass")
                .addInt("pid")
                .addString("collector")
                .addLong("pauseTime")
                .build();

        GcEvent event = getGcEvent();

        TableDataBuffer buffer = tableWriter.getTableDataBuffer();
        buffer.setTimestamp(0, event.timestamp());
        buffer.setString(1, event.host());
        buffer.setString(2, event.mainClass());
        buffer.setInt(3, event.pid());
        buffer.setString(4, event.name());
        buffer.setLong(5, event.pauseTime());

        tableWriter.invoke();

        connection.close();
    }

    private MutableGcEvent getGcEvent() {
        MutableGcEvent event = new MutableGcEvent();
        event.timestamp(System.currentTimeMillis());
        event.host("host1");
        event.mainClass("io.nano.FakeClass");
        event.name("Dumb Collector");
        event.pid(1748);
        event.pauseTime(864112L);
        return event;
    }


}