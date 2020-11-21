package com.oneplus.security.network.calibrate;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.oneplus.security.network.operator.AccountDayLocalCache;
import com.oneplus.security.network.trafficinfo.NativeTrafficDataModel;
import com.oneplus.security.network.trafficinfo.TrafficDataModelInterface;
import com.oneplus.security.utils.LogUtils;
import com.oneplus.security.utils.TimeRegionUtils;

public class AutoSaveNativeTrafficUsageService extends IntentService {
    private TrafficDataModelInterface mNativeTrafficDataModel;

    public static void getAndSaveCurrentNativeTrafficTotalUsage(Context context, int i) {
        Intent intent = new Intent(context, AutoSaveNativeTrafficUsageService.class);
        intent.setAction("com.oneplus.security.network.calibrate.action.fetch_and_save");
        intent.putExtra("key_slot_id_to_save_native_total_usage", i);
        context.startService(intent);
    }

    public AutoSaveNativeTrafficUsageService() {
        super("AutoSaveNativeTrafficUsageService");
    }

    public void onCreate() {
        super.onCreate();
        this.mNativeTrafficDataModel = NativeTrafficDataModel.getTrafficModelInstance();
    }

    public void onDestroy() {
        super.onDestroy();
        this.mNativeTrafficDataModel.clearTrafficData();
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(Intent intent) {
        long j;
        if (intent != null && "com.oneplus.security.network.calibrate.action.fetch_and_save".equals(intent.getAction()) && this.mNativeTrafficDataModel != null) {
            int intExtra = intent.getIntExtra("key_slot_id_to_save_native_total_usage", -1);
            if (intExtra == -1) {
                Log.e("SaveNativeTrafficUsage", "invalid slot id offered, finish.");
            }
            long lastCalibrateTime = AutoCalibrateUtil.getLastCalibrateTime(this, intExtra);
            if (lastCalibrateTime == -1) {
                long[] regionTime = TimeRegionUtils.getRegionTime(AccountDayLocalCache.getAccountDay(this, intExtra), System.currentTimeMillis());
                AutoCalibrateUtil.setLastCalibrateTime(this, regionTime[0], intExtra, false);
                long j2 = regionTime[0];
                LogUtils.d("SaveNativeTrafficUsage", "lastCalibrateTime is invalid time,so we set the start of this datausage cycle as lastCalibrateTime:" + regionTime[0]);
                j = j2;
            } else {
                j = lastCalibrateTime;
            }
            long nativeDataUsageWithinSpecificTime = this.mNativeTrafficDataModel.getNativeDataUsageWithinSpecificTime(intExtra, 0, j);
            AutoCalibrateUtil.saveNativeTotalUsageWhenLastCalibrated(this, intExtra, nativeDataUsageWithinSpecificTime);
            LogUtils.d("SaveNativeTrafficUsage", "saveNativeTotalUsageWhenLastCalibrated for slotid:" + intExtra + ",value:" + nativeDataUsageWithinSpecificTime + ",lastCalibrateTime:" + j);
        }
    }
}
