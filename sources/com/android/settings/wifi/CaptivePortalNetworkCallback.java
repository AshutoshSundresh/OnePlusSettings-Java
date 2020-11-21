package com.android.settings.wifi;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import com.android.internal.util.Preconditions;

/* access modifiers changed from: package-private */
public class CaptivePortalNetworkCallback extends ConnectivityManager.NetworkCallback {
    private final ConnectedAccessPointPreference mConnectedApPreference;
    private boolean mIsCaptivePortal;
    private final Network mNetwork;

    public abstract void onCaptivePortalCapabilityChanged();

    CaptivePortalNetworkCallback(Network network, ConnectedAccessPointPreference connectedAccessPointPreference) {
        this.mNetwork = (Network) Preconditions.checkNotNull(network);
        this.mConnectedApPreference = (ConnectedAccessPointPreference) Preconditions.checkNotNull(connectedAccessPointPreference);
    }

    public final void onLost(Network network) {
        if (this.mNetwork.equals(network)) {
            setIsCaptivePortal(false);
        }
    }

    public final void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        if (this.mNetwork.equals(network)) {
            boolean canSignIntoNetwork = WifiUtils.canSignIntoNetwork(networkCapabilities);
            setIsCaptivePortal(canSignIntoNetwork);
            this.mConnectedApPreference.setCaptivePortal(canSignIntoNetwork);
        }
    }

    private void setIsCaptivePortal(boolean z) {
        if (z != this.mIsCaptivePortal) {
            this.mIsCaptivePortal = z;
            onCaptivePortalCapabilityChanged();
        }
    }

    public final boolean isSameNetworkAndPreference(Network network, ConnectedAccessPointPreference connectedAccessPointPreference) {
        return this.mNetwork.equals(network) && this.mConnectedApPreference == connectedAccessPointPreference;
    }

    public final boolean isCaptivePortal() {
        return this.mIsCaptivePortal;
    }

    public final Network getNetwork() {
        return this.mNetwork;
    }
}
