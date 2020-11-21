package com.oneplus.settings.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

public class OPProgressDialog extends ProgressDialog {
    private Handler mHandler = new Handler() {
        /* class com.oneplus.settings.ui.OPProgressDialog.AnonymousClass1 */

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                ((OPProgressDialog) message.obj).show();
            } else if (i == 1 && OPProgressDialog.this.mTimeOutListener != null) {
                OPProgressDialog.this.mTimeOutListener.onTimeOut(OPProgressDialog.this);
                OPProgressDialog.this.dismiss();
            }
        }
    };
    private long mShowDelayTime = 0;
    private long mTimeOut = 0;
    private OnTimeOutListener mTimeOutListener = null;
    private Timer mTimer = null;

    public interface OnTimeOutListener {
        void onTimeOut(OPProgressDialog oPProgressDialog);
    }

    public void showDelay(long j) {
        this.mShowDelayTime = j;
        showDelay();
    }

    public void showDelay() {
        this.mHandler.removeMessages(0);
        Message obtainMessage = this.mHandler.obtainMessage(0);
        obtainMessage.obj = this;
        this.mHandler.sendMessageDelayed(obtainMessage, this.mShowDelayTime);
    }

    public void dismiss() {
        this.mHandler.removeMessages(0);
        super.dismiss();
    }

    public OPProgressDialog(Context context) {
        super(context);
    }

    public void setTimeOut(long j, OnTimeOutListener onTimeOutListener) {
        this.mTimeOut = j;
        if (onTimeOutListener != null) {
            this.mTimeOutListener = onTimeOutListener;
        }
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
                /* class com.oneplus.settings.ui.OPProgressDialog.AnonymousClass2 */

                public void run() {
                    Log.d("OPProgressDialog", "timerOutTast......");
                    OPProgressDialog.this.mHandler.sendEmptyMessage(1);
                }
            }, this.mTimeOut);
        }
    }
}
