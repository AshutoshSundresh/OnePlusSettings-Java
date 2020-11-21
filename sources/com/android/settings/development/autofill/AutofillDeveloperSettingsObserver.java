package com.android.settings.development.autofill;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

/* access modifiers changed from: package-private */
public final class AutofillDeveloperSettingsObserver extends ContentObserver {
    private final Runnable mChangeCallback;
    private final ContentResolver mResolver;

    public AutofillDeveloperSettingsObserver(Context context, Runnable runnable) {
        super(new Handler(Looper.getMainLooper()));
        this.mResolver = context.getContentResolver();
        this.mChangeCallback = runnable;
    }

    public void register() {
        this.mResolver.registerContentObserver(Settings.Global.getUriFor("autofill_logging_level"), false, this, -1);
        this.mResolver.registerContentObserver(Settings.Global.getUriFor("autofill_max_partitions_size"), false, this, -1);
        this.mResolver.registerContentObserver(Settings.Global.getUriFor("autofill_max_visible_datasets"), false, this, -1);
    }

    public void unregister() {
        this.mResolver.unregisterContentObserver(this);
    }

    public void onChange(boolean z, Uri uri, int i) {
        this.mChangeCallback.run();
    }
}
