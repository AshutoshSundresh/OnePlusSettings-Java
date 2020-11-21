package com.oneplus.settings.timer.timepower;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;

public class OPShutdownActivity extends Activity {
    public static CountDownTimer sCountDownTimer;
    private String mMessage;
    private int mSecondsCountdown;
    private TelephonyManager mTelephonyManager;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("ShutdownActivity", "screen is on ? ----- " + ((PowerManager) getSystemService("power")).isScreenOn());
        this.mTelephonyManager = (TelephonyManager) getSystemService("phone");
        getWindow().addFlags(4718592);
        if (bundle == null) {
            this.mSecondsCountdown = 11;
        } else {
            this.mSecondsCountdown = bundle.getInt("lefttime");
            this.mMessage = bundle.getString("message");
        }
        sCountDownTimer = new CountDownTimer((long) (this.mSecondsCountdown * 1000), 1000) {
            /* class com.oneplus.settings.timer.timepower.OPShutdownActivity.AnonymousClass1 */

            public void onTick(long j) {
                long j2 = j / 1000;
                OPShutdownActivity.this.mSecondsCountdown = (int) j2;
                if (OPShutdownActivity.this.mSecondsCountdown > 1) {
                    OPShutdownActivity oPShutdownActivity = OPShutdownActivity.this;
                    oPShutdownActivity.mMessage = oPShutdownActivity.getString(C0017R$string.oneplus_shutdown_message, new Object[]{Integer.valueOf(oPShutdownActivity.mSecondsCountdown)});
                } else {
                    OPShutdownActivity oPShutdownActivity2 = OPShutdownActivity.this;
                    oPShutdownActivity2.mMessage = oPShutdownActivity2.getString(C0017R$string.oneplus_shutdown_message_second, new Object[]{Integer.valueOf(oPShutdownActivity2.mSecondsCountdown)});
                }
                Log.d("ShutdownActivity", "showDialog time = " + j2);
                OPShutdownActivity.this.showDialog(1);
            }

            public void onFinish() {
                if (OPShutdownActivity.this.mTelephonyManager.getCallState() != 0) {
                    Log.d("ShutdownActivity", "phone is incall, countdown end");
                    OPShutdownActivity.this.finish();
                    return;
                }
                Log.d("ShutdownActivity", "count down timer arrived, shutdown phone");
                OPShutdownActivity.this.fireShutDown();
                OPShutdownActivity.sCountDownTimer = null;
            }
        };
        Log.d("ShutdownActivity", "ShutdonwActivity onCreate");
        CountDownTimer countDownTimer = sCountDownTimer;
        if (countDownTimer == null) {
            finish();
        } else {
            countDownTimer.start();
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("lefttime", this.mSecondsCountdown);
        bundle.putString("message", this.mMessage);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void cancelCountDownTimer() {
        if (sCountDownTimer != null) {
            Log.d("ShutdownActivity", "cancel sCountDownTimer");
            sCountDownTimer.cancel();
            sCountDownTimer = null;
        }
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int i) {
        Log.d("ShutdownActivity", "onCreateDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setIcon(17301543);
        builder.setTitle("power off");
        builder.setMessage(this.mMessage);
        builder.setPositiveButton(17039379, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.timer.timepower.OPShutdownActivity.AnonymousClass3 */

            public void onClick(DialogInterface dialogInterface, int i) {
                OPShutdownActivity.this.cancelCountDownTimer();
                OPShutdownActivity.this.fireShutDown();
            }
        });
        builder.setNegativeButton(17039369, new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.timer.timepower.OPShutdownActivity.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                OPShutdownActivity.this.cancelCountDownTimer();
                OPShutdownActivity.this.finish();
            }
        });
        return builder.create();
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialog(int i, Dialog dialog) {
        ((AlertDialog) dialog).setMessage(this.mMessage);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void fireShutDown() {
        if (!SystemProperties.getBoolean("sys.debug.watchdog", false)) {
            Intent intent = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
            intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
            intent.setFlags(8388608);
            intent.setFlags(268435456);
            startActivity(intent);
        }
    }
}
