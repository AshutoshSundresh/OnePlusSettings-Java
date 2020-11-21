package com.android.settings.applications.appinfo;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
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
import com.android.settings.applications.AppStateWriteSettingsBridge;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.applications.ApplicationsState;

public class WriteSettingsDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private AppStateWriteSettingsBridge mAppBridge;
    private AppOpsManager mAppOpsManager;
    private Intent mSettingsIntent;
    private SwitchPreference mSwitchPref;
    private AppStateWriteSettingsBridge.WriteSettingsState mWriteSettingsState;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 221;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.applications.AppInfoBase, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mAppBridge = new AppStateWriteSettingsBridge(activity, ((AppInfoBase) this).mState, null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService("appops");
        addPreferencesFromResource(C0019R$xml.write_system_settings_permissions_details);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("app_ops_settings_switch");
        this.mSwitchPref = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        this.mSettingsIntent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.USAGE_ACCESS_CONFIG").setPackage(this.mPackageName);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != this.mSwitchPref) {
            return false;
        }
        if (!(this.mWriteSettingsState == null || ((Boolean) obj).booleanValue() == this.mWriteSettingsState.isPermissible())) {
            setCanWriteSettings(!this.mWriteSettingsState.isPermissible());
            refreshUi();
        }
        return true;
    }

    private void setCanWriteSettings(boolean z) {
        logSpecialPermissionChange(z, this.mPackageName);
        this.mAppOpsManager.setMode(23, this.mPackageInfo.applicationInfo.uid, this.mPackageName, z ? 0 : 2);
    }

    /* access modifiers changed from: package-private */
    public void logSpecialPermissionChange(boolean z, String str) {
        FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider().action(getContext(), z ? 774 : 775, str);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public boolean refreshUi() {
        AppStateWriteSettingsBridge.WriteSettingsState writeSettingsInfo = this.mAppBridge.getWriteSettingsInfo(this.mPackageName, this.mPackageInfo.applicationInfo.uid);
        this.mWriteSettingsState = writeSettingsInfo;
        this.mSwitchPref.setChecked(writeSettingsInfo.isPermissible());
        this.mSwitchPref.setEnabled(this.mWriteSettingsState.permissionDeclared);
        this.mPm.resolveActivityAsUser(this.mSettingsIntent, 128, this.mUserId);
        return true;
    }

    public static CharSequence getSummary(Context context, ApplicationsState.AppEntry appEntry) {
        AppStateWriteSettingsBridge.WriteSettingsState writeSettingsState;
        Object obj = appEntry.extraInfo;
        if (obj instanceof AppStateWriteSettingsBridge.WriteSettingsState) {
            writeSettingsState = (AppStateWriteSettingsBridge.WriteSettingsState) obj;
        } else if (obj instanceof AppStateAppOpsBridge.PermissionState) {
            writeSettingsState = new AppStateWriteSettingsBridge.WriteSettingsState((AppStateAppOpsBridge.PermissionState) obj);
        } else {
            AppStateWriteSettingsBridge appStateWriteSettingsBridge = new AppStateWriteSettingsBridge(context, null, null);
            ApplicationInfo applicationInfo = appEntry.info;
            writeSettingsState = appStateWriteSettingsBridge.getWriteSettingsInfo(applicationInfo.packageName, applicationInfo.uid);
        }
        return getSummary(context, writeSettingsState);
    }

    public static CharSequence getSummary(Context context, AppStateWriteSettingsBridge.WriteSettingsState writeSettingsState) {
        int i;
        if (writeSettingsState.isPermissible()) {
            i = C0017R$string.app_permission_summary_allowed;
        } else {
            i = C0017R$string.app_permission_summary_not_allowed;
        }
        return context.getString(i);
    }
}
