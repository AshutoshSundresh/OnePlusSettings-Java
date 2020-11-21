package com.android.settings.wifi.details2;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.WifiDialog2;
import com.android.wifitrackerlib.WifiEntry;

public class WifiPrivacyPreferenceController2 extends BasePreferenceController implements Preference.OnPreferenceChangeListener, WifiDialog2.WifiDialog2Listener {
    private static final String KEY_WIFI_PRIVACY = "privacy";
    private static final int PREF_RANDOMIZATION_NONE = 1;
    private static final int PREF_RANDOMIZATION_PERSISTENT = 0;
    private Preference mPreference;
    private WifiEntry mWifiEntry;
    private WifiManager mWifiManager;

    public static int translateMacRandomizedValueToPrefValue(int i) {
        return i == 1 ? 0 : 1;
    }

    public static int translatePrefValueToMacRandomizedValue(int i) {
        return i == 0 ? 1 : 0;
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

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public /* bridge */ /* synthetic */ void onForget(WifiDialog2 wifiDialog2) {
        super.onForget(wifiDialog2);
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public /* bridge */ /* synthetic */ void onScan(WifiDialog2 wifiDialog2, String str) {
        super.onScan(wifiDialog2, str);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WifiPrivacyPreferenceController2(Context context) {
        super(context, KEY_WIFI_PRIVACY);
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public void setWifiEntry(WifiEntry wifiEntry) {
        this.mWifiEntry = wifiEntry;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mWifiManager.isConnectedMacRandomizationSupported() ? 0 : 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        DropDownPreference dropDownPreference = (DropDownPreference) preference;
        int randomizationValue = getRandomizationValue();
        dropDownPreference.setValue(Integer.toString(randomizationValue));
        updateSummary(dropDownPreference, randomizationValue);
        if (!this.mWifiEntry.canSetPrivacy()) {
            preference.setSelectable(false);
            dropDownPreference.setSummary(C0017R$string.wifi_privacy_settings_ephemeral_summary);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        this.mWifiEntry.setPrivacy(parseInt);
        if (this.mWifiEntry.getConnectedState() == 2) {
            this.mWifiEntry.disconnect(null);
            this.mWifiEntry.connect(null);
        }
        updateSummary((DropDownPreference) preference, parseInt);
        return true;
    }

    /* access modifiers changed from: package-private */
    public int getRandomizationValue() {
        return this.mWifiEntry.getPrivacy();
    }

    private void updateSummary(DropDownPreference dropDownPreference, int i) {
        dropDownPreference.setSummary(dropDownPreference.getEntries()[translateMacRandomizedValueToPrefValue(i)]);
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onSubmit(WifiDialog2 wifiDialog2) {
        WifiConfiguration config;
        if (wifiDialog2.getController() != null && (config = wifiDialog2.getController().getConfig()) != null && getWifiEntryPrivacy(config) != this.mWifiEntry.getPrivacy()) {
            this.mWifiEntry.setPrivacy(getWifiEntryPrivacy(config));
            onPreferenceChange(this.mPreference, String.valueOf(config.macRandomizationSetting));
        }
    }

    private int getWifiEntryPrivacy(WifiConfiguration wifiConfiguration) {
        int i = wifiConfiguration.macRandomizationSetting;
        if (i != 0) {
            return i != 1 ? 2 : 1;
        }
        return 0;
    }
}
