package com.android.settings.accessibility;

import android.os.Bundle;
import android.view.View;
import com.android.settings.C0017R$string;
import com.android.settings.accessibility.ToggleFeaturePreferenceFragment;
import com.google.common.collect.ImmutableSet;

public class VolumeShortcutToggleAccessibilityServicePreferenceFragment extends ToggleAccessibilityServicePreferenceFragment {
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mShortcutPreference.setSummary(getPrefContext().getText(C0017R$string.accessibility_shortcut_edit_dialog_title_hardware));
        this.mShortcutPreference.setSettingsEditable(false);
        setAllowedPreferredShortcutType(2);
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment
    public int getUserShortcutTypes() {
        int userShortcutTypes = super.getUserShortcutTypes();
        return (!((getAccessibilityServiceInfo().flags & 256) != 0) || !getArguments().getBoolean("checked")) ? userShortcutTypes & -2 : userShortcutTypes | 1;
    }

    private void setAllowedPreferredShortcutType(int i) {
        SharedPreferenceUtils.setUserShortcutType(getPrefContext(), ImmutableSet.of(new ToggleFeaturePreferenceFragment.AccessibilityUserShortcutType(this.mComponentName.flattenToString(), i).flattenToString()));
    }
}
