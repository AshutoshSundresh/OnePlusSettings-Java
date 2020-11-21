package com.android.settings.biometrics.fingerprint;

import android.content.Intent;
import com.android.settings.SetupWizardUtils;

public class SetupFingerprintEnrollEnrolling extends FingerprintEnrollEnrolling {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable, com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling
    public int getMetricsCategory() {
        return 246;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricsEnrollEnrolling, com.android.settings.biometrics.fingerprint.FingerprintEnrollEnrolling
    public Intent getFinishIntent() {
        Intent intent = new Intent(this, SetupFingerprintEnrollFinish.class);
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        return intent;
    }
}
