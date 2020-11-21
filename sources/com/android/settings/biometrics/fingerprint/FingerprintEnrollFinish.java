package com.android.settings.biometrics.fingerprint;

import android.content.ComponentName;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.Utils;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.oneplus.settings.OPMemberController;
import com.oneplus.settings.utils.OPUtils;

public class FingerprintEnrollFinish extends BiometricEnrollBase {
    static final String FINGERPRINT_SUGGESTION_ACTIVITY = "com.android.settings.SetupFingerprintSuggestionActivity";
    static final int REQUEST_ADD_ANOTHER = 1;
    protected Button mBtnAdd;
    protected Button mBtnNext;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 242;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.core.InstrumentedActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (OPUtils.isSupportCustomFingerprint()) {
            getWindow().getDecorView().setSystemUiVisibility(0);
        }
        setContentView(C0012R$layout.op_fod_fingerprint_dynamic_enroll_finish);
        setHeaderText(C0017R$string.security_settings_fingerprint_enroll_finish_title);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        FooterButton.Builder builder = new FooterButton.Builder(this);
        builder.setText(C0017R$string.fingerprint_enroll_button_add);
        builder.setButtonType(7);
        builder.setTheme(C0018R$style.OnePlusSecondaryButtonStyle);
        footerBarMixin.setSecondaryButton(builder.build());
        FooterBarMixin footerBarMixin2 = this.mFooterBarMixin;
        FooterButton.Builder builder2 = new FooterButton.Builder(this);
        builder2.setText(C0017R$string.security_settings_fingerprint_enroll_done);
        builder2.setListener(new View.OnClickListener() {
            /* class com.android.settings.biometrics.fingerprint.$$Lambda$G14P8JOcqqo212XtjtFUSNAjU */

            public final void onClick(View view) {
                FingerprintEnrollFinish.this.lambda$onCreate$0(view);
            }
        });
        builder2.setButtonType(5);
        builder2.setTheme(C0018R$style.OnePlusPrimaryButtonStyle);
        footerBarMixin2.setPrimaryButton(builder2.build());
        this.mBtnNext = (Button) findViewById(C0010R$id.next_button);
        this.mBtnAdd = (Button) findViewById(C0010R$id.add_another_button);
        this.mBtnNext.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.biometrics.fingerprint.$$Lambda$FingerprintEnrollFinish$FuTKHYXCbQzLLu3tH8bt4aQY7FY */

            public final void onClick(View view) {
                FingerprintEnrollFinish.this.lambda$onCreate$0$FingerprintEnrollFinish(view);
            }
        });
        if (((FingerprintManager) getSystemService("fingerprint")).getEnrolledFingerprints(this.mUserId).size() >= getResources().getInteger(17694816)) {
            this.mBtnAdd.setVisibility(4);
        }
    }

    @Override // androidx.activity.ComponentActivity
    public void onBackPressed() {
        super.onBackPressed();
        updateFingerprintSuggestionEnableState();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        this.mFooterBarMixin.getSecondaryButton();
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        boolean z = false;
        if (fingerprintManagerOrNull != null && fingerprintManagerOrNull.getEnrolledFingerprints(this.mUserId).size() >= getResources().getInteger(17694816)) {
            z = true;
        }
        if (z) {
            this.mBtnAdd.setVisibility(4);
        } else {
            this.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                /* class com.android.settings.biometrics.fingerprint.$$Lambda$FingerprintEnrollFinish$pbvaovi36rC6rWx2OXL1O9T7RaQ */

                public final void onClick(View view) {
                    FingerprintEnrollFinish.this.onAddAnotherButtonClick(view);
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
        updateFingerprintSuggestionEnableState();
    }

    /* access modifiers changed from: protected */
    /* renamed from: onNextButtonClick */
    public void lambda$onCreate$0(View view) {
        updateFingerprintSuggestionEnableState();
        setResult(1);
        if (WizardManagerHelper.isAnySetupWizard(getIntent())) {
            postEnroll();
        } else if (this.mFromSettingsSummary) {
            launchFingerprintSettings();
        }
        finish();
    }

    private void updateFingerprintSuggestionEnableState() {
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        if (fingerprintManagerOrNull != null) {
            int size = fingerprintManagerOrNull.getEnrolledFingerprints(this.mUserId).size();
            boolean z = true;
            getPackageManager().setComponentEnabledSetting(new ComponentName(getApplicationContext(), FINGERPRINT_SUGGESTION_ACTIVITY), size == 1 ? 1 : 2, 1);
            StringBuilder sb = new StringBuilder();
            sb.append("com.android.settings.SetupFingerprintSuggestionActivity enabled state = ");
            if (size != 1) {
                z = false;
            }
            sb.append(z);
            Log.d("FingerprintEnrollFinish", sb.toString());
        }
    }

    private void postEnroll() {
        int postEnroll;
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(this);
        if (fingerprintManagerOrNull != null && (postEnroll = fingerprintManagerOrNull.postEnroll()) < 0) {
            Log.w("FingerprintEnrollFinish", "postEnroll failed: result = " + postEnroll);
        }
    }

    private void launchFingerprintSettings() {
        Intent intent = new Intent("android.settings.FINGERPRINT_SETTINGS");
        intent.setPackage(OPMemberController.PACKAGE_NAME);
        intent.putExtra("hw_auth_token", this.mToken);
        intent.setFlags(603979776);
        intent.putExtra("android.intent.extra.USER_ID", this.mUserId);
        startActivity(intent);
    }

    /* access modifiers changed from: private */
    public void onAddAnotherButtonClick(View view) {
        startActivityForResult(getFingerprintEnrollingIntent(), 1);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onActivityResult(int i, int i2, Intent intent) {
        updateFingerprintSuggestionEnableState();
        if (i != 1 || i2 == 0) {
            super.onActivityResult(i, i2, intent);
            return;
        }
        setResult(i2, intent);
        finish();
    }
}
