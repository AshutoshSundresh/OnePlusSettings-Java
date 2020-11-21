package com.android.settings.notification.zen;

import android.app.NotificationManager;
import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.notification.OPZenNoDividerCustomRadioButtonPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenModeVisEffectsAllPreferenceController extends AbstractZenModePreferenceController implements OPZenNoDividerCustomRadioButtonPreference.OnRadioButtonClickListener {
    private OPZenNoDividerCustomRadioButtonPreference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeVisEffectsAllPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        OPZenNoDividerCustomRadioButtonPreference oPZenNoDividerCustomRadioButtonPreference = (OPZenNoDividerCustomRadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = oPZenNoDividerCustomRadioButtonPreference;
        oPZenNoDividerCustomRadioButtonPreference.setOnRadioButtonClickListener(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference.setChecked(NotificationManager.Policy.areAllVisualEffectsSuppressed(this.mBackend.mPolicy.suppressedVisualEffects));
    }

    @Override // com.android.settings.notification.OPZenNoDividerCustomRadioButtonPreference.OnRadioButtonClickListener
    public void onRadioButtonClick(OPZenNoDividerCustomRadioButtonPreference oPZenNoDividerCustomRadioButtonPreference) {
        this.mMetricsFeatureProvider.action(this.mContext, 1397, true);
        this.mBackend.saveVisualEffectsPolicy(511, true);
    }
}
