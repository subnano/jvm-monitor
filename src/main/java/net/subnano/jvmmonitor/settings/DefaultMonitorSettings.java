package net.subnano.jvmmonitor.settings;

/**
 * TODO Move defaults out and only have one Settings
 *
 * @author Mark Wardell
 */
public class DefaultMonitorSettings {

    private static final String PROCESS_SCAN_INTERVAL_KEY = "jvmmonitor.process.scan.interval";
    private static final String GC_INTERVAL_YG_KEY = "jvmmonitor.gc.interval.young";
    private static final String GC_INTERVAL_OG_KEY = "jvmmonitor.gc.interval.old";
    private static final String HEAP_SAMPLE_INTERVAL_YG_KEY = "jvmmonitor.heap.sample.interval.young";
    private static final String HEAP_SAMPLE_INTERVAL_OG_KEY = "jvmmonitor.heap.sample.interval.old";

    private static final long DEFAULT_PROCESS_SCAN_INTERVAL = 5_000;
    private static final long DEFAULT_GC_INTERVAL_YG = 250;
    private static final long DEFAULT_GC_INTERVAL_OG = 1_000;
    private static final long DEFAULT_HEAP_SAMPLE_INTERVAL_YG = 1_000;
    private static final long DEFAULT_HEAP_SAMPLE_INTERVAL_OG = 5_000;

    public static MonitorSettings newInstance(String[] args) {
        return new MonitorSettings.Builder()
                .processScanInterval(getLongValue(PROCESS_SCAN_INTERVAL_KEY, DEFAULT_PROCESS_SCAN_INTERVAL))
                .gcIntervalYoungGen(getLongValue(GC_INTERVAL_YG_KEY, DEFAULT_GC_INTERVAL_YG))
                .gcIntervalOldGen(getLongValue(GC_INTERVAL_OG_KEY, DEFAULT_GC_INTERVAL_OG))
                .heapSampleIntervalYoungGen(getLongValue(HEAP_SAMPLE_INTERVAL_YG_KEY, DEFAULT_HEAP_SAMPLE_INTERVAL_YG))
                .heapSampleIntervalOldGen(getLongValue(HEAP_SAMPLE_INTERVAL_OG_KEY, DEFAULT_HEAP_SAMPLE_INTERVAL_OG))
                .build();
    }

    private static long getLongValue(String key, long defaultValue) {
        String propertyValue = System.getProperty(key);
        if (propertyValue != null) {
            try {
                return Long.parseLong(propertyValue);
            } catch (NumberFormatException e) {
                // cannot parse property value
            }
        }
        return defaultValue;
    }
}
