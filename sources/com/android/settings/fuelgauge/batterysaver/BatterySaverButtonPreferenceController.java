package com.android.settings.fuelgauge.batterysaver;

import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.PowerManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.fuelgauge.BatterySaverReceiver;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
import com.oneplus.settings.utils.OPUtils;

public class BatterySaverButtonPreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop, BatterySaverReceiver.BatterySaverListener {
    private final BatterySaverReceiver mBatterySaverReceiver;
    private Context mContext;
    private final PowerManager mPowerManager;
    private SwitchPreference mPreference;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public BatterySaverButtonPreferenceController(Context context, String str) {
        super(context, str);
        this.mContext = context;
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        BatterySaverReceiver batterySaverReceiver = new BatterySaverReceiver(context);
        this.mBatterySaverReceiver = batterySaverReceiver;
        batterySaverReceiver.setBatterySaverListener(this);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public Uri getSliceUri() {
        return new Uri.Builder().scheme("content").authority("android.settings.slices").appendPath("action").appendPath("battery_saver").build();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mBatterySaverReceiver.setListening(true);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mBatterySaverReceiver.setListening(false);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mPowerManager.isPowerSaveMode();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        OPUtils.startVibratePattern(this.mContext);
        return BatterySaverUtils.setPowerSaveMode(this.mContext, z, false);
    }

    @Override // com.android.settings.fuelgauge.BatterySaverReceiver.BatterySaverListener
    public void onPowerSaveModeChanged() {
        boolean isChecked = isChecked();
        SwitchPreference switchPreference = this.mPreference;
        if (switchPreference != null && switchPreference.isChecked() != isChecked) {
            this.mPreference.setChecked(isChecked);
        }
    }

    @Override // com.android.settings.fuelgauge.BatterySaverReceiver.BatterySaverListener
    public void onBatteryChanged(boolean z) {
        SwitchPreference switchPreference = this.mPreference;
        if (switchPreference != null) {
            switchPreference.setEnabled(!z);
        }
    }
}
