package io.subnano.kdb;

/**
 * @author Mark Wardell
 */
public interface KxTableWriter<T> {

    void write(T record);

}
