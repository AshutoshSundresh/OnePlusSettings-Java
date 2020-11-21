package com.android.settings.notification.zen;

import android.content.Context;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenModeDurationPreferenceController extends AbstractZenModePreferenceController implements PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return "zen_mode_duration_settings";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeDurationPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, "zen_mode_duration_settings", lifecycle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int zenDuration = getZenDuration();
        if (zenDuration < 0) {
            return this.mContext.getString(C0017R$string.zen_mode_duration_summary_always_prompt);
        }
        if (zenDuration == 0) {
            return this.mContext.getString(C0017R$string.zen_mode_duration_summary_forever);
        }
        if (zenDuration >= 60) {
            int i = zenDuration / 60;
            return this.mContext.getResources().getQuantityString(C0015R$plurals.zen_mode_duration_summary_time_hours, i, Integer.valueOf(i));
        }
        return this.mContext.getResources().getString(C0017R$string.zen_mode_duration_summary_time_minutes, Integer.valueOf(zenDuration));
    }
}
