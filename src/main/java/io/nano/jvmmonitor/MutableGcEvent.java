package io.nano.jvmmonitor;

public class MutableGcEvent implements GcEvent {

    private long timestamp;
    private String host;
    private String pid;
    private String mainClass;
    private String cause;
    private String name;
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
    public String pid() {
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
    public String name() {
        return name;
    }

    @Override
    public long pauseTime() {
        return pauseTime;
    }

    public void host(String host) {
        this.host = host;
    }

    public void pid(String pid) {
        this.pid = pid;
    }

    public void mainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public void cause(String cause) {
        this.cause = cause;
    }

    public void name(String name) {
        this.name = name;
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
                ", " + name +
                ", " + pauseTime +
                '}';
    }

}
