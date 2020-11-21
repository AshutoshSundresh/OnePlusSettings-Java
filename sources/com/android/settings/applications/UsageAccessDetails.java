package com.android.settings.applications;

import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.AppStateUsageBridge;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class UsageAccessDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private AppOpsManager mAppOpsManager;
    private DevicePolicyManager mDpm;
    private Intent mSettingsIntent;
    private SwitchPreference mSwitchPref;
    private AppStateUsageBridge mUsageBridge;
    private Preference mUsageDesc;
    private AppStateUsageBridge.UsageState mUsageState;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 183;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.applications.AppInfoBase, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mUsageBridge = new AppStateUsageBridge(activity, ((AppInfoBase) this).mState, null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService("appops");
        this.mDpm = (DevicePolicyManager) activity.getSystemService(DevicePolicyManager.class);
        addPreferencesFromResource(C0019R$xml.app_ops_permissions_details);
        this.mSwitchPref = (SwitchPreference) findPreference("app_ops_settings_switch");
        this.mUsageDesc = findPreference("app_ops_settings_description");
        getPreferenceScreen().setTitle(C0017R$string.usage_access);
        this.mSwitchPref.setTitle(C0017R$string.permit_usage_access);
        this.mUsageDesc.setSummary(C0017R$string.usage_access_description);
        this.mSwitchPref.setOnPreferenceChangeListener(this);
        this.mSettingsIntent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.USAGE_ACCESS_CONFIG").setPackage(this.mPackageName);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != this.mSwitchPref) {
            return false;
        }
        if (!(this.mUsageState == null || ((Boolean) obj).booleanValue() == this.mUsageState.isPermissible())) {
            if (this.mUsageState.isPermissible() && this.mDpm.isProfileOwnerApp(this.mPackageName)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setIcon(17302394);
                builder.setTitle(17039380);
                builder.setMessage(C0017R$string.work_profile_usage_access_warning);
                builder.setPositiveButton(C0017R$string.okay, (DialogInterface.OnClickListener) null);
                builder.show();
            }
            setHasAccess(!this.mUsageState.isPermissible());
            refreshUi();
        }
        return true;
    }

    private static boolean doesAnyPermissionMatch(String str, String[] strArr) {
        for (String str2 : strArr) {
            if (str.equals(str2)) {
                return true;
            }
        }
        return false;
    }

    private void setHasAccess(boolean z) {
        logSpecialPermissionChange(z, this.mPackageName);
        int i = !z ? 1 : 0;
        int i2 = this.mPackageInfo.applicationInfo.uid;
        if (doesAnyPermissionMatch("android.permission.PACKAGE_USAGE_STATS", this.mUsageState.packageInfo.requestedPermissions)) {
            this.mAppOpsManager.setMode(43, i2, this.mPackageName, i);
        }
        if (doesAnyPermissionMatch("android.permission.LOADER_USAGE_STATS", this.mUsageState.packageInfo.requestedPermissions)) {
            this.mAppOpsManager.setMode(95, i2, this.mPackageName, i);
        }
    }

    /* access modifiers changed from: package-private */
    public void logSpecialPermissionChange(boolean z, String str) {
        int i = z ? 783 : 784;
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), i, getMetricsCategory(), str, 0);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public boolean refreshUi() {
        PackageInfo packageInfo;
        retrieveAppEntry();
        if (this.mAppEntry == null || (packageInfo = this.mPackageInfo) == null) {
            return false;
        }
        AppStateUsageBridge.UsageState usageInfo = this.mUsageBridge.getUsageInfo(this.mPackageName, packageInfo.applicationInfo.uid);
        this.mUsageState = usageInfo;
        this.mSwitchPref.setChecked(usageInfo.isPermissible());
        this.mSwitchPref.setEnabled(this.mUsageState.permissionDeclared);
        ResolveInfo resolveActivityAsUser = this.mPm.resolveActivityAsUser(this.mSettingsIntent, 128, this.mUserId);
        if (resolveActivityAsUser == null) {
            return true;
        }
        Bundle bundle = resolveActivityAsUser.activityInfo.metaData;
        Intent intent = this.mSettingsIntent;
        ActivityInfo activityInfo = resolveActivityAsUser.activityInfo;
        intent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
        if (bundle == null || !bundle.containsKey("android.settings.metadata.USAGE_ACCESS_REASON")) {
            return true;
        }
        this.mSwitchPref.setSummary(bundle.getString("android.settings.metadata.USAGE_ACCESS_REASON"));
        return true;
    }
}
