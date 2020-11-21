package com.android.settings.accessibility;

import android.os.Bundle;

public class ToggleScreenMagnificationPreferenceFragmentForSetupWizard extends ToggleScreenMagnificationPreferenceFragment {
    @Override // com.android.settings.accessibility.ToggleScreenMagnificationPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable, com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getMetricsCategory() {
        return 368;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        Bundle arguments = getArguments();
        if (!(arguments == null || !arguments.containsKey("checked") || this.mToggleServiceDividerSwitchPreference.isChecked() == arguments.getBoolean("checked"))) {
            this.mMetricsFeatureProvider.action(getContext(), 368, this.mToggleServiceDividerSwitchPreference.isChecked());
        }
        super.onStop();
    }
}
