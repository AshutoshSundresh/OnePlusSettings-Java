package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryStats;
import android.os.SystemClock;
import com.android.internal.os.BatteryStatsHelper;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.utils.AsyncLoaderCompat;
import com.android.settingslib.utils.PowerUtil;
import java.util.ArrayList;
import java.util.List;

public class DebugEstimatesLoader extends AsyncLoaderCompat<List<BatteryInfo>> {
    private BatteryStatsHelper mStatsHelper;

    /* access modifiers changed from: protected */
    public void onDiscardResult(List<BatteryInfo> list) {
    }

    public DebugEstimatesLoader(Context context, BatteryStatsHelper batteryStatsHelper) {
        super(context);
        this.mStatsHelper = batteryStatsHelper;
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public List<BatteryInfo> loadInBackground() {
        Context context = getContext();
        PowerUsageFeatureProvider powerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
        long convertMsToUs = PowerUtil.convertMsToUs(SystemClock.elapsedRealtime());
        Intent registerReceiver = getContext().registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        BatteryStats stats = this.mStatsHelper.getStats();
        BatteryInfo batteryInfoOld = BatteryInfo.getBatteryInfoOld(getContext(), registerReceiver, stats, convertMsToUs, false);
        Estimate enhancedBatteryPrediction = powerUsageFeatureProvider.getEnhancedBatteryPrediction(context);
        if (enhancedBatteryPrediction == null) {
            enhancedBatteryPrediction = new Estimate(0, false, -1);
        }
        BatteryInfo batteryInfo = BatteryInfo.getBatteryInfo(getContext(), registerReceiver, stats, enhancedBatteryPrediction, convertMsToUs, false);
        ArrayList arrayList = new ArrayList();
        arrayList.add(batteryInfoOld);
        arrayList.add(batteryInfo);
        return arrayList;
    }
}
