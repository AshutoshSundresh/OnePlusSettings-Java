package com.oneplus.settings;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;

public class OPThemeService extends Service {
    private Handler mHandler = new Handler() {
        /* class com.oneplus.settings.OPThemeService.AnonymousClass1 */

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0 || i == 1) {
                OPThemeService.this.dismissAndStopService();
            }
            super.handleMessage(message);
        }
    };
    private AlertDialog mLoadingDialog;
    private BroadcastReceiver mThemeDoneReceiver = new BroadcastReceiver() {
        /* class com.oneplus.settings.OPThemeService.AnonymousClass2 */

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.settings.oneplus_theme_ready")) {
                Log.v("OPThemeService", "onReceive arg1.getAction() = " + intent.getAction());
                OPThemeService.this.mHandler.removeMessages(1);
                OPThemeService.this.mHandler.sendMessageDelayed(OPThemeService.this.mHandler.obtainMessage(0), 2000);
            }
        }
    };

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        Log.d("OPThemeService", "onStartCommand");
        showDialog();
        registerReceiver();
        return super.onStartCommand(intent, i, i2);
    }

    private void showDialog() {
        AlertDialog create = new AlertDialog.Builder(new ContextThemeWrapper(this, C0018R$style.Theme_SubSettings)).create();
        this.mLoadingDialog = create;
        create.getWindow().setType(2008);
        this.mLoadingDialog.setCancelable(false);
        this.mLoadingDialog.setMessage(getString(C0017R$string.switch_skin_doing));
        this.mLoadingDialog.setCanceledOnTouchOutside(false);
        this.mLoadingDialog.show();
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(1), 5000);
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.settings.oneplus_theme_ready");
        registerReceiver(this.mThemeDoneReceiver, intentFilter);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void dismissAndStopService() {
        AlertDialog alertDialog = this.mLoadingDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            try {
                this.mLoadingDialog.dismiss();
            } catch (Exception e) {
                Log.e("OPThemeService", "dismiss", e);
            }
        }
        try {
            unregisterReceiver(this.mThemeDoneReceiver);
        } catch (Exception e2) {
            Log.e("OPThemeService", "unregisterReceiver", e2);
        }
        Toast.makeText(this, getString(C0017R$string.switch_theme_has_success), 0).show();
        stopSelf();
    }
}
