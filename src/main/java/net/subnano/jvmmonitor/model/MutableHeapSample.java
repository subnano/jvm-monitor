package net.subnano.jvmmonitor.model;

public class MutableHeapSample extends AbstractJvmEvent implements HeapSample {

    private String name;
    private long heapUsed;
    private long heapCapacity;

    @Override
    public String name() {
        return name;
    }

    @Override
    public long heapUsed() {
        return heapUsed;
    }

    @Override
    public long heapCapacity() {
        return heapCapacity;
    }

    public void name(String name) {
        this.name = name;
    }

    public void heapUsed(long bytesUsed) {
        this.heapUsed = bytesUsed;
    }

    public void heapCapacity(long capacity) {
        this.heapCapacity = capacity;
    }

    @Override
    public String toString() {
        return "HeapSample{" +
                host() +
                ", " + pid() +
                ", " + mainClass() +
                ", " + name +
                ", " + heapUsed +
                ", " + heapCapacity +
                '}';
    }

}
