package io.subnano.jvmmonitor.model;

/**
 * @author Mark Wardell
 */
public interface HeapSample extends JvmEvent {

    String name();

    long heapUsed();

    long heapCapacity();

}
