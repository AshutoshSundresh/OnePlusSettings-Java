package com.android.settings.applications.appinfo;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Settings;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.AppStateInstallAppsBridge;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.applications.ApplicationsState;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;

public class ExternalSourcesDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener {
    private ActivityManager mActivityManager;
    private AppStateInstallAppsBridge mAppBridge;
    private AppOpsManager mAppOpsManager;
    private AppStateInstallAppsBridge.InstallAppsState mInstallAppsState;
    private RestrictedSwitchPreference mSwitchPref;
    private UserManager mUserManager;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 808;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.applications.AppInfoBase, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mAppBridge = new AppStateInstallAppsBridge(activity, ((AppInfoBase) this).mState, null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService("appops");
        this.mActivityManager = (ActivityManager) activity.getSystemService(ActivityManager.class);
        this.mUserManager = UserManager.get(activity);
        addPreferencesFromResource(C0019R$xml.external_sources_details);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) findPreference("external_sources_settings_switch");
        this.mSwitchPref = restrictedSwitchPreference;
        restrictedSwitchPreference.setOnPreferenceChangeListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        int i = 0;
        if (preference != this.mSwitchPref) {
            return false;
        }
        AppStateInstallAppsBridge.InstallAppsState installAppsState = this.mInstallAppsState;
        if (installAppsState == null || booleanValue == installAppsState.canInstallApps()) {
            return true;
        }
        if (Settings.ManageAppExternalSourcesActivity.class.getName().equals(getIntent().getComponent().getClassName())) {
            if (booleanValue) {
                i = -1;
            }
            setResult(i);
        }
        setCanInstallApps(booleanValue);
        refreshUi();
        return true;
    }

    public static CharSequence getPreferenceSummary(Context context, ApplicationsState.AppEntry appEntry) {
        int i;
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(appEntry.info.uid);
        UserManager userManager = UserManager.get(context);
        int userRestrictionSource = userManager.getUserRestrictionSource("no_install_unknown_sources_globally", userHandleForUid) | userManager.getUserRestrictionSource("no_install_unknown_sources", userHandleForUid);
        if ((userRestrictionSource & 1) != 0) {
            return context.getString(C0017R$string.disabled_by_admin);
        }
        if (userRestrictionSource != 0) {
            return context.getString(C0017R$string.disabled);
        }
        AppStateInstallAppsBridge appStateInstallAppsBridge = new AppStateInstallAppsBridge(context, null, null);
        ApplicationInfo applicationInfo = appEntry.info;
        if (appStateInstallAppsBridge.createInstallAppsStateFor(applicationInfo.packageName, applicationInfo.uid).canInstallApps()) {
            i = C0017R$string.app_permission_summary_allowed;
        } else {
            i = C0017R$string.app_permission_summary_not_allowed;
        }
        return context.getString(i);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setCanInstallApps(boolean z) {
        int i = 2;
        this.mAppOpsManager.setMode(66, this.mPackageInfo.applicationInfo.uid, this.mPackageName, z ? 0 : 2);
        if (UserHandle.getUserId(this.mPackageInfo.applicationInfo.uid) == 0) {
            if (OPUtils.hasMultiApp(SettingsBaseApplication.mApplication, this.mPackageName)) {
                this.mAppOpsManager.setMode(66, UserHandle.getUid(999, UserHandle.getAppId(this.mPackageInfo.applicationInfo.uid)), this.mPackageName, z ? 0 : 2);
            }
            if (!z) {
                killApp(this.mPackageInfo.applicationInfo.uid);
            }
        }
        if (UserHandle.getUserId(this.mPackageInfo.applicationInfo.uid) == 999) {
            this.mAppOpsManager.setMode(66, UserHandle.getUid(0, UserHandle.getAppId(this.mPackageInfo.applicationInfo.uid)), this.mPackageName, z ? 0 : 2);
        }
        AppOpsManager appOpsManager = this.mAppOpsManager;
        int i2 = this.mPackageInfo.applicationInfo.uid;
        String str = this.mPackageName;
        if (z) {
            i = 0;
        }
        appOpsManager.setMode(66, i2, str, i);
    }

    private void killApp(int i) {
        if (!UserHandle.isCore(i)) {
            this.mActivityManager.killUid(i, "User denied OP_REQUEST_INSTALL_PACKAGES");
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public boolean refreshUi() {
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null || packageInfo.applicationInfo == null) {
            return false;
        }
        if (this.mUserManager.hasBaseUserRestriction("no_install_unknown_sources", UserHandle.of(UserHandle.myUserId()))) {
            this.mSwitchPref.setChecked(false);
            this.mSwitchPref.setSummary(C0017R$string.disabled);
            this.mSwitchPref.setEnabled(false);
            return true;
        }
        this.mSwitchPref.checkRestrictionAndSetDisabled("no_install_unknown_sources");
        if (!this.mSwitchPref.isDisabledByAdmin()) {
            this.mSwitchPref.checkRestrictionAndSetDisabled("no_install_unknown_sources_globally");
        }
        if (this.mSwitchPref.isDisabledByAdmin()) {
            return true;
        }
        AppStateInstallAppsBridge.InstallAppsState createInstallAppsStateFor = this.mAppBridge.createInstallAppsStateFor(this.mPackageName, this.mPackageInfo.applicationInfo.uid);
        this.mInstallAppsState = createInstallAppsStateFor;
        this.mSwitchPref.setChecked(createInstallAppsStateFor.canInstallApps());
        return true;
    }
}
