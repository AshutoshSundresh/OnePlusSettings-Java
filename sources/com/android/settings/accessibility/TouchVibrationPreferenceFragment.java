package com.android.settings.accessibility;

import android.os.Vibrator;
import com.android.settings.C0019R$xml;

public class TouchVibrationPreferenceFragment extends VibrationPreferenceFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1294;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    public int getPreviewVibrationAudioAttributesUsage() {
        return 13;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    public String getVibrationEnabledSetting() {
        return "haptic_feedback_enabled";
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    public String getVibrationIntensitySetting() {
        return "haptic_feedback_intensity";
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.accessibility_touch_vibration_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    public int getDefaultVibrationIntensity() {
        return ((Vibrator) getContext().getSystemService(Vibrator.class)).getDefaultHapticFeedbackIntensity();
    }
}
