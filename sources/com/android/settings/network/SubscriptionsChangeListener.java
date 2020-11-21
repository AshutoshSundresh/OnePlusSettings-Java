package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SubscriptionManager;

public class SubscriptionsChangeListener extends ContentObserver {
    private Uri mAirplaneModeSettingUri = Settings.Global.getUriFor("airplane_mode_on");
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        /* class com.android.settings.network.SubscriptionsChangeListener.AnonymousClass2 */

        public void onReceive(Context context, Intent intent) {
            if (!isInitialStickyBroadcast()) {
                SubscriptionsChangeListener.this.subscriptionsChangedCallback();
            }
        }
    };
    private SubscriptionsChangeListenerClient mClient;
    private Context mContext;
    private SubscriptionManager mSubscriptionManager;
    private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener(Looper.getMainLooper()) {
        /* class com.android.settings.network.SubscriptionsChangeListener.AnonymousClass1 */

        public void onSubscriptionsChanged() {
            SubscriptionsChangeListener.this.subscriptionsChangedCallback();
        }
    };

    public interface SubscriptionsChangeListenerClient {
        void onAirplaneModeChanged(boolean z);

        void onSubscriptionsChanged();
    }

    public SubscriptionsChangeListener(Context context, SubscriptionsChangeListenerClient subscriptionsChangeListenerClient) {
        super(new Handler(Looper.getMainLooper()));
        this.mContext = context;
        this.mClient = subscriptionsChangeListenerClient;
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
    }

    public void start() {
        this.mSubscriptionManager.addOnSubscriptionsChangedListener(this.mContext.getMainExecutor(), this.mSubscriptionsChangedListener);
        this.mContext.getContentResolver().registerContentObserver(this.mAirplaneModeSettingUri, false, this);
        this.mContext.registerReceiver(this.mBroadcastReceiver, new IntentFilter("android.intent.action.RADIO_TECHNOLOGY"));
    }

    public void stop() {
        this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mSubscriptionsChangedListener);
        this.mContext.getContentResolver().unregisterContentObserver(this);
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
    }

    public boolean isAirplaneModeOn() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void subscriptionsChangedCallback() {
        this.mClient.onSubscriptionsChanged();
    }

    public void onChange(boolean z, Uri uri) {
        if (uri.equals(this.mAirplaneModeSettingUri)) {
            this.mClient.onAirplaneModeChanged(isAirplaneModeOn());
        }
    }
}
