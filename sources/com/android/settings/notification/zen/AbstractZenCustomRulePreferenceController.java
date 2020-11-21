package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.os.Bundle;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.lifecycle.Lifecycle;

abstract class AbstractZenCustomRulePreferenceController extends AbstractZenModePreferenceController implements PreferenceControllerMixin {
    String mId;
    AutomaticZenRule mRule;

    AbstractZenCustomRulePreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str, lifecycle);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        String str = this.mId;
        if (str != null) {
            this.mRule = this.mBackend.getAutomaticZenRule(str);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mRule != null;
    }

    public void onResume(AutomaticZenRule automaticZenRule, String str) {
        this.mId = str;
        this.mRule = automaticZenRule;
    }

    /* access modifiers changed from: package-private */
    public Bundle createBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("RULE_ID", this.mId);
        return bundle;
    }
}
