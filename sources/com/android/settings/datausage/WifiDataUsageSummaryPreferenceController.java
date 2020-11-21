package com.android.settings.datausage;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.NetworkTemplate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.net.DataUsageController;

public class WifiDataUsageSummaryPreferenceController extends DataUsageSummaryPreferenceController {
    final String mNetworkId;

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.datausage.DataUsageSummaryPreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public WifiDataUsageSummaryPreferenceController(Activity activity, Lifecycle lifecycle, PreferenceFragmentCompat preferenceFragmentCompat, CharSequence charSequence) {
        super(activity, lifecycle, preferenceFragmentCompat, -1);
        if (charSequence == null) {
            this.mNetworkId = null;
        } else {
            this.mNetworkId = String.valueOf(charSequence);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.datausage.DataUsageSummaryPreferenceController
    public void updateState(Preference preference) {
        if (preference != null) {
            DataUsageSummaryPreference dataUsageSummaryPreference = (DataUsageSummaryPreference) preference;
            NetworkTemplate buildTemplateWifi = NetworkTemplate.buildTemplateWifi(this.mNetworkId);
            DataUsageController.DataUsageInfo dataUsageInfo = this.mDataUsageController.getDataUsageInfo(buildTemplateWifi);
            this.mDataInfoController.updateDataLimit(dataUsageInfo, this.mPolicyEditor.getPolicy(buildTemplateWifi));
            dataUsageSummaryPreference.setWifiMode(true, dataUsageInfo.period, true);
            dataUsageSummaryPreference.setChartEnabled(true);
            long j = dataUsageInfo.usageLevel;
            dataUsageSummaryPreference.setUsageNumbers(j, j, false);
            dataUsageSummaryPreference.setProgress(100.0f);
            dataUsageSummaryPreference.setLabels(DataUsageUtils.formatDataUsage(this.mContext, 0), DataUsageUtils.formatDataUsage(this.mContext, dataUsageInfo.usageLevel));
        }
    }
}
