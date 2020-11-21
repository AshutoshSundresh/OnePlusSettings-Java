package com.android.settings.display;

public class FontSizePreferenceFragmentForSetupWizard extends ToggleFontSizePreferenceFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable, com.android.settings.display.ToggleFontSizePreferenceFragment
    public int getMetricsCategory() {
        return 369;
    }

    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        if (this.mCurrentIndex != this.mInitialIndex) {
            this.mMetricsFeatureProvider.action(getContext(), 369, this.mCurrentIndex);
        }
        super.onStop();
    }
}
