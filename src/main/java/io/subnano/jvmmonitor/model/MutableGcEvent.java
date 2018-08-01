package io.subnano.jvmmonitor.model;

public class MutableGcEvent extends AbstractJvmEvent implements GcEvent {

    private String cause;
    private String collector;
    private float pauseTime;

    @Override
    public String cause() {
        return cause;
    }

    @Override
    public String collector() {
        return collector;
    }

    @Override
    public float pauseTime() {
        return pauseTime;
    }

    public void cause(String cause) {
        this.cause = cause;
    }

    public void collector(String collector) {
        this.collector = collector;
    }

    public void pauseTime(float pauseTime) {
        this.pauseTime = pauseTime;
    }

    @Override
    public String toString() {
        return "GcEvent{" +
                host() +
                ", " + pid() +
                ", " + mainClass() +
                ", " + cause +
                ", " + collector +
                ", " + pauseTime +
                '}';
    }

}
