package com.android.settings.deviceinfo;

import android.os.Bundle;
import android.view.View;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;

public class StorageWizardReady extends StorageWizardBase {
    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.deviceinfo.StorageWizardBase, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mDisk == null) {
            finish();
            return;
        }
        setContentView(C0012R$layout.storage_wizard_generic);
        setHeaderText(C0017R$string.storage_wizard_ready_title, getDiskShortDescription());
        if (findFirstVolume(1) == null) {
            setBodyText(C0017R$string.storage_wizard_ready_v2_external_body, getDiskDescription());
        } else if (getIntent().getBooleanExtra("migrate_skip", false)) {
            setBodyText(C0017R$string.storage_wizard_ready_v2_internal_body, getDiskDescription());
        } else {
            setBodyText(C0017R$string.storage_wizard_ready_v2_internal_moved_body, getDiskDescription(), getDiskShortDescription());
        }
        setNextButtonText(C0017R$string.done, new CharSequence[0]);
        setBackButtonVisibility(4);
    }

    @Override // com.android.settings.deviceinfo.StorageWizardBase
    public void onNavigateNext(View view) {
        finishAffinity();
    }
}
