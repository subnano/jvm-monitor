package net.subnano.jvmmonitor.util;

import java.util.Collections;

public final class Strings {

    public static final String EMPTY = "";
    public static final String SPACE = " ";

    private Strings() {
        // can't touch this
    }

    public static String padRight(long value, int width) {
        return padRight(String.valueOf(value), width);
    }

    public static String padRight(String str, int width) {
        return str.length() >= width ? str : str + space(width - str.length());
    }

    public static String padLeft(long value, int width) {
        return padLeft(String.valueOf(value), width);
    }

    public static String padLeft(String str, int width) {
        return str == null || str.length() >= width ? str : space(width - str.length()) + str;
    }

    public static String space(int len) {
        return repeat(SPACE, len);
    }

    public static String repeat(String str, int count) {
        return String.join(EMPTY, Collections.nCopies(count, str));
    }

}

