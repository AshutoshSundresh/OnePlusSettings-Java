package com.android.settingslib.applications.instantapps;

import android.content.pm.ApplicationInfo;

public interface InstantAppDataProvider {
    boolean isInstantApp(ApplicationInfo applicationInfo);
}
