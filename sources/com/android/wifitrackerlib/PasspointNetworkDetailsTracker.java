package com.android.wifitrackerlib;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
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
import android.util.Pair;
import androidx.core.util.Preconditions;
import androidx.lifecycle.Lifecycle;
import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/* access modifiers changed from: package-private */
public class PasspointNetworkDetailsTracker extends NetworkDetailsTracker {
    private final PasspointWifiEntry mChosenEntry;
    private NetworkInfo mCurrentNetworkInfo;
    private OsuWifiEntry mOsuWifiEntry;

    PasspointNetworkDetailsTracker(Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, Handler handler, Handler handler2, Clock clock, long j, long j2, String str) {
        super(lifecycle, context, wifiManager, connectivityManager, networkScoreManager, handler, handler2, clock, j, j2, "PasspointNetworkDetailsTracker");
        Optional<PasspointConfiguration> findAny = this.mWifiManager.getPasspointConfigurations().stream().filter(new Predicate(str) {
            /* class com.android.wifitrackerlib.$$Lambda$PasspointNetworkDetailsTracker$f6N1PzciB6IOqUxbZjrv70YfKo */
            public final /* synthetic */ String f$0;

            {
                this.f$0 = r1;
            }

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return TextUtils.equals(this.f$0, PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(((PasspointConfiguration) obj).getUniqueId()));
            }
        }).findAny();
        if (findAny.isPresent()) {
            this.mChosenEntry = new PasspointWifiEntry(this.mContext, this.mMainHandler, findAny.get(), this.mWifiManager, this.mWifiNetworkScoreCache, false);
        } else {
            Optional findAny2 = this.mWifiManager.getPrivilegedConfiguredNetworks().stream().filter(new Predicate(str) {
                /* class com.android.wifitrackerlib.$$Lambda$PasspointNetworkDetailsTracker$f3K9fctj5iLndELi8zSmqMZdkI */
                public final /* synthetic */ String f$0;

                {
                    this.f$0 = r1;
                }

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return PasspointNetworkDetailsTracker.lambda$new$1(this.f$0, (WifiConfiguration) obj);
                }
            }).findAny();
            if (findAny2.isPresent()) {
                this.mChosenEntry = new PasspointWifiEntry(this.mContext, this.mMainHandler, (WifiConfiguration) findAny2.get(), this.mWifiManager, this.mWifiNetworkScoreCache, false);
            } else {
                throw new IllegalArgumentException("Cannot find config for given PasspointWifiEntry key!");
            }
        }
        cacheNewScanResults();
        conditionallyUpdateScanResults(true);
        conditionallyUpdateConfig();
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(this.mWifiManager.getCurrentNetwork());
        this.mCurrentNetworkInfo = networkInfo;
        this.mChosenEntry.updateConnectionInfo(connectionInfo, networkInfo);
        handleLinkPropertiesChanged(this.mConnectivityManager.getLinkProperties(this.mWifiManager.getCurrentNetwork()));
    }

    static /* synthetic */ boolean lambda$new$1(String str, WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.isPasspoint() && TextUtils.equals(str, PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey()));
    }

    @Override // com.android.wifitrackerlib.NetworkDetailsTracker
    public WifiEntry getWifiEntry() {
        return this.mChosenEntry;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleWifiStateChangedAction() {
        conditionallyUpdateScanResults(true);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleScanResultsAvailableAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        conditionallyUpdateScanResults(intent.getBooleanExtra("resultsUpdated", true));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleConfiguredNetworksChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        conditionallyUpdateConfig();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleRssiChangedAction() {
        this.mChosenEntry.updateConnectionInfo(this.mWifiManager.getConnectionInfo(), this.mCurrentNetworkInfo);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleNetworkStateChangedAction(Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null!");
        this.mCurrentNetworkInfo = (NetworkInfo) intent.getExtra("networkInfo");
        this.mChosenEntry.updateConnectionInfo(this.mWifiManager.getConnectionInfo(), this.mCurrentNetworkInfo);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleLinkPropertiesChanged(LinkProperties linkProperties) {
        if (this.mChosenEntry.getConnectedState() == 2) {
            this.mChosenEntry.updateLinkProperties(linkProperties);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleNetworkScoreCacheUpdated() {
        this.mChosenEntry.onScoreCacheUpdated();
    }

    private void updatePasspointWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        for (Pair pair : this.mWifiManager.getAllMatchingWifiConfigs(list)) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
            if (TextUtils.equals(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey()), this.mChosenEntry.getKey())) {
                this.mChosenEntry.updateScanResultInfo(wifiConfiguration, (List) ((Map) pair.second).get(0), (List) ((Map) pair.second).get(1));
                return;
            }
        }
        this.mChosenEntry.updateScanResultInfo(null, null, null);
    }

    private void updateOsuWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        Map matchingOsuProviders = this.mWifiManager.getMatchingOsuProviders(list);
        Map matchingPasspointConfigsForOsuProviders = this.mWifiManager.getMatchingPasspointConfigsForOsuProviders(matchingOsuProviders.keySet());
        OsuWifiEntry osuWifiEntry = this.mOsuWifiEntry;
        if (osuWifiEntry != null) {
            osuWifiEntry.updateScanResultInfo((List) matchingOsuProviders.get(osuWifiEntry.getOsuProvider()));
        } else {
            for (OsuProvider osuProvider : matchingOsuProviders.keySet()) {
                PasspointConfiguration passpointConfiguration = (PasspointConfiguration) matchingPasspointConfigsForOsuProviders.get(osuProvider);
                if (passpointConfiguration != null && TextUtils.equals(this.mChosenEntry.getKey(), PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()))) {
                    OsuWifiEntry osuWifiEntry2 = new OsuWifiEntry(this.mContext, this.mMainHandler, osuProvider, this.mWifiManager, this.mWifiNetworkScoreCache, false);
                    this.mOsuWifiEntry = osuWifiEntry2;
                    osuWifiEntry2.updateScanResultInfo((List) matchingOsuProviders.get(osuProvider));
                    this.mOsuWifiEntry.setAlreadyProvisioned(true);
                    this.mChosenEntry.setOsuWifiEntry(this.mOsuWifiEntry);
                    return;
                }
            }
        }
        OsuWifiEntry osuWifiEntry3 = this.mOsuWifiEntry;
        if (osuWifiEntry3 != null && osuWifiEntry3.getLevel() == -1) {
            this.mChosenEntry.setOsuWifiEntry(null);
            this.mOsuWifiEntry = null;
        }
    }

    private void conditionallyUpdateScanResults(boolean z) {
        if (this.mWifiManager.getWifiState() == 1) {
            this.mChosenEntry.updateScanResultInfo(null, Collections.emptyList(), Collections.emptyList());
            return;
        }
        long j = this.mMaxScanAgeMillis;
        if (z) {
            cacheNewScanResults();
        } else {
            j += this.mScanIntervalMillis;
        }
        List<ScanResult> scanResults = this.mScanResultUpdater.getScanResults(j);
        updatePasspointWifiEntryScans(scanResults);
        updateOsuWifiEntryScans(scanResults);
    }

    private void conditionallyUpdateConfig() {
        this.mWifiManager.getPasspointConfigurations().stream().filter(new Predicate() {
            /* class com.android.wifitrackerlib.$$Lambda$PasspointNetworkDetailsTracker$0W7xlLKDDotyH98hU3W2TjOfq8c */

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return PasspointNetworkDetailsTracker.this.lambda$conditionallyUpdateConfig$2$PasspointNetworkDetailsTracker((PasspointConfiguration) obj);
            }
        }).findAny().ifPresent(new Consumer() {
            /* class com.android.wifitrackerlib.$$Lambda$PasspointNetworkDetailsTracker$GW4OvaOI_VBi991zq1zNrs27hE */

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                PasspointNetworkDetailsTracker.this.lambda$conditionallyUpdateConfig$3$PasspointNetworkDetailsTracker((PasspointConfiguration) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$conditionallyUpdateConfig$2 */
    public /* synthetic */ boolean lambda$conditionallyUpdateConfig$2$PasspointNetworkDetailsTracker(PasspointConfiguration passpointConfiguration) {
        return TextUtils.equals(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()), this.mChosenEntry.getKey());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$conditionallyUpdateConfig$3 */
    public /* synthetic */ void lambda$conditionallyUpdateConfig$3$PasspointNetworkDetailsTracker(PasspointConfiguration passpointConfiguration) {
        this.mChosenEntry.updatePasspointConfig(passpointConfiguration);
    }

    private void cacheNewScanResults() {
        this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
    }
}
