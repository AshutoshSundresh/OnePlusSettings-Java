package com.android.settings.biometrics.face;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.slices.SliceBackgroundWorker;

public class FaceProfileStatusPreferenceController extends FaceStatusPreferenceController {
    private static final String KEY_FACE_SETTINGS = "face_settings_profile";

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.biometrics.face.FaceStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public FaceProfileStatusPreferenceController(Context context) {
        super(context, KEY_FACE_SETTINGS);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController
    public int getAvailabilityStatus() {
        int availabilityStatus = super.getAvailabilityStatus();
        if (availabilityStatus != 0) {
            return availabilityStatus;
        }
        return 1;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    public boolean isUserSupported() {
        int i = this.mProfileChallengeUserId;
        return i != -10000 && this.mLockPatternUtils.isSeparateProfileChallengeAllowed(i);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    public int getUserId() {
        return this.mProfileChallengeUserId;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setTitle(this.mContext.getResources().getString(C0017R$string.security_settings_face_profile_preference_title));
    }
}
