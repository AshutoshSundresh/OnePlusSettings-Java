package com.android.settings.notification.zen;

import android.content.Context;
import android.os.Bundle;
import com.android.settings.C0019R$xml;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.List;

public class ZenModeBlockedEffectsSettings extends ZenModeSettingsBase {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.zen_mode_block_settings) {
        /* class com.android.settings.notification.zen.ZenModeBlockedEffectsSettings.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ZenModeBlockedEffectsSettings.buildPreferenceControllers(context, null);
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1339;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.notification.zen.ZenModeSettingsBase, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_intent", 4, 1332, null));
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_light", 8, 1333, null));
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_peek", 16, 1334, null));
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_status", 32, 1335, new int[]{256}));
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_badge", 64, 1336, null));
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_ambient", 128, 1337, null));
        arrayList.add(new ZenModeVisEffectPreferenceController(context, lifecycle, "zen_effect_list", 256, 1338, null));
        return arrayList;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.zen_mode_block_settings;
    }
}
