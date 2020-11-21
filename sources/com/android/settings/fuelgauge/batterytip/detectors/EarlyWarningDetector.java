package com.android.settings.fuelgauge.batterytip.detectors;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PowerManager;
import com.android.settings.fuelgauge.PowerUsageFeatureProvider;
import com.android.settings.fuelgauge.batterytip.BatteryTipPolicy;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settings.fuelgauge.batterytip.tips.EarlyWarningTip;
import com.android.settings.overlay.FeatureFactory;

public class EarlyWarningDetector {
    private Context mContext;
    private BatteryTipPolicy mPolicy;
    private PowerManager mPowerManager;
    private PowerUsageFeatureProvider mPowerUsageFeatureProvider;

    public EarlyWarningDetector(BatteryTipPolicy batteryTipPolicy, Context context) {
        this.mPolicy = batteryTipPolicy;
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        this.mContext = context;
        this.mPowerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
    }

    public BatteryTip detect() {
        int i = 1;
        boolean z = this.mContext.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("plugged", -1) == 0;
        boolean isPowerSaveMode = this.mPowerManager.isPowerSaveMode();
        boolean z2 = this.mPowerUsageFeatureProvider.getEarlyWarningSignal(this.mContext, EarlyWarningDetector.class.getName()) || this.mPolicy.testBatterySaverTip;
        if (!isPowerSaveMode) {
            i = (!this.mPolicy.batterySaverTipEnabled || !z || !z2) ? 2 : 0;
        }
        return new EarlyWarningTip(i, isPowerSaveMode);
    }
}
