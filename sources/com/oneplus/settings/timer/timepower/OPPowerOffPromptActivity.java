package com.oneplus.settings.timer.timepower;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import com.android.settings.C0017R$string;
import com.oneplus.settings.ui.OPTimerDialog;

public class OPPowerOffPromptActivity extends Activity {
    private OPTimerDialog alertDialog;
    private Handler mHandler = new Handler() {
        /* class com.oneplus.settings.timer.timepower.OPPowerOffPromptActivity.AnonymousClass3 */

        public void handleMessage(Message message) {
            if (OPPowerOffPromptActivity.this.alertDialog != null) {
                Button nButton = OPPowerOffPromptActivity.this.alertDialog.getNButton();
                Button pButton = OPPowerOffPromptActivity.this.alertDialog.getPButton();
                int i = message.what;
                if (i != 1) {
                    if (i != 2) {
                        if (i == 1000) {
                            boolean z = OPPowerOffPromptActivity.this.mResume;
                        }
                    } else if (OPPowerOffPromptActivity.this.mNegativeCount > 0) {
                        OPPowerOffPromptActivity.access$410(OPPowerOffPromptActivity.this);
                        if (nButton != null) {
                            nButton.setText(OPPowerOffPromptActivity.this.alertDialog.getTimeText((String) nButton.getText(), OPPowerOffPromptActivity.this.mNegativeCount));
                        }
                        OPPowerOffPromptActivity.this.mHandler.sendEmptyMessageDelayed(2, 1000);
                    } else if (nButton == null) {
                    } else {
                        if (nButton.isEnabled()) {
                            nButton.performClick();
                        } else {
                            nButton.setEnabled(true);
                        }
                    }
                } else if (OPPowerOffPromptActivity.this.mPositiveCount > 0) {
                    OPPowerOffPromptActivity.access$610(OPPowerOffPromptActivity.this);
                    if (pButton != null) {
                        OPPowerOffPromptActivity.this.alertDialog.setMessage(String.format(OPPowerOffPromptActivity.this.getResources().getString(C0017R$string.oneplus_timer_shutdown_summary), Integer.valueOf(OPPowerOffPromptActivity.this.mPositiveCount)));
                    }
                    if (OPPowerOffPromptActivity.this.mHandler != null) {
                        OPPowerOffPromptActivity.this.mHandler.sendEmptyMessageDelayed(1, 1000);
                    }
                } else if (pButton != null && !OPPowerOffPromptActivity.this.mStatus) {
                    if (pButton.isEnabled()) {
                        int callState = OPPowerOffPromptActivity.this.mTelephonyManager.getCallState();
                        if (callState != 0) {
                            Log.d("OPPowerOffPromptActivity", "Cancel auto shutdown while phone state is:" + callState);
                            OPPowerOffPromptActivity.this.cancel();
                            return;
                        }
                        Log.d("OPPowerOffPromptActivity", "Perform auto shutdown");
                        pButton.performClick();
                        return;
                    }
                    pButton.setEnabled(true);
                }
            }
        }
    };
    private PowerManager.WakeLock mLock = null;
    private int mNegativeCount = 0;
    private int mPositiveCount = 60;
    private boolean mResume = false;
    private ProgressDialog mShutdownDialog;
    private boolean mStatus = false;
    private TelephonyManager mTelephonyManager;
    private PowerManager.WakeLock mWakeLock;
    private PowerManager pm = null;

    static /* synthetic */ int access$410(OPPowerOffPromptActivity oPPowerOffPromptActivity) {
        int i = oPPowerOffPromptActivity.mNegativeCount;
        oPPowerOffPromptActivity.mNegativeCount = i - 1;
        return i;
    }

