package com.android.settings.network;

import android.content.Context;
import android.os.Looper;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import java.util.ArrayList;
import java.util.List;

public class ProxySubscriptionManager implements LifecycleObserver {
    private static ProxySubscriptionManager sSingleton;
    private List<OnActiveSubscriptionChangedListener> mActiveSubscriptionsListeners = new ArrayList();
    private GlobalSettingsChangeListener mAirplaneModeMonitor;
    private Lifecycle mLifecycle;
    private ActiveSubsciptionsListener mSubscriptionMonitor;

    public interface OnActiveSubscriptionChangedListener {
        default Lifecycle getLifecycle() {
            return null;
        }

        void onChanged();
    }

    public static ProxySubscriptionManager getInstance(Context context) {
        ProxySubscriptionManager proxySubscriptionManager = sSingleton;
        if (proxySubscriptionManager != null) {
            return proxySubscriptionManager;
        }
        ProxySubscriptionManager proxySubscriptionManager2 = new ProxySubscriptionManager(context.getApplicationContext());
        sSingleton = proxySubscriptionManager2;
        return proxySubscriptionManager2;
    }

    private ProxySubscriptionManager(Context context) {
        Looper mainLooper = context.getMainLooper();
        this.mSubscriptionMonitor = new ActiveSubsciptionsListener(mainLooper, context) {
            /* class com.android.settings.network.ProxySubscriptionManager.AnonymousClass1 */

            @Override // com.android.settings.network.ActiveSubsciptionsListener
            public void onChanged() {
                ProxySubscriptionManager.this.notifyAllListeners();
            }
        };
        this.mAirplaneModeMonitor = new GlobalSettingsChangeListener(mainLooper, context, "airplane_mode_on") {
            /* class com.android.settings.network.ProxySubscriptionManager.AnonymousClass2 */

            @Override // com.android.settings.network.GlobalSettingsChangeListener
            public void onChanged(String str) {
                ProxySubscriptionManager.this.mSubscriptionMonitor.clearCache();
                ProxySubscriptionManager.this.notifyAllListeners();
            }
        };
        this.mSubscriptionMonitor.start();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void notifyAllListeners() {
        for (OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener : this.mActiveSubscriptionsListeners) {
            Lifecycle lifecycle = onActiveSubscriptionChangedListener.getLifecycle();
            if (lifecycle == null || lifecycle.getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                onActiveSubscriptionChangedListener.onChanged();
            }
        }
    }

    public void setLifecycle(Lifecycle lifecycle) {
        Lifecycle lifecycle2 = this.mLifecycle;
        if (lifecycle2 != lifecycle) {
            if (lifecycle2 != null) {
                lifecycle2.removeObserver(this);
            }
            if (lifecycle != null) {
                lifecycle.addObserver(this);
            }
            this.mLifecycle = lifecycle;
            this.mAirplaneModeMonitor.notifyChangeBasedOn(lifecycle);
        }
    }

    /* access modifiers changed from: package-private */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mSubscriptionMonitor.start();
    }

    /* access modifiers changed from: package-private */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mSubscriptionMonitor.stop();
    }

    /* access modifiers changed from: package-private */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        this.mSubscriptionMonitor.close();
        this.mAirplaneModeMonitor.close();
        Lifecycle lifecycle = this.mLifecycle;
        if (lifecycle != null) {
            lifecycle.removeObserver(this);
            this.mLifecycle = null;
            sSingleton = null;
        }
    }

    public SubscriptionManager get() {
        return this.mSubscriptionMonitor.getSubscriptionManager();
    }

    public int getActiveSubscriptionInfoCountMax() {
        return this.mSubscriptionMonitor.getActiveSubscriptionInfoCountMax();
    }

    public List<SubscriptionInfo> getActiveSubscriptionsInfo() {
        return this.mSubscriptionMonitor.getActiveSubscriptionsInfo();
    }

    public SubscriptionInfo getActiveSubscriptionInfo(int i) {
        return this.mSubscriptionMonitor.getActiveSubscriptionInfo(i);
    }

    public List<SubscriptionInfo> getAccessibleSubscriptionsInfo() {
        return this.mSubscriptionMonitor.getAccessibleSubscriptionsInfo();
    }

    public SubscriptionInfo getAccessibleSubscriptionInfo(int i) {
        return this.mSubscriptionMonitor.getAccessibleSubscriptionInfo(i);
    }

    public void addActiveSubscriptionsListener(OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener) {
        if (!this.mActiveSubscriptionsListeners.contains(onActiveSubscriptionChangedListener)) {
            this.mActiveSubscriptionsListeners.add(onActiveSubscriptionChangedListener);
        }
    }

    public void removeActiveSubscriptionsListener(OnActiveSubscriptionChangedListener onActiveSubscriptionChangedListener) {
        this.mActiveSubscriptionsListeners.remove(onActiveSubscriptionChangedListener);
    }
}
