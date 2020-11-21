package com.android.settings.security;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0018R$style;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupdesign.GlifLayout;

public class InstallCaCertificateWarning extends Activity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.ca_certificate_warning_dialog);
        FooterBarMixin footerBarMixin = (FooterBarMixin) ((GlifLayout) findViewById(C0010R$id.setup_wizard_layout)).getMixin(FooterBarMixin.class);
        FooterButton.Builder builder = new FooterButton.Builder(this);
        builder.setText(C0017R$string.certificate_warning_install_anyway);
        builder.setListener(installCaCertificate());
        builder.setButtonType(0);
        builder.setTheme(C0018R$style.OnePlusSecondaryButtonStyle);
        footerBarMixin.setSecondaryButton(builder.build());
        FooterButton.Builder builder2 = new FooterButton.Builder(this);
        builder2.setText(C0017R$string.certificate_warning_dont_install);
        builder2.setListener(returnToInstallCertificateFromStorage());
        builder2.setButtonType(5);
        builder2.setTheme(C0018R$style.OnePlusPrimaryButtonStyle);
        footerBarMixin.setPrimaryButton(builder2.build());
    }

    private View.OnClickListener installCaCertificate() {
        return new View.OnClickListener() {
            /* class com.android.settings.security.$$Lambda$InstallCaCertificateWarning$zyPh1JGA8ya2G74aw_uK5DpjoQo */

            public final void onClick(View view) {
                InstallCaCertificateWarning.this.lambda$installCaCertificate$0$InstallCaCertificateWarning(view);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$installCaCertificate$0 */
    public /* synthetic */ void lambda$installCaCertificate$0$InstallCaCertificateWarning(View view) {
        Intent intent = new Intent();
        intent.setAction("android.credentials.INSTALL");
        intent.putExtra("certificate_install_usage", "ca");
        startActivity(intent);
        finish();
    }

    private View.OnClickListener returnToInstallCertificateFromStorage() {
        return new View.OnClickListener() {
            /* class com.android.settings.security.$$Lambda$InstallCaCertificateWarning$cMHKz1h0aYs7diZ7cAm8c_jG_z8 */

            public final void onClick(View view) {
                InstallCaCertificateWarning.this.lambda$returnToInstallCertificateFromStorage$1$InstallCaCertificateWarning(view);
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$returnToInstallCertificateFromStorage$1 */
    public /* synthetic */ void lambda$returnToInstallCertificateFromStorage$1$InstallCaCertificateWarning(View view) {
        Toast.makeText(this, C0017R$string.cert_not_installed, 0).show();
        finish();
    }
}
