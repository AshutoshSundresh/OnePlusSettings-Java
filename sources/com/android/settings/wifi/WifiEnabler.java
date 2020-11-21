package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Pair;
import android.widget.Toast;
import com.android.settings.C0017R$string;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settings.wifi.tether.utils.TetherUtils;
import com.android.settingslib.WirelessUtils;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.oneplus.settings.utils.OPUtils;
import java.util.concurrent.atomic.AtomicBoolean;

public class WifiEnabler implements SwitchWidgetController.OnSwitchChangeListener, TetherUtils.OnDialogConfirmCallback {
    private AtomicBoolean mConnected;
    private Context mContext;
    private final IntentFilter mIntentFilter;
    private boolean mListeningToOnSwitchChange;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final BroadcastReceiver mReceiver;
    private boolean mStateMachineEvent;
    private final SwitchWidgetController mSwitchWidget;
    private final WifiManager mWifiManager;

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleStateChanged(NetworkInfo.DetailedState detailedState) {
    }

    public WifiEnabler(Context context, SwitchWidgetController switchWidgetController, MetricsFeatureProvider metricsFeatureProvider) {
        this(context, switchWidgetController, metricsFeatureProvider, (ConnectivityManager) context.getSystemService("connectivity"));
    }

    WifiEnabler(Context context, SwitchWidgetController switchWidgetController, MetricsFeatureProvider metricsFeatureProvider, ConnectivityManager connectivityManager) {
        this.mListeningToOnSwitchChange = false;
        this.mConnected = new AtomicBoolean(false);
        this.mReceiver = new BroadcastReceiver() {
            /* class com.android.settings.wifi.WifiEnabler.AnonymousClass1 */

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
                    WifiEnabler wifiEnabler = WifiEnabler.this;
                    wifiEnabler.handleWifiStateChanged(wifiEnabler.mWifiManager.getWifiState());
                } else if ("android.net.wifi.supplicant.STATE_CHANGE".equals(action)) {
                    if (!WifiEnabler.this.mConnected.get()) {
                        WifiEnabler.this.handleStateChanged(WifiInfo.getDetailedStateOf((SupplicantState) intent.getParcelableExtra("newState")));
                    }
                } else if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                    NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                    WifiEnabler.this.mConnected.set(networkInfo.isConnected());
                    WifiEnabler.this.handleStateChanged(networkInfo.getDetailedState());
                }
            }
        };
        this.mContext = context;
        this.mSwitchWidget = switchWidgetController;
        switchWidgetController.setListener(this);
        this.mMetricsFeatureProvider = metricsFeatureProvider;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        IntentFilter intentFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        this.mIntentFilter.addAction("android.net.wifi.STATE_CHANGE");
        setupSwitchController();
    }

    public void setupSwitchController() {
        handleWifiStateChanged(this.mWifiManager.getWifiState());
        if (!this.mListeningToOnSwitchChange) {
            this.mSwitchWidget.startListening();
            this.mListeningToOnSwitchChange = true;
        }
        this.mSwitchWidget.setupView();
    }

    public void teardownSwitchController() {
        if (this.mListeningToOnSwitchChange) {
            this.mSwitchWidget.stopListening();
            this.mListeningToOnSwitchChange = false;
        }
        this.mSwitchWidget.teardownView();
    }

    public void resume(Context context) {
        this.mContext = context;
        context.registerReceiver(this.mReceiver, this.mIntentFilter);
        if (!this.mListeningToOnSwitchChange) {
            this.mSwitchWidget.startListening();
            this.mListeningToOnSwitchChange = true;
        }
    }

    public void pause() {
        this.mContext.unregisterReceiver(this.mReceiver);
        if (this.mListeningToOnSwitchChange) {
            this.mSwitchWidget.stopListening();
            this.mListeningToOnSwitchChange = false;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleWifiStateChanged(int i) {
        this.mSwitchWidget.setDisabledByAdmin(null);
        if (i == 0) {
            return;
        }
        if (i == 1) {
            setSwitchBarChecked(false);
            this.mSwitchWidget.setEnabled(true);
        } else if (i == 2) {
        } else {
            if (i != 3) {
                setSwitchBarChecked(false);
                this.mSwitchWidget.setEnabled(true);
                return;
            }
            setSwitchBarChecked(true);
            this.mSwitchWidget.setEnabled(true);
        }
    }

    private void setSwitchBarChecked(boolean z) {
        this.mStateMachineEvent = true;
        this.mSwitchWidget.setChecked(z);
        this.mStateMachineEvent = false;
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        if (this.mStateMachineEvent) {
            return true;
        }
        if (!z || WirelessUtils.isRadioAllowed(this.mContext, "wifi")) {
            if (!z) {
                this.mMetricsFeatureProvider.action(this.mContext, 138, this.mConnected.get());
            } else if (!OPUtils.isSupportUstMode() || !TetherUtils.isTetheringOpen(this.mContext)) {
                this.mMetricsFeatureProvider.action(this.mContext, 139, new Pair[0]);
            } else {
                TetherUtils.openUstWifi(this.mContext, this);
            }
            if (!this.mWifiManager.setWifiEnabled(z)) {
                this.mSwitchWidget.setEnabled(true);
                Toast.makeText(this.mContext, C0017R$string.wifi_error, 0).show();
            }
            return true;
        }
        Toast.makeText(this.mContext, C0017R$string.wifi_in_airplane_mode, 0).show();
        this.mSwitchWidget.setChecked(false);
        return false;
    }

    @Override // com.android.settings.wifi.tether.utils.TetherUtils.OnDialogConfirmCallback
    public void onConfirm() {
        MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeatureProvider;
        if (metricsFeatureProvider != null) {
            metricsFeatureProvider.action(this.mContext, 139, new Pair[0]);
        }
    }
}
