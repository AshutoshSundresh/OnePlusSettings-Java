package com.android.settings.development.graphicsdriver;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;

public class GraphicsDriverContentObserver extends ContentObserver {
    OnGraphicsDriverContentChangedListener mListener;

    interface OnGraphicsDriverContentChangedListener {
        void onGraphicsDriverContentChanged();
    }

    public GraphicsDriverContentObserver(Handler handler, OnGraphicsDriverContentChangedListener onGraphicsDriverContentChangedListener) {
        super(handler);
        this.mListener = onGraphicsDriverContentChangedListener;
    }

    public void onChange(boolean z) {
        super.onChange(z);
        this.mListener.onGraphicsDriverContentChanged();
    }

    public void register(ContentResolver contentResolver) {
        contentResolver.registerContentObserver(Settings.Global.getUriFor("game_driver_all_apps"), false, this);
    }

    public void unregister(ContentResolver contentResolver) {
        contentResolver.unregisterContentObserver(this);
    }
}
