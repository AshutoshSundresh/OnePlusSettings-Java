package com.android.settings.wifi.savedaccesspoints;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0019R$xml;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.wifi.details.WifiNetworkDetailsFragment;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.AccessPointPreference;

public class SavedAccessPointsWifiSettings extends DashboardFragment {
    Bundle mAccessPointSavedState;
    private AccessPoint mSelectedAccessPoint;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SavedAccessPoints";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 106;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.wifi_display_saved_access_points;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        ((SavedAccessPointsPreferenceController) use(SavedAccessPointsPreferenceController.class)).setHost(this);
        ((SubscribedAccessPointsPreferenceController) use(SubscribedAccessPointsPreferenceController.class)).setHost(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            return;
        }
        if (bundle.containsKey("wifi_ap_state")) {
            this.mAccessPointSavedState = bundle.getBundle("wifi_ap_state");
        } else {
            this.mAccessPointSavedState = null;
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStart() {
        super.onStart();
        if (this.mAccessPointSavedState != null) {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            ((SavedAccessPointsPreferenceController) use(SavedAccessPointsPreferenceController.class)).displayPreference(preferenceScreen);
            ((SubscribedAccessPointsPreferenceController) use(SubscribedAccessPointsPreferenceController.class)).displayPreference(preferenceScreen);
        }
    }

    public void showWifiPage(AccessPointPreference accessPointPreference) {
        removeDialog(1);
        if (accessPointPreference != null) {
            this.mSelectedAccessPoint = accessPointPreference.getAccessPoint();
        } else {
            this.mSelectedAccessPoint = null;
            this.mAccessPointSavedState = null;
        }
        if (this.mSelectedAccessPoint == null) {
            this.mSelectedAccessPoint = new AccessPoint(getActivity(), this.mAccessPointSavedState);
        }
        Bundle bundle = new Bundle();
        this.mSelectedAccessPoint.saveWifiState(bundle);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setTitleText(this.mSelectedAccessPoint.getTitle());
        subSettingLauncher.setDestination(WifiNetworkDetailsFragment.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.launch();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mSelectedAccessPoint != null) {
            Bundle bundle2 = new Bundle();
            this.mAccessPointSavedState = bundle2;
            this.mSelectedAccessPoint.saveWifiState(bundle2);
            bundle.putBundle("wifi_ap_state", this.mAccessPointSavedState);
        }
    }
}
