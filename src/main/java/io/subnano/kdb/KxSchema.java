package io.subnano.kdb;

public interface KxSchema {

    String tableName();

    String[] columnNames();

    Object[] data();

}
