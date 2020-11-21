package com.oneplus.loading;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

public abstract class LoadingHelper {
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private long mProgreeMinShowTime = 500;
    private Object mProgreeView;
    private Runnable mShowProgreeRunnable;
    private long mShowProgreeTime;
    private long mWillShowProgreeTime = 300;

    public interface FinishShowCallback {
        void finish(boolean z);
    }

    /* access modifiers changed from: protected */
    public abstract void hideProgree(Object obj);

    /* access modifiers changed from: protected */
    public abstract Object showProgree();

    public void beginShowProgress() {
        AnonymousClass1 r0 = new Runnable() {
            /* class com.oneplus.loading.LoadingHelper.AnonymousClass1 */

            public void run() {
                LoadingHelper.this.mShowProgreeRunnable = null;
                LoadingHelper loadingHelper = LoadingHelper.this;
                loadingHelper.mProgreeView = loadingHelper.showProgree();
                LoadingHelper.this.mShowProgreeTime = SystemClock.elapsedRealtime();
            }
        };
        this.mShowProgreeRunnable = r0;
        mHandler.postDelayed(r0, this.mWillShowProgreeTime);
    }

    public void finishShowProgress(final FinishShowCallback finishShowCallback) {
        Runnable runnable = this.mShowProgreeRunnable;
        if (runnable != null) {
            mHandler.removeCallbacks(runnable);
            doFinish(finishShowCallback, false);
            return;
        }
        long elapsedRealtime = this.mProgreeMinShowTime - (SystemClock.elapsedRealtime() - this.mShowProgreeTime);
        if (elapsedRealtime > 0) {
            mHandler.postDelayed(new Runnable() {
                /* class com.oneplus.loading.LoadingHelper.AnonymousClass2 */

                public void run() {
                    LoadingHelper.this.doFinish(finishShowCallback, true);
                }
            }, elapsedRealtime);
        } else {
            doFinish(finishShowCallback, true);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void doFinish(final FinishShowCallback finishShowCallback, final boolean z) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (z) {
                hideProgree(this.mProgreeView);
            }
            if (finishShowCallback != null) {
                finishShowCallback.finish(true);
                return;
            }
            return;
        }
        mHandler.post(new Runnable() {
            /* class com.oneplus.loading.LoadingHelper.AnonymousClass3 */

            public void run() {
                if (z) {
                    LoadingHelper loadingHelper = LoadingHelper.this;
                    loadingHelper.hideProgree(loadingHelper.mProgreeView);
                }
                FinishShowCallback finishShowCallback = finishShowCallback;
                if (finishShowCallback != null) {
                    finishShowCallback.finish(true);
                }
            }
        });
    }
}
