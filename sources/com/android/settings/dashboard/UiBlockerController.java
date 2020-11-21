package com.android.settings.dashboard;

import android.util.Log;
import com.android.settingslib.utils.ThreadUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class UiBlockerController {
    private boolean mBlockerFinished;
    private CountDownLatch mCountDownLatch;
    private Set<String> mKeys;
    private long mTimeoutMillis;

    public UiBlockerController(List<String> list) {
        this(list, 500);
    }

    public UiBlockerController(List<String> list, long j) {
        this.mCountDownLatch = new CountDownLatch(list.size());
        this.mBlockerFinished = list.isEmpty();
        this.mKeys = new HashSet(list);
        this.mTimeoutMillis = j;
    }

    public boolean start(Runnable runnable) {
        if (this.mKeys.isEmpty()) {
            return false;
        }
        ThreadUtils.postOnBackgroundThread(new Runnable(runnable) {
            /* class com.android.settings.dashboard.$$Lambda$UiBlockerController$sgkOT2EMGELmDqohsdUCTZDV8U */
            public final /* synthetic */ Runnable f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                UiBlockerController.this.lambda$start$0$UiBlockerController(this.f$1);
            }
        });
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$start$0 */
    public /* synthetic */ void lambda$start$0$UiBlockerController(Runnable runnable) {
        try {
            this.mCountDownLatch.await(this.mTimeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException unused) {
            Log.w("UiBlockerController", "interrupted");
        }
        this.mBlockerFinished = true;
        ThreadUtils.postOnMainThread(runnable);
    }

    public boolean isBlockerFinished() {
        return this.mBlockerFinished;
    }

    public boolean countDown(String str) {
        if (!this.mKeys.remove(str)) {
            return false;
        }
        this.mCountDownLatch.countDown();
        return true;
    }
}
