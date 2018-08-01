package io.subnano.jvmmonitor.model;

public interface GcEvent extends JvmEvent {

    String cause();

    String collector();

    float pauseTime();

}
