package com.bumptech.glide.util.pool;

import android.util.Log;
import androidx.core.util.Pools$Pool;
import androidx.core.util.Pools$SynchronizedPool;
import java.util.ArrayList;
import java.util.List;

public final class FactoryPools {
    private static final Resetter<Object> EMPTY_RESETTER = new Resetter<Object>() {
        /* class com.bumptech.glide.util.pool.FactoryPools.AnonymousClass1 */

        @Override // com.bumptech.glide.util.pool.FactoryPools.Resetter
        public void reset(Object obj) {
        }
    };

    public interface Factory<T> {
        T create();
    }

    public interface Poolable {
        StateVerifier getVerifier();
    }

    public interface Resetter<T> {
        void reset(T t);
    }

    public static <T extends Poolable> Pools$Pool<T> threadSafe(int i, Factory<T> factory) {
        return build(new Pools$SynchronizedPool(i), factory);
    }

    public static <T> Pools$Pool<List<T>> threadSafeList() {
        return threadSafeList(20);
    }

    public static <T> Pools$Pool<List<T>> threadSafeList(int i) {
        return build(new Pools$SynchronizedPool(i), new Factory<List<T>>() {
            /* class com.bumptech.glide.util.pool.FactoryPools.AnonymousClass2 */

            @Override // com.bumptech.glide.util.pool.FactoryPools.Factory
            public List<T> create() {
                return new ArrayList();
            }
        }, new Resetter<List<T>>() {
            /* class com.bumptech.glide.util.pool.FactoryPools.AnonymousClass3 */

            public void reset(List<T> list) {
                list.clear();
            }
        });
    }

    private static <T extends Poolable> Pools$Pool<T> build(Pools$Pool<T> pools$Pool, Factory<T> factory) {
        return build(pools$Pool, factory, emptyResetter());
    }

    private static <T> Pools$Pool<T> build(Pools$Pool<T> pools$Pool, Factory<T> factory, Resetter<T> resetter) {
        return new FactoryPool(pools$Pool, factory, resetter);
    }

    private static <T> Resetter<T> emptyResetter() {
        return (Resetter<T>) EMPTY_RESETTER;
    }

    /* access modifiers changed from: private */
    public static final class FactoryPool<T> implements Pools$Pool<T> {
        private final Factory<T> factory;
        private final Pools$Pool<T> pool;
        private final Resetter<T> resetter;

        FactoryPool(Pools$Pool<T> pools$Pool, Factory<T> factory2, Resetter<T> resetter2) {
            this.pool = pools$Pool;
            this.factory = factory2;
            this.resetter = resetter2;
        }

        @Override // androidx.core.util.Pools$Pool
        public T acquire() {
            T acquire = this.pool.acquire();
            if (acquire == null) {
                acquire = this.factory.create();
                if (Log.isLoggable("FactoryPools", 2)) {
                    Log.v("FactoryPools", "Created new " + acquire.getClass());
                }
            }
            if (acquire instanceof Poolable) {
                acquire.getVerifier().setRecycled(false);
            }
            return acquire;
        }

        @Override // androidx.core.util.Pools$Pool
        public boolean release(T t) {
            if (t instanceof Poolable) {
                t.getVerifier().setRecycled(true);
            }
            this.resetter.reset(t);
            return this.pool.release(t);
        }
    }
}
