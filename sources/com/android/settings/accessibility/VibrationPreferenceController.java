package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Vibrator;
import android.provider.Settings;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class VibrationPreferenceController extends BasePreferenceController {
    private final Vibrator mVibrator = ((Vibrator) this.mContext.getSystemService(Vibrator.class));

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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public VibrationPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "ring_vibration_intensity", this.mVibrator.getDefaultRingVibrationIntensity());
        if (Settings.System.getInt(this.mContext.getContentResolver(), "vibrate_when_ringing", 0) == 0 && !AccessibilitySettings.isRampingRingerEnabled(this.mContext)) {
            i = 0;
        }
        CharSequence intensityString = VibrationIntensityPreferenceController.getIntensityString(this.mContext, i);
        int i2 = Settings.System.getInt(this.mContext.getContentResolver(), "notification_vibration_intensity", this.mVibrator.getDefaultNotificationVibrationIntensity());
        CharSequence intensityString2 = VibrationIntensityPreferenceController.getIntensityString(this.mContext, i2);
        int i3 = Settings.System.getInt(this.mContext.getContentResolver(), "haptic_feedback_intensity", this.mVibrator.getDefaultHapticFeedbackIntensity());
        if (Settings.System.getInt(this.mContext.getContentResolver(), "haptic_feedback_enabled", 0) == 0) {
            i3 = 0;
        }
        CharSequence intensityString3 = VibrationIntensityPreferenceController.getIntensityString(this.mContext, i3);
        if (i == i3 && i == i2) {
            return intensityString;
        }
        return this.mContext.getString(C0017R$string.accessibility_vibration_summary, intensityString, intensityString2, intensityString3);
    }
}
