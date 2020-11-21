package com.android.settings.wifi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;

public final class WifiNoInternetDialog extends AlertActivity implements DialogInterface.OnClickListener {
    private String mAction;
    private CheckBox mAlwaysAllow;
    private boolean mButtonClicked;
    private ConnectivityManager mCM;
    private Network mNetwork;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private String mNetworkName;

    private boolean isKnownAction(Intent intent) {
        return "android.net.conn.PROMPT_UNVALIDATED".equals(intent.getAction()) || "android.net.conn.PROMPT_LOST_VALIDATION".equals(intent.getAction()) || "android.net.conn.PROMPT_PARTIAL_CONNECTIVITY".equals(intent.getAction());
    }

    public void onCreate(Bundle bundle) {
        WifiNoInternetDialog.super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent == null || !isKnownAction(intent) || !"netId".equals(intent.getScheme())) {
            Log.e("WifiNoInternetDialog", "Unexpected intent " + intent + ", exiting");
            finish();
            return;
        }
        this.mAction = intent.getAction();
        try {
            this.mNetwork = new Network(Integer.parseInt(intent.getData().getSchemeSpecificPart()));
        } catch (NullPointerException | NumberFormatException unused) {
            this.mNetwork = null;
        }
        if (this.mNetwork == null) {
            Log.e("WifiNoInternetDialog", "Can't determine network from '" + intent.getData() + "' , exiting");
            finish();
            return;
        }
        NetworkRequest build = new NetworkRequest.Builder().clearCapabilities().build();
        this.mNetworkCallback = new ConnectivityManager.NetworkCallback() {
            /* class com.android.settings.wifi.WifiNoInternetDialog.AnonymousClass1 */

            public void onLost(Network network) {
                if (WifiNoInternetDialog.this.mNetwork.equals(network)) {
                    Log.d("WifiNoInternetDialog", "Network " + WifiNoInternetDialog.this.mNetwork + " disconnected");
                    WifiNoInternetDialog.this.finish();
                }
            }

            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                if (WifiNoInternetDialog.this.mNetwork.equals(network) && networkCapabilities.hasCapability(16)) {
                    Log.d("WifiNoInternetDialog", "Network " + WifiNoInternetDialog.this.mNetwork + " validated");
                    WifiNoInternetDialog.this.finish();
                }
            }
        };
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        this.mCM = connectivityManager;
        connectivityManager.registerNetworkCallback(build, this.mNetworkCallback);
        NetworkInfo networkInfo = this.mCM.getNetworkInfo(this.mNetwork);
        NetworkCapabilities networkCapabilities = this.mCM.getNetworkCapabilities(this.mNetwork);
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting() || networkCapabilities == null) {
            Log.d("WifiNoInternetDialog", "Network " + this.mNetwork + " is not connected: " + networkInfo);
            finish();
            return;
        }
        String ssid = networkCapabilities.getSsid();
        this.mNetworkName = ssid;
        if (ssid != null) {
            this.mNetworkName = WifiInfo.sanitizeSsid(ssid);
        }
        createDialog();
    }

    private void createDialog() {
        ((AlertActivity) this).mAlert.setIcon(C0008R$drawable.ic_settings_wireless);
        AlertController.AlertParams alertParams = ((AlertActivity) this).mAlertParams;
        if ("android.net.conn.PROMPT_UNVALIDATED".equals(this.mAction)) {
            alertParams.mTitle = this.mNetworkName;
            alertParams.mMessage = getString(C0017R$string.no_internet_access_text);
            alertParams.mPositiveButtonText = getString(C0017R$string.yes);
            alertParams.mNegativeButtonText = getString(C0017R$string.no);
        } else if ("android.net.conn.PROMPT_PARTIAL_CONNECTIVITY".equals(this.mAction)) {
            alertParams.mTitle = this.mNetworkName;
            alertParams.mMessage = getString(C0017R$string.partial_connectivity_text);
            alertParams.mPositiveButtonText = getString(C0017R$string.yes);
            alertParams.mNegativeButtonText = getString(C0017R$string.no);
        } else {
            alertParams.mTitle = getString(C0017R$string.lost_internet_access_title);
            alertParams.mMessage = getString(C0017R$string.lost_internet_access_text);
            alertParams.mPositiveButtonText = getString(C0017R$string.lost_internet_access_switch);
            alertParams.mNegativeButtonText = getString(C0017R$string.lost_internet_access_cancel);
        }
        alertParams.mPositiveButtonListener = this;
        alertParams.mNegativeButtonListener = this;
        View inflate = LayoutInflater.from(alertParams.mContext).inflate(17367092, (ViewGroup) null);
        alertParams.mView = inflate;
        this.mAlwaysAllow = (CheckBox) inflate.findViewById(16908751);
        if ("android.net.conn.PROMPT_UNVALIDATED".equals(this.mAction) || "android.net.conn.PROMPT_PARTIAL_CONNECTIVITY".equals(this.mAction)) {
            this.mAlwaysAllow.setText(getString(C0017R$string.no_internet_access_remember));
        } else {
            this.mAlwaysAllow.setText(getString(C0017R$string.lost_internet_access_persist));
        }
        setupAlert();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        ConnectivityManager.NetworkCallback networkCallback = this.mNetworkCallback;
        if (networkCallback != null) {
            this.mCM.unregisterNetworkCallback(networkCallback);
            this.mNetworkCallback = null;
        }
        if (isFinishing() && !this.mButtonClicked) {
            if ("android.net.conn.PROMPT_PARTIAL_CONNECTIVITY".equals(this.mAction)) {
                this.mCM.setAcceptPartialConnectivity(this.mNetwork, false, false);
            } else if ("android.net.conn.PROMPT_UNVALIDATED".equals(this.mAction)) {
                this.mCM.setAcceptUnvalidated(this.mNetwork, false, false);
            }
        }
        WifiNoInternetDialog.super.onDestroy();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        String str;
        if (i == -2 || i == -1) {
            boolean isChecked = this.mAlwaysAllow.isChecked();
            boolean z = true;
            this.mButtonClicked = true;
            String str2 = "Connect";
            if ("android.net.conn.PROMPT_UNVALIDATED".equals(this.mAction)) {
                if (i != -1) {
                    z = false;
                }
                if (!z) {
                    str2 = "Ignore";
                }
                this.mCM.setAcceptUnvalidated(this.mNetwork, z, isChecked);
                str = "NO_INTERNET";
            } else if ("android.net.conn.PROMPT_PARTIAL_CONNECTIVITY".equals(this.mAction)) {
                if (i != -1) {
                    z = false;
                }
                if (!z) {
                    str2 = "Ignore";
                }
                this.mCM.setAcceptPartialConnectivity(this.mNetwork, z, isChecked);
                str = "PARTIAL_CONNECTIVITY";
            } else {
                if (i != -1) {
                    z = false;
                }
                str2 = z ? "Switch away" : "Get stuck";
                if (isChecked) {
                    Settings.Global.putString(((AlertActivity) this).mAlertParams.mContext.getContentResolver(), "network_avoid_bad_wifi", z ? "1" : "0");
                } else if (z) {
                    this.mCM.setAvoidUnvalidated(this.mNetwork);
                }
                str = "LOST_INTERNET";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(": ");
            sb.append(str2);
            sb.append(" network=");
            sb.append(this.mNetwork);
            sb.append(isChecked ? " and remember" : "");
            Log.d("WifiNoInternetDialog", sb.toString());
        }
    }
}
