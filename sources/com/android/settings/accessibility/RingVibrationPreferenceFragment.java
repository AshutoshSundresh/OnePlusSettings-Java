package com.android.settings.accessibility;

import android.os.Vibrator;
import com.android.settings.C0019R$xml;

public class RingVibrationPreferenceFragment extends VibrationPreferenceFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1620;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    public int getPreviewVibrationAudioAttributesUsage() {
        return 6;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    public String getVibrationIntensitySetting() {
        return "ring_vibration_intensity";
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.accessibility_ring_vibration_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    public String getVibrationEnabledSetting() {
        return AccessibilitySettings.isRampingRingerEnabled(getContext()) ? "apply_ramping_ringer" : "vibrate_when_ringing";
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    public int getDefaultVibrationIntensity() {
        return ((Vibrator) getContext().getSystemService(Vibrator.class)).getDefaultRingVibrationIntensity();
    }
}
