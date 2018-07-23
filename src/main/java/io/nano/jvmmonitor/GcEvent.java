package io.nano.jvmmonitor;

public class GcEvent implements JvmEvent {

    private final String hostName;
    private final String pid;
    private final String mainVmClass;
    private final String cause;
    private final long pauseTimeMicros;

    public GcEvent(String hostName, String pid, String mainVmClass, String cause, long pauseTimeMicros) {
        this.hostName = hostName;
        this.pid = pid;
        this.mainVmClass = mainVmClass;
        this.cause = cause;
        this.pauseTimeMicros = pauseTimeMicros;
    }

    public String getCause() {
        return cause;
    }

    public long getTime() {
        return pauseTimeMicros;
    }

    @Override
    public String toString() {
        return "GcEvent{" +
                hostName +
                ", " + pid +
                ", " + mainVmClass +
                ", " + cause +
                ", " + pauseTimeMicros +
                '}';
    }
}
