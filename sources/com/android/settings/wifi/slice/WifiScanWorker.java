package com.android.settings.wifi.slice;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.Preconditions;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.WifiTracker;
import java.util.ArrayList;
import java.util.List;

public class WifiScanWorker extends SliceBackgroundWorker<AccessPoint> implements WifiTracker.WifiListener {
    private static String sClickedWifiSsid;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    WifiNetworkCallback mNetworkCallback;
    private final WifiTracker mWifiTracker = new WifiTracker(this.mContext, this, true, true);

    /* access modifiers changed from: protected */
    public int getApRowCount() {
        return 3;
    }

    /* access modifiers changed from: protected */
    public boolean isSessionValid() {
        return true;
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onConnectedChanged() {
    }

    public WifiScanWorker(Context context, Uri uri) {
        super(context, uri);
        this.mContext = context;
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.slices.SliceBackgroundWorker
    public void onSlicePinned() {
        this.mWifiTracker.onStart();
        onAccessPointsChanged();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.slices.SliceBackgroundWorker
    public void onSliceUnpinned() {
        this.mWifiTracker.onStop();
        unregisterNetworkCallback();
        clearClickedWifiOnSliceUnpinned();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        this.mWifiTracker.onDestroy();
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onWifiStateChanged(int i) {
        notifySliceChange();
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onAccessPointsChanged() {
        if (!this.mWifiTracker.getManager().isWifiEnabled()) {
            updateResults(null);
            return;
        }
        List<AccessPoint> accessPoints = this.mWifiTracker.getAccessPoints();
        ArrayList arrayList = new ArrayList();
        int apRowCount = getApRowCount();
        for (AccessPoint accessPoint : accessPoints) {
            if (accessPoint.isReachable()) {
                arrayList.add(clone(accessPoint));
                if (arrayList.size() >= apRowCount) {
                    break;
                }
            }
        }
        updateResults(arrayList);
    }

    private AccessPoint clone(AccessPoint accessPoint) {
        Bundle bundle = new Bundle();
        accessPoint.saveWifiState(bundle);
        return new AccessPoint(this.mContext, bundle);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.slices.SliceBackgroundWorker
    public boolean areListsTheSame(List<AccessPoint> list, List<AccessPoint> list2) {
        if (!list.equals(list2)) {
            return false;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (list.get(i).getDetailedState() != list2.get(i).getDetailedState()) {
                return false;
            }
        }
        return true;
    }

    static void saveClickedWifi(AccessPoint accessPoint) {
        sClickedWifiSsid = accessPoint.getSsidStr();
    }

    static void clearClickedWifi() {
        sClickedWifiSsid = null;
    }

    static boolean isWifiClicked(WifiInfo wifiInfo) {
        String sanitizeSsid = WifiInfo.sanitizeSsid(wifiInfo.getSSID());
        return !TextUtils.isEmpty(sanitizeSsid) && TextUtils.equals(sanitizeSsid, sClickedWifiSsid);
    }

    /* access modifiers changed from: protected */
    public void clearClickedWifiOnSliceUnpinned() {
        clearClickedWifi();
    }

    public void registerNetworkCallback(Network network) {
        if (network != null) {
            WifiNetworkCallback wifiNetworkCallback = this.mNetworkCallback;
            if (wifiNetworkCallback == null || !wifiNetworkCallback.isSameNetwork(network)) {
                unregisterNetworkCallback();
                this.mNetworkCallback = new WifiNetworkCallback(network);
                this.mConnectivityManager.registerNetworkCallback(new NetworkRequest.Builder().clearCapabilities().addTransportType(1).build(), this.mNetworkCallback, new Handler(Looper.getMainLooper()));
            }
        }
    }

    public void unregisterNetworkCallback() {
        WifiNetworkCallback wifiNetworkCallback = this.mNetworkCallback;
        if (wifiNetworkCallback != null) {
            try {
                this.mConnectivityManager.unregisterNetworkCallback(wifiNetworkCallback);
            } catch (RuntimeException e) {
                Log.e("WifiScanWorker", "Unregistering CaptivePortalNetworkCallback failed.", e);
            }
            this.mNetworkCallback = null;
        }
    }

    /* access modifiers changed from: package-private */
    public class WifiNetworkCallback extends ConnectivityManager.NetworkCallback {
        private boolean mHasPartialConnectivity;
        private boolean mIsCaptivePortal;
        private boolean mIsValidated;
        private final Network mNetwork;

        WifiNetworkCallback(Network network) {
            this.mNetwork = (Network) Preconditions.checkNotNull(network);
        }

        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            if (isSameNetwork(network)) {
                boolean z = this.mIsCaptivePortal;
                boolean z2 = this.mHasPartialConnectivity;
                boolean z3 = this.mIsValidated;
                this.mIsCaptivePortal = networkCapabilities.hasCapability(17);
                this.mHasPartialConnectivity = networkCapabilities.hasCapability(24);
                boolean hasCapability = networkCapabilities.hasCapability(16);
                this.mIsValidated = hasCapability;
                if (z != this.mIsCaptivePortal || z2 != this.mHasPartialConnectivity || z3 != hasCapability) {
                    WifiScanWorker.this.notifySliceChange();
                    if (!z && this.mIsCaptivePortal && WifiScanWorker.isWifiClicked(WifiScanWorker.this.mWifiTracker.getManager().getConnectionInfo()) && WifiScanWorker.this.isSessionValid()) {
                        WifiScanWorker.this.mContext.sendBroadcastAsUser(new Intent(WifiScanWorker.this.mContext, ConnectToWifiHandler.class).putExtra("android.net.extra.NETWORK", network).addFlags(268435456), UserHandle.CURRENT);
                    }
                }
            }
        }

        public boolean isSameNetwork(Network network) {
            return this.mNetwork.equals(network);
        }
    }
}
