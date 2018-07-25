package io.nano.jvmmonitor.kdb;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

class KxConnectionTest {

    private static final String[] COL_NAMES
            = new String[]{"time", "sym", "pid", "gcName", "pauseTime"};

    @Test
    void testKxConnection() throws IOException {
        KxConnection connection = new KxConnection("localhost", 5010);
        connection.connect();

        Date[] dates = new Date[1];
        String[] syms = new String[1];
        int[] pids = new int[1];
        String[] gcNames = new String[1];
        long[] pauseTimes = new long[1];

        dates[0] = new Date();
        syms[0] = "service1";
        pids[0] = 12345;
        gcNames[0] = "YoungGen";
        pauseTimes[0] = 864102;

        Object[] data = new Object[]{dates, syms, pids, gcNames, pauseTimes};
        c.Flip table = new c.Flip(new c.Dict(COL_NAMES, data));
        connection.update("events", table);

        connection.close();
    }


}