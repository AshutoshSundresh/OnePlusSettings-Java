package com.android.settings.wifi.dpp;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import com.android.settings.C0017R$string;
import com.android.settingslib.wifi.AccessPoint;
import com.android.wifitrackerlib.WifiEntry;
import java.time.Duration;
import java.util.concurrent.Executor;

public class WifiDppUtils {
    private static final Duration VIBRATE_DURATION_QR_CODE_RECOGNITION = Duration.ofMillis(3);

    static boolean isWifiDppEnabled(Context context) {
        return ((WifiManager) context.getSystemService(WifiManager.class)).isEasyConnectSupported();
    }

    public static Intent getEnrolleeQrCodeScannerIntent(String str) {
        Intent intent = new Intent("android.settings.WIFI_DPP_ENROLLEE_QR_CODE_SCANNER");
        if (!TextUtils.isEmpty(str)) {
            intent.putExtra("ssid", str);
        }
        return intent;
    }

    private static String getPresharedKey(WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
        for (WifiConfiguration wifiConfiguration2 : wifiManager.getPrivilegedConfiguredNetworks()) {
            if (wifiConfiguration2.networkId == wifiConfiguration.networkId) {
                if (!wifiConfiguration.allowedKeyManagement.get(0) || !wifiConfiguration.allowedAuthAlgorithms.get(1)) {
                    return wifiConfiguration2.preSharedKey;
                }
                return wifiConfiguration2.wepKeys[wifiConfiguration2.wepTxKeyIndex];
            }
        }
        return wifiConfiguration.preSharedKey;
    }

    private static String removeFirstAndLastDoubleQuotes(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        int i = 0;
        int length = str.length() - 1;
        if (str.charAt(0) == '\"') {
            i = 1;
        }
        if (str.charAt(length) == '\"') {
            length--;
        }
        return str.substring(i, length + 1);
    }

