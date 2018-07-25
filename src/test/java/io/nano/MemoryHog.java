package io.nano;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemoryHog {

    private static final int CAPACITY = 5000;
    private static final int ALLOCATE_PERIOD = 25;
    private static final int DEALLOCATE_PERIOD = 1000 * 10;
    private static final int BUFFER_SIZE = 2 * 1024 * 1024;

    private static LinkedList<byte[]> LIST = new LinkedList<>();

    private static void allocate() {
        byte[] bytes = new byte[BUFFER_SIZE];
//        LIST.addFirst(bytes);
//        if (LIST.size() > CAPACITY) {
//            LIST.removeLast();
//        }
    }

    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            allocate();
        }, 0, ALLOCATE_PERIOD, TimeUnit.MILLISECONDS);

        executor.scheduleAtFixedRate(() -> {
            //LIST.clear();
        }, 0, DEALLOCATE_PERIOD, TimeUnit.MILLISECONDS);
    }
}
