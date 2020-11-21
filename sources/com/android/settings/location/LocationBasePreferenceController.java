package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.location.LocationEnabler;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;

public abstract class LocationBasePreferenceController extends BasePreferenceController implements LocationEnabler.LocationModeChangeListener {
    protected DashboardFragment mFragment;
    protected Lifecycle mLifecycle;
    protected LocationEnabler mLocationEnabler;
    protected UserManager mUserManager = ((UserManager) this.mContext.getSystemService("user"));

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    @Override // com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public abstract /* synthetic */ void onLocationModeChanged(int i, boolean z);

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public LocationBasePreferenceController(Context context, String str) {
        super(context, str);
    }

    public void init(DashboardFragment dashboardFragment) {
        this.mFragment = dashboardFragment;
        this.mLifecycle = dashboardFragment.getSettingsLifecycle();
        this.mLocationEnabler = new LocationEnabler(this.mContext, this, this.mLifecycle);
    }
}
