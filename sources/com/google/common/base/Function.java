package com.google.common.base;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

public interface Function<F, T> {
    @CanIgnoreReturnValue
    T apply(F f);

    boolean equals(Object obj);
}
