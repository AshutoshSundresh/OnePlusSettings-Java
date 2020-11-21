package com.android.settings.wifi.tether;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;

public class WifiTetherAutoOffPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    private static final int CUSTOM_AUTO_TURN_OFF_TIME = 300000;
    private final WifiManager mWifiManager;

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

    public WifiTetherAutoOffPreferenceController(Context context, String str) {
        super(context, str);
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (OPUtils.isSupportUstMode() || ProductUtils.isUsvMode()) ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        SoftApConfiguration softApConfiguration = this.mWifiManager.getSoftApConfiguration();
        boolean isAutoShutdownEnabled = softApConfiguration.isAutoShutdownEnabled();
        if (isAutoShutdownEnabled && softApConfiguration.getShutdownTimeoutMillis() != 300000) {
            setNewSoftApConfiguration(softApConfiguration, true);
        }
        ((SwitchPreference) preference).setChecked(isAutoShutdownEnabled);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return setNewSoftApConfiguration(((Boolean) obj).booleanValue());
    }

    private boolean setNewSoftApConfiguration(boolean z) {
        return setNewSoftApConfiguration(this.mWifiManager.getSoftApConfiguration(), z);
    }

    private boolean setNewSoftApConfiguration(SoftApConfiguration softApConfiguration, boolean z) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "soft_ap_timeout_enabled", z ? 1 : 0);
        if (softApConfiguration == null) {
            return false;
        }
        return this.mWifiManager.setSoftApConfiguration(new SoftApConfiguration.Builder(softApConfiguration).setShutdownTimeoutMillis(300000).setAutoShutdownEnabled(z).build());
    }
}
