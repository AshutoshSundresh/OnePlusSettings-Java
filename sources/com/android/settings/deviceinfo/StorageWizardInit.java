package com.android.settings.deviceinfo;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.os.storage.VolumeInfo;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.overlay.FeatureFactory;

public class StorageWizardInit extends StorageWizardBase {
    private Button mInternal;
    private boolean mIsPermittedToAdopt;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.deviceinfo.StorageWizardBase, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mDisk == null) {
            finish();
            return;
        }
        setContentView(C0012R$layout.storage_wizard_init);
        this.mIsPermittedToAdopt = UserManager.get(this).isAdminUser() && !ActivityManager.isUserAMonkey();
        setHeaderText(C0017R$string.storage_wizard_init_v2_title, getDiskShortDescription());
        this.mInternal = (Button) requireViewById(C0010R$id.storage_wizard_init_internal);
        setBackButtonText(C0017R$string.storage_wizard_init_v2_later, new CharSequence[0]);
        setNextButtonVisibility(4);
        if (!this.mDisk.isAdoptable()) {
            this.mInternal.setEnabled(false);
            onNavigateExternal(null);
        } else if (!this.mIsPermittedToAdopt) {
            this.mInternal.setEnabled(false);
        }
    }

    @Override // com.android.settings.deviceinfo.StorageWizardBase
    public void onNavigateBack(View view) {
        finish();
    }

    public void onNavigateExternal(View view) {
        if (view != null) {
            FeatureFactory.getFactory(this).getMetricsFeatureProvider().action(this, 1407, new Pair[0]);
        }
        VolumeInfo volumeInfo = this.mVolume;
        if (volumeInfo == null || volumeInfo.getType() != 0 || this.mVolume.getState() == 6) {
            StorageWizardFormatConfirm.showPublic(this, this.mDisk.getId());
            return;
        }
        this.mStorage.setVolumeInited(this.mVolume.getFsUuid(), true);
        Intent intent = new Intent(this, StorageWizardReady.class);
        intent.putExtra("android.os.storage.extra.DISK_ID", this.mDisk.getId());
        startActivity(intent);
        finish();
    }

    public void onNavigateInternal(View view) {
        if (view != null) {
            FeatureFactory.getFactory(this).getMetricsFeatureProvider().action(this, 1408, new Pair[0]);
        }
        StorageWizardFormatConfirm.showPrivate(this, this.mDisk.getId());
    }
}
