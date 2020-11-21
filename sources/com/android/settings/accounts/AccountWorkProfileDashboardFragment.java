package com.android.settings.accounts;

import android.content.Context;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.users.AutoSyncDataPreferenceController;
import com.android.settings.users.AutoSyncWorkDataPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class AccountWorkProfileDashboardFragment extends DashboardFragment {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AccountWorkProfileFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1807;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.accounts_work_dashboard_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_user_and_account_dashboard;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this, getIntent().getStringArrayExtra("authorities"));
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context, SettingsPreferenceFragment settingsPreferenceFragment, String[] strArr) {
        ArrayList arrayList = new ArrayList();
        AccountPreferenceController accountPreferenceController = new AccountPreferenceController(context, settingsPreferenceFragment, strArr, 2);
        if (settingsPreferenceFragment != null) {
            settingsPreferenceFragment.getSettingsLifecycle().addObserver(accountPreferenceController);
        }
        arrayList.add(accountPreferenceController);
        arrayList.add(new AutoSyncDataPreferenceController(context, settingsPreferenceFragment));
        arrayList.add(new AutoSyncWorkDataPreferenceController(context, settingsPreferenceFragment));
        return arrayList;
    }
}
