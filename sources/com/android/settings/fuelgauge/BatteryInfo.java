package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.BatteryStats;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseIntArray;
import com.android.internal.os.BatteryStatsHelper;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.UsageView;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.utils.PowerUtil;
import com.android.settingslib.utils.StringUtil;
import com.oneplus.settings.utils.OPUtils;

public class BatteryInfo {
    public long averageTimeToDischarge = -1;
    public int batteryLevel;
    public String batteryPercentString;
    public CharSequence chargeLabel;
    public boolean discharging = true;
    private boolean mCharging;
    private BatteryStats mStats;
    public CharSequence remainingLabel;
    public long remainingTimeUs = 0;
    public String statusLabel;
    public String suggestionLabel;
    private long timePeriod;

    public interface BatteryDataParser {
        void onDataGap();

        void onDataPoint(long j, BatteryStats.HistoryItem historyItem);

        void onParsingDone();

        void onParsingStarted(long j, long j2);
    }

    public interface Callback {
        void onBatteryInfoLoaded(BatteryInfo batteryInfo);
    }

    public void bindHistory(final UsageView usageView, BatteryDataParser... batteryDataParserArr) {
        final Context context = usageView.getContext();
        AnonymousClass1 r1 = new BatteryDataParser() {
            /* class com.android.settings.fuelgauge.BatteryInfo.AnonymousClass1 */
            byte lastLevel;
            int lastTime = -1;
            SparseIntArray points = new SparseIntArray();
            long startTime;

            @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
            public void onParsingStarted(long j, long j2) {
                this.startTime = j;
                BatteryInfo.this.timePeriod = j2 - j;
                usageView.clearPaths();
                usageView.configureGraph((int) BatteryInfo.this.timePeriod, 100);
            }

            @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
            public void onDataPoint(long j, BatteryStats.HistoryItem historyItem) {
                int i = (int) j;
                this.lastTime = i;
                byte b = historyItem.batteryLevel;
                this.lastLevel = b;
                this.points.put(i, b);
            }

            @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
            public void onDataGap() {
                if (this.points.size() > 1) {
                    usageView.addPath(this.points);
                }
                this.points.clear();
            }

            @Override // com.android.settings.fuelgauge.BatteryInfo.BatteryDataParser
            public void onParsingDone() {
                onDataGap();
                if (BatteryInfo.this.remainingTimeUs != 0) {
                    PowerUsageFeatureProvider powerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
                    if (BatteryInfo.this.mCharging || !powerUsageFeatureProvider.isEnhancedBatteryPredictionEnabled(context)) {
                        int i = this.lastTime;
                        if (i >= 0) {
                            this.points.put(i, this.lastLevel);
                            this.points.put((int) (BatteryInfo.this.timePeriod + PowerUtil.convertUsToMs(BatteryInfo.this.remainingTimeUs)), BatteryInfo.this.mCharging ? 100 : 0);
                        }
                    } else {
                        this.points = powerUsageFeatureProvider.getEnhancedBatteryPredictionCurve(context, this.startTime);
                    }
                }
                SparseIntArray sparseIntArray = this.points;
                if (sparseIntArray != null && sparseIntArray.size() > 0) {
                    SparseIntArray sparseIntArray2 = this.points;
                    usageView.configureGraph(sparseIntArray2.keyAt(sparseIntArray2.size() - 1), 100);
                    usageView.addProjectedPath(this.points);
                }
            }
        };
        BatteryDataParser[] batteryDataParserArr2 = new BatteryDataParser[(batteryDataParserArr.length + 1)];
        for (int i = 0; i < batteryDataParserArr.length; i++) {
            batteryDataParserArr2[i] = batteryDataParserArr[i];
        }
        batteryDataParserArr2[batteryDataParserArr.length] = r1;
        parse(this.mStats, batteryDataParserArr2);
        String string = context.getString(R$string.charge_length_format, Formatter.formatShortElapsedTime(context, this.timePeriod));
        long j = this.remainingTimeUs;
        usageView.setBottomLabels(new CharSequence[]{string, j != 0 ? context.getString(R$string.remaining_length_format, Formatter.formatShortElapsedTime(context, j / 1000)) : ""});
    }

