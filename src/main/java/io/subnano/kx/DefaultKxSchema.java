package io.subnano.kx;

/**
 * @author Mark Wardell
 */
public class DefaultKxSchema implements KxSchema {

    private final String tableName;
    private final String[] columnNames;
    private final Object[] data;

    DefaultKxSchema(String tableName,
                    String[] columnNames,
                    Object[] data) {
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.data = data;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    @Override
    public String[] columnNames() {
        return columnNames;
    }

    @Override
    public Object[] data() {
        return data;
    }

}
