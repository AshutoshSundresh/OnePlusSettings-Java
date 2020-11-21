package com.android.settings.biometrics.face;

import android.content.Intent;
import android.hardware.face.FaceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollIntroduction;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.span.LinkSpan;
import com.google.android.setupdesign.template.RequireScrollMixin;

public class FaceEnrollIntroduction extends BiometricEnrollIntroduction {
    private FaceFeatureProvider mFaceFeatureProvider;
    private FaceManager mFaceManager;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public String getExtraKeyForBiometric() {
        return "for_face";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1506;
    }

    @Override // com.google.android.setupdesign.span.LinkSpan.OnClickListener
    public void onClick(LinkSpan linkSpan) {
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.core.InstrumentedActivity
    public void onCreate(Bundle bundle) {
        int i;
        super.onCreate(bundle);
        this.mFaceManager = Utils.getFaceManagerOrNull(this);
        this.mFaceFeatureProvider = FeatureFactory.getFactory(getApplicationContext()).getFaceFeatureProvider();
        this.mFooterBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        if (WizardManagerHelper.isAnySetupWizard(getIntent())) {
            FooterBarMixin footerBarMixin = this.mFooterBarMixin;
            FooterButton.Builder builder = new FooterButton.Builder(this);
            builder.setText(C0017R$string.security_settings_face_enroll_introduction_no_thanks);
            builder.setListener(new View.OnClickListener() {
                /* class com.android.settings.biometrics.face.$$Lambda$FaceEnrollIntroduction$jMt6I49jNO37kzpftHnGbDtfakE */

                public final void onClick(View view) {
                    FaceEnrollIntroduction.this.onSkipButtonClick(view);
                }
            });
            builder.setButtonType(7);
            builder.setTheme(C0018R$style.OnePlusSecondaryButtonStyle);
            footerBarMixin.setSecondaryButton(builder.build());
        } else {
            FooterBarMixin footerBarMixin2 = this.mFooterBarMixin;
            FooterButton.Builder builder2 = new FooterButton.Builder(this);
            builder2.setText(C0017R$string.security_settings_face_enroll_introduction_no_thanks);
            builder2.setListener(new View.OnClickListener() {
                /* class com.android.settings.biometrics.face.$$Lambda$FaceEnrollIntroduction$1ydREpEzxloopI4SIMM9xynQRio */

                public final void onClick(View view) {
                    FaceEnrollIntroduction.this.onCancelButtonClick(view);
                }
            });
            builder2.setButtonType(2);
            builder2.setTheme(C0018R$style.OnePlusSecondaryButtonStyle);
            footerBarMixin2.setSecondaryButton(builder2.build());
        }
        FooterButton.Builder builder3 = new FooterButton.Builder(this);
        builder3.setText(C0017R$string.security_settings_face_enroll_introduction_agree);
        builder3.setButtonType(5);
        builder3.setTheme(C0018R$style.OnePlusPrimaryButtonStyle);
        if (maxFacesEnrolled()) {
            builder3.setListener(new View.OnClickListener() {
                /* class com.android.settings.biometrics.face.$$Lambda$FaceEnrollIntroduction$ewQUr6y3DwDlrhcAfGX06MsIWCc */

                public final void onClick(View view) {
                    FaceEnrollIntroduction.this.onNextButtonClick(view);
                }
            });
            this.mFooterBarMixin.setPrimaryButton(builder3.build());
        } else {
            FooterButton build = builder3.build();
            this.mFooterBarMixin.setPrimaryButton(build);
            ((RequireScrollMixin) getLayout().getMixin(RequireScrollMixin.class)).requireScrollWithButton(this, build, C0017R$string.security_settings_face_enroll_introduction_more, new View.OnClickListener() {
                /* class com.android.settings.biometrics.face.$$Lambda$FaceEnrollIntroduction$h1dvwhkhQ5mcuoEtW75f7i8wM */

                public final void onClick(View view) {
                    FaceEnrollIntroduction.this.lambda$onCreate$0$FaceEnrollIntroduction(view);
                }
            });
        }
        TextView textView = (TextView) findViewById(C0010R$id.face_enroll_introduction_footer_part_2);
        if (this.mFaceFeatureProvider.isAttentionSupported(getApplicationContext())) {
            i = C0017R$string.security_settings_face_enroll_introduction_footer_part_2;
        } else {
            i = C0017R$string.security_settings_face_settings_footer_attention_not_supported;
        }
        textView.setText(i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$FaceEnrollIntroduction(View view) {
        onNextButtonClick(view);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public boolean isDisabledByAdmin() {
        return RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(this, 128, this.mUserId) != null;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int getLayoutResource() {
        return C0012R$layout.face_enroll_introduction;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int getHeaderResDisabledByAdmin() {
        return C0017R$string.security_settings_face_enroll_introduction_title_unlock_disabled;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int getHeaderResDefault() {
        return C0017R$string.security_settings_face_enroll_introduction_title;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int getDescriptionResDisabledByAdmin() {
        return C0017R$string.security_settings_face_enroll_introduction_message_unlock_disabled;
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

    private boolean maxFacesEnrolled() {
        if (this.mFaceManager == null || this.mFaceManager.getEnrolledFaces(this.mUserId).size() < getResources().getInteger(17694815)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int checkMaxEnrolled() {
        if (this.mFaceManager == null) {
            return C0017R$string.face_intro_error_unknown;
        }
        if (maxFacesEnrolled()) {
            return C0017R$string.face_intro_error_max;
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public long getChallenge() {
        FaceManager faceManagerOrNull = Utils.getFaceManagerOrNull(this);
        this.mFaceManager = faceManagerOrNull;
        if (faceManagerOrNull == null) {
            return 0;
        }
        return faceManagerOrNull.generateChallenge();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public Intent getEnrollingIntent() {
        Intent intent = new Intent(this, FaceEnrollEducation.class);
        WizardManagerHelper.copyWizardManagerExtras(getIntent(), intent);
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction
    public int getConfirmLockTitleResId() {
        return C0017R$string.security_settings_face_preference_title;
    }
}
