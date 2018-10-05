package net.subnano.jvmmonitor.monitor;

/**
 * Simple interface for all monitor implementations.
 *
 * @author Mark Wardell
 */
public interface VmMonitor {

    String hostName();

    String mainClass();

    int pid();

    void invoke();
}
