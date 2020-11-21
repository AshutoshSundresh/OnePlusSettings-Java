package com.android.settings.accessibility;

import android.os.Bundle;
import android.view.View;

public class VolumeShortcutToggleScreenReaderPreferenceFragmentForSetupWizard extends VolumeShortcutToggleAccessibilityServicePreferenceFragment {
    private boolean mToggleSwitchWasInitiallyChecked;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment
    public int getMetricsCategory() {
        return 371;
    }

    @Override // com.android.settings.accessibility.VolumeShortcutToggleAccessibilityServicePreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mToggleSwitchWasInitiallyChecked = this.mToggleServiceDividerSwitchPreference.isChecked();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        if (this.mToggleServiceDividerSwitchPreference.isChecked() != this.mToggleSwitchWasInitiallyChecked) {
            this.mMetricsFeatureProvider.action(getContext(), 371, this.mToggleServiceDividerSwitchPreference.isChecked());
        }
        super.onStop();
    }
}
