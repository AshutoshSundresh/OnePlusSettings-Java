package com.android.settings.deviceinfo;

import android.content.Context;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.deviceinfo.AbstractUptimePreferenceController;

public class UptimePreferenceController extends AbstractUptimePreferenceController implements PreferenceControllerMixin {
    public UptimePreferenceController(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }
}
