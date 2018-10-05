package net.subnano.jvmmonitor.monitor;

import net.subnano.jvmmonitor.model.MutableGcEvent;
import net.subnano.jvmmonitor.recorder.EventRecorder;
import net.subnano.jvmmonitor.util.MonitorUtil;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitoredVm;

import java.io.IOException;

/**
 * sun.gc.cause = No GC
 * sun.gc.collector.0.invocations = 0 (Events)ti
 * sun.gc.collector.0.lastEntryTime = 0 (Ticks)
 * sun.gc.collector.0.lastExitTime = 0 (Ticks)
 * sun.gc.collector.0.name = PSScavenge
 * sun.gc.collector.0.time = 0 (Ticks)
 * sun.gc.collector.1.invocations = 0 (Events)
 * sun.gc.collector.1.lastEntryTime = 0 (Ticks)
 * sun.gc.collector.1.lastExitTime = 0 (Ticks)
 * sun.gc.collector.1.name = PSParallelCompact
 * sun.gc.collector.1.time = 0 (Ticks)
 */
public class GcEventMonitor extends AbstractVmMonitor {

    private static final String GC_LAST_CAUSE = "sun.gc.lastCause";
    private static final String GC_NAME = "sun.gc.collector.%s.name";
    private static final String GC_TIME = "sun.gc.collector.%s.time";

    private final Monitor monitorGcCause;
    private final EventRecorder recorder;
    private final Monitor timeMonitor;
    private final Monitor monitorGcName;
    private final MutableGcEvent event;

    private long previousPauseTime = 0;

    GcEventMonitor(final MonitoredVm vm,
                   final String hostName,
                   final long monitorInterval,
                   final EventRecorder recorder,
                   final int generationIndex) {
        super(vm, hostName, monitorInterval);
        this.recorder = recorder;
        this.monitorGcCause = MonitorUtil.getMonitor(vm, GC_LAST_CAUSE);
        this.monitorGcName = MonitorUtil.getIndexedMonitor(vm, GC_NAME, generationIndex);
        this.timeMonitor = MonitorUtil.getIndexedMonitor(vm, GC_TIME, generationIndex);
        this.event = new MutableGcEvent();

        // add persistent values
        event.host(hostName);
        event.pid(super.pid());
        event.mainClass(super.mainClass());
        event.space(HeapNames.getName(generationIndex));
    }

    @Override
    protected void monitorFunction() {
        long currentPauseTime = (long) timeMonitor.getValue();
        if (currentPauseTime != previousPauseTime) {
            event.timestamp(System.currentTimeMillis());
            event.cause((String) monitorGcCause.getValue());
            event.collector((String) monitorGcName.getValue());
            event.pauseTime(((float) currentPauseTime - (float) previousPauseTime) / 1_000_000_000.0f);
            previousPauseTime = currentPauseTime;
            try {
                recorder.record(event);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

}
