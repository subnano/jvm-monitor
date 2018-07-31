package io.subnano.kx;

import java.io.IOException;

/**
 * @author Mark Wardell
 */
public interface KxTableWriter<T> {

    void write(T record) throws IOException;

}
