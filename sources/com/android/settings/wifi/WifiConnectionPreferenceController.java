package com.android.settings.wifi;

import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.wifi.details2.WifiNetworkDetailsFragment2;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.wifi.WifiEntryPreference;
import com.android.wifitrackerlib.WifiEntry;
import com.android.wifitrackerlib.WifiPickerTracker;

public class WifiConnectionPreferenceController extends AbstractPreferenceController implements WifiPickerTracker.WifiPickerTrackerCallback {
    private int mMetricsCategory;
    private Context mPrefContext;
    private WifiEntryPreference mPreference;
    private PreferenceGroup mPreferenceGroup;
    private String mPreferenceGroupKey;
    private UpdateListener mUpdateListener;
    public WifiPickerTracker mWifiPickerTracker;
    private HandlerThread mWorkerThread;
    private int order;

    public interface UpdateListener {
        void onChildrenUpdated();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "active_wifi_connection";
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedNetworksChanged() {
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onNumSavedSubscriptionsChanged() {
    }

    /* JADX WARN: Type inference failed for: r8v0, types: [com.android.settings.wifi.WifiConnectionPreferenceController$1, java.time.Clock] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public WifiConnectionPreferenceController(android.content.Context r17, com.android.settingslib.core.lifecycle.Lifecycle r18, com.android.settings.wifi.WifiConnectionPreferenceController.UpdateListener r19, java.lang.String r20, int r21, int r22) {
        /*
        // Method dump skipped, instructions count: 132
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiConnectionPreferenceController.<init>(android.content.Context, com.android.settingslib.core.lifecycle.Lifecycle, com.android.settings.wifi.WifiConnectionPreferenceController$UpdateListener, java.lang.String, int, int):void");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mWifiPickerTracker.getConnectedWifiEntry() != null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(this.mPreferenceGroupKey);
        this.mPrefContext = preferenceScreen.getContext();
        update();
    }

    private void updatePreference(WifiEntry wifiEntry) {
        WifiEntryPreference wifiEntryPreference = this.mPreference;
        if (wifiEntryPreference != null) {
            this.mPreferenceGroup.removePreference(wifiEntryPreference);
            this.mPreference = null;
        }
        if (wifiEntry != null && this.mPrefContext != null) {
            WifiEntryPreference wifiEntryPreference2 = new WifiEntryPreference(this.mPrefContext, wifiEntry);
            this.mPreference = wifiEntryPreference2;
            wifiEntryPreference2.setKey("active_wifi_connection");
            this.mPreference.refresh();
            this.mPreference.setOrder(this.order);
            this.mPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(wifiEntry) {
                /* class com.android.settings.wifi.$$Lambda$WifiConnectionPreferenceController$hoKW95IMHhfoYRdsPGOnxkhjA */
                public final /* synthetic */ WifiEntry f$1;

                {
                    this.f$1 = r2;
                }

                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    return WifiConnectionPreferenceController.this.lambda$updatePreference$0$WifiConnectionPreferenceController(this.f$1, preference);
                }
            });
            this.mPreferenceGroup.addPreference(this.mPreference);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updatePreference$0 */
    public /* synthetic */ boolean lambda$updatePreference$0$WifiConnectionPreferenceController(WifiEntry wifiEntry, Preference preference) {
        Bundle bundle = new Bundle();
        bundle.putString("key_chosen_wifientry_key", wifiEntry.getKey());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mPrefContext);
        subSettingLauncher.setTitleRes(C0017R$string.pref_title_network_details);
        subSettingLauncher.setDestination(WifiNetworkDetailsFragment2.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setSourceMetricsCategory(this.mMetricsCategory);
        subSettingLauncher.launch();
        return true;
    }

    private void update() {
        WifiEntry connectedWifiEntry = this.mWifiPickerTracker.getConnectedWifiEntry();
        if (connectedWifiEntry == null) {
            updatePreference(null);
        } else {
            WifiEntryPreference wifiEntryPreference = this.mPreference;
            if (wifiEntryPreference == null || !wifiEntryPreference.getWifiEntry().equals(connectedWifiEntry)) {
                updatePreference(connectedWifiEntry);
            } else {
                WifiEntryPreference wifiEntryPreference2 = this.mPreference;
                if (wifiEntryPreference2 != null) {
                    wifiEntryPreference2.refresh();
                }
            }
        }
        this.mUpdateListener.onChildrenUpdated();
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    public void onWifiStateChanged() {
        update();
    }

    @Override // com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback
    public void onWifiEntriesChanged() {
        update();
    }
}
