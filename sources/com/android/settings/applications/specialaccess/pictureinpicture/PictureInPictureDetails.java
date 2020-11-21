package com.android.settings.applications.specialaccess.pictureinpicture;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class PictureInPictureDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener {
    private SwitchPreference mSwitchPref;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 812;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.applications.AppInfoBase, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.picture_in_picture_permissions_details);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("app_ops_settings_switch");
        this.mSwitchPref = switchPreference;
        switchPreference.setTitle(C0017R$string.picture_in_picture_app_detail_switch);
        this.mSwitchPref.setOnPreferenceChangeListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != this.mSwitchPref) {
            return false;
        }
        Boolean bool = (Boolean) obj;
        logSpecialPermissionChange(bool.booleanValue(), this.mPackageName);
        setEnterPipStateForPackage(getActivity(), this.mPackageInfo.applicationInfo.uid, this.mPackageName, bool.booleanValue());
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public boolean refreshUi() {
        this.mSwitchPref.setChecked(getEnterPipStateForPackage(getActivity(), this.mPackageInfo.applicationInfo.uid, this.mPackageName));
        return true;
    }

    static void setEnterPipStateForPackage(Context context, int i, String str, boolean z) {
        ((AppOpsManager) context.getSystemService(AppOpsManager.class)).setMode(67, i, str, z ? 0 : 2);
    }

    static boolean getEnterPipStateForPackage(Context context, int i, String str) {
        return ((AppOpsManager) context.getSystemService(AppOpsManager.class)).checkOpNoThrow(67, i, str) == 0;
    }

    public static int getPreferenceSummary(Context context, int i, String str) {
        if (getEnterPipStateForPackage(context, i, str)) {
            return C0017R$string.app_permission_summary_allowed;
        }
        return C0017R$string.app_permission_summary_not_allowed;
    }

    /* access modifiers changed from: package-private */
    public void logSpecialPermissionChange(boolean z, String str) {
        int i = z ? 813 : 814;
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), i, getMetricsCategory(), str, 0);
    }
}
