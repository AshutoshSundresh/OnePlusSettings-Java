package com.android.settingslib.deviceinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import com.android.internal.util.ArrayUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.lang.ref.WeakReference;

public abstract class AbstractConnectivityPreferenceController extends AbstractPreferenceController implements LifecycleObserver, OnStart, OnStop {
    private final BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        /* class com.android.settingslib.deviceinfo.AbstractConnectivityPreferenceController.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            if (ArrayUtils.contains(AbstractConnectivityPreferenceController.this.getConnectivityIntents(), intent.getAction())) {
                AbstractConnectivityPreferenceController.this.getHandler().sendEmptyMessage(600);
            }
        }
    };
    private Handler mHandler;

    /* access modifiers changed from: protected */
    public abstract String[] getConnectivityIntents();

    /* access modifiers changed from: protected */
    public abstract void updateConnectivity();

    public AbstractConnectivityPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.unregisterReceiver(this.mConnectivityReceiver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        for (String str : getConnectivityIntents()) {
            intentFilter.addAction(str);
        }
        this.mContext.registerReceiver(this.mConnectivityReceiver, intentFilter, "android.permission.CHANGE_NETWORK_STATE", null);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private Handler getHandler() {
        if (this.mHandler == null) {
            this.mHandler = new ConnectivityEventHandler(this);
        }
        return this.mHandler;
    }

    /* access modifiers changed from: private */
    public static class ConnectivityEventHandler extends Handler {
        private WeakReference<AbstractConnectivityPreferenceController> mPreferenceController;

        public ConnectivityEventHandler(AbstractConnectivityPreferenceController abstractConnectivityPreferenceController) {
            this.mPreferenceController = new WeakReference<>(abstractConnectivityPreferenceController);
        }

        public void handleMessage(Message message) {
            AbstractConnectivityPreferenceController abstractConnectivityPreferenceController = this.mPreferenceController.get();
            if (abstractConnectivityPreferenceController != null) {
                if (message.what == 600) {
                    abstractConnectivityPreferenceController.updateConnectivity();
                    return;
                }
                throw new IllegalStateException("Unknown message " + message.what);
            }
        }
    }
}
