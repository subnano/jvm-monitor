package net.subnano.jvmmonitor.model;

public interface JvmEvent {

    long timestamp();

    String host();

    int pid();

    String mainClass();

}
