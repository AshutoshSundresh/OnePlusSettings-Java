package com.android.wifitrackerlib;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkScoreManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import androidx.core.util.Preconditions;
import androidx.lifecycle.Lifecycle;
import com.android.wifitrackerlib.BaseWifiTracker;
import com.android.wifitrackerlib.WifiPickerTracker;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WifiPickerTracker extends BaseWifiTracker {
    private WifiEntry mConnectedWifiEntry;
    private NetworkInfo mCurrentNetworkInfo;
    private final WifiPickerTrackerCallback mListener;
    private final Object mLock = new Object();
    private NetworkRequestEntry mNetworkRequestEntry;
    private int mNumSavedNetworks;
    private final Map<String, OsuWifiEntry> mOsuWifiEntryCache = new HashMap();
    private final Map<String, PasspointConfiguration> mPasspointConfigCache = new HashMap();
    private final Map<String, PasspointWifiEntry> mPasspointWifiEntryCache = new HashMap();
    private final Map<String, StandardWifiEntry> mStandardWifiEntryCache = new HashMap();
    private final Map<String, WifiConfiguration> mSuggestedConfigCache = new HashMap();
    private final Map<String, StandardWifiEntry> mSuggestedWifiEntryCache = new HashMap();
    private final Map<String, WifiConfiguration> mWifiConfigCache = new HashMap();
    private final List<WifiEntry> mWifiEntries = new ArrayList();

    public interface WifiPickerTrackerCallback extends BaseWifiTracker.BaseWifiTrackerCallback {
        void onNumSavedNetworksChanged();

        void onNumSavedSubscriptionsChanged();

        void onWifiEntriesChanged();
    }

    public WifiPickerTracker(Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, Handler handler, Handler handler2, Clock clock, long j, long j2, WifiPickerTrackerCallback wifiPickerTrackerCallback) {
        super(lifecycle, context, wifiManager, connectivityManager, networkScoreManager, handler, handler2, clock, j, j2, wifiPickerTrackerCallback, "WifiPickerTracker");
        this.mListener = wifiPickerTrackerCallback;
    }

    public WifiEntry getConnectedWifiEntry() {
        return this.mConnectedWifiEntry;
    }

    public List<WifiEntry> getWifiEntries() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mWifiEntries);
        }
        return arrayList;
    }

    public int getNumSavedNetworks() {
        return this.mNumSavedNetworks;
    }

    public int getNumSavedSubscriptions() {
        return this.mPasspointConfigCache.size();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleOnStart() {
        updateWifiConfigurations(this.mWifiManager.getPrivilegedConfiguredNetworks());
        updatePasspointConfigurations(this.mWifiManager.getPasspointConfigurations());
        this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
        conditionallyUpdateScanResults(true);
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        Network currentNetwork = this.mWifiManager.getCurrentNetwork();
        NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(currentNetwork);
        this.mCurrentNetworkInfo = networkInfo;
        updateConnectionInfo(connectionInfo, networkInfo);
        handleLinkPropertiesChanged(this.mConnectivityManager.getLinkProperties(currentNetwork));
        notifyOnNumSavedNetworksChanged();
        notifyOnNumSavedSubscriptionsChanged();
        updateWifiEntries();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleWifiStateChangedAction() {
        conditionallyUpdateScanResults(true);
        updateWifiEntries();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleScanResultsAvailableAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        conditionallyUpdateScanResults(intent.getBooleanExtra("resultsUpdated", true));
        updateWifiEntries();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleConfiguredNetworksChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        WifiConfiguration wifiConfiguration = (WifiConfiguration) intent.getExtra("wifiConfiguration");
        if (wifiConfiguration == null || wifiConfiguration.isPasspoint()) {
            updateWifiConfigurations(this.mWifiManager.getPrivilegedConfiguredNetworks());
        } else {
            updateWifiConfiguration(wifiConfiguration, ((Integer) intent.getExtra("changeReason")).intValue());
        }
        updatePasspointConfigurations(this.mWifiManager.getPasspointConfigurations());
        List<ScanResult> scanResults = this.mScanResultUpdater.getScanResults();
        updateStandardWifiEntryScans(scanResults);
        updateNetworkRequestEntryScans(scanResults);
        updatePasspointWifiEntryScans(scanResults);
        updateOsuWifiEntryScans(scanResults);
        notifyOnNumSavedNetworksChanged();
        notifyOnNumSavedSubscriptionsChanged();
        updateWifiEntries();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleNetworkStateChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        NetworkInfo networkInfo = (NetworkInfo) intent.getExtra("networkInfo");
        this.mCurrentNetworkInfo = networkInfo;
        updateConnectionInfo(connectionInfo, networkInfo);
        updateWifiEntries();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleRssiChangedAction() {
        if (this.mConnectedWifiEntry != null) {
            this.mConnectedWifiEntry.updateConnectionInfo(this.mWifiManager.getConnectionInfo(), this.mCurrentNetworkInfo);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleLinkPropertiesChanged(LinkProperties linkProperties) {
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null && wifiEntry.getConnectedState() == 2) {
            this.mConnectedWifiEntry.updateLinkProperties(linkProperties);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleNetworkCapabilitiesChanged(NetworkCapabilities networkCapabilities) {
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null && wifiEntry.getConnectedState() == 2) {
            this.mConnectedWifiEntry.updateNetworkCapabilities(networkCapabilities);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleNetworkScoreCacheUpdated() {
        for (StandardWifiEntry standardWifiEntry : this.mStandardWifiEntryCache.values()) {
            standardWifiEntry.onScoreCacheUpdated();
        }
        for (StandardWifiEntry standardWifiEntry2 : this.mSuggestedWifiEntryCache.values()) {
            standardWifiEntry2.onScoreCacheUpdated();
        }
        for (PasspointWifiEntry passpointWifiEntry : this.mPasspointWifiEntryCache.values()) {
            passpointWifiEntry.onScoreCacheUpdated();
        }
    }

    private void updateWifiEntries() {
        synchronized (this.mLock) {
            StandardWifiEntry orElse = this.mStandardWifiEntryCache.values().stream().filter($$Lambda$WifiPickerTracker$0TUEBOvt53oJDxdg7kKxPrqyWlc.INSTANCE).findAny().orElse(null);
            this.mConnectedWifiEntry = orElse;
            if (orElse == null) {
                this.mConnectedWifiEntry = this.mSuggestedWifiEntryCache.values().stream().filter($$Lambda$WifiPickerTracker$qhgVsdlHowIElQHRJK_fI8WN1nM.INSTANCE).findAny().orElse(null);
            }
            if (this.mConnectedWifiEntry == null) {
                this.mConnectedWifiEntry = this.mPasspointWifiEntryCache.values().stream().filter($$Lambda$WifiPickerTracker$ij2wZKo1q0VBIHIbqkDbWHwhHcA.INSTANCE).findAny().orElse(null);
            }
            if (!(this.mConnectedWifiEntry != null || this.mNetworkRequestEntry == null || this.mNetworkRequestEntry.getConnectedState() == 0)) {
                this.mConnectedWifiEntry = this.mNetworkRequestEntry;
            }
            this.mWifiEntries.clear();
            for (String str : this.mStandardWifiEntryCache.keySet()) {
                if (this.mConnectedWifiEntry == null || !TextUtils.equals(str, this.mConnectedWifiEntry.getKey())) {
                    StandardWifiEntry standardWifiEntry = this.mStandardWifiEntryCache.get(str);
                    StandardWifiEntry standardWifiEntry2 = this.mSuggestedWifiEntryCache.get(str);
                    if (standardWifiEntry.isSaved() || standardWifiEntry2 == null || !standardWifiEntry2.isUserShareable()) {
                        if (standardWifiEntry.getConnectedState() == 0) {
                            this.mWifiEntries.add(standardWifiEntry);
                        }
                    } else if (standardWifiEntry2.getConnectedState() == 0) {
                        this.mWifiEntries.add(standardWifiEntry2);
                    }
                }
            }
            this.mWifiEntries.addAll((Collection) this.mPasspointWifiEntryCache.values().stream().filter($$Lambda$WifiPickerTracker$QROaltglkm0qmJQUXPRJYd0Po2c.INSTANCE).collect(Collectors.toList()));
            this.mWifiEntries.addAll((Collection) this.mOsuWifiEntryCache.values().stream().filter($$Lambda$WifiPickerTracker$RLU9K1ZzG3pjvkYPNdVuba88eBA.INSTANCE).collect(Collectors.toList()));
            Collections.sort(this.mWifiEntries);
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                Log.v("WifiPickerTracker", "Connected WifiEntry: " + this.mConnectedWifiEntry);
                Log.v("WifiPickerTracker", "Updated WifiEntries: " + Arrays.toString(this.mWifiEntries.toArray()));
            }
        }
        notifyOnWifiEntriesChanged();
    }

    static /* synthetic */ boolean lambda$updateWifiEntries$0(StandardWifiEntry standardWifiEntry) {
        int connectedState = standardWifiEntry.getConnectedState();
        return connectedState == 2 || connectedState == 1;
    }

    static /* synthetic */ boolean lambda$updateWifiEntries$1(StandardWifiEntry standardWifiEntry) {
        int connectedState = standardWifiEntry.getConnectedState();
        return connectedState == 2 || connectedState == 1;
    }

    static /* synthetic */ boolean lambda$updateWifiEntries$2(PasspointWifiEntry passpointWifiEntry) {
        int connectedState = passpointWifiEntry.getConnectedState();
        return connectedState == 2 || connectedState == 1;
    }

    static /* synthetic */ boolean lambda$updateWifiEntries$3(PasspointWifiEntry passpointWifiEntry) {
        return passpointWifiEntry.getConnectedState() == 0;
    }

    static /* synthetic */ boolean lambda$updateWifiEntries$4(OsuWifiEntry osuWifiEntry) {
        return osuWifiEntry.getConnectedState() == 0 && !osuWifiEntry.isAlreadyProvisioned();
    }

    private void updateStandardWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        Map<String, List<ScanResult>> mapScanResultsToKey = Utils.mapScanResultsToKey(list, true, this.mWifiConfigCache, this.mWifiManager.isWpa3SaeSupported(), this.mWifiManager.isWpa3SuiteBSupported(), this.mWifiManager.isEnhancedOpenSupported(), this.mWifiManager.isWapiSupported());
        this.mStandardWifiEntryCache.entrySet().removeIf(new Predicate(mapScanResultsToKey) {
            /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$BCwaIQQiP0fvxsNUSnDq9UqTFVQ */
            public final /* synthetic */ Map f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return WifiPickerTracker.lambda$updateStandardWifiEntryScans$5(this.f$0, (Map.Entry) obj);
            }
        });
        for (Map.Entry<String, List<ScanResult>> entry : mapScanResultsToKey.entrySet()) {
            StandardWifiEntry standardWifiEntry = new StandardWifiEntry(this.mContext, this.mMainHandler, entry.getKey(), entry.getValue(), this.mWifiManager, this.mWifiNetworkScoreCache, false);
            standardWifiEntry.updateConfig(this.mWifiConfigCache.get(standardWifiEntry.getKey()));
            this.mStandardWifiEntryCache.put(standardWifiEntry.getKey(), standardWifiEntry);
        }
    }

    static /* synthetic */ boolean lambda$updateStandardWifiEntryScans$5(Map map, Map.Entry entry) {
        StandardWifiEntry standardWifiEntry = (StandardWifiEntry) entry.getValue();
        standardWifiEntry.updateScanResultInfo((List) map.remove((String) entry.getKey()));
        return standardWifiEntry.getLevel() == -1;
    }

    private void updateSuggestedWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        Map<String, List<ScanResult>> mapScanResultsToKey = Utils.mapScanResultsToKey(list, true, this.mWifiConfigCache, this.mWifiManager.isWpa3SaeSupported(), this.mWifiManager.isWpa3SuiteBSupported(), this.mWifiManager.isEnhancedOpenSupported(), this.mWifiManager.isWapiSupported());
        Map map = (Map) this.mWifiManager.getWifiConfigForMatchedNetworkSuggestionsSharedWithUser(list).stream().collect(Collectors.toMap($$Lambda$eRhiL3TPu1j8op3nmit378jGeyk.INSTANCE, Function.identity()));
        TreeSet treeSet = new TreeSet();
        for (String str : map.keySet()) {
            treeSet.add(str);
            if (!this.mSuggestedWifiEntryCache.containsKey(str)) {
                this.mSuggestedWifiEntryCache.put(str, new StandardWifiEntry(this.mContext, this.mMainHandler, str, (WifiConfiguration) map.get(str), this.mWifiManager, this.mWifiNetworkScoreCache, false));
            }
            StandardWifiEntry standardWifiEntry = this.mSuggestedWifiEntryCache.get(str);
            standardWifiEntry.setUserShareable(true);
            standardWifiEntry.updateScanResultInfo(mapScanResultsToKey.get(str));
        }
        this.mSuggestedWifiEntryCache.entrySet().removeIf(new Predicate(treeSet) {
            /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$b19vs3Vj3eXbrjFN5lP9dtI7hsE */
            public final /* synthetic */ Set f$1;

            {
                this.f$1 = r2;
            }

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return WifiPickerTracker.this.lambda$updateSuggestedWifiEntryScans$6$WifiPickerTracker(this.f$1, (Map.Entry) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateSuggestedWifiEntryScans$6 */
    public /* synthetic */ boolean lambda$updateSuggestedWifiEntryScans$6$WifiPickerTracker(Set set, Map.Entry entry) {
        StandardWifiEntry standardWifiEntry = (StandardWifiEntry) entry.getValue();
        String str = (String) entry.getKey();
        if (!set.contains(str)) {
            standardWifiEntry.updateConfig(this.mSuggestedConfigCache.get(str));
            standardWifiEntry.setUserShareable(false);
        }
        return !standardWifiEntry.isSuggestion();
    }

    private void updatePasspointWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        TreeSet treeSet = new TreeSet();
        for (Pair pair : this.mWifiManager.getAllMatchingWifiConfigs(list)) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
            List<ScanResult> list2 = (List) ((Map) pair.second).get(0);
            List<ScanResult> list3 = (List) ((Map) pair.second).get(1);
            String uniqueIdToPasspointWifiEntryKey = PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey());
            treeSet.add(uniqueIdToPasspointWifiEntryKey);
            if (!this.mPasspointWifiEntryCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                if (wifiConfiguration.fromWifiNetworkSuggestion) {
                    this.mPasspointWifiEntryCache.put(uniqueIdToPasspointWifiEntryKey, new PasspointWifiEntry(this.mContext, this.mMainHandler, wifiConfiguration, this.mWifiManager, this.mWifiNetworkScoreCache, false));
                } else if (this.mPasspointConfigCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                    this.mPasspointWifiEntryCache.put(uniqueIdToPasspointWifiEntryKey, new PasspointWifiEntry(this.mContext, this.mMainHandler, this.mPasspointConfigCache.get(uniqueIdToPasspointWifiEntryKey), this.mWifiManager, this.mWifiNetworkScoreCache, false));
                }
            }
            this.mPasspointWifiEntryCache.get(uniqueIdToPasspointWifiEntryKey).updateScanResultInfo(wifiConfiguration, list2, list3);
        }
        this.mPasspointWifiEntryCache.entrySet().removeIf(new Predicate(treeSet) {
            /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$b5PKKEVNG30K9l5cXQHsuoupJmU */
            public final /* synthetic */ Set f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return WifiPickerTracker.lambda$updatePasspointWifiEntryScans$7(this.f$0, (Map.Entry) obj);
            }
        });
    }

    static /* synthetic */ boolean lambda$updatePasspointWifiEntryScans$7(Set set, Map.Entry entry) {
        return ((PasspointWifiEntry) entry.getValue()).getLevel() == -1 || !set.contains(entry.getKey());
    }

    private void updateOsuWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        Map matchingOsuProviders = this.mWifiManager.getMatchingOsuProviders(list);
        Map matchingPasspointConfigsForOsuProviders = this.mWifiManager.getMatchingPasspointConfigsForOsuProviders(matchingOsuProviders.keySet());
        for (OsuWifiEntry osuWifiEntry : this.mOsuWifiEntryCache.values()) {
            osuWifiEntry.updateScanResultInfo((List) matchingOsuProviders.remove(osuWifiEntry.getOsuProvider()));
        }
        for (OsuProvider osuProvider : matchingOsuProviders.keySet()) {
            OsuWifiEntry osuWifiEntry2 = new OsuWifiEntry(this.mContext, this.mMainHandler, osuProvider, this.mWifiManager, this.mWifiNetworkScoreCache, false);
            osuWifiEntry2.updateScanResultInfo((List) matchingOsuProviders.get(osuProvider));
            this.mOsuWifiEntryCache.put(OsuWifiEntry.osuProviderToOsuWifiEntryKey(osuProvider), osuWifiEntry2);
        }
        this.mOsuWifiEntryCache.values().forEach(new Consumer(matchingPasspointConfigsForOsuProviders) {
            /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$v1LdO2tPR3JJiIf1xFFNAIzpUTI */
            public final /* synthetic */ Map f$1;

            {
                this.f$1 = r2;
            }

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                WifiPickerTracker.this.lambda$updateOsuWifiEntryScans$8$WifiPickerTracker(this.f$1, (OsuWifiEntry) obj);
            }
        });
        this.mOsuWifiEntryCache.entrySet().removeIf($$Lambda$WifiPickerTracker$ZvrS1GLiBdiVh6rqhik8skRXPAw.INSTANCE);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateOsuWifiEntryScans$8 */
    public /* synthetic */ void lambda$updateOsuWifiEntryScans$8$WifiPickerTracker(Map map, OsuWifiEntry osuWifiEntry) {
        PasspointConfiguration passpointConfiguration = (PasspointConfiguration) map.get(osuWifiEntry.getOsuProvider());
        if (passpointConfiguration == null) {
            osuWifiEntry.setAlreadyProvisioned(false);
            return;
        }
        osuWifiEntry.setAlreadyProvisioned(true);
        PasspointWifiEntry passpointWifiEntry = this.mPasspointWifiEntryCache.get(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()));
        if (passpointWifiEntry != null) {
            passpointWifiEntry.setOsuWifiEntry(osuWifiEntry);
        }
    }

    static /* synthetic */ boolean lambda$updateOsuWifiEntryScans$9(Map.Entry entry) {
        return ((OsuWifiEntry) entry.getValue()).getLevel() == -1;
    }

    private void updateNetworkRequestEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        NetworkRequestEntry networkRequestEntry = this.mNetworkRequestEntry;
        if (networkRequestEntry != null) {
            String ssid = networkRequestEntry.getSsid();
            int security = this.mNetworkRequestEntry.getSecurity();
            this.mNetworkRequestEntry.updateScanResultInfo((List) list.stream().filter(new Predicate(ssid, security) {
                /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$pqxDzYwc6dZqh70kHuMaEBXjaV4 */
                public final /* synthetic */ String f$0;
                public final /* synthetic */ int f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return WifiPickerTracker.lambda$updateNetworkRequestEntryScans$10(this.f$0, this.f$1, (ScanResult) obj);
                }
            }).collect(Collectors.toList()));
        }
    }

    static /* synthetic */ boolean lambda$updateNetworkRequestEntryScans$10(String str, int i, ScanResult scanResult) {
        return TextUtils.equals(scanResult.SSID, str) && Utils.getSecurityTypesFromScanResult(scanResult).contains(Integer.valueOf(i));
    }

    private void conditionallyUpdateScanResults(boolean z) {
        if (this.mWifiManager.getWifiState() == 1) {
            updateStandardWifiEntryScans(Collections.emptyList());
            updateSuggestedWifiEntryScans(Collections.emptyList());
            updatePasspointWifiEntryScans(Collections.emptyList());
            updateOsuWifiEntryScans(Collections.emptyList());
            updateNetworkRequestEntryScans(Collections.emptyList());
            return;
        }
        long j = this.mMaxScanAgeMillis;
        if (z) {
            this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
        } else {
            j += this.mScanIntervalMillis;
        }
        List<ScanResult> scanResults = this.mScanResultUpdater.getScanResults(j);
        updateStandardWifiEntryScans(scanResults);
        updateSuggestedWifiEntryScans(scanResults);
        updatePasspointWifiEntryScans(scanResults);
        updateOsuWifiEntryScans(scanResults);
        updateNetworkRequestEntryScans(scanResults);
    }

    private void updateWifiConfiguration(WifiConfiguration wifiConfiguration, int i) {
        WifiConfiguration wifiConfiguration2;
        StandardWifiEntry standardWifiEntry;
        Preconditions.checkNotNull(wifiConfiguration, "Config should not be null!");
        if (!wifiConfiguration.fromWifiNetworkSpecifier) {
            String wifiConfigToStandardWifiEntryKey = StandardWifiEntry.wifiConfigToStandardWifiEntryKey(wifiConfiguration);
            if (wifiConfiguration.fromWifiNetworkSuggestion) {
                if (i == 1) {
                    this.mSuggestedConfigCache.remove(wifiConfigToStandardWifiEntryKey);
                } else {
                    this.mSuggestedConfigCache.put(wifiConfigToStandardWifiEntryKey, wifiConfiguration);
                }
                wifiConfiguration2 = this.mSuggestedConfigCache.get(wifiConfigToStandardWifiEntryKey);
                standardWifiEntry = this.mSuggestedWifiEntryCache.get(wifiConfigToStandardWifiEntryKey);
            } else {
                if (i == 1) {
                    this.mWifiConfigCache.remove(wifiConfigToStandardWifiEntryKey);
                } else {
                    this.mWifiConfigCache.put(wifiConfigToStandardWifiEntryKey, wifiConfiguration);
                }
                wifiConfiguration2 = this.mWifiConfigCache.get(wifiConfigToStandardWifiEntryKey);
                this.mNumSavedNetworks = (int) this.mWifiConfigCache.values().stream().filter($$Lambda$WifiPickerTracker$VCBQWp0Yd_9TYKb9qTFEq0hUb8.INSTANCE).count();
                standardWifiEntry = this.mStandardWifiEntryCache.get(wifiConfigToStandardWifiEntryKey);
            }
            if (standardWifiEntry != null) {
                standardWifiEntry.updateConfig(wifiConfiguration2);
            }
        } else if (i == 1) {
            updateNetworkRequestConfig(null);
        } else {
            updateNetworkRequestConfig(wifiConfiguration);
        }
    }

    static /* synthetic */ boolean lambda$updateWifiConfiguration$11(WifiConfiguration wifiConfiguration) {
        return !wifiConfiguration.isEphemeral() && !wifiConfiguration.isPasspoint();
    }

    private void updateWifiConfigurations(List<WifiConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        this.mWifiConfigCache.clear();
        this.mSuggestedConfigCache.clear();
        boolean z = false;
        for (WifiConfiguration wifiConfiguration : list) {
            if (wifiConfiguration.fromWifiNetworkSuggestion) {
                this.mSuggestedConfigCache.put(StandardWifiEntry.wifiConfigToStandardWifiEntryKey(wifiConfiguration), wifiConfiguration);
            } else if (wifiConfiguration.fromWifiNetworkSpecifier) {
                z = true;
                updateNetworkRequestConfig(wifiConfiguration);
            } else {
                this.mWifiConfigCache.put(StandardWifiEntry.wifiConfigToStandardWifiEntryKey(wifiConfiguration), wifiConfiguration);
            }
        }
        if (!z) {
            updateNetworkRequestConfig(null);
        }
        this.mNumSavedNetworks = (int) this.mWifiConfigCache.values().stream().filter($$Lambda$WifiPickerTracker$GePaFPqD_aenaiEba32ZGYPo9E.INSTANCE).count();
        this.mStandardWifiEntryCache.entrySet().forEach(new Consumer() {
            /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$nziraTiXnLOckrKYTwmswRtPvWo */

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                WifiPickerTracker.this.lambda$updateWifiConfigurations$13$WifiPickerTracker((Map.Entry) obj);
            }
        });
        this.mSuggestedWifiEntryCache.entrySet().removeIf(new Predicate() {
            /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$n1MQJLze79Jma9HXue3y63vJ8 */

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return WifiPickerTracker.this.lambda$updateWifiConfigurations$14$WifiPickerTracker((Map.Entry) obj);
            }
        });
    }

    static /* synthetic */ boolean lambda$updateWifiConfigurations$12(WifiConfiguration wifiConfiguration) {
        return !wifiConfiguration.isEphemeral() && !wifiConfiguration.isPasspoint();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateWifiConfigurations$13 */
    public /* synthetic */ void lambda$updateWifiConfigurations$13$WifiPickerTracker(Map.Entry entry) {
        StandardWifiEntry standardWifiEntry = (StandardWifiEntry) entry.getValue();
        WifiConfiguration wifiConfiguration = this.mWifiConfigCache.get(standardWifiEntry.getKey());
        if (wifiConfiguration == null || !wifiConfiguration.isPasspoint()) {
            standardWifiEntry.updateConfig(wifiConfiguration);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateWifiConfigurations$14 */
    public /* synthetic */ boolean lambda$updateWifiConfigurations$14$WifiPickerTracker(Map.Entry entry) {
        StandardWifiEntry standardWifiEntry = (StandardWifiEntry) entry.getValue();
        WifiConfiguration wifiConfiguration = this.mSuggestedConfigCache.get(standardWifiEntry.getKey());
        if (wifiConfiguration == null || wifiConfiguration.isPasspoint()) {
            return true;
        }
        standardWifiEntry.updateConfig(wifiConfiguration);
        return false;
    }

    private void updateNetworkRequestConfig(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration == null) {
            this.mNetworkRequestEntry = null;
            return;
        }
        String wifiConfigToNetworkRequestEntryKey = NetworkRequestEntry.wifiConfigToNetworkRequestEntryKey(wifiConfiguration);
        NetworkRequestEntry networkRequestEntry = this.mNetworkRequestEntry;
        if (networkRequestEntry == null || !TextUtils.equals(wifiConfigToNetworkRequestEntryKey, networkRequestEntry.getKey())) {
            this.mNetworkRequestEntry = new NetworkRequestEntry(this.mContext, this.mMainHandler, wifiConfigToNetworkRequestEntryKey, this.mWifiManager, this.mWifiNetworkScoreCache, false);
        }
        this.mNetworkRequestEntry.updateConfig(wifiConfiguration);
    }

    private void updatePasspointConfigurations(List<PasspointConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        this.mPasspointConfigCache.clear();
        this.mPasspointConfigCache.putAll((Map) list.stream().collect(Collectors.toMap($$Lambda$WifiPickerTracker$NZj8llmsS2xh549r2eluZN8xzY.INSTANCE, Function.identity())));
        this.mPasspointWifiEntryCache.entrySet().removeIf(new Predicate() {
            /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$pk3mnes5HX5A_t5O7BAlvARDDy4 */

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return WifiPickerTracker.this.lambda$updatePasspointConfigurations$16$WifiPickerTracker((Map.Entry) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updatePasspointConfigurations$16 */
    public /* synthetic */ boolean lambda$updatePasspointConfigurations$16$WifiPickerTracker(Map.Entry entry) {
        PasspointWifiEntry passpointWifiEntry = (PasspointWifiEntry) entry.getValue();
        passpointWifiEntry.updatePasspointConfig(this.mPasspointConfigCache.get(passpointWifiEntry.getKey()));
        return !passpointWifiEntry.isSubscription() && !passpointWifiEntry.isSuggestion();
    }

    private void updateConnectionInfo(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        for (StandardWifiEntry standardWifiEntry : this.mStandardWifiEntryCache.values()) {
            standardWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
        }
        for (StandardWifiEntry standardWifiEntry2 : this.mSuggestedWifiEntryCache.values()) {
            standardWifiEntry2.updateConnectionInfo(wifiInfo, networkInfo);
        }
        for (PasspointWifiEntry passpointWifiEntry : this.mPasspointWifiEntryCache.values()) {
            passpointWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
        }
        for (OsuWifiEntry osuWifiEntry : this.mOsuWifiEntryCache.values()) {
            osuWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
        }
        NetworkRequestEntry networkRequestEntry = this.mNetworkRequestEntry;
        if (networkRequestEntry != null) {
            networkRequestEntry.updateConnectionInfo(wifiInfo, networkInfo);
        }
        conditionallyCreateConnectedStandardWifiEntry(wifiInfo, networkInfo);
        conditionallyCreateConnectedSuggestedWifiEntry(wifiInfo, networkInfo);
        conditionallyCreateConnectedPasspointWifiEntry(wifiInfo, networkInfo);
    }

    private void conditionallyCreateConnectedStandardWifiEntry(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (!wifiInfo.isPasspointAp() && !wifiInfo.isOsuAp()) {
            this.mWifiConfigCache.values().stream().filter(new Predicate(wifiInfo.getNetworkId()) {
                /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$DitgtO9_1wALYexVACURnNqwLlI */
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return WifiPickerTracker.this.lambda$conditionallyCreateConnectedStandardWifiEntry$17$WifiPickerTracker(this.f$1, (WifiConfiguration) obj);
                }
            }).findAny().ifPresent(new Consumer(wifiInfo, networkInfo) {
                /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$PEwbf5ZKF_Aj2ns46UMv4q1HGVo */
                public final /* synthetic */ WifiInfo f$1;
                public final /* synthetic */ NetworkInfo f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WifiPickerTracker.this.lambda$conditionallyCreateConnectedStandardWifiEntry$18$WifiPickerTracker(this.f$1, this.f$2, (WifiConfiguration) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$conditionallyCreateConnectedStandardWifiEntry$17 */
    public /* synthetic */ boolean lambda$conditionallyCreateConnectedStandardWifiEntry$17$WifiPickerTracker(int i, WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.networkId == i && !this.mStandardWifiEntryCache.containsKey(StandardWifiEntry.wifiConfigToStandardWifiEntryKey(wifiConfiguration));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$conditionallyCreateConnectedStandardWifiEntry$18 */
    public /* synthetic */ void lambda$conditionallyCreateConnectedStandardWifiEntry$18$WifiPickerTracker(WifiInfo wifiInfo, NetworkInfo networkInfo, WifiConfiguration wifiConfiguration) {
        StandardWifiEntry standardWifiEntry = new StandardWifiEntry(this.mContext, this.mMainHandler, StandardWifiEntry.wifiConfigToStandardWifiEntryKey(wifiConfiguration), wifiConfiguration, this.mWifiManager, this.mWifiNetworkScoreCache, false);
        standardWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
        this.mStandardWifiEntryCache.put(standardWifiEntry.getKey(), standardWifiEntry);
    }

    private void conditionallyCreateConnectedSuggestedWifiEntry(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (!wifiInfo.isPasspointAp() && !wifiInfo.isOsuAp()) {
            this.mSuggestedConfigCache.values().stream().filter(new Predicate(wifiInfo.getNetworkId()) {
                /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$tJL8mW_AZGayOGupUDCgP3_UlzQ */
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return WifiPickerTracker.this.lambda$conditionallyCreateConnectedSuggestedWifiEntry$19$WifiPickerTracker(this.f$1, (WifiConfiguration) obj);
                }
            }).findAny().ifPresent(new Consumer(wifiInfo, networkInfo) {
                /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$BFLWShdPM2NcgUJrV0IHcg4MXWc */
                public final /* synthetic */ WifiInfo f$1;
                public final /* synthetic */ NetworkInfo f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WifiPickerTracker.this.lambda$conditionallyCreateConnectedSuggestedWifiEntry$20$WifiPickerTracker(this.f$1, this.f$2, (WifiConfiguration) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$conditionallyCreateConnectedSuggestedWifiEntry$19 */
    public /* synthetic */ boolean lambda$conditionallyCreateConnectedSuggestedWifiEntry$19$WifiPickerTracker(int i, WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.networkId == i && !this.mSuggestedWifiEntryCache.containsKey(StandardWifiEntry.wifiConfigToStandardWifiEntryKey(wifiConfiguration));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$conditionallyCreateConnectedSuggestedWifiEntry$20 */
    public /* synthetic */ void lambda$conditionallyCreateConnectedSuggestedWifiEntry$20$WifiPickerTracker(WifiInfo wifiInfo, NetworkInfo networkInfo, WifiConfiguration wifiConfiguration) {
        StandardWifiEntry standardWifiEntry = new StandardWifiEntry(this.mContext, this.mMainHandler, StandardWifiEntry.wifiConfigToStandardWifiEntryKey(wifiConfiguration), wifiConfiguration, this.mWifiManager, this.mWifiNetworkScoreCache, false);
        standardWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
        this.mSuggestedWifiEntryCache.put(standardWifiEntry.getKey(), standardWifiEntry);
    }

    private void conditionallyCreateConnectedPasspointWifiEntry(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (wifiInfo.isPasspointAp()) {
            Stream.concat(this.mWifiConfigCache.values().stream(), this.mSuggestedConfigCache.values().stream()).filter(new Predicate(wifiInfo.getNetworkId()) {
                /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$bjLRrQ4OQ8BTAnYZyvpe1EHxYpE */
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return WifiPickerTracker.this.lambda$conditionallyCreateConnectedPasspointWifiEntry$21$WifiPickerTracker(this.f$1, (WifiConfiguration) obj);
                }
            }).findAny().ifPresent(new Consumer(wifiInfo, networkInfo) {
                /* class com.android.wifitrackerlib.$$Lambda$WifiPickerTracker$g9rZ4NCkczW2OW23M8uCJ7orWlk */
                public final /* synthetic */ WifiInfo f$1;
                public final /* synthetic */ NetworkInfo f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    WifiPickerTracker.this.lambda$conditionallyCreateConnectedPasspointWifiEntry$22$WifiPickerTracker(this.f$1, this.f$2, (WifiConfiguration) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$conditionallyCreateConnectedPasspointWifiEntry$21 */
    public /* synthetic */ boolean lambda$conditionallyCreateConnectedPasspointWifiEntry$21$WifiPickerTracker(int i, WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.isPasspoint() && wifiConfiguration.networkId == i && !this.mPasspointWifiEntryCache.containsKey(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey()));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$conditionallyCreateConnectedPasspointWifiEntry$22 */
    public /* synthetic */ void lambda$conditionallyCreateConnectedPasspointWifiEntry$22$WifiPickerTracker(WifiInfo wifiInfo, NetworkInfo networkInfo, WifiConfiguration wifiConfiguration) {
        PasspointWifiEntry passpointWifiEntry;
        PasspointConfiguration passpointConfiguration = this.mPasspointConfigCache.get(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey()));
        if (passpointConfiguration != null) {
            passpointWifiEntry = new PasspointWifiEntry(this.mContext, this.mMainHandler, passpointConfiguration, this.mWifiManager, this.mWifiNetworkScoreCache, false);
        } else {
            passpointWifiEntry = new PasspointWifiEntry(this.mContext, this.mMainHandler, wifiConfiguration, this.mWifiManager, this.mWifiNetworkScoreCache, false);
        }
        passpointWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
        this.mPasspointWifiEntryCache.put(passpointWifiEntry.getKey(), passpointWifiEntry);
    }

    private void notifyOnWifiEntriesChanged() {
        WifiPickerTrackerCallback wifiPickerTrackerCallback = this.mListener;
        if (wifiPickerTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(wifiPickerTrackerCallback);
            handler.post(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$5hHTXQ3X9FmFmB6qtFasRAuw8jY */

                public final void run() {
                    WifiPickerTracker.WifiPickerTrackerCallback.this.onWifiEntriesChanged();
                }
            });
        }
    }

    private void notifyOnNumSavedNetworksChanged() {
        WifiPickerTrackerCallback wifiPickerTrackerCallback = this.mListener;
        if (wifiPickerTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(wifiPickerTrackerCallback);
            handler.post(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$fOs_tKYRPCSHYuhDjdNG_Oyno */

                public final void run() {
                    WifiPickerTracker.WifiPickerTrackerCallback.this.onNumSavedNetworksChanged();
                }
            });
        }
    }

    private void notifyOnNumSavedSubscriptionsChanged() {
        WifiPickerTrackerCallback wifiPickerTrackerCallback = this.mListener;
        if (wifiPickerTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(wifiPickerTrackerCallback);
            handler.post(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$pqRUdfn3mQVx4DYE6gYgaQJgj0E */

                public final void run() {
                    WifiPickerTracker.WifiPickerTrackerCallback.this.onNumSavedSubscriptionsChanged();
                }
            });
        }
    }
}
