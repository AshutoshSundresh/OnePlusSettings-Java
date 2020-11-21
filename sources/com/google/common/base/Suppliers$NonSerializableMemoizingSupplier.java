package com.google.common.base;

class Suppliers$NonSerializableMemoizingSupplier<T> implements Supplier<T> {
    volatile Supplier<T> delegate;
    volatile boolean initialized;
    T value;

    @Override // com.google.common.base.Supplier
    public T get() {
        if (!this.initialized) {
            synchronized (this) {
                if (!this.initialized) {
                    T t = this.delegate.get();
                    this.value = t;
                    this.initialized = true;
                    this.delegate = null;
                    return t;
                }
            }
        }
        return this.value;
    }

    public String toString() {
        Object obj = this.delegate;
        StringBuilder sb = new StringBuilder();
        sb.append("Suppliers.memoize(");
        if (obj == null) {
            obj = "<supplier that returned " + ((Object) this.value) + ">";
        }
        sb.append(obj);
        sb.append(")");
        return sb.toString();
    }
}
