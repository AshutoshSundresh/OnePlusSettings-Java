package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import androidx.preference.PreferenceScreen;
import com.android.settings.network.MobileDataContentObserver;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class MmsMessagePreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private MobileDataContentObserver mMobileDataContentObserver;
    private PreferenceScreen mScreen;
    private SubscriptionManager mSubscriptionManager;
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

    public MmsMessagePreferenceController(Context context, String str) {
        super(context, str);
        this.mSubscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        MobileDataContentObserver mobileDataContentObserver = new MobileDataContentObserver(new Handler(Looper.getMainLooper()));
        this.mMobileDataContentObserver = mobileDataContentObserver;
        mobileDataContentObserver.setOnMobileDataChangedListener(new MobileDataContentObserver.OnMobileDataChangedListener() {
            /* class com.android.settings.network.telephony.$$Lambda$MmsMessagePreferenceController$2e1FtpwzBORCSFQhsMXLPB4Kqz0 */

            @Override // com.android.settings.network.MobileDataContentObserver.OnMobileDataChangedListener
            public final void onMobileDataChanged() {
                MmsMessagePreferenceController.this.lambda$new$0$MmsMessagePreferenceController();
            }
        });
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback, com.android.settings.network.telephony.TelephonyTogglePreferenceController
    public int getAvailabilityStatus(int i) {
        TelephonyManager createForSubscriptionId = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(i);
        if (i == -1 || createForSubscriptionId.isDataEnabled() || !createForSubscriptionId.isApnMetered(2)) {
            return 2;
        }
        return 0;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        int i = this.mSubId;
        if (i != -1) {
            this.mMobileDataContentObserver.register(this.mContext, i);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mSubId != -1) {
            this.mMobileDataContentObserver.unRegister(this.mContext);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
    }

    public void init(int i) {
        this.mSubId = i;
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return this.mTelephonyManager.setAlwaysAllowMmsData(z);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mTelephonyManager.isDataEnabledForApn(2);
    }

    /* access modifiers changed from: private */
    /* renamed from: refreshPreference */
    public void lambda$new$0() {
        PreferenceScreen preferenceScreen = this.mScreen;
        if (preferenceScreen != null) {
            super.displayPreference(preferenceScreen);
        }
    }
}
