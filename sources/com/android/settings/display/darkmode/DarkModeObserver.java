package com.android.settings.display.darkmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;

public class DarkModeObserver {
    private final BroadcastReceiver mBatterySaverReceiver = new BroadcastReceiver() {
        /* class com.android.settings.display.darkmode.DarkModeObserver.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            DarkModeObserver.this.mCallback.run();
        }
    };
    private Runnable mCallback;
    private ContentObserver mContentObserver;
    private Context mContext;

    public DarkModeObserver(Context context) {
        this.mContext = context;
        this.mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            /* class com.android.settings.display.darkmode.DarkModeObserver.AnonymousClass2 */

            public void onChange(boolean z, Uri uri) {
                String str;
                super.onChange(z, uri);
                if (uri == null) {
                    str = null;
                } else {
                    str = uri.getLastPathSegment();
                }
                if (str != null && DarkModeObserver.this.mCallback != null) {
                    DarkModeObserver.this.mCallback.run();
                }
            }
        };
    }

    public void subscribe(Runnable runnable) {
        runnable.run();
        this.mCallback = runnable;
        Uri uriFor = Settings.Secure.getUriFor("ui_night_mode");
        Uri uriFor2 = Settings.Secure.getUriFor("dark_theme_custom_start_time");
        Uri uriFor3 = Settings.Secure.getUriFor("dark_theme_custom_end_time");
        this.mContext.getContentResolver().registerContentObserver(uriFor, false, this.mContentObserver);
        this.mContext.getContentResolver().registerContentObserver(uriFor2, false, this.mContentObserver);
        this.mContext.getContentResolver().registerContentObserver(uriFor3, false, this.mContentObserver);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        this.mContext.registerReceiver(this.mBatterySaverReceiver, intentFilter);
    }

    public void unsubscribe() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
        try {
            this.mContext.unregisterReceiver(this.mBatterySaverReceiver);
        } catch (IllegalArgumentException e) {
            Log.w("DarkModeObserver", e.getMessage());
        }
        this.mCallback = null;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void setContentObserver(ContentObserver contentObserver) {
        this.mContentObserver = contentObserver;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public ContentObserver getContentObserver() {
        return this.mContentObserver;
    }
}
