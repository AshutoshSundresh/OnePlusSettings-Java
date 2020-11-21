package com.google.analytics.tracking.android;

import android.content.Context;
import android.content.Intent;
import com.google.analytics.tracking.android.AnalyticsGmsCoreClient;
import com.google.android.gms.analytics.internal.Command;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

class GAServiceProxy implements ServiceProxy, AnalyticsGmsCoreClient.OnConnectedListener, AnalyticsGmsCoreClient.OnConnectionFailedListener {
    private volatile AnalyticsClient client;
    private Clock clock;
    private volatile int connectTries;
    private final Context ctx;
    private volatile Timer disconnectCheckTimer;
    private volatile Timer failedConnectTimer;
    private boolean forceLocalDispatch;
    private final GoogleAnalytics gaInstance;
    private long idleTimeout;
    private volatile long lastRequestTime;
    private boolean pendingClearHits;
    private boolean pendingDispatch;
    private boolean pendingServiceDisconnect;
    private final Queue<HitParams> queue;
    private volatile Timer reConnectTimer;
    private volatile ConnectState state;
    private AnalyticsStore store;
    private AnalyticsStore testStore;
    private final AnalyticsThread thread;

    /* access modifiers changed from: private */
    public enum ConnectState {
        CONNECTING,
        CONNECTED_SERVICE,
        CONNECTED_LOCAL,
        BLOCKED,
        PENDING_CONNECTION,
        PENDING_DISCONNECT,
        DISCONNECTED
    }

    GAServiceProxy(Context context, AnalyticsThread analyticsThread, AnalyticsStore analyticsStore, GoogleAnalytics googleAnalytics) {
        this.queue = new ConcurrentLinkedQueue();
        this.idleTimeout = 300000;
        this.testStore = analyticsStore;
        this.ctx = context;
        this.thread = analyticsThread;
        this.gaInstance = googleAnalytics;
        this.clock = new Clock(this) {
            /* class com.google.analytics.tracking.android.GAServiceProxy.AnonymousClass1 */

            @Override // com.google.analytics.tracking.android.Clock
            public long currentTimeMillis() {
                return System.currentTimeMillis();
            }
        };
        this.connectTries = 0;
        this.state = ConnectState.DISCONNECTED;
    }

    GAServiceProxy(Context context, AnalyticsThread analyticsThread) {
        this(context, analyticsThread, null, GoogleAnalytics.getInstance(context));
    }

