package com.android.settings.notification.zen;

import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenModeSystemPreferenceController extends AbstractZenModePreferenceController implements Preference.OnPreferenceChangeListener {
    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return "zen_mode_system";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeSystemPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, "zen_mode_system", lifecycle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        SwitchPreference switchPreference = (SwitchPreference) preference;
        int zenMode = getZenMode();
        if (zenMode == 2) {
            switchPreference.setEnabled(false);
            switchPreference.setChecked(false);
        } else if (zenMode != 3) {
            switchPreference.setEnabled(true);
            switchPreference.setChecked(this.mBackend.isPriorityCategoryEnabled(128));
        } else {
            switchPreference.setEnabled(false);
            switchPreference.setChecked(false);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (ZenModeSettingsBase.DEBUG) {
            Log.d("PrefControllerMixin", "onPrefChange allowSystem=" + booleanValue);
        }
        this.mMetricsFeatureProvider.action(this.mContext, 1340, booleanValue);
        this.mBackend.saveSoundPolicy(128, booleanValue);
        return true;
    }
}
