package com.android.settings.password;

import android.app.Activity;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0018R$style;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;

public class ForgotPasswordActivity extends Activity {
    public static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int intExtra = getIntent().getIntExtra("android.intent.extra.USER_ID", -1);
        if (intExtra < 0) {
            Log.e(TAG, "No valid userId supplied, exiting");
            finish();
            return;
        }
        setContentView(C0012R$layout.forgot_password_activity);
        FooterButton.Builder builder = new FooterButton.Builder(this);
        builder.setText(17039370);
        builder.setListener(new View.OnClickListener() {
            /* class com.android.settings.password.$$Lambda$ForgotPasswordActivity$2twrBv2twVGDBO3UODOzCy_Fguk */

            public final void onClick(View view) {
                ForgotPasswordActivity.this.lambda$onCreate$0$ForgotPasswordActivity(view);
            }
        });
        builder.setButtonType(4);
        builder.setTheme(C0018R$style.OnePlusPrimaryButtonStyle);
        ((FooterBarMixin) ((GlifLayout) findViewById(C0010R$id.setup_wizard_layout)).getMixin(FooterBarMixin.class)).setPrimaryButton(builder.build());
        UserManager.get(this).requestQuietModeEnabled(false, UserHandle.of(intExtra), 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ void lambda$onCreate$0$ForgotPasswordActivity(View view) {
        finish();
    }
}
