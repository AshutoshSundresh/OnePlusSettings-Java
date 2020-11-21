package com.android.settings.wallpaper;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;

public class StyleSuggestionActivity extends StyleSuggestionActivityBase {
    @VisibleForTesting
    public static boolean isSuggestionComplete(Context context) {
        if (StyleSuggestionActivityBase.isWallpaperServiceEnabled(context) && TextUtils.isEmpty(Settings.Secure.getStringForUser(context.getContentResolver(), "theme_customization_overlay_packages", context.getUserId()))) {
            return false;
        }
        return true;
    }
}
