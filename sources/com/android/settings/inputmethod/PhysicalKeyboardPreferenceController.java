package com.android.settings.inputmethod;

import android.content.Context;
import android.hardware.input.InputManager;
import android.icu.text.ListFormatter;
import androidx.preference.Preference;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.inputmethod.PhysicalKeyboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.util.ArrayList;
import java.util.List;

public class PhysicalKeyboardPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnResume, OnPause, InputManager.InputDeviceListener {
    private final InputManager mIm;
    private Preference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "physical_keyboard_pref";
    }

    public PhysicalKeyboardPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mIm = (InputManager) context.getSystemService("input");
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_physical_keyboard_pref);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mPreference = preference;
        updateSummary();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mIm.unregisterInputDeviceListener(this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mIm.registerInputDeviceListener(this, null);
    }

    public void onInputDeviceAdded(int i) {
        updateSummary();
    }

    public void onInputDeviceRemoved(int i) {
        updateSummary();
    }

    public void onInputDeviceChanged(int i) {
        updateSummary();
    }

    private void updateSummary() {
        if (this.mPreference != null) {
            List<PhysicalKeyboardFragment.HardKeyboardDeviceInfo> hardKeyboards = PhysicalKeyboardFragment.getHardKeyboards(this.mContext);
            if (hardKeyboards.isEmpty()) {
                this.mPreference.setSummary(C0017R$string.keyboard_disconnected);
                return;
            }
            ArrayList arrayList = new ArrayList();
            for (PhysicalKeyboardFragment.HardKeyboardDeviceInfo hardKeyboardDeviceInfo : hardKeyboards) {
                arrayList.add(hardKeyboardDeviceInfo.mDeviceName);
            }
            this.mPreference.setSummary(ListFormatter.getInstance().format(arrayList));
        }
    }
}
