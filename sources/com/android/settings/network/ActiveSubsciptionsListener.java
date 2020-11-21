package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ActiveSubsciptionsListener extends SubscriptionManager.OnSubscriptionsChangedListener implements AutoCloseable {
    private AtomicInteger mCacheState;
    private List<SubscriptionInfo> mCachedActiveSubscriptionInfo;
    private Context mContext;
    private Looper mLooper;
    private AtomicInteger mMaxActiveSubscriptionInfos;
    private IntentFilter mSubscriptionChangeIntentFilter;
    private BroadcastReceiver mSubscriptionChangeReceiver;
    private SubscriptionManager mSubscriptionManager;
    private final int mTargetSubscriptionId;

    public abstract void onChanged();

    public ActiveSubsciptionsListener(Looper looper, Context context) {
        this(looper, context, -1);
    }

    public ActiveSubsciptionsListener(Looper looper, Context context, int i) {
        super(looper);
        this.mLooper = looper;
        this.mContext = context;
        this.mTargetSubscriptionId = i;
        this.mCacheState = new AtomicInteger(0);
        this.mMaxActiveSubscriptionInfos = new AtomicInteger(-1);
        IntentFilter intentFilter = new IntentFilter();
        this.mSubscriptionChangeIntentFilter = intentFilter;
        intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        this.mSubscriptionChangeIntentFilter.addAction("android.intent.action.RADIO_TECHNOLOGY");
        this.mSubscriptionChangeIntentFilter.addAction("android.telephony.action.MULTI_SIM_CONFIG_CHANGED");
    }

    /* access modifiers changed from: package-private */
    public BroadcastReceiver getSubscriptionChangeReceiver() {
        return new BroadcastReceiver() {
            /* class com.android.settings.network.ActiveSubsciptionsListener.AnonymousClass1 */

            public void onReceive(Context context, Intent intent) {
                if (!isInitialStickyBroadcast()) {
                    String action = intent.getAction();
                    if (!TextUtils.isEmpty(action)) {
                        if ("android.telephony.action.CARRIER_CONFIG_CHANGED".equals(action)) {
                            int intExtra = intent.getIntExtra("android.telephony.extra.SUBSCRIPTION_INDEX", -1);
                            if (ActiveSubsciptionsListener.this.clearCachedSubId(intExtra)) {
                                if (SubscriptionManager.isValidSubscriptionId(ActiveSubsciptionsListener.this.mTargetSubscriptionId) && SubscriptionManager.isValidSubscriptionId(intExtra) && ActiveSubsciptionsListener.this.mTargetSubscriptionId != intExtra) {
                                    return;
                                }
                            } else {
                                return;
                            }
                        }
                        ActiveSubsciptionsListener.this.onSubscriptionsChanged();
                    }
                }
            }
        };
    }

    public void onSubscriptionsChanged() {
        clearCache();
        listenerNotify();
    }

    public void start() {
        monitorSubscriptionsChange(true);
    }

    public void stop() {
        monitorSubscriptionsChange(false);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        stop();
    }

    public SubscriptionManager getSubscriptionManager() {
        if (this.mSubscriptionManager == null) {
            this.mSubscriptionManager = (SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class);
        }
        return this.mSubscriptionManager;
    }

    public int getActiveSubscriptionInfoCountMax() {
        if (this.mCacheState.get() < 3) {
            return getSubscriptionManager().getActiveSubscriptionInfoCountMax();
        }
        this.mMaxActiveSubscriptionInfos.compareAndSet(-1, getSubscriptionManager().getActiveSubscriptionInfoCountMax());
        return this.mMaxActiveSubscriptionInfos.get();
    }

    public List<SubscriptionInfo> getActiveSubscriptionsInfo() {
        if (this.mCacheState.get() >= 4) {
            return this.mCachedActiveSubscriptionInfo;
        }
        this.mCachedActiveSubscriptionInfo = getSubscriptionManager().getActiveSubscriptionInfoList();
        this.mCacheState.compareAndSet(3, 4);
        return this.mCachedActiveSubscriptionInfo;
    }

    public SubscriptionInfo getActiveSubscriptionInfo(int i) {
        List<SubscriptionInfo> activeSubscriptionsInfo = getActiveSubscriptionsInfo();
        if (activeSubscriptionsInfo == null) {
            return null;
        }
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionsInfo) {
            if (subscriptionInfo.getSubscriptionId() == i) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    public List<SubscriptionInfo> getAccessibleSubscriptionsInfo() {
        return getSubscriptionManager().getAvailableSubscriptionInfoList();
    }

    public SubscriptionInfo getAccessibleSubscriptionInfo(int i) {
        SubscriptionInfo activeSubscriptionInfo = getActiveSubscriptionInfo(i);
        if (activeSubscriptionInfo != null) {
            return activeSubscriptionInfo;
        }
        List<SubscriptionInfo> accessibleSubscriptionsInfo = getAccessibleSubscriptionsInfo();
        if (accessibleSubscriptionsInfo == null) {
            return null;
        }
        for (SubscriptionInfo subscriptionInfo : accessibleSubscriptionsInfo) {
            if (subscriptionInfo.getSubscriptionId() == i) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    public void clearCache() {
        this.mMaxActiveSubscriptionInfos.set(-1);
        this.mCacheState.compareAndSet(4, 3);
        this.mCachedActiveSubscriptionInfo = null;
    }

    /* access modifiers changed from: package-private */
    public void registerForSubscriptionsChange() {
        getSubscriptionManager().addOnSubscriptionsChangedListener(this.mContext.getMainExecutor(), this);
    }

    private void monitorSubscriptionsChange(boolean z) {
        if (!z) {
            int andSet = this.mCacheState.getAndSet(1);
            if (andSet <= 1) {
                this.mCacheState.compareAndSet(1, andSet);
                return;
            }
            BroadcastReceiver broadcastReceiver = this.mSubscriptionChangeReceiver;
            if (broadcastReceiver != null) {
                this.mContext.unregisterReceiver(broadcastReceiver);
            }
            getSubscriptionManager().removeOnSubscriptionsChangedListener(this);
            clearCache();
            this.mCacheState.compareAndSet(1, 0);
        } else if (this.mCacheState.compareAndSet(0, 2)) {
            if (this.mSubscriptionChangeReceiver == null) {
                this.mSubscriptionChangeReceiver = getSubscriptionChangeReceiver();
            }
            this.mContext.registerReceiver(this.mSubscriptionChangeReceiver, this.mSubscriptionChangeIntentFilter, null, new Handler(this.mLooper));
            registerForSubscriptionsChange();
            this.mCacheState.compareAndSet(2, 3);
        }
    }

    private void listenerNotify() {
        if (this.mCacheState.get() >= 3) {
            onChanged();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean clearCachedSubId(int i) {
        List<SubscriptionInfo> list;
        if (this.mCacheState.get() < 4 || (list = this.mCachedActiveSubscriptionInfo) == null) {
            return false;
        }
        for (SubscriptionInfo subscriptionInfo : list) {
            if (subscriptionInfo.getSubscriptionId() == i) {
                clearCache();
                return true;
            }
        }
        return false;
    }
}
