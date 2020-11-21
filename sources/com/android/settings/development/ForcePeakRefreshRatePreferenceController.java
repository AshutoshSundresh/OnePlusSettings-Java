package com.android.settings.development;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0005R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class ForcePeakRefreshRatePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static float DEFAULT_REFRESH_RATE = 60.0f;
    static float NO_CONFIG;
    float mPeakRefreshRate;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "pref_key_peak_refresh_rate";
    }

    public ForcePeakRefreshRatePreferenceController(Context context) {
        super(context);
        Display display = ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplay(0);
        if (display == null) {
            Log.w("ForcePeakRefreshRateCtr", "No valid default display device");
            this.mPeakRefreshRate = DEFAULT_REFRESH_RATE;
        } else {
            this.mPeakRefreshRate = findPeakRefreshRate(display.getSupportedModes());
        }
        Log.d("ForcePeakRefreshRateCtr", "DEFAULT_REFRESH_RATE : " + DEFAULT_REFRESH_RATE + " mPeakRefreshRate : " + this.mPeakRefreshRate);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        forcePeakRefreshRate(((Boolean) obj).booleanValue());
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(isForcePeakRefreshRateEnabled());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settingslib.development.DeveloperOptionsPreferenceController
    public boolean isAvailable() {
        if (!this.mContext.getResources().getBoolean(C0005R$bool.config_show_smooth_display) || this.mPeakRefreshRate <= DEFAULT_REFRESH_RATE) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.System.putFloat(this.mContext.getContentResolver(), "min_refresh_rate", NO_CONFIG);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    /* access modifiers changed from: package-private */
    public void forcePeakRefreshRate(boolean z) {
        Settings.System.putFloat(this.mContext.getContentResolver(), "min_refresh_rate", z ? this.mPeakRefreshRate : NO_CONFIG);
    }

    /* access modifiers changed from: package-private */
    public boolean isForcePeakRefreshRateEnabled() {
        return Settings.System.getFloat(this.mContext.getContentResolver(), "min_refresh_rate", NO_CONFIG) >= this.mPeakRefreshRate;
    }

    private float findPeakRefreshRate(Display.Mode[] modeArr) {
        float f = DEFAULT_REFRESH_RATE;
        for (Display.Mode mode : modeArr) {
            if (((float) Math.round(mode.getRefreshRate())) > DEFAULT_REFRESH_RATE) {
                f = mode.getRefreshRate();
            }
        }
        return f;
    }
}
