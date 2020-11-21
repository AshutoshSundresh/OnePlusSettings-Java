package com.bumptech.glide.load.engine.bitmap_recycle;

public interface ArrayPool {
    void clearMemory();

    <T> T get(int i, Class<T> cls);

    <T> T getExact(int i, Class<T> cls);

    <T> void put(T t);

    void trimMemory(int i);
}
