package io.subnano.kx;

public interface KxSchema {

    String tableName();

    String[] columnNames();

    Object[] data();

}
