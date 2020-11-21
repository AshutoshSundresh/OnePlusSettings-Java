package com.oneplus.settings.chargingstations;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.MasterSwitchController;
import com.android.settings.widget.MasterSwitchPreference;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.oneplus.settings.utils.OPUtils;

public class OPChargingStationPrefController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause, SwitchWidgetController.OnSwitchChangeListener {
    private Context mContext;
    private MasterSwitchController mSwitchController;
    private MasterSwitchPreference mSwitchPreference;

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

    public OPChargingStationPrefController(Context context, String str) {
        super(context, str);
        this.mContext = context;
    }

    public void setLifeCycle(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitchPreference = (MasterSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return OPUtils.isAppExist(this.mContext, "com.oneplus.chargingpilar") ? 0 : 3;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        MasterSwitchPreference masterSwitchPreference = this.mSwitchPreference;
        if (masterSwitchPreference != null) {
            MasterSwitchController masterSwitchController = new MasterSwitchController(masterSwitchPreference);
            this.mSwitchController = masterSwitchController;
            masterSwitchController.setListener(this);
            this.mSwitchController.startListening();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        MasterSwitchController masterSwitchController = this.mSwitchController;
        if (masterSwitchController != null) {
            masterSwitchController.stopListening();
        }
    }

    @Override // com.android.settings.widget.SwitchWidgetController.OnSwitchChangeListener
    public boolean onSwitchToggled(boolean z) {
        OPChargingStationUtils.putIntSystemProperty(this.mContext, "op_charging_stations_feature_on", z ? 1 : 0);
        OPUtils.sendAnalytics("C22AG9UUDL", "settings_action", "settings_feature_enabled", z ? "on" : "off");
        OPChargingStationUtils.sendBroadcastToApp(this.mContext, z ? "type_enabled" : "type_undo");
        if (z) {
            return true;
        }
        OPChargingStationUtils.putStringSystemProperty(this.mContext, "op_charging_station_beacon_name", "");
        return true;
    }
}
