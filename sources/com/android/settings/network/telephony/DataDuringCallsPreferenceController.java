package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.network.MobileDataContentObserver;
import com.android.settings.network.SubscriptionsChangeListener;
import com.android.settings.slices.SliceBackgroundWorker;

public class DataDuringCallsPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, SubscriptionsChangeListener.SubscriptionsChangeListenerClient {
    private SubscriptionsChangeListener mChangeListener;
    private TelephonyManager mManager;
    private MobileDataContentObserver mMobileDataContentObserver;
    private SwitchPreference mPreference;
    private PreferenceScreen mScreen;

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

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onAirplaneModeChanged(boolean z) {
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DataDuringCallsPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void init(Lifecycle lifecycle, int i) {
        this.mSubId = i;
        this.mManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (this.mChangeListener == null) {
            this.mChangeListener = new SubscriptionsChangeListener(this.mContext, this);
        }
        this.mChangeListener.start();
        if (this.mMobileDataContentObserver == null) {
            MobileDataContentObserver mobileDataContentObserver = new MobileDataContentObserver(new Handler(Looper.getMainLooper()));
            this.mMobileDataContentObserver = mobileDataContentObserver;
            mobileDataContentObserver.setOnMobileDataChangedListener(new MobileDataContentObserver.OnMobileDataChangedListener() {
                /* class com.android.settings.network.telephony.$$Lambda$DataDuringCallsPreferenceController$lk9OUws_h5DPcYp5kYWzc82dQgw */

                @Override // com.android.settings.network.MobileDataContentObserver.OnMobileDataChangedListener
                public final void onMobileDataChanged() {
                    DataDuringCallsPreferenceController.this.lambda$onResume$0$DataDuringCallsPreferenceController();
                }
            });
        }
        this.mMobileDataContentObserver.register(this.mContext, this.mSubId);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        SubscriptionsChangeListener subscriptionsChangeListener = this.mChangeListener;
        if (subscriptionsChangeListener != null) {
            subscriptionsChangeListener.stop();
        }
        MobileDataContentObserver mobileDataContentObserver = this.mMobileDataContentObserver;
        if (mobileDataContentObserver != null) {
            mobileDataContentObserver.unRegister(this.mContext);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mScreen = preferenceScreen;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mManager.isDataAllowedInVoiceCall();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mManager.setDataAllowedDuringVoiceCall(z);
        return true;
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback, com.android.settings.network.telephony.TelephonyTogglePreferenceController
    public int getAvailabilityStatus(int i) {
        return (!SubscriptionManager.isValidSubscriptionId(i) || SubscriptionManager.getDefaultDataSubscriptionId() == i) ? 2 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.TogglePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference != null) {
            preference.setVisible(isAvailable());
        }
    }

    @Override // com.android.settings.network.SubscriptionsChangeListener.SubscriptionsChangeListenerClient
    public void onSubscriptionsChanged() {
        updateState(this.mPreference);
    }

    @VisibleForTesting
    /* renamed from: refreshPreference */
    public void lambda$onResume$0() {
        PreferenceScreen preferenceScreen = this.mScreen;
        if (preferenceScreen != null) {
            super.displayPreference(preferenceScreen);
        }
    }
}
