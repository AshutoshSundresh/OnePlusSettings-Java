package com.android.settings.dashboard.suggestions;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import com.android.settings.Settings;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollSuggestionActivity;
import com.android.settings.biometrics.fingerprint.FingerprintSuggestionActivity;
import com.android.settings.display.NightDisplayPreferenceController;
import com.android.settings.notification.zen.ZenOnboardingActivity;
import com.android.settings.notification.zen.ZenSuggestionActivity;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.password.ScreenLockSuggestionActivity;
import com.android.settings.wallpaper.StyleSuggestionActivity;
import com.android.settings.wallpaper.WallpaperSuggestionActivity;
import com.android.settings.wifi.calling.WifiCallingSuggestionActivity;

public class SuggestionFeatureProviderImpl implements SuggestionFeatureProvider {
    @Override // com.android.settings.dashboard.suggestions.SuggestionFeatureProvider
    public ComponentName getSuggestionServiceComponent() {
        return new ComponentName("com.android.settings.intelligence", "com.android.settings.intelligence.suggestions.SuggestionService");
    }

    @Override // com.android.settings.dashboard.suggestions.SuggestionFeatureProvider
    public boolean isSuggestionComplete(Context context, ComponentName componentName) {
        String className = componentName.getClassName();
        if (className.equals(WallpaperSuggestionActivity.class.getName())) {
            return WallpaperSuggestionActivity.isSuggestionComplete(context);
        }
        if (className.equals(StyleSuggestionActivity.class.getName())) {
            return StyleSuggestionActivity.isSuggestionComplete(context);
        }
        if (className.equals(FingerprintSuggestionActivity.class.getName())) {
            return FingerprintSuggestionActivity.isSuggestionComplete(context);
        }
        if (className.equals(FingerprintEnrollSuggestionActivity.class.getName())) {
            return FingerprintEnrollSuggestionActivity.isSuggestionComplete(context);
        }
        if (className.equals(ScreenLockSuggestionActivity.class.getName())) {
            return ScreenLockSuggestionActivity.isSuggestionComplete(context);
        }
        if (className.equals(WifiCallingSuggestionActivity.class.getName())) {
            return WifiCallingSuggestionActivity.isSuggestionComplete(context);
        }
        if (className.equals(Settings.NightDisplaySuggestionActivity.class.getName())) {
            return NightDisplayPreferenceController.isSuggestionComplete(context);
        }
        if (className.equals(ZenSuggestionActivity.class.getName())) {
            return ZenOnboardingActivity.isSuggestionComplete(context);
        }
        return false;
    }

    @Override // com.android.settings.dashboard.suggestions.SuggestionFeatureProvider
    public SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences("suggestions", 0);
    }

    public SuggestionFeatureProviderImpl(Context context) {
        FeatureFactory.getFactory(context.getApplicationContext()).getMetricsFeatureProvider();
    }
}
