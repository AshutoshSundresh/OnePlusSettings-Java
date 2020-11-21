package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.slices.SliceBackgroundWorker;

public class DrawOverlayDetailPreferenceController extends AppInfoPreferenceControllerBase {
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

    public DrawOverlayDetailPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public int getAvailabilityStatus() {
        PackageInfo packageInfo;
        if (!isManagedProfile(UserManager.get(this.mContext)) && (packageInfo = this.mParent.getPackageInfo()) != null && packageInfo.requestedPermissions != null) {
            int i = 0;
            while (true) {
                String[] strArr = packageInfo.requestedPermissions;
                if (i >= strArr.length) {
                    break;
                } else if (strArr[i].equals("android.permission.SYSTEM_ALERT_WINDOW")) {
                    return 0;
                } else {
                    i++;
                }
            }
        }
        return 4;
    }

    public static boolean isManagedProfile(UserManager userManager) {
        return isManagedProfile(userManager, UserHandle.myUserId());
    }

    public static boolean isManagedProfile(UserManager userManager, int i) {
        if (userManager != null) {
            UserInfo userInfo = userManager.getUserInfo(i);
            if (i == 999 || userInfo == null) {
                return false;
            }
            return userInfo.isManagedProfile();
        }
        throw new IllegalArgumentException("userManager must not be null");
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    public Class<? extends SettingsPreferenceFragment> getDetailFragmentClass() {
        return DrawOverlayDetails.class;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return DrawOverlayDetails.getSummary(this.mContext, this.mParent.getAppEntry());
    }
}
