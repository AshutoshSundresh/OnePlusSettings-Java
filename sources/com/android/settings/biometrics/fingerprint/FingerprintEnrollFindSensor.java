package com.android.settings.biometrics.fingerprint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.C0002R$anim;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.android.settings.biometrics.BiometricEnrollSidecar;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;

public class FingerprintEnrollFindSensor extends BiometricEnrollBase {
    private FingerprintFindSensorAnimation mAnimation;
    private boolean mNextClicked;
    private FingerprintEnrollSidecar mSidecar;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 241;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.core.InstrumentedActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(getContentView());
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        FooterButton.Builder builder = new FooterButton.Builder(this);
        builder.setText(C0017R$string.skip_label);
        builder.setListener(new View.OnClickListener() {
            /* class com.android.settings.biometrics.fingerprint.$$Lambda$DHTjUcEj_0xJoKUWFRXcWA9fscA */

            public final void onClick(View view) {
                FingerprintEnrollFindSensor.this.onSkipButtonClick(view);
            }
        });
        builder.setButtonType(7);
        builder.setTheme(C0018R$style.OnePlusSecondaryButtonStyle);
        footerBarMixin.setSecondaryButton(builder.build());
        setHeaderText(C0017R$string.security_settings_fingerprint_enroll_find_sensor_title);
        startLookingForFingerprint();
        View findViewById = findViewById(C0010R$id.fingerprint_sensor_location_animation);
        if (findViewById instanceof FingerprintFindSensorAnimation) {
            this.mAnimation = (FingerprintFindSensorAnimation) findViewById;
        } else {
            this.mAnimation = null;
        }
    }

    /* access modifiers changed from: protected */
    public int getContentView() {
        return C0012R$layout.fingerprint_enroll_find_sensor;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStart() {
        super.onStart();
        FingerprintFindSensorAnimation fingerprintFindSensorAnimation = this.mAnimation;
        if (fingerprintFindSensorAnimation != null) {
            fingerprintFindSensorAnimation.startAnimation();
        }
    }

    private void startLookingForFingerprint() {
        FingerprintEnrollSidecar fingerprintEnrollSidecar = (FingerprintEnrollSidecar) getSupportFragmentManager().findFragmentByTag("sidecar");
        this.mSidecar = fingerprintEnrollSidecar;
        if (fingerprintEnrollSidecar == null) {
            this.mSidecar = new FingerprintEnrollSidecar();
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.add(this.mSidecar, "sidecar");
            beginTransaction.commitAllowingStateLoss();
        }
        this.mSidecar.setListener(new BiometricEnrollSidecar.Listener() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintEnrollFindSensor.AnonymousClass1 */

            @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
            public void onEnrollmentHelp(int i, CharSequence charSequence) {
            }

            @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
            public void onEnrollmentProgressChange(int i, int i2) {
                FingerprintEnrollFindSensor.this.mNextClicked = true;
                FingerprintEnrollFindSensor.this.proceedToEnrolling(true);
            }

            @Override // com.android.settings.biometrics.BiometricEnrollSidecar.Listener
            public void onEnrollmentError(int i, CharSequence charSequence) {
                if (FingerprintEnrollFindSensor.this.mNextClicked && i == 5) {
                    FingerprintEnrollFindSensor.this.mNextClicked = false;
                    FingerprintEnrollFindSensor.this.proceedToEnrolling(false);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStop() {
        super.onStop();
        FingerprintFindSensorAnimation fingerprintFindSensorAnimation = this.mAnimation;
        if (fingerprintFindSensorAnimation != null) {
            fingerprintFindSensorAnimation.pauseAnimation();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public boolean shouldFinishWhenBackgrounded() {
        return super.shouldFinishWhenBackgrounded() && !this.mNextClicked;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
        FingerprintFindSensorAnimation fingerprintFindSensorAnimation = this.mAnimation;
        if (fingerprintFindSensorAnimation != null) {
            fingerprintFindSensorAnimation.stopAnimation();
        }
    }

    /* access modifiers changed from: protected */
    public void onSkipButtonClick(View view) {
        setResult(2);
        finish();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void proceedToEnrolling(boolean z) {
        FingerprintEnrollSidecar fingerprintEnrollSidecar = this.mSidecar;
        if (fingerprintEnrollSidecar == null) {
            return;
        }
        if (!z || !fingerprintEnrollSidecar.cancelEnrollment()) {
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.remove(this.mSidecar);
            beginTransaction.commitAllowingStateLoss();
            this.mSidecar = null;
            startActivityForResult(getFingerprintEnrollingIntent(), 5);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 4) {
            if (i2 != -1 || intent == null) {
                finish();
                return;
            }
            this.mToken = intent.getByteArrayExtra("hw_auth_token");
            overridePendingTransition(C0002R$anim.sud_slide_next_in, C0002R$anim.sud_slide_next_out);
            getIntent().putExtra("hw_auth_token", this.mToken);
            startLookingForFingerprint();
        } else if (i != 5) {
            super.onActivityResult(i, i2, intent);
        } else if (i2 == 1 || i2 == 2 || i2 == 3) {
            setResult(i2);
            finish();
        } else if (Utils.getFingerprintManagerOrNull(this).getEnrolledFingerprints().size() >= getResources().getInteger(17694816)) {
            finish();
        } else {
            startLookingForFingerprint();
        }
    }
}
