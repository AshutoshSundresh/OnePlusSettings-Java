package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserManager;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.applications.AppStoreUtil;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.applications.AppUtils;

public class AppInstallerInfoPreferenceController extends AppInfoPreferenceControllerBase {
    private CharSequence mInstallerLabel;
    private String mInstallerPackage;
    private String mPackageName;

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppInstallerInfoPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public int getAvailabilityStatus() {
        if (!UserManager.get(this.mContext).isManagedProfile() && !AppUtils.isMainlineModule(this.mContext.getPackageManager(), this.mPackageName) && this.mInstallerLabel != null) {
            return 0;
        }
        return 4;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int i;
        if (AppUtils.isInstant(this.mParent.getPackageInfo().applicationInfo)) {
            i = C0017R$string.instant_app_details_summary;
        } else {
            i = C0017R$string.app_install_details_summary;
        }
        preference.setSummary(this.mContext.getString(i, this.mInstallerLabel));
        Intent appStoreLink = AppStoreUtil.getAppStoreLink(this.mContext, this.mInstallerPackage, this.mPackageName);
        if (appStoreLink != null) {
            preference.setIntent(appStoreLink);
        } else {
            preference.setEnabled(false);
        }
    }

    public void setPackageName(String str) {
        this.mPackageName = str;
        String installerPackageName = AppStoreUtil.getInstallerPackageName(this.mContext, str);
        this.mInstallerPackage = installerPackageName;
        this.mInstallerLabel = Utils.getApplicationLabel(this.mContext, installerPackageName);
    }
}
