package com.android.settings.wifi.savedaccesspoints2;

import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.C0019R$xml;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.wifi.details2.WifiNetworkDetailsFragment2;
import com.android.wifitrackerlib.SavedNetworkTracker;

public class SavedAccessPointsWifiSettings2 extends DashboardFragment implements SavedNetworkTracker.SavedNetworkTrackerCallback {
    private SavedNetworkTracker mSavedNetworkTracker;
    private HandlerThread mWorkerThread;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SavedAccessPoints2";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 106;
    }

    @Override // com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    public void onWifiStateChanged() {
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.wifi_display_saved_access_points2;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((SavedAccessPointsPreferenceController2) use(SavedAccessPointsPreferenceController2.class)).setHost(this);
        ((SubscribedAccessPointsPreferenceController2) use(SubscribedAccessPointsPreferenceController2.class)).setHost(this);
    }

    /* JADX WARN: Type inference failed for: r8v0, types: [java.time.Clock, com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsWifiSettings2$1] */
    /* JADX WARNING: Unknown variable types count: 1 */
    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r15) {
        /*
        // Method dump skipped, instructions count: 117
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsWifiSettings2.onCreate(android.os.Bundle):void");
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        onSavedWifiEntriesChanged();
        onSubscriptionWifiEntriesChanged();
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        this.mWorkerThread.quit();
        super.onDestroy();
    }

    public void showWifiPage(String str, CharSequence charSequence) {
        removeDialog(1);
        if (TextUtils.isEmpty(str)) {
            Log.e("SavedAccessPoints2", "Not able to show WifiEntry of an empty key");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("key_chosen_wifientry_key", str);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setTitleText(charSequence);
        subSettingLauncher.setDestination(WifiNetworkDetailsFragment2.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.launch();
    }

    @Override // com.android.wifitrackerlib.SavedNetworkTracker.SavedNetworkTrackerCallback
    public void onSavedWifiEntriesChanged() {
        if (!isFinishingOrDestroyed()) {
            ((SavedAccessPointsPreferenceController2) use(SavedAccessPointsPreferenceController2.class)).displayPreference(getPreferenceScreen(), this.mSavedNetworkTracker.getSavedWifiEntries());
        }
    }

    @Override // com.android.wifitrackerlib.SavedNetworkTracker.SavedNetworkTrackerCallback
    public void onSubscriptionWifiEntriesChanged() {
        if (!isFinishingOrDestroyed()) {
            ((SubscribedAccessPointsPreferenceController2) use(SubscribedAccessPointsPreferenceController2.class)).displayPreference(getPreferenceScreen(), this.mSavedNetworkTracker.getSubscriptionWifiEntries());
        }
    }
}
