package com.android.settings.wifi.dpp;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.C0010R$id;
import com.android.settings.wifi.dpp.WifiDppAddDeviceFragment;
import com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment;
import com.android.settings.wifi.dpp.WifiNetworkConfig;
import com.android.settings.wifi.dpp.WifiNetworkListFragment;

public class WifiDppConfiguratorActivity extends WifiDppBaseActivity implements WifiNetworkConfig.Retriever, WifiDppQrCodeScannerFragment.OnScanWifiDppSuccessListener, WifiDppAddDeviceFragment.OnClickChooseDifferentNetworkListener, WifiNetworkListFragment.OnChooseNetworkListener {
    private boolean mIsTest;
    private WifiQrCode mWifiDppQrCode;
    private WifiNetworkConfig mWifiNetworkConfig;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1595;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.core.InstrumentedActivity, com.android.settings.wifi.dpp.WifiDppBaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.mWifiDppQrCode = WifiQrCode.getValidWifiDppQrCodeOrNull(bundle.getString("key_qr_code"));
            this.mWifiNetworkConfig = WifiNetworkConfig.getValidConfigOrNull(bundle.getString("key_wifi_security"), bundle.getString("key_wifi_ssid"), bundle.getString("key_wifi_preshared_key"), bundle.getBoolean("key_wifi_hidden_ssid"), bundle.getInt("key_wifi_network_id"), bundle.getBoolean("key_is_hotspot"));
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00bf  */
    /* JADX WARNING: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
    @Override // com.android.settings.wifi.dpp.WifiDppBaseActivity
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleIntent(android.content.Intent r9) {
        /*
        // Method dump skipped, instructions count: 195
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.dpp.WifiDppConfiguratorActivity.handleIntent(android.content.Intent):void");
    }

    private void showQrCodeScannerFragment() {
        WifiDppQrCodeScannerFragment wifiDppQrCodeScannerFragment = (WifiDppQrCodeScannerFragment) this.mFragmentManager.findFragmentByTag("qr_code_scanner_fragment");
        if (wifiDppQrCodeScannerFragment == null) {
            WifiDppQrCodeScannerFragment wifiDppQrCodeScannerFragment2 = new WifiDppQrCodeScannerFragment();
            FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
            beginTransaction.replace(C0010R$id.fragment_container, wifiDppQrCodeScannerFragment2, "qr_code_scanner_fragment");
            beginTransaction.commit();
        } else if (!wifiDppQrCodeScannerFragment.isVisible()) {
            this.mFragmentManager.popBackStackImmediate();
        }
    }

    private void showQrCodeGeneratorFragment() {
        WifiDppQrCodeGeneratorFragment wifiDppQrCodeGeneratorFragment = (WifiDppQrCodeGeneratorFragment) this.mFragmentManager.findFragmentByTag("qr_code_generator_fragment");
        if (wifiDppQrCodeGeneratorFragment == null) {
            WifiDppQrCodeGeneratorFragment wifiDppQrCodeGeneratorFragment2 = new WifiDppQrCodeGeneratorFragment();
            FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
            beginTransaction.replace(C0010R$id.fragment_container, wifiDppQrCodeGeneratorFragment2, "qr_code_generator_fragment");
            beginTransaction.commit();
        } else if (!wifiDppQrCodeGeneratorFragment.isVisible()) {
            this.mFragmentManager.popBackStackImmediate();
        }
    }

    private void showChooseSavedWifiNetworkFragment(boolean z) {
        WifiDppChooseSavedWifiNetworkFragment wifiDppChooseSavedWifiNetworkFragment = (WifiDppChooseSavedWifiNetworkFragment) this.mFragmentManager.findFragmentByTag("choose_saved_wifi_network_fragment");
        if (wifiDppChooseSavedWifiNetworkFragment == null) {
            WifiDppChooseSavedWifiNetworkFragment wifiDppChooseSavedWifiNetworkFragment2 = new WifiDppChooseSavedWifiNetworkFragment();
            if (this.mIsTest) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("test", true);
                wifiDppChooseSavedWifiNetworkFragment2.setArguments(bundle);
            }
            FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
            beginTransaction.replace(C0010R$id.fragment_container, wifiDppChooseSavedWifiNetworkFragment2, "choose_saved_wifi_network_fragment");
            if (z) {
                beginTransaction.addToBackStack(null);
            }
            beginTransaction.commit();
        } else if (!wifiDppChooseSavedWifiNetworkFragment.isVisible()) {
            this.mFragmentManager.popBackStackImmediate();
        }
    }

    private void showAddDeviceFragment(boolean z) {
        WifiDppAddDeviceFragment wifiDppAddDeviceFragment = (WifiDppAddDeviceFragment) this.mFragmentManager.findFragmentByTag("add_device_fragment");
        if (wifiDppAddDeviceFragment == null) {
            WifiDppAddDeviceFragment wifiDppAddDeviceFragment2 = new WifiDppAddDeviceFragment();
            FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
            beginTransaction.replace(C0010R$id.fragment_container, wifiDppAddDeviceFragment2, "add_device_fragment");
            if (z) {
                beginTransaction.addToBackStack(null);
            }
            beginTransaction.commit();
        } else if (!wifiDppAddDeviceFragment.isVisible()) {
            this.mFragmentManager.popBackStackImmediate();
        }
    }

    @Override // com.android.settings.wifi.dpp.WifiNetworkConfig.Retriever
    public WifiNetworkConfig getWifiNetworkConfig() {
        return this.mWifiNetworkConfig;
    }

    /* access modifiers changed from: package-private */
    public WifiQrCode getWifiDppQrCode() {
        return this.mWifiDppQrCode;
    }

