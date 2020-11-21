package com.android.settings.development;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class DSUTermsOfServiceActivity extends Activity {
    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void installDSU(Intent intent) {
        intent.setClassName("com.android.dynsystem", "com.android.dynsystem.VerificationActivity");
        startActivity(intent);
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.dsu_terms_of_service);
        TextView textView = (TextView) findViewById(C0010R$id.tos_content);
        final Intent intent = getIntent();
        if (!intent.hasExtra("KEY_TOS")) {
            finish();
        }
        String stringExtra = intent.getStringExtra("KEY_TOS");
        if (TextUtils.isEmpty(stringExtra)) {
            installDSU(intent);
            return;
        }
        textView.setText(stringExtra);
        ((Button) findViewById(C0010R$id.accept)).setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.development.DSUTermsOfServiceActivity.AnonymousClass1 */

            public void onClick(View view) {
                DSUTermsOfServiceActivity.this.installDSU(intent);
            }
        });
    }
}
