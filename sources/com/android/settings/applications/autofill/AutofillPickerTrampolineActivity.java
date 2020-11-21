package com.android.settings.applications.autofill;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.autofill.AutofillManager;

public class AutofillPickerTrampolineActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        AutofillManager autofillManager = (AutofillManager) getSystemService(AutofillManager.class);
        if (autofillManager == null || !autofillManager.hasAutofillFeature() || !autofillManager.isAutofillSupported()) {
            setResult(0);
            finish();
            return;
        }
        Intent intent = getIntent();
        String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
        ComponentName autofillServiceComponentName = autofillManager.getAutofillServiceComponentName();
        if (autofillServiceComponentName == null || !autofillServiceComponentName.getPackageName().equals(schemeSpecificPart)) {
            startActivity(new Intent(this, AutofillPickerActivity.class).setFlags(33554432).setData(intent.getData()));
            finish();
            return;
        }
        setResult(-1);
        finish();
    }
}
