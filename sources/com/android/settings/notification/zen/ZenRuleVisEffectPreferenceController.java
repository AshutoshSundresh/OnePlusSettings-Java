package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.service.notification.ZenPolicy;
import android.util.Pair;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.widget.DisabledCheckBoxPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenRuleVisEffectPreferenceController extends AbstractZenCustomRulePreferenceController implements Preference.OnPreferenceChangeListener {
    protected int mEffect;
    private final int mMetricsCategory;
    protected int[] mParentSuppressedEffects;

    public ZenRuleVisEffectPreferenceController(Context context, Lifecycle lifecycle, String str, int i, int i2, int[] iArr) {
        super(context, str, lifecycle);
        this.mEffect = i;
        this.mMetricsCategory = i2;
        this.mParentSuppressedEffects = iArr;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public boolean isAvailable() {
        if (!super.isAvailable()) {
            return false;
        }
        if (this.mEffect == 1) {
            return this.mContext.getResources().getBoolean(17891478);
        }
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public void updateState(Preference preference) {
        boolean z;
        super.updateState(preference);
        AutomaticZenRule automaticZenRule = this.mRule;
        if (!(automaticZenRule == null || automaticZenRule.getZenPolicy() == null)) {
            boolean z2 = !this.mRule.getZenPolicy().isVisualEffectAllowed(this.mEffect, false);
            int[] iArr = this.mParentSuppressedEffects;
            if (iArr != null) {
                z = false;
                for (int i : iArr) {
                    if (!this.mRule.getZenPolicy().isVisualEffectAllowed(i, true)) {
                        z = true;
                    }
                }
            } else {
                z = false;
            }
            if (z) {
                ((CheckBoxPreference) preference).setChecked(z);
                onPreferenceChange(preference, Boolean.valueOf(z));
                ((DisabledCheckBoxPreference) preference).enableCheckbox(false);
                return;
            }
            ((DisabledCheckBoxPreference) preference).enableCheckbox(true);
            ((CheckBoxPreference) preference).setChecked(z2);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        this.mMetricsFeatureProvider.action(this.mContext, this.mMetricsCategory, Pair.create(1602, Integer.valueOf(booleanValue ? 1 : 0)), Pair.create(1603, this.mId));
        this.mRule.setZenPolicy(new ZenPolicy.Builder(this.mRule.getZenPolicy()).showVisualEffect(this.mEffect, !booleanValue ? 1 : 0).build());
        this.mBackend.updateZenRule(this.mId, this.mRule);
        return true;
    }
}
