package com.android.settings.development;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

public class SelectDebugAppPreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin, OnActivityResultListener {
    private final DevelopmentSettingsDashboardFragment mFragment;
    private final PackageManager mPackageManager = this.mContext.getPackageManager();

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "debug_app";
    }

    public SelectDebugAppPreferenceController(Context context, DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment) {
        super(context);
        this.mFragment = developmentSettingsDashboardFragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!"debug_app".equals(preference.getKey())) {
            return false;
        }
        Intent activityStartIntent = getActivityStartIntent();
        activityStartIntent.putExtra("com.android.settings.extra.DEBUGGABLE", true);
        this.mFragment.startActivityForResult(activityStartIntent, 1);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updatePreferenceSummary();
    }

    @Override // com.android.settings.development.OnActivityResultListener
    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i != 1 || i2 != -1) {
            return false;
        }
        Settings.Global.putString(this.mContext.getContentResolver(), "debug_app", intent.getAction());
        updatePreferenceSummary();
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        this.mPreference.setSummary(this.mContext.getResources().getString(C0017R$string.debug_app_not_set));
    }

    /* access modifiers changed from: package-private */
    public Intent getActivityStartIntent() {
        return new Intent(this.mContext, AppPicker.class);
    }

    private void updatePreferenceSummary() {
        String string = Settings.Global.getString(this.mContext.getContentResolver(), "debug_app");
        if (string == null || string.length() <= 0) {
            this.mPreference.setSummary(this.mContext.getResources().getString(C0017R$string.debug_app_not_set));
            return;
        }
        this.mPreference.setSummary(this.mContext.getResources().getString(C0017R$string.debug_app_set, getAppLabel(string)));
    }

    private String getAppLabel(String str) {
        try {
            CharSequence applicationLabel = this.mPackageManager.getApplicationLabel(this.mPackageManager.getApplicationInfo(str, 512));
            return applicationLabel != null ? applicationLabel.toString() : str;
        } catch (PackageManager.NameNotFoundException unused) {
            return str;
        }
    }
}
