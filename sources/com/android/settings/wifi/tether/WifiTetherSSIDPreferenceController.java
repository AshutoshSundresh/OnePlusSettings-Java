package com.android.settings.wifi.tether;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SoftApConfiguration;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.OPEditTextPreferenceForWifiTetherName;
import com.android.settings.wifi.dpp.WifiDppUtils;
import com.android.settings.wifi.tether.WifiTetherBasePreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.oneplus.settings.utils.OPUtils;

public class WifiTetherSSIDPreferenceController extends WifiTetherBasePreferenceController implements OPEditTextPreferenceForWifiTetherName.Validator {
    static final String DEFAULT_SSID = "AndroidAP";
    private static final String PREF_KEY = "wifi_tether_network_name";
    private static final String TAG = "WifiTetherSsidPref";
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private String mSSID;
    private WifiDeviceNameTextValidator mWifiDeviceNameTextValidator = new WifiDeviceNameTextValidator();

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

    WifiTetherSSIDPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener, MetricsFeatureProvider metricsFeatureProvider) {
        super(context, onTetherConfigUpdateListener, PREF_KEY);
        this.mMetricsFeatureProvider = metricsFeatureProvider;
    }

    public WifiTetherSSIDPreferenceController(Context context, WifiTetherBasePreferenceController.OnTetherConfigUpdateListener onTetherConfigUpdateListener) {
        super(context, onTetherConfigUpdateListener, PREF_KEY);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one") ? "wifi_tether_network_name_2" : PREF_KEY;
    }

    @Override // com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public void updateDisplay() {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        if (softApConfiguration != null) {
            this.mSSID = softApConfiguration.getSsid();
        } else {
            this.mSSID = DEFAULT_SSID;
        }
        ((OPEditTextPreferenceForWifiTetherName) this.mPreference).setValidator(this);
        if (!this.mWifiManager.isWifiApEnabled() || softApConfiguration == null) {
            ((WifiTetherSsidPreference) this.mPreference).setButtonVisible(false);
        } else {
            Intent hotspotConfiguratorIntentOrNull = WifiDppUtils.getHotspotConfiguratorIntentOrNull(this.mContext, this.mWifiManager, softApConfiguration);
            if (hotspotConfiguratorIntentOrNull == null) {
                Log.e(TAG, "Invalid security to share hotspot");
                ((WifiTetherSsidPreference) this.mPreference).setButtonVisible(false);
            } else {
                ((WifiTetherSsidPreference) this.mPreference).setButtonOnClickListener(new View.OnClickListener(hotspotConfiguratorIntentOrNull) {
                    /* class com.android.settings.wifi.tether.$$Lambda$WifiTetherSSIDPreferenceController$9y6x9r5FowGt1BqA5hm3Y_mqPlQ */
                    public final /* synthetic */ Intent f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        WifiTetherSSIDPreferenceController.this.lambda$updateDisplay$0$WifiTetherSSIDPreferenceController(this.f$1, view);
                    }
                });
                ((WifiTetherSsidPreference) this.mPreference).setButtonVisible(true);
            }
        }
        updateSsidDisplay((EditTextPreference) this.mPreference);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateDisplay$0 */
    public /* synthetic */ void lambda$updateDisplay$0$WifiTetherSSIDPreferenceController(Intent intent, View view) {
        shareHotspotNetwork(intent);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener, com.android.settings.wifi.tether.WifiTetherBasePreferenceController
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String str = (String) obj;
        if (!TextUtils.equals(this.mSSID, str)) {
            this.mMetricsFeatureProvider.action(this.mContext, 1736, new Pair[0]);
        }
        this.mSSID = str;
        updateSsidDisplay((EditTextPreference) preference);
        this.mListener.onTetherConfigUpdated(this);
        return true;
    }

    @Override // com.android.settings.widget.OPEditTextPreferenceForWifiTetherName.Validator
    public boolean isTextValid(String str) {
        return this.mWifiDeviceNameTextValidator.isTextValid(str);
    }

    public String getSSID() {
        return this.mSSID;
    }

    private void updateSsidDisplay(EditTextPreference editTextPreference) {
        editTextPreference.setText(this.mSSID);
        if (OPUtils.isEF009Project()) {
            boolean isContainSymbol = OPUtils.isContainSymbol(this.mSSID);
            String str = this.mSSID;
            Spanned spanned = str;
            if (isContainSymbol) {
                spanned = OPUtils.getSymbolDeviceName(str);
            }
            editTextPreference.setSummary(spanned);
            return;
        }
        editTextPreference.setSummary(this.mSSID);
    }

    private void shareHotspotNetwork(Intent intent) {
        WifiDppUtils.showLockScreen(this.mContext, new Runnable(intent) {
            /* class com.android.settings.wifi.tether.$$Lambda$WifiTetherSSIDPreferenceController$Uuc4492JmFKnNdaFNJky9fSywuI */
            public final /* synthetic */ Intent f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WifiTetherSSIDPreferenceController.this.lambda$shareHotspotNetwork$1$WifiTetherSSIDPreferenceController(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$shareHotspotNetwork$1 */
    public /* synthetic */ void lambda$shareHotspotNetwork$1$WifiTetherSSIDPreferenceController(Intent intent) {
        this.mMetricsFeatureProvider.action(0, 1712, 1595, null, Integer.MIN_VALUE);
        this.mContext.startActivity(intent);
    }

    /* access modifiers changed from: package-private */
    public boolean isQrCodeButtonAvailable() {
        return ((WifiTetherSsidPreference) this.mPreference).isQrCodeButtonAvailable();
    }
}
