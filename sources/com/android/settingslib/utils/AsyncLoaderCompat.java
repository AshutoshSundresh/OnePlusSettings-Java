package com.android.settingslib.utils;

import android.content.Context;
import androidx.loader.content.AsyncTaskLoader;

public abstract class AsyncLoaderCompat<T> extends AsyncTaskLoader<T> {
    private T mResult;

    /* access modifiers changed from: protected */
    public abstract void onDiscardResult(T t);

    public AsyncLoaderCompat(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onStartLoading() {
        T t = this.mResult;
        if (t != null) {
            deliverResult(t);
        }
        if (takeContentChanged() || this.mResult == null) {
            forceLoad();
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onStopLoading() {
        cancelLoad();
    }

    @Override // androidx.loader.content.Loader
    public void deliverResult(T t) {
        if (!isReset()) {
            T t2 = this.mResult;
            this.mResult = t;
            if (isStarted()) {
                super.deliverResult(t);
            }
            if (t2 != null && t2 != this.mResult) {
                onDiscardResult(t2);
            }
        } else if (t != null) {
            onDiscardResult(t);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.loader.content.Loader
    public void onReset() {
        super.onReset();
        onStopLoading();
        T t = this.mResult;
        if (t != null) {
            onDiscardResult(t);
        }
        this.mResult = null;
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public void onCanceled(T t) {
        super.onCanceled(t);
        if (t != null) {
            onDiscardResult(t);
        }
    }
}
