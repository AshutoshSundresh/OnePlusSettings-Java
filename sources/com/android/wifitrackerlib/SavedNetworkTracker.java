package com.android.wifitrackerlib;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkScoreManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import androidx.core.util.Preconditions;
import androidx.lifecycle.Lifecycle;
import com.android.wifitrackerlib.BaseWifiTracker;
import com.android.wifitrackerlib.SavedNetworkTracker;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SavedNetworkTracker extends BaseWifiTracker {
    private final SavedNetworkTrackerCallback mListener;
    private final Object mLock = new Object();
    private final Map<String, PasspointWifiEntry> mPasspointWifiEntryCache = new HashMap();
    private final List<WifiEntry> mSavedWifiEntries = new ArrayList();
    private final Map<String, StandardWifiEntry> mStandardWifiEntryCache = new HashMap();
    private final List<WifiEntry> mSubscriptionWifiEntries = new ArrayList();

    public interface SavedNetworkTrackerCallback extends BaseWifiTracker.BaseWifiTrackerCallback {
        void onSavedWifiEntriesChanged();

        void onSubscriptionWifiEntriesChanged();
    }

    public SavedNetworkTracker(Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, Handler handler, Handler handler2, Clock clock, long j, long j2, SavedNetworkTrackerCallback savedNetworkTrackerCallback) {
        super(lifecycle, context, wifiManager, connectivityManager, networkScoreManager, handler, handler2, clock, j, j2, savedNetworkTrackerCallback, "SavedNetworkTracker");
        this.mListener = savedNetworkTrackerCallback;
    }

    public List<WifiEntry> getSavedWifiEntries() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mSavedWifiEntries);
        }
        return arrayList;
    }

    public List<WifiEntry> getSubscriptionWifiEntries() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mSubscriptionWifiEntries);
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleOnStart() {
        updateStandardWifiEntryConfigs(this.mWifiManager.getConfiguredNetworks());
        updatePasspointWifiEntryConfigs(this.mWifiManager.getPasspointConfigurations());
        conditionallyUpdateScanResults(true);
        updateSavedWifiEntries();
        updateSubscriptionWifiEntries();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleWifiStateChangedAction() {
        conditionallyUpdateScanResults(true);
        updateSavedWifiEntries();
        updateSubscriptionWifiEntries();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleScanResultsAvailableAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        conditionallyUpdateScanResults(intent.getBooleanExtra("resultsUpdated", true));
        updateSavedWifiEntries();
        updateSubscriptionWifiEntries();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleConfiguredNetworksChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        WifiConfiguration wifiConfiguration = (WifiConfiguration) intent.getExtra("wifiConfiguration");
        if (wifiConfiguration == null || wifiConfiguration.isPasspoint()) {
            updateStandardWifiEntryConfigs(this.mWifiManager.getConfiguredNetworks());
        } else {
            updateStandardWifiEntryConfig(wifiConfiguration, ((Integer) intent.getExtra("changeReason")).intValue());
        }
        updatePasspointWifiEntryConfigs(this.mWifiManager.getPasspointConfigurations());
        updateSavedWifiEntries();
        updateSubscriptionWifiEntries();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleNetworkScoreCacheUpdated() {
        for (StandardWifiEntry standardWifiEntry : this.mStandardWifiEntryCache.values()) {
            standardWifiEntry.onScoreCacheUpdated();
        }
        for (PasspointWifiEntry passpointWifiEntry : this.mPasspointWifiEntryCache.values()) {
            passpointWifiEntry.onScoreCacheUpdated();
        }
    }

    private void updateSavedWifiEntries() {
        synchronized (this.mLock) {
            this.mSavedWifiEntries.clear();
            this.mSavedWifiEntries.addAll(this.mStandardWifiEntryCache.values());
            Collections.sort(this.mSavedWifiEntries);
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v("SavedNetworkTracker", "Updated SavedWifiEntries: " + Arrays.toString(this.mSavedWifiEntries.toArray()));
            }
        }
        notifyOnSavedWifiEntriesChanged();
    }

    private void updateSubscriptionWifiEntries() {
        synchronized (this.mLock) {
            this.mSubscriptionWifiEntries.clear();
            this.mSubscriptionWifiEntries.addAll(this.mPasspointWifiEntryCache.values());
            Collections.sort(this.mSubscriptionWifiEntries);
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v("SavedNetworkTracker", "Updated SubscriptionWifiEntries: " + Arrays.toString(this.mSubscriptionWifiEntries.toArray()));
            }
        }
        notifyOnSubscriptionWifiEntriesChanged();
    }

    private void updateStandardWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        this.mStandardWifiEntryCache.entrySet().forEach(new Consumer(Utils.mapScanResultsToKey(list, false, null, this.mWifiManager.isWpa3SaeSupported(), this.mWifiManager.isWpa3SuiteBSupported(), this.mWifiManager.isEnhancedOpenSupported(), this.mWifiManager.isWapiSupported())) {
            /* class com.android.wifitrackerlib.$$Lambda$SavedNetworkTracker$RngE0pMCth0JbStnHNgSxFSuio */
            public final /* synthetic */ Map f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                Map.Entry entry;
                ((StandardWifiEntry) entry.getValue()).updateScanResultInfo((List) this.f$0.get((String) ((Map.Entry) obj).getKey()));
            }
        });
    }

    private void updatePasspointWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        TreeSet treeSet = new TreeSet();
        for (Pair pair : this.mWifiManager.getAllMatchingWifiConfigs(list)) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
            String uniqueIdToPasspointWifiEntryKey = PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey());
            treeSet.add(uniqueIdToPasspointWifiEntryKey);
            if (this.mPasspointWifiEntryCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                this.mPasspointWifiEntryCache.get(uniqueIdToPasspointWifiEntryKey).updateScanResultInfo(wifiConfiguration, (List) ((Map) pair.second).get(0), (List) ((Map) pair.second).get(1));
            }
        }
        for (PasspointWifiEntry passpointWifiEntry : this.mPasspointWifiEntryCache.values()) {
            if (!treeSet.contains(passpointWifiEntry.getKey())) {
                passpointWifiEntry.updateScanResultInfo(null, null, null);
            }
        }
    }

    private void conditionallyUpdateScanResults(boolean z) {
        if (this.mWifiManager.getWifiState() == 1) {
            updateStandardWifiEntryScans(Collections.emptyList());
            updatePasspointWifiEntryScans(Collections.emptyList());
            return;
        }
        long j = this.mMaxScanAgeMillis;
        if (z) {
            this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
        } else {
            j += this.mScanIntervalMillis;
        }
        updateStandardWifiEntryScans(this.mScanResultUpdater.getScanResults(j));
        updatePasspointWifiEntryScans(this.mScanResultUpdater.getScanResults(j));
    }

    private void updateStandardWifiEntryConfig(WifiConfiguration wifiConfiguration, int i) {
        Preconditions.checkNotNull(wifiConfiguration, "Config should not be null!");
        String wifiConfigToStandardWifiEntryKey = StandardWifiEntry.wifiConfigToStandardWifiEntryKey(wifiConfiguration);
        StandardWifiEntry standardWifiEntry = this.mStandardWifiEntryCache.get(wifiConfigToStandardWifiEntryKey);
        if (standardWifiEntry != null) {
            if (i == 1) {
                standardWifiEntry.updateConfig(null);
                this.mStandardWifiEntryCache.remove(wifiConfigToStandardWifiEntryKey);
                return;
            }
            standardWifiEntry.updateConfig(wifiConfiguration);
        } else if (i != 1) {
            this.mStandardWifiEntryCache.put(wifiConfigToStandardWifiEntryKey, new StandardWifiEntry(this.mContext, this.mMainHandler, wifiConfigToStandardWifiEntryKey, wifiConfiguration, this.mWifiManager, this.mWifiNetworkScoreCache, true));
        }
    }

    private void updateStandardWifiEntryConfigs(List<WifiConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        Map map = (Map) list.stream().collect(Collectors.toMap($$Lambda$eRhiL3TPu1j8op3nmit378jGeyk.INSTANCE, Function.identity()));
        this.mStandardWifiEntryCache.entrySet().removeIf(new Predicate(map) {
            /* class com.android.wifitrackerlib.$$Lambda$SavedNetworkTracker$Uwm7U8CRSvjmODKGQBR_weIFtK8 */
            public final /* synthetic */ Map f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return SavedNetworkTracker.lambda$updateStandardWifiEntryConfigs$1(this.f$0, (Map.Entry) obj);
            }
        });
        for (String str : map.keySet()) {
            this.mStandardWifiEntryCache.put(str, new StandardWifiEntry(this.mContext, this.mMainHandler, str, (WifiConfiguration) map.get(str), this.mWifiManager, this.mWifiNetworkScoreCache, true));
        }
    }

    static /* synthetic */ boolean lambda$updateStandardWifiEntryConfigs$1(Map map, Map.Entry entry) {
        StandardWifiEntry standardWifiEntry = (StandardWifiEntry) entry.getValue();
        standardWifiEntry.updateConfig((WifiConfiguration) map.remove(standardWifiEntry.getKey()));
        return !standardWifiEntry.isSaved();
    }

    private void updatePasspointWifiEntryConfigs(List<PasspointConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        Map map = (Map) list.stream().collect(Collectors.toMap($$Lambda$SavedNetworkTracker$GiPU7UrK85F3w9N7PMlA7M9niw.INSTANCE, Function.identity()));
        this.mPasspointWifiEntryCache.entrySet().removeIf(new Predicate(map) {
            /* class com.android.wifitrackerlib.$$Lambda$SavedNetworkTracker$eyseTp76DXJATqUf1NbJ_EBfOw */
            public final /* synthetic */ Map f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return SavedNetworkTracker.lambda$updatePasspointWifiEntryConfigs$3(this.f$0, (Map.Entry) obj);
            }
        });
        for (String str : map.keySet()) {
            this.mPasspointWifiEntryCache.put(str, new PasspointWifiEntry(this.mContext, this.mMainHandler, (PasspointConfiguration) map.get(str), this.mWifiManager, this.mWifiNetworkScoreCache, true));
        }
    }

    static /* synthetic */ boolean lambda$updatePasspointWifiEntryConfigs$3(Map map, Map.Entry entry) {
        PasspointWifiEntry passpointWifiEntry = (PasspointWifiEntry) entry.getValue();
        PasspointConfiguration passpointConfiguration = (PasspointConfiguration) map.remove(passpointWifiEntry.getKey());
        if (passpointConfiguration == null) {
            return true;
        }
        passpointWifiEntry.updatePasspointConfig(passpointConfiguration);
        return false;
    }

    private void notifyOnSavedWifiEntriesChanged() {
        SavedNetworkTrackerCallback savedNetworkTrackerCallback = this.mListener;
        if (savedNetworkTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(savedNetworkTrackerCallback);
            handler.post(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$h1BuAemuREs5Akn8naywLEicng */

                public final void run() {
                    SavedNetworkTracker.SavedNetworkTrackerCallback.this.onSavedWifiEntriesChanged();
                }
            });
        }
    }

    private void notifyOnSubscriptionWifiEntriesChanged() {
        SavedNetworkTrackerCallback savedNetworkTrackerCallback = this.mListener;
        if (savedNetworkTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(savedNetworkTrackerCallback);
            handler.post(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$bWK0YQGHCIgfdyydB3bBJO0pQoM */

                public final void run() {
                    SavedNetworkTracker.SavedNetworkTrackerCallback.this.onSubscriptionWifiEntriesChanged();
                }
            });
        }
    }
}
