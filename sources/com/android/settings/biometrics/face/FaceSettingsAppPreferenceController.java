package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.face.FaceManager;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.Utils;
import com.android.settings.slices.SliceBackgroundWorker;

public class FaceSettingsAppPreferenceController extends FaceSettingsPreferenceController {
    private static final int DEFAULT = 1;
    static final String KEY = "security_settings_face_app";
    private static final int OFF = 0;
    private static final int ON = 1;
    private FaceManager mFaceManager;

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.biometrics.face.FaceSettingsPreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public FaceSettingsAppPreferenceController(Context context, String str) {
        super(context, str);
        this.mFaceManager = Utils.getFaceManagerOrNull(context);
    }

    public FaceSettingsAppPreferenceController(Context context) {
        this(context, KEY);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        if (FaceSettings.isFaceHardwareDetected(this.mContext) && Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "face_unlock_app_enabled", 1, getUserId()) == 1) {
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "face_unlock_app_enabled", z ? 1 : 0, getUserId());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.TogglePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (!FaceSettings.isFaceHardwareDetected(this.mContext)) {
            preference.setEnabled(false);
        } else if (!this.mFaceManager.hasEnrolledTemplates(getUserId())) {
            preference.setEnabled(false);
        } else {
            preference.setEnabled(true);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        FaceManager faceManager = this.mFaceManager;
        if (faceManager == null) {
            return 1;
        }
        boolean hasEnrolledTemplates = faceManager.hasEnrolledTemplates(getUserId());
        boolean z = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "face_unlock_app_enabled", 0, getUserId()) == 1;
        if (!hasEnrolledTemplates || z) {
            return 1;
        }
        return 0;
    }
}
