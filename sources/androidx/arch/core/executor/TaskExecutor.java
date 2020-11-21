package androidx.arch.core.executor;

public abstract class TaskExecutor {
    public abstract boolean isMainThread();

    public abstract void postToMainThread(Runnable runnable);
}
