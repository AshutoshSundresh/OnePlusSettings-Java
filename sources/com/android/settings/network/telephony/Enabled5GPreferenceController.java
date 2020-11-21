package com.android.settings.network.telephony;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class Enabled5GPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "Enable5g";
    private PersistableBundle mCarrierConfig;
    private CarrierConfigManager mCarrierConfigManager;
    private final BroadcastReceiver mDefaultDataChangedReceiver = new BroadcastReceiver() {
        /* class com.android.settings.network.telephony.Enabled5GPreferenceController.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            if (Enabled5GPreferenceController.this.mPreference != null) {
                Log.d(Enabled5GPreferenceController.TAG, "DDS is changed");
                Enabled5GPreferenceController enabled5GPreferenceController = Enabled5GPreferenceController.this;
                enabled5GPreferenceController.updateState(enabled5GPreferenceController.mPreference);
            }
        }
    };
    Preference mPreference;
    private ContentObserver mPreferredNetworkModeObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        /* class com.android.settings.network.telephony.Enabled5GPreferenceController.AnonymousClass2 */

        public void onChange(boolean z) {
            if (Enabled5GPreferenceController.this.mPreference != null) {
                Log.d(Enabled5GPreferenceController.TAG, "mPreferredNetworkModeObserver#onChange");
                Enabled5GPreferenceController enabled5GPreferenceController = Enabled5GPreferenceController.this;
                enabled5GPreferenceController.updateState(enabled5GPreferenceController.mPreference);
            }
        }
    };
    private ContentObserver mSubsidySettingsObserver;
    private TelephonyManager mTelephonyManager;

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public Enabled5GPreferenceController(Context context, String str) {
        super(context, str);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    public Enabled5GPreferenceController init(int i) {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId) && this.mSubId == i) {
            return this;
        }
        this.mSubId = i;
        this.mCarrierConfig = this.mCarrierConfigManager.getConfigForSubId(i);
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        return this;
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback, com.android.settings.network.telephony.TelephonyTogglePreferenceController
    public int getAvailabilityStatus(int i) {
        init(i);
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(i);
        if (configForSubId == null || this.mTelephonyManager == null) {
            return 2;
        }
        boolean z = true;
        boolean z2 = SubscriptionManager.getDefaultDataSubscriptionId() == i;
        boolean z3 = (this.mTelephonyManager.getAllowedNetworkTypes() & 524288) > 0;
        if (!SubscriptionManager.isValidSubscriptionId(i) || configForSubId.getBoolean("hide_enabled_5g_bool") || !z3 || !z2) {
            z = false;
        }
        if (z) {
            return 0;
        }
        return 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        contentResolver.registerContentObserver(Settings.Global.getUriFor("preferred_network_mode" + this.mSubId), true, this.mPreferredNetworkModeObserver);
        this.mContext.registerReceiver(this.mDefaultDataChangedReceiver, new IntentFilter("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED"));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mPreferredNetworkModeObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mPreferredNetworkModeObserver);
        }
        BroadcastReceiver broadcastReceiver = this.mDefaultDataChangedReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.TogglePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        SwitchPreference switchPreference = (SwitchPreference) preference;
        switchPreference.setVisible(isAvailable());
        ContentResolver contentResolver = this.mContext.getContentResolver();
        switchPreference.setChecked(isNrNetworkModeType(MobileNetworkUtils.getRafFromNetworkType(Settings.Global.getInt(contentResolver, "preferred_network_mode" + this.mSubId, TelephonyManager.DEFAULT_PREFERRED_NETWORK_MODE))));
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        long j;
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return false;
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        int i = Settings.Global.getInt(contentResolver, "preferred_network_mode" + this.mSubId, TelephonyManager.DEFAULT_PREFERRED_NETWORK_MODE);
        if (23 != i) {
            long rafFromNetworkType = MobileNetworkUtils.getRafFromNetworkType(i);
            j = z ? rafFromNetworkType | 524288 : rafFromNetworkType & -524289;
        } else {
            j = MobileNetworkUtils.getRafFromNetworkType(11);
        }
        ContentResolver contentResolver2 = this.mContext.getContentResolver();
        Settings.Global.putInt(contentResolver2, "preferred_network_mode" + this.mSubId, MobileNetworkUtils.getNetworkTypeFromRaf((int) j));
        if (!this.mTelephonyManager.setPreferredNetworkTypeBitmask(j)) {
            return false;
        }
        Log.d(TAG, "setPreferredNetworkTypeBitmask");
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        return isNrNetworkModeType(MobileNetworkUtils.getRafFromNetworkType(Settings.Global.getInt(contentResolver, "preferred_network_mode" + this.mSubId, TelephonyManager.DEFAULT_PREFERRED_NETWORK_MODE)));
    }

    private boolean isNrNetworkModeType(long j) {
        return checkSupportedRadioBitmask(j, 524288);
    }

    /* access modifiers changed from: package-private */
    public boolean checkSupportedRadioBitmask(long j, long j2) {
        Log.d(TAG, "supportedRadioBitmask: " + j);
        return (j2 & j) > 0;
    }
}
