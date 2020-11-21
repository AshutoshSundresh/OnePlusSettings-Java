package com.android.wifitrackerlib;

import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Handler;
import androidx.core.util.Preconditions;
import com.android.wifitrackerlib.WifiEntry;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public abstract class WifiEntry implements Comparable<WifiEntry> {
    protected Handler mCallbackHandler;
    protected boolean mCalledConnect = false;
    protected boolean mCalledDisconnect = false;
    protected ConnectCallback mConnectCallback;
    protected ConnectedInfo mConnectedInfo;
    private int mDeviceWifiStandard;
    protected DisconnectCallback mDisconnectCallback;
    final boolean mForSavedNetworksPage;
    protected ForgetCallback mForgetCallback;
    private boolean mHe8ssCapableAp;
    private boolean mIsOweTransitionMode;
    private boolean mIsPskSaeTransitionMode;
    protected int mLevel = -1;
    private WifiEntryCallback mListener;
    protected NetworkCapabilities mNetworkCapabilities;
    protected NetworkInfo mNetworkInfo;
    protected WifiNetworkScoreCache mScoreCache;
    protected int mSpeed = 0;
    private boolean mVhtMax8SpatialStreamsSupport;
    protected WifiInfo mWifiInfo;
    protected final WifiManager mWifiManager;
    private int mWifiStandard = 1;

    public interface ConnectCallback {
        void onConnectResult(int i);
    }

    public interface DisconnectCallback {
        void onDisconnectResult(int i);
    }

    public interface ForgetCallback {
        void onForgetResult(int i);
    }

    public interface SignInCallback {
    }

    public interface WifiEntryCallback {
        void onUpdated();
    }

    public abstract boolean canConnect();

    public abstract boolean canDisconnect();

    public abstract boolean canEasyConnect();

    public abstract boolean canForget();

    public boolean canManageSubscription() {
        return false;
    }

    public abstract boolean canSetAutoJoinEnabled();

    public abstract boolean canSetMeteredChoice();

    public abstract boolean canSetPrivacy();

    public abstract boolean canShare();

    public abstract boolean canSignIn();

    public abstract void connect(ConnectCallback connectCallback);

    /* access modifiers changed from: protected */
    public abstract boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo);

    public abstract void disconnect(DisconnectCallback disconnectCallback);

    public abstract void forget(ForgetCallback forgetCallback);

    public String getHelpUriString() {
        return null;
    }

    public abstract String getKey();

    public abstract String getMacAddress();

    public abstract int getMeteredChoice();

    /* access modifiers changed from: package-private */
    public String getNetworkSelectionDescription() {
        return "";
    }

    public abstract int getPrivacy();

    /* access modifiers changed from: package-private */
    public abstract String getScanResultDescription();

    public CharSequence getSecondSummary() {
        return "";
    }

    public abstract int getSecurity();

    public abstract String getSecurityString(boolean z);

    public abstract String getSsid();

    public abstract String getSummary(boolean z);

    public abstract String getTitle();

    public abstract WifiConfiguration getWifiConfiguration();

    public abstract boolean isAutoJoinEnabled();

    public abstract boolean isMetered();

    public abstract boolean isSaved();

    public abstract boolean isSubscription();

    public abstract boolean isSuggestion();

    public void manageSubscription() {
    }

    public abstract void setAutoJoinEnabled(boolean z);

    public abstract void setMeteredChoice(int i);

    public abstract void setPrivacy(int i);

    public boolean shouldEditBeforeConnect() {
        return false;
    }

    public abstract void signIn(SignInCallback signInCallback);

    WifiEntry(Handler handler, WifiManager wifiManager, WifiNetworkScoreCache wifiNetworkScoreCache, boolean z) throws IllegalArgumentException {
        Preconditions.checkNotNull(handler, "Cannot construct with null handler!");
        Preconditions.checkNotNull(wifiManager, "Cannot construct with null WifiManager!");
        this.mCallbackHandler = handler;
        this.mForSavedNetworksPage = z;
        this.mWifiManager = wifiManager;
        this.mScoreCache = wifiNetworkScoreCache;
        updatetDeviceWifiGenerationInfo();
    }

    public int getConnectedState() {
        NetworkInfo networkInfo = this.mNetworkInfo;
        if (networkInfo == null) {
            return 0;
        }
        switch (AnonymousClass1.$SwitchMap$android$net$NetworkInfo$DetailedState[networkInfo.getDetailedState().ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return 1;
            case 7:
                return 2;
            default:
                return 0;
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.android.wifitrackerlib.WifiEntry$1  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$net$NetworkInfo$DetailedState;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|16) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                android.net.NetworkInfo$DetailedState[] r0 = android.net.NetworkInfo.DetailedState.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.android.wifitrackerlib.WifiEntry.AnonymousClass1.$SwitchMap$android$net$NetworkInfo$DetailedState = r0
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.SCANNING     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = com.android.wifitrackerlib.WifiEntry.AnonymousClass1.$SwitchMap$android$net$NetworkInfo$DetailedState     // Catch:{ NoSuchFieldError -> 0x001d }
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.CONNECTING     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = com.android.wifitrackerlib.WifiEntry.AnonymousClass1.$SwitchMap$android$net$NetworkInfo$DetailedState     // Catch:{ NoSuchFieldError -> 0x0028 }
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.AUTHENTICATING     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = com.android.wifitrackerlib.WifiEntry.AnonymousClass1.$SwitchMap$android$net$NetworkInfo$DetailedState     // Catch:{ NoSuchFieldError -> 0x0033 }
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.OBTAINING_IPADDR     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = com.android.wifitrackerlib.WifiEntry.AnonymousClass1.$SwitchMap$android$net$NetworkInfo$DetailedState     // Catch:{ NoSuchFieldError -> 0x003e }
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.VERIFYING_POOR_LINK     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = com.android.wifitrackerlib.WifiEntry.AnonymousClass1.$SwitchMap$android$net$NetworkInfo$DetailedState     // Catch:{ NoSuchFieldError -> 0x0049 }
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = com.android.wifitrackerlib.WifiEntry.AnonymousClass1.$SwitchMap$android$net$NetworkInfo$DetailedState     // Catch:{ NoSuchFieldError -> 0x0054 }
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.CONNECTED     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.WifiEntry.AnonymousClass1.<clinit>():void");
        }
    }

    public String getSummary() {
        return getSummary(true);
    }

    public int getLevel() {
        return this.mLevel;
    }

    public int getSpeed() {
        return this.mSpeed;
    }

    public ConnectedInfo getConnectedInfo() {
        if (getConnectedState() != 2) {
            return null;
        }
        return this.mConnectedInfo;
    }

    public static class ConnectedInfo {
        public List<String> dnsServers;
        public int frequencyMhz;
        public String gateway;
        public String ipAddress;
        public List<String> ipv6Addresses;
        public int linkSpeedMbps;
        public String subnetMask;

        public ConnectedInfo() {
            new ArrayList();
            new ArrayList();
        }
    }

    public void setListener(WifiEntryCallback wifiEntryCallback) {
        this.mListener = wifiEntryCallback;
    }

    /* access modifiers changed from: protected */
    public void notifyOnUpdated() {
        if (this.mListener != null) {
            this.mCallbackHandler.post(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$WifiEntry$Z7qIuj7K1pqbGQNunqibzqO18s0 */

                public final void run() {
                    WifiEntry.this.lambda$notifyOnUpdated$0$WifiEntry();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$notifyOnUpdated$0 */
    public /* synthetic */ void lambda$notifyOnUpdated$0$WifiEntry() {
        this.mListener.onUpdated();
    }

    /* access modifiers changed from: package-private */
    public void updateConnectionInfo(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (wifiInfo == null || networkInfo == null || !connectionInfoMatches(wifiInfo, networkInfo)) {
            this.mNetworkInfo = null;
            this.mNetworkCapabilities = null;
            this.mConnectedInfo = null;
            if (this.mCalledDisconnect) {
                this.mCalledDisconnect = false;
                this.mCallbackHandler.post(new Runnable() {
                    /* class com.android.wifitrackerlib.$$Lambda$WifiEntry$Wbjrmqbh0TpH4DVTSRzGNL2Aks */

                    public final void run() {
                        WifiEntry.this.lambda$updateConnectionInfo$2$WifiEntry();
                    }
                });
            }
        } else {
            this.mWifiInfo = wifiInfo;
            this.mNetworkInfo = networkInfo;
            int rssi = wifiInfo.getRssi();
            if (rssi != -127) {
                this.mLevel = this.mWifiManager.calculateSignalLevel(rssi);
                this.mSpeed = Utils.getSpeedFromWifiInfo(this.mScoreCache, wifiInfo);
            }
            if (getConnectedState() == 2) {
                if (this.mCalledConnect) {
                    this.mCalledConnect = false;
                    this.mCallbackHandler.post(new Runnable() {
                        /* class com.android.wifitrackerlib.$$Lambda$WifiEntry$mJZ4Rvce3rMD9mRMpzvWu5ItY */

                        public final void run() {
                            WifiEntry.this.lambda$updateConnectionInfo$1$WifiEntry();
                        }
                    });
                }
                if (this.mConnectedInfo == null) {
                    this.mConnectedInfo = new ConnectedInfo();
                }
                this.mConnectedInfo.frequencyMhz = wifiInfo.getFrequency();
                this.mConnectedInfo.linkSpeedMbps = wifiInfo.getLinkSpeed();
            }
        }
        notifyOnUpdated();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateConnectionInfo$1 */
    public /* synthetic */ void lambda$updateConnectionInfo$1$WifiEntry() {
        ConnectCallback connectCallback = this.mConnectCallback;
        if (connectCallback != null) {
            connectCallback.onConnectResult(0);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateConnectionInfo$2 */
    public /* synthetic */ void lambda$updateConnectionInfo$2$WifiEntry() {
        DisconnectCallback disconnectCallback = this.mDisconnectCallback;
        if (disconnectCallback != null) {
            disconnectCallback.onDisconnectResult(0);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateLinkProperties(LinkProperties linkProperties) {
        if (linkProperties == null || getConnectedState() != 2) {
            this.mConnectedInfo = null;
            notifyOnUpdated();
            return;
        }
        if (this.mConnectedInfo == null) {
            this.mConnectedInfo = new ConnectedInfo();
        }
        ArrayList arrayList = new ArrayList();
        for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
            if (linkAddress.getAddress() instanceof Inet4Address) {
                this.mConnectedInfo.ipAddress = linkAddress.getAddress().getHostAddress();
                try {
                    InetAddress byAddress = InetAddress.getByAddress(new byte[]{-1, -1, -1, -1});
                    this.mConnectedInfo.subnetMask = NetworkUtils.getNetworkPart(byAddress, linkAddress.getPrefixLength()).getHostAddress();
                } catch (UnknownHostException unused) {
                }
            } else if (linkAddress.getAddress() instanceof Inet6Address) {
                arrayList.add(linkAddress.getAddress().getHostAddress());
            }
        }
        this.mConnectedInfo.ipv6Addresses = arrayList;
        Iterator<RouteInfo> it = linkProperties.getRoutes().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            RouteInfo next = it.next();
            if (next.isIPv4Default() && next.hasGateway()) {
                this.mConnectedInfo.gateway = next.getGateway().getHostAddress();
                break;
            }
        }
        this.mConnectedInfo.dnsServers = (List) linkProperties.getDnsServers().stream().map($$Lambda$XZAGhHrbkIDyusER4MAM6luKcT0.INSTANCE).collect(Collectors.toList());
        notifyOnUpdated();
    }

    /* access modifiers changed from: package-private */
    public void updateNetworkCapabilities(NetworkCapabilities networkCapabilities) {
        this.mNetworkCapabilities = networkCapabilities;
    }

    /* access modifiers changed from: package-private */
    public String getWifiInfoDescription() {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (getConnectedState() == 2 && this.mWifiInfo != null) {
            stringJoiner.add("f = " + this.mWifiInfo.getFrequency());
            String bssid = this.mWifiInfo.getBSSID();
            if (bssid != null) {
                stringJoiner.add(bssid);
            }
            stringJoiner.add("standard = " + this.mWifiInfo.getWifiStandard());
            stringJoiner.add("rssi = " + this.mWifiInfo.getRssi());
            stringJoiner.add("score = " + this.mWifiInfo.getScore());
            stringJoiner.add(String.format(" tx=%.1f,", Double.valueOf(this.mWifiInfo.getSuccessfulTxPacketsPerSecond())));
            stringJoiner.add(String.format("%.1f,", Double.valueOf(this.mWifiInfo.getRetriedTxPacketsPerSecond())));
            stringJoiner.add(String.format("%.1f ", Double.valueOf(this.mWifiInfo.getLostTxPacketsPerSecond())));
            stringJoiner.add(String.format("rx=%.1f", Double.valueOf(this.mWifiInfo.getSuccessfulRxPacketsPerSecond())));
        }
        return stringJoiner.toString();
    }

    protected class ConnectActionListener implements WifiManager.ActionListener {
        protected ConnectActionListener() {
        }

        public void onSuccess() {
            WifiEntry wifiEntry = WifiEntry.this;
            wifiEntry.mCalledConnect = true;
            wifiEntry.mCallbackHandler.postDelayed(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$WifiEntry$ConnectActionListener$5ELC6BF3FPbf8_YJTi4LLio_uKE */

                public final void run() {
                    WifiEntry.ConnectActionListener.this.lambda$onSuccess$0$WifiEntry$ConnectActionListener();
                }
            }, 10000);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onSuccess$0 */
        public /* synthetic */ void lambda$onSuccess$0$WifiEntry$ConnectActionListener() {
            WifiEntry wifiEntry = WifiEntry.this;
            if (wifiEntry.mConnectCallback != null && wifiEntry.mCalledConnect && wifiEntry.getConnectedState() == 0) {
                WifiEntry.this.mConnectCallback.onConnectResult(2);
                WifiEntry.this.mCalledConnect = false;
            }
        }

        public void onFailure(int i) {
            WifiEntry.this.mCallbackHandler.post(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$WifiEntry$ConnectActionListener$rNyjcSXqEa1w6d60US0s2fEnAoo */

                public final void run() {
                    WifiEntry.ConnectActionListener.this.lambda$onFailure$1$WifiEntry$ConnectActionListener();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onFailure$1 */
        public /* synthetic */ void lambda$onFailure$1$WifiEntry$ConnectActionListener() {
            ConnectCallback connectCallback = WifiEntry.this.mConnectCallback;
            if (connectCallback != null) {
                connectCallback.onConnectResult(2);
            }
        }
    }

    protected class ForgetActionListener implements WifiManager.ActionListener {
        protected ForgetActionListener() {
        }

        public void onSuccess() {
            WifiEntry.this.mCallbackHandler.post(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$WifiEntry$ForgetActionListener$SZ0IZPl_pQYHOFIlfk05jMAoIag */

                public final void run() {
                    WifiEntry.ForgetActionListener.this.lambda$onSuccess$0$WifiEntry$ForgetActionListener();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onSuccess$0 */
        public /* synthetic */ void lambda$onSuccess$0$WifiEntry$ForgetActionListener() {
            ForgetCallback forgetCallback = WifiEntry.this.mForgetCallback;
            if (forgetCallback != null) {
                forgetCallback.onForgetResult(0);
            }
        }

        public void onFailure(int i) {
            WifiEntry.this.mCallbackHandler.post(new Runnable() {
                /* class com.android.wifitrackerlib.$$Lambda$WifiEntry$ForgetActionListener$B2flgYnZZJGOI7c8yNLpXNeAXg */

                public final void run() {
                    WifiEntry.ForgetActionListener.this.lambda$onFailure$1$WifiEntry$ForgetActionListener();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onFailure$1 */
        public /* synthetic */ void lambda$onFailure$1$WifiEntry$ForgetActionListener() {
            ForgetCallback forgetCallback = WifiEntry.this.mForgetCallback;
            if (forgetCallback != null) {
                forgetCallback.onForgetResult(1);
            }
        }
    }

    public int compareTo(WifiEntry wifiEntry) {
        if (getLevel() != -1 && wifiEntry.getLevel() == -1) {
            return -1;
        }
        if (getLevel() == -1 && wifiEntry.getLevel() != -1) {
            return 1;
        }
        if (isSubscription() && !wifiEntry.isSubscription()) {
            return -1;
        }
        if (!isSubscription() && wifiEntry.isSubscription()) {
            return 1;
        }
        if (isSaved() && !wifiEntry.isSaved()) {
            return -1;
        }
        if (!isSaved() && wifiEntry.isSaved()) {
            return 1;
        }
        if (isSuggestion() && !wifiEntry.isSuggestion()) {
            return -1;
        }
        if (!isSuggestion() && wifiEntry.isSuggestion()) {
            return 1;
        }
        if (getLevel() > wifiEntry.getLevel()) {
            return -1;
        }
        if (getLevel() < wifiEntry.getLevel()) {
            return 1;
        }
        return getTitle().compareTo(wifiEntry.getTitle());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WifiEntry)) {
            return false;
        }
        return getKey().equals(((WifiEntry) obj).getKey());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getKey());
        sb.append(",title:");
        sb.append(getTitle());
        sb.append(",summary:");
        sb.append(getSummary());
        sb.append(",isSaved:");
        sb.append(isSaved());
        sb.append(",isSubscription:");
        sb.append(isSubscription());
        sb.append(",isSuggestion:");
        sb.append(isSuggestion());
        sb.append(",level:");
        sb.append(getLevel());
        sb.append(",security:");
        sb.append(getSecurity());
        sb.append(",standard:");
        sb.append(getWifiStandard());
        sb.append(",he8ssAp:");
        sb.append(isHe8ssCapableAp());
        sb.append(",vhtMax8ssCapa:");
        sb.append(isVhtMax8SpatialStreamsSupported());
        sb.append(",connected:");
        sb.append(getConnectedState() == 2 ? "true" : "false");
        sb.append(",connectedInfo:");
        sb.append(getConnectedInfo());
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public void updateTransitionModeCapa(ScanResult scanResult) {
        this.mIsPskSaeTransitionMode = scanResult.capabilities.contains("PSK") && scanResult.capabilities.contains("SAE");
        this.mIsOweTransitionMode = scanResult.capabilities.contains("OWE_TRANSITION");
    }

    public boolean isPskSaeTransitionMode() {
        return this.mIsPskSaeTransitionMode;
    }

    public boolean isOweTransitionMode() {
        return this.mIsOweTransitionMode;
    }

    private void updatetDeviceWifiGenerationInfo() {
        if (this.mWifiManager.isWifiStandardSupported(6)) {
            this.mDeviceWifiStandard = 6;
        } else if (this.mWifiManager.isWifiStandardSupported(5)) {
            this.mDeviceWifiStandard = 5;
        } else if (this.mWifiManager.isWifiStandardSupported(4)) {
            this.mDeviceWifiStandard = 4;
        } else {
            this.mDeviceWifiStandard = 1;
        }
        this.mVhtMax8SpatialStreamsSupport = this.mWifiManager.isVht8ssCapableDevice();
    }

    public int getWifiStandard() {
        if (getConnectedInfo() == null || this.mWifiInfo == null || getConnectedState() != 2) {
            return this.mWifiStandard;
        }
        return this.mWifiInfo.getWifiStandard();
    }

    public boolean isHe8ssCapableAp() {
        if (getConnectedInfo() == null || this.mWifiInfo == null || getConnectedState() != 2) {
            return this.mHe8ssCapableAp;
        }
        return this.mWifiInfo.isHe8ssCapableAp();
    }

    public boolean isVhtMax8SpatialStreamsSupported() {
        if (getConnectedInfo() == null || this.mWifiInfo == null || getConnectedState() != 2) {
            return this.mVhtMax8SpatialStreamsSupport;
        }
        return this.mWifiInfo.isVhtMax8SpatialStreamsSupported();
    }

    /* access modifiers changed from: protected */
    public void updateWifiGenerationInfo(List<ScanResult> list) {
        int i = this.mDeviceWifiStandard;
        this.mHe8ssCapableAp = true;
        for (ScanResult scanResult : list) {
            int wifiStandard = scanResult.getWifiStandard();
            if (!scanResult.capabilities.contains("WFA-HE-READY") && this.mHe8ssCapableAp) {
                this.mHe8ssCapableAp = false;
            }
            if (wifiStandard < i) {
                i = wifiStandard;
            }
        }
        this.mWifiStandard = i;
    }
}
