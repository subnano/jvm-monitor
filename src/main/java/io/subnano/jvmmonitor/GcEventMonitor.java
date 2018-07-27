package io.subnano.jvmmonitor;

import io.subnano.jvmmonitor.recorder.EventRecorder;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;

import java.util.Date;

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
    private static final String GC_NAME = "sun.gc.collector.%s.name";
    private static final String GC_TIME = "sun.gc.collector.%s.time";
    private static final String GC_ENTRY_TIME = "sun.gc.collector.%s.lastEntryTime";
    private static final String HRT_FREQUENCY = "sun.os.hrt.frequency";

    private final Monitor monitorGcCause;
    private final EventRecorder recorder;
    private final Monitor pauseTimeMonitor;
    private final Monitor monitorGcName;
    private final MutableGcEvent event;
    private final Monitor entryTimeMonitor;
    private final long hrtFrequency;
    private final MonitoredVm vm;

    private long previousPauseTime = 0;

    public GcEventMonitor(String hostName,
                          MonitoredVm vm,
                          MonitorSettings settings,
                          EventRecorder recorder,
                          int generationIndex) {
        this.recorder = recorder;
        this.monitorGcCause = MonitorUtil.getMonitor(vm, GC_LAST_CAUSE);
        this.monitorGcName = MonitorUtil.getIndexedMonitor(vm, GC_NAME, generationIndex);
        this.pauseTimeMonitor = MonitorUtil.getIndexedMonitor(vm, GC_TIME, generationIndex);
        this.entryTimeMonitor = MonitorUtil.getIndexedMonitor(vm, GC_ENTRY_TIME, generationIndex);
        this.hrtFrequency = MonitorUtil.getLongValue(vm, HRT_FREQUENCY);
        this.vm = vm;
        this.event = new MutableGcEvent();

        // add persistent values
        event.host(hostName);
        event.pid(Integer.parseInt(vm.getVmIdentifier().getUserInfo()));
        event.mainClass(getVmMainClass(vm));
    }

    void invoke() {
        long currentPauseTime = (long) pauseTimeMonitor.getValue();
        if (currentPauseTime != previousPauseTime) {
            event.timestamp(System.currentTimeMillis());
            event.cause((String) monitorGcCause.getValue());
            event.collector((String) monitorGcName.getValue());
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
