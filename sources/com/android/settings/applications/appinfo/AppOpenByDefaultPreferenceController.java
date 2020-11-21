package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.IUsbManager;
import android.os.ServiceManager;
import android.os.UserHandle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.AppLaunchSettings;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;

public class AppOpenByDefaultPreferenceController extends AppInfoPreferenceControllerBase {
    private PackageManager mPackageManager;
    private String mPackageName;
    private IUsbManager mUsbManager = IUsbManager.Stub.asInterface(ServiceManager.getService("usb"));

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

    public AppOpenByDefaultPreferenceController(Context context, String str) {
        super(context, str);
        this.mPackageManager = context.getPackageManager();
    }

    public AppOpenByDefaultPreferenceController setPackageName(String str) {
        this.mPackageName = str;
        return this;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController, com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public void displayPreference(PreferenceScreen preferenceScreen) {
        ApplicationInfo applicationInfo;
        super.displayPreference(preferenceScreen);
        ApplicationsState.AppEntry appEntry = this.mParent.getAppEntry();
        if (appEntry == null || (applicationInfo = appEntry.info) == null) {
            this.mPreference.setEnabled(false);
        } else if ((applicationInfo.flags & 8388608) == 0 || !applicationInfo.enabled) {
            this.mPreference.setEnabled(false);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        PackageInfo packageInfo = this.mParent.getPackageInfo();
        if (packageInfo == null || AppUtils.isInstant(packageInfo.applicationInfo) || AppUtils.isBrowserApp(this.mContext, packageInfo.packageName, UserHandle.myUserId())) {
            preference.setVisible(false);
            return;
        }
        preference.setVisible(true);
        preference.setSummary(AppUtils.getLaunchByDefaultSummary(this.mParent.getAppEntry(), this.mUsbManager, this.mPackageManager, this.mContext));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public Class<? extends SettingsPreferenceFragment> getDetailFragmentClass() {
        return AppLaunchSettings.class;
    }
}
