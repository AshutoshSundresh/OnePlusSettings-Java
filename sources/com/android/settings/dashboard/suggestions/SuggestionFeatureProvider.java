package com.android.settings.dashboard.suggestions;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;

public interface SuggestionFeatureProvider {
    SharedPreferences getSharedPrefs(Context context);

    ComponentName getSuggestionServiceComponent();

    boolean isSuggestionComplete(Context context, ComponentName componentName);
}
