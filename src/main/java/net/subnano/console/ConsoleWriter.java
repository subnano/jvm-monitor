package net.subnano.console;

import net.subnano.jvmmonitor.util.Strings;

import java.io.PrintStream;

/**
 * Provides a richer interface to a console that supports Ansi escape sequences.
 *
 * One basic premise is that the console should be decorated before any absolute positioning can be performed.
 */
public class ConsoleWriter {

    private static final char ESC_CHAR_1 = 27;
    private static final char ESC_CHAR_2 = '[';
    private static final char ESC_CHAR_3 = 'm';

    // http://linux.about.com/library/cmd/blcmdl4_console_codes.htm
    private static final String CLEAR_LINE = "\u001B[K";
    private static final String CLEAR_WHOLE_LINE = "\u001B[2K";
    private static final String LINE_UP = "\u001B[1A";
    private static final char NEW_LINE = '\n';

    private final StringBuilder buffer;
    private final PrintStream out;
    private final int[] tmpArray = new int[64];    // 64 should do it

    private Ansi.Attribute attribute;

    public ConsoleWriter(PrintStream out) {
        this.out = out;
        this.buffer = new StringBuilder();
    }

    public ConsoleWriter reset() {
        appendEscapeSequence(Ansi.Attribute.Reset.value());
        buffer.append('m');
        return this;
    }

    public ConsoleWriter clearScreen() {
        appendEscapeSequence(Ansi.Erase.Screen.value());
        return this;
    }

    public ConsoleWriter cursor(final int row, final int col) {
        appendEscapeSequence('H', row, col);
        return this;
    }

    public ConsoleWriter clear(int lines) {
        int[] newLineIndexes = getNewLineIndexes(buffer);
        int absLines = Math.abs(lines);
        if (absLines > 0 && absLines < newLineIndexes.length) {
            int offset = newLineIndexes[newLineIndexes.length - (absLines + 1)];
            buffer.delete(offset + 1, buffer.length());
        } else {
            buffer.setLength(0);
        }
        out.print(Strings.repeat(CLEAR_WHOLE_LINE + LINE_UP, newLineIndexes.length));
        out.print(CLEAR_LINE);
        return this;
    }

    public ConsoleWriter print(String s) {
        doAttribute();
        buffer.append(s);
        return this;
    }

    public ConsoleWriter fg(Ansi.Color color) {
        appendEscapeSequence(String.valueOf(color.value() + 30));
        buffer.append('m');
        return this;
    }

    public ConsoleWriter bg(Ansi.Color color) {
        appendEscapeSequence(String.valueOf(color.value() + 40));
        buffer.append('m');
        return this;
    }

    public ConsoleWriter attribute(Ansi.Attribute attribute) {
        this.attribute = attribute;
        doAttribute();
        return this;
    }


    public ConsoleWriter line() {
        buffer.append(Strings.repeat("─", 50)).append(NEW_LINE);
        return this;
    }

    public void display() {
        if (buffer.charAt(buffer.length() - 1) != NEW_LINE) {
            buffer.append(NEW_LINE);
        }
        out.print(buffer.toString());
        out.flush();
    }

    public ConsoleWriter cursorOff() {
        appendEscapeSequence("?25");
        buffer.append('l');
        return this;
    }

    public ConsoleWriter cursorOn() {
        appendEscapeSequence("?25");
        buffer.append('h');
        return this;
    }

    private int[] getNewLineIndexes(StringBuilder buffer) {
        int count = 0;
        for (int i=0; i<buffer.length(); i++) {
            if (buffer.charAt(i) == NEW_LINE) {
                tmpArray[count++] = i;
            }
        }
        int[] newLineIndexes = new int[count];
        System.arraycopy(tmpArray, 0, newLineIndexes, 0, count);
        return newLineIndexes;
    }

    private ConsoleWriter doAttribute() {
        if (attribute != null) {
            appendEscapeSequence(String.valueOf(attribute.value()));
            buffer.append('m');
        }
        return this;
    }

    private void appendEscapeSequence(String command) {
        buffer.append(ESC_CHAR_1);
        buffer.append(ESC_CHAR_2);
        buffer.append(command);
    }

    private void appendEscapeSequence(int command) {
        buffer.append(ESC_CHAR_1);
        buffer.append(ESC_CHAR_2);
        buffer.append(command);
    }

    private void appendEscapeSequence(char command, int option) {
        buffer.append(ESC_CHAR_1);
        buffer.append(ESC_CHAR_2);
        buffer.append(option);
        buffer.append(command);
    }

    private void appendEscapeSequence(char command, int option1, int option2) {
        buffer.append(ESC_CHAR_1);
        buffer.append(ESC_CHAR_2);
        buffer.append(option1);
        buffer.append(';');
        buffer.append(option2);
        buffer.append(command);
    }

    public static void main(String[] args) {
        ConsoleWriter console = new ConsoleWriter(System.out);
        console.clearScreen();
        console.bg(Ansi.Color.Green);
        console.fg(Ansi.Color.Black);
        console.cursor(1, 1);
        console.print("Header Line");
        console.reset();
        console.cursor(2, 1);
        console.fg(Ansi.Color.Blue);
        console.print("1\n2\n3\n4\n5\n6\n7\n8\n");
        console.line();

        console.cursor(2, 20);
        console.print("");

        console.cursor(3, 30);
        console.print("");

        console.cursor(4, 40);
        console.print("");

        console.reset();
        console.display();
    }
}
