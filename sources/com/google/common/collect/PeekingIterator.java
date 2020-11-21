package com.google.common.collect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;

public interface PeekingIterator<E> extends Iterator<E> {
    @Override // java.util.Iterator
    @CanIgnoreReturnValue
    E next();

    E peek();
}
