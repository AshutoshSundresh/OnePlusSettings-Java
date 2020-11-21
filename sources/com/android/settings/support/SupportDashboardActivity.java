package com.android.settings.support;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableData;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.overlay.SupportFeatureProvider;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.List;

public class SupportDashboardActivity extends Activity {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.support.SupportDashboardActivity.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
            searchIndexableRaw.title = context.getString(C0017R$string.page_tab_title_support);
            searchIndexableRaw.screenTitle = context.getString(C0017R$string.page_tab_title_support);
            searchIndexableRaw.summaryOn = context.getString(C0017R$string.support_summary);
            ((SearchIndexableData) searchIndexableRaw).intentTargetPackage = context.getPackageName();
            ((SearchIndexableData) searchIndexableRaw).intentTargetClass = SupportDashboardActivity.class.getName();
            ((SearchIndexableData) searchIndexableRaw).intentAction = "android.intent.action.MAIN";
            ((SearchIndexableData) searchIndexableRaw).key = "support_dashboard_activity";
            arrayList.add(searchIndexableRaw);
            return arrayList;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!context.getResources().getBoolean(C0005R$bool.config_support_enabled)) {
                nonIndexableKeys.add("support_dashboard_activity");
            }
            return nonIndexableKeys;
        }
    };

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SupportFeatureProvider supportFeatureProvider = FeatureFactory.getFactory(this).getSupportFeatureProvider(this);
        if (supportFeatureProvider != null) {
            supportFeatureProvider.startSupport(this);
            finish();
        }
    }
}