    @Override // com.google.analytics.tracking.android.ServiceProxy
    public void putHit(Map<String, String> map, long j, String str, List<Command> list) {
        Log.v("putHit called");
        this.queue.add(new HitParams(map, j, str, list));
        sendQueue();
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.google.analytics.tracking.android.GAServiceProxy$3  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$google$analytics$tracking$android$GAServiceProxy$ConnectState;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|14) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                com.google.analytics.tracking.android.GAServiceProxy$ConnectState[] r0 = com.google.analytics.tracking.android.GAServiceProxy.ConnectState.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.google.analytics.tracking.android.GAServiceProxy.AnonymousClass3.$SwitchMap$com$google$analytics$tracking$android$GAServiceProxy$ConnectState = r0
                com.google.analytics.tracking.android.GAServiceProxy$ConnectState r1 = com.google.analytics.tracking.android.GAServiceProxy.ConnectState.CONNECTED_LOCAL     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = com.google.analytics.tracking.android.GAServiceProxy.AnonymousClass3.$SwitchMap$com$google$analytics$tracking$android$GAServiceProxy$ConnectState     // Catch:{ NoSuchFieldError -> 0x001d }
                com.google.analytics.tracking.android.GAServiceProxy$ConnectState r1 = com.google.analytics.tracking.android.GAServiceProxy.ConnectState.CONNECTED_SERVICE     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = com.google.analytics.tracking.android.GAServiceProxy.AnonymousClass3.$SwitchMap$com$google$analytics$tracking$android$GAServiceProxy$ConnectState     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.google.analytics.tracking.android.GAServiceProxy$ConnectState r1 = com.google.analytics.tracking.android.GAServiceProxy.ConnectState.CONNECTING     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = com.google.analytics.tracking.android.GAServiceProxy.AnonymousClass3.$SwitchMap$com$google$analytics$tracking$android$GAServiceProxy$ConnectState     // Catch:{ NoSuchFieldError -> 0x0033 }
                com.google.analytics.tracking.android.GAServiceProxy$ConnectState r1 = com.google.analytics.tracking.android.GAServiceProxy.ConnectState.PENDING_CONNECTION     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = com.google.analytics.tracking.android.GAServiceProxy.AnonymousClass3.$SwitchMap$com$google$analytics$tracking$android$GAServiceProxy$ConnectState     // Catch:{ NoSuchFieldError -> 0x003e }
                com.google.analytics.tracking.android.GAServiceProxy$ConnectState r1 = com.google.analytics.tracking.android.GAServiceProxy.ConnectState.PENDING_DISCONNECT     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = com.google.analytics.tracking.android.GAServiceProxy.AnonymousClass3.$SwitchMap$com$google$analytics$tracking$android$GAServiceProxy$ConnectState     // Catch:{ NoSuchFieldError -> 0x0049 }
                com.google.analytics.tracking.android.GAServiceProxy$ConnectState r1 = com.google.analytics.tracking.android.GAServiceProxy.ConnectState.DISCONNECTED     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.analytics.tracking.android.GAServiceProxy.AnonymousClass3.<clinit>():void");
        }
    }

    @Override // com.google.analytics.tracking.android.ServiceProxy
    public void dispatch() {
        int i = AnonymousClass3.$SwitchMap$com$google$analytics$tracking$android$GAServiceProxy$ConnectState[this.state.ordinal()];
        if (i == 1) {
            dispatchToStore();
        } else if (i != 2) {
            this.pendingDispatch = true;
        }
    }

    public void clearHits() {
        Log.v("clearHits called");
        this.queue.clear();
        int i = AnonymousClass3.$SwitchMap$com$google$analytics$tracking$android$GAServiceProxy$ConnectState[this.state.ordinal()];
        if (i == 1) {
            this.store.clearHits(0);
            this.pendingClearHits = false;
        } else if (i != 2) {
            this.pendingClearHits = true;
        } else {
            this.client.clearHits();
            this.pendingClearHits = false;
        }
    }

    @Override // com.google.analytics.tracking.android.ServiceProxy
    public synchronized void setForceLocalDispatch() {
        if (!this.forceLocalDispatch) {
            Log.v("setForceLocalDispatch called.");
            this.forceLocalDispatch = true;
            int i = AnonymousClass3.$SwitchMap$com$google$analytics$tracking$android$GAServiceProxy$ConnectState[this.state.ordinal()];
            if (i == 2) {
                disconnectFromService();
            } else if (i == 3) {
                this.pendingServiceDisconnect = true;
            }
        }
    }

    private Timer cancelTimer(Timer timer) {
        if (timer == null) {
            return null;
        }
        timer.cancel();
        return null;
    }

    private void clearAllTimers() {
        this.reConnectTimer = cancelTimer(this.reConnectTimer);
        this.failedConnectTimer = cancelTimer(this.failedConnectTimer);
        this.disconnectCheckTimer = cancelTimer(this.disconnectCheckTimer);
    }

    @Override // com.google.analytics.tracking.android.ServiceProxy
    public void createService() {
        if (this.client == null) {
            this.client = new AnalyticsGmsCoreClient(this.ctx, this, this);
            connectToService();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private synchronized void sendQueue() {
        if (!Thread.currentThread().equals(this.thread.getThread())) {
            this.thread.getQueue().add(new Runnable() {
                /* class com.google.analytics.tracking.android.GAServiceProxy.AnonymousClass2 */

                public void run() {
                    GAServiceProxy.this.sendQueue();
                }
            });
            return;
        }
        if (this.pendingClearHits) {
            clearHits();
        }
        int i = AnonymousClass3.$SwitchMap$com$google$analytics$tracking$android$GAServiceProxy$ConnectState[this.state.ordinal()];
        if (i == 1) {
            while (!this.queue.isEmpty()) {
                HitParams poll = this.queue.poll();
                Log.v("Sending hit to store  " + poll);
                this.store.putHit(poll.getWireFormatParams(), poll.getHitTimeInMilliseconds(), poll.getPath(), poll.getCommands());
            }
            if (this.pendingDispatch) {
                dispatchToStore();
            }
        } else if (i == 2) {
            while (!this.queue.isEmpty()) {
                HitParams peek = this.queue.peek();
                Log.v("Sending hit to service   " + peek);
                if (!this.gaInstance.isDryRunEnabled()) {
                    this.client.sendHit(peek.getWireFormatParams(), peek.getHitTimeInMilliseconds(), peek.getPath(), peek.getCommands());
                } else {
                    Log.v("Dry run enabled. Hit not actually sent to service.");
                }
                this.queue.poll();
            }
            this.lastRequestTime = this.clock.currentTimeMillis();
        } else if (i == 6) {
            Log.v("Need to reconnect");
            if (!this.queue.isEmpty()) {
                connectToService();
            }
        }
    }

    private void dispatchToStore() {
        this.store.dispatch();
        this.pendingDispatch = false;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private synchronized void useStore() {
        if (this.state != ConnectState.CONNECTED_LOCAL) {
            clearAllTimers();
            Log.v("falling back to local store");
            if (this.testStore != null) {
                this.store = this.testStore;
            } else {
                GAServiceManager instance = GAServiceManager.getInstance();
                instance.initialize(this.ctx, this.thread);
                this.store = instance.getStore();
            }
            this.state = ConnectState.CONNECTED_LOCAL;
            sendQueue();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private synchronized void connectToService() {
        if (this.forceLocalDispatch || this.client == null || this.state == ConnectState.CONNECTED_LOCAL) {
            Log.w("client not initialized.");
            useStore();
        } else {
            try {
                this.connectTries++;
                cancelTimer(this.failedConnectTimer);
                this.state = ConnectState.CONNECTING;
                this.failedConnectTimer = new Timer("Failed Connect");
                this.failedConnectTimer.schedule(new FailedConnectTask(), 3000);
                Log.v("connecting to Analytics service");
                this.client.connect();
            } catch (SecurityException unused) {
                Log.w("security exception on connectToService");
                useStore();
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private synchronized void disconnectFromService() {
        if (this.client != null && this.state == ConnectState.CONNECTED_SERVICE) {
            this.state = ConnectState.PENDING_DISCONNECT;
            this.client.disconnect();
        }
    }

    @Override // com.google.analytics.tracking.android.AnalyticsGmsCoreClient.OnConnectedListener
    public synchronized void onConnected() {
        this.failedConnectTimer = cancelTimer(this.failedConnectTimer);
        this.connectTries = 0;
        Log.v("Connected to service");
        this.state = ConnectState.CONNECTED_SERVICE;
        if (this.pendingServiceDisconnect) {
            disconnectFromService();
            this.pendingServiceDisconnect = false;
            return;
        }
        sendQueue();
        this.disconnectCheckTimer = cancelTimer(this.disconnectCheckTimer);
        this.disconnectCheckTimer = new Timer("disconnect check");
        this.disconnectCheckTimer.schedule(new DisconnectCheckTask(), this.idleTimeout);
    }

    @Override // com.google.analytics.tracking.android.AnalyticsGmsCoreClient.OnConnectedListener
    public synchronized void onDisconnected() {
        if (this.state == ConnectState.PENDING_DISCONNECT) {
            Log.v("Disconnected from service");
            clearAllTimers();
            this.state = ConnectState.DISCONNECTED;
        } else {
            Log.v("Unexpected disconnect.");
            this.state = ConnectState.PENDING_CONNECTION;
            if (this.connectTries < 2) {
                fireReconnectAttempt();
            } else {
                useStore();
            }
        }
    }

    @Override // com.google.analytics.tracking.android.AnalyticsGmsCoreClient.OnConnectionFailedListener
    public synchronized void onConnectionFailed(int i, Intent intent) {
        this.state = ConnectState.PENDING_CONNECTION;
        if (this.connectTries < 2) {
            Log.w("Service unavailable (code=" + i + "), will retry.");
            fireReconnectAttempt();
        } else {
            Log.w("Service unavailable (code=" + i + "), using local store.");
            useStore();
        }
    }

    private void fireReconnectAttempt() {
        this.reConnectTimer = cancelTimer(this.reConnectTimer);
        this.reConnectTimer = new Timer("Service Reconnect");
        this.reConnectTimer.schedule(new ReconnectTask(), 5000);
    }

    /* access modifiers changed from: private */
    public class FailedConnectTask extends TimerTask {
        private FailedConnectTask() {
        }

        public void run() {
            if (GAServiceProxy.this.state == ConnectState.CONNECTING) {
                GAServiceProxy.this.useStore();
            }
        }
    }

    /* access modifiers changed from: private */
    public class ReconnectTask extends TimerTask {
        private ReconnectTask() {
        }

        public void run() {
            GAServiceProxy.this.connectToService();
        }
    }

    private class DisconnectCheckTask extends TimerTask {
        private DisconnectCheckTask() {
        }

        public void run() {
            if (GAServiceProxy.this.state != ConnectState.CONNECTED_SERVICE || !GAServiceProxy.this.queue.isEmpty() || GAServiceProxy.this.lastRequestTime + GAServiceProxy.this.idleTimeout >= GAServiceProxy.this.clock.currentTimeMillis()) {
                GAServiceProxy.this.disconnectCheckTimer.schedule(new DisconnectCheckTask(), GAServiceProxy.this.idleTimeout);
                return;
            }
            Log.v("Disconnecting due to inactivity");
            GAServiceProxy.this.disconnectFromService();
        }
    }

    /* access modifiers changed from: private */
    public static class HitParams {
        private final List<Command> commands;
        private final long hitTimeInMilliseconds;
        private final String path;
        private final Map<String, String> wireFormatParams;

        public HitParams(Map<String, String> map, long j, String str, List<Command> list) {
            this.wireFormatParams = map;
            this.hitTimeInMilliseconds = j;
            this.path = str;
            this.commands = list;
        }

        public Map<String, String> getWireFormatParams() {
            return this.wireFormatParams;
        }

        public long getHitTimeInMilliseconds() {
            return this.hitTimeInMilliseconds;
        }

        public String getPath() {
            return this.path;
        }

        public List<Command> getCommands() {
            return this.commands;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("PATH: ");
            sb.append(this.path);
            if (this.wireFormatParams != null) {
                sb.append("  PARAMS: ");
                for (Map.Entry<String, String> entry : this.wireFormatParams.entrySet()) {
                    sb.append(entry.getKey());
                    sb.append("=");
                    sb.append(entry.getValue());
                    sb.append(",  ");
                }
            }
            return sb.toString();
        }
    }
}
