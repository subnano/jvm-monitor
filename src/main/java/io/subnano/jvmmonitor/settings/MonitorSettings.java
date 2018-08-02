package io.subnano.jvmmonitor.settings;

/**
 * @author Mark Wardell
 */
public class MonitorSettings {

    private final long processScanInterval;
    private final long gcIntervalYoungGen;
    private final long gcIntervalOldGen;
    private final long heapSampleIntervalYoungGen;
    private final long heapSampleIntervalOldGen;

    private MonitorSettings(Builder builder) {
        this.processScanInterval = builder.processScanInterval;
        this.gcIntervalYoungGen = builder.gcIntervalYoungGen;
        this.gcIntervalOldGen = builder.gcIntervalOldGen;
        this.heapSampleIntervalYoungGen = builder.heapSampleIntervalYoungGen;
        this.heapSampleIntervalOldGen = builder.heapSampleIntervalOldGen;
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
    }
}
