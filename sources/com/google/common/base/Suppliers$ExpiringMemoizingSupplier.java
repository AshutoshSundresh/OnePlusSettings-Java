package com.google.common.base;

import java.io.Serializable;

class Suppliers$ExpiringMemoizingSupplier<T> implements Supplier<T>, Serializable {
    private static final long serialVersionUID = 0;
    final Supplier<T> delegate;
    final long durationNanos;
    volatile transient long expirationNanos;
    volatile transient T value;

    @Override // com.google.common.base.Supplier
    public T get() {
        long j = this.expirationNanos;
        long systemNanoTime = Platform.systemNanoTime();
        if (j == 0 || systemNanoTime - j >= 0) {
            synchronized (this) {
                if (j == this.expirationNanos) {
                    T t = this.delegate.get();
                    this.value = t;
                    long j2 = systemNanoTime + this.durationNanos;
                    if (j2 == 0) {
                        j2 = 1;
                    }
                    this.expirationNanos = j2;
                    return t;
                }
            }
        }
        return this.value;
    }

    public String toString() {
        return "Suppliers.memoizeWithExpiration(" + this.delegate + ", " + this.durationNanos + ", NANOS)";
    }
}
