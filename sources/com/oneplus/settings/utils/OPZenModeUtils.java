package com.oneplus.settings.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OPZenModeUtils {
    private static final Long IGNORE_TIME_VALUE = 10000L;
    private static OPZenModeUtils mOPZenModeUtils;
    private Context mContext;
    private Handler mHandler;
    private Runnable mRun;
    private SharedPreferences mSharedPreferences = null;
    private int mZenMode = 0;

    public OPZenModeUtils(Context context) {
        new Date();
        this.mHandler = new Handler();
        this.mRun = new Runnable() {
            /* class com.oneplus.settings.utils.OPZenModeUtils.AnonymousClass1 */

            public void run() {
                OPZenModeUtils.this.sendAppTracker();
            }
        };
        this.mContext = context;
    }

    public static OPZenModeUtils getInstance(Context context) {
        if (mOPZenModeUtils == null) {
            mOPZenModeUtils = new OPZenModeUtils(context);
        }
        return mOPZenModeUtils;
    }

    public void sendAppTrackerDelay() {
        this.mHandler.removeCallbacks(this.mRun);
        this.mHandler.postDelayed(this.mRun, IGNORE_TIME_VALUE.longValue());
    }

    public void sendAppTracker() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences("App_Tracker", 0);
        this.mSharedPreferences = sharedPreferences;
        SharedPreferences.Editor edit = sharedPreferences.edit();
        String format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss").format(new Date());
        int zenMode = NotificationManager.from(this.mContext).getZenMode();
        this.mZenMode = zenMode;
        if (zenMode == 3) {
            OPUtils.sendAppTracker("zen_mode_alarms", format);
        } else if (zenMode == 1) {
            OPUtils.sendAppTracker("zen_mode_important_interruptions", format);
        } else if (zenMode == 0) {
            OPUtils.sendAppTracker("zen_mode_off", format);
        }
        edit.putInt("zen_mode", this.mZenMode);
        edit.commit();
    }
}
