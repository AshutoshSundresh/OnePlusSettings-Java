package androidx.constraintlayout.solver;

/* access modifiers changed from: package-private */
public class Pools$SimplePool<T> implements Pools$Pool<T> {
    private final Object[] mPool;
    private int mPoolSize;

    Pools$SimplePool(int i) {
        if (i > 0) {
            this.mPool = new Object[i];
            return;
        }
        throw new IllegalArgumentException("The max pool size must be > 0");
    }

    @Override // androidx.constraintlayout.solver.Pools$Pool
    public T acquire() {
        int i = this.mPoolSize;
        if (i <= 0) {
            return null;
        }
        int i2 = i - 1;
        Object[] objArr = this.mPool;
        T t = (T) objArr[i2];
        objArr[i2] = null;
        this.mPoolSize = i - 1;
        return t;
    }

    @Override // androidx.constraintlayout.solver.Pools$Pool
    public boolean release(T t) {
        int i = this.mPoolSize;
        Object[] objArr = this.mPool;
        if (i >= objArr.length) {
            return false;
        }
        objArr[i] = t;
        this.mPoolSize = i + 1;
        return true;
    }

    @Override // androidx.constraintlayout.solver.Pools$Pool
    public void releaseAll(T[] tArr, int i) {
        if (i > tArr.length) {
            i = tArr.length;
        }
        for (int i2 = 0; i2 < i; i2++) {
            T t = tArr[i2];
            int i3 = this.mPoolSize;
            Object[] objArr = this.mPool;
            if (i3 < objArr.length) {
                objArr[i3] = t;
                this.mPoolSize = i3 + 1;
            }
        }
    }
}