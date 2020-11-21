package com.android.wifitrackerlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkKey;
import android.net.NetworkRequest;
import android.net.NetworkScoreManager;
import android.net.ScoredNetwork;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.android.wifitrackerlib.BaseWifiTracker;
import java.time.Clock;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseWifiTracker implements LifecycleObserver {
    private static boolean sVerboseLogging;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        /* class com.android.wifitrackerlib.BaseWifiTracker.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                String str = BaseWifiTracker.this.mTag;
                Log.v(str, "Received broadcast: " + action);
            }
            if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
                if (BaseWifiTracker.this.mWifiManager.getWifiState() == 3) {
                    BaseWifiTracker.this.mScanner.start();
                } else {
                    BaseWifiTracker.this.mScanner.stop();
                }
                BaseWifiTracker.this.notifyOnWifiStateChanged();
                BaseWifiTracker.this.handleWifiStateChangedAction();
            } else if ("android.net.wifi.SCAN_RESULTS".equals(action)) {
                BaseWifiTracker baseWifiTracker = BaseWifiTracker.this;
                NetworkScoreManager networkScoreManager = baseWifiTracker.mNetworkScoreManager;
                Stream<R> map = baseWifiTracker.mWifiManager.getScanResults().stream().map($$Lambda$F7I5Dkjpf4Rkj99qB_bsUx3MJiA.INSTANCE);
                Set set = BaseWifiTracker.this.mRequestedScoreKeys;
                Objects.requireNonNull(set);
                networkScoreManager.requestScores((Collection) map.filter(new Predicate(set) {
                    /* class com.android.wifitrackerlib.$$Lambda$odOcE4rV8hguohUouyGmOXJmSc */
                    public final /* synthetic */ Set f$0;

                    {
                        this.f$0 = r1;
                    }

                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        return this.f$0.add((NetworkKey) obj);
                    }
                }).collect(Collectors.toList()));
                BaseWifiTracker.this.handleScanResultsAvailableAction(intent);
            } else if ("android.net.wifi.CONFIGURED_NETWORKS_CHANGE".equals(action)) {
                BaseWifiTracker.this.handleConfiguredNetworksChangedAction(intent);
            } else if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                BaseWifiTracker.this.handleNetworkStateChangedAction(intent);
            } else if ("android.net.wifi.RSSI_CHANGED".equals(action)) {
                BaseWifiTracker.this.handleRssiChangedAction();
            }
        }
    };
    protected final ConnectivityManager mConnectivityManager;
    protected final Context mContext;
    private final BaseWifiTrackerCallback mListener;
    protected final Handler mMainHandler;
    protected final long mMaxScanAgeMillis;
    private final ConnectivityManager.NetworkCallback mNetworkCallback = new ConnectivityManager.NetworkCallback() {
        /* class com.android.wifitrackerlib.BaseWifiTracker.AnonymousClass2 */

        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            BaseWifiTracker.this.handleLinkPropertiesChanged(linkProperties);
        }

        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            BaseWifiTracker.this.handleNetworkCapabilitiesChanged(networkCapabilities);
        }
    };
    private final NetworkRequest mNetworkRequest = new NetworkRequest.Builder().clearCapabilities().addTransportType(1).build();
    protected final NetworkScoreManager mNetworkScoreManager;
    private final Set<NetworkKey> mRequestedScoreKeys = new HashSet();
    protected final long mScanIntervalMillis;
    protected final ScanResultUpdater mScanResultUpdater;
    private final Scanner mScanner;
    private final String mTag;
    protected final WifiManager mWifiManager;
    protected final WifiNetworkScoreCache mWifiNetworkScoreCache;
    protected final Handler mWorkerHandler;

    /* access modifiers changed from: protected */
    public interface BaseWifiTrackerCallback {
        void onWifiStateChanged();
    }

    /* access modifiers changed from: protected */
    public void handleConfiguredNetworksChangedAction(Intent intent) {
    }

    /* access modifiers changed from: protected */
    public void handleLinkPropertiesChanged(LinkProperties linkProperties) {
    }

    /* access modifiers changed from: protected */
    public void handleNetworkCapabilitiesChanged(NetworkCapabilities networkCapabilities) {
    }

    /* access modifiers changed from: protected */
    public void handleNetworkScoreCacheUpdated() {
    }

    /* access modifiers changed from: protected */
    public void handleNetworkStateChangedAction(Intent intent) {
    }

    /* access modifiers changed from: protected */
    public void handleOnStart() {
    }

    /* access modifiers changed from: protected */
    public void handleRssiChangedAction() {
    }

    /* access modifiers changed from: protected */
    public void handleScanResultsAvailableAction(Intent intent) {
    }

    /* access modifiers changed from: protected */
    public void handleWifiStateChangedAction() {
    }

    public static boolean isVerboseLoggingEnabled() {
        return sVerboseLogging;
    }

    BaseWifiTracker(Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, Handler handler, Handler handler2, Clock clock, long j, long j2, BaseWifiTrackerCallback baseWifiTrackerCallback, String str) {
        lifecycle.addObserver(this);
        this.mContext = context;
        this.mWifiManager = wifiManager;
        this.mConnectivityManager = connectivityManager;
        this.mNetworkScoreManager = networkScoreManager;
        this.mMainHandler = handler;
        this.mWorkerHandler = handler2;
        this.mMaxScanAgeMillis = j;
        this.mScanIntervalMillis = j2;
        this.mListener = baseWifiTrackerCallback;
        this.mTag = str;
        this.mScanResultUpdater = new ScanResultUpdater(clock, j + j2);
        this.mWifiNetworkScoreCache = new WifiNetworkScoreCache(this.mContext, new WifiNetworkScoreCache.CacheListener(this.mWorkerHandler) {
            /* class com.android.wifitrackerlib.BaseWifiTracker.AnonymousClass3 */

            public void networkCacheUpdated(List<ScoredNetwork> list) {
                BaseWifiTracker.this.handleNetworkScoreCacheUpdated();
            }
        });
        this.mScanner = new Scanner(handler2.getLooper());
        sVerboseLogging = this.mWifiManager.isVerboseLoggingEnabled();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        intentFilter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.RSSI_CHANGED");
        this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter, null, this.mWorkerHandler);
        this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback, this.mWorkerHandler);
        this.mNetworkScoreManager.registerNetworkScoreCache(1, this.mWifiNetworkScoreCache, 2);
        if (this.mWifiManager.getWifiState() == 3) {
            Handler handler = this.mWorkerHandler;
            Scanner scanner = this.mScanner;
            Objects.requireNonNull(scanner);
            handler.post(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$BaseWifiTracker$pw9dhfMm5LbxL1gY1jP_JJmuRkU */

                public final void run() {
                    BaseWifiTracker.Scanner.this.start();
                }
            });
        } else {
            Handler handler2 = this.mWorkerHandler;
            Scanner scanner2 = this.mScanner;
            Objects.requireNonNull(scanner2);
            handler2.post(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$BaseWifiTracker$KvY90710q6wRJD152kDBMW9ndFs */

                public final void run() {
                    BaseWifiTracker.Scanner.this.stop();
                }
            });
        }
        this.mWorkerHandler.post(new Runnable() {
            /* class com.android.wifitrackerlib.$$Lambda$S9fuCAjGYC38JCa05_AkNB8BE */

            public final void run() {
                BaseWifiTracker.this.handleOnStart();
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Handler handler = this.mWorkerHandler;
        Scanner scanner = this.mScanner;
        Objects.requireNonNull(scanner);
        handler.post(new Runnable() {
            /* class com.android.wifitrackerlib.$$Lambda$BaseWifiTracker$QlWe0ki0RbTEAz4j0GPGXOVBvC0 */

            public final void run() {
                BaseWifiTracker.Scanner.this.stop();
            }
        });
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
        this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
        this.mNetworkScoreManager.unregisterNetworkScoreCache(1, this.mWifiNetworkScoreCache);
        Handler handler2 = this.mWorkerHandler;
        Set<NetworkKey> set = this.mRequestedScoreKeys;
        Objects.requireNonNull(set);
        handler2.post(new Runnable(set) {
            /* class com.android.wifitrackerlib.$$Lambda$gWMviBWa7YejsjM1KbUOIv9j1JA */
            public final /* synthetic */ Set f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.clear();
            }
        });
    }

    public int getWifiState() {
        return this.mWifiManager.getWifiState();
    }

    /* access modifiers changed from: private */
    public class Scanner extends Handler {
        private int mRetry;

        private Scanner(Looper looper) {
            super(looper);
            this.mRetry = 0;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        public void start() {
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v(BaseWifiTracker.this.mTag, "Scanner start");
            }
            postScan();
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        public void stop() {
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v(BaseWifiTracker.this.mTag, "Scanner stop");
            }
            this.mRetry = 0;
            removeCallbacksAndMessages(null);
        }

        /* access modifiers changed from: private */
        public void postScan() {
            if (BaseWifiTracker.this.mWifiManager.startScan()) {
                this.mRetry = 0;
            } else {
                int i = this.mRetry + 1;
                this.mRetry = i;
                if (i >= 3) {
                    if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                        String str = BaseWifiTracker.this.mTag;
                        Log.v(str, "Scanner failed to start scan " + this.mRetry + " times!");
                    }
                    this.mRetry = 0;
                    return;
                }
            }
            postDelayed(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$BaseWifiTracker$Scanner$Lob1PHu6bdjiK_7H86IDLNF_WiM */

                public final void run() {
                    BaseWifiTracker.Scanner.this.postScan();
                }
            }, BaseWifiTracker.this.mScanIntervalMillis);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void notifyOnWifiStateChanged() {
        BaseWifiTrackerCallback baseWifiTrackerCallback = this.mListener;
        if (baseWifiTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(baseWifiTrackerCallback);
            handler.post(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$MIul75RWhCdcC435EGCLupy9Spc */

                public final void run() {
                    BaseWifiTracker.BaseWifiTrackerCallback.this.onWifiStateChanged();
                }
            });
        }
    }
}
