package net.subnano.jvmmonitor.monitor;

import net.subnano.jvmmonitor.model.MutableHeapSample;
import net.subnano.jvmmonitor.recorder.EventRecorder;
import net.subnano.jvmmonitor.util.MonitorUtil;
import sun.jvmstat.monitor.Monitor;
import sun.jvmstat.monitor.MonitoredVm;

import java.io.IOException;

/**
 * sun.gc.generation.0.capacity = 178782208 (Bytes)
 * sun.gc.generation.0.maxCapacity = 178782208 (Bytes)
 * sun.gc.generation.0.minCapacity = 178782208 (Bytes)
 * <p>
 * sun.gc.generation.0.name = new
 * sun.gc.generation.0.space.0.capacity = 177733632 (Bytes)
 * sun.gc.generation.0.space.0.initCapacity = 0 (Bytes)
 * sun.gc.generation.0.space.0.maxCapacity = 177733632 (Bytes)
 * sun.gc.generation.0.space.0.name = eden
 * sun.gc.generation.0.space.0.used = 76927040 (Bytes)
 * <p>
 * sun.gc.generation.0.space.1.capacity = 524288 (Bytes)
 * sun.gc.generation.0.space.1.initCapacity = 0 (Bytes)
 * sun.gc.generation.0.space.1.maxCapacity = 59244544 (Bytes)
 * sun.gc.generation.0.space.1.name = s0
 * sun.gc.generation.0.space.1.used = 65536 (Bytes)
 * <p>
 * sun.gc.generation.0.space.2.capacity = 524288 (Bytes)
 * sun.gc.generation.0.space.2.initCapacity = 0 (Bytes)
 * sun.gc.generation.0.space.2.maxCapacity = 59244544 (Bytes)
 * sun.gc.generation.0.space.2.name = s1
 * sun.gc.generation.0.space.2.used = 0 (Bytes)
 * sun.gc.generation.0.spaces = 3
 * <p>
 * sun.gc.generation.1.capacity = 358088704 (Bytes)
 * sun.gc.generation.1.maxCapacity = 358088704 (Bytes)
 * sun.gc.generation.1.minCapacity = 358088704 (Bytes)
 * sun.gc.generation.1.name = old
 * sun.gc.generation.1.space.0.capacity = 358088704 (Bytes)
 * sun.gc.generation.1.space.0.initCapacity = 358088704 (Bytes)
 * sun.gc.generation.1.space.0.maxCapacity = 358088704 (Bytes)
 * sun.gc.generation.1.space.0.name = old
 * sun.gc.generation.1.space.0.used = 766080 (Bytes)
 * sun.gc.generation.1.spaces = 1
 */
public class HeapSampleMonitor extends AbstractVmMonitor {

    private static final String HEAP_USED = "sun.gc.generation.%s.space.0.used";
    private static final String HEAP_CAPACITY = "sun.gc.generation.%s.space.0.capacity";
    //"sun.gc.generation.0.capacity" ->

    private final EventRecorder recorder;
    private final Monitor heapUsedMonitor;
    private final Monitor heapCapacityMonitor;
    private final MutableHeapSample heapUsedSample;

    private long previousHeapUsed = 0;

    HeapSampleMonitor(final MonitoredVm vm,
                      final String hostName,
                      final long monitorInterval,
                      final EventRecorder recorder,
                      final int generationIndex) {
        super(vm, hostName, monitorInterval);
        this.recorder = recorder;
        this.heapUsedMonitor = MonitorUtil.getIndexedMonitor(vm, HEAP_USED, generationIndex);
        this.heapCapacityMonitor = MonitorUtil.getIndexedMonitor(vm, HEAP_CAPACITY, generationIndex);
        this.heapUsedSample = new MutableHeapSample();

        // add persistent values
        heapUsedSample.host(hostName);
        heapUsedSample.pid(super.pid());
        heapUsedSample.mainClass(super.mainClass());
        heapUsedSample.name(HeapNames.getName(generationIndex));
    }

    @Override
    protected void monitorFunction() {
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

}
