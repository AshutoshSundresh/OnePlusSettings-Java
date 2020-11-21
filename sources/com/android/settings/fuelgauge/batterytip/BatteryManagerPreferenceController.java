package com.android.settings.fuelgauge.batterytip;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.fuelgauge.PowerUsageFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;

public class BatteryManagerPreferenceController extends BasePreferenceController {
    private static final String KEY_BATTERY_MANAGER = "smart_battery_manager";
    private static final int ON = 1;
    private AppOpsManager mAppOpsManager;
    private PowerUsageFeatureProvider mPowerUsageFeatureProvider;
    private UserManager mUserManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
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

    public BatteryManagerPreferenceController(Context context) {
        super(context, KEY_BATTERY_MANAGER);
        this.mPowerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        int size = BatteryTipUtils.getRestrictedAppsList(this.mAppOpsManager, this.mUserManager).size();
        boolean z = true;
        if (Settings.Global.getInt(this.mContext.getContentResolver(), this.mPowerUsageFeatureProvider.isSmartBatterySupported() ? "adaptive_battery_management_enabled" : "app_auto_restriction_enabled", 1) != 1) {
            z = false;
        }
        updateSummary(preference, z, size);
    }

    /* access modifiers changed from: package-private */
    public void updateSummary(Preference preference, boolean z, int i) {
        if (i > 0) {
            preference.setSummary(this.mContext.getResources().getQuantityString(C0015R$plurals.battery_manager_app_restricted, i, Integer.valueOf(i)));
        } else if (z) {
            preference.setSummary(C0017R$string.battery_manager_on);
        } else {
            preference.setSummary(C0017R$string.battery_manager_off);
        }
    }
}
