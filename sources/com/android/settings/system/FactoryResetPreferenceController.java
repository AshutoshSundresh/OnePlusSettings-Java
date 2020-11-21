package com.android.settings.system;

import android.content.Context;
import android.os.UserManager;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class FactoryResetPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final UserManager mUm;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "factory_reset";
    }

    public FactoryResetPreferenceController(Context context) {
        super(context);
        this.mUm = (UserManager) context.getSystemService("user");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mUm.isAdminUser() || Utils.isDemoUser(this.mContext);
    }
}
