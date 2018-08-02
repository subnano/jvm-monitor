package io.subnano.jvmmonitor.recorder;

import io.subnano.jvmmonitor.model.GcEvent;
import io.subnano.kx.ColumnType;
import io.subnano.kx.KxConnection;
import io.subnano.kx.KxSchema;
import io.subnano.kx.KxSchemaBuilder;
import io.subnano.kx.KxTableWriter;
import io.subnano.kx.TableDataBuffer;

import java.io.IOException;

/**
 * @author Mark Wardell
 */
public class GcEventWriter implements KxTableWriter<GcEvent> {

    private static final String TABLE_NAME = "vm_gc";

    private final KxTableWriter<GcEvent> tableWriter;

    public GcEventWriter(KxConnection connection) {
        this.tableWriter = connection.newTableWriter(buildKxSchema(), this::updateGcEvent);
    }

    @Override
    public void write(GcEvent event) throws IOException {
        tableWriter.write(event);
    }

    private KxSchema buildKxSchema() {
        return new KxSchemaBuilder()
                .forTable(TABLE_NAME)
                .addColumn("timestamp", ColumnType.Timestamp)
                .addColumn("host", ColumnType.String)
                .addColumn("main_class", ColumnType.String)
                .addColumn("pid", ColumnType.Int)
                .addColumn("collector", ColumnType.String)
                .addColumn("pause_time", ColumnType.Float)
                .build();
    }

    private void updateGcEvent(GcEvent event, TableDataBuffer buffer) {
        buffer.reset();
        buffer.addTimestamp(event.timestamp());
        buffer.addString(event.host());
        buffer.addString(event.mainClass());
        buffer.addInt(event.pid());
        buffer.addString(event.collector());
        buffer.addFloat(event.pauseTime());
    }

}
