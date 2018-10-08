package net.subnano.jvmmonitor.recorder;

import net.subnano.jvmmonitor.model.HeapSample;
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
public class HeapSampleWriterSource implements KxWriterSource<HeapSample> {

    private static final String TABLE_NAME = "vm_heap";

    @Override
    public KxSchema schema() {
        return new DefaultKxSchema.Builder()
                .table(TABLE_NAME)
                .addColumn("sym", ColumnType.String)
                .addColumn("host", ColumnType.String)
                .addColumn("timestamp", ColumnType.Long)
                .addColumn("pid", ColumnType.Int)
                .addColumn("space", ColumnType.String)
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
        return KxConnection.Mode.Async;
    }

    private void encode(HeapSample event, TableDataBuffer buffer) {
        buffer.reset();
        buffer.addString(event.mainClass());
        buffer.addString(event.host());
        buffer.addLong(event.timestamp());
        buffer.addInt(event.pid());
        buffer.addString(event.name());
        buffer.addLong(event.heapUsed());
        buffer.addLong(event.heapCapacity());
        buffer.completeRow();
    }
}
