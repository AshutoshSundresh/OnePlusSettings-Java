package com.android.settings.applications.appinfo;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.Utils;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.AppStateAppOpsBridge;
import com.android.settings.applications.AppStateOverlayBridge;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class DrawOverlayDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private AppOpsManager mAppOpsManager;
    private AppStateOverlayBridge mOverlayBridge;
    private AppStateOverlayBridge.OverlayState mOverlayState;
    private SwitchPreference mSwitchPref;

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
        this.mOverlayBridge = new AppStateOverlayBridge(activity, ((AppInfoBase) this).mState, null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService("appops");
        if (!Utils.isSystemAlertWindowEnabled(activity)) {
            this.mPackageInfo = null;
            return;
        }
        addPreferencesFromResource(C0019R$xml.draw_overlay_permissions_details);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("app_ops_settings_switch");
        this.mSwitchPref = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (this.mPackageInfo == null) {
            return layoutInflater.inflate(C0012R$layout.manage_applications_apps_unsupported, (ViewGroup) null);
        }
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settings.applications.AppInfoBase, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        super.onDestroy();
        this.mOverlayBridge.release();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != this.mSwitchPref) {
            return false;
        }
        if (!(this.mOverlayState == null || ((Boolean) obj).booleanValue() == this.mOverlayState.isPermissible())) {
            setCanDrawOverlay(!this.mOverlayState.isPermissible());
            refreshUi();
        }
        return true;
    }

    private void setCanDrawOverlay(boolean z) {
        logSpecialPermissionChange(z, this.mPackageName);
        this.mAppOpsManager.setMode(24, this.mPackageInfo.applicationInfo.uid, this.mPackageName, z ? 0 : 2);
    }

    /* access modifiers changed from: package-private */
    public void logSpecialPermissionChange(boolean z, String str) {
        int i = z ? 770 : 771;
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), i, getMetricsCategory(), str, 0);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public boolean refreshUi() {
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null) {
            return true;
        }
        AppStateOverlayBridge.OverlayState overlayInfo = this.mOverlayBridge.getOverlayInfo(this.mPackageName, packageInfo.applicationInfo.uid);
        this.mOverlayState = overlayInfo;
        this.mSwitchPref.setChecked(overlayInfo.isPermissible());
        String str = this.mPackageName;
        boolean z = false;
        if (str == null || !"com.heytap.speechassist".equals(str)) {
            SwitchPreference switchPreference = this.mSwitchPref;
            AppStateOverlayBridge.OverlayState overlayState = this.mOverlayState;
            if (overlayState.permissionDeclared && overlayState.controlEnabled) {
                z = true;
            }
            switchPreference.setEnabled(z);
        } else {
            this.mSwitchPref.setEnabled(false);
        }
        return true;
    }

    public static CharSequence getSummary(Context context, ApplicationsState.AppEntry appEntry) {
        AppStateOverlayBridge.OverlayState overlayState;
        Object obj = appEntry.extraInfo;
        if (obj instanceof AppStateOverlayBridge.OverlayState) {
            overlayState = (AppStateOverlayBridge.OverlayState) obj;
        } else if (obj instanceof AppStateAppOpsBridge.PermissionState) {
            overlayState = new AppStateOverlayBridge.OverlayState((AppStateAppOpsBridge.PermissionState) appEntry.extraInfo);
        } else {
            AppStateOverlayBridge appStateOverlayBridge = new AppStateOverlayBridge(context, null, null);
            ApplicationInfo applicationInfo = appEntry.info;
            overlayState = appStateOverlayBridge.getOverlayInfo(applicationInfo.packageName, applicationInfo.uid);
        }
        return getSummary(context, overlayState);
    }

    public static CharSequence getSummary(Context context, AppStateOverlayBridge.OverlayState overlayState) {
        int i;
        if (overlayState.isPermissible()) {
            i = C0017R$string.app_permission_summary_allowed;
        } else {
            i = C0017R$string.app_permission_summary_not_allowed;
        }
        return context.getString(i);
    }
}
