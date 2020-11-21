package com.oneplus.settings.controllers;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SubscriptionManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.ims.ImsManager;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.oneplus.settings.utils.OPUtils;

public class OPWiFiCallingPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private static final String KEY_WIFI_CALLING = "wifi_calling";
    private ImsManager mImsMgr;
    private Preference mPreference;
    private UstWfcStatusTracker mUstWfcStatusTracker;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return KEY_WIFI_CALLING;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OPWiFiCallingPreferenceController(Context context) {
        super(context, KEY_WIFI_CALLING);
        if (OPUtils.isSupportUstMode()) {
            this.mImsMgr = ImsManager.getInstance(context, SubscriptionManager.getDefaultVoicePhoneId());
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!OPUtils.isSupportUstMode()) {
            return 4;
        }
        ImsManager imsManager = this.mImsMgr;
        return ((imsManager == null || imsManager.isWfcEnabledByPlatform()) && !OPUtils.isGuestMode()) ? 0 : 4;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (OPUtils.isSupportUstMode()) {
            UstWfcStatusTracker ustWfcStatusTracker = new UstWfcStatusTracker(this.mContext, this.mPreference);
            this.mUstWfcStatusTracker = ustWfcStatusTracker;
            if (ustWfcStatusTracker != null) {
                ustWfcStatusTracker.startObserve();
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        UstWfcStatusTracker ustWfcStatusTracker;
        if (OPUtils.isSupportUstMode() && (ustWfcStatusTracker = this.mUstWfcStatusTracker) != null) {
            ustWfcStatusTracker.stopObserve();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!OPUtils.isSupportUstMode() || !KEY_WIFI_CALLING.equals(preference.getKey())) {
            return false;
        }
        try {
            Intent intent = new Intent("com.android.wificalling.setting.action");
            intent.putExtra("slot", SubscriptionManager.getDefaultVoicePhoneId());
            this.mContext.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return true;
        }
    }
}
