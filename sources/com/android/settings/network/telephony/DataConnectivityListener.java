package com.android.settings.network.telephony;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

public class DataConnectivityListener extends ConnectivityManager.NetworkCallback {
    private Client mClient;
    private ConnectivityManager mConnectivityManager;
    private Context mContext;
    private final NetworkRequest mNetworkRequest = new NetworkRequest.Builder().addCapability(12).build();

    public interface Client {
        void onDataConnectivityChange();
    }

    public DataConnectivityListener(Context context, Client client) {
        this.mContext = context;
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
        this.mClient = client;
    }

    public void start() {
        this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this, this.mContext.getMainThreadHandler());
    }

    public void stop() {
        this.mConnectivityManager.unregisterNetworkCallback(this);
    }

    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        Network activeNetwork = this.mConnectivityManager.getActiveNetwork();
        if (activeNetwork != null && activeNetwork.equals(network)) {
            this.mClient.onDataConnectivityChange();
        }
    }

    public void onLosing(Network network, int i) {
        this.mClient.onDataConnectivityChange();
    }

    public void onLost(Network network) {
        this.mClient.onDataConnectivityChange();
    }
}