    static String getSecurityString(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.allowedKeyManagement.get(8)) {
            return "SAE";
        }
        if (wifiConfiguration.allowedKeyManagement.get(9)) {
            return "nopass";
        }
        if (wifiConfiguration.allowedKeyManagement.get(1) || wifiConfiguration.allowedKeyManagement.get(4)) {
            return "WPA";
        }
        if (wifiConfiguration.wepKeys[0] == null) {
            return "nopass";
        }
        return "WEP";
    }

    public static Intent getConfiguratorQrCodeGeneratorIntentOrNull(Context context, WifiManager wifiManager, AccessPoint accessPoint) {
        Intent intent = new Intent(context, WifiDppConfiguratorActivity.class);
        if (!isSupportConfiguratorQrCodeGenerator(context, accessPoint)) {
            return null;
        }
        intent.setAction("android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_GENERATOR");
        setConfiguratorIntentExtra(intent, wifiManager, accessPoint.getConfig());
        if (accessPoint.isPskSaeTransitionMode()) {
            intent.putExtra("security", "WPA");
        }
        return intent;
    }

    public static Intent getConfiguratorQrCodeGeneratorIntentOrNull(Context context, WifiManager wifiManager, WifiEntry wifiEntry) {
        Intent intent = new Intent(context, WifiDppConfiguratorActivity.class);
        if (!wifiEntry.canShare()) {
            return null;
        }
        intent.setAction("android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_GENERATOR");
        setConfiguratorIntentExtra(intent, wifiManager, wifiEntry.getWifiConfiguration());
        return intent;
    }

    public static Intent getConfiguratorQrCodeScannerIntentOrNull(Context context, WifiManager wifiManager, AccessPoint accessPoint) {
        Intent intent = new Intent(context, WifiDppConfiguratorActivity.class);
        if (!isSupportConfiguratorQrCodeScanner(context, accessPoint)) {
            return null;
        }
        intent.setAction("android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_SCANNER");
        WifiConfiguration config = accessPoint.getConfig();
        setConfiguratorIntentExtra(intent, wifiManager, config);
        int i = config.networkId;
        if (i != -1) {
            intent.putExtra("networkId", i);
            return intent;
        }
        throw new IllegalArgumentException("Invalid network ID");
    }

    public static Intent getConfiguratorQrCodeScannerIntentOrNull(Context context, WifiManager wifiManager, WifiEntry wifiEntry) {
        Intent intent = new Intent(context, WifiDppConfiguratorActivity.class);
        if (!wifiEntry.canEasyConnect()) {
            return null;
        }
        intent.setAction("android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_SCANNER");
        WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
        setConfiguratorIntentExtra(intent, wifiManager, wifiConfiguration);
        int i = wifiConfiguration.networkId;
        if (i != -1) {
            intent.putExtra("networkId", i);
            return intent;
        }
        throw new IllegalArgumentException("Invalid network ID");
    }

    public static Intent getHotspotConfiguratorIntentOrNull(Context context, WifiManager wifiManager, SoftApConfiguration softApConfiguration) {
        String str;
        Intent intent = new Intent(context, WifiDppConfiguratorActivity.class);
        if (!isSupportHotspotConfiguratorQrCodeGenerator(softApConfiguration)) {
            return null;
        }
        intent.setAction("android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_GENERATOR");
        String removeFirstAndLastDoubleQuotes = removeFirstAndLastDoubleQuotes(softApConfiguration.getSsid());
        if (softApConfiguration.getSecurityType() == 1) {
            str = "WPA";
        } else {
            str = softApConfiguration.getSecurityType() == 3 ? "SAE" : "nopass";
        }
        String removeFirstAndLastDoubleQuotes2 = removeFirstAndLastDoubleQuotes(softApConfiguration.getPassphrase());
        if (!TextUtils.isEmpty(removeFirstAndLastDoubleQuotes)) {
            intent.putExtra("ssid", removeFirstAndLastDoubleQuotes);
        }
        if (!TextUtils.isEmpty(str)) {
            intent.putExtra("security", str);
        }
        if (!TextUtils.isEmpty(removeFirstAndLastDoubleQuotes2)) {
            intent.putExtra("preSharedKey", removeFirstAndLastDoubleQuotes2);
        }
        intent.putExtra("hiddenSsid", softApConfiguration.isHiddenSsid());
        intent.putExtra("networkId", -1);
        intent.putExtra("isHotspot", true);
        return intent;
    }

    private static void setConfiguratorIntentExtra(Intent intent, WifiManager wifiManager, WifiConfiguration wifiConfiguration) {
        String removeFirstAndLastDoubleQuotes = removeFirstAndLastDoubleQuotes(wifiConfiguration.SSID);
        String securityString = getSecurityString(wifiConfiguration);
        String removeFirstAndLastDoubleQuotes2 = removeFirstAndLastDoubleQuotes(getPresharedKey(wifiManager, wifiConfiguration));
        if (!TextUtils.isEmpty(removeFirstAndLastDoubleQuotes)) {
            intent.putExtra("ssid", removeFirstAndLastDoubleQuotes);
        }
        if (!TextUtils.isEmpty(securityString)) {
            intent.putExtra("security", securityString);
        }
        if (!TextUtils.isEmpty(removeFirstAndLastDoubleQuotes2)) {
            intent.putExtra("preSharedKey", removeFirstAndLastDoubleQuotes2);
        }
        intent.putExtra("hiddenSsid", wifiConfiguration.hiddenSSID);
    }

    public static void showLockScreen(Context context, final Runnable runnable) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService("keyguard");
        if (keyguardManager.isKeyguardSecure()) {
            AnonymousClass1 r1 = new BiometricPrompt.AuthenticationCallback() {
                /* class com.android.settings.wifi.dpp.WifiDppUtils.AnonymousClass1 */

                public void onAuthenticationError(int i, CharSequence charSequence) {
                }

                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult authenticationResult) {
                    runnable.run();
                }
            };
            BiometricPrompt.Builder title = new BiometricPrompt.Builder(context).setTitle(context.getText(C0017R$string.wifi_dpp_lockscreen_title));
            if (keyguardManager.isDeviceSecure()) {
                title.setDeviceCredentialAllowed(true);
            }
            title.build().authenticate(new CancellationSignal(), new Executor(new Handler(Looper.getMainLooper())) {
                /* class com.android.settings.wifi.dpp.$$Lambda$WifiDppUtils$oTeAENAZBPIju7SIYfaPlvYAJ0 */
                public final /* synthetic */ Handler f$0;

                {
                    this.f$0 = r1;
                }

                public final void execute(Runnable runnable) {
                    this.f$0.post(runnable);
                }
            }, r1);
            return;
        }
        runnable.run();
    }

    public static boolean isSupportConfiguratorQrCodeScanner(Context context, AccessPoint accessPoint) {
        if (accessPoint.isPasspoint()) {
            return false;
        }
        return isSupportWifiDpp(context, accessPoint.getSecurity());
    }

    public static boolean isSupportConfiguratorQrCodeGenerator(Context context, AccessPoint accessPoint) {
        if (accessPoint.isPasspoint()) {
            return false;
        }
        return isSupportZxing(context, accessPoint.getSecurity());
    }

    public static boolean isSupportEnrolleeQrCodeScanner(Context context, int i) {
        return isSupportWifiDpp(context, i) || isSupportZxing(context, i);
    }

    private static boolean isSupportHotspotConfiguratorQrCodeGenerator(SoftApConfiguration softApConfiguration) {
        if (softApConfiguration.getSecurityType() == 1 || softApConfiguration.getSecurityType() == 0 || softApConfiguration.getSecurityType() == 3 || softApConfiguration.getSecurityType() == 4) {
            return true;
        }
        return false;
    }

    private static boolean isSupportWifiDpp(Context context, int i) {
        if (!isWifiDppEnabled(context)) {
            return false;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        if (i == 2) {
            return true;
        }
        if (i == 5 && wifiManager.isWpa3SaeSupported()) {
            return true;
        }
        return false;
    }

    private static boolean isSupportZxing(Context context, int i) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WifiManager.class);
        if (i == 0 || i == 1 || i == 2) {
            return true;
        }
        if (i != 4) {
            if (i == 5 && wifiManager.isWpa3SaeSupported()) {
                return true;
            }
            return false;
        } else if (wifiManager.isEnhancedOpenSupported()) {
            return true;
        } else {
            return false;
        }
    }

    static void triggerVibrationForQrCodeRecognition(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATE_DURATION_QR_CODE_RECOGNITION.toMillis(), -1));
        }
    }
}
