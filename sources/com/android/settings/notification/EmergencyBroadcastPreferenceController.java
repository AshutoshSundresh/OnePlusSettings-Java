package com.android.settings.notification;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.preference.Preference;
import com.android.settings.accounts.AccountRestrictionHelper;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.oneplus.settings.utils.OPUtils;

public class EmergencyBroadcastPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private AccountRestrictionHelper mHelper;
    private final String mPrefKey;
    private UserManager mUserManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        return false;
    }

    public EmergencyBroadcastPreferenceController(Context context, String str) {
        this(context, new AccountRestrictionHelper(context), str);
    }

    EmergencyBroadcastPreferenceController(Context context, AccountRestrictionHelper accountRestrictionHelper, String str) {
        super(context);
        this.mPrefKey = str;
        this.mHelper = accountRestrictionHelper;
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mContext.getPackageManager();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof RestrictedPreference) {
            ((RestrictedPreference) preference).checkRestrictionAndSetDisabled("no_config_cell_broadcasts");
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mPrefKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mUserManager.isAdminUser() && isCellBroadcastAppLinkEnabled() && !this.mHelper.hasBaseUserRestriction("no_config_cell_broadcasts", UserHandle.myUserId());
    }

    private boolean isCellBroadcastAppLinkEnabled() {
        boolean z = this.mContext.getResources().getBoolean(17891396);
        if (!z) {
            return z;
        }
        try {
            if (OPUtils.isApplicationEnabled(this.mContext, "com.android.cellbroadcastreceiver") || OPUtils.isApplicationEnabled(this.mContext, "com.google.android.cellbroadcastreceiver")) {
                return z;
            }
        } catch (IllegalArgumentException unused) {
        }
        return false;
    }
}
