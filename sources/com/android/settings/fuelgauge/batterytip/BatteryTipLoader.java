package com.android.settings.fuelgauge.batterytip;

import android.content.Context;
import com.android.internal.os.BatteryStatsHelper;
import com.android.settings.fuelgauge.BatteryInfo;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.fuelgauge.batterytip.detectors.EarlyWarningDetector;
import com.android.settings.fuelgauge.batterytip.detectors.HighUsageDetector;
import com.android.settings.fuelgauge.batterytip.detectors.LowBatteryDetector;
import com.android.settings.fuelgauge.batterytip.detectors.SmartBatteryDetector;
import com.android.settings.fuelgauge.batterytip.detectors.SummaryDetector;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settingslib.utils.AsyncLoaderCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BatteryTipLoader extends AsyncLoaderCompat<List<BatteryTip>> {
    private BatteryStatsHelper mBatteryStatsHelper;
    BatteryUtils mBatteryUtils;

    /* access modifiers changed from: protected */
    public void onDiscardResult(List<BatteryTip> list) {
    }

    public BatteryTipLoader(Context context, BatteryStatsHelper batteryStatsHelper) {
        super(context);
        this.mBatteryStatsHelper = batteryStatsHelper;
        this.mBatteryUtils = BatteryUtils.getInstance(context);
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public List<BatteryTip> loadInBackground() {
        ArrayList arrayList = new ArrayList();
        BatteryTipPolicy batteryTipPolicy = new BatteryTipPolicy(getContext());
        BatteryInfo batteryInfo = this.mBatteryUtils.getBatteryInfo(this.mBatteryStatsHelper, "BatteryTipLoader");
        Context context = getContext();
        arrayList.add(new LowBatteryDetector(context, batteryTipPolicy, batteryInfo).detect());
        arrayList.add(new HighUsageDetector(context, batteryTipPolicy, this.mBatteryStatsHelper, batteryInfo.discharging).detect());
        arrayList.add(new SmartBatteryDetector(batteryTipPolicy, context.getContentResolver()).detect());
        arrayList.add(new EarlyWarningDetector(batteryTipPolicy, context).detect());
        arrayList.add(new SummaryDetector(batteryTipPolicy, batteryInfo.averageTimeToDischarge).detect());
        Collections.sort(arrayList);
        return arrayList;
    }
}
