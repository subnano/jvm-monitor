package net.subnano.jvmmonitor.console;

import net.subnano.console.Ansi;
import net.subnano.console.Ansi.Color;
import net.subnano.console.ConsoleWriter;
import net.subnano.jvmmonitor.util.Strings;

class JvmInfoGrid {

    private static final String[] COLS_HEADS = {
        "  PID", " CPU%", "Threads", "  Heap", "  Max", "    %", " YGCs", " OGCs", "Pause", "  Avg", "  Alloc", "  Rate", "MainClass"
    };

    private final ConsoleWriter console;
    private final int[] colWidths;
    private final int[] colIndexes;
    private final String[][] values;
    private final Color[][] colors;
    private final int rows;
    private final int cols;
    private final int columnOffset;

    JvmInfoGrid(int rows, int cols) {
        this.console = new ConsoleWriter(System.out);
        this.rows = rows;
        this.cols = cols;
        this.colWidths = calcColWidths(COLS_HEADS);
        this.colIndexes = calcColIndexes(COLS_HEADS);
        this.values = new String[rows][cols];
        this.colors = new Color[rows][cols];
        this.columnOffset = 1;
        initConsole();
    }

    private void initConsole() {
        // now update console
        console.clearScreen();
        console.cursorOff();
        console.attribute(Ansi.Attribute.IntensityFaint);
        // TODO decorate heading
        console.cursor(1, 1);
        console.bg(Color.Green);
        console.print(Strings.space(100));
        console.reset();
        for (int i = 0; i < COLS_HEADS.length; i++) {
            console.attribute(Ansi.Attribute.IntensityFaint);
            console.bg(Color.Green);
            console.fg(Color.Black);
            console.cursor(1, columnOffset + colIndexes[i]);
            console.print(Strings.padLeft(COLS_HEADS[i], colWidths[i]));
        }
        console.reset();
        console.fg(Color.White);
        for (int row = 2; row <= 8; row++) {
            console.cursor(row, 1);
            console.print("");
        }
        //console.line();
        console.display();
    }

    public void refresh() {
        console.attribute(Ansi.Attribute.IntensityFaint);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                String value = values[row][col];
                if (colors[row][col] != null) {
                    console.fg(colors[row][col]);
                }
                updateCell(row, col, value == null ? "" : value);
                console.reset();
            }
        }
        console.display();
    }

    public void reset() {
        console.cursorOn();
        console.display();
    }

    private void updateCell(int row, int col, String value) {
        console.cursor(row+2, columnOffset + colIndexes[col]);
        console.print(Strings.padLeft(value, colWidths[col]));
    }

    public void setValue(int row, int col, int value) {
        values[row][col] = String.valueOf(value);
    }

    public void setValue(int row, int col, String value) {
        values[row][col] = value;
    }

    public void setValue(int row, int col, double value, int precision) {
        values[row][col] = String.format("%." + precision + "f", value);
    }

    public void setColor(int row, int col, Color color) {
        colors[row][col] = color;
    }

    private int[] calcColWidths(String[] heads) {
        int[] values = new int[heads.length];
        for (int i = 0; i < heads.length; i++) {
            values[i] = heads[i].length();
        }
        return values;
    }

    private static int[] calcColIndexes(String[] heads) {
        int[] values = new int[heads.length];
        int index = 1;
        for (int i = 0; i < heads.length; i++) {
            values[i] = index;
            index += (heads[i].length() + 1);
        }
        return values;
    }
}
