package io.nano.jvmmonitor;

import io.nano.jvmmonitor.recorder.EventRecorder;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;

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
public class GcEventMonitor {

    private static final String GC_LAST_CAUSE = "sun.gc.lastCause";
    private static final String GC_NAME_YG = "sun.gc.collector.%s.name";
    private static final String GC_TIME_YG = "sun.gc.collector.%s.time";

    private final Monitor monitorGcCause;
    private final EventRecorder recorder;
    private final Monitor pauseTimeMonitor;
    private final Monitor monitorGcName;
    private final MutableGcEvent event;

    private long previousPauseTime = 0;

    public GcEventMonitor(String hostName,
                          MonitoredVm vm,
                          MonitorSettings settings,
                          EventRecorder recorder,
                          int generationIndex) {
        this.recorder = recorder;
        this.monitorGcCause = MonitorUtil.getMonitor(vm, GC_LAST_CAUSE);
        this.monitorGcName = MonitorUtil.getIndexedMonitor(vm, GC_NAME_YG, generationIndex);
        this.pauseTimeMonitor = MonitorUtil.getIndexedMonitor(vm, GC_TIME_YG, generationIndex);
        this.event = new MutableGcEvent();

        // add persistent values
        event.host(hostName);
        event.pid(vm.getVmIdentifier().getUserInfo());
        event.mainClass(getVmMainClass(vm));
    }

    void invoke() {
        long currentPauseTime = (long) pauseTimeMonitor.getValue();
        if (currentPauseTime != previousPauseTime) {
            event.cause((String) monitorGcCause.getValue());
            event.name((String) monitorGcName.getValue());
            event.pauseTime(currentPauseTime - previousPauseTime);
            previousPauseTime = currentPauseTime;
            recorder.record(event);
        }
    }

    private String getVmMainClass(MonitoredVm vm) {
        try {
            return MonitoredVmUtil.mainClass(vm, true);
        } catch (MonitorException e) {
            throw new IllegalArgumentException("Error obtaining Vm main class: ", e);
        }
    }
}
