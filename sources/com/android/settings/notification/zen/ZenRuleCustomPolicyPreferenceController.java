package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.service.notification.ZenPolicy;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.zen.ZenCustomRadioButtonPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenRuleCustomPolicyPreferenceController extends AbstractZenCustomRulePreferenceController {
    private ZenCustomRadioButtonPreference mPreference;

    public ZenRuleCustomPolicyPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ZenCustomRadioButtonPreference zenCustomRadioButtonPreference = (ZenCustomRadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = zenCustomRadioButtonPreference;
        zenCustomRadioButtonPreference.setOnGearClickListener(new ZenCustomRadioButtonPreference.OnGearClickListener() {
            /* class com.android.settings.notification.zen.$$Lambda$ZenRuleCustomPolicyPreferenceController$68YWVR_aA2gsLEr_OrC4U05Jq5A */

            @Override // com.android.settings.notification.zen.ZenCustomRadioButtonPreference.OnGearClickListener
            public final void onGearClick(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
                ZenRuleCustomPolicyPreferenceController.this.lambda$displayPreference$0$ZenRuleCustomPolicyPreferenceController(zenCustomRadioButtonPreference);
            }
        });
        this.mPreference.setOnRadioButtonClickListener(new ZenCustomRadioButtonPreference.OnRadioButtonClickListener() {
            /* class com.android.settings.notification.zen.$$Lambda$ZenRuleCustomPolicyPreferenceController$k5C7dENqChFhKqRhtwG8Tl8gNSE */

            @Override // com.android.settings.notification.zen.ZenCustomRadioButtonPreference.OnRadioButtonClickListener
            public final void onRadioButtonClick(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
                ZenRuleCustomPolicyPreferenceController.this.lambda$displayPreference$1$ZenRuleCustomPolicyPreferenceController(zenCustomRadioButtonPreference);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$ZenRuleCustomPolicyPreferenceController(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
        setCustomPolicy();
        launchCustomSettings();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$1 */
    public /* synthetic */ void lambda$displayPreference$1$ZenRuleCustomPolicyPreferenceController(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
        setCustomPolicy();
        launchCustomSettings();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public void updateState(Preference preference) {
        AutomaticZenRule automaticZenRule;
        super.updateState(preference);
        if (this.mId != null && (automaticZenRule = this.mRule) != null) {
            this.mPreference.setChecked(automaticZenRule.getZenPolicy() != null);
        }
    }

    private void setCustomPolicy() {
        if (this.mRule.getZenPolicy() == null) {
            this.mRule.setZenPolicy(this.mBackend.setDefaultZenPolicy(new ZenPolicy()));
            this.mBackend.updateZenRule(this.mId, this.mRule);
        }
    }

    private void launchCustomSettings() {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(ZenCustomRuleConfigSettings.class.getName());
        subSettingLauncher.setArguments(createBundle());
        subSettingLauncher.setSourceMetricsCategory(1605);
        subSettingLauncher.launch();
    }
}
