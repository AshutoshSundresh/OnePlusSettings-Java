package com.android.settings.security;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

public class EncryptionStatusPreferenceController extends BasePreferenceController {
    static final String PREF_KEY_ENCRYPTION_DETAIL_PAGE = "encryption_and_credentials_encryption_status";
    static final String PREF_KEY_ENCRYPTION_SECURITY_PAGE = "encryption_and_credential";
    private final UserManager mUserManager;

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

    public EncryptionStatusPreferenceController(Context context, String str) {
        super(context, str);
        this.mUserManager = (UserManager) context.getSystemService("user");
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!TextUtils.equals(getPreferenceKey(), PREF_KEY_ENCRYPTION_DETAIL_PAGE) || this.mContext.getResources().getBoolean(C0005R$bool.config_show_encryption_and_credentials_encryption_status)) {
            return this.mUserManager.isAdminUser() ? 0 : 4;
        }
        return 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (LockPatternUtils.isDeviceEncryptionEnabled()) {
            if (TextUtils.equals(getPreferenceKey(), PREF_KEY_ENCRYPTION_DETAIL_PAGE)) {
                preference.setFragment(null);
            }
            preference.setSummary(C0017R$string.crypt_keeper_encrypted_summary);
            return;
        }
        if (TextUtils.equals(getPreferenceKey(), PREF_KEY_ENCRYPTION_DETAIL_PAGE)) {
            preference.setFragment(CryptKeeperSettings.class.getName());
        }
        preference.setSummary(C0017R$string.decryption_settings_summary);
    }
}
