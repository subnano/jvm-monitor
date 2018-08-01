package io.subnano.jvmmonitor.recorder;

import io.subnano.jvmmonitor.model.HeapSample;
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
public class HeapSampleWriter implements KxTableWriter<HeapSample> {

    private static final String TABLE_NAME = "vm_heap";

    private final KxTableWriter<HeapSample> tableWriter;

    HeapSampleWriter(KxConnection connection) {
        this.tableWriter = connection.newTableWriter(buildKxSchema(), this::updateHeapSample);
    }

    @Override
    public void write(HeapSample event) throws IOException {
        tableWriter.write(event);
    }

    private KxSchema buildKxSchema() {
        return new KxSchemaBuilder()
                .forTable(TABLE_NAME)
                .addColumn("timestamp", ColumnType.Timestamp)
                .addColumn("host", ColumnType.String)
                .addColumn("main_class", ColumnType.String)
                .addColumn("pid", ColumnType.Int)
                .addColumn("name", ColumnType.String)
                .addColumn("heap_used", ColumnType.Long)
                .addColumn("heap_capacity", ColumnType.Long)
                .build();
    }

    private void updateHeapSample(HeapSample event, TableDataBuffer buffer) {
        buffer.reset();
        buffer.addTimestamp(event.timestamp());
        buffer.addString(event.host());
        buffer.addString(event.mainClass());
        buffer.addInt(event.pid());
        buffer.addString(event.name());
        buffer.addLong(event.heapUsed());
        buffer.addLong(event.heapCapacity());
    }

}
