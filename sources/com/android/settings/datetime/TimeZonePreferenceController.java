package com.android.settings.datetime;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.datetime.ZoneGetter;
import java.util.Calendar;

public class TimeZonePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final AutoTimeZonePreferenceController mAutoTimeZonePreferenceController;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "timezone";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public TimeZonePreferenceController(Context context, AutoTimeZonePreferenceController autoTimeZonePreferenceController) {
        super(context);
        this.mAutoTimeZonePreferenceController = autoTimeZonePreferenceController;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof RestrictedPreference) {
            preference.setSummary(getTimeZoneOffsetAndName());
            if (!((RestrictedPreference) preference).isDisabledByAdmin()) {
                preference.setEnabled(!this.mAutoTimeZonePreferenceController.isEnabled());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public CharSequence getTimeZoneOffsetAndName() {
        Calendar instance = Calendar.getInstance();
        return ZoneGetter.getTimeZoneOffsetAndName(this.mContext, instance.getTimeZone(), instance.getTime());
    }
}
