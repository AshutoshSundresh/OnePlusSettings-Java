package com.android.settings.notification.zen;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.zen.ZenModeSettings;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenModeCallsPreferenceController extends AbstractZenModePreferenceController implements PreferenceControllerMixin {
    private final String KEY_BEHAVIOR_SETTINGS;
    private final ZenModeSettings.SummaryBuilder mSummaryBuilder;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeCallsPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
        this.KEY_BEHAVIOR_SETTINGS = str;
        this.mSummaryBuilder = new ZenModeSettings.SummaryBuilder(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return this.KEY_BEHAVIOR_SETTINGS;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        int zenMode = getZenMode();
        if (zenMode == 2 || zenMode == 3) {
            preference.setEnabled(false);
            preference.setSummary(this.mBackend.getAlarmsTotalSilencePeopleSummary(8));
            return;
        }
        preference.setEnabled(true);
        preference.setSummary(this.mSummaryBuilder.getCallsSettingSummary(getPolicy()));
    }
}
