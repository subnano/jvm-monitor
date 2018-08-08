package io.subnano.jvmmonitor.recorder;

import io.subnano.jvmmonitor.model.HeapSample;
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
public class HeapSampleWriterSource implements KxWriterSource<HeapSample> {

    private static final String TABLE_NAME = "vm_heap";

    @Override
    public KxSchema schema() {
        return new DefaultKxSchema.Builder()
                .forTable(TABLE_NAME)
                .addColumn("host", ColumnType.String)
                .addColumn("main_class", ColumnType.String)
                .addColumn("time", ColumnType.Timestamp)
                .addColumn("pid", ColumnType.Int)
                .addColumn("name", ColumnType.String)
                .addColumn("heap_used", ColumnType.Long)
                .addColumn("heap_capacity", ColumnType.Long)
                .build();
    }

    @Override
    public KxEncoder<HeapSample> encoder() {
        return this::encode;
    }

    @Override
    public KxConnection.Mode mode() {
        return KxConnection.Mode.Sync;
    }

    private void encode(HeapSample event, TableDataBuffer buffer) {
        buffer.reset();
        buffer.addString(event.host());
        buffer.addString(event.mainClass());
        buffer.addTimestamp(event.timestamp());
        buffer.addInt(event.pid());
        buffer.addString(event.name());
        buffer.addLong(event.heapUsed());
        buffer.addLong(event.heapCapacity());
    }
}
