package com.android.settings.network;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.network.telephony.MobileNetworkActivity;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.List;

public class MobileNetworkPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnStart, OnStop {
    static final String KEY_MOBILE_NETWORK_SETTINGS = "mobile_network_settings";
    private BroadcastReceiver mAirplanModeChangedReceiver;
    private final boolean mIsSecondaryUser;
    private final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionsChangeListener;
    PhoneStateListener mPhoneStateListener;
    private Preference mPreference;
    private SubscriptionManager mSubscriptionManager;
    private String mSummary;
    private final TelephonyManager mTelephonyManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_MOBILE_NETWORK_SETTINGS;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !isUserRestricted() && !Utils.isWifiOnly(this.mContext);
    }

    public boolean isUserRestricted() {
        return this.mIsSecondaryUser || RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_config_mobile_networks", UserHandle.myUserId());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        SubscriptionManager subscriptionManager = this.mSubscriptionManager;
        if (subscriptionManager != null) {
            subscriptionManager.addOnSubscriptionsChangedListener(this.mOnSubscriptionsChangeListener);
        }
        if (isAvailable()) {
            if (this.mPhoneStateListener == null) {
                this.mPhoneStateListener = new PhoneStateListener() {
                    /* class com.android.settings.network.MobileNetworkPreferenceController.AnonymousClass2 */

                    public void onServiceStateChanged(ServiceState serviceState) {
                        MobileNetworkPreferenceController.this.updateDisplayName();
                        MobileNetworkPreferenceController mobileNetworkPreferenceController = MobileNetworkPreferenceController.this;
                        mobileNetworkPreferenceController.updateState(mobileNetworkPreferenceController.mPreference);
                    }
                };
            }
            this.mTelephonyManager.listen(this.mPhoneStateListener, 1);
        }
        BroadcastReceiver broadcastReceiver = this.mAirplanModeChangedReceiver;
        if (broadcastReceiver != null) {
            this.mContext.registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.AIRPLANE_MODE"));
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateDisplayName() {
        if (this.mPreference != null) {
            List<SubscriptionInfo> activeSubscriptionInfoList = this.mSubscriptionManager.getActiveSubscriptionInfoList();
            if (activeSubscriptionInfoList == null || activeSubscriptionInfoList.isEmpty()) {
                this.mSummary = this.mTelephonyManager.getNetworkOperatorName();
                return;
            }
            boolean z = false;
            StringBuilder sb = new StringBuilder();
            for (SubscriptionInfo subscriptionInfo : activeSubscriptionInfoList) {
                if (isSubscriptionInService(subscriptionInfo.getSubscriptionId())) {
                    if (z) {
                        sb.append(", ");
                    }
                    sb.append(this.mTelephonyManager.getNetworkOperatorName(subscriptionInfo.getSubscriptionId()));
                    z = true;
                }
            }
            this.mSummary = sb.toString();
        }
    }

    private boolean isSubscriptionInService(int i) {
        TelephonyManager telephonyManager = this.mTelephonyManager;
        return telephonyManager != null && telephonyManager.getServiceStateForSubscriber(i).getState() == 0;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        PhoneStateListener phoneStateListener = this.mPhoneStateListener;
        if (phoneStateListener != null) {
            this.mTelephonyManager.listen(phoneStateListener, 0);
        }
        this.mSubscriptionManager.removeOnSubscriptionsChangedListener(this.mOnSubscriptionsChangeListener);
        BroadcastReceiver broadcastReceiver = this.mAirplanModeChangedReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (!(preference instanceof RestrictedPreference) || !((RestrictedPreference) preference).isDisabledByAdmin()) {
            boolean z = false;
            if (Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 0) {
                z = true;
            }
            preference.setEnabled(z);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_MOBILE_NETWORK_SETTINGS.equals(preference.getKey())) {
            return false;
        }
        this.mContext.startActivity(new Intent(this.mContext, MobileNetworkActivity.class));
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mSummary;
    }
}
