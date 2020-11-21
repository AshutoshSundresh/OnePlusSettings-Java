package com.android.settings.network.telephony.gsm;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.network.PreferredNetworkModeContentObserver;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.NetworkSelectSettings;
import com.android.settings.network.telephony.TelephonyBasePreferenceController;
import com.android.settings.network.telephony.gsm.AutoSelectPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class OpenNetworkSelectPagePreferenceController extends TelephonyBasePreferenceController implements AutoSelectPreferenceController.OnNetworkSelectModeListener, LifecycleObserver {
    private Preference mPreference;
    private PreferenceScreen mPreferenceScreen;
    private PreferredNetworkModeContentObserver mPreferredNetworkModeObserver;
    private TelephonyManager mTelephonyManager;

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

    public OpenNetworkSelectPagePreferenceController(Context context, String str) {
        super(context, str);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mSubId = -1;
        PreferredNetworkModeContentObserver preferredNetworkModeContentObserver = new PreferredNetworkModeContentObserver(new Handler(Looper.getMainLooper()));
        this.mPreferredNetworkModeObserver = preferredNetworkModeContentObserver;
        preferredNetworkModeContentObserver.setPreferredNetworkModeChangedListener(new PreferredNetworkModeContentObserver.OnPreferredNetworkModeChangedListener() {
            /* class com.android.settings.network.telephony.gsm.$$Lambda$OpenNetworkSelectPagePreferenceController$owh_25qDasHSujAvHBJFE_as2xQ */

            @Override // com.android.settings.network.PreferredNetworkModeContentObserver.OnPreferredNetworkModeChangedListener
            public final void onPreferredNetworkModeChanged() {
                OpenNetworkSelectPagePreferenceController.this.lambda$new$0$OpenNetworkSelectPagePreferenceController();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: updatePreference */
    public void lambda$new$0() {
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen != null) {
            displayPreference(preferenceScreen);
        }
        Preference preference = this.mPreference;
        if (preference != null) {
            updateState(preference);
        }
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback, com.android.settings.network.telephony.TelephonyBasePreferenceController
    public int getAvailabilityStatus(int i) {
        return MobileNetworkUtils.shouldDisplayNetworkSelectOptions(this.mContext, i) ? 0 : 2;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mPreferredNetworkModeObserver.register(this.mContext, this.mSubId);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mPreferredNetworkModeObserver.unregister(this.mContext);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        boolean z = true;
        if (this.mTelephonyManager.getNetworkSelectionMode() == 1) {
            z = false;
        }
        preference.setEnabled(z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        ServiceState serviceState = this.mTelephonyManager.getServiceState();
        if (serviceState == null || serviceState.getState() != 0) {
            return this.mContext.getString(C0017R$string.network_disconnected);
        }
        return MobileNetworkUtils.getCurrentCarrierNameForDisplay(this.mContext, this.mSubId);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("android.provider.extra.SUB_ID", this.mSubId);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(NetworkSelectSettings.class.getName());
        subSettingLauncher.setSourceMetricsCategory(1581);
        subSettingLauncher.setTitleRes(C0017R$string.choose_network_title);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.launch();
        return true;
    }

    public OpenNetworkSelectPagePreferenceController init(Lifecycle lifecycle, int i) {
        this.mSubId = i;
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        lifecycle.addObserver(this);
        return this;
    }

    @Override // com.android.settings.network.telephony.gsm.AutoSelectPreferenceController.OnNetworkSelectModeListener
    public void onNetworkSelectModeChanged() {
        updateState(this.mPreference);
    }
}
