package com.android.settings.backup;

import android.app.backup.IBackupManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class AutoRestorePreferenceController extends TogglePreferenceController {
    private static final String TAG = "AutoRestorePrefCtrler";
    private PrivacySettingsConfigData mPSCD = PrivacySettingsConfigData.getInstance();
    private Preference mPreference;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AutoRestorePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!PrivacySettingsUtils.isAdminUser(this.mContext)) {
            return 4;
        }
        return PrivacySettingsUtils.isInvisibleKey(this.mContext, "auto_restore") ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.TogglePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference = preference;
        preference.setEnabled(this.mPSCD.isBackupEnabled());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        if (Settings.Secure.getInt(this.mContext.getContentResolver(), "backup_auto_restore", 1) == 1) {
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        try {
            IBackupManager.Stub.asInterface(ServiceManager.getService("backup")).setAutoRestore(z);
            return true;
        } catch (RemoteException e) {
            ((SwitchPreference) this.mPreference).setChecked(!z);
            Log.e(TAG, "Error can't set setAutoRestore", e);
            return false;
        }
    }
}
