package io.subnano.kdb;

import io.subnano.jvmmonitor.GcEvent;
import io.subnano.jvmmonitor.MutableGcEvent;
import io.subnano.jvmmonitor.recorder.GcEventWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class KxConnectionTest {

    private KxConnection connection;

    @BeforeEach
    void setUp() throws IOException {
        connection = new KxConnection("localhost", 5010);
        connection.connect();
    }

    @AfterEach
    void tearDown() throws IOException {
        connection.close();
    }

    @Test
    void writeSingleRow() {
        KxTableWriter<GcEvent> tableWriter = new GcEventWriter(connection);
        GcEvent event = getGcEvent();
        tableWriter.write(event);
    }

    @Test
    void writeMultipleRows() throws InterruptedException {
        KxTableWriter<GcEvent> tableWriter = new GcEventWriter(connection);
        MutableGcEvent event = getGcEvent();
        for (int i=0; i<10; i++) {
            event.timestamp(System.currentTimeMillis());
            tableWriter.write(event);
            Thread.sleep(5_000);
        }
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