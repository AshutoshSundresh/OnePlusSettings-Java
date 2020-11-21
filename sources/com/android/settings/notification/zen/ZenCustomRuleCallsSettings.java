package com.android.settings.notification.zen;

import android.content.Context;
import android.os.Bundle;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ZenCustomRuleCallsSettings extends ZenCustomRuleSettingsBase {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1611;
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    public String getPreferenceCategoryKey() {
        return "zen_mode_settings_category_calls";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.notification.zen.ZenModeSettingsBase, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.zen_mode_custom_rule_calls_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((ZenCustomRuleSettingsBase) this).mControllers = arrayList;
        arrayList.add(new ZenRuleCallsPreferenceController(context, "zen_mode_calls", getSettingsLifecycle()));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleRepeatCallersPreferenceController(context, "zen_mode_repeat_callers", getSettingsLifecycle(), context.getResources().getInteger(17694927)));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleStarredContactsPreferenceController(context, getSettingsLifecycle(), 3, "zen_mode_starred_contacts_callers"));
        return ((ZenCustomRuleSettingsBase) this).mControllers;
    }

    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    public void updatePreferences() {
        super.updatePreferences();
        getPreferenceScreen().findPreference("footer_preference").setTitle(this.mContext.getResources().getString(C0017R$string.zen_mode_custom_calls_footer, this.mRule.getName()));
    }
}
