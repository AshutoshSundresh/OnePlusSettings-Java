package com.oneplus.settings.controllers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;

public class UstWfcStatusTracker {
    private boolean mBound = false;
    private Messenger mClientMessenger;
    private ServiceConnection mConnection = new ServiceConnection() {
        /* class com.oneplus.settings.controllers.UstWfcStatusTracker.AnonymousClass2 */

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            UstWfcStatusTracker.logd("onServiceConnected");
            UstWfcStatusTracker.this.mServiceMessenger = new Messenger(iBinder);
            UstWfcStatusTracker.this.mBound = true;
            Message obtain = Message.obtain((Handler) null, 0);
            obtain.replyTo = UstWfcStatusTracker.this.mClientMessenger;
            try {
                UstWfcStatusTracker.this.mServiceMessenger.send(obtain);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            UstWfcStatusTracker.logd("onServiceDisconnected");
            UstWfcStatusTracker.this.mServiceMessenger = null;
            UstWfcStatusTracker.this.mBound = false;
        }
    };
    private Context mContext;
    private Handler mHandler = new Handler() {
        /* class com.oneplus.settings.controllers.UstWfcStatusTracker.AnonymousClass1 */

        public void handleMessage(Message message) {
            if (message.what == 0) {
                String string = message.getData().getString("status", "");
                UstWfcStatusTracker.logd("Receive WFC status is " + string);
                if (!TextUtils.isEmpty(string)) {
                    UstWfcStatusTracker.this.mPreference.setSummary(string);
                } else {
                    UstWfcStatusTracker.this.mPreference.setSummary(UstWfcStatusTracker.this.mContext.getString(C0017R$string.op_data_vowlan_enable_summary));
                }
            }
        }
    };
    private Preference mPreference;
    private Messenger mServiceMessenger;

    public UstWfcStatusTracker(Context context, Preference preference) {
        logd("Init...");
        this.mContext = context;
        this.mPreference = preference;
        if (preference != null) {
            this.mClientMessenger = new Messenger(this.mHandler);
        }
    }

    public void startObserve() {
        logd("Start observe");
        Intent intent = new Intent("oneplus.intent.action.TMO_WFC_TRACKER_START");
        intent.setPackage("com.oneplus.operator.tmo.wfctracker");
        this.mContext.bindService(intent, this.mConnection, 1);
    }

    public void stopObserve() {
        logd("Stop observe");
        if (!this.mBound) {
            this.mContext.unbindService(this.mConnection);
            return;
        }
        Message obtain = Message.obtain((Handler) null, 1);
        obtain.replyTo = this.mClientMessenger;
        try {
            this.mServiceMessenger.send(obtain);
            this.mContext.unbindService(this.mConnection);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public static void logd(String str) {
        Log.d("UstWfcStatusTracker", "[UstWfcStatusTracker]" + str);
    }
}
