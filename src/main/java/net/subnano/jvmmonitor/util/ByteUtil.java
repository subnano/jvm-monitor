package net.subnano.jvmmonitor.util;

/**
 * @author Mark Wardell
 */
public final class ByteUtil {

    public static final long KB = 1000;
    public static final long MB = 1000 * KB;
    public static final long GB = 1000 * MB;

    private ByteUtil() {
        // can't touch this
    }

    public static double toKB(long bytes) {
        return bytes / (double) KB;
    }

    public static double toMB(long bytes) {
        return bytes / (double) MB;
    }

    public static double toGB(long bytes) {
        return bytes / (double) GB;
    }

    public static String getScaledByteText(long bytes, int precision) {
        if (bytes < KB) {
            return String.format("%dB", (int) toKB(bytes));
        }
        if (bytes < MB) {
            return String.format("%dK", (int) toKB(bytes));
        }
        if (bytes < GB) {
            return String.format("%dM", (int) toMB(bytes));
        }
        return String.format("%." + precision + "fG", toGB(bytes));
    }
}

//  999B - no need for decimal
//  xxxK - no need for decimal
// 111.1M - 1DP
//  11.1G - 1DP