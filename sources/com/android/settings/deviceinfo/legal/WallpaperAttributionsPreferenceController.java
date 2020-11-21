package com.android.settings.deviceinfo.legal;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.custom.utils.OpCustomizeSettings;
import com.oneplus.settings.utils.OPUtils;

public class WallpaperAttributionsPreferenceController extends BasePreferenceController {
    private static final String ONEPLUS_A5000 = "ONEPLUS A5000";
    private static final String ONEPLUS_A5010 = "ONEPLUS A5010";
    private static final String ONEPLUS_A6000 = "ONEPLUS A6000";
    private static final String ONEPLUS_A6003 = "ONEPLUS A6003";
    private static final String ONEPLUS_WALLPAPER_ATTRIBUTIONS_STARWAR_VALUES = "© &amp;™ Lucasfilm Ltd.";
    private String KEY;
    private Context mContext;
    private Preference mPreference;

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

    public WallpaperAttributionsPreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
        this.KEY = str;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_wallpaper_attribution) ? 0 : 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        if (Build.MODEL.equalsIgnoreCase(ONEPLUS_A5000)) {
            this.mPreference.setSummary(C0017R$string.oneplus_wallpaper_attributions_2017_values);
        } else if (Build.MODEL.equalsIgnoreCase(ONEPLUS_A5010)) {
            this.mPreference.setSummary(C0017R$string.oneplus_wallpaper_attributions_a5010_values);
        } else if (Build.MODEL.equalsIgnoreCase(ONEPLUS_A6000) || Build.MODEL.equalsIgnoreCase(ONEPLUS_A6003)) {
            this.mPreference.setSummary(C0017R$string.oneplus_wallpaper_attributions_17819_values);
        } else if (Build.MODEL.equalsIgnoreCase(this.mContext.getString(C0017R$string.oneplus_model_for_china_and_india)) || Build.MODEL.equalsIgnoreCase(this.mContext.getString(C0017R$string.oneplus_model_for_europe_and_america))) {
            this.mPreference.setSummary(C0017R$string.oneplus_wallpaper_attributions_17819_values);
        } else if (OPUtils.isSM8150Products()) {
            this.mPreference.setSummary(C0017R$string.oneplus_wallpaper_attributions_8150_values);
        } else if (OPUtils.isSM8250Products()) {
            this.mPreference.setSummary(C0017R$string.oneplus_wallpaper_attributions_8250_R_values);
        }
        if (OpCustomizeSettings.CUSTOM_TYPE.SW.equals(OpCustomizeSettings.getCustomType())) {
            this.mPreference.setSummary(ONEPLUS_WALLPAPER_ATTRIBUTIONS_STARWAR_VALUES);
        } else if (OpCustomizeSettings.CUSTOM_TYPE.AVG.equals(OpCustomizeSettings.getCustomType())) {
            this.mPreference.setSummary(C0017R$string.oneplus_wallpaper_attributions_avg_values);
        }
    }
}