    static /* synthetic */ int access$610(OPPowerOffPromptActivity oPPowerOffPromptActivity) {
        int i = oPPowerOffPromptActivity.mPositiveCount;
        oPPowerOffPromptActivity.mPositiveCount = i - 1;
        return i;
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("time", this.mPositiveCount);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        this.mTelephonyManager = (TelephonyManager) getSystemService("phone");
        if (bundle != null) {
            this.mPositiveCount = bundle.getInt("time");
        }
        getWindow().addFlags(1574016);
        super.onCreate(bundle);
        this.pm = (PowerManager) getSystemService("power");
        raiseScreenUp();
        OPTimerDialog oPTimerDialog = new OPTimerDialog(this);
        this.alertDialog = oPTimerDialog;
        oPTimerDialog.setTitle(getResources().getString(C0017R$string.oneplus_timer_shutdown_title));
        this.alertDialog.setMessage(String.format(getResources().getString(C0017R$string.oneplus_timer_shutdown_summary), Integer.valueOf(this.mPositiveCount)));
        this.alertDialog.setPositiveButton(getResources().getString(C0017R$string.oneplus_timer_shutdown_position), new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.timer.timepower.OPPowerOffPromptActivity.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if (OPPowerOffPromptActivity.this.alertDialog != null) {
                    OPPowerOffPromptActivity.this.alertDialog.dismiss();
                    OPPowerOffPromptActivity.this.alertDialog = null;
                }
                if (!SystemProperties.getBoolean("sys.debug.watchdog", false)) {
                    OPPowerOffPromptActivity oPPowerOffPromptActivity = OPPowerOffPromptActivity.this;
                    oPPowerOffPromptActivity.showDialog(oPPowerOffPromptActivity);
                    Intent intent = new Intent("com.android.settings.POWER_CONFIRM_OP_OFF");
                    intent.addFlags(285212672);
                    OPPowerOffPromptActivity.this.sendBroadcast(intent);
                }
            }
        }, 60);
        this.alertDialog.setNegativeButton(getResources().getString(C0017R$string.oneplus_timer_shutdown_nagative), new DialogInterface.OnClickListener() {
            /* class com.oneplus.settings.timer.timepower.OPPowerOffPromptActivity.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                OPPowerOffPromptActivity.this.cancel();
            }
        }, 10);
        this.alertDialog.show();
        this.alertDialog.setButtonType(-1, this.mPositiveCount, true);
        this.mHandler.sendEmptyMessageDelayed(1, 200);
        ProgressDialog progressDialog = new ProgressDialog(this);
        this.mShutdownDialog = progressDialog;
        progressDialog.setMessage(getString(17041262));
        this.mShutdownDialog.setCancelable(false);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showDialog(Context context) {
        ProgressDialog progressDialog;
        if (!isFinishing() && !isDestroyed() && (progressDialog = this.mShutdownDialog) != null) {
            progressDialog.show();
        }
    }

    private void dismissShutdownDialog() {
        ProgressDialog progressDialog = this.mShutdownDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void cancel() {
        Intent intent = new Intent("com.android.settings.POWER_CANCEL_OP_OFF");
        intent.addFlags(285212672);
        sendBroadcast(intent);
        this.mStatus = true;
        this.alertDialog.dismiss();
        finish();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        this.mResume = true;
        acquireWakeLock();
    }

    public void finish() {
        super.finish();
        releaseWakeLock();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.mResume = false;
        OPTimerDialog oPTimerDialog = this.alertDialog;
        if (oPTimerDialog != null && oPTimerDialog.isShowing()) {
            this.mHandler.obtainMessage(1000);
        }
        dismissShutdownDialog();
    }

    private void acquireWakeLock() {
        if (this.mWakeLock == null) {
            PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(536870913, "TimepowerWakeLock");
            this.mWakeLock = newWakeLock;
            newWakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        PowerManager.WakeLock wakeLock = this.mWakeLock;
        if (wakeLock != null && wakeLock.isHeld()) {
            this.mWakeLock.release();
            this.mWakeLock = null;
        }
    }

    private void raiseScreenUp() {
        PowerManager powerManager = (PowerManager) getSystemService("power");
        this.pm = powerManager;
        PowerManager.WakeLock newWakeLock = powerManager.newWakeLock(805306374, "TimepowerWakeLock");
        this.mLock = newWakeLock;
        newWakeLock.acquire();
        this.mLock.release();
        this.mLock = null;
    }
}
