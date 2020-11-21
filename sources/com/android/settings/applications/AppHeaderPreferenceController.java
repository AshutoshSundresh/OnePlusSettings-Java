package com.android.settings.applications;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0010R$id;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.LayoutPreference;

public class AppHeaderPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume {
    private LayoutPreference mHeaderPreference;
    private Lifecycle mLifecycle;
    private PackageInfo mPackageInfo;
    private DashboardFragment mParent;

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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppHeaderPreferenceController(Context context, String str) {
        super(context, str);
    }

    public AppHeaderPreferenceController setParentFragment(DashboardFragment dashboardFragment) {
        this.mParent = dashboardFragment;
        return this;
    }

    public AppHeaderPreferenceController setPackageInfo(PackageInfo packageInfo) {
        this.mPackageInfo = packageInfo;
        return this;
    }

    public AppHeaderPreferenceController setLifeCycle(Lifecycle lifecycle) {
        this.mLifecycle = lifecycle;
        return this;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mHeaderPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        FragmentActivity activity = this.mParent.getActivity();
        PackageManager packageManager = activity.getPackageManager();
        EntityHeaderController newInstance = EntityHeaderController.newInstance(activity, this.mParent, this.mHeaderPreference.findViewById(C0010R$id.entity_header));
        newInstance.setRecyclerView(this.mParent.getListView(), this.mLifecycle);
        newInstance.setIcon(Utils.getBadgedIcon(this.mParent.getContext(), this.mPackageInfo.applicationInfo));
        newInstance.setLabel(this.mPackageInfo.applicationInfo.loadLabel(packageManager));
        newInstance.setSummary(this.mPackageInfo);
        newInstance.setIsInstantApp(AppUtils.isInstant(this.mPackageInfo.applicationInfo));
        newInstance.setPackageName(this.mPackageInfo.packageName);
        newInstance.setUid(this.mPackageInfo.applicationInfo.uid);
        newInstance.setButtonActions(0, 0);
        newInstance.done((Activity) this.mParent.getActivity(), true);
    }
}
