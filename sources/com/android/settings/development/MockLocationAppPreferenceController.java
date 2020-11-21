package com.android.settings.development;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import java.util.List;

public class MockLocationAppPreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin, OnActivityResultListener {
    private static final int[] MOCK_LOCATION_APP_OPS = {58};
    private final AppOpsManager mAppsOpsManager;
    private final DevelopmentSettingsDashboardFragment mFragment;
    private final PackageManager mPackageManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "mock_location_app";
    }

    public MockLocationAppPreferenceController(Context context, DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment) {
        super(context);
        this.mFragment = developmentSettingsDashboardFragment;
        this.mAppsOpsManager = (AppOpsManager) context.getSystemService("appops");
        this.mPackageManager = context.getPackageManager();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        Intent intent = new Intent(this.mContext, AppPicker.class);
        intent.putExtra("com.android.settings.extra.REQUESTIING_PERMISSION", "android.permission.ACCESS_MOCK_LOCATION");
        this.mFragment.startActivityForResult(intent, 2);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateMockLocation();
    }

    @Override // com.android.settings.development.OnActivityResultListener
    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i != 2 || i2 != -1) {
            return false;
        }
        writeMockLocation(intent.getAction());
        updateMockLocation();
        return true;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsDisabled() {
        super.onDeveloperOptionsDisabled();
        removeAllMockLocations();
    }

    private void updateMockLocation() {
        String currentMockLocationApp = getCurrentMockLocationApp();
        if (!TextUtils.isEmpty(currentMockLocationApp)) {
            this.mPreference.setSummary(this.mContext.getResources().getString(C0017R$string.mock_location_app_set, getAppLabel(currentMockLocationApp)));
            return;
        }
        this.mPreference.setSummary(this.mContext.getResources().getString(C0017R$string.mock_location_app_not_set));
    }

    private void writeMockLocation(String str) {
        removeAllMockLocations();
        if (!TextUtils.isEmpty(str)) {
            try {
                this.mAppsOpsManager.setMode(58, this.mPackageManager.getApplicationInfo(str, 512).uid, str, 0);
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
    }

    private String getAppLabel(String str) {
        try {
            CharSequence applicationLabel = this.mPackageManager.getApplicationLabel(this.mPackageManager.getApplicationInfo(str, 512));
            return applicationLabel != null ? applicationLabel.toString() : str;
        } catch (PackageManager.NameNotFoundException unused) {
            return str;
        }
    }

    private void removeAllMockLocations() {
        List<AppOpsManager.PackageOps> packagesForOps = this.mAppsOpsManager.getPackagesForOps(MOCK_LOCATION_APP_OPS);
        if (packagesForOps != null) {
            for (AppOpsManager.PackageOps packageOps : packagesForOps) {
                if (((AppOpsManager.OpEntry) packageOps.getOps().get(0)).getMode() != 2) {
                    removeMockLocationForApp(packageOps.getPackageName());
                }
            }
        }
    }

    private void removeMockLocationForApp(String str) {
        try {
            this.mAppsOpsManager.setMode(58, this.mPackageManager.getApplicationInfo(str, 512).uid, str, 2);
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }

    /* access modifiers changed from: package-private */
    public String getCurrentMockLocationApp() {
        List<AppOpsManager.PackageOps> packagesForOps = this.mAppsOpsManager.getPackagesForOps(MOCK_LOCATION_APP_OPS);
        if (packagesForOps == null) {
            return null;
        }
        for (AppOpsManager.PackageOps packageOps : packagesForOps) {
            if (((AppOpsManager.OpEntry) packageOps.getOps().get(0)).getMode() == 0) {
                return ((AppOpsManager.PackageOps) packagesForOps.get(0)).getPackageName();
            }
        }
        return null;
    }
}
