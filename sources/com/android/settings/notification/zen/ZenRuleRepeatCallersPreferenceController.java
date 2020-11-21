package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.service.notification.ZenPolicy;
import android.util.Log;
import android.util.Pair;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenRuleRepeatCallersPreferenceController extends AbstractZenCustomRulePreferenceController implements Preference.OnPreferenceChangeListener {
    private final int mRepeatCallersThreshold;

    public ZenRuleRepeatCallersPreferenceController(Context context, String str, Lifecycle lifecycle, int i) {
        super(context, str, lifecycle);
        this.mRepeatCallersThreshold = i;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        setRepeatCallerSummary(preferenceScreen.findPreference(this.KEY));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        AutomaticZenRule automaticZenRule = this.mRule;
        if (automaticZenRule != null && automaticZenRule.getZenPolicy() != null) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            boolean z = false;
            if (this.mRule.getZenPolicy().getPriorityCallSenders() == 1) {
                switchPreference.setEnabled(false);
                switchPreference.setChecked(true);
                return;
            }
            switchPreference.setEnabled(true);
            if (this.mRule.getZenPolicy().getPriorityCategoryRepeatCallers() == 1) {
                z = true;
            }
            switchPreference.setChecked(z);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (ZenModeSettingsBase.DEBUG) {
            Log.d("PrefControllerMixin", this.KEY + " onPrefChange mRule=" + this.mRule + " mCategory=4 allow=" + booleanValue);
        }
        this.mMetricsFeatureProvider.action(this.mContext, 171, Pair.create(1602, Integer.valueOf(booleanValue ? 1 : 0)), Pair.create(1603, this.mId));
        this.mRule.setZenPolicy(new ZenPolicy.Builder(this.mRule.getZenPolicy()).allowRepeatCallers(booleanValue).build());
        this.mBackend.updateZenRule(this.mId, this.mRule);
        return true;
    }

    private void setRepeatCallerSummary(Preference preference) {
        preference.setSummary(this.mContext.getString(C0017R$string.zen_mode_repeat_callers_summary, Integer.valueOf(this.mRepeatCallersThreshold)));
    }
}
