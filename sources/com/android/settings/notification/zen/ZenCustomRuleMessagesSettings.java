package com.android.settings.notification.zen;

import android.content.Context;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ZenCustomRuleMessagesSettings extends ZenCustomRuleSettingsBase {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1610;
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    public String getPreferenceCategoryKey() {
        return "zen_mode_settings_category_messages";
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.zen_mode_custom_rule_messages_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((ZenCustomRuleSettingsBase) this).mControllers = arrayList;
        arrayList.add(new ZenRuleMessagesPreferenceController(context, "zen_mode_messages", getSettingsLifecycle()));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleStarredContactsPreferenceController(context, getSettingsLifecycle(), 2, "zen_mode_starred_contacts_messages"));
        return ((ZenCustomRuleSettingsBase) this).mControllers;
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    public void updatePreferences() {
        super.updatePreferences();
        getPreferenceScreen().findPreference("footer_preference").setTitle(this.mContext.getResources().getString(C0017R$string.zen_mode_custom_messages_footer, this.mRule.getName()));
    }
}
