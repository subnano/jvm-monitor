package io.subnano.kdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A builder to create a KxSchema
 */
public class KxSchemaBuilder {

    // Only supports single row for now
    private final int rowCount = 1;

    private String tableName;

    private final List<String> columnNames = new ArrayList<>();
    private final List<ColumnType> columnTypes = new ArrayList<>();

    public KxSchemaBuilder() {
        // nothing else to do
    }

    public KxSchemaBuilder forTable(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public KxSchemaBuilder addColumn(String name, ColumnType columnType) {
        columnNames.add(name);
        columnTypes.add(columnType);
        return this;
    }

    public KxSchema build() {
        return new DefaultKxSchema(
                tableName,
                columnNames.toArray(new String[0]),
                newTableData()
        );
    }

    private Object[] newTableData() {
        Object[] data = new Object[columnTypes.size()];
        for (int i = 0; i < columnTypes.size(); i++) {
            ColumnType type = columnTypes.get(i);
            if (type == ColumnType.Short)
                data[i] = new short[rowCount];
            else if (type == ColumnType.Int)
                data[i] = new int[rowCount];
            else if (type == ColumnType.Long)
                data[i] = new long[rowCount];
            else if (type == ColumnType.String)
                data[i] = new String[rowCount];
            else if (type == ColumnType.Timestamp)
                data[i] = new Date[rowCount];
            else
                throw new IllegalArgumentException("Unsupported column type: " + type);
        }
        return data;
    }
}
