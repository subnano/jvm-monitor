package net.subnano.jvmmonitor.monitor;

public class HeapNames {

    public static final String YOUNG_GEN = "YoungGen";
    public static final String OLD_GEN = "OldGen";

    private HeapNames() {
        // can't touch this
    }

    static String getName(int generationIndex) {
        switch (generationIndex) {
            case 0:
                return YOUNG_GEN;
            case 1:
                return OLD_GEN;
            default:
                throw new IllegalArgumentException("Invalid generation space: " + generationIndex);
        }
    }
}
