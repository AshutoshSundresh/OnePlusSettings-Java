package com.android.settings.network.ims;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

class IntegerConsumer extends Semaphore implements Consumer<Integer> {
    private volatile AtomicInteger mValue = new AtomicInteger();

    IntegerConsumer() {
        super(0);
    }

    /* access modifiers changed from: package-private */
    public int get(long j) throws InterruptedException {
        tryAcquire(j, TimeUnit.MILLISECONDS);
        return this.mValue.get();
    }

    public void accept(Integer num) {
        if (num != null) {
            this.mValue.set(num.intValue());
        }
        release();
    }
}
