package com.android.settings.inputmethod;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.input.InputManager;
import android.provider.Settings;
import android.view.InputDevice;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0005R$bool;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class GameControllerPreferenceController extends TogglePreferenceController implements PreferenceControllerMixin, InputManager.InputDeviceListener, LifecycleObserver, OnResume, OnPause {
    private final InputManager mIm;
    private Preference mPreference;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
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
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public GameControllerPreferenceController(Context context, String str) {
        super(context, str);
        this.mIm = (InputManager) context.getSystemService("input");
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mIm.registerInputDeviceListener(this, null);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mIm.unregisterInputDeviceListener(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!this.mContext.getResources().getBoolean(C0005R$bool.config_show_vibrate_input_devices)) {
            return 3;
        }
        for (int i : this.mIm.getInputDeviceIds()) {
            InputDevice inputDevice = this.mIm.getInputDevice(i);
            if (!(inputDevice == null || inputDevice.isVirtual() || !inputDevice.getVibrator().hasVibrator())) {
                return 0;
            }
        }
        return 2;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.TogglePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference != null) {
            this.mPreference.setVisible(isAvailable());
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        if (Settings.System.getInt(this.mContext.getContentResolver(), "vibrate_input_devices", 1) > 0) {
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return Settings.System.putInt(this.mContext.getContentResolver(), "vibrate_input_devices", z ? 1 : 0);
    }

    public void onInputDeviceAdded(int i) {
        updateState(this.mPreference);
    }

    public void onInputDeviceRemoved(int i) {
        updateState(this.mPreference);
    }

    public void onInputDeviceChanged(int i) {
        updateState(this.mPreference);
    }
}
