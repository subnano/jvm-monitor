package io.nano.jvmmonitor;

public interface GcEvent extends JvmEvent {

    long timestamp();

    String host();

    int pid();

    String mainClass();

    String cause();

    String name();

    long pauseTime();

}
