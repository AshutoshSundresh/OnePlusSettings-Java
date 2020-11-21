package com.google.analytics.tracking.android;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.google.analytics.tracking.android.GAUsage;

public class GAServiceManager extends ServiceManager {
    private static final Object MSG_OBJECT = new Object();
    private static GAServiceManager instance;
    private boolean connected = true;
    private Context ctx;
    private int dispatchPeriodInSeconds = 1800;
    private Handler handler;
    private boolean listenForNetwork = true;
    private AnalyticsStoreStateListener listener = new AnalyticsStoreStateListener() {
        /* class com.google.analytics.tracking.android.GAServiceManager.AnonymousClass1 */

        @Override // com.google.analytics.tracking.android.AnalyticsStoreStateListener
        public void reportStoreIsEmpty(boolean z) {
            GAServiceManager gAServiceManager = GAServiceManager.this;
            gAServiceManager.updatePowerSaveMode(z, gAServiceManager.connected);
        }
    };
    private GANetworkReceiver networkReceiver;
    private boolean pendingDispatch = true;
    private boolean pendingForceLocalDispatch;
    private String pendingHostOverride;
    private AnalyticsStore store;
    private boolean storeIsEmpty = false;
    private volatile AnalyticsThread thread;

    public static GAServiceManager getInstance() {
        if (instance == null) {
            instance = new GAServiceManager();
        }
        return instance;
    }

    private GAServiceManager() {
    }

    static void clearInstance() {
        instance = null;
    }

    GAServiceManager(Context context, AnalyticsThread analyticsThread, AnalyticsStore analyticsStore, boolean z) {
        this.store = analyticsStore;
        this.thread = analyticsThread;
        this.listenForNetwork = z;
        initialize(context, analyticsThread);
    }

    private void initializeNetworkReceiver() {
        GANetworkReceiver gANetworkReceiver = new GANetworkReceiver(this);
        this.networkReceiver = gANetworkReceiver;
        gANetworkReceiver.register(this.ctx);
    }

    private void initializeHandler() {
        Handler handler2 = new Handler(this.ctx.getMainLooper(), new Handler.Callback() {
            /* class com.google.analytics.tracking.android.GAServiceManager.AnonymousClass2 */

            public boolean handleMessage(Message message) {
                if (1 == message.what && GAServiceManager.MSG_OBJECT.equals(message.obj)) {
                    GAUsage.getInstance().setDisableUsage(true);
                    GAServiceManager.this.dispatchLocalHits();
                    GAUsage.getInstance().setDisableUsage(false);
                    if (GAServiceManager.this.dispatchPeriodInSeconds > 0 && !GAServiceManager.this.storeIsEmpty) {
                        GAServiceManager.this.handler.sendMessageDelayed(GAServiceManager.this.handler.obtainMessage(1, GAServiceManager.MSG_OBJECT), (long) (GAServiceManager.this.dispatchPeriodInSeconds * 1000));
                    }
                }
                return true;
            }
        });
        this.handler = handler2;
        if (this.dispatchPeriodInSeconds > 0) {
            handler2.sendMessageDelayed(handler2.obtainMessage(1, MSG_OBJECT), (long) (this.dispatchPeriodInSeconds * 1000));
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void initialize(Context context, AnalyticsThread analyticsThread) {
        if (this.ctx == null) {
            this.ctx = context.getApplicationContext();
            if (this.thread == null) {
                this.thread = analyticsThread;
                if (this.pendingDispatch) {
                    dispatchLocalHits();
                    this.pendingDispatch = false;
                }
                if (this.pendingForceLocalDispatch) {
                    setForceLocalDispatch();
                    this.pendingForceLocalDispatch = false;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public AnalyticsStoreStateListener getListener() {
        return this.listener;
    }

    /* access modifiers changed from: package-private */
    public synchronized AnalyticsStore getStore() {
        if (this.store == null) {
            if (this.ctx != null) {
                PersistentAnalyticsStore persistentAnalyticsStore = new PersistentAnalyticsStore(this.listener, this.ctx);
                this.store = persistentAnalyticsStore;
                if (this.pendingHostOverride != null) {
                    persistentAnalyticsStore.getDispatcher().overrideHostUrl(this.pendingHostOverride);
                    this.pendingHostOverride = null;
                }
            } else {
                throw new IllegalStateException("Cant get a store unless we have a context");
            }
        }
        if (this.handler == null) {
            initializeHandler();
        }
        if (this.networkReceiver == null && this.listenForNetwork) {
            initializeNetworkReceiver();
        }
        return this.store;
    }

    /* access modifiers changed from: package-private */
    public synchronized void overrideHostUrl(String str) {
        if (this.store == null) {
            this.pendingHostOverride = str;
        } else {
            this.store.getDispatcher().overrideHostUrl(str);
        }
    }

    @Deprecated
    public synchronized void dispatchLocalHits() {
        if (this.thread == null) {
            Log.v("Dispatch call queued. Dispatch will run once initialization is complete.");
            this.pendingDispatch = true;
            return;
        }
        GAUsage.getInstance().setUsage(GAUsage.Field.DISPATCH);
        this.thread.dispatch();
    }

    @Deprecated
    public void setForceLocalDispatch() {
        if (this.thread == null) {
            Log.v("setForceLocalDispatch() queued. It will be called once initialization is complete.");
            this.pendingForceLocalDispatch = true;
            return;
        }
        GAUsage.getInstance().setUsage(GAUsage.Field.SET_FORCE_LOCAL_DISPATCH);
        this.thread.setForceLocalDispatch();
    }

    /* access modifiers changed from: package-private */
    public synchronized void updatePowerSaveMode(boolean z, boolean z2) {
        Object obj = MSG_OBJECT;
        synchronized (this) {
            if (this.storeIsEmpty != z || this.connected != z2) {
                if ((z || !z2) && this.dispatchPeriodInSeconds > 0) {
                    this.handler.removeMessages(1, obj);
                }
                if (!z && z2 && this.dispatchPeriodInSeconds > 0) {
                    this.handler.sendMessageDelayed(this.handler.obtainMessage(1, obj), (long) (this.dispatchPeriodInSeconds * 1000));
                }
                StringBuilder sb = new StringBuilder();
                sb.append("PowerSaveMode ");
                sb.append((z || !z2) ? "initiated." : "terminated.");
                Log.v(sb.toString());
                this.storeIsEmpty = z;
                this.connected = z2;
            }
        }
    }

    /* access modifiers changed from: package-private */
    @Override // com.google.analytics.tracking.android.ServiceManager
    public synchronized void updateConnectivityStatus(boolean z) {
        updatePowerSaveMode(this.storeIsEmpty, z);
    }

    /* access modifiers changed from: package-private */
    @Override // com.google.analytics.tracking.android.ServiceManager
    public synchronized void onRadioPowered() {
        Object obj = MSG_OBJECT;
        synchronized (this) {
            if (!this.storeIsEmpty && this.connected && this.dispatchPeriodInSeconds > 0) {
                this.handler.removeMessages(1, obj);
                this.handler.sendMessage(this.handler.obtainMessage(1, obj));
            }
        }
    }
}
