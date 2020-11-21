package com.android.settings.wifi.calling;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.ims.ImsMmTelManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.slices.CustomSliceRegistry;
import com.android.settings.slices.SliceBroadcastReceiver;
import com.android.settingslib.Utils;
import com.oneplus.settings.OPMemberController;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WifiCallingSliceHelper {
    private final Context mContext;

    public WifiCallingSliceHelper(Context context) {
        this.mContext = context;
    }

    public Slice createWifiCallingSlice(Uri uri) {
        int defaultVoiceSubId = getDefaultVoiceSubId();
        if (!SubscriptionManager.isValidSubscriptionId(defaultVoiceSubId)) {
            Log.d("WifiCallingSliceHelper", "Invalid subscription Id");
            return null;
        } else if (!queryImsState(defaultVoiceSubId).isWifiCallingProvisioned()) {
            Log.d("WifiCallingSliceHelper", "Wifi calling is either not provisioned or not enabled by Platform");
            return null;
        } else {
            boolean isWifiCallingEnabled = isWifiCallingEnabled();
            if (getWifiCallingCarrierActivityIntent(defaultVoiceSubId) == null || isWifiCallingEnabled) {
                return getWifiCallingSlice(uri, isWifiCallingEnabled, defaultVoiceSubId);
            }
            Log.d("WifiCallingSliceHelper", "Needs Activation");
            Resources resourcesForSubId = getResourcesForSubId(defaultVoiceSubId);
            return getNonActionableWifiCallingSlice(resourcesForSubId.getText(C0017R$string.wifi_calling_settings_title), resourcesForSubId.getText(C0017R$string.wifi_calling_settings_activation_instructions), uri, getActivityIntent("android.settings.WIFI_CALLING_SETTINGS"));
        }
    }

    private boolean isWifiCallingEnabled() {
        WifiCallingQueryImsState queryImsState = queryImsState(getDefaultVoiceSubId());
        return queryImsState.isEnabledByUser() && queryImsState.isAllowUserControl();
    }

    private Slice getWifiCallingSlice(Uri uri, boolean z, int i) {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.wifi_signal);
        Resources resourcesForSubId = getResourcesForSubId(i);
        ListBuilder listBuilder = new ListBuilder(this.mContext, uri, -1);
        listBuilder.setAccentColor(Utils.getColorAccentDefaultColor(this.mContext));
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitle(resourcesForSubId.getText(C0017R$string.wifi_calling_settings_title));
        rowBuilder.addEndItem(SliceAction.createToggle(getBroadcastIntent("com.android.settings.wifi.calling.action.WIFI_CALLING_CHANGED"), null, z));
        rowBuilder.setPrimaryAction(SliceAction.createDeeplink(getActivityIntent("android.settings.WIFI_CALLING_SETTINGS"), createWithResource, 0, resourcesForSubId.getText(C0017R$string.wifi_calling_settings_title)));
        listBuilder.addRow(rowBuilder);
        return listBuilder.build();
    }

    public Slice createWifiCallingPreferenceSlice(Uri uri) {
        int defaultVoiceSubId = getDefaultVoiceSubId();
        if (!SubscriptionManager.isValidSubscriptionId(defaultVoiceSubId)) {
            Log.d("WifiCallingSliceHelper", "Invalid Subscription Id");
            return null;
        }
        boolean isCarrierConfigManagerKeyEnabled = isCarrierConfigManagerKeyEnabled("editable_wfc_mode_bool", defaultVoiceSubId, false);
        boolean isCarrierConfigManagerKeyEnabled2 = isCarrierConfigManagerKeyEnabled("carrier_wfc_supports_wifi_only_bool", defaultVoiceSubId, true);
        if (!isCarrierConfigManagerKeyEnabled) {
            Log.d("WifiCallingSliceHelper", "Wifi calling preference is not editable");
            return null;
        } else if (!queryImsState(defaultVoiceSubId).isWifiCallingProvisioned()) {
            Log.d("WifiCallingSliceHelper", "Wifi calling is either not provisioned or not enabled by platform");
            return null;
        } else {
            try {
                ImsMmTelManager imsMmTelManager = getImsMmTelManager(defaultVoiceSubId);
                boolean isWifiCallingEnabled = isWifiCallingEnabled();
                int wfcMode = getWfcMode(imsMmTelManager);
                if (isWifiCallingEnabled) {
                    return getWifiCallingPreferenceSlice(isCarrierConfigManagerKeyEnabled2, wfcMode, uri, defaultVoiceSubId);
                }
                Resources resourcesForSubId = getResourcesForSubId(defaultVoiceSubId);
                return getNonActionableWifiCallingSlice(resourcesForSubId.getText(C0017R$string.wifi_calling_mode_title), resourcesForSubId.getText(C0017R$string.wifi_calling_turn_on), uri, getActivityIntent("android.settings.WIFI_CALLING_SETTINGS"));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                Log.e("WifiCallingSliceHelper", "Unable to get wifi calling preferred mode", e);
                return null;
            }
        }
    }

    private Slice getWifiCallingPreferenceSlice(boolean z, int i, Uri uri, int i2) {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.wifi_signal);
        Resources resourcesForSubId = getResourcesForSubId(i2);
        ListBuilder listBuilder = new ListBuilder(this.mContext, uri, -1);
        listBuilder.setAccentColor(Utils.getColorAccentDefaultColor(this.mContext));
        ListBuilder.HeaderBuilder headerBuilder = new ListBuilder.HeaderBuilder();
        headerBuilder.setTitle(resourcesForSubId.getText(C0017R$string.wifi_calling_mode_title));
        headerBuilder.setPrimaryAction(SliceAction.createDeeplink(getActivityIntent("android.settings.WIFI_CALLING_SETTINGS"), createWithResource, 0, resourcesForSubId.getText(C0017R$string.wifi_calling_mode_title)));
        if (!com.android.settings.Utils.isSettingsIntelligence(this.mContext)) {
            headerBuilder.setSubtitle(getWifiCallingPreferenceSummary(i, i2));
        }
        listBuilder.setHeader(headerBuilder);
        if (z) {
            listBuilder.addRow(wifiPreferenceRowBuilder(listBuilder, 17041477, "com.android.settings.slice.action.WIFI_CALLING_PREFERENCE_WIFI_ONLY", i == 0, i2));
        }
        listBuilder.addRow(wifiPreferenceRowBuilder(listBuilder, 17041478, "com.android.settings.slice.action.WIFI_CALLING_PREFERENCE_WIFI_PREFERRED", i == 2, i2));
        listBuilder.addRow(wifiPreferenceRowBuilder(listBuilder, 17041475, "com.android.settings.slice.action.WIFI_CALLING_PREFERENCE_CELLULAR_PREFERRED", i == 1, i2));
        return listBuilder.build();
    }

    private ListBuilder.RowBuilder wifiPreferenceRowBuilder(ListBuilder listBuilder, int i, String str, boolean z, int i2) {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.radio_button_check);
        Resources resourcesForSubId = getResourcesForSubId(i2);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitle(resourcesForSubId.getText(i));
        rowBuilder.setTitleItem(SliceAction.createToggle(getBroadcastIntent(str), createWithResource, resourcesForSubId.getText(i), z));
        return rowBuilder;
    }

    private CharSequence getWifiCallingPreferenceSummary(int i, int i2) {
        Resources resourcesForSubId = getResourcesForSubId(i2);
        if (i == 0) {
            return resourcesForSubId.getText(17041477);
        }
        if (i == 1) {
            return resourcesForSubId.getText(17041475);
        }
        if (i != 2) {
            return null;
        }
        return resourcesForSubId.getText(17041478);
    }

    /* access modifiers changed from: protected */
    public ImsMmTelManager getImsMmTelManager(int i) {
        return ImsMmTelManager.createForSubscriptionId(i);
    }

    private int getWfcMode(final ImsMmTelManager imsMmTelManager) throws InterruptedException, ExecutionException, TimeoutException {
        FutureTask futureTask = new FutureTask(new Callable<Integer>(this) {
            /* class com.android.settings.wifi.calling.WifiCallingSliceHelper.AnonymousClass1 */

            @Override // java.util.concurrent.Callable
            public Integer call() {
                return Integer.valueOf(imsMmTelManager.getVoWiFiModeSetting());
            }
        });
        Executors.newSingleThreadExecutor().execute(futureTask);
        return ((Integer) futureTask.get(2000, TimeUnit.MILLISECONDS)).intValue();
    }

    public void handleWifiCallingChanged(Intent intent) {
        int defaultVoiceSubId = getDefaultVoiceSubId();
        if (SubscriptionManager.isValidSubscriptionId(defaultVoiceSubId)) {
            WifiCallingQueryImsState queryImsState = queryImsState(defaultVoiceSubId);
            if (queryImsState.isWifiCallingProvisioned()) {
                boolean z = queryImsState.isEnabledByUser() && queryImsState.isAllowUserControl();
                boolean booleanExtra = intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", z);
                Intent wifiCallingCarrierActivityIntent = getWifiCallingCarrierActivityIntent(defaultVoiceSubId);
                if ((!booleanExtra || wifiCallingCarrierActivityIntent == null) && booleanExtra != z) {
                    getImsMmTelManager(defaultVoiceSubId).setVoWiFiSettingEnabled(booleanExtra);
                }
            }
        }
        this.mContext.getContentResolver().notifyChange(CustomSliceRegistry.WIFI_CALLING_URI, null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x007c, code lost:
        if (r3 != false) goto L_0x0080;
     */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x007c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleWifiCallingPreferenceChanged(android.content.Intent r10) {
        /*
        // Method dump skipped, instructions count: 148
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.calling.WifiCallingSliceHelper.handleWifiCallingPreferenceChanged(android.content.Intent):void");
    }

    private Slice getNonActionableWifiCallingSlice(CharSequence charSequence, CharSequence charSequence2, Uri uri, PendingIntent pendingIntent) {
        IconCompat createWithResource = IconCompat.createWithResource(this.mContext, C0008R$drawable.wifi_signal);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder();
        rowBuilder.setTitle(charSequence);
        rowBuilder.setPrimaryAction(SliceAction.createDeeplink(pendingIntent, createWithResource, 1, charSequence));
        if (!com.android.settings.Utils.isSettingsIntelligence(this.mContext)) {
            rowBuilder.setSubtitle(charSequence2);
        }
        ListBuilder listBuilder = new ListBuilder(this.mContext, uri, -1);
        listBuilder.setAccentColor(Utils.getColorAccentDefaultColor(this.mContext));
        listBuilder.addRow(rowBuilder);
        return listBuilder.build();
    }

    /* access modifiers changed from: protected */
    public boolean isCarrierConfigManagerKeyEnabled(String str, int i, boolean z) {
        PersistableBundle configForSubId;
        CarrierConfigManager carrierConfigManager = getCarrierConfigManager(this.mContext);
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(i)) == null) {
            return false;
        }
        return configForSubId.getBoolean(str, z);
    }

    /* access modifiers changed from: protected */
    public CarrierConfigManager getCarrierConfigManager(Context context) {
        return (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    /* access modifiers changed from: protected */
    public int getDefaultVoiceSubId() {
        return SubscriptionManager.getDefaultVoiceSubscriptionId();
    }

    /* access modifiers changed from: protected */
    public Intent getWifiCallingCarrierActivityIntent(int i) {
        PersistableBundle configForSubId;
        ComponentName unflattenFromString;
        CarrierConfigManager carrierConfigManager = getCarrierConfigManager(this.mContext);
        if (carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(i)) == null) {
            return null;
        }
        String string = configForSubId.getString("wfc_emergency_address_carrier_app_string");
        if (TextUtils.isEmpty(string) || (unflattenFromString = ComponentName.unflattenFromString(string)) == null) {
            return null;
        }
        Intent intent = new Intent();
        intent.setComponent(unflattenFromString);
        return intent;
    }

    private PendingIntent getBroadcastIntent(String str) {
        Intent intent = new Intent(str);
        intent.setClass(this.mContext, SliceBroadcastReceiver.class);
        intent.addFlags(268435456);
        return PendingIntent.getBroadcast(this.mContext, 0, intent, 268435456);
    }

    private PendingIntent getActivityIntent(String str) {
        Intent intent = new Intent(str);
        intent.setPackage(OPMemberController.PACKAGE_NAME);
        intent.addFlags(268435456);
        return PendingIntent.getActivity(this.mContext, 0, intent, 0);
    }

    private Resources getResourcesForSubId(int i) {
        return SubscriptionManager.getResourcesForSubId(this.mContext, i);
    }

    /* access modifiers changed from: package-private */
    public WifiCallingQueryImsState queryImsState(int i) {
        return new WifiCallingQueryImsState(this.mContext, i);
    }
}
