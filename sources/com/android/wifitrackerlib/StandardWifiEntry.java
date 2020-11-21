package com.android.wifitrackerlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkScoreManager;
import android.net.NetworkScorerAppData;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import androidx.core.util.Preconditions;
import com.android.internal.annotations.VisibleForTesting;
import com.android.wifitrackerlib.WifiEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@VisibleForTesting
public class StandardWifiEntry extends WifiEntry {
    private final Context mContext;
    private final List<ScanResult> mCurrentScanResults;
    private int mEapType;
    private boolean mIsUserShareable;
    private final String mKey;
    private final Object mLock;
    private int mPskType;
    private String mRecommendationServiceLabel;
    private final int mSecurity;
    private boolean mShouldAutoOpenCaptivePortal;
    private final String mSsid;
    private WifiConfiguration mWifiConfig;

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean isSubscription() {
        return false;
    }

    StandardWifiEntry(Context context, Handler handler, String str, List<ScanResult> list, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) throws IllegalArgumentException {
        this(context, handler, str, wifiManager, wifiNetworkScoreCache, z);
        Preconditions.checkNotNull(list, "Cannot construct with null ScanResult list!");
        if (!list.isEmpty()) {
            updateScanResultInfo(list);
            updateRecommendationServiceLabel();
            return;
        }
        throw new IllegalArgumentException("Cannot construct with empty ScanResult list!");
    }

    StandardWifiEntry(Context context, Handler handler, String str, WifiConfiguration wifiConfiguration, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) throws IllegalArgumentException {
        this(context, handler, str, wifiManager, wifiNetworkScoreCache, z);
        Preconditions.checkNotNull(wifiConfiguration, "Cannot construct with null config!");
        Preconditions.checkNotNull(wifiConfiguration.SSID, "Supplied config must have an SSID!");
        this.mWifiConfig = wifiConfiguration;
        updateRecommendationServiceLabel();
    }

