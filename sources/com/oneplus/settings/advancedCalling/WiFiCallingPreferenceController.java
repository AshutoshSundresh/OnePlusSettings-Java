package com.oneplus.settings.advancedCalling;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SubscriptionManager;
import androidx.preference.Preference;
import com.android.ims.ImsException;
import com.android.ims.ImsManager;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;

public class WiFiCallingPreferenceController extends BasePreferenceController {
    private static final String KEY_ACTIVATE_WIFI_CALLING = "vzw_wifi_calling";
    private static final String PACKAGE_NAME_WIFI_CALLING = "com.oneplus.vzw.emergencyaddress";
    private ImsManager mImsManager;
    private SubscriptionManager mSubscriptionManager;

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

    public WiFiCallingPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!ProductUtils.isUsvMode() || !getVzWiFiCallingStatus()) ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mContext.getString(C0017R$string.advanced_wificall_summary);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!KEY_ACTIVATE_WIFI_CALLING.equals(preference.getKey())) {
            return false;
        }
        try {
            if (!OPUtils.isAppExist(this.mContext, PACKAGE_NAME_WIFI_CALLING)) {
                return true;
            }
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(PACKAGE_NAME_WIFI_CALLING, "com.oneplus.vzw.emergencyaddress.WiFiCallingUI"));
            this.mContext.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return true;
        }
    }

    private boolean getVzWiFiCallingStatus() {
        try {
            SubscriptionManager from = SubscriptionManager.from(this.mContext);
            this.mSubscriptionManager = from;
            ImsManager instance = ImsManager.getInstance(this.mContext, from.getDefaultDataPhoneId());
            this.mImsManager = instance;
            if (instance.getConfigInterface().getProvisionedValue(28) == 1) {
                return true;
            }
            return false;
        } catch (ImsException e) {
            e.printStackTrace();
            return false;
        }
    }
}
