package com.android.settings.accessibility;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.accessibility.AccessibilityTimeoutController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class AccessibilityControlTimeoutPreferenceFragment extends DashboardFragment implements AccessibilityTimeoutController.OnChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.accessibility_control_timeout_settings) {
        /* class com.android.settings.accessibility.AccessibilityControlTimeoutPreferenceFragment.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return AccessibilityControlTimeoutPreferenceFragment.buildPreferenceControllers(context, null);
        }
    };
    private static final List<AbstractPreferenceController> sControllers = new ArrayList();

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AccessibilityControlTimeoutPreferenceFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 2;
    }

    @Override // com.android.settings.accessibility.AccessibilityTimeoutController.OnChangeListener
    public void onCheckedChanged(Preference preference) {
        for (AbstractPreferenceController abstractPreferenceController : sControllers) {
            abstractPreferenceController.updateState(preference);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        Iterator<AbstractPreferenceController> it = buildPreferenceControllers(getPrefContext(), getSettingsLifecycle()).iterator();
        while (it.hasNext()) {
            ((AccessibilityTimeoutController) it.next()).setOnChangeListener(this);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        Iterator<AbstractPreferenceController> it = buildPreferenceControllers(getPrefContext(), getSettingsLifecycle()).iterator();
        while (it.hasNext()) {
            ((AccessibilityTimeoutController) it.next()).setOnChangeListener(null);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.accessibility_control_timeout_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_timeout;
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        String[] stringArray;
        if (sControllers.size() == 0) {
            for (String str : context.getResources().getStringArray(C0003R$array.accessibility_timeout_control_selector_keys)) {
                sControllers.add(new AccessibilityTimeoutController(context, lifecycle, str));
            }
        }
        return sControllers;
    }
}
