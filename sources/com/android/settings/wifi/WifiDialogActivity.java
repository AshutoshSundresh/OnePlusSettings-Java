package com.android.settings.wifi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import com.android.settings.C0018R$style;
import com.android.settings.SetupWizardUtils;
import com.android.settings.wifi.WifiDialog;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settingslib.wifi.AccessPoint;
import com.google.android.setupcompat.util.WizardManagerHelper;

public class WifiDialogActivity extends Activity implements WifiDialog.WifiDialogListener, DialogInterface.OnDismissListener {
    static final String KEY_CONNECT_FOR_CALLER = "connect_for_caller";
    private WifiDialog mDialog;
    private Intent mIntent;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        Intent intent = getIntent();
        this.mIntent = intent;
        if (WizardManagerHelper.isSetupWizardIntent(intent)) {
            setTheme(SetupWizardUtils.getTransparentTheme(this.mIntent));
        }
        super.onCreate(bundle);
        Bundle bundleExtra = this.mIntent.getBundleExtra("access_point_state");
        AccessPoint accessPoint = null;
        if (bundleExtra != null) {
            accessPoint = new AccessPoint(this, bundleExtra);
        }
        if (WizardManagerHelper.isAnySetupWizard(getIntent())) {
            this.mDialog = WifiDialog.createModal(this, this, accessPoint, 1, C0018R$style.SuwAlertDialogThemeCompat_Light);
        } else {
            this.mDialog = WifiDialog.createModal(this, this, accessPoint, 1);
        }
        this.mDialog.show();
        this.mDialog.setOnDismissListener(this);
    }

    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    public void onDestroy() {
        super.onDestroy();
        WifiDialog wifiDialog = this.mDialog;
        if (wifiDialog != null && wifiDialog.isShowing()) {
            this.mDialog.dismiss();
            this.mDialog = null;
        }
    }

    @Override // com.android.settings.wifi.WifiDialog.WifiDialogListener
    public void onForget(WifiDialog wifiDialog) {
        WifiManager wifiManager = (WifiManager) getSystemService(WifiManager.class);
        AccessPoint accessPoint = wifiDialog.getController().getAccessPoint();
        if (accessPoint != null) {
            if (accessPoint.isSaved()) {
                wifiManager.forget(accessPoint.getConfig().networkId, null);
            } else if (accessPoint.getNetworkInfo() == null || accessPoint.getNetworkInfo().getState() == NetworkInfo.State.DISCONNECTED) {
                Log.e("WifiDialogActivity", "Failed to forget invalid network " + accessPoint.getConfig());
            } else {
                wifiManager.disableEphemeralNetwork(AccessPoint.convertToQuotedString(accessPoint.getSsidStr()));
            }
        }
        Intent intent = new Intent();
        if (accessPoint != null) {
            Bundle bundle = new Bundle();
            accessPoint.saveWifiState(bundle);
            intent.putExtra("access_point_state", bundle);
        }
        setResult(2);
        finish();
    }

    @Override // com.android.settings.wifi.WifiDialog.WifiDialogListener
    public void onSubmit(WifiDialog wifiDialog) {
        NetworkInfo networkInfo;
        WifiConfiguration config = wifiDialog.getController().getConfig();
        AccessPoint accessPoint = wifiDialog.getController().getAccessPoint();
        WifiManager wifiManager = (WifiManager) getSystemService(WifiManager.class);
        if (getIntent().getBooleanExtra(KEY_CONNECT_FOR_CALLER, true)) {
            if (config != null) {
                wifiManager.save(config, null);
                if (accessPoint != null && ((networkInfo = accessPoint.getNetworkInfo()) == null || !networkInfo.isConnected())) {
                    wifiManager.connect(config, null);
                }
            } else if (accessPoint != null && accessPoint.isSaved()) {
                wifiManager.connect(accessPoint.getConfig(), null);
            }
        }
        Intent intent = new Intent();
        if (accessPoint != null) {
            Bundle bundle = new Bundle();
            accessPoint.saveWifiState(bundle);
            intent.putExtra("access_point_state", bundle);
        }
        if (config != null) {
            intent.putExtra("wifi_configuration", config);
        }
        setResult(1, intent);
        finish();
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.mDialog = null;
        finish();
    }

    @Override // com.android.settings.wifi.WifiDialog.WifiDialogListener
    public void onScan(WifiDialog wifiDialog, String str) {
        Intent enrolleeQrCodeScannerIntent = WifiDppUtils.getEnrolleeQrCodeScannerIntent(str);
        WizardManagerHelper.copyWizardManagerExtras(this.mIntent, enrolleeQrCodeScannerIntent);
        startActivityForResult(enrolleeQrCodeScannerIntent, 0);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 0 && i2 == -1) {
            setResult(1, intent);
            finish();
        }
    }
}
