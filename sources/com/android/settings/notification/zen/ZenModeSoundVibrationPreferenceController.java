package com.android.settings.notification.zen;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.zen.ZenModeSettings;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenModeSoundVibrationPreferenceController extends AbstractZenModePreferenceController implements PreferenceControllerMixin {
    private final String mKey;
    private final ZenModeSettings.SummaryBuilder mSummaryBuilder;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeSoundVibrationPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
        this.mKey = str;
        this.mSummaryBuilder = new ZenModeSettings.SummaryBuilder(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return this.mKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        int zenMode = getZenMode();
        if (zenMode == 2) {
            preference.setEnabled(false);
            preference.setSummary(this.mContext.getString(C0017R$string.zen_mode_other_sounds_none));
        } else if (zenMode != 3) {
            preference.setEnabled(true);
            preference.setSummary(this.mSummaryBuilder.getOtherSoundCategoriesSummary(getPolicy()));
        } else {
            preference.setEnabled(false);
            preference.setSummary(this.mContext.getString(C0017R$string.zen_mode_behavior_alarms_only));
        }
    }
}