    /* access modifiers changed from: package-private */
    public boolean setWifiNetworkConfig(WifiNetworkConfig wifiNetworkConfig) {
        if (!WifiNetworkConfig.isValidConfig(wifiNetworkConfig)) {
            return false;
        }
        this.mWifiNetworkConfig = new WifiNetworkConfig(wifiNetworkConfig);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean setWifiDppQrCode(WifiQrCode wifiQrCode) {
        if (wifiQrCode == null || !"DPP".equals(wifiQrCode.getScheme())) {
            return false;
        }
        this.mWifiDppQrCode = new WifiQrCode(wifiQrCode.getQrCode());
        return true;
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment.OnScanWifiDppSuccessListener
    public void onScanWifiDppSuccess(WifiQrCode wifiQrCode) {
        this.mWifiDppQrCode = wifiQrCode;
        showAddDeviceFragment(true);
    }

    @Override // com.android.settings.wifi.dpp.WifiDppAddDeviceFragment.OnClickChooseDifferentNetworkListener
    public void onClickChooseDifferentNetwork() {
        showChooseSavedWifiNetworkFragment(true);
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onSaveInstanceState(Bundle bundle) {
        WifiQrCode wifiQrCode = this.mWifiDppQrCode;
        if (wifiQrCode != null) {
            bundle.putString("key_qr_code", wifiQrCode.getQrCode());
        }
        WifiNetworkConfig wifiNetworkConfig = this.mWifiNetworkConfig;
        if (wifiNetworkConfig != null) {
            bundle.putString("key_wifi_security", wifiNetworkConfig.getSecurity());
            bundle.putString("key_wifi_ssid", this.mWifiNetworkConfig.getSsid());
            bundle.putString("key_wifi_preshared_key", this.mWifiNetworkConfig.getPreSharedKey());
            bundle.putBoolean("key_wifi_hidden_ssid", this.mWifiNetworkConfig.getHiddenSsid());
            bundle.putInt("key_wifi_network_id", this.mWifiNetworkConfig.getNetworkId());
            bundle.putBoolean("key_is_hotspot", this.mWifiNetworkConfig.isHotspot());
        }
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.wifi.dpp.WifiNetworkListFragment.OnChooseNetworkListener
    public void onChooseNetwork(WifiNetworkConfig wifiNetworkConfig) {
        this.mWifiNetworkConfig = new WifiNetworkConfig(wifiNetworkConfig);
        showAddDeviceFragment(true);
    }

    private WifiNetworkConfig getConnectedWifiNetworkConfigOrNull() {
        WifiInfo connectionInfo;
        WifiManager wifiManager = (WifiManager) getSystemService(WifiManager.class);
        if (!wifiManager.isWifiEnabled() || (connectionInfo = wifiManager.getConnectionInfo()) == null) {
            return null;
        }
        int networkId = connectionInfo.getNetworkId();
        for (WifiConfiguration wifiConfiguration : wifiManager.getConfiguredNetworks()) {
            if (wifiConfiguration.networkId == networkId) {
                return WifiNetworkConfig.getValidConfigOrNull(WifiDppUtils.getSecurityString(wifiConfiguration), wifiConfiguration.getPrintableSsid(), wifiConfiguration.preSharedKey, wifiConfiguration.hiddenSSID, wifiConfiguration.networkId, false);
            }
        }
        return null;
    }
}
