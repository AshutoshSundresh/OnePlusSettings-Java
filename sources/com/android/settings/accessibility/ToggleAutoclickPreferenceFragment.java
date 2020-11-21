package com.android.settings.accessibility;

import android.content.Context;
import android.content.res.Resources;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.accessibility.ToggleAutoclickPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ToggleAutoclickPreferenceFragment extends DashboardFragment implements ToggleAutoclickPreferenceController.OnChangeListener {
    private static final int[] AUTOCLICK_PREFERENCE_SUMMARIES = {C0015R$plurals.accessibilty_autoclick_preference_subtitle_short_delay, C0015R$plurals.accessibilty_autoclick_preference_subtitle_medium_delay, C0015R$plurals.accessibilty_autoclick_preference_subtitle_long_delay};
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.accessibility_autoclick_settings) {
        /* class com.android.settings.accessibility.ToggleAutoclickPreferenceFragment.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return ToggleAutoclickPreferenceFragment.buildPreferenceControllers(context, null);
        }
    };
    private static final List<AbstractPreferenceController> sControllers = new ArrayList();

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AutoclickPrefFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 335;
    }

    static CharSequence getAutoclickPreferenceSummary(Resources resources, int i) {
        int autoclickPreferenceSummaryIndex = getAutoclickPreferenceSummaryIndex(i);
        int i2 = i == 1000 ? 1 : 3;
        float f = ((float) i) / 1000.0f;
        return resources.getQuantityString(AUTOCLICK_PREFERENCE_SUMMARIES[autoclickPreferenceSummaryIndex], i2, String.format(f == 1.0f ? "%.0f" : "%.1f", Float.valueOf(f)));
    }

    private static int getAutoclickPreferenceSummaryIndex(int i) {
        if (i <= 200) {
            return 0;
        }
        if (i >= 1000) {
            return AUTOCLICK_PREFERENCE_SUMMARIES.length - 1;
        }
        return (i - 200) / (800 / (AUTOCLICK_PREFERENCE_SUMMARIES.length - 1));
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_autoclick;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.accessibility_autoclick_settings;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        Iterator<AbstractPreferenceController> it = sControllers.iterator();
        while (it.hasNext()) {
            ((ToggleAutoclickPreferenceController) it.next()).setOnChangeListener(this);
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        super.onPause();
        Iterator<AbstractPreferenceController> it = sControllers.iterator();
        while (it.hasNext()) {
            ((ToggleAutoclickPreferenceController) it.next()).setOnChangeListener(null);
        }
    }

    @Override // com.android.settings.accessibility.ToggleAutoclickPreferenceController.OnChangeListener
    public void onCheckedChanged(Preference preference) {
        for (AbstractPreferenceController abstractPreferenceController : sControllers) {
            abstractPreferenceController.updateState(preference);
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        for (String str : context.getResources().getStringArray(C0003R$array.accessibility_autoclick_control_selector_keys)) {
            sControllers.add(new ToggleAutoclickPreferenceController(context, lifecycle, str));
        }
        return sControllers;
    }
}
