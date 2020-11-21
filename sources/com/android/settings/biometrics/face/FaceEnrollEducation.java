package com.android.settings.biometrics.face;

import android.content.ComponentName;
import android.content.Intent;
import android.hardware.face.FaceManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.view.IllustrationVideoView;

public class FaceEnrollEducation extends BiometricEnrollBase {
    private TextView mDescriptionText;
    private FaceManager mFaceManager;
    private View mIllustrationAccessibility;
    private IllustrationVideoView mIllustrationNormal;
    private boolean mNextClicked;
    private Intent mResultIntent;
    private FaceEnrollAccessibilityToggle mSwitchDiversity;
    private CompoundButton.OnCheckedChangeListener mSwitchDiversityListener = new CompoundButton.OnCheckedChangeListener() {
        /* class com.android.settings.biometrics.face.FaceEnrollEducation.AnonymousClass1 */

        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            int i;
            if (z) {
                i = C0017R$string.security_settings_face_enroll_education_title_accessibility;
            } else {
                i = C0017R$string.security_settings_face_enroll_education_title;
            }
            FaceEnrollEducation.this.getLayout().setHeaderText(i);
            FaceEnrollEducation.this.setTitle(i);
            if (z) {
                FaceEnrollEducation.this.mIllustrationNormal.stop();
                FaceEnrollEducation.this.mIllustrationNormal.setVisibility(4);
                FaceEnrollEducation.this.mIllustrationAccessibility.setVisibility(0);
                FaceEnrollEducation.this.mDescriptionText.setVisibility(4);
                return;
            }
            FaceEnrollEducation.this.mIllustrationNormal.setVisibility(0);
            FaceEnrollEducation.this.mIllustrationNormal.start();
            FaceEnrollEducation.this.mIllustrationAccessibility.setVisibility(4);
            FaceEnrollEducation.this.mDescriptionText.setVisibility(0);
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1506;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.core.InstrumentedActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.face_enroll_education);
        getLayout().setHeaderText(C0017R$string.security_settings_face_enroll_education_title);
        setTitle(C0017R$string.security_settings_face_enroll_education_title);
        new Handler();
        this.mFaceManager = Utils.getFaceManagerOrNull(this);
        this.mIllustrationNormal = (IllustrationVideoView) findViewById(C0010R$id.illustration_normal);
        this.mIllustrationAccessibility = findViewById(C0010R$id.illustration_accessibility);
        this.mDescriptionText = (TextView) findViewById(C0010R$id.sud_layout_description);
        this.mFooterBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        if (WizardManagerHelper.isAnySetupWizard(getIntent())) {
            FooterBarMixin footerBarMixin = this.mFooterBarMixin;
            FooterButton.Builder builder = new FooterButton.Builder(this);
            builder.setText(C0017R$string.skip_label);
            builder.setListener(new View.OnClickListener() {
                /* class com.android.settings.biometrics.face.$$Lambda$N7mjXS72UleD5SviUhN3TiOt1uQ */

                public final void onClick(View view) {
                    FaceEnrollEducation.this.onSkipButtonClick(view);
                }
            });
            builder.setButtonType(7);
            builder.setTheme(C0018R$style.OnePlusSecondaryButtonStyle);
            footerBarMixin.setSecondaryButton(builder.build());
        } else {
            FooterBarMixin footerBarMixin2 = this.mFooterBarMixin;
            FooterButton.Builder builder2 = new FooterButton.Builder(this);
            builder2.setText(C0017R$string.security_settings_face_enroll_introduction_cancel);
            builder2.setListener(new View.OnClickListener() {
                /* class com.android.settings.biometrics.face.$$Lambda$N7mjXS72UleD5SviUhN3TiOt1uQ */

                public final void onClick(View view) {
                    FaceEnrollEducation.this.onSkipButtonClick(view);
                }
            });
            builder2.setButtonType(2);
            builder2.setTheme(C0018R$style.OnePlusSecondaryButtonStyle);
            footerBarMixin2.setSecondaryButton(builder2.build());
        }
        FooterButton.Builder builder3 = new FooterButton.Builder(this);
        builder3.setText(C0017R$string.security_settings_face_enroll_education_start);
        builder3.setListener(new View.OnClickListener() {
            /* class com.android.settings.biometrics.face.$$Lambda$CVTqYfzdKGktOatbRnhbQpNkJc */

            public final void onClick(View view) {
                FaceEnrollEducation.this.onNextButtonClick(view);
            }
        });
        builder3.setButtonType(5);
        builder3.setTheme(C0018R$style.OnePlusPrimaryButtonStyle);
        FooterButton build = builder3.build();
        AccessibilityManager accessibilityManager = (AccessibilityManager) getApplicationContext().getSystemService(AccessibilityManager.class);
        boolean z = false;
        if (accessibilityManager != null && accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled()) {
            z = true;
        }
        this.mFooterBarMixin.setPrimaryButton(build);
        Button button = (Button) findViewById(C0010R$id.accessibility_button);
        button.setOnClickListener(new View.OnClickListener(button) {
            /* class com.android.settings.biometrics.face.$$Lambda$FaceEnrollEducation$Mq_CzR2B1x2fNnOC9sBofQwkMjA */
            public final /* synthetic */ Button f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                FaceEnrollEducation.this.lambda$onCreate$0$FaceEnrollEducation(this.f$1, view);
            }
        });
        FaceEnrollAccessibilityToggle faceEnrollAccessibilityToggle = (FaceEnrollAccessibilityToggle) findViewById(C0010R$id.toggle_diversity);
        this.mSwitchDiversity = faceEnrollAccessibilityToggle;
        faceEnrollAccessibilityToggle.setListener(this.mSwitchDiversityListener);
        this.mSwitchDiversity.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.biometrics.face.$$Lambda$FaceEnrollEducation$upJM5ryct1DGbCtsr7aNLot4Vbo */

            public final void onClick(View view) {
                FaceEnrollEducation.this.lambda$onCreate$1$FaceEnrollEducation(view);
            }
        });
        if (z) {
            button.callOnClick();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$FaceEnrollEducation(Button button, View view) {
        this.mSwitchDiversity.setChecked(true);
        button.setVisibility(8);
        this.mSwitchDiversity.setVisibility(0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$1 */
    public /* synthetic */ void lambda$onCreate$1$FaceEnrollEducation(View view) {
        this.mSwitchDiversity.getSwitch().toggle();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        this.mSwitchDiversityListener.onCheckedChanged(this.mSwitchDiversity.getSwitch(), this.mSwitchDiversity.isChecked());
        if (this.mFaceManager.getEnrolledFaces(this.mUserId).size() >= getResources().getInteger(17694815)) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollBase
    public boolean shouldFinishWhenBackgrounded() {
        return super.shouldFinishWhenBackgrounded() && !this.mNextClicked;
    }

    /* access modifiers changed from: protected */
    public void onNextButtonClick(View view) {
        Intent intent = new Intent();
        byte[] bArr = this.mToken;
        if (bArr != null) {
            intent.putExtra("hw_auth_token", bArr);
        }
        int i = this.mUserId;
        if (i != -10000) {
            intent.putExtra("android.intent.extra.USER_ID", i);
        }
        intent.putExtra("from_settings_summary", this.mFromSettingsSummary);
        String string = getString(C0017R$string.config_face_enroll);
        if (!TextUtils.isEmpty(string)) {
            intent.setComponent(ComponentName.unflattenFromString(string));
        } else {
            intent.setClass(this, FaceEnrollEnrolling.class);
        }
        WizardManagerHelper.copyWizardManagerExtras(getIntent(), intent);
        Intent intent2 = this.mResultIntent;
        if (intent2 != null) {
            intent.putExtras(intent2);
        }
        this.mNextClicked = true;
        intent.putExtra("accessibility_diversity", true ^ this.mSwitchDiversity.isChecked());
        startActivityForResult(intent, 2);
    }

    /* access modifiers changed from: protected */
    public void onSkipButtonClick(View view) {
        setResult(2);
        finish();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        this.mResultIntent = intent;
        if (i != 2) {
            return;
        }
        if (i2 == 1 || i2 == 2 || i2 == 3) {
            setResult(i2, intent);
            finish();
        }
    }
}
