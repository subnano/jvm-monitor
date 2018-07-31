package io.subnano.kx;

import java.io.IOException;

/**
 * Single responsibility and that is to insert records into kx table.
 * TODO handle different command to 'insert'
 * TODO handle error scenarios better
 * TODO handle sync vs async
 */
public class SyncKxTableWriter<T> implements KxTableWriter<T> {

    private static final String UPDATE_COMMAND = "insert";

    private final KxConnection kxConnection;
    private final String tableName;
    private final KxEncoder<T> encoder;
    private final TableDataBuffer tableDataBuffer;

    SyncKxTableWriter(final KxConnection connection,
                      final String tableName,
                      final String[] columnNames,
                      final Object[] tableSchema,
                      final KxEncoder<T> encoder) {
        this.kxConnection = connection;
        this.tableName = tableName;
        this.encoder = encoder;
        this.tableDataBuffer = new TableDataBuffer(columnNames, tableSchema);
    }

    @Override
    public void write(T record) throws IOException {
        encoder.encoder(record, tableDataBuffer);
        kxConnection.invoke(tableName, UPDATE_COMMAND, tableDataBuffer.flip());
    }

}
