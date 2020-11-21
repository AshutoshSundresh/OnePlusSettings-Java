package com.oneplus.settings.aboutphone;

import android.content.Context;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.deviceinfo.AbstractUptimePreferenceController;

public class OPUptimePreferenceController extends AbstractUptimePreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.deviceinfo.AbstractUptimePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public OPUptimePreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }
}
