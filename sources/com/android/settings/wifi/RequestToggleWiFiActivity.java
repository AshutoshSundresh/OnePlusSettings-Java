package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Message;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.C0017R$string;

public class RequestToggleWiFiActivity extends AlertActivity implements DialogInterface.OnClickListener {
    private CharSequence mAppLabel;
    private int mLastUpdateState = -1;
    private final StateChangeReceiver mReceiver = new StateChangeReceiver();
    private int mState = -1;
    private final Runnable mTimeoutCommand = new Runnable() {
        /* class com.android.settings.wifi.$$Lambda$RequestToggleWiFiActivity$PwZgoHTFFBr3iYEQbWj0vZPfHpw */

        public final void run() {
            RequestToggleWiFiActivity.this.lambda$new$0$RequestToggleWiFiActivity();
        }
    };
    private WifiManager mWiFiManager;

    public void dismiss() {
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$RequestToggleWiFiActivity() {
        if (!isFinishing() && !isDestroyed()) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0063, code lost:
        if (r0.equals("android.net.wifi.action.REQUEST_ENABLE") != false) goto L_0x0067;
     */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0073  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r6) {
        /*
        // Method dump skipped, instructions count: 144
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.RequestToggleWiFiActivity.onCreate(android.os.Bundle):void");
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            finish();
        } else if (i == -1) {
            int i2 = this.mState;
            if (i2 == 1) {
                this.mWiFiManager.setWifiEnabled(true);
                this.mState = 2;
                scheduleToggleTimeout();
                updateUi();
            } else if (i2 == 3) {
                this.mWiFiManager.setWifiEnabled(false);
                this.mState = 4;
                scheduleToggleTimeout();
                updateUi();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        RequestToggleWiFiActivity.super.onStart();
        this.mReceiver.register();
        int wifiState = this.mWiFiManager.getWifiState();
        int i = this.mState;
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    if (i == 4) {
                        if (wifiState == 0) {
                            scheduleToggleTimeout();
                        } else if (wifiState == 1) {
                            setResult(-1);
                            finish();
                            return;
                        } else if (wifiState == 2 || wifiState == 3) {
                            this.mState = 3;
                        }
                    }
                } else if (wifiState == 1) {
                    setResult(-1);
                    finish();
                    return;
                } else if (wifiState == 2) {
                    this.mState = 4;
                    scheduleToggleTimeout();
                }
            } else if (wifiState == 0 || wifiState == 1) {
                this.mState = 1;
            } else if (wifiState == 2) {
                scheduleToggleTimeout();
            } else if (wifiState == 3) {
                setResult(-1);
                finish();
                return;
            }
        } else if (wifiState == 2) {
            this.mState = 2;
            scheduleToggleTimeout();
        } else if (wifiState == 3) {
            setResult(-1);
            finish();
            return;
        }
        updateUi();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        this.mReceiver.unregister();
        unscheduleToggleTimeout();
        RequestToggleWiFiActivity.super.onStop();
    }

    private void updateUi() {
        int i = this.mLastUpdateState;
        int i2 = this.mState;
        if (i != i2) {
            this.mLastUpdateState = i2;
            if (i2 == 1) {
                ((AlertActivity) this).mAlertParams.mPositiveButtonText = getString(C0017R$string.allow);
                AlertController.AlertParams alertParams = ((AlertActivity) this).mAlertParams;
                alertParams.mPositiveButtonListener = this;
                alertParams.mNegativeButtonText = getString(C0017R$string.deny);
                AlertController.AlertParams alertParams2 = ((AlertActivity) this).mAlertParams;
                alertParams2.mNegativeButtonListener = this;
                alertParams2.mMessage = getString(C0017R$string.wifi_ask_enable, new Object[]{this.mAppLabel});
            } else if (i2 == 2) {
                ((AlertActivity) this).mAlert.setButton(-1, (CharSequence) null, (DialogInterface.OnClickListener) null, (Message) null);
                ((AlertActivity) this).mAlert.setButton(-2, (CharSequence) null, (DialogInterface.OnClickListener) null, (Message) null);
                AlertController.AlertParams alertParams3 = ((AlertActivity) this).mAlertParams;
                alertParams3.mPositiveButtonText = null;
                alertParams3.mPositiveButtonListener = null;
                alertParams3.mNegativeButtonText = null;
                alertParams3.mNegativeButtonListener = null;
                alertParams3.mMessage = getString(C0017R$string.wifi_starting);
            } else if (i2 == 3) {
                ((AlertActivity) this).mAlertParams.mPositiveButtonText = getString(C0017R$string.allow);
                AlertController.AlertParams alertParams4 = ((AlertActivity) this).mAlertParams;
                alertParams4.mPositiveButtonListener = this;
                alertParams4.mNegativeButtonText = getString(C0017R$string.deny);
                AlertController.AlertParams alertParams5 = ((AlertActivity) this).mAlertParams;
                alertParams5.mNegativeButtonListener = this;
                alertParams5.mMessage = getString(C0017R$string.wifi_ask_disable, new Object[]{this.mAppLabel});
            } else if (i2 == 4) {
                ((AlertActivity) this).mAlert.setButton(-1, (CharSequence) null, (DialogInterface.OnClickListener) null, (Message) null);
                ((AlertActivity) this).mAlert.setButton(-2, (CharSequence) null, (DialogInterface.OnClickListener) null, (Message) null);
                AlertController.AlertParams alertParams6 = ((AlertActivity) this).mAlertParams;
                alertParams6.mPositiveButtonText = null;
                alertParams6.mPositiveButtonListener = null;
                alertParams6.mNegativeButtonText = null;
                alertParams6.mNegativeButtonListener = null;
                alertParams6.mMessage = getString(C0017R$string.wifi_stopping);
            }
            setupAlert();
        }
    }

    private void scheduleToggleTimeout() {
        getWindow().getDecorView().postDelayed(this.mTimeoutCommand, 10000);
    }

    private void unscheduleToggleTimeout() {
        getWindow().getDecorView().removeCallbacks(this.mTimeoutCommand);
    }

    private final class StateChangeReceiver extends BroadcastReceiver {
        private final IntentFilter mFilter;

        private StateChangeReceiver() {
            this.mFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        }

        public void register() {
            RequestToggleWiFiActivity.this.registerReceiver(this, this.mFilter);
        }

        public void unregister() {
            RequestToggleWiFiActivity.this.unregisterReceiver(this);
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [com.android.settings.wifi.RequestToggleWiFiActivity, android.app.Activity] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r1, android.content.Intent r2) {
            /*
                r0 = this;
                com.android.settings.wifi.RequestToggleWiFiActivity r1 = com.android.settings.wifi.RequestToggleWiFiActivity.this
                boolean r2 = r1.isFinishing()
                if (r2 != 0) goto L_0x003d
                boolean r1 = r1.isDestroyed()
                if (r1 == 0) goto L_0x000f
                goto L_0x003d
            L_0x000f:
                com.android.settings.wifi.RequestToggleWiFiActivity r1 = com.android.settings.wifi.RequestToggleWiFiActivity.this
                android.net.wifi.WifiManager r1 = com.android.settings.wifi.RequestToggleWiFiActivity.access$100(r1)
                int r1 = r1.getWifiState()
                r2 = 1
                if (r1 == r2) goto L_0x0020
                r2 = 3
                if (r1 == r2) goto L_0x0020
                goto L_0x003d
            L_0x0020:
                com.android.settings.wifi.RequestToggleWiFiActivity r1 = com.android.settings.wifi.RequestToggleWiFiActivity.this
                int r1 = com.android.settings.wifi.RequestToggleWiFiActivity.access$200(r1)
                r2 = 2
                if (r1 == r2) goto L_0x0032
                com.android.settings.wifi.RequestToggleWiFiActivity r1 = com.android.settings.wifi.RequestToggleWiFiActivity.this
                int r1 = com.android.settings.wifi.RequestToggleWiFiActivity.access$200(r1)
                r2 = 4
                if (r1 != r2) goto L_0x003d
            L_0x0032:
                com.android.settings.wifi.RequestToggleWiFiActivity r1 = com.android.settings.wifi.RequestToggleWiFiActivity.this
                r2 = -1
                r1.setResult(r2)
                com.android.settings.wifi.RequestToggleWiFiActivity r0 = com.android.settings.wifi.RequestToggleWiFiActivity.this
                r0.finish()
            L_0x003d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.RequestToggleWiFiActivity.StateChangeReceiver.onReceive(android.content.Context, android.content.Intent):void");
        }
    }
}
