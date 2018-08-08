package io.subnano.jvmmonitor.recorder;

import io.subnano.jvmmonitor.model.GcEvent;
import io.subnano.kx.ColumnType;
import io.subnano.kx.DefaultKxSchema;
import io.subnano.kx.KxConnection;
import io.subnano.kx.KxEncoder;
import io.subnano.kx.KxSchema;
import io.subnano.kx.KxWriterSource;
import io.subnano.kx.TableDataBuffer;

/**
 * @author Mark Wardell
 */
public class GcEventWriterSource implements KxWriterSource<GcEvent> {

    private static final String TABLE_NAME = "vm_gc";

    @Override
    public KxSchema schema() {
        return new DefaultKxSchema.Builder()
                .forTable(TABLE_NAME)
                .addColumn("host", ColumnType.String)
                .addColumn("main_class", ColumnType.String)
                .addColumn("time", ColumnType.Timestamp)
                .addColumn("pid", ColumnType.Int)
                .addColumn("collector", ColumnType.String)
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
        buffer.addString(event.collector());
        buffer.addDouble(event.pauseTime());
    }
}
