package net.subnano.jvmmonitor.settings;

/**
 * @author Mark Wardell
 */
public class MonitorSettings {

    private static final String KDB_HOST_KEY = "kdb.host";
    private static final String KDB_PORT_KEY = "kdb.port";

    private final String kdbHost;
    private final int kdbPort;
    private final long processScanInterval;
    private final long gcIntervalYoungGen;
    private final long gcIntervalOldGen;
    private final long heapSampleIntervalYoungGen;
    private final long heapSampleIntervalOldGen;

    private MonitorSettings(Builder builder) {
        this.kdbHost = builder.kdbHost;
        this.kdbPort = builder.kdbPort;
        this.processScanInterval = builder.processScanInterval;
        this.gcIntervalYoungGen = builder.gcIntervalYoungGen;
        this.gcIntervalOldGen = builder.gcIntervalOldGen;
        this.heapSampleIntervalYoungGen = builder.heapSampleIntervalYoungGen;
        this.heapSampleIntervalOldGen = builder.heapSampleIntervalOldGen;
    }

    public String kdbHost() {
        return kdbHost;
    }

    public int kdbPort() {
        return kdbPort;
    }

    public long processScanInterval() {
        return processScanInterval;
    }

    public long gcIntervalYoungGen() {
        return gcIntervalYoungGen;
    }

    public long gcIntervalOldGen() {
        return gcIntervalOldGen;
    }

    public long heapSampleIntervalYoungGen() {
        return heapSampleIntervalYoungGen;
    }

    public long heapSampleIntervalOldGen() {
        return heapSampleIntervalOldGen;
    }

    public static class Builder {

        private String kdbHost = getProperty(KDB_HOST_KEY, "localhost");
        private int kdbPort = getIntProperty(KDB_PORT_KEY, 5001);

        private long processScanInterval;
        private long gcIntervalYoungGen;
        private long gcIntervalOldGen;
        private long heapSampleIntervalYoungGen;
        private long heapSampleIntervalOldGen;

        public Builder processScanInterval(long value) {
            processScanInterval = value;
            return this;
        }

        public Builder gcIntervalYoungGen(long value) {
            gcIntervalYoungGen = value;
            return this;
        }

        public Builder gcIntervalOldGen(long value) {
            gcIntervalOldGen = value;
            return this;
        }

        public Builder heapSampleIntervalYoungGen(long value) {
            heapSampleIntervalYoungGen = value;
            return this;
        }

        public Builder heapSampleIntervalOldGen(long value) {
            heapSampleIntervalOldGen = value;
            return this;
        }

        public MonitorSettings build() {
            return new MonitorSettings(this);
        }

        private String getProperty(String key, String defaultValue) {
            return System.getProperty(key, defaultValue);
        }

        private int getIntProperty(String key, int defaultValue) {
            String propertyValue = System.getProperty(key);
            if (propertyValue != null) {
                return Integer.parseInt(propertyValue);
            }
            return defaultValue;
        }
    }
}
