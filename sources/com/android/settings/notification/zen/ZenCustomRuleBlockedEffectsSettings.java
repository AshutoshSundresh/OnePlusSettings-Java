package com.android.settings.notification.zen;

import android.content.Context;
import android.os.Bundle;
import com.android.settings.C0019R$xml;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ZenCustomRuleBlockedEffectsSettings extends ZenCustomRuleSettingsBase {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1609;
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    public String getPreferenceCategoryKey() {
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.notification.zen.ZenModeSettingsBase, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.zen_mode_block_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((ZenCustomRuleSettingsBase) this).mControllers = arrayList;
        arrayList.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_intent", 0, 1332, null));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_light", 1, 1333, null));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_peek", 2, 1334, null));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_status", 3, 1335, new int[]{6}));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_badge", 4, 1336, null));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_ambient", 5, 1337, null));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectPreferenceController(context, getSettingsLifecycle(), "zen_effect_list", 6, 1338, null));
        return ((ZenCustomRuleSettingsBase) this).mControllers;
    }
}
