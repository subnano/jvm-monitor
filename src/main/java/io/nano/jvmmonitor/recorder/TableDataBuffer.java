package io.nano.jvmmonitor.recorder;

import java.util.Date;

public class TableDataBuffer {

    // We may want to support writing multiple rows
    private final int rowIndex = 0;
    private final Object[] tableData;

    TableDataBuffer(Object[] tableData) {
        this.tableData = tableData;
    }

    public void setTimestamp(int index, long timestamp) {
        Object colData = tableData[index];
        if (!(colData instanceof Date[]))
            throw new ClassCastException(colData.getClass() + " cannot be cast to Date[] when updating column " + index);
        Date date = ((Date[]) colData)[rowIndex];
        if (date == null) {
            date = new Date();
            ((Date[]) colData)[rowIndex] = date;
        }
        date.setTime(timestamp);
    }

    public void setString(int index, String value) {
        Object colData = tableData[index];
        if (!(colData instanceof String[]))
            throw new ClassCastException(colData.getClass() + " cannot be cast to String[] when updating column " + index);
        ((String[]) colData)[rowIndex] = value;
    }

    public void setInt(int index, int value) {
        Object colData = tableData[index];
        ((int[]) colData)[rowIndex] = value;
    }

    public void setLong(int index, long value) {
        Object colData = tableData[index];
        ((long[]) colData)[rowIndex] = value;
    }

}


