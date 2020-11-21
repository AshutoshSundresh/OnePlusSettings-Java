package com.android.settings.dashboard;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

public abstract class DynamicDataObserver extends ContentObserver {
    public abstract Uri getUri();

    public abstract void onDataChanged();

    protected DynamicDataObserver() {
        super(new Handler(Looper.getMainLooper()));
    }

    public void onChange(boolean z) {
        onDataChanged();
    }
}
