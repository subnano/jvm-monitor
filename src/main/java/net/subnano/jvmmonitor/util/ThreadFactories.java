package net.subnano.jvmmonitor.util;

import java.util.concurrent.ThreadFactory;

public final class ThreadFactories {

    public static ThreadFactory newThreadFactory(String threadName, boolean daemon) {
        return runnable -> {
            Thread thread = new Thread(runnable, threadName);
            thread.setDaemon(daemon);
            return thread;
        };
    }

}
