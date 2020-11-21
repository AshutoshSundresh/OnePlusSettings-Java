package com.android.settings.widget;

import androidx.preference.Preference;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.SwitchWidgetController;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

public class MasterSwitchController extends SwitchWidgetController implements Preference.OnPreferenceChangeListener {
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final MasterSwitchPreference mPreference;

    @Override // com.android.settings.widget.SwitchWidgetController
    public void updateTitle(boolean z) {
    }

    public MasterSwitchController(MasterSwitchPreference masterSwitchPreference) {
        this.mPreference = masterSwitchPreference;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(masterSwitchPreference.getContext()).getMetricsFeatureProvider();
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void startListening() {
        this.mPreference.setOnPreferenceChangeListener(this);
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void stopListening() {
        this.mPreference.setOnPreferenceChangeListener(null);
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setChecked(boolean z) {
        this.mPreference.setChecked(z);
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public boolean isChecked() {
        return this.mPreference.isChecked();
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setEnabled(boolean z) {
        this.mPreference.setSwitchEnabled(z);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        SwitchWidgetController.OnSwitchChangeListener onSwitchChangeListener = this.mListener;
        if (onSwitchChangeListener == null) {
            return false;
        }
        boolean onSwitchToggled = onSwitchChangeListener.onSwitchToggled(((Boolean) obj).booleanValue());
        if (onSwitchToggled) {
            this.mMetricsFeatureProvider.logClickedPreference(preference, preference.getExtras().getInt("category"));
        }
        return onSwitchToggled;
    }

    @Override // com.android.settings.widget.SwitchWidgetController
    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mPreference.setDisabledByAdmin(enforcedAdmin);
    }
}
