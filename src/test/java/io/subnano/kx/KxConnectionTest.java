package io.subnano.kx;

import io.subnano.jvmmonitor.model.GcEvent;
import io.subnano.jvmmonitor.model.MutableGcEvent;
import io.subnano.jvmmonitor.recorder.GcEventWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class KxConnectionTest {

    private DefaultKxConnection connection;

    @BeforeEach
    void setUp() throws IOException {
        connection = new DefaultKxConnection("localhost", 5001);
        connection.connect();
    }

    @AfterEach
    void tearDown() throws IOException {
        connection.close();
    }

    @Test
    void writeSingleRow() throws Exception {
        KxTableWriter<GcEvent> tableWriter = new GcEventWriter(connection);
        GcEvent event = getGcEvent();
        tableWriter.write(event);
        Thread.sleep(1000000);
    }

    @Test
    void writeMultipleRows() throws Exception {
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
        event.host("localhost");
        event.mainClass("io.nano.FakeClass");
        event.collector("Dumb Collector");
        event.pid(1748);
        event.pauseTime(864112L);
        return event;
    }


}