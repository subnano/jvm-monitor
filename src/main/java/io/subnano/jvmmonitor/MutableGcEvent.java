package io.subnano.jvmmonitor;

public class MutableGcEvent implements GcEvent {

    private long timestamp;
    private String host;
    private int pid;
    private String mainClass;
    private String cause;
    private String collector;
    private long pauseTime;

    public long timestamp() {
        return timestamp;
    }

    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String host() {
        return host;
    }

    @Override
    public int pid() {
        return pid;
    }

    @Override
    public String mainClass() {
        return mainClass;
    }

    @Override
    public String cause() {
        return cause;
    }

    @Override
    public String collector() {
        return collector;
    }

    @Override
    public long pauseTime() {
        return pauseTime;
    }

    public void host(String host) {
        this.host = host;
    }

    public void pid(int pid) {
        this.pid = pid;
    }

    public void mainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public void cause(String cause) {
        this.cause = cause;
    }

    public void collector(String collector) {
        this.collector = collector;
    }

    public void pauseTime(long pauseTime) {
        this.pauseTime = pauseTime;
    }

    @Override
    public String toString() {
        return "GcEvent{" +
                host +
                ", " + pid +
                ", " + mainClass +
                ", " + cause +
                ", " + collector +
                ", " + pauseTime +
                '}';
    }

}
