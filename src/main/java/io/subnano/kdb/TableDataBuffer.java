package io.subnano.kdb;

import kx.c;
import kx.c.Flip;

import java.util.Date;

/**
 * Not so much a buffer as a utility method to access the Object[][]
 */
public class TableDataBuffer {

    // We may want to support writing multiple rows
    private final int rowIndex = 0;
    // pointer to column to be updated
    private int colIndex = 0;
    private final Object[] tableData;
    private final Flip flip;

    TableDataBuffer(String[] columnNames, Object[] tableSchema) {
        this.tableData = tableSchema;
        this.flip = new Flip(new c.Dict(columnNames, tableSchema));
    }

    /**
     * Rests column index (rename?)
     */
    public void reset() {
        colIndex = 0;
    }

    public void addTimestamp(long timestamp) {
        Object colData = tableData[colIndex];
        if (!(colData instanceof Date[]))
            throw new ClassCastException(colData.getClass() + " cannot be cast to Date[] when updating column " + colIndex);
        Date date = ((Date[]) colData)[rowIndex];
        if (date == null) {
            date = new Date();
            ((Date[]) colData)[rowIndex] = date;
        }
        date.setTime(timestamp);
        colIndex++;
    }

    public void addString(String value) {
        Object colData = tableData[colIndex];
        if (!(colData instanceof String[]))
            throw new ClassCastException(colData.getClass() + " cannot be cast to String[] when updating column " + colIndex);
        ((String[]) colData)[rowIndex] = value;
        colIndex++;
    }

    public void addInt(int value) {
        Object colData = tableData[colIndex++];
        ((int[]) colData)[rowIndex] = value;
    }

    public void addLong(long value) {
        Object colData = tableData[colIndex++];
        ((long[]) colData)[rowIndex] = value;
    }

    Flip flip() {
        return flip;
    }
}


