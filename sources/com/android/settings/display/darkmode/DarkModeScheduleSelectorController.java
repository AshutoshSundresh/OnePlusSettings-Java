package com.android.settings.display.darkmode;

import android.app.UiModeManager;
import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.PowerManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class DarkModeScheduleSelectorController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "DarkModeScheduleSelectorController";
    private int mCurrentMode;
    Preference mGlobalDarkPreference;
    private LocationManager mLocationManager;
    private PowerManager mPowerManager;
    private ListPreference mPreference;
    private final UiModeManager mUiModeManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public DarkModeScheduleSelectorController(Context context, String str) {
        super(context, str);
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mLocationManager = (LocationManager) context.getSystemService(LocationManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (ListPreference) preferenceScreen.findPreference(getPreferenceKey());
        Preference findPreference = preferenceScreen.findPreference("oneplus_global_dark_mode");
        this.mGlobalDarkPreference = findPreference;
        if (findPreference != null) {
            findPreference.setEnabled(needEnableGlobalDarkPreference());
        }
    }

    private boolean needEnableGlobalDarkPreference() {
        boolean z = (this.mContext.getResources().getConfiguration().uiMode & 32) != 0;
        int nightMode = this.mUiModeManager.getNightMode();
        return z || (nightMode == 0 || nightMode == 3);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public final void updateState(Preference preference) {
        this.mPreference.setEnabled(!this.mPowerManager.isPowerSaveMode());
        int currentMode = getCurrentMode();
        this.mCurrentMode = currentMode;
        this.mPreference.setValueIndex(currentMode);
        Preference preference2 = this.mGlobalDarkPreference;
        if (preference2 != null) {
            preference2.setEnabled(needEnableGlobalDarkPreference());
        }
    }

    private int getCurrentMode() {
        int i;
        int nightMode = this.mUiModeManager.getNightMode();
        if (nightMode == 0) {
            i = C0017R$string.oneplus_sunrise_sunset;
        } else if (nightMode != 3) {
            i = C0017R$string.oneplus_never_auto;
        } else {
            i = C0017R$string.oneplus_custom_time;
        }
        return this.mPreference.findIndexOfValue(this.mContext.getString(i));
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public final boolean onPreferenceChange(Preference preference, Object obj) {
        int findIndexOfValue = this.mPreference.findIndexOfValue((String) obj);
        if (findIndexOfValue == this.mCurrentMode) {
            return false;
        }
        boolean z = (this.mContext.getResources().getConfiguration().uiMode & 32) != 0;
        if (findIndexOfValue == this.mPreference.findIndexOfValue(this.mContext.getString(C0017R$string.oneplus_never_auto))) {
            this.mUiModeManager.setNightMode(z ? 2 : 1);
            this.mGlobalDarkPreference.setEnabled(z);
        } else if (findIndexOfValue == this.mPreference.findIndexOfValue(this.mContext.getString(C0017R$string.oneplus_sunrise_sunset))) {
            this.mUiModeManager.setNightMode(0);
            this.mGlobalDarkPreference.setEnabled(true);
        } else if (findIndexOfValue == this.mPreference.findIndexOfValue(this.mContext.getString(C0017R$string.oneplus_custom_time))) {
            this.mUiModeManager.setNightMode(3);
            this.mGlobalDarkPreference.setEnabled(true);
        }
        this.mCurrentMode = findIndexOfValue;
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void refreshSummary(Preference preference) {
        int i;
        int nightMode = this.mUiModeManager.getNightMode();
        if (nightMode == 0) {
            i = C0017R$string.oneplus_sunrise_sunset;
        } else if (nightMode != 3) {
            i = C0017R$string.oneplus_never_auto;
        } else {
            i = C0017R$string.oneplus_custom_time;
        }
        preference.setSummary(i);
    }
}
