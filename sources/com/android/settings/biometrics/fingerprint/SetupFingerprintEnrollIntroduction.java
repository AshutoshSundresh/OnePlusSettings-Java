package com.android.settings.biometrics.fingerprint;

import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.SetupWizardUtils;
import com.android.settings.Utils;
import com.android.settings.password.SetupChooseLockGeneric;

public class SetupFingerprintEnrollIntroduction extends FingerprintEnrollIntroduction {
    private boolean mAlreadyHadLockScreenSetup = false;

    @Override // com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 249;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.core.InstrumentedActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            this.mAlreadyHadLockScreenSetup = isKeyguardSecure();
        } else {
            this.mAlreadyHadLockScreenSetup = bundle.getBoolean("wasLockScreenPresent", false);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settings.biometrics.BiometricEnrollBase, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("wasLockScreenPresent", this.mAlreadyHadLockScreenSetup);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public Intent getChooseLockIntent() {
        Intent intent = new Intent(this, SetupChooseLockGeneric.class);
        if (StorageManager.isFileEncryptedNativeOrEmulated()) {
            intent.putExtra("lockscreen.password_type", 131072);
            intent.putExtra("show_options_button", true);
        }
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, com.android.settings.biometrics.fingerprint.FingerprintEnrollIntroduction
    public Intent getEnrollingIntent() {
        Intent intent = new Intent(this, SetupFingerprintEnrollEnrolling.class);
        intent.putExtra("hw_auth_token", this.mToken);
        int i = this.mUserId;
        if (i != -10000) {
            intent.putExtra("android.intent.extra.USER_ID", i);
        }
        SetupWizardUtils.copySetupExtras(getIntent(), intent);
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollBase
    public void initViews() {
        super.initViews();
        ((TextView) findViewById(C0010R$id.sud_layout_description)).setText(C0017R$string.oneplus_how_to_use_fingerprint_summary_no_note);
        getNextButton().setText(this, C0017R$string.security_settings_fingerprint_enroll_introduction_continue_setup);
        getCancelButton().setText(this, C0017R$string.security_settings_fingerprint_enroll_introduction_cancel_setup);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, androidx.activity.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 2 && isKeyguardSecure()) {
            if (!this.mAlreadyHadLockScreenSetup) {
                intent = getMetricIntent(intent);
            }
            if (i2 == 1) {
                intent = setFingerprintCount(intent);
            }
        }
        super.onActivityResult(i, i2, intent);
    }

    private Intent getMetricIntent(Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        intent.putExtra(":settings:password_quality", new LockPatternUtils(this).getKeyguardStoredPasswordQuality(UserHandle.myUserId()));
        return intent;
    }

    private Intent setFingerprintCount(Intent intent) {
        if (intent == null) {
            intent = new Intent();
        }
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        if (fingerprintManagerOrNull != null) {
            intent.putExtra("fingerprint_enrolled_count", fingerprintManagerOrNull.getEnrolledFingerprints(this.mUserId).size());
        }
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public void onCancelButtonClick(View view) {
        if (isKeyguardSecure()) {
            Intent intent = null;
            if (!this.mAlreadyHadLockScreenSetup) {
                intent = getMetricIntent(null);
            }
            setResult(2, intent);
            finish();
            return;
        }
        setResult(11);
        finish();
    }

    @Override // androidx.activity.ComponentActivity
    public void onBackPressed() {
        if (!this.mAlreadyHadLockScreenSetup && isKeyguardSecure()) {
            setResult(0, getMetricIntent(null));
        }
        super.onBackPressed();
    }

    private boolean isKeyguardSecure() {
        return ((KeyguardManager) getSystemService(KeyguardManager.class)).isKeyguardSecure();
    }
}
