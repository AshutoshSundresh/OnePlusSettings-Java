package com.android.settings.nfc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.oneplus.settings.BaseAppCompatActivity;

public class HowItWorks extends BaseAppCompatActivity {
    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.nfc_payment_how_it_works);
        Toolbar toolbar = (Toolbar) findViewById(C0010R$id.action_bar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.nfc.HowItWorks.AnonymousClass1 */

            public void onClick(View view) {
                HowItWorks.this.onBackPressed();
            }
        });
        ((Button) findViewById(C0010R$id.nfc_how_it_works_button)).setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.nfc.HowItWorks.AnonymousClass2 */

            public void onClick(View view) {
                HowItWorks.this.finish();
            }
        });
    }

    public boolean onNavigateUp() {
        finish();
        return true;
    }
}
