package com.android.settings.biometrics;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.util.Log;
import com.android.settings.SetupWizardUtils;
import com.android.settings.biometrics.face.FaceEnrollIntroduction;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollFindSensor;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction;
import com.android.settings.biometrics.fingerprint.SetupFingerprintEnrollIntroduction;
import com.android.settings.core.InstrumentedActivity;
import com.android.settings.password.ChooseLockGeneric;
import com.google.android.setupcompat.util.WizardManagerHelper;

public class BiometricEnrollActivity extends InstrumentedActivity {

    public static final class InternalActivity extends BiometricEnrollActivity {
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1586;
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.core.InstrumentedActivity
    public void onCreate(Bundle bundle) {
        Intent intent;
        super.onCreate(bundle);
        int intExtra = getIntent().getIntExtra("android.provider.extra.BIOMETRIC_AUTHENTICATORS_ALLOWED", 255);
        Log.d("BiometricEnrollActivity", "Authenticators: " + intExtra);
        PackageManager packageManager = getApplicationContext().getPackageManager();
        int canAuthenticate = ((BiometricManager) getSystemService(BiometricManager.class)).canAuthenticate(intExtra);
        if (WizardManagerHelper.isAnySetupWizard(getIntent()) || !(canAuthenticate == 0 || canAuthenticate == 12)) {
            if (intExtra == 32768) {
                intent = new Intent(this, ChooseLockGeneric.class);
                intent.putExtra("minimum_quality", 65536);
            } else if (packageManager.hasSystemFeature("android.hardware.fingerprint")) {
                intent = (!getIntent().getBooleanExtra("skip_intro", false) || !(this instanceof InternalActivity)) ? getFingerprintIntroIntent() : getFingerprintFindSensorIntent();
            } else {
                intent = packageManager.hasSystemFeature("android.hardware.biometrics.face") ? getFaceIntroIntent() : null;
            }
            if (intent != null) {
                intent.setFlags(33554432);
                if (this instanceof InternalActivity) {
                    byte[] byteArrayExtra = getIntent().getByteArrayExtra("hw_auth_token");
                    int intExtra2 = getIntent().getIntExtra("android.intent.extra.USER_ID", -10000);
                    intent.putExtra("hw_auth_token", byteArrayExtra);
                    intent.putExtra("android.intent.extra.USER_ID", intExtra2);
                }
                startActivity(intent);
                finish();
                return;
            }
            Log.e("BiometricEnrollActivity", "Intent was null, finishing");
            finish();
            return;
        }
        Log.e("BiometricEnrollActivity", "Unexpected result: " + canAuthenticate);
        finish();
    }

    private Intent getFingerprintFindSensorIntent() {
        Intent intent = new Intent(this, FingerprintEnrollFindSensor.class);
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        return intent;
    }

    private Intent getFingerprintIntroIntent() {
        if (!WizardManagerHelper.isAnySetupWizard(getIntent())) {
            return new Intent(this, FingerprintEnrollIntroduction.class);
        }
        Intent intent = new Intent(this, SetupFingerprintEnrollIntroduction.class);
        WizardManagerHelper.copyWizardManagerExtras(getIntent(), intent);
        return intent;
    }

    private Intent getFaceIntroIntent() {
        Intent intent = new Intent(this, FaceEnrollIntroduction.class);
        WizardManagerHelper.copyWizardManagerExtras(getIntent(), intent);
        return intent;
    }
}
