package io.nano.jvmmonitor.recorder;

import io.nano.jvmmonitor.kdb.KxConnection;
import io.nano.jvmmonitor.kdb.c;

public class KxTableWriter {

    private final KxConnection kxConnection;
    private final String tableName;
    private final TableDataBuffer tableDataBuffer;
    private c.Flip flip;

    public KxTableWriter(KxConnection kxConnection, String tableName, String[] columnNames, Object[] tableData) {
        this.kxConnection = kxConnection;
        this.tableName = tableName;
        this.flip = new c.Flip(new c.Dict(columnNames, tableData));
        this.tableDataBuffer = new TableDataBuffer(tableData);
    }

    public TableDataBuffer getTableDataBuffer() {
        return tableDataBuffer;
    }

    public void invoke() {
        kxConnection.update(tableName, flip);
    }

}
