package com.android.settings.fuelgauge.batterytip.detectors;

import android.content.Context;
import android.os.BatteryStats;
import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;
import com.android.settings.fuelgauge.BatteryInfo;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.fuelgauge.batterytip.AppInfo;
import com.android.settings.fuelgauge.batterytip.BatteryTipPolicy;
import com.android.settings.fuelgauge.batterytip.HighUsageDataParser;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settings.fuelgauge.batterytip.tips.HighUsageTip;
import com.oneplus.settings.OPMemberController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HighUsageDetector {
    private BatteryStatsHelper mBatteryStatsHelper;
    BatteryUtils mBatteryUtils;
    HighUsageDataParser mDataParser;
    boolean mDischarging;
    private List<AppInfo> mHighUsageAppList = new ArrayList();
    private BatteryTipPolicy mPolicy;

    public HighUsageDetector(Context context, BatteryTipPolicy batteryTipPolicy, BatteryStatsHelper batteryStatsHelper, boolean z) {
        this.mPolicy = batteryTipPolicy;
        this.mBatteryStatsHelper = batteryStatsHelper;
        this.mBatteryUtils = BatteryUtils.getInstance(context);
        BatteryTipPolicy batteryTipPolicy2 = this.mPolicy;
        this.mDataParser = new HighUsageDataParser(batteryTipPolicy2.highUsagePeriodMs, batteryTipPolicy2.highUsageBatteryDraining);
        this.mDischarging = z;
    }

    public BatteryTip detect() {
        long calculateLastFullChargeTime = this.mBatteryUtils.calculateLastFullChargeTime(this.mBatteryStatsHelper, System.currentTimeMillis());
        if (this.mPolicy.highUsageEnabled && this.mDischarging) {
            parseBatteryData();
            if (this.mDataParser.isDeviceHeavilyUsed() || this.mPolicy.testHighUsageTip) {
                BatteryStats stats = this.mBatteryStatsHelper.getStats();
                ArrayList<BatterySipper> arrayList = new ArrayList(this.mBatteryStatsHelper.getUsageList());
                double totalPower = this.mBatteryStatsHelper.getTotalPower();
                int i = 0;
                if (stats != null) {
                    i = stats.getDischargeAmount(0);
                }
                Collections.sort(arrayList, $$Lambda$HighUsageDetector$28BD4HACLyHurD4PO4rsFVqsaMI.INSTANCE);
                for (BatterySipper batterySipper : arrayList) {
                    if (this.mBatteryUtils.calculateBatteryPercent(batterySipper.totalSmearedPowerMah, totalPower, 0.0d, i) + 0.5d >= 1.0d && !this.mBatteryUtils.shouldHideSipper(batterySipper)) {
                        List<AppInfo> list = this.mHighUsageAppList;
                        AppInfo.Builder builder = new AppInfo.Builder();
                        builder.setUid(batterySipper.getUid());
                        builder.setPackageName(this.mBatteryUtils.getPackageName(batterySipper.getUid()));
                        list.add(builder.build());
                        if (this.mHighUsageAppList.size() >= this.mPolicy.highUsageAppCount) {
                            break;
                        }
                    }
                }
                if (this.mPolicy.testHighUsageTip && this.mHighUsageAppList.isEmpty()) {
                    List<AppInfo> list2 = this.mHighUsageAppList;
                    AppInfo.Builder builder2 = new AppInfo.Builder();
                    builder2.setPackageName(OPMemberController.PACKAGE_NAME);
                    builder2.setScreenOnTimeMs(TimeUnit.HOURS.toMillis(3));
                    list2.add(builder2.build());
                }
            }
        }
        return new HighUsageTip(calculateLastFullChargeTime, this.mHighUsageAppList);
    }

    /* access modifiers changed from: package-private */
    public void parseBatteryData() {
        BatteryInfo.parse(this.mBatteryStatsHelper.getStats(), this.mDataParser);
    }
}
