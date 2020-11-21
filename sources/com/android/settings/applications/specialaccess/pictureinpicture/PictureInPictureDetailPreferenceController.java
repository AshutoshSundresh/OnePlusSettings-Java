package com.android.settings.applications.specialaccess.pictureinpicture;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase;
import com.android.settings.slices.SliceBackgroundWorker;

public class PictureInPictureDetailPreferenceController extends AppInfoPreferenceControllerBase {
    private static final String TAG = "PicInPicDetailControl";
    private final PackageManager mPackageManager;
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

    public PictureInPictureDetailPreferenceController(Context context, String str) {
        super(context, str);
        this.mPackageManager = context.getPackageManager();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public int getAvailabilityStatus() {
        if (!this.mContext.getPackageManager().hasSystemFeature("android.software.picture_in_picture")) {
            return 3;
        }
        return hasPictureInPictureActivites() ? 0 : 4;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setSummary(getPreferenceSummary());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public Class<? extends SettingsPreferenceFragment> getDetailFragmentClass() {
        return PictureInPictureDetails.class;
    }

    /* access modifiers changed from: package-private */
    public boolean hasPictureInPictureActivites() {
        PackageInfo packageInfo;
        try {
            packageInfo = this.mPackageManager.getPackageInfoAsUser(this.mPackageName, 1, UserHandle.myUserId());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Exception while retrieving the package info of " + this.mPackageName, e);
            packageInfo = null;
        }
        if (packageInfo == null || !PictureInPictureSettings.checkPackageHasPictureInPictureActivities(packageInfo.packageName, packageInfo.activities)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public int getPreferenceSummary() {
        return PictureInPictureDetails.getPreferenceSummary(this.mContext, this.mParent.getPackageInfo().applicationInfo.uid, this.mPackageName);
    }

    public void setPackageName(String str) {
        this.mPackageName = str;
    }
}