    StandardWifiEntry(Context context, Handler handler, String str, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) {
        super(handler, wifiManager, wifiNetworkScoreCache, z);
        this.mLock = new Object();
        this.mCurrentScanResults = new ArrayList();
        this.mEapType = 2;
        this.mPskType = 3;
        this.mIsUserShareable = false;
        this.mShouldAutoOpenCaptivePortal = false;
        this.mContext = context;
        this.mKey = str;
        try {
            int indexOf = str.indexOf(":");
            int lastIndexOf = str.lastIndexOf(",");
            this.mSsid = str.substring(indexOf + 1, lastIndexOf);
            this.mSecurity = Integer.valueOf(str.substring(lastIndexOf + 1)).intValue();
            updateRecommendationServiceLabel();
        } catch (NumberFormatException | StringIndexOutOfBoundsException unused) {
            throw new IllegalArgumentException("Malformed key: " + str);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getKey() {
        return this.mKey;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getTitle() {
        return this.mSsid;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getSummary(boolean z) {
        StringJoiner stringJoiner = new StringJoiner(this.mContext.getString(R$string.summary_separator));
        if (!z && this.mForSavedNetworksPage && isSaved()) {
            String appLabel = Utils.getAppLabel(this.mContext, this.mWifiConfig.creatorName);
            if (!TextUtils.isEmpty(appLabel)) {
                stringJoiner.add(this.mContext.getString(R$string.saved_network, appLabel));
            }
        }
        if (getConnectedState() == 0) {
            String disconnectedStateDescription = Utils.getDisconnectedStateDescription(this.mContext, this);
            if (!TextUtils.isEmpty(disconnectedStateDescription)) {
                stringJoiner.add(disconnectedStateDescription);
            } else if (z) {
                stringJoiner.add(this.mContext.getString(R$string.wifi_disconnected));
            } else if (!this.mForSavedNetworksPage) {
                if (isSuggestion()) {
                    Context context = this.mContext;
                    String carrierNameForSubId = Utils.getCarrierNameForSubId(context, Utils.getSubIdForConfig(context, this.mWifiConfig));
                    String appLabel2 = Utils.getAppLabel(this.mContext, this.mWifiConfig.creatorName);
                    if (TextUtils.isEmpty(appLabel2)) {
                        appLabel2 = this.mWifiConfig.creatorName;
                    }
                    Context context2 = this.mContext;
                    int i = R$string.available_via_app;
                    Object[] objArr = new Object[1];
                    if (carrierNameForSubId == null) {
                        carrierNameForSubId = appLabel2;
                    }
                    objArr[0] = carrierNameForSubId;
                    stringJoiner.add(context2.getString(i, objArr));
                } else if (isSaved()) {
                    stringJoiner.add(this.mContext.getString(R$string.wifi_remembered));
                }
            }
        } else {
            String connectStateDescription = getConnectStateDescription();
            if (!TextUtils.isEmpty(connectStateDescription)) {
                stringJoiner.add(connectStateDescription);
            }
        }
        String speedDescription = Utils.getSpeedDescription(this.mContext, this);
        if (!TextUtils.isEmpty(speedDescription)) {
            stringJoiner.add(speedDescription);
        }
        String autoConnectDescription = Utils.getAutoConnectDescription(this.mContext, this);
        if (!TextUtils.isEmpty(autoConnectDescription)) {
            stringJoiner.add(autoConnectDescription);
        }
        String meteredDescription = Utils.getMeteredDescription(this.mContext, this);
        if (!TextUtils.isEmpty(meteredDescription)) {
            stringJoiner.add(meteredDescription);
        }
        if (!z) {
            String verboseLoggingDescription = Utils.getVerboseLoggingDescription(this);
            if (!TextUtils.isEmpty(verboseLoggingDescription)) {
                stringJoiner.add(verboseLoggingDescription);
            }
        }
        return stringJoiner.toString();
    }

    private String getConnectStateDescription() {
        if (getConnectedState() == 2) {
            WifiInfo wifiInfo = this.mWifiInfo;
            String str = null;
            String requestingPackageName = wifiInfo != null ? wifiInfo.getRequestingPackageName() : null;
            if (!TextUtils.isEmpty(requestingPackageName)) {
                WifiConfiguration wifiConfiguration = this.mWifiConfig;
                if (wifiConfiguration != null) {
                    Context context = this.mContext;
                    str = Utils.getCarrierNameForSubId(context, Utils.getSubIdForConfig(context, wifiConfiguration));
                }
                String appLabel = Utils.getAppLabel(this.mContext, requestingPackageName);
                if (!TextUtils.isEmpty(appLabel)) {
                    requestingPackageName = appLabel;
                }
                Context context2 = this.mContext;
                int i = R$string.connected_via_app;
                Object[] objArr = new Object[1];
                if (str == null) {
                    str = requestingPackageName;
                }
                objArr[0] = str;
                return context2.getString(i, objArr);
            } else if (isSaved() || isSuggestion()) {
                String currentNetworkCapabilitiesInformation = Utils.getCurrentNetworkCapabilitiesInformation(this.mContext, this.mNetworkCapabilities);
                if (!TextUtils.isEmpty(currentNetworkCapabilitiesInformation)) {
                    return currentNetworkCapabilitiesInformation;
                }
            } else if (TextUtils.isEmpty(this.mRecommendationServiceLabel)) {
                return this.mContext.getString(R$string.connected_via_network_scorer_default);
            } else {
                return String.format(this.mContext.getString(R$string.connected_via_network_scorer), this.mRecommendationServiceLabel);
            }
        }
        return Utils.getNetworkDetailedState(this.mContext, this.mNetworkInfo);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public CharSequence getSecondSummary() {
        return getConnectedState() == 2 ? Utils.getImsiProtectionDescription(this.mContext, getWifiConfiguration()) : "";
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getSsid() {
        return this.mSsid;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public int getSecurity() {
        return this.mSecurity;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getMacAddress() {
        if (this.mWifiConfig != null && getPrivacy() == 1) {
            return this.mWifiConfig.getRandomizedMacAddress().toString();
        }
        String[] factoryMacAddresses = this.mWifiManager.getFactoryMacAddresses();
        if (factoryMacAddresses.length > 0) {
            return factoryMacAddresses[0];
        }
        return null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean isMetered() {
        if (getMeteredChoice() == 1) {
            return true;
        }
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        return wifiConfiguration != null && wifiConfiguration.meteredHint;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean isSaved() {
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        return wifiConfiguration != null && !wifiConfiguration.isEphemeral();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean isSuggestion() {
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        return wifiConfiguration != null && wifiConfiguration.fromWifiNetworkSuggestion;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public WifiConfiguration getWifiConfiguration() {
        if (!isSaved()) {
            return null;
        }
        return this.mWifiConfig;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public WifiEntry.ConnectedInfo getConnectedInfo() {
        return this.mConnectedInfo;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canConnect() {
        return this.mLevel != -1 && getConnectedState() == 0;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void connect(WifiEntry.ConnectCallback connectCallback) {
        this.mConnectCallback = connectCallback;
        this.mShouldAutoOpenCaptivePortal = true;
        if (isSaved() || isSuggestion()) {
            this.mWifiManager.connect(this.mWifiConfig.networkId, new WifiEntry.ConnectActionListener());
            return;
        }
        int i = this.mSecurity;
        if (i == 0 || i == 4) {
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = "\"" + this.mSsid + "\"";
            if (this.mSecurity == 4) {
                wifiConfiguration.allowedKeyManagement.set(9);
                wifiConfiguration.requirePmf = true;
            } else {
                wifiConfiguration.allowedKeyManagement.set(0);
            }
            this.mWifiManager.connect(wifiConfiguration, new WifiEntry.ConnectActionListener());
        } else if (connectCallback != null) {
            this.mCallbackHandler.post(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$StandardWifiEntry$RuC7yREceMbI61_zVW5EXFuJHA */

                public final void run() {
                    WifiEntry.ConnectCallback.this.onConnectResult(1);
                }
            });
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canDisconnect() {
        return getConnectedState() == 2;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void disconnect(WifiEntry.DisconnectCallback disconnectCallback) {
        if (canDisconnect()) {
            this.mCalledDisconnect = true;
            this.mDisconnectCallback = disconnectCallback;
            this.mCallbackHandler.postDelayed(new Runnable(disconnectCallback) {
                /* class com.android.wifitrackerlib.$$Lambda$StandardWifiEntry$UZGGZY6miJgeSSJxai0hhutNNIQ */
                public final /* synthetic */ WifiEntry.DisconnectCallback f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    StandardWifiEntry.this.lambda$disconnect$1$StandardWifiEntry(this.f$1);
                }
            }, 10000);
            this.mWifiManager.disableEphemeralNetwork(this.mWifiConfig.SSID);
            this.mWifiManager.disconnect();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$disconnect$1 */
    public /* synthetic */ void lambda$disconnect$1$StandardWifiEntry(WifiEntry.DisconnectCallback disconnectCallback) {
        if (disconnectCallback != null && this.mCalledDisconnect) {
            disconnectCallback.onDisconnectResult(1);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canForget() {
        return getWifiConfiguration() != null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void forget(WifiEntry.ForgetCallback forgetCallback) {
        if (canForget()) {
            this.mForgetCallback = forgetCallback;
            this.mWifiManager.forget(this.mWifiConfig.networkId, new WifiEntry.ForgetActionListener());
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSignIn() {
        NetworkCapabilities networkCapabilities = this.mNetworkCapabilities;
        return networkCapabilities != null && networkCapabilities.hasCapability(17);
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void signIn(WifiEntry.SignInCallback signInCallback) {
        if (canSignIn()) {
            ((ConnectivityManager) this.mContext.getSystemService("connectivity")).startCaptivePortalApp(this.mWifiManager.getCurrentNetwork());
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canShare() {
        if (getWifiConfiguration() == null) {
            return false;
        }
        int i = this.mSecurity;
        if (i == 0 || i == 1 || i == 2 || i == 4 || i == 5) {
            return true;
        }
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canEasyConnect() {
        if (getWifiConfiguration() == null || !this.mWifiManager.isEasyConnectSupported()) {
            return false;
        }
        int i = this.mSecurity;
        if (i == 2 || i == 5) {
            return true;
        }
        return false;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public int getMeteredChoice() {
        if (getWifiConfiguration() == null) {
            return 0;
        }
        int i = getWifiConfiguration().meteredOverride;
        if (i == 1) {
            return 1;
        }
        return i == 2 ? 2 : 0;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSetMeteredChoice() {
        return getWifiConfiguration() != null;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void setMeteredChoice(int i) {
        if (canSetMeteredChoice()) {
            if (i == 0) {
                this.mWifiConfig.meteredOverride = 0;
            } else if (i == 1) {
                this.mWifiConfig.meteredOverride = 1;
            } else if (i == 2) {
                this.mWifiConfig.meteredOverride = 2;
            }
            this.mWifiManager.save(this.mWifiConfig, null);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSetPrivacy() {
        return isSaved();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public int getPrivacy() {
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        return (wifiConfiguration == null || wifiConfiguration.macRandomizationSetting != 0) ? 1 : 0;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void setPrivacy(int i) {
        if (canSetPrivacy()) {
            WifiConfiguration wifiConfiguration = this.mWifiConfig;
            int i2 = 1;
            if (i != 1) {
                i2 = 0;
            }
            wifiConfiguration.macRandomizationSetting = i2;
            this.mWifiManager.save(this.mWifiConfig, null);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean isAutoJoinEnabled() {
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        if (wifiConfiguration == null) {
            return false;
        }
        return wifiConfiguration.allowAutojoin;
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean canSetAutoJoinEnabled() {
        return isSaved() || isSuggestion();
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public void setAutoJoinEnabled(boolean z) {
        if (canSetAutoJoinEnabled()) {
            this.mWifiManager.allowAutojoin(this.mWifiConfig.networkId, z);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public String getSecurityString(boolean z) {
        switch (this.mSecurity) {
            case 1:
                return this.mContext.getString(R$string.wifi_security_wep);
            case 2:
                int i = this.mPskType;
                if (i != 0) {
                    if (i != 1) {
                        if (z) {
                            return this.mContext.getString(R$string.wifi_security_short_wpa_wpa2_wpa3);
                        }
                        return this.mContext.getString(R$string.wifi_security_wpa_wpa2_wpa3);
                    } else if (z) {
                        return this.mContext.getString(R$string.wifi_security_short_wpa2_wpa3);
                    } else {
                        return this.mContext.getString(R$string.wifi_security_wpa2_wpa3);
                    }
                } else if (z) {
                    return this.mContext.getString(R$string.wifi_security_short_wpa);
                } else {
                    return this.mContext.getString(R$string.wifi_security_wpa);
                }
            case 3:
                int i2 = this.mEapType;
                if (i2 != 0) {
                    if (i2 != 1) {
                        if (z) {
                            return this.mContext.getString(R$string.wifi_security_short_eap);
                        }
                        return this.mContext.getString(R$string.wifi_security_eap);
                    } else if (z) {
                        return this.mContext.getString(R$string.wifi_security_short_eap_wpa2_wpa3);
                    } else {
                        return this.mContext.getString(R$string.wifi_security_eap_wpa2_wpa3);
                    }
                } else if (z) {
                    return this.mContext.getString(R$string.wifi_security_short_eap_wpa);
                } else {
                    return this.mContext.getString(R$string.wifi_security_eap_wpa);
                }
            case 4:
                if (z) {
                    return this.mContext.getString(R$string.wifi_security_short_owe);
                }
                return this.mContext.getString(R$string.wifi_security_owe);
            case 5:
                if (z) {
                    return this.mContext.getString(R$string.wifi_security_short_sae);
                }
                return this.mContext.getString(R$string.wifi_security_sae);
            case 6:
                if (z) {
                    return this.mContext.getString(R$string.wifi_security_short_eap_suiteb);
                }
                return this.mContext.getString(R$string.wifi_security_eap_suiteb);
            case 7:
            default:
                if (z) {
                    return "";
                }
                return this.mContext.getString(R$string.wifi_security_none);
            case 8:
                return this.mContext.getString(R$string.wifi_security_wapi_psk);
            case 9:
                return this.mContext.getString(R$string.wifi_security_wapi_cert);
        }
    }

    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean shouldEditBeforeConnect() {
        WifiConfiguration wifiConfiguration = getWifiConfiguration();
        if (wifiConfiguration == null) {
            return false;
        }
        if (getSecurity() != 0 && getSecurity() != 4 && !wifiConfiguration.getNetworkSelectionStatus().hasEverConnected()) {
            return true;
        }
        WifiConfiguration.NetworkSelectionStatus networkSelectionStatus = wifiConfiguration.getNetworkSelectionStatus();
        if (networkSelectionStatus.getNetworkSelectionStatus() == 0 || (networkSelectionStatus.getDisableReasonCounter(2) <= 0 && networkSelectionStatus.getDisableReasonCounter(8) <= 0 && networkSelectionStatus.getDisableReasonCounter(5) <= 0)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void updateScanResultInfo(List<ScanResult> list) throws IllegalArgumentException {
        if (list == null) {
            list = new ArrayList<>();
        }
        for (ScanResult scanResult : list) {
            if (!TextUtils.equals(scanResult.SSID, this.mSsid)) {
                throw new IllegalArgumentException("Attempted to update with wrong SSID! Expected: " + this.mSsid + ", Actual: " + scanResult.SSID + ", ScanResult: " + scanResult);
            }
        }
        synchronized (this.mLock) {
            this.mCurrentScanResults.clear();
            this.mCurrentScanResults.addAll(list);
        }
        ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(list);
        if (bestScanResultByLevel != null) {
            updateEapType(bestScanResultByLevel);
            updatePskType(bestScanResultByLevel);
            updateTransitionModeCapa(bestScanResultByLevel);
        }
        if (getConnectedState() == 0) {
            this.mLevel = bestScanResultByLevel != null ? this.mWifiManager.calculateSignalLevel(bestScanResultByLevel.level) : -1;
            synchronized (this.mLock) {
                this.mSpeed = Utils.getAverageSpeedFromScanResults(this.mScoreCache, this.mCurrentScanResults);
            }
        }
        updateWifiGenerationInfo(this.mCurrentScanResults);
        notifyOnUpdated();
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.wifitrackerlib.WifiEntry
    public void updateNetworkCapabilities(NetworkCapabilities networkCapabilities) {
        super.updateNetworkCapabilities(networkCapabilities);
        if (canSignIn() && this.mShouldAutoOpenCaptivePortal) {
            this.mShouldAutoOpenCaptivePortal = false;
            signIn(null);
        }
    }

    /* access modifiers changed from: package-private */
    public void onScoreCacheUpdated() {
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null) {
            this.mSpeed = Utils.getSpeedFromWifiInfo(this.mScoreCache, wifiInfo);
        } else {
            synchronized (this.mLock) {
                this.mSpeed = Utils.getAverageSpeedFromScanResults(this.mScoreCache, this.mCurrentScanResults);
            }
        }
        notifyOnUpdated();
    }

    private void updateEapType(ScanResult scanResult) {
        if (scanResult.capabilities.contains("RSN-EAP")) {
            this.mEapType = 1;
        } else if (scanResult.capabilities.contains("WPA-EAP")) {
            this.mEapType = 0;
        } else {
            this.mEapType = 2;
        }
    }

    private void updatePskType(ScanResult scanResult) {
        if (this.mSecurity != 2) {
            this.mPskType = 3;
            return;
        }
        boolean contains = scanResult.capabilities.contains("WPA-PSK");
        boolean contains2 = scanResult.capabilities.contains("RSN-PSK");
        if (contains2 && contains) {
            this.mPskType = 2;
        } else if (contains2) {
            this.mPskType = 1;
        } else if (contains) {
            this.mPskType = 0;
        } else {
            this.mPskType = 3;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateConfig(WifiConfiguration wifiConfiguration) throws IllegalArgumentException {
        if (wifiConfiguration != null) {
            if (!TextUtils.equals(this.mSsid, WifiInfo.sanitizeSsid(wifiConfiguration.SSID))) {
                throw new IllegalArgumentException("Attempted to update with wrong SSID! Expected: " + this.mSsid + ", Actual: " + WifiInfo.sanitizeSsid(wifiConfiguration.SSID) + ", Config: " + wifiConfiguration);
            } else if (this.mSecurity != Utils.getSecurityTypeFromWifiConfiguration(wifiConfiguration)) {
                throw new IllegalArgumentException("Attempted to update with wrong security! Expected: " + this.mSecurity + ", Actual: " + Utils.getSecurityTypeFromWifiConfiguration(wifiConfiguration) + ", Config: " + wifiConfiguration);
            }
        }
        this.mWifiConfig = wifiConfiguration;
        notifyOnUpdated();
    }

    /* access modifiers changed from: package-private */
    public void setUserShareable(boolean z) {
        this.mIsUserShareable = z;
    }

    /* access modifiers changed from: package-private */
    public boolean isUserShareable() {
        return this.mIsUserShareable;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.wifitrackerlib.WifiEntry
    public boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        WifiConfiguration wifiConfiguration;
        if (wifiInfo.isPasspointAp() || wifiInfo.isOsuAp() || (wifiConfiguration = this.mWifiConfig) == null || wifiConfiguration.networkId != wifiInfo.getNetworkId()) {
            return false;
        }
        return true;
    }

    private void updateRecommendationServiceLabel() {
        NetworkScorerAppData activeScorer = ((NetworkScoreManager) this.mContext.getSystemService("network_score")).getActiveScorer();
        if (activeScorer != null) {
            this.mRecommendationServiceLabel = activeScorer.getRecommendationServiceLabel();
        }
    }

    static String ssidAndSecurityToStandardWifiEntryKey(String str, int i) {
        return "StandardWifiEntry:" + str + "," + i;
    }

    static String wifiConfigToStandardWifiEntryKey(WifiConfiguration wifiConfiguration) {
        Preconditions.checkNotNull(wifiConfiguration, "Cannot create key with null config!");
        Preconditions.checkNotNull(wifiConfiguration.SSID, "Cannot create key with null SSID in config!");
        return "StandardWifiEntry:" + WifiInfo.sanitizeSsid(wifiConfiguration.SSID) + "," + Utils.getSecurityTypeFromWifiConfiguration(wifiConfiguration);
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.wifitrackerlib.WifiEntry
    public String getScanResultDescription() {
        synchronized (this.mLock) {
            if (this.mCurrentScanResults.size() == 0) {
                return "";
            }
            return "[" + getScanResultDescription(2400, 2500) + ";" + getScanResultDescription(4900, 5900) + ";" + getScanResultDescription(5925, 7125) + "]";
        }
    }

    private String getScanResultDescription(int i, int i2) {
        List list;
        synchronized (this.mLock) {
            list = (List) this.mCurrentScanResults.stream().filter(new Predicate(i, i2) {
                /* class com.android.wifitrackerlib.$$Lambda$StandardWifiEntry$lKgEQcmtM1x3SpHuutK3I2nfI0 */
                public final /* synthetic */ int f$0;
                public final /* synthetic */ int f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return StandardWifiEntry.lambda$getScanResultDescription$2(this.f$0, this.f$1, (ScanResult) obj);
                }
            }).sorted(Comparator.comparingInt($$Lambda$StandardWifiEntry$Lr4BrIBW8EpwljEjYsXvjwUzPU.INSTANCE)).collect(Collectors.toList());
        }
        int size = list.size();
        if (size == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(size);
        sb.append(")");
        if (size > 4) {
            int asInt = list.stream().mapToInt($$Lambda$StandardWifiEntry$ulMGK6KYyQVXHFy8lpHK9UIg2Q4.INSTANCE).max().getAsInt();
            sb.append("max=");
            sb.append(asInt);
            sb.append(",");
        }
        list.forEach(new Consumer(sb, SystemClock.elapsedRealtime()) {
            /* class com.android.wifitrackerlib.$$Lambda$StandardWifiEntry$HDaxgAFxNOzpZGjcKD6Vxnrfnp4 */
            public final /* synthetic */ StringBuilder f$1;
            public final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                StandardWifiEntry.this.lambda$getScanResultDescription$5$StandardWifiEntry(this.f$1, this.f$2, (ScanResult) obj);
            }
        });
        return sb.toString();
    }

    static /* synthetic */ boolean lambda$getScanResultDescription$2(int i, int i2, ScanResult scanResult) {
        int i3 = scanResult.frequency;
        return i3 >= i && i3 <= i2;
    }

    static /* synthetic */ int lambda$getScanResultDescription$3(ScanResult scanResult) {
        return scanResult.level * -1;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getScanResultDescription$5 */
    public /* synthetic */ void lambda$getScanResultDescription$5$StandardWifiEntry(StringBuilder sb, long j, ScanResult scanResult) {
        sb.append(getScanResultDescription(scanResult, j));
    }

    private String getScanResultDescription(ScanResult scanResult, long j) {
        StringBuilder sb = new StringBuilder();
        sb.append(" \n{");
        sb.append(scanResult.BSSID);
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null && scanResult.BSSID.equals(wifiInfo.getBSSID())) {
            sb.append("*");
        }
        sb.append("=");
        sb.append(scanResult.frequency);
        sb.append(",");
        sb.append(scanResult.level);
        sb.append(",");
        sb.append(((int) (j - (scanResult.timestamp / 1000))) / 1000);
        sb.append("s");
        sb.append("}");
        return sb.toString();
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.wifitrackerlib.WifiEntry
    public String getNetworkSelectionDescription() {
        return Utils.getNetworkSelectionDescription(getWifiConfiguration());
    }
}
