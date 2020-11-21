package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.util.Pair;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.zen.ZenCustomRadioButtonPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenRuleVisEffectsCustomPreferenceController extends AbstractZenCustomRulePreferenceController implements PreferenceControllerMixin {
    private ZenCustomRadioButtonPreference mPreference;

    public ZenRuleVisEffectsCustomPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ZenCustomRadioButtonPreference zenCustomRadioButtonPreference = (ZenCustomRadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = zenCustomRadioButtonPreference;
        zenCustomRadioButtonPreference.setOnGearClickListener(new ZenCustomRadioButtonPreference.OnGearClickListener() {
            /* class com.android.settings.notification.zen.$$Lambda$ZenRuleVisEffectsCustomPreferenceController$OwvDj0Zf72QFBjfNxuAcTL4SImg */

            @Override // com.android.settings.notification.zen.ZenCustomRadioButtonPreference.OnGearClickListener
            public final void onGearClick(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
                ZenRuleVisEffectsCustomPreferenceController.this.lambda$displayPreference$0$ZenRuleVisEffectsCustomPreferenceController(zenCustomRadioButtonPreference);
            }
        });
        this.mPreference.setOnRadioButtonClickListener(new ZenCustomRadioButtonPreference.OnRadioButtonClickListener() {
            /* class com.android.settings.notification.zen.$$Lambda$ZenRuleVisEffectsCustomPreferenceController$tEtVFSCcqdJWJ7n1QQy5jNo5s */

            @Override // com.android.settings.notification.zen.ZenCustomRadioButtonPreference.OnRadioButtonClickListener
            public final void onRadioButtonClick(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
                ZenRuleVisEffectsCustomPreferenceController.this.lambda$displayPreference$1$ZenRuleVisEffectsCustomPreferenceController(zenCustomRadioButtonPreference);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$ZenRuleVisEffectsCustomPreferenceController(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
        launchCustomSettings();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$1 */
    public /* synthetic */ void lambda$displayPreference$1$ZenRuleVisEffectsCustomPreferenceController(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
        launchCustomSettings();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public void updateState(Preference preference) {
        AutomaticZenRule automaticZenRule;
        super.updateState(preference);
        if (this.mId != null && (automaticZenRule = this.mRule) != null && automaticZenRule.getZenPolicy() != null) {
            this.mPreference.setChecked(!this.mRule.getZenPolicy().shouldHideAllVisualEffects() && !this.mRule.getZenPolicy().shouldShowAllVisualEffects());
        }
    }

    private void launchCustomSettings() {
        this.mMetricsFeatureProvider.action(this.mContext, 1398, Pair.create(1603, this.mId));
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ZenCustomRuleBlockedEffectsSettings.class.getName());
        subSettingLauncher.setArguments(createBundle());
        subSettingLauncher.setSourceMetricsCategory(1609);
        subSettingLauncher.launch();
    }
}
