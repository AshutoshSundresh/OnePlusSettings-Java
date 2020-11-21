package com.android.settings.biometrics.face;

import android.os.Bundle;
import android.view.View;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.android.settings.biometrics.BiometricEnrollBase;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;

public class FaceEnrollFinish extends BiometricEnrollBase {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1508;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settings.biometrics.BiometricEnrollBase, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.core.InstrumentedActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.face_enroll_finish);
        setHeaderText(C0017R$string.security_settings_face_enroll_finish_title);
        FooterBarMixin footerBarMixin = (FooterBarMixin) getLayout().getMixin(FooterBarMixin.class);
        this.mFooterBarMixin = footerBarMixin;
        FooterButton.Builder builder = new FooterButton.Builder(this);
        builder.setText(C0017R$string.security_settings_face_enroll_done);
        builder.setListener(new View.OnClickListener() {
            /* class com.android.settings.biometrics.face.$$Lambda$BOOKfYhhJGp_5aFpMRf7jt7tHE */

            public final void onClick(View view) {
                FaceEnrollFinish.this.onNextButtonClick(view);
            }
        });
        builder.setButtonType(5);
        builder.setTheme(C0018R$style.OnePlusPrimaryButtonStyle);
        footerBarMixin.setPrimaryButton(builder.build());
    }

    public void onNextButtonClick(View view) {
        setResult(1);
        finish();
    }
}
