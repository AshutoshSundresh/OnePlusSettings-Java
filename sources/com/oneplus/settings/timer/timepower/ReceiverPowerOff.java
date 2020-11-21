package com.oneplus.settings.timer.timepower;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class ReceiverPowerOff extends BroadcastReceiver {
    private static boolean mIsCalling = false;
    private static boolean mIsPoweroff = false;
    private Context mContext = null;
    private final Handler mHandler = new Handler();
    private final Runnable mPowerOffPromptRunnable = new Runnable() {
        /* class com.oneplus.settings.timer.timepower.ReceiverPowerOff.AnonymousClass1 */

        public void run() {
            if (ReceiverPowerOff.this.mContext == null || ReceiverPowerOff.this.mPoweroffAction == null) {
                Log.e("ReceiverPowerOff", "mContext = " + ReceiverPowerOff.this.mContext + " mPoweroffAction = " + ReceiverPowerOff.this.mPoweroffAction);
                return;
            }
            ComponentName componentName = ((ActivityManager) ReceiverPowerOff.this.mContext.getSystemService("activity")).getRunningTasks(1).get(0).topActivity;
            String packageName = componentName.getPackageName();
            String className = componentName.getClassName();
            Log.d("ReceiverPowerOff", "pkg:" + packageName);
            Log.d("ReceiverPowerOff", "cls:" + className);
            if (!packageName.equals("com.android.incallui") || !className.equals("com.android.incallui.OppoInCallActivity")) {
                Intent intent = new Intent(ReceiverPowerOff.this.mPoweroffAction);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setFlags(268435456);
                ReceiverPowerOff.this.mContext.startActivity(intent);
                return;
            }
            ReceiverPowerOff.this.mHandler.removeCallbacks(ReceiverPowerOff.this.mPowerOffPromptRunnable);
            ReceiverPowerOff.this.mHandler.postDelayed(ReceiverPowerOff.this.mPowerOffPromptRunnable, 500);
        }
    };
    private String mPoweroffAction = null;

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            String str = ((KeyguardManager) context.getSystemService("keyguard")).inKeyguardRestrictedInputMode() ? "com.android.settings.ShutdownWhenLocked" : "com.android.settings.Shutdown";
            if (action.equals("com.android.settings.POWER_OFF")) {
                if (System.currentTimeMillis() - intent.getExtras().getLong("trigger_time") < 60000) {
                    if (mIsCalling) {
                        mIsPoweroff = true;
                        return;
                    }
                    Toast.makeText(context, "phone want to turn off now !", 0).show();
                    if (isUsingTheme(context)) {
                        Log.i("ReceiverPowerOff", "time to shutdown when changing theme, so delay shutdown");
                        rememberShutdownRequestMissed(context);
                        return;
                    }
                    Intent intent2 = new Intent(str);
                    intent2.setFlags(268435456);
                    context.startActivity(intent2);
                }
            } else if (action.equals("android.intent.action.PHONE_STATE")) {
                String stringExtra = intent.getStringExtra("state");
                if (stringExtra.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK) || stringExtra.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                    mIsCalling = true;
                }
                if (stringExtra.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {
                    mIsCalling = false;
                    if (mIsPoweroff) {
                        mIsPoweroff = false;
                        this.mContext = context;
                        this.mPoweroffAction = str;
                        this.mHandler.removeCallbacks(this.mPowerOffPromptRunnable);
                        this.mHandler.postDelayed(this.mPowerOffPromptRunnable, 500);
                    }
                }
            }
        }
    }

    private boolean isUsingTheme(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "oem_is_using_theme", 0) == 1;
    }

    private void rememberShutdownRequestMissed(Context context) {
        Settings.System.putInt(context.getContentResolver(), "oem_shutdown_request_missed", 1);
    }
}
