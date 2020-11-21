package com.android.settings.wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settingslib.wifi.AccessPoint;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

public class WifiStatusTest extends Activity {
    private TextView mBSSID;
    private TextView mHiddenSSID;
    private TextView mHttpClientTest;
    private String mHttpClientTestResult;
    private TextView mIPAddr;
    private TextView mMACAddr;
    private TextView mNetworkId;
    private TextView mNetworkState;
    View.OnClickListener mPingButtonHandler = new View.OnClickListener() {
        /* class com.android.settings.wifi.WifiStatusTest.AnonymousClass2 */

        public void onClick(View view) {
            WifiStatusTest.this.updatePingState();
        }
    };
    private TextView mPingHostname;
    private String mPingHostnameResult;
    private TextView mRSSI;
    private TextView mRxLinkSpeed;
    private TextView mSSID;
    private TextView mScanList;
    private TextView mSupplicantState;
    private TextView mTxLinkSpeed;
    private WifiManager mWifiManager;
    private TextView mWifiState;
    private IntentFilter mWifiStateFilter;
    private final BroadcastReceiver mWifiStateReceiver = new BroadcastReceiver() {
        /* class com.android.settings.wifi.WifiStatusTest.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
                WifiStatusTest.this.handleWifiStateChanged(intent.getIntExtra("wifi_state", 4));
            } else if (intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
                WifiStatusTest.this.handleNetworkStateChanged((NetworkInfo) intent.getParcelableExtra("networkInfo"));
            } else if (intent.getAction().equals("android.net.wifi.SCAN_RESULTS")) {
                WifiStatusTest.this.handleScanResultsAvailable();
            } else if (!intent.getAction().equals("android.net.wifi.supplicant.CONNECTION_CHANGE")) {
                if (intent.getAction().equals("android.net.wifi.supplicant.STATE_CHANGE")) {
                    WifiStatusTest.this.handleSupplicantStateChanged((SupplicantState) intent.getParcelableExtra("newState"), intent.hasExtra("supplicantError"), intent.getIntExtra("supplicantError", 0));
                } else if (intent.getAction().equals("android.net.wifi.RSSI_CHANGED")) {
                    WifiStatusTest.this.handleSignalChanged(intent.getIntExtra("newRssi", 0));
                } else if (!intent.getAction().equals("android.net.wifi.NETWORK_IDS_CHANGED")) {
                    Log.e("WifiStatusTest", "Received an unknown Wifi Intent");
                }
            }
        }
    };
    private Button pingTestButton;
    private Button updateButton;
    View.OnClickListener updateButtonHandler = new View.OnClickListener() {
        /* class com.android.settings.wifi.WifiStatusTest.AnonymousClass3 */

        public void onClick(View view) {
            WifiInfo connectionInfo = WifiStatusTest.this.mWifiManager.getConnectionInfo();
            WifiStatusTest wifiStatusTest = WifiStatusTest.this;
            wifiStatusTest.setWifiStateText(wifiStatusTest.mWifiManager.getWifiState());
            WifiStatusTest.this.mBSSID.setText(connectionInfo.getBSSID());
            WifiStatusTest.this.mHiddenSSID.setText(String.valueOf(connectionInfo.getHiddenSSID()));
            int ipAddress = connectionInfo.getIpAddress();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ipAddress & 255);
            stringBuffer.append('.');
            int i = ipAddress >>> 8;
            stringBuffer.append(i & 255);
            stringBuffer.append('.');
            int i2 = i >>> 8;
            stringBuffer.append(i2 & 255);
            stringBuffer.append('.');
            stringBuffer.append((i2 >>> 8) & 255);
            WifiStatusTest.this.mIPAddr.setText(stringBuffer);
            TextView textView = WifiStatusTest.this.mTxLinkSpeed;
            textView.setText(String.valueOf(connectionInfo.getTxLinkSpeedMbps()) + " Mbps");
            TextView textView2 = WifiStatusTest.this.mRxLinkSpeed;
            textView2.setText(String.valueOf(connectionInfo.getRxLinkSpeedMbps()) + " Mbps");
            WifiStatusTest.this.mMACAddr.setText(connectionInfo.getMacAddress());
            WifiStatusTest.this.mNetworkId.setText(String.valueOf(connectionInfo.getNetworkId()));
            WifiStatusTest.this.mRSSI.setText(String.valueOf(connectionInfo.getRssi()));
            WifiStatusTest.this.mSSID.setText(connectionInfo.getSSID());
            WifiStatusTest.this.setSupplicantStateText(connectionInfo.getSupplicantState());
        }
    };

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mWifiManager = (WifiManager) getSystemService("wifi");
        IntentFilter intentFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        this.mWifiStateFilter = intentFilter;
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.mWifiStateFilter.addAction("android.net.wifi.SCAN_RESULTS");
        this.mWifiStateFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        this.mWifiStateFilter.addAction("android.net.wifi.RSSI_CHANGED");
        this.mWifiStateFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        registerReceiver(this.mWifiStateReceiver, this.mWifiStateFilter);
        setContentView(C0012R$layout.wifi_status_test);
        Button button = (Button) findViewById(C0010R$id.update);
        this.updateButton = button;
        button.setOnClickListener(this.updateButtonHandler);
        this.mWifiState = (TextView) findViewById(C0010R$id.wifi_state);
        this.mNetworkState = (TextView) findViewById(C0010R$id.network_state);
        this.mSupplicantState = (TextView) findViewById(C0010R$id.supplicant_state);
        this.mRSSI = (TextView) findViewById(C0010R$id.rssi);
        this.mBSSID = (TextView) findViewById(C0010R$id.bssid);
        this.mSSID = (TextView) findViewById(C0010R$id.ssid);
        this.mHiddenSSID = (TextView) findViewById(C0010R$id.hidden_ssid);
        this.mIPAddr = (TextView) findViewById(C0010R$id.ipaddr);
        this.mMACAddr = (TextView) findViewById(C0010R$id.macaddr);
        this.mNetworkId = (TextView) findViewById(C0010R$id.networkid);
        this.mTxLinkSpeed = (TextView) findViewById(C0010R$id.tx_link_speed);
        this.mRxLinkSpeed = (TextView) findViewById(C0010R$id.rx_link_speed);
        this.mScanList = (TextView) findViewById(C0010R$id.scan_list);
        this.mPingHostname = (TextView) findViewById(C0010R$id.pingHostname);
        this.mHttpClientTest = (TextView) findViewById(C0010R$id.httpClientTest);
        Button button2 = (Button) findViewById(C0010R$id.ping_test);
        this.pingTestButton = button2;
        button2.setOnClickListener(this.mPingButtonHandler);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        registerReceiver(this.mWifiStateReceiver, this.mWifiStateFilter);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        unregisterReceiver(this.mWifiStateReceiver);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setSupplicantStateText(SupplicantState supplicantState) {
        if (SupplicantState.FOUR_WAY_HANDSHAKE.equals(supplicantState)) {
            this.mSupplicantState.setText("FOUR WAY HANDSHAKE");
        } else if (SupplicantState.ASSOCIATED.equals(supplicantState)) {
            this.mSupplicantState.setText("ASSOCIATED");
        } else if (SupplicantState.ASSOCIATING.equals(supplicantState)) {
            this.mSupplicantState.setText("ASSOCIATING");
        } else if (SupplicantState.COMPLETED.equals(supplicantState)) {
            this.mSupplicantState.setText("COMPLETED");
        } else if (SupplicantState.DISCONNECTED.equals(supplicantState)) {
            this.mSupplicantState.setText("DISCONNECTED");
        } else if (SupplicantState.DORMANT.equals(supplicantState)) {
            this.mSupplicantState.setText("DORMANT");
        } else if (SupplicantState.GROUP_HANDSHAKE.equals(supplicantState)) {
            this.mSupplicantState.setText("GROUP HANDSHAKE");
        } else if (SupplicantState.INACTIVE.equals(supplicantState)) {
            this.mSupplicantState.setText("INACTIVE");
        } else if (SupplicantState.INVALID.equals(supplicantState)) {
            this.mSupplicantState.setText("INVALID");
        } else if (SupplicantState.SCANNING.equals(supplicantState)) {
            this.mSupplicantState.setText("SCANNING");
        } else if (SupplicantState.UNINITIALIZED.equals(supplicantState)) {
            this.mSupplicantState.setText("UNINITIALIZED");
        } else {
            this.mSupplicantState.setText("BAD");
            Log.e("WifiStatusTest", "supplicant state is bad");
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setWifiStateText(int i) {
        String str;
        if (i == 0) {
            str = getString(C0017R$string.wifi_state_disabling);
        } else if (i == 1) {
            str = getString(C0017R$string.wifi_state_disabled);
        } else if (i == 2) {
            str = getString(C0017R$string.wifi_state_enabling);
        } else if (i == 3) {
            str = getString(C0017R$string.wifi_state_enabled);
        } else if (i != 4) {
            Log.e("WifiStatusTest", "wifi state is bad");
            str = "BAD";
        } else {
            str = getString(C0017R$string.wifi_state_unknown);
        }
        this.mWifiState.setText(str);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleSignalChanged(int i) {
        this.mRSSI.setText(String.valueOf(i));
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleWifiStateChanged(int i) {
        setWifiStateText(i);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleScanResultsAvailable() {
        List<ScanResult> scanResults = this.mWifiManager.getScanResults();
        StringBuffer stringBuffer = new StringBuffer();
        if (scanResults != null) {
            for (int size = scanResults.size() - 1; size >= 0; size--) {
                ScanResult scanResult = scanResults.get(size);
                if (scanResult != null && !TextUtils.isEmpty(scanResult.SSID)) {
                    stringBuffer.append(scanResult.SSID + " ");
                }
            }
        }
        this.mScanList.setText(stringBuffer);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleSupplicantStateChanged(SupplicantState supplicantState, boolean z, int i) {
        if (z) {
            this.mSupplicantState.setText("ERROR AUTHENTICATING");
        } else {
            setSupplicantStateText(supplicantState);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleNetworkStateChanged(NetworkInfo networkInfo) {
        if (this.mWifiManager.isWifiEnabled()) {
            WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
            this.mNetworkState.setText(AccessPoint.getSummary(this, connectionInfo.getSSID(), networkInfo.getDetailedState(), connectionInfo.getNetworkId() == -1, null));
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private final void updatePingState() {
        final Handler handler = new Handler();
        this.mPingHostnameResult = getResources().getString(C0017R$string.radioInfo_unknown);
        this.mHttpClientTestResult = getResources().getString(C0017R$string.radioInfo_unknown);
        this.mPingHostname.setText(this.mPingHostnameResult);
        this.mHttpClientTest.setText(this.mHttpClientTestResult);
        final AnonymousClass4 r1 = new Runnable() {
            /* class com.android.settings.wifi.WifiStatusTest.AnonymousClass4 */

            public void run() {
                WifiStatusTest.this.mPingHostname.setText(WifiStatusTest.this.mPingHostnameResult);
                WifiStatusTest.this.mHttpClientTest.setText(WifiStatusTest.this.mHttpClientTestResult);
            }
        };
        new Thread() {
            /* class com.android.settings.wifi.WifiStatusTest.AnonymousClass5 */

            public void run() {
                WifiStatusTest.this.pingHostname();
                handler.post(r1);
            }
        }.start();
        new Thread() {
            /* class com.android.settings.wifi.WifiStatusTest.AnonymousClass6 */

            public void run() {
                WifiStatusTest.this.httpClientTest();
                handler.post(r1);
            }
        }.start();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private final void pingHostname() {
        try {
            if (Runtime.getRuntime().exec("ping -c 1 -w 100 www.google.com").waitFor() == 0) {
                this.mPingHostnameResult = "Pass";
            } else {
                this.mPingHostnameResult = "Fail: Host unreachable";
            }
        } catch (UnknownHostException unused) {
            this.mPingHostnameResult = "Fail: Unknown Host";
        } catch (IOException unused2) {
            this.mPingHostnameResult = "Fail: IOException";
        } catch (InterruptedException unused3) {
            this.mPingHostnameResult = "Fail: InterruptedException";
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x004b  */
    /* JADX WARNING: Removed duplicated region for block: B:27:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void httpClientTest() {
        /*
            r3 = this;
            r0 = 0
            java.net.URL r1 = new java.net.URL     // Catch:{ IOException -> 0x003f }
            java.lang.String r2 = "https://www.google.com"
            r1.<init>(r2)     // Catch:{ IOException -> 0x003f }
            java.net.URLConnection r1 = r1.openConnection()     // Catch:{ IOException -> 0x003f }
            java.net.HttpURLConnection r1 = (java.net.HttpURLConnection) r1     // Catch:{ IOException -> 0x003f }
            int r0 = r1.getResponseCode()     // Catch:{ IOException -> 0x003b, all -> 0x0038 }
            r2 = 200(0xc8, float:2.8E-43)
            if (r0 != r2) goto L_0x001b
            java.lang.String r0 = "Pass"
            r3.mHttpClientTestResult = r0     // Catch:{ IOException -> 0x003b, all -> 0x0038 }
            goto L_0x0032
        L_0x001b:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x003b, all -> 0x0038 }
            r0.<init>()     // Catch:{ IOException -> 0x003b, all -> 0x0038 }
            java.lang.String r2 = "Fail: Code: "
            r0.append(r2)     // Catch:{ IOException -> 0x003b, all -> 0x0038 }
            java.lang.String r2 = r1.getResponseMessage()     // Catch:{ IOException -> 0x003b, all -> 0x0038 }
            r0.append(r2)     // Catch:{ IOException -> 0x003b, all -> 0x0038 }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException -> 0x003b, all -> 0x0038 }
            r3.mHttpClientTestResult = r0     // Catch:{ IOException -> 0x003b, all -> 0x0038 }
        L_0x0032:
            if (r1 == 0) goto L_0x0048
            r1.disconnect()
            goto L_0x0048
        L_0x0038:
            r3 = move-exception
            r0 = r1
            goto L_0x0049
        L_0x003b:
            r0 = r1
            goto L_0x003f
        L_0x003d:
            r3 = move-exception
            goto L_0x0049
        L_0x003f:
            java.lang.String r1 = "Fail: IOException"
            r3.mHttpClientTestResult = r1     // Catch:{ all -> 0x003d }
            if (r0 == 0) goto L_0x0048
            r0.disconnect()
        L_0x0048:
            return
        L_0x0049:
            if (r0 == 0) goto L_0x004e
            r0.disconnect()
        L_0x004e:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiStatusTest.httpClientTest():void");
    }
}
