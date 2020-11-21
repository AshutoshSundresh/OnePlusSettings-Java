package com.android.settings.display.darkmode;

import android.app.ActivityManager;
import android.app.UiModeManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0003R$array;
import com.android.settings.C0006R$color;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPUtils;

public class OPDarkModeEnabledPreferenceController extends TogglePreferenceController {
    Preference mGlobalDarkPreference;
    private PowerManager mPowerManager;
    SwitchPreference mPreference;
    private final UiModeManager mUiModeManager;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OPDarkModeEnabledPreferenceController(Context context, String str) {
        super(context, str);
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return (this.mContext.getResources().getConfiguration().uiMode & 32) != 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        Preference findPreference = preferenceScreen.findPreference("oneplus_global_dark_mode");
        this.mGlobalDarkPreference = findPreference;
        if (findPreference != null) {
            findPreference.setEnabled(isChecked() || isAutoModeEnabled());
        }
    }

    private boolean isAutoModeEnabled() {
        int nightMode = this.mUiModeManager.getNightMode();
        return nightMode == 0 || nightMode == 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        Preference preference = this.mGlobalDarkPreference;
        if (preference != null) {
            preference.setEnabled(z || isAutoModeEnabled());
        }
        enableDarkMode(z);
        return true;
    }

    public void enableDarkMode(boolean z) {
        boolean z2 = (this.mContext.getResources().getConfiguration().uiMode & 32) != 0;
        String str = z ? "1" : "0";
        Settings.System.putInt(this.mContext.getContentResolver(), "oem_black_mode", z ? 1 : 0);
        SystemProperties.set("persist.sys.theme.status", str);
        this.mUiModeManager.setNightModeActivated(!z2);
    }

    private String getCurrentDarkAccentColors(String str) {
        Resources resources = this.mContext.getResources();
        TypedArray obtainTypedArray = resources.obtainTypedArray(C0003R$array.op_custom_accent_text_color_values_dark);
        int length = obtainTypedArray.length();
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = resources.getString(obtainTypedArray.getResourceId(i, -1));
        }
        obtainTypedArray.recycle();
        int i2 = Settings.System.getInt(this.mContext.getContentResolver(), "oem_black_mode_accent_color_index", 0);
        if (i2 < 0 || i2 > 11) {
            return OPUtils.getTextAccentColor(str);
        }
        return strArr[i2];
    }

    public void syncAccentColor(boolean z) {
        String str;
        if (z) {
            String string = Settings.System.getString(this.mContext.getContentResolver(), "oem_black_mode_accent_color");
            Settings.System.putStringForUser(this.mContext.getContentResolver(), "oneplus_accent_color", string, ActivityManager.getCurrentUser());
            String currentDarkAccentColors = getCurrentDarkAccentColors(string);
            Settings.System.putStringForUser(this.mContext.getContentResolver(), "oneplus_accent_text_color", currentDarkAccentColors, ActivityManager.getCurrentUser());
            if (!TextUtils.isEmpty(currentDarkAccentColors)) {
                currentDarkAccentColors = currentDarkAccentColors.replace("#", "");
            }
            SystemProperties.set("persist.sys.theme.accent_text_color", currentDarkAccentColors);
            SystemProperties.set("persist.sys.theme.accentcolor", !TextUtils.isEmpty(string) ? string.replace("#", "") : string);
            int i = Settings.System.getInt(this.mContext.getContentResolver(), "oem_black_mode_accent_color_index", 0);
            if (i == 0) {
                string = "#D8FFFFFF";
            }
            Settings.System.putStringForUser(this.mContext.getContentResolver(), "oneplus_sub_accent_color", string, ActivityManager.getCurrentUser());
            if (!TextUtils.isEmpty(string)) {
                string = string.replace("#", "");
            }
            SystemProperties.set("persist.sys.theme.sub_accentcolor", string);
            this.mContext.getResources().getString(C0006R$color.op_control_text_color_primary_dark);
            if (i == 0) {
                str = this.mContext.getResources().getString(C0006R$color.op_control_text_color_primary_light);
            } else {
                str = this.mContext.getResources().getString(C0006R$color.op_control_text_color_primary_dark);
            }
            Settings.System.putStringForUser(this.mContext.getContentResolver(), "oneplus_accent_button_text_color", str, ActivityManager.getCurrentUser());
            if (!TextUtils.isEmpty(str)) {
                str = str.replace("#", "");
            }
            SystemProperties.set("persist.sys.theme.oneplus_accent_button_text_color", str);
            return;
        }
        String string2 = Settings.System.getString(this.mContext.getContentResolver(), "oem_white_mode_accent_color");
        Settings.System.putStringForUser(this.mContext.getContentResolver(), "oneplus_accent_color", string2, ActivityManager.getCurrentUser());
        Settings.System.putStringForUser(this.mContext.getContentResolver(), "oneplus_accent_text_color", string2, ActivityManager.getCurrentUser());
        String replace = !TextUtils.isEmpty(string2) ? string2.replace("#", "") : string2;
        SystemProperties.set("persist.sys.theme.accentcolor", replace);
        SystemProperties.set("persist.sys.theme.accent_text_color", replace);
        if (Settings.System.getInt(this.mContext.getContentResolver(), "oem_white_mode_accent_color_index", 0) == 0) {
            string2 = "#D8000000";
        }
        Settings.System.putStringForUser(this.mContext.getContentResolver(), "oneplus_sub_accent_color", string2, ActivityManager.getCurrentUser());
        if (!TextUtils.isEmpty(string2)) {
            string2 = string2.replace("#", "");
        }
        SystemProperties.set("persist.sys.theme.sub_accentcolor", string2);
        String string3 = this.mContext.getResources().getString(C0006R$color.op_control_text_color_primary_dark);
        Settings.System.putStringForUser(this.mContext.getContentResolver(), "oneplus_accent_button_text_color", string3, ActivityManager.getCurrentUser());
        if (!TextUtils.isEmpty(string3)) {
            string3 = string3.replace("#", "");
        }
        SystemProperties.set("persist.sys.theme.oneplus_accent_button_text_color", string3);
    }
}
