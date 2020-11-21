package com.android.settings.wifi.savedaccesspoints2;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.slices.SliceBackgroundWorker;

public class SubscribedAccessPointsPreferenceController2 extends SavedAccessPointsPreferenceController2 {
    @Override // com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsPreferenceController2, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsPreferenceController2, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsPreferenceController2, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsPreferenceController2, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsPreferenceController2, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsPreferenceController2, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsPreferenceController2, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsPreferenceController2, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public SubscribedAccessPointsPreferenceController2(Context context, String str) {
        super(context, str);
    }
}
