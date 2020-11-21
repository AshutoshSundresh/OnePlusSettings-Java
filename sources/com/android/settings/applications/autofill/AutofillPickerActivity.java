package com.android.settings.applications.autofill;

import android.content.Intent;
import android.os.Bundle;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.applications.defaultapps.DefaultAutofillPicker;

public class AutofillPickerActivity extends SettingsActivity {
    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.android.settings.core.SettingsBaseActivity, com.android.settings.SettingsActivity
    public void onCreate(Bundle bundle) {
        Intent intent = getIntent();
        String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
        intent.putExtra(":settings:show_fragment", DefaultAutofillPicker.class.getName());
        intent.putExtra(":settings:show_fragment_title_resid", C0017R$string.autofill_app);
        intent.putExtra("package_name", schemeSpecificPart);
        super.onCreate(bundle);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        return super.isValidFragment(str) || DefaultAutofillPicker.class.getName().equals(str);
    }
}
