package com.google.common.base;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

public interface Supplier<T> {
    @CanIgnoreReturnValue
    T get();
}
