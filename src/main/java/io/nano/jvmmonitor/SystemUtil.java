package io.nano.jvmmonitor;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class SystemUtil {

    private SystemUtil() {
        // can't touch this - da da da da
    }

    public static String getHostName() {
        String hostname = "localhost";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // ignore
        }
        return hostname;
    }
}
