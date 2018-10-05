package net.subnano.jvmmonitor.recorder;

import net.subnano.jvmmonitor.model.GcEvent;
import net.subnano.kx.ColumnType;
import net.subnano.kx.DefaultKxSchema;
import net.subnano.kx.KxConnection;
import net.subnano.kx.KxEncoder;
import net.subnano.kx.KxSchema;
import net.subnano.kx.KxWriterSource;
import net.subnano.kx.TableDataBuffer;

/**
 * @author Mark Wardell
 */
public class GcEventWriterSource implements KxWriterSource<GcEvent> {

    private static final String TABLE_NAME = "vm_gc";

    @Override
    public KxSchema schema() {
        return new DefaultKxSchema.Builder()
                .table(TABLE_NAME)
                .addColumn("host", ColumnType.String)
                .addColumn("main_class", ColumnType.String)
                .addColumn("timestamp", ColumnType.Timestamp)
                .addColumn("pid", ColumnType.Int)
                .addColumn("space", ColumnType.String)
                .addColumn("collector", ColumnType.String)
                .addColumn("cause", ColumnType.String)
                .addColumn("pause_time", ColumnType.Double)
                .build();
    }

    @Override
    public KxEncoder<GcEvent> encoder() {
        return this::encode;
    }

    @Override
    public KxConnection.Mode mode() {
        return KxConnection.Mode.Sync;
    }

    private void encode(GcEvent event, TableDataBuffer buffer) {
        buffer.reset();
        buffer.addString(event.host());
        buffer.addString(event.mainClass());
        buffer.addTimestamp(event.timestamp());
        buffer.addInt(event.pid());
        buffer.addString(event.space());
        buffer.addString(event.collector());
        buffer.addString(event.cause());
        buffer.addDouble(event.pauseTime());
        buffer.completeRow();
    }
}
