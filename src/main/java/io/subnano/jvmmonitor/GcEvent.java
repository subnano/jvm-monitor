package io.subnano.jvmmonitor;

public interface GcEvent extends JvmEvent {

    long timestamp();

    String host();

    int pid();

    String mainClass();

    String cause();

    String collector();

    float pauseTime();

}
