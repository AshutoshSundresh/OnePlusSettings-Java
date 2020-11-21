package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.widget.Switch;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;

public class ZenAutomaticRuleSwitchPreferenceController extends AbstractZenModeAutomaticRulePreferenceController implements SwitchBar.OnSwitchChangeListener {
    private String mId;
    private AutomaticZenRule mRule;
    private SwitchBar mSwitchBar;

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return "zen_automatic_rule_switch";
    }

    public ZenAutomaticRuleSwitchPreferenceController(Context context, Fragment fragment, Lifecycle lifecycle) {
        super(context, "zen_automatic_rule_switch", fragment, lifecycle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return (this.mRule == null || this.mId == null) ? false : true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference("zen_automatic_rule_switch");
        SwitchBar switchBar = (SwitchBar) layoutPreference.findViewById(C0010R$id.switch_bar);
        this.mSwitchBar = switchBar;
        if (switchBar != null) {
            int i = C0017R$string.zen_mode_use_automatic_rule;
            switchBar.setSwitchBarText(i, i);
            try {
                layoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    /* class com.android.settings.notification.zen.$$Lambda$ZenAutomaticRuleSwitchPreferenceController$2c3mDHth7WKiFbClJLjtBDPkQU */

                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return ZenAutomaticRuleSwitchPreferenceController.this.lambda$displayPreference$0$ZenAutomaticRuleSwitchPreferenceController(preference);
                    }
                });
                this.mSwitchBar.addOnSwitchChangeListener(this);
            } catch (IllegalStateException unused) {
            }
            this.mSwitchBar.show();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ boolean lambda$displayPreference$0$ZenAutomaticRuleSwitchPreferenceController(Preference preference) {
        AutomaticZenRule automaticZenRule = this.mRule;
        automaticZenRule.setEnabled(!automaticZenRule.isEnabled());
        ((AbstractZenModeAutomaticRulePreferenceController) this).mBackend.updateZenRule(this.mId, this.mRule);
        return true;
    }

    public void onResume(AutomaticZenRule automaticZenRule, String str) {
        this.mRule = automaticZenRule;
        this.mId = str;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModeAutomaticRulePreferenceController
    public void updateState(Preference preference) {
        AutomaticZenRule automaticZenRule = this.mRule;
        if (automaticZenRule != null) {
            this.mSwitchBar.setChecked(automaticZenRule.isEnabled());
        }
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        if (z != this.mRule.isEnabled()) {
            this.mRule.setEnabled(z);
            ((AbstractZenModeAutomaticRulePreferenceController) this).mBackend.updateZenRule(this.mId, this.mRule);
        }
    }
}
