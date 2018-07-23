package io.nano.jvmmonitor;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.Units;
import sun.jvmstat.monitor.VmIdentifier;
import sun.jvmstat.perfdata.monitor.AbstractPerfDataBuffer;
import sun.jvmstat.perfdata.monitor.protocol.local.PerfDataBuffer;
import sun.jvmstat.perfdata.monitor.protocol.local.PerfDataFile;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PerfDataReader {

    static void dump(Integer jvmId) throws IOException, MonitorException {
        File perfDataFile = PerfDataFile.getFile(jvmId);
        Path perfDataPath = Paths.get(perfDataFile.getPath());
        try (FileChannel channel = FileChannel.open(perfDataPath, StandardOpenOption.READ)) {
            ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size());
            HSPerfDataBuffer perfReaderBuffer = new HSPerfDataBuffer(buffer);
            perfReaderBuffer.findByPattern(".*")
                    .forEach(PerfDataReader::printMonitor);
        }
    }

    private static void printMonitor(Monitor monitor) {
        System.out.print(monitor.getName());
        System.out.print(" = ");
        System.out.print(monitor.getValue());

        Units unit = monitor.getUnits();

        if ((unit.intValue() != Units.NONE.intValue()) &&
                (unit.intValue() != Units.STRING.intValue())) {
            System.out.print(" (");
            System.out.print(monitor.getUnits());
            System.out.print(")");
        }
        System.out.println();
    }

    public static void dump2(VmIdentifier vmIdentifier) throws MonitorException {
        PerfDataBuffer perfDataBuffer = new PerfDataBuffer(vmIdentifier);
        Monitor byName = perfDataBuffer.findByName("sun.gc.cause");
        perfDataBuffer.findByPattern("sun.gc.*")
                .forEach(PerfDataReader::printMonitor);
    }

    private static class HSPerfDataBuffer extends AbstractPerfDataBuffer {
        public HSPerfDataBuffer(ByteBuffer buffer) throws MonitorException {
            super();
            super.createPerfDataBuffer(buffer, -1);
        }
    }
}
