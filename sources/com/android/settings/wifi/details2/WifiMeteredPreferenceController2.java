package com.android.settings.wifi.details2;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import androidx.preference.DropDownPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.WifiDialog2;
import com.android.wifitrackerlib.WifiEntry;

public class WifiMeteredPreferenceController2 extends BasePreferenceController implements Preference.OnPreferenceChangeListener, WifiDialog2.WifiDialog2Listener {
    private static final String KEY_WIFI_METERED = "metered";
    private Preference mPreference;
    private WifiEntry mWifiEntry;

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

    public WifiMeteredPreferenceController2(Context context, WifiEntry wifiEntry) {
        super(context, KEY_WIFI_METERED);
        this.mWifiEntry = wifiEntry;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        DropDownPreference dropDownPreference = (DropDownPreference) preference;
        int meteredOverride = getMeteredOverride();
        preference.setSelectable(this.mWifiEntry.canSetMeteredChoice());
        dropDownPreference.setValue(Integer.toString(meteredOverride));
        updateSummary(dropDownPreference, meteredOverride);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mWifiEntry.isSaved() || this.mWifiEntry.isSubscription()) {
            this.mWifiEntry.setMeteredChoice(Integer.parseInt((String) obj));
        }
        BackupManager.dataChanged("com.android.providers.settings");
        updateSummary((DropDownPreference) preference, getMeteredOverride());
        return true;
    }

    /* access modifiers changed from: package-private */
    public int getMeteredOverride() {
        if (this.mWifiEntry.isSaved() || this.mWifiEntry.isSubscription()) {
            return this.mWifiEntry.getMeteredChoice();
        }
        return 0;
    }

    private void updateSummary(DropDownPreference dropDownPreference, int i) {
        dropDownPreference.setSummary(dropDownPreference.getEntries()[i]);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.wifi.WifiDialog2.WifiDialog2Listener
    public void onSubmit(WifiDialog2 wifiDialog2) {
        WifiConfiguration config;
        if (wifiDialog2.getController() != null && this.mWifiEntry.canSetMeteredChoice() && (config = wifiDialog2.getController().getConfig()) != null && getWifiEntryMeteredChoice(config) != this.mWifiEntry.getMeteredChoice()) {
            this.mWifiEntry.setMeteredChoice(getWifiEntryMeteredChoice(config));
            onPreferenceChange(this.mPreference, String.valueOf(config.meteredOverride));
        }
    }

    private int getWifiEntryMeteredChoice(WifiConfiguration wifiConfiguration) {
        int i = wifiConfiguration.meteredOverride;
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                return 0;
            }
        }
        return i2;
    }
}
