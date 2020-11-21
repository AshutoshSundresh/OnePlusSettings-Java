package com.android.settings.wifi.tether;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.slices.SliceBackgroundWorker;

public abstract class WifiTetherBasePreferenceController extends BasePreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    protected final ConnectivityManager mCm;
    protected final OnTetherConfigUpdateListener mListener;
    protected Preference mPreference;
    protected final WifiManager mWifiManager;
    protected final String[] mWifiRegexs;

    public interface OnTetherConfigUpdateListener {
        void onTetherConfigUpdated(BasePreferenceController basePreferenceController);
    }

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

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public abstract /* synthetic */ boolean onPreferenceChange(Preference preference, Object obj);

    public abstract void updateDisplay();

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WifiTetherBasePreferenceController(Context context, OnTetherConfigUpdateListener onTetherConfigUpdateListener, String str) {
        super(context, str);
        this.mListener = onTetherConfigUpdateListener;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mCm = connectivityManager;
        this.mWifiRegexs = connectivityManager.getTetherableWifiRegexs();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        String[] strArr;
        return this.mWifiManager != null && (strArr = this.mWifiRegexs) != null && strArr.length > 0 ? 0 : 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        updateDisplay();
    }
}
