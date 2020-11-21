package com.android.settings.inputmethod;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.hardware.input.KeyboardLayout;
import android.view.InputDevice;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KeyboardLayoutPickerController extends BasePreferenceController implements InputManager.InputDeviceListener, LifecycleObserver, OnStart, OnStop {
    private final InputManager mIm;
    private int mInputDeviceId = -1;
    private InputDeviceIdentifier mInputDeviceIdentifier;
    private KeyboardLayout[] mKeyboardLayouts;
    private Fragment mParent;
    private final Map<SwitchPreference, KeyboardLayout> mPreferenceMap = new HashMap();
    private PreferenceScreen mScreen;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public void onInputDeviceAdded(int i) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public KeyboardLayoutPickerController(Context context, String str) {
        super(context, str);
        this.mIm = (InputManager) context.getSystemService("input");
    }

    public void initialize(Fragment fragment, InputDeviceIdentifier inputDeviceIdentifier) {
        this.mParent = fragment;
        this.mInputDeviceIdentifier = inputDeviceIdentifier;
        KeyboardLayout[] keyboardLayoutsForInputDevice = this.mIm.getKeyboardLayoutsForInputDevice(inputDeviceIdentifier);
        this.mKeyboardLayouts = keyboardLayoutsForInputDevice;
        Arrays.sort(keyboardLayoutsForInputDevice);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mIm.registerInputDeviceListener(this, null);
        InputDevice inputDeviceByDescriptor = this.mIm.getInputDeviceByDescriptor(this.mInputDeviceIdentifier.getDescriptor());
        if (inputDeviceByDescriptor == null) {
            this.mParent.getActivity().finish();
            return;
        }
        this.mInputDeviceId = inputDeviceByDescriptor.getId();
        updateCheckedState();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mIm.unregisterInputDeviceListener(this);
        this.mInputDeviceId = -1;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
        createPreferenceHierarchy();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!(preference instanceof SwitchPreference)) {
            return false;
        }
        SwitchPreference switchPreference = (SwitchPreference) preference;
        KeyboardLayout keyboardLayout = this.mPreferenceMap.get(switchPreference);
        if (keyboardLayout == null) {
            return true;
        }
        if (switchPreference.isChecked()) {
            this.mIm.addKeyboardLayoutForInputDevice(this.mInputDeviceIdentifier, keyboardLayout.getDescriptor());
            return true;
        }
        this.mIm.removeKeyboardLayoutForInputDevice(this.mInputDeviceIdentifier, keyboardLayout.getDescriptor());
        return true;
    }

    public void onInputDeviceRemoved(int i) {
        int i2 = this.mInputDeviceId;
        if (i2 >= 0 && i == i2) {
            this.mParent.getActivity().finish();
        }
    }

    public void onInputDeviceChanged(int i) {
        int i2 = this.mInputDeviceId;
        if (i2 >= 0 && i == i2) {
            updateCheckedState();
        }
    }

    private void updateCheckedState() {
        String[] enabledKeyboardLayoutsForInputDevice = this.mIm.getEnabledKeyboardLayoutsForInputDevice(this.mInputDeviceIdentifier);
        Arrays.sort(enabledKeyboardLayoutsForInputDevice);
        for (Map.Entry<SwitchPreference, KeyboardLayout> entry : this.mPreferenceMap.entrySet()) {
            entry.getKey().setChecked(Arrays.binarySearch(enabledKeyboardLayoutsForInputDevice, entry.getValue().getDescriptor()) >= 0);
        }
    }

    private void createPreferenceHierarchy() {
        KeyboardLayout[] keyboardLayoutArr = this.mKeyboardLayouts;
        for (KeyboardLayout keyboardLayout : keyboardLayoutArr) {
            SwitchPreference switchPreference = new SwitchPreference(this.mScreen.getContext());
            switchPreference.setTitle(keyboardLayout.getLabel());
            switchPreference.setSummary(keyboardLayout.getCollection());
            switchPreference.setKey(keyboardLayout.getDescriptor());
            this.mScreen.addPreference(switchPreference);
            this.mPreferenceMap.put(switchPreference, keyboardLayout);
        }
    }
}
