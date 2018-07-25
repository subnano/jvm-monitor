package io.nano.jvmmonitor;

import java.io.Closeable;
import java.io.IOException;

public interface Connectable extends Closeable {

    void connect() throws IOException;

}
