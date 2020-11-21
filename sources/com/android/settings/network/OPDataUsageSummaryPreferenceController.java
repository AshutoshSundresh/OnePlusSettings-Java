package com.android.settings.network;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.oneplus.settings.utils.ProductUtils;

public class OPDataUsageSummaryPreferenceController extends BasePreferenceController implements LifecycleObserver {
    private static final String KEY_DATA_USAGE_SUMMARY = "data_usage_summary";
    private static final String PCO_DEFAULT_VALUE = "-1";
    private static final String PCO_POST_PAID_CARD = "2";
    private static final String PCO_PRE_PAID_CARD = "3";
    private static final String PROPERTY_PCO_STATE = "persist.radio.pco.state";
    private int mDdsPhoneId = -1;
    private String mPCOvalue = PCO_DEFAULT_VALUE;
    private Preference mPreference;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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
        return KEY_DATA_USAGE_SUMMARY;
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

    public OPDataUsageSummaryPreferenceController(Context context) {
        super(context, KEY_DATA_USAGE_SUMMARY);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        if (ProductUtils.isUsvMode()) {
            disableBasedOnPCO();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_DATA_USAGE_SUMMARY.equals(preference.getKey())) {
            return false;
        }
        try {
            this.mContext.startActivity(new Intent("com.oneplus.security.action.USAGE_DATA_SUMMARY"));
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return true;
        }
    }

    private void disableBasedOnPCO() {
        int slotIndex = SubscriptionManager.getSlotIndex(SubscriptionManager.getDefaultDataSubscriptionId());
        this.mDdsPhoneId = slotIndex;
        String telephonyProperty = TelephonyManager.getTelephonyProperty(slotIndex, PROPERTY_PCO_STATE, PCO_DEFAULT_VALUE);
        this.mPCOvalue = telephonyProperty;
        if (TextUtils.equals(telephonyProperty, PCO_POST_PAID_CARD) || TextUtils.equals(this.mPCOvalue, "3")) {
            this.mPreference.setEnabled(false);
        }
    }
}
