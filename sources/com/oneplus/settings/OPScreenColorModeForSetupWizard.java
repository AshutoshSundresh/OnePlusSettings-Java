package com.oneplus.settings;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.C0002R$anim;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.core.SettingsBaseActivity;
import com.oneplus.settings.better.OPSuwScreenColorMode;

public class OPScreenColorModeForSetupWizard extends SettingsBaseActivity {
    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.android.settings.core.SettingsBaseActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.op_screen_color_mode_for_setupwizard_layout);
        OPSuwScreenColorMode oPSuwScreenColorMode = new OPSuwScreenColorMode();
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        beginTransaction.replace(C0010R$id.main_content, oPSuwScreenColorMode);
        beginTransaction.commitAllowingStateLoss();
        final Intent intent = new Intent();
        ((Button) findViewById(C0010R$id.next_button)).setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.OPScreenColorModeForSetupWizard.AnonymousClass1 */

            public void onClick(View view) {
                intent.setComponent(new ComponentName("com.oneplus.setupwizard", "com.oneplus.setupwizard.OneplusFontSetActivity"));
                OPScreenColorModeForSetupWizard.this.startActivity(intent);
                OPScreenColorModeForSetupWizard.this.overridePendingTransition(C0002R$anim.op_slide_in, C0002R$anim.op_slide_out);
            }
        });
    }
}
