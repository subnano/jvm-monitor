package net.subnano.console;

public class Ansi {

    public static final Ansi Yellow = new Ansi(AnsiCodes.YELLOW);
    public static final Ansi Blue = new Ansi(AnsiCodes.BLUE);
    public static final Ansi Red = new Ansi(AnsiCodes.RED);
    public static final Ansi Green = new Ansi(AnsiCodes.GREEN);

    //    LowIntensity(new Ansi(LOW_INTENSITY)
//    HighIntensity(new Ansi(HIGH_INTENSITY)
//    Bold(HighIntensity
//    Normal(LowIntensity
//
//    Italic(new Ansi(ITALIC)
//    Underline(new Ansi(UNDERLINE)
//    Blink(new Ansi(BLINK)
//    RapidBlink(new Ansi(RAPID_BLINK)
//
    public enum Color {
        Black(0),
        Red(1),
        Green(2),
        Yellow(3),
        Blue(4),
        Magenta(5),
        Cyan(6),
        White(7),
        Default(9)
        ;

        private final int value;

        Color(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    public enum Erase {
        Line("K"),
        Screen("2J")
        ;

        private final String value;

        Erase(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }

    public enum Attribute {
        Reset(0),
        IntensityBold(1),
        IntensityFaint(2),
        Italic(3),
        Underline(4),
        BlinkSlow(5),
        BlinkFast(6),
        NegativeOn(7),
        ConcealOn(8),
        StrikethroughOn(9),
        UnderlineDouble(21),
        IntensityBoldOff(22),
        ItalicOff(23),
        UnderlineOff(24),
        BlinkOff(25),
        NegativeOff(27),
        ConcealOff(28),
        StrikethroughOff(29);

        private final int value;

        Attribute(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    public static final Ansi BgBlack = new Ansi(AnsiCodes.BACKGROUND_BLACK);
    public static final Ansi BgRed = new Ansi(AnsiCodes.BACKGROUND_RED);
    public static final Ansi BgGreen = new Ansi(AnsiCodes.BACKGROUND_GREEN);
    public static final Ansi BgYellow = new Ansi(AnsiCodes.BACKGROUND_YELLOW);
    public static final Ansi BgBlue = new Ansi(AnsiCodes.BACKGROUND_BLUE);
    public static final Ansi BgMagenta = new Ansi(AnsiCodes.BACKGROUND_MAGENTA);
    public static final Ansi BgCyan = new Ansi(AnsiCodes.BACKGROUND_CYAN);
    public static final Ansi BgWhite = new Ansi(AnsiCodes.BACKGROUND_WHITE);

    private final String code;

    Ansi(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public Ansi and(Ansi other) {
        return new Ansi(code + other.code);
    }

    public String format(String str) {
        return code + str + AnsiCodes.SANE;
    }

    static class AnsiCodes {
        // Color code strings from:
        // http://www.topmudsites.com/forums/mud-coding/413-java-ansi.html
        private static String SANE = "\u001B[0m";
        private static String HIGH_INTENSITY = "\u001B[1m";
        private static String LOW_INTENSITY = "\u001B[2m";
        private static String ITALIC = "\u001B[3m";
        private static String UNDERLINE = "\u001B[4m";
        private static String BLINK = "\u001B[5m";
        private static String RAPID_BLINK = "\u001B[6m";
        private static String REVERSE_VIDEO = "\u001B[7m";
        private static String INVISIBLE_TEXT = "\u001B[8m";

        private static String BLACK = "\u001B[30m";
        private static String RED = "\u001B[31m";
        private static String GREEN = "\u001B[32m";
        private static String YELLOW = "\u001B[33m";
        private static String BLUE = "\u001B[34m";
        private static String MAGENTA = "\u001B[35m";
        private static String CYAN = "\u001B[36m";
        private static String WHITE = "\u001B[37m";

        private static String BACKGROUND_BLACK = "\u001B[40m";
        private static String BACKGROUND_RED = "\u001B[41m";
        private static String BACKGROUND_GREEN = "\u001B[42m";
        private static String BACKGROUND_YELLOW = "\u001B[43m";
        private static String BACKGROUND_BLUE = "\u001B[44m";
        private static String BACKGROUND_MAGENTA = "\u001B[45m";
        private static String BACKGROUND_CYAN = "\u001B[46m";
        private static String BACKGROUND_WHITE = "\u001B[47m";

    }
}
