package com.android.settings.notification.zen;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.notification.zen.ZenRuleSelectionDialog;
import com.android.settings.utils.ZenServiceListing;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenModeAddAutomaticRulePreferenceController extends AbstractZenModeAutomaticRulePreferenceController implements Preference.OnPreferenceClickListener {
    private final ZenServiceListing mZenServiceListing;

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return "zen_mode_add_automatic_rule";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public ZenModeAddAutomaticRulePreferenceController(Context context, Fragment fragment, ZenServiceListing zenServiceListing, Lifecycle lifecycle) {
        super(context, "zen_mode_add_automatic_rule", fragment, lifecycle);
        this.mZenServiceListing = zenServiceListing;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference("zen_mode_add_automatic_rule");
        findPreference.setPersistent(false);
        findPreference.setOnPreferenceClickListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        ZenRuleSelectionDialog.show(this.mContext, this.mParent, new RuleSelectionListener(), this.mZenServiceListing);
        return true;
    }

    public class RuleSelectionListener implements ZenRuleSelectionDialog.PositiveClickListener {
        public RuleSelectionListener() {
        }

        @Override // com.android.settings.notification.zen.ZenRuleSelectionDialog.PositiveClickListener
        public void onSystemRuleSelected(ZenRuleInfo zenRuleInfo, Fragment fragment) {
            ZenModeAddAutomaticRulePreferenceController.this.showNameRuleDialog(zenRuleInfo, fragment);
        }

        @Override // com.android.settings.notification.zen.ZenRuleSelectionDialog.PositiveClickListener
        public void onExternalRuleSelected(ZenRuleInfo zenRuleInfo, Fragment fragment) {
            fragment.startActivity(new Intent().setComponent(zenRuleInfo.configurationActivity));
        }
    }
}
