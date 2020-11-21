package com.android.settings.network.telephony.cdma;

import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.slices.SliceBackgroundWorker;

public class CdmaSubscriptionPreferenceController extends CdmaBasePreferenceController implements Preference.OnPreferenceChangeListener {
    private static final String TYPE_NV = "NV";
    private static final String TYPE_RUIM = "RUIM";
    ListPreference mPreference;

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public CdmaSubscriptionPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.network.telephony.TelephonyAvailabilityCallback, com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController
    public int getAvailabilityStatus(int i) {
        return (!MobileNetworkUtils.isCdmaOptions(this.mContext, i) || !deviceSupportsNvAndRuim()) ? 2 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ListPreference listPreference = (ListPreference) preference;
        listPreference.setVisible(getAvailabilityStatus() == 0);
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "subscription_mode", 0);
        if (i != -1) {
            listPreference.setValue(Integer.toString(i));
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        if (!this.mTelephonyManager.setCdmaSubscriptionMode(parseInt)) {
            return false;
        }
        Settings.Global.putInt(this.mContext.getContentResolver(), "subscription_mode", parseInt);
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean deviceSupportsNvAndRuim() {
        boolean z;
        boolean z2;
        String str = SystemProperties.get("ril.subscription.types");
        if (!TextUtils.isEmpty(str)) {
            z2 = false;
            z = false;
            for (String str2 : str.split(",")) {
                String trim = str2.trim();
                if (trim.equalsIgnoreCase(TYPE_NV)) {
                    z2 = true;
                } else if (trim.equalsIgnoreCase(TYPE_RUIM)) {
                    z = true;
                }
            }
        } else {
            z2 = false;
            z = false;
        }
        return z2 && z;
    }
}
