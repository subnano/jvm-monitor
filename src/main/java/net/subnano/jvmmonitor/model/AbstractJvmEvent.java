package net.subnano.jvmmonitor.model;

/**
 * @author Mark Wardell
 */
abstract class AbstractJvmEvent implements JvmEvent {

    private long timestamp;
    private String host;
    private int pid;
    private String mainClass;

    public long timestamp() {
        return timestamp;
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

    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
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

}
