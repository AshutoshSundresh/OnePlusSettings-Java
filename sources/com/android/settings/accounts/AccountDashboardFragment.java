package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableData;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.users.AutoSyncDataPreferenceController;
import com.android.settings.users.AutoSyncPersonalDataPreferenceController;
import com.android.settings.users.AutoSyncWorkDataPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.List;

public class AccountDashboardFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.accounts_dashboard_settings) {
        /* class com.android.settings.accounts.AccountDashboardFragment.AnonymousClass1 */

        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return AccountDashboardFragment.buildPreferenceControllers(context, null, null);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableRaw> getDynamicRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            for (UserInfo userInfo : ((UserManager) context.getSystemService("user")).getProfiles(UserHandle.myUserId())) {
                if (userInfo.isManagedProfile()) {
                    return arrayList;
                }
            }
            Account[] accounts = AccountManager.get(context).getAccounts();
            for (Account account : accounts) {
                SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
                ((SearchIndexableData) searchIndexableRaw).key = AccountTypePreference.buildKey(account);
                searchIndexableRaw.title = account.name;
                arrayList.add(searchIndexableRaw);
            }
            return arrayList;
        }
    };

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AccountDashboardFrag";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 8;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.accounts_dashboard_settings;
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

    /* access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, SettingsPreferenceFragment settingsPreferenceFragment, String[] strArr) {
        ArrayList arrayList = new ArrayList();
        AccountPreferenceController accountPreferenceController = new AccountPreferenceController(context, settingsPreferenceFragment, strArr, 3);
        if (settingsPreferenceFragment != null) {
            settingsPreferenceFragment.getSettingsLifecycle().addObserver(accountPreferenceController);
        }
        arrayList.add(accountPreferenceController);
        arrayList.add(new AutoSyncDataPreferenceController(context, settingsPreferenceFragment));
        arrayList.add(new AutoSyncPersonalDataPreferenceController(context, settingsPreferenceFragment));
        arrayList.add(new AutoSyncWorkDataPreferenceController(context, settingsPreferenceFragment));
        return arrayList;
    }
}
