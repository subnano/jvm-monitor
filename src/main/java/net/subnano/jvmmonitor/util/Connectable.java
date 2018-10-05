package net.subnano.jvmmonitor.util;

import java.io.Closeable;
import java.io.IOException;

public interface Connectable extends Closeable {

    void connect() throws IOException;

}
