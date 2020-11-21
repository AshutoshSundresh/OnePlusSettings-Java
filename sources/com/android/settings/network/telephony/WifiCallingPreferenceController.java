package com.android.settings.network.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.os.PersistableBundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.CarrierConfigManager;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.ims.ImsMmTelManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.network.ims.WifiCallingQueryImsState;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class WifiCallingPreferenceController extends TelephonyBasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "WifiCallingPreference";
    Integer mCallState;
    CarrierConfigManager mCarrierConfigManager;
    private ImsMmTelManager mImsMmTelManager;
    private PhoneCallStateListener mPhoneStateListener = new PhoneCallStateListener();
    private Preference mPreference;
    PhoneAccountHandle mSimCallManager;

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WifiCallingPreferenceController(Context context, String str) {
        super(context, str);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback, com.android.settings.network.telephony.TelephonyBasePreferenceController
    public int getAvailabilityStatus(int i) {
        return (!SubscriptionManager.isValidSubscriptionId(i) || !isWifiCallingEnabled(this.mContext, i)) ? 3 : 0;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mPhoneStateListener.register(this.mContext, this.mSubId);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mPhoneStateListener.unregister();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = findPreference;
        Intent intent = findPreference.getIntent();
        if (intent != null) {
            intent.putExtra("android.provider.extra.SUB_ID", this.mSubId);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mCallState == null || preference == null) {
            Log.d(TAG, "Skip update under mCallState=" + this.mCallState);
            return;
        }
        CharSequence charSequence = null;
        PhoneAccountHandle phoneAccountHandle = this.mSimCallManager;
        boolean z = false;
        if (phoneAccountHandle != null) {
            Intent buildPhoneAccountConfigureIntent = MobileNetworkUtils.buildPhoneAccountConfigureIntent(this.mContext, phoneAccountHandle);
            if (buildPhoneAccountConfigureIntent != null) {
                PackageManager packageManager = this.mContext.getPackageManager();
                preference.setTitle(packageManager.queryIntentActivities(buildPhoneAccountConfigureIntent, 0).get(0).loadLabel(packageManager));
                preference.setIntent(buildPhoneAccountConfigureIntent);
            } else {
                return;
            }
        } else {
            preference.setTitle(SubscriptionManager.getResourcesForSubId(this.mContext, this.mSubId).getString(C0017R$string.wifi_calling_settings_title));
            charSequence = getResourceIdForWfcMode(this.mSubId);
        }
        preference.setSummary(charSequence);
        if (this.mCallState.intValue() == 0) {
            z = true;
        }
        preference.setEnabled(z);
    }

    private CharSequence getResourceIdForWfcMode(int i) {
        int i2;
        int i3;
        PersistableBundle configForSubId;
        if (queryImsState(i).isEnabledByUser()) {
            boolean z = false;
            CarrierConfigManager carrierConfigManager = this.mCarrierConfigManager;
            if (!(carrierConfigManager == null || (configForSubId = carrierConfigManager.getConfigForSubId(i)) == null)) {
                z = configForSubId.getBoolean("use_wfc_home_network_mode_in_roaming_network_bool");
            }
            if (!getTelephonyManager(this.mContext, i).isNetworkRoaming() || z) {
                i3 = this.mImsMmTelManager.getVoWiFiModeSetting();
            } else {
                i3 = this.mImsMmTelManager.getVoWiFiRoamingModeSetting();
            }
            if (i3 == 0) {
                i2 = 17041477;
            } else if (i3 == 1) {
                i2 = 17041475;
            } else if (i3 == 2) {
                i2 = 17041478;
            }
            return SubscriptionManager.getResourcesForSubId(this.mContext, i).getText(i2);
        }
        i2 = 17041508;
        return SubscriptionManager.getResourcesForSubId(this.mContext, i).getText(i2);
    }

    public WifiCallingPreferenceController init(int i) {
        this.mSubId = i;
        this.mImsMmTelManager = getImsMmTelManager(i);
        this.mSimCallManager = ((TelecomManager) this.mContext.getSystemService(TelecomManager.class)).getSimCallManagerForSubscription(this.mSubId);
        return this;
    }

    /* access modifiers changed from: package-private */
    public WifiCallingQueryImsState queryImsState(int i) {
        return new WifiCallingQueryImsState(this.mContext, i);
    }

    /* access modifiers changed from: protected */
    public ImsMmTelManager getImsMmTelManager(int i) {
        if (!SubscriptionManager.isValidSubscriptionId(i)) {
            return null;
        }
        return ImsMmTelManager.createForSubscriptionId(i);
    }

    /* access modifiers changed from: package-private */
    public TelephonyManager getTelephonyManager(Context context, int i) {
        TelephonyManager createForSubscriptionId;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        return (SubscriptionManager.isValidSubscriptionId(i) && (createForSubscriptionId = telephonyManager.createForSubscriptionId(i)) != null) ? createForSubscriptionId : telephonyManager;
    }

    private class PhoneCallStateListener extends PhoneStateListener {
        private TelephonyManager mTelephonyManager;

        PhoneCallStateListener() {
            super(Looper.getMainLooper());
        }

        public void onCallStateChanged(int i, String str) {
            WifiCallingPreferenceController.this.mCallState = Integer.valueOf(i);
            WifiCallingPreferenceController wifiCallingPreferenceController = WifiCallingPreferenceController.this;
            wifiCallingPreferenceController.updateState(wifiCallingPreferenceController.mPreference);
        }

        public void register(Context context, int i) {
            TelephonyManager telephonyManager = WifiCallingPreferenceController.this.getTelephonyManager(context, i);
            this.mTelephonyManager = telephonyManager;
            telephonyManager.listen(this, 32);
        }

        public void unregister() {
            WifiCallingPreferenceController.this.mCallState = null;
            this.mTelephonyManager.listen(this, 0);
        }
    }

    private boolean isWifiCallingEnabled(Context context, int i) {
        PhoneAccountHandle simCallManagerForSubscription = ((TelecomManager) context.getSystemService(TelecomManager.class)).getSimCallManagerForSubscription(i);
        SubscriptionManager.getSlotIndex(i);
        if (simCallManagerForSubscription != null) {
            return MobileNetworkUtils.buildPhoneAccountConfigureIntent(context, simCallManagerForSubscription) != null;
        }
        return queryImsState(i).isReadyToWifiCalling();
    }
}
