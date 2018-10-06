package net.subnano.jvmmonitor.util;

/**
 * @author Mark Wardell
 */
public final class ByteUtil {

    public static final long KB = 1024;
    public static final long MB = 1024 * KB;
    public static final long GB = 1024 * MB;

    private ByteUtil() {
        // can't touch this
    }

    public static double toMB(long bytes) {
        return bytes / (double) MB;
    }

    public static double toGB(long bytes) {
        return bytes / (double) GB;
    }

}
