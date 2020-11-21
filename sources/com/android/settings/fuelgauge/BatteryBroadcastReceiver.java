package com.android.settings.fuelgauge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.settings.Utils;
import com.android.settings.homepage.contextualcards.slices.BatteryFixSlice;

public class BatteryBroadcastReceiver extends BroadcastReceiver {
    String mBatteryLevel;
    private OnBatteryChangedListener mBatteryListener;
    String mBatteryStatus;
    private Context mContext;

    public interface OnBatteryChangedListener {
        void onBatteryChanged(int i);
    }

    public BatteryBroadcastReceiver(Context context) {
        this.mContext = context;
    }

    public void onReceive(Context context, Intent intent) {
        updateBatteryStatus(intent, false);
    }

    public void setBatteryChangedListener(OnBatteryChangedListener onBatteryChangedListener) {
        this.mBatteryListener = onBatteryChangedListener;
    }

    public void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        updateBatteryStatus(this.mContext.registerReceiver(this, intentFilter), true);
    }

    public void unRegister() {
        this.mContext.unregisterReceiver(this);
    }

    private void updateBatteryStatus(Intent intent, boolean z) {
        if (!(intent == null || this.mBatteryListener == null)) {
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
                String batteryPercentage = Utils.getBatteryPercentage(intent);
                String batteryStatus = com.android.settingslib.Utils.getBatteryStatus(this.mContext, intent);
                if (z) {
                    this.mBatteryListener.onBatteryChanged(0);
                } else if (!batteryPercentage.equals(this.mBatteryLevel)) {
                    this.mBatteryListener.onBatteryChanged(1);
                } else if (!batteryStatus.equals(this.mBatteryStatus)) {
                    this.mBatteryListener.onBatteryChanged(3);
                }
                this.mBatteryLevel = batteryPercentage;
                this.mBatteryStatus = batteryStatus;
            } else if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(intent.getAction())) {
                this.mBatteryListener.onBatteryChanged(2);
            }
        }
        BatteryFixSlice.updateBatteryTipAvailabilityCache(this.mContext);
    }
}
