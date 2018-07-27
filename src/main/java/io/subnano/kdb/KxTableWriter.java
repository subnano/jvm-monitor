package io.subnano.kdb;

import kx.c;

public class KxTableWriter {

    private final KxConnection kxConnection;
    private final String tableName;
    private final String command;
    private final TableDataBuffer tableDataBuffer;
    private c.Flip flip;

    public KxTableWriter(KxConnection kxConnection, String tableName, String command, String[] columnNames, Object[] tableData) {
        this.kxConnection = kxConnection;
        this.tableName = tableName;
        this.command = command;
        this.flip = new c.Flip(new c.Dict(columnNames, tableData));
        this.tableDataBuffer = new TableDataBuffer(tableData);
    }

    public TableDataBuffer getTableDataBuffer() {
        return tableDataBuffer;
    }

    /**
     * TODO Review decoupled data update and invoke - should be a single atomic operation
     */
    public void invoke() {
        Object[] tableData = flip.y;
        kxConnection.invoke(tableName, command, flip);
    }

}
