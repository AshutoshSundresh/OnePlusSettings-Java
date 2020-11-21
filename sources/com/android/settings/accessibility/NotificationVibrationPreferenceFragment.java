package com.android.settings.accessibility;

import android.os.Vibrator;
import com.android.settings.C0019R$xml;

public class NotificationVibrationPreferenceFragment extends VibrationPreferenceFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1293;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    public int getPreviewVibrationAudioAttributesUsage() {
        return 5;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    public String getVibrationEnabledSetting() {
        return "";
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    public String getVibrationIntensitySetting() {
        return "notification_vibration_intensity";
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.widget.RadioButtonPickerFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.accessibility_notification_vibration_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    public int getDefaultVibrationIntensity() {
        return ((Vibrator) getContext().getSystemService(Vibrator.class)).getDefaultNotificationVibrationIntensity();
    }
}
