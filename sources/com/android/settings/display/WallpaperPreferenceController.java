package com.android.settings.display;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import java.util.List;

public class WallpaperPreferenceController extends BasePreferenceController {
    private static final String TAG = "WallpaperPrefController";
    private final String mStylesAndWallpaperClass = this.mContext.getString(C0017R$string.config_styles_and_wallpaper_picker_class);
    private final String mWallpaperClass = this.mContext.getString(C0017R$string.config_wallpaper_picker_class);
    private final String mWallpaperPackage = this.mContext.getString(C0017R$string.config_wallpaper_picker_package);

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WallpaperPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        preferenceScreen.findPreference(getPreferenceKey()).setTitle(getTitle());
    }

    public String getTitle() {
        return this.mContext.getString(areStylesAvailable() ? C0017R$string.style_and_wallpaper_settings_title : C0017R$string.wallpaper_settings_title);
    }

    public ComponentName getComponentName() {
        return new ComponentName(this.mWallpaperPackage, areStylesAvailable() ? this.mStylesAndWallpaperClass : this.mWallpaperClass);
    }

    public String getKeywords() {
        StringBuilder sb = new StringBuilder(this.mContext.getString(C0017R$string.keywords_wallpaper));
        if (areStylesAvailable()) {
            sb.append(", ");
            sb.append(this.mContext.getString(C0017R$string.keywords_styles));
        }
        return sb.toString();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!TextUtils.isEmpty(this.mWallpaperPackage) && !TextUtils.isEmpty(this.mWallpaperClass)) {
            return canResolveWallpaperComponent(this.mWallpaperClass) ? 1 : 2;
        }
        Log.e(TAG, "No Wallpaper picker specified!");
        return 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        disablePreferenceIfManaged((RestrictedPreference) preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!getPreferenceKey().equals(preference.getKey())) {
            return super.handlePreferenceTreeClick(preference);
        }
        preference.getContext().startActivity(new Intent().setComponent(getComponentName()));
        return true;
    }

    public boolean areStylesAvailable() {
        return !TextUtils.isEmpty(this.mStylesAndWallpaperClass) && canResolveWallpaperComponent(this.mStylesAndWallpaperClass);
    }

    private boolean canResolveWallpaperComponent(String str) {
        List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(new Intent().setComponent(new ComponentName(this.mWallpaperPackage, str)), 0);
        if (queryIntentActivities == null || queryIntentActivities.isEmpty()) {
            return false;
        }
        return true;
    }

    private void disablePreferenceIfManaged(RestrictedPreference restrictedPreference) {
        if (restrictedPreference != null) {
            restrictedPreference.setDisabledByAdmin(null);
            if (RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_set_wallpaper", UserHandle.myUserId())) {
                restrictedPreference.setEnabled(false);
            } else {
                restrictedPreference.checkRestrictionAndSetDisabled("no_set_wallpaper");
            }
        }
    }
}
