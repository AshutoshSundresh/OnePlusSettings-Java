package com.oneplus.security.firewall;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

public class OPProgressDialog extends ProgressDialog {
    private Handler mHandler = new Handler() {
        /* class com.oneplus.security.firewall.OPProgressDialog.AnonymousClass1 */

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                OPProgressDialog.this.mStartTime = System.currentTimeMillis();
                ((OPProgressDialog) message.obj).show();
            } else if (i != 1) {
                if (i == 2) {
                    ((OPProgressDialog) message.obj).dismiss();
                }
            } else if (OPProgressDialog.this.mTimeOutListener != null) {
                OPProgressDialog.this.mTimeOutListener.onTimeOut(OPProgressDialog.this);
                OPProgressDialog.this.dismiss();
            }
        }
    };
    private long mStartTime;
    private long mTimeOut = 0;
    private OnTimeOutListener mTimeOutListener = null;
    private Timer mTimer = null;

    public interface OnTimeOutListener {
        void onTimeOut(OPProgressDialog oPProgressDialog);
    }

    public void dismiss() {
        this.mHandler.removeMessages(0);
        super.dismiss();
    }

    public OPProgressDialog(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        Timer timer = this.mTimer;
        if (timer != null) {
            timer.cancel();
            this.mTimer = null;
        }
    }

    public void onStart() {
        super.onStart();
        if (this.mTimeOut > 0) {
            this.mTimer = new Timer();
            this.mTimer.schedule(new TimerTask() {
                /* class com.oneplus.security.firewall.OPProgressDialog.AnonymousClass2 */

                public void run() {
                    Log.d("OPProgressDialog", "timerOutTast......");
                    OPProgressDialog.this.mHandler.sendEmptyMessage(1);
                }
            }, this.mTimeOut);
        }
    }
}
