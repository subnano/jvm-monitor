package io.nano.jvmmonitor;

import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;

import java.util.concurrent.locks.LockSupport;

/**
 sun.gc.cause = No GC
 sun.gc.collector.0.invocations = 0 (Events)ti
 sun.gc.collector.0.lastEntryTime = 0 (Ticks)
 sun.gc.collector.0.lastExitTime = 0 (Ticks)
 sun.gc.collector.0.name = PSScavenge
 sun.gc.collector.0.time = 0 (Ticks)
 sun.gc.collector.1.invocations = 0 (Events)
 sun.gc.collector.1.lastEntryTime = 0 (Ticks)
 sun.gc.collector.1.lastExitTime = 0 (Ticks)
 sun.gc.collector.1.name = PSParallelCompact
 sun.gc.collector.1.time = 0 (Ticks)
 */
public class YoungGenEventMonitor {

    private static final String HRT_FREQUENCY = "sun.os.hrt.frequency";
    private static final String GC_LAST_CAUSE = "sun.gc.lastCause";
    private static final String GC_EVENTS_YG = "sun.gc.collector.0.invocations";
    private static final String GC_EVENTS_OG = "sun.gc.collector.1.invocations";
    private static final String GC_TIME_YG = "sun.gc.collector.0.time";
    private static final String GC__ENTRY_TIME_YG = "sun.gc.collector.0.lastEntryTime";
    private static final String GC_EXIT_TIME_YG = "sun.gc.collector.0.lastExitTime";
    private static final String GC_TIME_OG = "sun.gc.collector.1.time";

    private final String hostName;
    private final MonitoredVm vm;
    private final MonitorSettings settings;
    private final Monitor monitorGcCause;
    private final LongMonitor monitorGcEventsYG;
    private final String mainVmClass;
    private final EventRecorder recorder;
    private final long hrtFrequency;
    private final LongMonitor pauseTimeMonitor;
    private final LongMonitor entryTimeMonitor;
    private final LongMonitor exitTimeMonitor;

    public YoungGenEventMonitor(String hostName,
                                MonitoredVm vm,
                                MonitorSettings settings,
                                EventRecorder recorder) {
        this.hostName = hostName;
        this.vm = vm;
        this.settings = settings;
        this.hrtFrequency = MonitorUtil.getLongValue(vm, HRT_FREQUENCY);
        this.mainVmClass = getVmMainClass(vm);
        this.recorder = recorder;
        this.monitorGcCause = MonitorUtil.getMonitor(vm, GC_LAST_CAUSE);
        this.monitorGcEventsYG = MonitorUtil.getLongMonitor(vm, GC_EVENTS_YG);
        this.entryTimeMonitor = MonitorUtil.getLongMonitor(vm, GC__ENTRY_TIME_YG);
        this.exitTimeMonitor = MonitorUtil.getLongMonitor(vm, GC_EXIT_TIME_YG);
        this.pauseTimeMonitor = MonitorUtil.getLongMonitor(vm, GC_TIME_YG);
    }

    private String getVmMainClass(MonitoredVm vm) {
        try {
            return MonitoredVmUtil.mainClass(vm, true);
        } catch (MonitorException e) {
            throw new IllegalArgumentException("Error obtaining Vm main class: ", e);
        }
    }

    public void invoke() {
        long currentCollectorEvents = monitorGcEventsYG.getLongValue();
        long currentPauseTime = pauseTimeMonitor.getLongValue();
        long currentEntryTime = entryTimeMonitor.getLongValue();
        long currentExitTime = exitTimeMonitor.getLongValue();
        if (monitorGcEventsYG.changed() || pauseTimeMonitor.changed()
                || entryTimeMonitor.changed() || exitTimeMonitor.changed()) {
            System.out.println(String.format("RAW %s, %s, %d, %d, %d, %d, %d",
                    mainVmClass,
                    monitorGcCause.getValue(),
                    currentCollectorEvents,
                    currentEntryTime,
                    currentExitTime,
                    currentExitTime - currentEntryTime,
                    currentPauseTime - pauseTimeMonitor.previousValue()));
        }

        if (currentCollectorEvents > monitorGcEventsYG.previousValue()) {
            // MICRO_FACTOR = 1_000_000_000
            recorder.record(new GcEvent(
                    hostName,
                    vm.getVmIdentifier().getUserInfo(),
                    mainVmClass,
                    (String) monitorGcCause.getValue(),
                    (long) ((currentPauseTime - pauseTimeMonitor.previousValue()) / (double) 1000)
            ));
        }
    }
}
