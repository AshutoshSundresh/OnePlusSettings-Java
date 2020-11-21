package com.android.settings.fuelgauge;

import android.content.Context;
import com.android.internal.os.BatteryStatsHelper;
import com.android.settingslib.utils.AsyncLoaderCompat;

public class BatteryInfoLoader extends AsyncLoaderCompat<BatteryInfo> {
    BatteryUtils batteryUtils;
    BatteryStatsHelper mStatsHelper;

    /* access modifiers changed from: protected */
    public void onDiscardResult(BatteryInfo batteryInfo) {
    }

    public BatteryInfoLoader(Context context, BatteryStatsHelper batteryStatsHelper) {
        super(context);
        this.mStatsHelper = batteryStatsHelper;
        this.batteryUtils = BatteryUtils.getInstance(context);
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public BatteryInfo loadInBackground() {
        return this.batteryUtils.getBatteryInfo(this.mStatsHelper, "BatteryInfoLoader");
    }
}
