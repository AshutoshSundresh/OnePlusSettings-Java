package com.android.settings.wifi;

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class CellularFallbackPreferenceController extends TogglePreferenceController {
    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public CellularFallbackPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return avoidBadWifiConfig() ? 3 : 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return avoidBadWifiCurrentSettings();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return Settings.Global.putString(this.mContext.getContentResolver(), "network_avoid_bad_wifi", z ? "1" : null);
    }

    private boolean avoidBadWifiConfig() {
        int activeDataSubscriptionId = getActiveDataSubscriptionId();
        if (activeDataSubscriptionId == -1 || getResourcesForSubId(activeDataSubscriptionId).getInteger(17694856) == 1) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getActiveDataSubscriptionId() {
        return SubscriptionManager.getActiveDataSubscriptionId();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Resources getResourcesForSubId(int i) {
        return SubscriptionManager.getResourcesForSubId(this.mContext, i);
    }

    private boolean avoidBadWifiCurrentSettings() {
        return "1".equals(Settings.Global.getString(this.mContext.getContentResolver(), "network_avoid_bad_wifi"));
    }
}
