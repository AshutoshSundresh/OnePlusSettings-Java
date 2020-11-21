package androidx.constraintlayout.solver;

/* access modifiers changed from: package-private */
public interface Pools$Pool<T> {
    T acquire();

    boolean release(T t);

    void releaseAll(T[] tArr, int i);
}
