package io.subnano.jvmmonitor;

import io.subnano.jvmmonitor.model.MutableGcEvent;
import io.subnano.jvmmonitor.recorder.EventRecorder;
import io.subnano.jvmmonitor.util.MonitorUtil;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;

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
public class GcEventMonitor implements VmMonitor {

    private static final String GC_LAST_CAUSE = "sun.gc.lastCause";
    private static final String GC_NAME = "sun.gc.collector.%s.name";
    private static final String GC_TIME = "sun.gc.collector.%s.time";

    private final Monitor monitorGcCause;
    private final EventRecorder recorder;
    private final Monitor timeMonitor;
    private final Monitor monitorGcName;
    private final MutableGcEvent event;

    private long previousPauseTime = 0;

    GcEventMonitor(String hostName,
                   MonitoredVm vm,
                   MonitorSettings settings,
                   EventRecorder recorder,
                   int generationIndex) {
        this.recorder = recorder;
        this.monitorGcCause = MonitorUtil.getMonitor(vm, GC_LAST_CAUSE);
        this.monitorGcName = MonitorUtil.getIndexedMonitor(vm, GC_NAME, generationIndex);
        this.timeMonitor = MonitorUtil.getIndexedMonitor(vm, GC_TIME, generationIndex);
        this.event = new MutableGcEvent();

        // add persistent values
        event.host(hostName);
        event.pid(Integer.parseInt(vm.getVmIdentifier().getUserInfo()));
        event.mainClass(getVmMainClass(vm));
    }

    @Override
    public void invoke() {
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

    private String getVmMainClass(MonitoredVm vm) {
        try {
            return MonitoredVmUtil.mainClass(vm, true);
        } catch (MonitorException e) {
            throw new IllegalArgumentException("Error obtaining Vm main class: ", e);
        }
    }
}
