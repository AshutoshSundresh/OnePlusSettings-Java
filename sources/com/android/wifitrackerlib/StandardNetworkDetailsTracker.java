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
import android.os.Handler;
import android.text.TextUtils;
import androidx.core.util.Preconditions;
import androidx.lifecycle.Lifecycle;
import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* access modifiers changed from: package-private */
public class StandardNetworkDetailsTracker extends NetworkDetailsTracker {
    private final StandardWifiEntry mChosenEntry;
    private NetworkInfo mCurrentNetworkInfo;

    StandardNetworkDetailsTracker(Lifecycle lifecycle, Context context, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, Handler handler, Handler handler2, Clock clock, long j, long j2, String str) {
        super(lifecycle, context, wifiManager, connectivityManager, networkScoreManager, handler, handler2, clock, j, j2, "StandardNetworkDetailsTracker");
        if (str.startsWith("NetworkRequestEntry:")) {
            this.mChosenEntry = new NetworkRequestEntry(this.mContext, this.mMainHandler, str, this.mWifiManager, this.mWifiNetworkScoreCache, false);
        } else {
            this.mChosenEntry = new StandardWifiEntry(this.mContext, this.mMainHandler, str, this.mWifiManager, this.mWifiNetworkScoreCache, false);
        }
        cacheNewScanResults();
        conditionallyUpdateScanResults(true);
        conditionallyUpdateConfig();
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        Network currentNetwork = this.mWifiManager.getCurrentNetwork();
        NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(currentNetwork);
        this.mCurrentNetworkInfo = networkInfo;
        this.mChosenEntry.updateConnectionInfo(connectionInfo, networkInfo);
        handleLinkPropertiesChanged(this.mConnectivityManager.getLinkProperties(currentNetwork));
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
        WifiConfiguration wifiConfiguration = (WifiConfiguration) intent.getExtra("wifiConfiguration");
        if (wifiConfiguration == null || !configMatches(wifiConfiguration)) {
            conditionallyUpdateConfig();
            return;
        }
        int intExtra = intent.getIntExtra("changeReason", -1);
        if (intExtra == 0 || intExtra == 2) {
            this.mChosenEntry.updateConfig(wifiConfiguration);
        } else if (intExtra == 1) {
            this.mChosenEntry.updateConfig(null);
        }
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
    public void handleRssiChangedAction() {
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
    public void handleNetworkCapabilitiesChanged(NetworkCapabilities networkCapabilities) {
        if (this.mChosenEntry.getConnectedState() == 2) {
            this.mChosenEntry.updateNetworkCapabilities(networkCapabilities);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.BaseWifiTracker
    public void handleNetworkScoreCacheUpdated() {
        this.mChosenEntry.onScoreCacheUpdated();
    }

    private void conditionallyUpdateScanResults(boolean z) {
        if (this.mWifiManager.getWifiState() == 1) {
            this.mChosenEntry.updateScanResultInfo(Collections.emptyList());
            return;
        }
        long j = this.mMaxScanAgeMillis;
        if (z) {
            cacheNewScanResults();
        } else {
            j += this.mScanIntervalMillis;
        }
        this.mChosenEntry.updateScanResultInfo(this.mScanResultUpdater.getScanResults(j));
    }

    private void conditionallyUpdateConfig() {
        this.mChosenEntry.updateConfig((WifiConfiguration) this.mWifiManager.getPrivilegedConfiguredNetworks().stream().filter(new Predicate() {
            /* class com.android.wifitrackerlib.$$Lambda$StandardNetworkDetailsTracker$Zbxm60gLtoXTwkhPsrGCaQl4hMc */

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return StandardNetworkDetailsTracker.this.configMatches((WifiConfiguration) obj);
            }
        }).findAny().orElse(null));
    }

    private void cacheNewScanResults() {
        this.mScanResultUpdater.update((List) this.mWifiManager.getScanResults().stream().filter(new Predicate() {
            /* class com.android.wifitrackerlib.$$Lambda$StandardNetworkDetailsTracker$pFn5Xq4o7c3wIEPexiRYP1yHlzY */

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return StandardNetworkDetailsTracker.this.lambda$cacheNewScanResults$0$StandardNetworkDetailsTracker((ScanResult) obj);
            }
        }).collect(Collectors.toList()));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$cacheNewScanResults$0 */
    public /* synthetic */ boolean lambda$cacheNewScanResults$0$StandardNetworkDetailsTracker(ScanResult scanResult) {
        return TextUtils.equals(scanResult.SSID, this.mChosenEntry.getSsid()) && Utils.getSecurityTypesFromScanResult(scanResult).contains(Integer.valueOf(this.mChosenEntry.getSecurity()));
    }

    /* access modifiers changed from: private */
    public boolean configMatches(WifiConfiguration wifiConfiguration) {
        String str;
        if (wifiConfiguration.isPasspoint()) {
            return false;
        }
        if (wifiConfiguration.fromWifiNetworkSpecifier) {
            str = NetworkRequestEntry.wifiConfigToNetworkRequestEntryKey(wifiConfiguration);
        } else {
            str = StandardWifiEntry.wifiConfigToStandardWifiEntryKey(wifiConfiguration);
        }
        return TextUtils.equals(str, this.mChosenEntry.getKey());
    }
}
