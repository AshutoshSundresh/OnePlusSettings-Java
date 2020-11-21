package com.android.settings.display;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.ColorDisplayManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.SliderPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.SeekBarPreference;

public class NightDisplayIntensityPreferenceController extends SliderPreferenceController {
    private ColorDisplayManager mColorDisplayManager;

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMin() {
        return 0;
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public NightDisplayIntensityPreferenceController(Context context, String str) {
        super(context, str);
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!ColorDisplayManager.isNightDisplayAvailable(this.mContext)) {
            return 3;
        }
        return !this.mColorDisplayManager.isNightDisplayActivated() ? 5 : 0;
    }

    @Override // com.android.settings.core.SliderPreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "night_display_temperature");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SeekBarPreference seekBarPreference = (SeekBarPreference) preferenceScreen.findPreference(getPreferenceKey());
        seekBarPreference.setContinuousUpdates(true);
        seekBarPreference.setMax(getMax());
        seekBarPreference.setMin(getMin());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.SliderPreferenceController
    public final void updateState(Preference preference) {
        super.updateState(preference);
        preference.setEnabled(this.mColorDisplayManager.isNightDisplayActivated());
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getSliderPosition() {
        return convertTemperature(this.mColorDisplayManager.getNightDisplayColorTemperature());
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public boolean setSliderPosition(int i) {
        return this.mColorDisplayManager.setNightDisplayColorTemperature(convertTemperature(i));
    }

    @Override // com.android.settings.core.SliderPreferenceController
    public int getMax() {
        return convertTemperature(ColorDisplayManager.getMinimumColorTemperature(this.mContext));
    }

    private int convertTemperature(int i) {
        return ColorDisplayManager.getMaximumColorTemperature(this.mContext) - i;
    }
}
