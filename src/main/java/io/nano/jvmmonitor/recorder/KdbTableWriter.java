package io.nano.jvmmonitor.recorder;

import io.nano.jvmmonitor.kdb.KxConnection;

public class KdbTableWriter {

    private final KxConnection kxConnection;
    private final String tableName;
    private final String[] columnNames;

    public KdbTableWriter(KxConnection kxConnection, String tableName, String[] columnNames) {
        this.kxConnection = kxConnection;
        this.tableName = tableName;
        this.columnNames = columnNames;
    }


}
