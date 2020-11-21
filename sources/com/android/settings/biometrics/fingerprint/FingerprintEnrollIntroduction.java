package com.android.settings.biometrics.fingerprint;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollIntroduction;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.span.LinkSpan;
import com.oneplus.settings.utils.OPUtils;

public class FingerprintEnrollIntroduction extends BiometricEnrollIntroduction {
    private FingerprintManager mFingerprintManager;
    private LottieAnimationView mHowToUseTipsAnimView;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public String getExtraKeyForBiometric() {
        return "for_fingerprint";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 243;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.core.InstrumentedActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        findViewById(C0010R$id.setup_wizard_layout).setFitsSystemWindows(true);
        this.mHowToUseTipsAnimView = (LottieAnimationView) findViewById(C0010R$id.op_how_to_use_fingerprint_tips_view);
        if (OPUtils.isBlackModeOn(getContentResolver())) {
            this.mHowToUseTipsAnimView.setAnimation("op_custom_fingerprint_guide_dark.json");
        } else {
            this.mHowToUseTipsAnimView.setAnimation("op_custom_fingerprint_guide.json");
        }
        this.mHowToUseTipsAnimView.loop(true);
        this.mHowToUseTipsAnimView.playAnimation();
        TextView textView = (TextView) findViewById(C0010R$id.functional_terms);
        if (textView != null) {
            textView.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.biometrics.fingerprint.$$Lambda$FingerprintEnrollIntroduction$qUK7iYNkKyDQKBU_KuJvf09SqP8 */

                public final void onClick(View view) {
                    FingerprintEnrollIntroduction.this.lambda$onCreate$0$FingerprintEnrollIntroduction(view);
                }
            });
        }
        this.mFingerprintManager = Utils.getFingerprintManagerOrNull(this);
        RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(this, 32, this.mUserId);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        FooterButton.Builder builder = new FooterButton.Builder(this);
        builder.setText(C0017R$string.security_settings_face_enroll_introduction_cancel);
        builder.setListener(new View.OnClickListener() {
            /* class com.android.settings.biometrics.fingerprint.$$Lambda$FingerprintEnrollIntroduction$cKPkgzkUH_XMmiWPElPo2dP6s8 */

            public final void onClick(View view) {
                FingerprintEnrollIntroduction.this.onCancelButtonClick(view);
            }
        });
        builder.setButtonType(7);
        builder.setTheme(C0018R$style.OnePlusSecondaryButtonStyle);
        footerBarMixin.setSecondaryButton(builder.build());
        FooterBarMixin footerBarMixin2 = this.mFooterBarMixin;
        FooterButton.Builder builder2 = new FooterButton.Builder(this);
        builder2.setText(C0017R$string.wizard_next);
        builder2.setListener(new View.OnClickListener() {
            /* class com.android.settings.biometrics.fingerprint.$$Lambda$FingerprintEnrollIntroduction$wuUw2dRDPRCgC2ynV6XkWZaCVE */

            public final void onClick(View view) {
                FingerprintEnrollIntroduction.this.onNextButtonClick(view);
            }
        });
        builder2.setButtonType(5);
        builder2.setTheme(C0018R$style.OnePlusPrimaryButtonStyle);
        footerBarMixin2.setPrimaryButton(builder2.build());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$FingerprintEnrollIntroduction(View view) {
        Intent intent = new Intent("android.oem.intent.action.OP_LEGAL");
        intent.putExtra("op_legal_notices_type", 8);
        intent.putExtra("key_from_settings", true);
        startActivity(intent);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public void setHeaderText(int i) {
        ((TextView) findViewById(C0010R$id.suc_layout_title)).setText(i);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStart() {
        super.onStart();
        LottieAnimationView lottieAnimationView = this.mHowToUseTipsAnimView;
        if (lottieAnimationView != null) {
            lottieAnimationView.playAnimation();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStop() {
        super.onStop();
        LottieAnimationView lottieAnimationView = this.mHowToUseTipsAnimView;
        if (lottieAnimationView != null) {
            lottieAnimationView.pauseAnimation();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
        LottieAnimationView lottieAnimationView = this.mHowToUseTipsAnimView;
        if (lottieAnimationView != null) {
            lottieAnimationView.cancelAnimation();
            this.mHowToUseTipsAnimView = null;
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public boolean isDisabledByAdmin() {
        return RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(this, 32, this.mUserId) != null;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int getLayoutResource() {
        return C0012R$layout.fingerprint_enroll_introduction;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int getHeaderResDisabledByAdmin() {
        return C0017R$string.security_settings_fingerprint_enroll_introduction_title_unlock_disabled;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int getHeaderResDefault() {
        return C0017R$string.security_settings_fingerprint_enroll_introduction_title;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int getDescriptionResDisabledByAdmin() {
        return C0017R$string.security_settings_fingerprint_enroll_introduction_message_unlock_disabled;
    }

    /* access modifiers changed from: protected */
    public FooterButton getCancelButton() {
        FooterBarMixin footerBarMixin = this.mFooterBarMixin;
        if (footerBarMixin != null) {
            return footerBarMixin.getSecondaryButton();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public FooterButton getNextButton() {
        FooterBarMixin footerBarMixin = this.mFooterBarMixin;
        if (footerBarMixin != null) {
            return footerBarMixin.getPrimaryButton();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public TextView getErrorTextView() {
        return (TextView) findViewById(C0010R$id.error_text);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int checkMaxEnrolled() {
        if (this.mFingerprintManager == null) {
            return C0017R$string.fingerprint_intro_error_unknown;
        }
        if (this.mFingerprintManager.getEnrolledFingerprints(this.mUserId).size() >= getResources().getInteger(17694816)) {
            return C0017R$string.fingerprint_intro_error_max;
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public long getChallenge() {
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        this.mFingerprintManager = fingerprintManagerOrNull;
        if (fingerprintManagerOrNull == null) {
            return 0;
        }
        return fingerprintManagerOrNull.preEnroll();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public Intent getEnrollingIntent() {
        Intent intent = new Intent();
        intent.setClass(this, FingerprintEnrollEnrolling.class);
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int getConfirmLockTitleResId() {
        return C0017R$string.security_settings_fingerprint_preference_title;
    }

    @Override // com.google.android.setupdesign.span.LinkSpan.OnClickListener
    public void onClick(LinkSpan linkSpan) {
        if ("url".equals(linkSpan.getId())) {
            Intent helpIntent = HelpUtils.getHelpIntent(this, getString(C0017R$string.help_url_fingerprint), getClass().getName());
            if (helpIntent == null) {
                Log.w("FingerprintIntro", "Null help intent.");
                return;
            }
            try {
                startActivityForResult(helpIntent, 3);
            } catch (ActivityNotFoundException e) {
                Log.w("FingerprintIntro", "Activity was not found for intent, " + e);
            }
        }
    }
}
