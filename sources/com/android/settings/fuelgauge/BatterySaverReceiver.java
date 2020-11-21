package com.android.settings.fuelgauge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BatterySaverReceiver extends BroadcastReceiver {
    private BatterySaverListener mBatterySaverListener;
    private Context mContext;
    private boolean mRegistered;

    public interface BatterySaverListener {
        void onBatteryChanged(boolean z);

        void onPowerSaveModeChanged();
    }

    public BatterySaverReceiver(Context context) {
        this.mContext = context;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(action)) {
            BatterySaverListener batterySaverListener = this.mBatterySaverListener;
            if (batterySaverListener != null) {
                batterySaverListener.onPowerSaveModeChanged();
            }
        } else if ("android.intent.action.BATTERY_CHANGED".equals(action) && this.mBatterySaverListener != null) {
            boolean z = false;
            if (intent.getIntExtra("plugged", 0) != 0) {
                z = true;
            }
            this.mBatterySaverListener.onBatteryChanged(z);
        }
    }

    public void setListening(boolean z) {
        if (z && !this.mRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
            intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
            this.mContext.registerReceiver(this, intentFilter);
            this.mRegistered = true;
        } else if (!z && this.mRegistered) {
            this.mContext.unregisterReceiver(this);
            this.mRegistered = false;
        }
    }

    public void setBatterySaverListener(BatterySaverListener batterySaverListener) {
        this.mBatterySaverListener = batterySaverListener;
    }
}
