package com.android.settings.wifi.dpp;

import android.content.Intent;
import android.util.Log;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.C0010R$id;
import com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment;

public class WifiDppEnrolleeActivity extends WifiDppBaseActivity implements WifiDppQrCodeScannerFragment.OnScanWifiDppSuccessListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1596;
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment.OnScanWifiDppSuccessListener
    public void onScanWifiDppSuccess(WifiQrCode wifiQrCode) {
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.wifi.dpp.WifiDppBaseActivity
    public void handleIntent(Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (action == null) {
            finish();
            return;
        }
        char c = 65535;
        if (action.hashCode() == 2082201034 && action.equals("android.settings.WIFI_DPP_ENROLLEE_QR_CODE_SCANNER")) {
            c = 0;
        }
        if (c != 0) {
            Log.e("WifiDppEnrolleeActivity", "Launch with an invalid action");
            finish();
            return;
        }
        showQrCodeScannerFragment(intent.getStringExtra("ssid"));
    }

    private void showQrCodeScannerFragment(String str) {
        WifiDppQrCodeScannerFragment wifiDppQrCodeScannerFragment = (WifiDppQrCodeScannerFragment) this.mFragmentManager.findFragmentByTag("qr_code_scanner_fragment");
        if (wifiDppQrCodeScannerFragment == null) {
            WifiDppQrCodeScannerFragment wifiDppQrCodeScannerFragment2 = new WifiDppQrCodeScannerFragment(str);
            FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
            beginTransaction.replace(C0010R$id.fragment_container, wifiDppQrCodeScannerFragment2, "qr_code_scanner_fragment");
            beginTransaction.commit();
        } else if (!wifiDppQrCodeScannerFragment.isVisible()) {
            this.mFragmentManager.popBackStackImmediate();
        }
    }
}
