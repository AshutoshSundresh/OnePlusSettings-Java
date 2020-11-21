package com.android.settings.applications.specialaccess.interactacrossprofiles;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.CrossProfileApps;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class InteractAcrossProfilesController extends BasePreferenceController {
    private final Context mContext;
    private final CrossProfileApps mCrossProfileApps = ((CrossProfileApps) this.mContext.getSystemService(CrossProfileApps.class));
    private final PackageManager mPackageManager = this.mContext.getPackageManager();
    private final UserManager mUserManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public InteractAcrossProfilesController(Context context, String str) {
        super(context, str);
        this.mContext = context;
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        for (UserInfo userInfo : this.mUserManager.getProfiles(UserHandle.myUserId())) {
            if (userInfo.isManagedProfile()) {
                return 0;
            }
        }
        return 4;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int numberOfEnabledApps = InteractAcrossProfilesSettings.getNumberOfEnabledApps(this.mContext, this.mPackageManager, this.mUserManager, this.mCrossProfileApps);
        if (numberOfEnabledApps == 0) {
            return this.mContext.getResources().getString(C0017R$string.interact_across_profiles_number_of_connected_apps_none);
        }
        return this.mContext.getResources().getQuantityString(C0015R$plurals.interact_across_profiles_number_of_connected_apps, numberOfEnabledApps, Integer.valueOf(numberOfEnabledApps));
    }
}
