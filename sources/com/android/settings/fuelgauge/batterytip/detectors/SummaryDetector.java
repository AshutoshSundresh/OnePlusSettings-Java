package com.android.settings.fuelgauge.batterytip.detectors;

import com.android.settings.fuelgauge.batterytip.BatteryTipPolicy;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settings.fuelgauge.batterytip.tips.SummaryTip;

public class SummaryDetector {
    private long mAverageTimeMs;
    private BatteryTipPolicy mPolicy;

    public SummaryDetector(BatteryTipPolicy batteryTipPolicy, long j) {
        this.mPolicy = batteryTipPolicy;
        this.mAverageTimeMs = j;
    }

    public BatteryTip detect() {
        return new SummaryTip(this.mPolicy.summaryEnabled ? 0 : 2, this.mAverageTimeMs);
    }
}
