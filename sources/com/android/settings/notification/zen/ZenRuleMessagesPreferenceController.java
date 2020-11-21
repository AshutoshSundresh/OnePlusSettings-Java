package com.android.settings.notification.zen;

import android.app.AutomaticZenRule;
import android.content.Context;
import android.service.notification.ZenPolicy;
import android.text.TextUtils;
import android.util.Pair;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settingslib.core.lifecycle.Lifecycle;

public class ZenRuleMessagesPreferenceController extends AbstractZenCustomRulePreferenceController implements Preference.OnPreferenceChangeListener {
    private final String[] mListValues;

    public ZenRuleMessagesPreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str, lifecycle);
        this.mListValues = context.getResources().getStringArray(C0003R$array.zen_mode_contacts_values);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenCustomRulePreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        updateFromContactsValue(preference);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int zenPolicySettingFromPrefKey = ZenModeBackend.getZenPolicySettingFromPrefKey(obj.toString());
        this.mMetricsFeatureProvider.action(this.mContext, 169, Pair.create(1602, Integer.valueOf(zenPolicySettingFromPrefKey)), Pair.create(1603, this.mId));
        this.mRule.setZenPolicy(new ZenPolicy.Builder(this.mRule.getZenPolicy()).allowMessages(zenPolicySettingFromPrefKey).build());
        this.mBackend.updateZenRule(this.mId, this.mRule);
        updateFromContactsValue(preference);
        return true;
    }

    private void updateFromContactsValue(Preference preference) {
        AutomaticZenRule automaticZenRule = this.mRule;
        if (automaticZenRule != null && automaticZenRule.getZenPolicy() != null) {
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(this.mBackend.getContactsMessagesSummary(this.mRule.getZenPolicy()));
            listPreference.setValue(this.mListValues[getIndexOfSendersValue(ZenModeBackend.getKeyFromZenPolicySetting(this.mRule.getZenPolicy().getPriorityMessageSenders()))]);
        }
    }

    /* access modifiers changed from: protected */
    public int getIndexOfSendersValue(String str) {
        int i = 0;
        while (true) {
            String[] strArr = this.mListValues;
            if (i >= strArr.length) {
                return 3;
            }
            if (TextUtils.equals(str, strArr[i])) {
                return i;
            }
            i++;
        }
    }
}
