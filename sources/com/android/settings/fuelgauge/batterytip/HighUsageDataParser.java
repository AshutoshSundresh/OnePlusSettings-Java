package com.android.settings.fuelgauge.batterytip;

import android.os.BatteryStats;
import com.android.settings.fuelgauge.BatteryInfo;

public class HighUsageDataParser implements BatteryInfo.BatteryDataParser {
    private int mBatteryDrain;
    private byte mEndBatteryLevel;
    private long mEndTimeMs;
    private byte mLastPeriodBatteryLevel;
    private int mThreshold;
    private final long mTimePeriodMs;

    @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
    public void onDataGap() {
    }

    public HighUsageDataParser(long j, int i) {
        this.mTimePeriodMs = j;
        this.mThreshold = i;
    }

    @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
    public void onParsingStarted(long j, long j2) {
        this.mEndTimeMs = j2;
    }

    @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
    public void onDataPoint(long j, BatteryStats.HistoryItem historyItem) {
        if (j == 0 || historyItem.currentTime <= this.mEndTimeMs - this.mTimePeriodMs) {
            this.mLastPeriodBatteryLevel = historyItem.batteryLevel;
        }
        this.mEndBatteryLevel = historyItem.batteryLevel;
    }

    @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
    public void onParsingDone() {
        this.mBatteryDrain = this.mLastPeriodBatteryLevel - this.mEndBatteryLevel;
    }

    public boolean isDeviceHeavilyUsed() {
        return this.mBatteryDrain > this.mThreshold;
    }
}
