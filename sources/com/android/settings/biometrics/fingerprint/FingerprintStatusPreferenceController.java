package com.android.settings.biometrics.fingerprint;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.fingerprint.FingerprintManager;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricStatusPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class FingerprintStatusPreferenceController extends BiometricStatusPreferenceController {
    private static final String KEY_FINGERPRINT_SETTINGS = "fingerprint_settings";
    protected final FingerprintManager mFingerprintManager;

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public FingerprintStatusPreferenceController(Context context) {
        this(context, KEY_FINGERPRINT_SETTINGS);
    }

    public FingerprintStatusPreferenceController(Context context, String str) {
        super(context, str);
        this.mFingerprintManager = Utils.getFingerprintManagerOrNull(context);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    public boolean isDeviceSupported() {
        FingerprintManager fingerprintManager = this.mFingerprintManager;
        return fingerprintManager != null && fingerprintManager.isHardwareDetected();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    public boolean hasEnrolledBiometrics() {
        return this.mFingerprintManager.hasEnrolledFingerprints(getUserId());
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    public String getSummaryTextEnrolled() {
        int size = this.mFingerprintManager.getEnrolledFingerprints(getUserId()).size();
        return this.mContext.getResources().getQuantityString(C0015R$plurals.security_settings_fingerprint_preference_summary, size, Integer.valueOf(size));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    public String getSummaryTextNoneEnrolled() {
        return this.mContext.getString(C0017R$string.security_settings_fingerprint_preference_summary_none);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    public String getSettingsClassName() {
        return FingerprintSettings.class.getName();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricStatusPreferenceController
    public String getEnrollClassName() {
        return FingerprintEnrollIntroduction.class.getName();
    }
}
