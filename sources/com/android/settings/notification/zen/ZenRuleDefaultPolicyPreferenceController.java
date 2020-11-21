package com.android.settings.notification.zen;

import android.content.Context;
import android.util.Pair;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.zen.ZenCustomRadioButtonPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenRuleDefaultPolicyPreferenceController extends AbstractZenCustomRulePreferenceController implements PreferenceControllerMixin {
    private ZenCustomRadioButtonPreference mPreference;

    public ZenRuleDefaultPolicyPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ZenCustomRadioButtonPreference zenCustomRadioButtonPreference = (ZenCustomRadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = zenCustomRadioButtonPreference;
        zenCustomRadioButtonPreference.setOnRadioButtonClickListener(new ZenCustomRadioButtonPreference.OnRadioButtonClickListener() {
            /* class com.android.settings.notification.zen.$$Lambda$ZenRuleDefaultPolicyPreferenceController$ryRQFV0gp7xjFUTu4ldJZ1MttfI */

            @Override // com.android.settings.notification.zen.ZenCustomRadioButtonPreference.OnRadioButtonClickListener
            public final void onRadioButtonClick(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
                ZenRuleDefaultPolicyPreferenceController.this.lambda$displayPreference$0$ZenRuleDefaultPolicyPreferenceController(zenCustomRadioButtonPreference);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$ZenRuleDefaultPolicyPreferenceController(ZenCustomRadioButtonPreference zenCustomRadioButtonPreference) {
        this.mRule.setZenPolicy(null);
        this.mBackend.updateZenRule(this.mId, this.mRule);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mId != null && this.mRule != null) {
            boolean z = true;
            this.mMetricsFeatureProvider.action(this.mContext, 1606, Pair.create(1603, this.mId));
            ZenCustomRadioButtonPreference zenCustomRadioButtonPreference = this.mPreference;
            if (this.mRule.getZenPolicy() != null) {
                z = false;
            }
            zenCustomRadioButtonPreference.setChecked(z);
        }
    }
}
