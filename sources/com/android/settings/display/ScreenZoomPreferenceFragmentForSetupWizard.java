package com.android.settings.display;

public class ScreenZoomPreferenceFragmentForSetupWizard extends ScreenZoomSettings {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable, com.android.settings.display.ScreenZoomSettings
    public int getMetricsCategory() {
        return 370;
    }

    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        if (this.mCurrentIndex != this.mInitialIndex) {
            this.mMetricsFeatureProvider.action(getContext(), 370, this.mCurrentIndex);
        }
        super.onStop();
    }
}
