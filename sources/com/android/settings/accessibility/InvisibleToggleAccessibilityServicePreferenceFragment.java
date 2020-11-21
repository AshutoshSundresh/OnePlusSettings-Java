package com.android.settings.accessibility;

import android.content.DialogInterface;
import android.view.View;
import com.android.settings.C0010R$id;
import com.android.settings.accessibility.ShortcutPreference;
import com.android.settingslib.accessibility.AccessibilityUtils;

public class InvisibleToggleAccessibilityServicePreferenceFragment extends ToggleAccessibilityServicePreferenceFragment implements ShortcutPreference.OnClickCallback {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment
    public void onInstallSwitchPreferenceToggleSwitch() {
        super.onInstallSwitchPreferenceToggleSwitch();
        this.mToggleServiceDividerSwitchPreference.setVisible(false);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onToggleClicked(ShortcutPreference shortcutPreference) {
        super.onToggleClicked(shortcutPreference);
        AccessibilityUtils.setAccessibilityServiceState(getContext(), this.mComponentName, getArguments().getBoolean("checked") && shortcutPreference.isChecked());
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment
    public void onDialogButtonFromShortcutToggleClicked(View view) {
        super.onDialogButtonFromShortcutToggleClicked(view);
        if (view.getId() == C0010R$id.permission_enable_allow_button) {
            AccessibilityUtils.setAccessibilityServiceState(getContext(), this.mComponentName, true);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void callOnAlertDialogCheckboxClicked(DialogInterface dialogInterface, int i) {
        super.callOnAlertDialogCheckboxClicked(dialogInterface, i);
        AccessibilityUtils.setAccessibilityServiceState(getContext(), this.mComponentName, this.mShortcutPreference.isChecked());
    }
}
