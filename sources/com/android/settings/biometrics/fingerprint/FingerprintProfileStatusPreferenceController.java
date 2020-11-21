package com.android.settings.biometrics.fingerprint;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.slices.SliceBackgroundWorker;

public class FingerprintProfileStatusPreferenceController extends FingerprintStatusPreferenceController {
    public static final String KEY_FINGERPRINT_SETTINGS = "fingerprint_settings_profile";

    @Override // com.android.settings.biometrics.fingerprint.FingerprintStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintStatusPreferenceController, com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public FingerprintProfileStatusPreferenceController(Context context) {
        super(context, KEY_FINGERPRINT_SETTINGS);
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
}
