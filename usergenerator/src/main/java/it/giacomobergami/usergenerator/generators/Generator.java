package it.giacomobergami.usergenerator.generators;

import java.util.Iterator;

public interface Generator<T> extends Iterator<T> {
    default boolean hasNext() { return true; }
    T next(long ustep);
}
