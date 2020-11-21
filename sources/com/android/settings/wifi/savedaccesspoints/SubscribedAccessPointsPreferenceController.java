package com.android.settings.wifi.savedaccesspoints;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.wifi.WifiSavedConfigUtils;
import java.util.List;
import java.util.stream.Collectors;

public class SubscribedAccessPointsPreferenceController extends SavedAccessPointsPreferenceController {
    @Override // com.android.settings.wifi.savedaccesspoints.SavedAccessPointsPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.wifi.savedaccesspoints.SavedAccessPointsPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.wifi.savedaccesspoints.SavedAccessPointsPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.wifi.savedaccesspoints.SavedAccessPointsPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.wifi.savedaccesspoints.SavedAccessPointsPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.wifi.savedaccesspoints.SavedAccessPointsPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.wifi.savedaccesspoints.SavedAccessPointsPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.wifi.savedaccesspoints.SavedAccessPointsPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public SubscribedAccessPointsPreferenceController(Context context, String str) {
        super(context, str);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.wifi.savedaccesspoints.SavedAccessPointsPreferenceController
    public void refreshSavedAccessPoints() {
        this.mAccessPoints = (List) WifiSavedConfigUtils.getAllConfigs(this.mContext, this.mWifiManager).stream().filter($$Lambda$SubscribedAccessPointsPreferenceController$hMbCNMfk1vTBjfR8IBrpTCOpm4Y.INSTANCE).sorted(SavedNetworkComparator.INSTANCE).collect(Collectors.toList());
    }
}
