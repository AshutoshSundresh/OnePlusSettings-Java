package com.android.settings.notification.zen;

import android.content.Context;
import com.android.settings.C0019R$xml;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ZenCustomRuleNotificationsSettings extends ZenCustomRuleSettingsBase {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1608;
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.notification.zen.ZenCustomRuleSettingsBase
    public String getPreferenceCategoryKey() {
        return "restrict_category";
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.zen_mode_restrict_notifications_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ((ZenCustomRuleSettingsBase) this).mControllers = arrayList;
        arrayList.add(new ZenRuleVisEffectsAllPreferenceController(context, getSettingsLifecycle(), "zen_mute_notifications"));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectsNonePreferenceController(context, getSettingsLifecycle(), "zen_hide_notifications"));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleVisEffectsCustomPreferenceController(context, getSettingsLifecycle(), "zen_custom"));
        ((ZenCustomRuleSettingsBase) this).mControllers.add(new ZenRuleNotifFooterPreferenceController(context, getSettingsLifecycle(), "footer_preference"));
        return ((ZenCustomRuleSettingsBase) this).mControllers;
    }
}
