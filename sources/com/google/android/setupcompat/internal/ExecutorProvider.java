package com.google.android.setupcompat.internal;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ExecutorProvider<T extends Executor> {
    public static final ExecutorProvider<ExecutorService> setupCompatExecutor = new ExecutorProvider<>(createSizeBoundedExecutor("SetupBindbackServiceExecutor", 1));
    public static final ExecutorProvider<ExecutorService> setupCompatServiceInvoker = new ExecutorProvider<>(createSizeBoundedExecutor("SetupCompatServiceInvoker", 50));
    private final T executor;
    private T injectedExecutor;

    private ExecutorProvider(T t) {
        this.executor = t;
    }

    public T get() {
        T t = this.injectedExecutor;
        if (t != null) {
            return t;
        }
        return this.executor;
    }

    public void injectExecutor(T t) {
        this.injectedExecutor = t;
    }

    public static void resetExecutors() {
        ((ExecutorProvider) setupCompatServiceInvoker).injectedExecutor = null;
    }

    public static ExecutorService createSizeBoundedExecutor(String str, int i) {
        return new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue(i), new ThreadFactory(str) {
            /* class com.google.android.setupcompat.internal.$$Lambda$ExecutorProvider$iopywGRm1wiyn8apbtPv_b9qThw */
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            public final Thread newThread(Runnable runnable) {
                return ExecutorProvider.lambda$createSizeBoundedExecutor$0(this.f$0, runnable);
            }
        });
    }

    static /* synthetic */ Thread lambda$createSizeBoundedExecutor$0(String str, Runnable runnable) {
        return new Thread(runnable, str);
    }
}
