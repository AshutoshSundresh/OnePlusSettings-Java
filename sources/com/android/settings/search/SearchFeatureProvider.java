package com.android.settings.search;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.android.settings.C0017R$string;
import com.android.settingslib.search.SearchIndexableResources;

public interface SearchFeatureProvider {
    Intent buildSearchIntent(Context context, int i);

    SearchIndexableResources getSearchIndexableResources();

    void verifyLaunchSearchResultPageCaller(Context context, ComponentName componentName) throws SecurityException, IllegalArgumentException;

    default String getSettingsIntelligencePkgName(Context context) {
        return context.getString(C0017R$string.config_settingsintelligence_package_name);
    }
}
