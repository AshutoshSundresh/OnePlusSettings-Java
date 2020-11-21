package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.UserManager;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.fuelgauge.AdvancedPowerUsageDetail;
import com.android.settings.fuelgauge.BatteryEntry;
import com.android.settings.fuelgauge.BatteryStatsHelperLoader;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.Utils;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.fuelgauge.PowerWhitelistBackend;
import com.oneplus.settings.backgroundoptimize.AppBgOptimizeBridge;
import java.util.ArrayList;
import java.util.List;

public class AppBatteryPreferenceController extends BasePreferenceController implements LoaderManager.LoaderCallbacks<BatteryStatsHelper>, LifecycleObserver, OnResume, OnPause {
    private static final String KEY_BATTERY = "battery";
    private static PowerWhitelistBackend mPowerWhitelistBackend;
    BatteryStatsHelper mBatteryHelper;
    private String mBatteryPercent;
    BatteryUtils mBatteryUtils = BatteryUtils.getInstance(this.mContext);
    private final String mPackageName;
    private final AppInfoDashboardFragment mParent;
    private Preference mPreference;
    BatterySipper mSipper;

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

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<BatteryStatsHelper> loader) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppBatteryPreferenceController(Context context, AppInfoDashboardFragment appInfoDashboardFragment, String str, Lifecycle lifecycle) {
        super(context, KEY_BATTERY);
        this.mParent = appInfoDashboardFragment;
        this.mPackageName = str;
        mPowerWhitelistBackend = PowerWhitelistBackend.getInstance(this.mContext);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!this.mContext.getResources().getBoolean(C0005R$bool.config_show_app_info_settings_battery) || !AppBgOptimizeBridge.needShown(this.mPackageName, mPowerWhitelistBackend)) ? 2 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = findPreference;
        findPreference.setEnabled(false);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_BATTERY.equals(preference.getKey())) {
            return false;
        }
        if (isBatteryStatsAvailable()) {
            BatteryEntry batteryEntry = new BatteryEntry(this.mContext, null, (UserManager) this.mContext.getSystemService("user"), this.mSipper);
            batteryEntry.defaultPackageName = this.mPackageName;
            AdvancedPowerUsageDetail.startBatteryDetailPage(this.mParent.getActivity(), this.mParent, this.mBatteryHelper, 0, batteryEntry, this.mBatteryPercent);
            return true;
        }
        AdvancedPowerUsageDetail.startBatteryDetailPage(this.mParent.getActivity(), this.mParent, this.mPackageName);
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mParent.getLoaderManager().restartLoader(4, Bundle.EMPTY, this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mParent.getLoaderManager().destroyLoader(4);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<BatteryStatsHelper> onCreateLoader(int i, Bundle bundle) {
        return new BatteryStatsHelperLoader(this.mContext);
    }

    public void onLoadFinished(Loader<BatteryStatsHelper> loader, BatteryStatsHelper batteryStatsHelper) {
        this.mBatteryHelper = batteryStatsHelper;
        PackageInfo packageInfo = this.mParent.getPackageInfo();
        if (packageInfo != null) {
            this.mSipper = findTargetSipper(batteryStatsHelper, packageInfo.applicationInfo.uid);
            if (this.mParent.getActivity() != null) {
                updateBattery();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateBattery() {
        this.mPreference.setEnabled(true);
        if (isBatteryStatsAvailable()) {
            int dischargeAmount = this.mBatteryHelper.getStats().getDischargeAmount(0);
            String formatPercentage = Utils.formatPercentage((int) this.mBatteryUtils.calculateBatteryPercent(this.mSipper.totalPowerMah, this.mBatteryHelper.getTotalPower(), this.mBatteryUtils.removeHiddenBatterySippers(new ArrayList(this.mBatteryHelper.getUsageList())), dischargeAmount));
            this.mBatteryPercent = formatPercentage;
            this.mPreference.setSummary(this.mContext.getString(C0017R$string.battery_summary, formatPercentage));
            return;
        }
        this.mPreference.setSummary(this.mContext.getString(C0017R$string.no_battery_summary));
    }

    /* access modifiers changed from: package-private */
    public boolean isBatteryStatsAvailable() {
        return (this.mBatteryHelper == null || this.mSipper == null) ? false : true;
    }

    /* access modifiers changed from: package-private */
    public BatterySipper findTargetSipper(BatteryStatsHelper batteryStatsHelper, int i) {
        List usageList = batteryStatsHelper.getUsageList();
        int size = usageList.size();
        for (int i2 = 0; i2 < size; i2++) {
            BatterySipper batterySipper = (BatterySipper) usageList.get(i2);
            if (batterySipper.getUid() == i) {
                return batterySipper;
            }
        }
        return null;
    }
}
