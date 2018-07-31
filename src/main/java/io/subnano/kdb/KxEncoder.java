package io.subnano.kdb;

/**
 * @author Mark Wardell
 */
public interface KxEncoder<T> {

    void encoder(T anObject, TableDataBuffer buffer);

}
