package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.List;

/* access modifiers changed from: package-private */
public abstract class SettingsContentObserver extends ContentObserver {
    private final List<String> mKeysToObserve;

    public SettingsContentObserver(Handler handler) {
        super(handler);
        ArrayList arrayList = new ArrayList(2);
        this.mKeysToObserve = arrayList;
        arrayList.add("accessibility_enabled");
        this.mKeysToObserve.add("enabled_accessibility_services");
    }

    public SettingsContentObserver(Handler handler, List<String> list) {
        this(handler);
        this.mKeysToObserve.addAll(list);
    }

    public void register(ContentResolver contentResolver) {
        for (int i = 0; i < this.mKeysToObserve.size(); i++) {
            contentResolver.registerContentObserver(Settings.Secure.getUriFor(this.mKeysToObserve.get(i)), false, this);
        }
    }

    public void unregister(ContentResolver contentResolver) {
        contentResolver.unregisterContentObserver(this);
    }
}
