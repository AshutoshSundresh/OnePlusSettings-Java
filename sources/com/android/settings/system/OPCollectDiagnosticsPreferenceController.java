package com.android.settings.system;

import android.content.Context;
import android.os.UserManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0005R$bool;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class OPCollectDiagnosticsPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final UserManager mUm;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "reset_collect_diagnostics";
    }

    public OPCollectDiagnosticsPreferenceController(Context context) {
        super(context);
        this.mUm = (UserManager) context.getSystemService("user");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(C0005R$bool.config_show_collect_diagnostics) && (this.mUm.isAdminUser() || Utils.isDemoUser(this.mContext));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (!isAvailable()) {
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (!isAvailable()) {
        }
    }
}
