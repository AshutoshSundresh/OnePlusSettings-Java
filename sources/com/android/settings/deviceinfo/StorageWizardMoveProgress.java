package com.android.settings.deviceinfo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;

public class StorageWizardMoveProgress extends StorageWizardBase {
    private final PackageManager.MoveCallback mCallback = new PackageManager.MoveCallback() {
        /* class com.android.settings.deviceinfo.StorageWizardMoveProgress.AnonymousClass1 */

        public void onStatusChanged(int i, int i2, long j) {
            if (StorageWizardMoveProgress.this.mMoveId == i) {
                if (PackageManager.isMoveStatusFinished(i2)) {
                    Log.d("StorageSettings", "Finished with status " + i2);
                    if (i2 != -100) {
                        StorageWizardMoveProgress storageWizardMoveProgress = StorageWizardMoveProgress.this;
                        Toast.makeText(storageWizardMoveProgress, storageWizardMoveProgress.moveStatusToMessage(i2), 1).show();
                    }
                    StorageWizardMoveProgress.this.finishAffinity();
                    return;
                }
                StorageWizardMoveProgress.this.setCurrentProgress(i2);
            }
        }
    };
    private int mMoveId;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.deviceinfo.StorageWizardBase, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (this.mVolume == null) {
            finish();
            return;
        }
        setContentView(C0012R$layout.storage_wizard_progress);
        this.mMoveId = getIntent().getIntExtra("android.content.pm.extra.MOVE_ID", -1);
        String stringExtra = getIntent().getStringExtra("android.intent.extra.TITLE");
        String bestVolumeDescription = this.mStorage.getBestVolumeDescription(this.mVolume);
        setIcon(C0008R$drawable.ic_swap_horiz);
        setHeaderText(C0017R$string.storage_wizard_move_progress_title, stringExtra);
        setBodyText(C0017R$string.storage_wizard_move_progress_body, bestVolumeDescription, stringExtra);
        setBackButtonVisibility(4);
        setNextButtonVisibility(4);
        getPackageManager().registerMoveCallback(this.mCallback, new Handler());
        this.mCallback.onStatusChanged(this.mMoveId, getPackageManager().getMoveStatus(this.mMoveId), -1);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.deviceinfo.StorageWizardBase
    public void onDestroy() {
        super.onDestroy();
        getPackageManager().unregisterMoveCallback(this.mCallback);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private CharSequence moveStatusToMessage(int i) {
        if (i == -8) {
            return getString(C0017R$string.move_error_device_admin);
        }
        if (i == -5) {
            return getString(C0017R$string.invalid_location);
        }
        if (i == -3) {
            return getString(C0017R$string.system_package);
        }
        if (i != -2) {
            return i != -1 ? getString(C0017R$string.insufficient_storage) : getString(C0017R$string.insufficient_storage);
        }
        return getString(C0017R$string.does_not_exist);
    }
}
