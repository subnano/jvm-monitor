package io.nano.jvmmonitor.kdb;

import java.util.ArrayList;
import java.util.List;

public class KxSchemaBuilder {

    // Only supports single row for now
    private final int rowCount = 1;

    private final List<String> columnNames = new ArrayList<>();
    private final List<Object[]> columnData = new ArrayList<>();

    public KxSchemaBuilder() {
    }

    public KxSchemaBuilder addString(String name) {
        columnNames.add(name);
        columnData.add(new String[rowCount]);
        return this;
    }

    public KxSchemaBuilder addInt(String name) {
        columnNames.add(name);
        columnData.add(new Integer[rowCount]);
        return this;
    }
}
