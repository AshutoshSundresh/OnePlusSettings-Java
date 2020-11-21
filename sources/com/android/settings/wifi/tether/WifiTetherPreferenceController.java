package com.android.settings.wifi.tether;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiClient;
import android.net.wifi.WifiManager;
import android.text.BidiFormatter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.wifi.tether.WifiTetherSoftApManager;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.List;

public class WifiTetherPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnStart, OnStop {
    private final ConnectivityManager mConnectivityManager;
    Preference mPreference;
    private int mSoftApState;
    private final WifiManager mWifiManager;
    private final String[] mWifiRegexs;
    WifiTetherSoftApManager mWifiTetherSoftApManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wifi_tether";
    }

    public WifiTetherPreferenceController(Context context, Lifecycle lifecycle) {
        this(context, lifecycle, true);
    }

    WifiTetherPreferenceController(Context context, Lifecycle lifecycle, boolean z) {
        super(context);
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
        this.mWifiRegexs = this.mConnectivityManager.getTetherableWifiRegexs();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        if (z) {
            initWifiTetherSoftApManager();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        String[] strArr = this.mWifiRegexs;
        return (strArr == null || strArr.length == 0 || Utils.isMonkeyRunning()) ? false : true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference("wifi_tether");
        this.mPreference = findPreference;
        if (findPreference == null) {
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        WifiTetherSoftApManager wifiTetherSoftApManager;
        if (this.mPreference != null && (wifiTetherSoftApManager = this.mWifiTetherSoftApManager) != null) {
            wifiTetherSoftApManager.registerSoftApCallback();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        WifiTetherSoftApManager wifiTetherSoftApManager;
        if (this.mPreference != null && (wifiTetherSoftApManager = this.mWifiTetherSoftApManager) != null) {
            wifiTetherSoftApManager.unRegisterSoftApCallback();
        }
    }

    /* access modifiers changed from: package-private */
    public void initWifiTetherSoftApManager() {
        this.mWifiTetherSoftApManager = new WifiTetherSoftApManager(this.mWifiManager, new WifiTetherSoftApManager.WifiTetherSoftApCallback() {
            /* class com.android.settings.wifi.tether.WifiTetherPreferenceController.AnonymousClass1 */

            @Override // com.android.settings.wifi.tether.WifiTetherSoftApManager.WifiTetherSoftApCallback
            public void onStateChanged(int i, int i2) {
                WifiTetherPreferenceController.this.mSoftApState = i;
                WifiTetherPreferenceController.this.handleWifiApStateChanged(i, i2);
            }

            @Override // com.android.settings.wifi.tether.WifiTetherSoftApManager.WifiTetherSoftApCallback
            public void onConnectedClientsChanged(List<WifiClient> list) {
                WifiTetherPreferenceController wifiTetherPreferenceController = WifiTetherPreferenceController.this;
                if (wifiTetherPreferenceController.mPreference != null && wifiTetherPreferenceController.mSoftApState == 13) {
                    String str = WifiTetherPreferenceController.this.mWifiManager.isExtendingWifi() ? "Extending Wifi-Coverage: " : "";
                    Preference preference = WifiTetherPreferenceController.this.mPreference;
                    preference.setSummary(str + ((AbstractPreferenceController) WifiTetherPreferenceController.this).mContext.getResources().getQuantityString(C0015R$plurals.wifi_tether_connected_summary, list.size(), Integer.valueOf(list.size())));
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void handleWifiApStateChanged(int i, int i2) {
        switch (i) {
            case 10:
                this.mPreference.setSummary(C0017R$string.wifi_tether_stopping);
                return;
            case 11:
                this.mPreference.setSummary(C0017R$string.wifi_hotspot_off_subtext);
                return;
            case 12:
                this.mPreference.setSummary(C0017R$string.wifi_tether_starting);
                return;
            case 13:
                updateConfigSummary(this.mWifiManager.getSoftApConfiguration());
                return;
            default:
                if (i2 == 1) {
                    this.mPreference.setSummary(C0017R$string.wifi_sap_no_channel_error);
                    return;
                } else {
                    this.mPreference.setSummary(C0017R$string.wifi_error);
                    return;
                }
        }
    }

    private void updateConfigSummary(SoftApConfiguration softApConfiguration) {
        if (softApConfiguration != null) {
            this.mPreference.setSummary(this.mContext.getString(C0017R$string.wifi_tether_enabled_subtext, BidiFormatter.getInstance().unicodeWrap(softApConfiguration.getSsid())));
        }
    }
}
