package com.android.settings.dream;

import android.content.Context;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.dream.DreamBackend;
import java.util.ArrayList;
import java.util.List;

public class DreamSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.dream_fragment_overview) {
        /* class com.android.settings.dream.DreamSettings.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return DreamSettings.buildPreferenceControllers(context);
        }
    };

    static String getKeyFromSetting(int i) {
        return i != 0 ? i != 1 ? i != 2 ? "never" : "either_charging_or_docked" : "while_docked_only" : "while_charging_only";
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "DreamSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 47;
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    static int getSettingFromPrefKey(String str) {
        char c;
        switch (str.hashCode()) {
            case -1592701525:
                if (str.equals("while_docked_only")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -294641318:
                if (str.equals("either_charging_or_docked")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 104712844:
                if (str.equals("never")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1019349036:
                if (str.equals("while_charging_only")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0) {
            return 0;
        }
        if (c != 1) {
            return c != 2 ? 3 : 2;
        }
        return 1;
    }

    static int getDreamSettingDescriptionResId(int i) {
        if (i == 0) {
            return C0017R$string.screensaver_settings_summary_sleep;
        }
        if (i == 1) {
            return C0017R$string.screensaver_settings_summary_dock;
        }
        if (i != 2) {
            return C0017R$string.screensaver_settings_summary_never;
        }
        return C0017R$string.screensaver_settings_summary_either_long;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.dream_fragment_overview;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_screen_saver;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context);
    }

    public static CharSequence getSummaryTextWithDreamName(Context context) {
        return getSummaryTextFromBackend(DreamBackend.getInstance(context), context);
    }

    static CharSequence getSummaryTextFromBackend(DreamBackend dreamBackend, Context context) {
        if (!dreamBackend.isEnabled()) {
            return context.getString(C0017R$string.screensaver_settings_summary_off);
        }
        return dreamBackend.getActiveDreamName();
    }

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new WhenToDreamPreferenceController(context));
        arrayList.add(new StartNowPreferenceController(context));
        return arrayList;
    }
}
