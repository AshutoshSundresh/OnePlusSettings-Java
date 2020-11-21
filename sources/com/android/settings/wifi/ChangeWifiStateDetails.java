package com.android.settings.wifi;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.AppStateAppOpsBridge;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.wifi.AppStateChangeWifiStateBridge;
import com.android.settingslib.applications.ApplicationsState;

public class ChangeWifiStateDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener {
    private AppStateChangeWifiStateBridge mAppBridge;
    private AppOpsManager mAppOpsManager;
    private SwitchPreference mSwitchPref;
    private AppStateChangeWifiStateBridge.WifiSettingsState mWifiSettingsState;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 338;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.applications.AppInfoBase, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mAppBridge = new AppStateChangeWifiStateBridge(activity, ((AppInfoBase) this).mState, null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService("appops");
        addPreferencesFromResource(C0019R$xml.change_wifi_state_details);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("app_ops_settings_switch");
        this.mSwitchPref = switchPreference;
        switchPreference.setTitle(C0017R$string.change_wifi_state_app_detail_switch);
        this.mSwitchPref.setOnPreferenceChangeListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != this.mSwitchPref) {
            return false;
        }
        if (!(this.mWifiSettingsState == null || ((Boolean) obj).booleanValue() == this.mWifiSettingsState.isPermissible())) {
            setCanChangeWifiState(!this.mWifiSettingsState.isPermissible());
            refreshUi();
        }
        return true;
    }

    private void setCanChangeWifiState(boolean z) {
        logSpecialPermissionChange(z, this.mPackageName);
        this.mAppOpsManager.setMode(71, this.mPackageInfo.applicationInfo.uid, this.mPackageName, !z ? 1 : 0);
    }

    /* access modifiers changed from: protected */
    public void logSpecialPermissionChange(boolean z, String str) {
        FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider().action(getContext(), z ? 774 : 775, str);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public boolean refreshUi() {
        ApplicationInfo applicationInfo;
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null || (applicationInfo = packageInfo.applicationInfo) == null) {
            return false;
        }
        AppStateChangeWifiStateBridge.WifiSettingsState wifiSettingsInfo = this.mAppBridge.getWifiSettingsInfo(this.mPackageName, applicationInfo.uid);
        this.mWifiSettingsState = wifiSettingsInfo;
        this.mSwitchPref.setChecked(wifiSettingsInfo.isPermissible());
        this.mSwitchPref.setEnabled(this.mWifiSettingsState.permissionDeclared);
        return true;
    }

    public static CharSequence getSummary(Context context, ApplicationsState.AppEntry appEntry) {
        AppStateChangeWifiStateBridge.WifiSettingsState wifiSettingsState;
        Object obj = appEntry.extraInfo;
        if (obj instanceof AppStateChangeWifiStateBridge.WifiSettingsState) {
            wifiSettingsState = (AppStateChangeWifiStateBridge.WifiSettingsState) obj;
        } else if (obj instanceof AppStateAppOpsBridge.PermissionState) {
            wifiSettingsState = new AppStateChangeWifiStateBridge.WifiSettingsState((AppStateAppOpsBridge.PermissionState) obj);
        } else {
            AppStateChangeWifiStateBridge appStateChangeWifiStateBridge = new AppStateChangeWifiStateBridge(context, null, null);
            ApplicationInfo applicationInfo = appEntry.info;
            wifiSettingsState = appStateChangeWifiStateBridge.getWifiSettingsInfo(applicationInfo.packageName, applicationInfo.uid);
        }
        return getSummary(context, wifiSettingsState);
    }

    public static CharSequence getSummary(Context context, AppStateChangeWifiStateBridge.WifiSettingsState wifiSettingsState) {
        int i;
        if (wifiSettingsState.isPermissible()) {
            i = C0017R$string.app_permission_summary_allowed;
        } else {
            i = C0017R$string.app_permission_summary_not_allowed;
        }
        return context.getString(i);
    }
}
