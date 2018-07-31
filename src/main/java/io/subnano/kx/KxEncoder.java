package io.subnano.kx;

/**
 * @author Mark Wardell
 */
public interface KxEncoder<T> {

    void encoder(T anObject, TableDataBuffer buffer);

}
