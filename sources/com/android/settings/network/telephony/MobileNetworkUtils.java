package com.android.settings.network.telephony;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Telephony;
import android.telecom.PhoneAccountHandle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.euicc.EuiccManager;
import android.telephony.ims.ImsException;
import android.telephony.ims.ImsManager;
import android.telephony.ims.ImsRcsManager;
import android.telephony.ims.ProvisioningManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.android.settings.C0007R$dimen;
import com.android.settingslib.Utils;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.graph.SignalDrawable;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class MobileNetworkUtils {
    public static final Drawable EMPTY_DRAWABLE = new ColorDrawable(0);

    private static int getAdjustedRaf(int i) {
        if ((i & 32771) > 0) {
            i |= 32771;
        }
        if ((i & 17284) > 0) {
            i |= 17284;
        }
        if ((i & 72) > 0) {
            i |= 72;
        }
        if ((i & 10288) > 0) {
            i |= 10288;
        }
        if ((i & 266240) > 0) {
            i |= 266240;
        }
        return (i & 524288) > 0 ? i | 524288 : i;
    }

    public static long getRafFromNetworkType(int i) {
        switch (i) {
            case 0:
                return 50055;
            case 1:
                return 32771;
            case 2:
                return 17284;
            case 3:
                return 50055;
            case 4:
                return 10360;
            case 5:
                return 72;
            case 6:
                return 10288;
            case 7:
                return 60415;
            case 8:
                return 276600;
            case 9:
                return 316295;
            case 10:
                return 326655;
            case 11:
                return 266240;
            case 12:
                return 283524;
            case 13:
                return 65536;
            case 14:
                return 82820;
            case 15:
                return 331776;
            case 16:
                return 98307;
            case 17:
                return 364547;
            case 18:
                return 115591;
            case 19:
                return 349060;
            case 20:
                return 381831;
            case 21:
                return 125951;
            case 22:
                return 392191;
            case 23:
                return 524288;
            case 24:
                return 790528;
            case 25:
                return 800888;
            case 26:
                return 840583;
            case 27:
                return 850943;
            case 28:
                return 807812;
            case 29:
                return 856064;
            case 30:
                return 888835;
            case 31:
                return 873348;
            case 32:
                return 906119;
            case 33:
                return 916479;
            default:
                return 0;
        }
    }

    public static boolean isDpcApnEnforced(Context context) {
        Cursor query = context.getContentResolver().query(Telephony.Carriers.ENFORCE_MANAGED_URI, null, null, null, null);
        boolean z = false;
        if (query != null) {
            try {
                if (query.getCount() == 1) {
                    query.moveToFirst();
                    if (query.getInt(0) > 0) {
                        z = true;
                    }
                    if (query != null) {
                        query.close();
                    }
                    return z;
                }
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
        }
        if (query != null) {
            query.close();
        }
        return false;
        throw th;
    }

    public static boolean isWfcProvisionedOnDevice(int i) {
        ProvisioningManager createForSubscriptionId = ProvisioningManager.createForSubscriptionId(i);
        if (createForSubscriptionId == null) {
            return true;
        }
        return createForSubscriptionId.getProvisioningStatusForCapability(1, 1);
    }

    public static boolean isContactDiscoveryEnabled(Context context, int i) {
        return isContactDiscoveryEnabled((ImsManager) context.getSystemService(ImsManager.class), i);
    }

    public static boolean isContactDiscoveryEnabled(ImsManager imsManager, int i) {
        ImsRcsManager imsRcsManager = getImsRcsManager(imsManager, i);
        if (imsRcsManager == null) {
            return false;
        }
        try {
            return imsRcsManager.getUceAdapter().isUceSettingEnabled();
        } catch (ImsException e) {
            Log.w("MobileNetworkUtils", "UCE service is not available: " + e.getMessage());
            return false;
        }
    }

    public static void setContactDiscoveryEnabled(ImsManager imsManager, int i, boolean z) {
        ImsRcsManager imsRcsManager = getImsRcsManager(imsManager, i);
        if (imsRcsManager != null) {
            try {
                imsRcsManager.getUceAdapter().setUceSettingEnabled(z);
            } catch (ImsException e) {
                Log.w("MobileNetworkUtils", "UCE service is not available: " + e.getMessage());
            }
        }
    }

    private static ImsRcsManager getImsRcsManager(ImsManager imsManager, int i) {
        if (imsManager == null) {
            return null;
        }
        try {
            return imsManager.getImsRcsManager(i);
        } catch (Exception e) {
            Log.w("MobileNetworkUtils", "Could not resolve ImsRcsManager: " + e.getMessage());
            return null;
        }
    }

    public static boolean isContactDiscoveryVisible(Context context, int i) {
        CarrierConfigManager carrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        if (carrierConfigManager != null) {
            return carrierConfigManager.getConfigForSubId(i).getBoolean("use_rcs_presence_bool", false);
        }
        Log.w("MobileNetworkUtils", "isContactDiscoveryVisible: Could not resolve carrier config");
        return false;
    }

    static Intent buildPhoneAccountConfigureIntent(Context context, PhoneAccountHandle phoneAccountHandle) {
        Intent buildConfigureIntent = buildConfigureIntent(context, phoneAccountHandle, "android.telecom.action.CONFIGURE_PHONE_ACCOUNT");
        return buildConfigureIntent == null ? buildConfigureIntent(context, phoneAccountHandle, "android.telecom.action.CONNECTION_SERVICE_CONFIGURE") : buildConfigureIntent;
    }

    private static Intent buildConfigureIntent(Context context, PhoneAccountHandle phoneAccountHandle, String str) {
        if (phoneAccountHandle == null || phoneAccountHandle.getComponentName() == null || TextUtils.isEmpty(phoneAccountHandle.getComponentName().getPackageName())) {
            return null;
        }
        Intent intent = new Intent(str);
        intent.setPackage(phoneAccountHandle.getComponentName().getPackageName());
        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandle);
        if (context.getPackageManager().queryIntentActivities(intent, 0).size() == 0) {
            return null;
        }
        return intent;
    }

    public static boolean showEuiccSettings(Context context) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        try {
            return ((Boolean) ThreadUtils.postOnBackgroundThread(new Callable(context) {
                /* class com.android.settings.network.telephony.$$Lambda$MobileNetworkUtils$uwmDZA7BlwfjlS35v4tBwjAbg */
                public final /* synthetic */ Context f$0;

                {
                    this.f$0 = r1;
                }

                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return MobileNetworkUtils.showEuiccSettingsDetecting(this.f$0);
                }
            }).get()).booleanValue();
        } catch (InterruptedException | ExecutionException unused) {
            Log.w("MobileNetworkUtils", "Accessing Euicc takes too long: +" + (SystemClock.elapsedRealtime() - elapsedRealtime) + "ms");
            return false;
        }
    }

    /* access modifiers changed from: private */
    public static Boolean showEuiccSettingsDetecting(Context context) {
        if (!((EuiccManager) context.getSystemService(EuiccManager.class)).isEnabled()) {
            Log.w("MobileNetworkUtils", "EuiccManager is not enabled.");
            return Boolean.FALSE;
        }
        ContentResolver contentResolver = context.getContentResolver();
        boolean contains = Arrays.asList(TextUtils.split(SystemProperties.get("ro.setupwizard.esim_cid_ignore", ""), ",")).contains(SystemProperties.get("ro.boot.cid", (String) null));
        boolean z = true;
        boolean z2 = SystemProperties.getBoolean("esim.enable_esim_system_ui_by_default", true);
        boolean z3 = Settings.Global.getInt(contentResolver, "euicc_provisioned", 0) != 0;
        boolean isDevelopmentSettingsEnabled = DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context);
        Log.i("MobileNetworkUtils", String.format("showEuiccSettings: esimIgnoredDevice: %b, enabledEsimUiByDefault: %b, euiccProvisioned: %b, inDeveloperMode: %b.", Boolean.valueOf(contains), Boolean.valueOf(z2), Boolean.valueOf(z3), Boolean.valueOf(isDevelopmentSettingsEnabled)));
        if (!isDevelopmentSettingsEnabled && !z3 && (contains || !z2 || !isCurrentCountrySupported(context))) {
            z = false;
        }
        return Boolean.valueOf(z);
    }

    public static void setMobileDataEnabled(Context context, int i, boolean z, boolean z2) {
        List<SubscriptionInfo> activeSubscriptionInfoList;
        TelephonyManager createForSubscriptionId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        createForSubscriptionId.setDataEnabled(z);
        if (z2 && (activeSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList()) != null) {
            for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                if (subscriptionInfo.getSubscriptionId() != i && !subscriptionInfo.isOpportunistic()) {
                    ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(subscriptionInfo.getSubscriptionId()).setDataEnabled(false);
                }
            }
        }
    }

    public static boolean isCdmaOptions(Context context, int i) {
        if (i == -1) {
            return false;
        }
        TelephonyManager createForSubscriptionId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        PersistableBundle configForSubId = ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i);
        if (createForSubscriptionId.getPhoneType() == 2) {
            return true;
        }
        if (configForSubId != null && !configForSubId.getBoolean("hide_carrier_network_settings_bool") && configForSubId.getBoolean("world_phone_bool")) {
            return true;
        }
        if (isWorldMode(context, i)) {
            ContentResolver contentResolver = context.getContentResolver();
            int i2 = Settings.Global.getInt(contentResolver, "preferred_network_mode" + i, TelephonyManager.DEFAULT_PREFERRED_NETWORK_MODE);
            if (i2 == 9 || i2 == 8 || i2 == 26 || i2 == 25 || shouldSpeciallyUpdateGsmCdma(context, i)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGsmOptions(Context context, int i) {
        if (i == -1) {
            return false;
        }
        if (isGsmBasicOptions(context, i)) {
            return true;
        }
        ContentResolver contentResolver = context.getContentResolver();
        int i2 = Settings.Global.getInt(contentResolver, "preferred_network_mode" + i, TelephonyManager.DEFAULT_PREFERRED_NETWORK_MODE);
        return isWorldMode(context, i) && (i2 == 8 || i2 == 9 || i2 == 25 || i2 == 26 || shouldSpeciallyUpdateGsmCdma(context, i));
    }

    private static boolean isGsmBasicOptions(Context context, int i) {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        PersistableBundle configForSubId = ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i);
        if (createForSubscriptionId.getPhoneType() == 1) {
            return true;
        }
        if (configForSubId == null || configForSubId.getBoolean("hide_carrier_network_settings_bool") || !configForSubId.getBoolean("world_phone_bool")) {
            return false;
        }
        return true;
    }

    public static boolean isWorldMode(Context context, int i) {
        PersistableBundle configForSubId = ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i);
        if (configForSubId == null) {
            return false;
        }
        return configForSubId.getBoolean("world_mode_enabled_bool");
    }

    public static boolean shouldDisplayNetworkSelectOptions(Context context, int i) {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        PersistableBundle configForSubId = ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfigForSubId(i);
        if (i != -1 && configForSubId != null && configForSubId.getBoolean("operator_selection_expand_bool") && !configForSubId.getBoolean("hide_carrier_network_settings_bool") && (!configForSubId.getBoolean("csp_enabled_bool") || createForSubscriptionId.isManualNetworkSelectionAllowed())) {
            ContentResolver contentResolver = context.getContentResolver();
            int i2 = Settings.Global.getInt(contentResolver, "preferred_network_mode" + i, TelephonyManager.DEFAULT_PREFERRED_NETWORK_MODE);
            if ((i2 == 8 && isWorldMode(context, i)) || shouldSpeciallyUpdateGsmCdma(context, i)) {
                return false;
            }
            if (isGsmBasicOptions(context, i)) {
                return true;
            }
            if (!isWorldMode(context, i) || i2 != 9) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isTdscdmaSupported(Context context, int i) {
        return isTdscdmaSupported(context, ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(i));
    }

    private static boolean isTdscdmaSupported(Context context, TelephonyManager telephonyManager) {
        PersistableBundle config = ((CarrierConfigManager) context.getSystemService(CarrierConfigManager.class)).getConfig();
        if (config == null) {
            return false;
        }
        if (config.getBoolean("support_tdscdma_bool")) {
            return true;
        }
        String operatorNumeric = telephonyManager.getServiceState().getOperatorNumeric();
        String[] stringArray = config.getStringArray("support_tdscdma_roaming_networks_string_array");
        if (!(stringArray == null || operatorNumeric == null)) {
            for (String str : stringArray) {
                if (operatorNumeric.equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getSearchableSubscriptionId(Context context) {
        int[] activeSubscriptionIdList = getActiveSubscriptionIdList(context);
        if (activeSubscriptionIdList.length >= 1) {
            return activeSubscriptionIdList[0];
        }
        return -1;
    }

    public static int getAvailability(Context context, int i, TelephonyAvailabilityCallback telephonyAvailabilityCallback) {
        if (i != -1) {
            return telephonyAvailabilityCallback.getAvailabilityStatus(i);
        }
        int[] activeSubscriptionIdList = getActiveSubscriptionIdList(context);
        if (ArrayUtils.isEmpty(activeSubscriptionIdList)) {
            return telephonyAvailabilityCallback.getAvailabilityStatus(-1);
        }
        for (int i2 : activeSubscriptionIdList) {
            int availabilityStatus = telephonyAvailabilityCallback.getAvailabilityStatus(i2);
            if (availabilityStatus == 0) {
                return availabilityStatus;
            }
        }
        return telephonyAvailabilityCallback.getAvailabilityStatus(activeSubscriptionIdList[0]);
    }

    static boolean shouldSpeciallyUpdateGsmCdma(Context context, int i) {
        ContentResolver contentResolver = context.getContentResolver();
        int i2 = Settings.Global.getInt(contentResolver, "preferred_network_mode" + i, TelephonyManager.DEFAULT_PREFERRED_NETWORK_MODE);
        return (i2 == 17 || i2 == 20 || i2 == 15 || i2 == 19 || i2 == 22 || i2 == 10) && !isTdscdmaSupported(context, i) && isWorldMode(context, i);
    }

    public static Drawable getSignalStrengthIcon(Context context, int i, int i2, int i3, boolean z) {
        Drawable drawable;
        SignalDrawable signalDrawable = new SignalDrawable(context);
        signalDrawable.setLevel(SignalDrawable.getState(i, i2, z));
        if (i3 == 0) {
            drawable = EMPTY_DRAWABLE;
        } else {
            drawable = context.getResources().getDrawable(i3, context.getTheme());
        }
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(C0007R$dimen.signal_strength_icon_size);
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{drawable, signalDrawable});
        layerDrawable.setLayerGravity(0, 51);
        layerDrawable.setLayerGravity(1, 85);
        layerDrawable.setLayerSize(1, dimensionPixelSize, dimensionPixelSize);
        layerDrawable.setTintList(Utils.getColorAttr(context, 16843817));
        return layerDrawable;
    }

    public static CharSequence getCurrentCarrierNameForDisplay(Context context, int i) {
        SubscriptionInfo subscriptionInfo;
        SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        if (subscriptionManager == null || (subscriptionInfo = getSubscriptionInfo(subscriptionManager, i)) == null) {
            return getOperatorNameFromTelephonyManager(context);
        }
        return subscriptionInfo.getCarrierName();
    }

    private static SubscriptionInfo getSubscriptionInfo(SubscriptionManager subscriptionManager, int i) {
        List<SubscriptionInfo> accessibleSubscriptionInfoList = subscriptionManager.getAccessibleSubscriptionInfoList();
        if (accessibleSubscriptionInfoList == null) {
            accessibleSubscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
        }
        if (accessibleSubscriptionInfoList == null) {
            return null;
        }
        for (SubscriptionInfo subscriptionInfo : accessibleSubscriptionInfoList) {
            if (subscriptionInfo.getSubscriptionId() == i) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    private static String getOperatorNameFromTelephonyManager(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (telephonyManager == null) {
            return null;
        }
        return telephonyManager.getNetworkOperatorName();
    }

    private static int[] getActiveSubscriptionIdList(Context context) {
        List<SubscriptionInfo> activeSubscriptionInfoList = ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoList();
        int i = 0;
        if (activeSubscriptionInfoList == null) {
            return new int[0];
        }
        int[] iArr = new int[activeSubscriptionInfoList.size()];
        for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
            iArr[i] = subscriptionInfo.getSubscriptionId();
            i++;
        }
        return iArr;
    }

    private static boolean isCurrentCountrySupported(Context context) {
        EuiccManager euiccManager = (EuiccManager) context.getSystemService(EuiccManager.class);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        for (int i = 0; i < telephonyManager.getPhoneCount(); i++) {
            String networkCountryIso = telephonyManager.getNetworkCountryIso(i);
            if (euiccManager.isSupportedCountry(networkCountryIso)) {
                Log.i("MobileNetworkUtils", "isCurrentCountrySupported: eSIM is supported in " + networkCountryIso);
                return true;
            }
        }
        Log.i("MobileNetworkUtils", "isCurrentCountrySupported: eSIM is not supported in the current country.");
        return false;
    }

    public static int getNetworkTypeFromRaf(int i) {
        switch (getAdjustedRaf(i)) {
            case 72:
                return 5;
            case 10288:
                return 6;
            case 10360:
                return 4;
            case 17284:
                return 2;
            case 32771:
                return 1;
            case 50055:
                return 0;
            case 60415:
                return 7;
            case 65536:
                return 13;
            case 82820:
                return 14;
            case 98307:
                return 16;
            case 115591:
                return 18;
            case 125951:
                return 21;
            case 266240:
                return 11;
            case 276600:
                return 8;
            case 283524:
                return 12;
            case 316295:
                return 9;
            case 326655:
                return 10;
            case 331776:
                return 15;
            case 349060:
                return 19;
            case 364547:
                return 17;
            case 381831:
                return 20;
            case 392191:
                return 22;
            case 524288:
                return 23;
            case 790528:
                return 24;
            case 800888:
                return 25;
            case 807812:
                return 28;
            case 840583:
                return 26;
            case 850943:
                return 27;
            case 856064:
                return 29;
            case 873348:
                return 31;
            case 888835:
                return 30;
            case 906119:
                return 32;
            case 916479:
                return 33;
            default:
                return -1;
        }
    }
}