    public static void getBatteryInfo(Context context, Callback callback, boolean z) {
        getBatteryInfo(context, callback, null, z);
    }

    public static void getBatteryInfo(final Context context, final Callback callback, final BatteryStatsHelper batteryStatsHelper, final boolean z) {
        new AsyncTask<Void, Void, BatteryInfo>() {
            /* class com.android.settings.fuelgauge.BatteryInfo.AnonymousClass2 */

            /* access modifiers changed from: protected */
            public BatteryInfo doInBackground(Void... voidArr) {
                return BatteryInfo.getBatteryInfo(context, batteryStatsHelper, z);
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(BatteryInfo batteryInfo) {
                long currentTimeMillis = System.currentTimeMillis();
                callback.onBatteryInfoLoaded(batteryInfo);
                BatteryUtils.logRuntime("BatteryInfo", "time for callback", currentTimeMillis);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    public static BatteryInfo getBatteryInfo(Context context, BatteryStatsHelper batteryStatsHelper, boolean z) {
        BatteryStats batteryStats;
        Estimate enhancedBatteryPrediction;
        long currentTimeMillis = System.currentTimeMillis();
        boolean z2 = true;
        if (batteryStatsHelper == null) {
            BatteryStatsHelper batteryStatsHelper2 = new BatteryStatsHelper(context, true);
            batteryStatsHelper2.create((Bundle) null);
            batteryStats = batteryStatsHelper2.getStats();
        } else {
            batteryStats = batteryStatsHelper.getStats();
        }
        BatteryUtils.logRuntime("BatteryInfo", "time for getStats", currentTimeMillis);
        long currentTimeMillis2 = System.currentTimeMillis();
        PowerUsageFeatureProvider powerUsageFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
        long convertMsToUs = PowerUtil.convertMsToUs(SystemClock.elapsedRealtime());
        Intent registerReceiver = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (registerReceiver.getIntExtra("plugged", -1) != 0) {
            z2 = false;
        }
        if (!z2 || powerUsageFeatureProvider == null || !powerUsageFeatureProvider.isEnhancedBatteryPredictionEnabled(context) || (enhancedBatteryPrediction = powerUsageFeatureProvider.getEnhancedBatteryPrediction(context)) == null) {
            Estimate estimate = new Estimate(PowerUtil.convertUsToMs(z2 ? batteryStats.computeBatteryTimeRemaining(convertMsToUs) : 0), false, -1);
            BatteryUtils.logRuntime("BatteryInfo", "time for regular BatteryInfo", currentTimeMillis2);
            return getBatteryInfo(context, registerReceiver, batteryStats, estimate, convertMsToUs, z);
        }
        Estimate.storeCachedEstimate(context, enhancedBatteryPrediction);
        BatteryUtils.logRuntime("BatteryInfo", "time for enhanced BatteryInfo", currentTimeMillis2);
        return getBatteryInfo(context, registerReceiver, batteryStats, enhancedBatteryPrediction, convertMsToUs, z);
    }

    public static BatteryInfo getBatteryInfoOld(Context context, Intent intent, BatteryStats batteryStats, long j, boolean z) {
        return getBatteryInfo(context, intent, batteryStats, new Estimate(PowerUtil.convertUsToMs(batteryStats.computeBatteryTimeRemaining(j)), false, -1), j, z);
    }

    public static BatteryInfo getBatteryInfo(Context context, Intent intent, BatteryStats batteryStats, Estimate estimate, long j, boolean z) {
        long currentTimeMillis = System.currentTimeMillis();
        BatteryInfo batteryInfo = new BatteryInfo();
        batteryInfo.mStats = batteryStats;
        int batteryLevel2 = Utils.getBatteryLevel(intent);
        batteryInfo.batteryLevel = batteryLevel2;
        batteryInfo.batteryPercentString = Utils.formatPercentage(batteryLevel2);
        boolean z2 = false;
        if (intent.getIntExtra("plugged", 0) != 0) {
            z2 = true;
        }
        batteryInfo.mCharging = z2;
        batteryInfo.averageTimeToDischarge = estimate.getAverageDischargeTime();
        batteryInfo.statusLabel = Utils.getBatteryStatus(context, intent);
        if (!batteryInfo.mCharging) {
            updateBatteryInfoDischarging(context, z, estimate, batteryInfo);
        } else {
            updateBatteryInfoCharging(context, intent, batteryStats, j, batteryInfo);
        }
        BatteryUtils.logRuntime("BatteryInfo", "time for getBatteryInfo", currentTimeMillis);
        return batteryInfo;
    }

    private static void updateBatteryInfoCharging(Context context, Intent intent, BatteryStats batteryStats, long j, BatteryInfo batteryInfo) {
        String str;
        Resources resources = context.getResources();
        long chargTimeToFull = getChargTimeToFull(intent, batteryStats, j);
        Log.d("BatteryInfo", "Settings BatteryInfo updateBatteryInfoCharging chargeTime:" + chargTimeToFull);
        int intExtra = intent.getIntExtra("status", 1);
        batteryInfo.discharging = false;
        batteryInfo.suggestionLabel = null;
        if (chargTimeToFull <= 0 || intExtra == 5) {
            String batteryStatus = Utils.getBatteryStatus(context, intent);
            batteryInfo.remainingLabel = null;
            if (batteryInfo.batteryLevel == 100) {
                str = batteryInfo.batteryPercentString;
            } else {
                str = resources.getString(R$string.power_charging, batteryInfo.batteryPercentString, batteryStatus.toLowerCase());
            }
            batteryInfo.chargeLabel = str;
            return;
        }
        batteryInfo.remainingTimeUs = chargTimeToFull;
        CharSequence formatElapsedTime = StringUtil.formatElapsedTime(context, (double) PowerUtil.convertUsToMs(chargTimeToFull), false);
        int i = R$string.power_charging_duration;
        batteryInfo.remainingLabel = context.getString(R$string.power_remaining_charging_duration_only, formatElapsedTime);
        batteryInfo.chargeLabel = context.getString(i, batteryInfo.batteryPercentString, formatElapsedTime);
        Log.d("BatteryInfo", "Settings BatteryInfo updateBatteryInfoCharging timeString:" + ((Object) formatElapsedTime));
        Log.d("BatteryInfo", "Settings BatteryInfo updateBatteryInfoCharging remainingLabel:" + ((Object) batteryInfo.remainingLabel));
        Log.d("BatteryInfo", "Settings BatteryInfo updateBatteryInfoCharging chargeLabel:" + ((Object) batteryInfo.chargeLabel));
    }

    private static long getChargTimeToFull(Intent intent, BatteryStats batteryStats, long j) {
        if (!OPUtils.isProductSwarpChargSupport()) {
            return batteryStats.computeChargeTimeRemaining(j);
        }
        Log.d("BatteryInfo", "Settings BatteryInfo TimeToFull fastcharge_status:" + intent.getIntExtra("fastcharge_status", 0));
        if (intent.getIntExtra("fastcharge_status", 0) == 0) {
            return batteryStats.computeChargeTimeRemaining(j);
        }
        long longExtra = intent.getLongExtra("estimate_time_to_full", 0);
        Log.d("BatteryInfo", "Settings BatteryInfo TimeToFull " + longExtra);
        return longExtra > 0 ? longExtra * 1000 * 1000 : longExtra;
    }

    private static void updateBatteryInfoDischarging(Context context, boolean z, Estimate estimate, BatteryInfo batteryInfo) {
        long convertMsToUs = PowerUtil.convertMsToUs(estimate.getEstimateMillis());
        if (convertMsToUs > 0) {
            batteryInfo.remainingTimeUs = convertMsToUs;
            boolean z2 = true;
            batteryInfo.remainingLabel = PowerUtil.getBatteryRemainingStringFormatted(context, PowerUtil.convertUsToMs(convertMsToUs), null, estimate.isBasedOnUsage() && !z);
            long convertUsToMs = PowerUtil.convertUsToMs(convertMsToUs);
            String str = batteryInfo.batteryPercentString;
            if (!estimate.isBasedOnUsage() || z) {
                z2 = false;
            }
            batteryInfo.chargeLabel = PowerUtil.getBatteryRemainingStringFormatted(context, convertUsToMs, str, z2);
            batteryInfo.suggestionLabel = PowerUtil.getBatteryTipStringFormatted(context, PowerUtil.convertUsToMs(convertMsToUs));
            return;
        }
        batteryInfo.remainingLabel = null;
        batteryInfo.suggestionLabel = null;
        batteryInfo.chargeLabel = batteryInfo.batteryPercentString;
    }

    public static void parse(BatteryStats batteryStats, BatteryDataParser... batteryDataParserArr) {
        long j;
        long j2;
        long j3;
        long j4;
        long j5;
        int i;
        char c;
        long j6;
        long j7 = 0;
        if (batteryStats.startIteratingHistoryLocked()) {
            BatteryStats.HistoryItem historyItem = new BatteryStats.HistoryItem();
            j5 = 0;
            j4 = 0;
            j3 = 0;
            j2 = 0;
            j = 0;
            boolean z = true;
            i = 0;
            int i2 = 0;
            while (batteryStats.getNextHistoryLocked(historyItem)) {
                i2++;
                if (z) {
                    j = historyItem.time;
                    z = false;
                }
                byte b = historyItem.cmd;
                if (b == 5 || b == 7) {
                    if (historyItem.currentTime > j3 + 15552000000L || historyItem.time < j + 300000) {
                        j5 = 0;
                    }
                    j3 = historyItem.currentTime;
                    long j8 = historyItem.time;
                    if (j5 == 0) {
                        j5 = j3 - (j8 - j);
                    }
                    j2 = j8;
                }
                if (historyItem.isDeltaData()) {
                    j4 = historyItem.time;
                    i = i2;
                } else {
                    i = i;
                }
            }
        } else {
            j5 = 0;
            j4 = 0;
            j3 = 0;
            j2 = 0;
            j = 0;
            i = 0;
        }
        batteryStats.finishIteratingHistoryLocked();
        long j9 = (j3 + j4) - j2;
        for (BatteryDataParser batteryDataParser : batteryDataParserArr) {
            batteryDataParser.onParsingStarted(j5, j9);
        }
        if (j9 > j5 && batteryStats.startIteratingHistoryLocked()) {
            BatteryStats.HistoryItem historyItem2 = new BatteryStats.HistoryItem();
            long j10 = 0;
            int i3 = 0;
            while (batteryStats.getNextHistoryLocked(historyItem2) && i3 < i) {
                if (historyItem2.isDeltaData()) {
                    long j11 = historyItem2.time;
                    j10 += j11 - j2;
                    long j12 = j10 - j5;
                    long j13 = j12 < j7 ? j7 : j12;
                    for (BatteryDataParser batteryDataParser2 : batteryDataParserArr) {
                        batteryDataParser2.onDataPoint(j13, historyItem2);
                    }
                    j2 = j11;
                    c = 7;
                } else {
                    byte b2 = historyItem2.cmd;
                    c = 7;
                    if (b2 == 5 || b2 == 7) {
                        j6 = historyItem2.currentTime;
                        if (j6 < j5) {
                            j6 = (historyItem2.time - j) + j5;
                        }
                        j2 = historyItem2.time;
                    } else {
                        j6 = j10;
                    }
                    byte b3 = historyItem2.cmd;
                    if (b3 != 6 && (b3 != 5 || Math.abs(j10 - j6) > 3600000)) {
                        for (BatteryDataParser batteryDataParser3 : batteryDataParserArr) {
                            batteryDataParser3.onDataGap();
                        }
                    }
                    j10 = j6;
                }
                i3++;
                j7 = 0;
            }
        }
        batteryStats.finishIteratingHistoryLocked();
        for (BatteryDataParser batteryDataParser4 : batteryDataParserArr) {
            batteryDataParser4.onParsingDone();
        }
    }
}
