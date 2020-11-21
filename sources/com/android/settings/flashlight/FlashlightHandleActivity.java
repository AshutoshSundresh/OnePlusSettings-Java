package com.android.settings.flashlight;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchIndexableData;
import com.android.settings.C0017R$string;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.List;

public class FlashlightHandleActivity extends Activity {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.flashlight.FlashlightHandleActivity.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
            searchIndexableRaw.title = context.getString(C0017R$string.power_flashlight);
            searchIndexableRaw.screenTitle = context.getString(C0017R$string.power_flashlight);
            searchIndexableRaw.keywords = context.getString(C0017R$string.keywords_flashlight);
            ((SearchIndexableData) searchIndexableRaw).intentTargetPackage = context.getPackageName();
            ((SearchIndexableData) searchIndexableRaw).intentTargetClass = FlashlightHandleActivity.class.getName();
            ((SearchIndexableData) searchIndexableRaw).intentAction = "android.intent.action.MAIN";
            ((SearchIndexableData) searchIndexableRaw).key = "flashlight";
            arrayList.add(searchIndexableRaw);
            return arrayList;
        }
    };

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getIntent().getBooleanExtra("fallback_to_homepage", false)) {
            startActivity(new Intent("android.settings.SETTINGS"));
        }
        finish();
    }
}
