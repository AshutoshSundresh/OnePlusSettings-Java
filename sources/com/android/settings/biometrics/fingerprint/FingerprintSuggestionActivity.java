package com.android.settings.biometrics.fingerprint;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;

public class FingerprintSuggestionActivity extends SetupFingerprintEnrollIntroduction {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, com.android.settings.biometrics.fingerprint.SetupFingerprintEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollBase
    public void initViews() {
        super.initViews();
        getCancelButton().setText(this, C0017R$string.security_settings_fingerprint_enroll_introduction_cancel);
    }

    public void finish() {
        setResult(0);
        super.finish();
    }

    public static boolean isSuggestionComplete(Context context) {
        return !Utils.hasFingerprintHardware(context) || !isFingerprintEnabled(context) || isNotSingleFingerprintEnrolled(context);
    }

    private static boolean isNotSingleFingerprintEnrolled(Context context) {
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(context);
        if (fingerprintManagerOrNull == null || fingerprintManagerOrNull.getEnrolledFingerprints().size() != 1) {
            return true;
        }
        return false;
    }

    static boolean isFingerprintEnabled(Context context) {
        return (((DevicePolicyManager) context.getSystemService("device_policy")).getKeyguardDisabledFeatures(null, context.getUserId()) & 32) == 0;
    }
}
