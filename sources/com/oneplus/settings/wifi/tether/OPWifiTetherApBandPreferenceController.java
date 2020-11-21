package com.oneplus.settings.wifi.tether;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.icu.text.ListFormatter;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.WifiUtils;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.oneplus.settings.utils.ProductUtils;
import com.oneplus.settings.widget.OPHotspotApBandSelectionPreference;
import java.util.Arrays;

public class OPWifiTetherApBandPreferenceController extends WifiTetherBasePreferenceController {
    public static final String[] BAND_VALUES = {String.valueOf(1), String.valueOf(2)};
    private static final String PREF_KEY = "wifi_tether_network_ap_band_single_select";
    private static final String TAG = "OPWifiTetherApBandPref";
    private final int[] mBandEntries;
    private int mBandIndex;
    private final String[] mBandSummaries;
    private int mNewBandIndex;
    DialogInterface.OnClickListener onWarningDialogCLickListner = new DialogInterface.OnClickListener() {
        /* class com.oneplus.settings.wifi.tether.OPWifiTetherApBandPreferenceController.AnonymousClass1 */

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                OPWifiTetherApBandPreferenceController oPWifiTetherApBandPreferenceController = OPWifiTetherApBandPreferenceController.this;
                oPWifiTetherApBandPreferenceController.mBandIndex = oPWifiTetherApBandPreferenceController.mNewBandIndex;
                OPWifiTetherApBandPreferenceController.this.preference.setSummary(OPWifiTetherApBandPreferenceController.this.getConfigSummary());
                ((WifiTetherBasePreferenceController) OPWifiTetherApBandPreferenceController.this).mListener.onTetherConfigUpdated(OPWifiTetherApBandPreferenceController.this);
            } else if (i == -2) {
                OPWifiTetherApBandPreferenceController.this.preference.setSummary(OPWifiTetherApBandPreferenceController.this.getConfigSummary());
            }
        }
    };
    private OPHotspotApBandSelectionPreference preference;

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return PREF_KEY;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OPWifiTetherApBandPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener, PREF_KEY);
        Resources resources = this.mContext.getResources();
        this.mBandEntries = resources.getIntArray(C0003R$array.wifi_ap_band_config_full);
        this.mBandSummaries = resources.getStringArray(C0003R$array.wifi_ap_band_summary_full);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public int getAvailabilityStatus() {
        if (WifiUtils.isSupportDualBand()) {
            return 2;
        }
        return super.getAvailabilityStatus();
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        int i = this.mBandIndex;
        if (softApConfiguration == null) {
            this.mBandIndex = 0;
            Log.d(TAG, "Updating band index to 0 because no config");
        } else if (is5GhzBandSupported()) {
            int band = softApConfiguration.getBand();
            Log.d(TAG, "updateDisplay is5GhzBandSupported = true, band = " + band);
            if (band == 3) {
                this.mBandIndex = getBandIndex(2);
            } else {
                this.mBandIndex = getBandIndex(band);
            }
            Log.d(TAG, "Updating band index to " + this.mBandIndex);
        } else {
            SoftApConfiguration build = new SoftApConfiguration.Builder(softApConfiguration).setBand(1).build();
            this.mWifiManager.setSoftApConfiguration(build);
            Log.d(TAG, "updateDisplay else, band = " + build.getBand());
            this.mBandIndex = getBandIndex(build.getBand());
            Log.d(TAG, "5Ghz not supported, updating band index to " + this.mBandIndex);
        }
        this.preference = (OPHotspotApBandSelectionPreference) this.mPreference;
        if (this.mBandIndex >= this.mBandEntries.length) {
            this.mBandIndex = i;
        }
        if (this.preference == null) {
            return;
        }
        if (!is5GhzBandSupported()) {
            this.preference.setEnabled(false);
            this.preference.setSummary(C0017R$string.wifi_ap_choose_2G);
            return;
        }
        this.preference.setExistingConfigValue(this.mBandEntries[this.mBandIndex]);
        this.preference.setSummary(getConfigSummary());
    }

    private int getBandIndex(int i) {
        int binarySearch = Arrays.binarySearch(this.mBandEntries, i);
        if (binarySearch >= 0) {
            return binarySearch;
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public String getConfigSummary() {
        int[] iArr = this.mBandEntries;
        int i = this.mBandIndex;
        if (iArr[i] == 8) {
            return ListFormatter.getInstance().format(this.mBandSummaries);
        }
        return this.mBandSummaries[i];
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener, com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public boolean onPreferenceChange(Preference preference2, Object obj) {
        Intent intent;
        if (!this.mWifiManager.isWifiApEnabled() || this.mWifiManager.getSoftApConfiguration() == null) {
            intent = null;
        } else {
            Context context = this.mContext;
            WifiManager wifiManager = this.mWifiManager;
            intent = WifiDppUtils.getHotspotConfiguratorIntentOrNull(context, wifiManager, wifiManager.getSoftApConfiguration());
        }
        if (intent == null || !ProductUtils.isUsvMode()) {
            this.mBandIndex = getBandIndex(((Integer) obj).intValue());
            Log.d(TAG, "Band preference changed, updating band index to " + this.mBandIndex);
            preference2.setSummary(getConfigSummary());
            this.mListener.onTetherConfigUpdated(this);
            return true;
        }
        Log.v("Preference change", isAvailable() + "");
        this.mNewBandIndex = getBandIndex(((Integer) obj).intValue());
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(C0017R$string.save_changes);
        builder.setMessage(C0017R$string.verizon_wifi_tether_band_warning);
        builder.setPositiveButton(17039370, this.onWarningDialogCLickListner);
        builder.setNegativeButton(17039360, this.onWarningDialogCLickListner);
        builder.setCancelable(false);
        builder.create().show();
        return true;
    }

    private boolean is5GhzBandSupported() {
        return this.mWifiManager.is5GHzBandSupported() && this.mWifiManager.getCountryCode() != null;
    }

    public int getBandIndex() {
        return this.mBandIndex;
    }

    public int getBand() {
        int i = this.mBandIndex;
        int[] iArr = this.mBandEntries;
        if (i >= iArr.length || i < 0) {
            return 0;
        }
        return iArr[i];
    }
}
