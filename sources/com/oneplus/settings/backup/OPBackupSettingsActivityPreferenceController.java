package com.oneplus.settings.backup;

import android.app.backup.BackupManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.oneplus.settings.utils.OPUtils;

public class OPBackupSettingsActivityPreferenceController extends BasePreferenceController {
    private static final String KEY_BACKUP_SETTINGS = "backup_settings";
    private static final String TAG = "BackupSettingActivityPC";
    private final BackupManager mBackupManager;
    private final UserManager mUm;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OPBackupSettingsActivityPreferenceController(Context context) {
        super(context, KEY_BACKUP_SETTINGS);
        this.mUm = (UserManager) context.getSystemService("user");
        this.mBackupManager = new BackupManager(context);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!this.mUm.isAdminUser() || !OPUtils.isO2()) ? 3 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (this.mBackupManager.isBackupEnabled()) {
            return this.mContext.getText(C0017R$string.accessibility_feature_state_on);
        }
        return this.mContext.getText(C0017R$string.accessibility_feature_state_off);
    }
}
