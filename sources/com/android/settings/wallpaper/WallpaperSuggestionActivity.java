package com.android.settings.wallpaper;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.SearchIndexableData;
import com.android.settings.display.WallpaperPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import com.google.android.setupcompat.util.WizardManagerHelper;
import java.util.ArrayList;
import java.util.List;

public class WallpaperSuggestionActivity extends StyleSuggestionActivityBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.wallpaper.WallpaperSuggestionActivity.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            WallpaperPreferenceController wallpaperPreferenceController = new WallpaperPreferenceController(context, "dummy key");
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
            String title = wallpaperPreferenceController.getTitle();
            searchIndexableRaw.title = title;
            searchIndexableRaw.screenTitle = title;
            ComponentName componentName = wallpaperPreferenceController.getComponentName();
            ((SearchIndexableData) searchIndexableRaw).intentTargetPackage = componentName.getPackageName();
            ((SearchIndexableData) searchIndexableRaw).intentTargetClass = componentName.getClassName();
            ((SearchIndexableData) searchIndexableRaw).intentAction = "android.intent.action.MAIN";
            ((SearchIndexableData) searchIndexableRaw).key = "wallpaper_type";
            searchIndexableRaw.keywords = wallpaperPreferenceController.getKeywords();
            arrayList.add(searchIndexableRaw);
            return arrayList;
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.wallpaper.StyleSuggestionActivityBase
    public void addExtras(Intent intent) {
        if (WizardManagerHelper.isAnySetupWizard(intent)) {
            intent.putExtra("com.android.launcher3.WALLPAPER_FLAVOR", "wallpaper_only");
        } else {
            intent.putExtra("com.android.launcher3.WALLPAPER_FLAVOR", "focus_wallpaper");
        }
    }

    public static boolean isSuggestionComplete(Context context) {
        if (StyleSuggestionActivityBase.isWallpaperServiceEnabled(context) && ((WallpaperManager) context.getSystemService("wallpaper")).getWallpaperId(1) <= 0) {
            return false;
        }
        return true;
    }
}
