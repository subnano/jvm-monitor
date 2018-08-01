package io.subnano.jvmmonitor;

import io.subnano.jvmmonitor.model.MutableHeapSample;
import io.subnano.jvmmonitor.recorder.EventRecorder;
import io.subnano.jvmmonitor.util.MonitorUtil;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitorException;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;

import java.io.IOException;

/**
 * sun.gc.generation.0.heapCapacity = 178782208 (Bytes)
 * sun.gc.generation.0.maxCapacity = 178782208 (Bytes)
 * sun.gc.generation.0.minCapacity = 178782208 (Bytes)
 * <p>
 * sun.gc.generation.0.name = new
 * sun.gc.generation.0.space.0.heapCapacity = 177733632 (Bytes)
 * sun.gc.generation.0.space.0.initCapacity = 0 (Bytes)
 * sun.gc.generation.0.space.0.maxCapacity = 177733632 (Bytes)
 * sun.gc.generation.0.space.0.name = eden
 * sun.gc.generation.0.space.0.used = 76927040 (Bytes)
 * <p>
 * sun.gc.generation.0.space.1.heapCapacity = 524288 (Bytes)
 * sun.gc.generation.0.space.1.initCapacity = 0 (Bytes)
 * sun.gc.generation.0.space.1.maxCapacity = 59244544 (Bytes)
 * sun.gc.generation.0.space.1.name = s0
 * sun.gc.generation.0.space.1.used = 65536 (Bytes)
 * <p>
 * sun.gc.generation.0.space.2.heapCapacity = 524288 (Bytes)
 * sun.gc.generation.0.space.2.initCapacity = 0 (Bytes)
 * sun.gc.generation.0.space.2.maxCapacity = 59244544 (Bytes)
 * sun.gc.generation.0.space.2.name = s1
 * sun.gc.generation.0.space.2.used = 0 (Bytes)
 * sun.gc.generation.0.spaces = 3
 * <p>
 * sun.gc.generation.1.heapCapacity = 358088704 (Bytes)
 * sun.gc.generation.1.maxCapacity = 358088704 (Bytes)
 * sun.gc.generation.1.minCapacity = 358088704 (Bytes)
 * sun.gc.generation.1.name = old
 * sun.gc.generation.1.space.0.heapCapacity = 358088704 (Bytes)
 * sun.gc.generation.1.space.0.initCapacity = 358088704 (Bytes)
 * sun.gc.generation.1.space.0.maxCapacity = 358088704 (Bytes)
 * sun.gc.generation.1.space.0.name = old
 * sun.gc.generation.1.space.0.used = 766080 (Bytes)
 * sun.gc.generation.1.spaces = 1
 */
public class HeapSampleMonitor implements VmMonitor {

    private static final String HEAP_NAME_YG = "YoungGen";
    private static final String HEAP_NAME_OG = "OldGen";
    private static final String HEAP_USED = "sun.gc.generation.%s.space.0.used";
    private static final String HEAP_CAPACITY = "sun.gc.generation.%s.space.0.heapCapacity";

    private final EventRecorder recorder;
    private final Monitor heapUsedMonitor;
    private final Monitor heapCapacityMonitor;
    private final MutableHeapSample heapUsedSample;

    private long previousHeapUsed = 0;

    HeapSampleMonitor(final String hostName,
                      final MonitoredVm vm,
                      final MonitorSettings settings,
                      final EventRecorder recorder,
                      final int generationIndex) {
        this.recorder = recorder;
        this.heapUsedMonitor = MonitorUtil.getIndexedMonitor(vm, HEAP_USED, generationIndex);
        this.heapCapacityMonitor = MonitorUtil.getIndexedMonitor(vm, HEAP_CAPACITY, generationIndex);
        this.heapUsedSample = new MutableHeapSample();

        // add persistent values
        heapUsedSample.host(hostName);
        heapUsedSample.pid(Integer.parseInt(vm.getVmIdentifier().getUserInfo()));
        heapUsedSample.mainClass(getVmMainClass(vm));
        heapUsedSample.name(heapSpaceName(generationIndex));
    }

    @Override
    public void invoke() {
        long currentHeapUsed = (long) heapUsedMonitor.getValue();
        if (currentHeapUsed != previousHeapUsed) {
            heapUsedSample.timestamp(System.currentTimeMillis());
            heapUsedSample.heapUsed((long) heapUsedMonitor.getValue());
            heapUsedSample.heapCapacity((long) heapCapacityMonitor.getValue());
            previousHeapUsed = currentHeapUsed;
            try {
                recorder.record(heapUsedSample);
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

    private static String heapSpaceName(int generationIndex) {
        if (generationIndex == 0)
            return HEAP_NAME_YG;
        if (generationIndex == 1)
            return HEAP_NAME_OG;
        throw new IllegalArgumentException("Invalid generation space: " + generationIndex);
    }
}
