package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.service.notification.ZenPolicy;
import android.util.Pair;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.zen.ZenCustomRadioButtonPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenRuleVisEffectsAllPreferenceController extends AbstractZenCustomRulePreferenceController implements PreferenceControllerMixin {
    private ZenCustomRadioButtonPreference mPreference;

    public ZenRuleVisEffectsAllPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ZenCustomRadioButtonPreference zenCustomRadioButtonPreference = (ZenCustomRadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = zenCustomRadioButtonPreference;
        zenCustomRadioButtonPreference.setOnRadioButtonClickListener(new ZenCustomRadioButtonPreference.OnRadioButtonClickListener() {
            /* class com.android.settings.notification.zen.$$Lambda$ZenRuleVisEffectsAllPreferenceController$3xalYxBD7dJ8ZV3pz7H5iRuR_8c */

            @Override // com.android.settings.notification.zen.ZenCustomRadioButtonPreference.OnRadioButtonClickListener
            public final void onRadioButtonClick(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
                ZenRuleVisEffectsAllPreferenceController.this.lambda$displayPreference$0$ZenRuleVisEffectsAllPreferenceController(zenCustomRadioButtonPreference);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$ZenRuleVisEffectsAllPreferenceController(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
        this.mMetricsFeatureProvider.action(this.mContext, 1396, Pair.create(1603, this.mId));
        this.mRule.setZenPolicy(new ZenPolicy.Builder(this.mRule.getZenPolicy()).showAllVisualEffects().build());
        this.mBackend.updateZenRule(this.mId, this.mRule);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public void updateState(Preference preference) {
        AutomaticZenRule automaticZenRule;
        super.updateState(preference);
        if (this.mId != null && (automaticZenRule = this.mRule) != null && automaticZenRule.getZenPolicy() != null) {
            this.mPreference.setChecked(this.mRule.getZenPolicy().shouldShowAllVisualEffects());
        }
    }
}
