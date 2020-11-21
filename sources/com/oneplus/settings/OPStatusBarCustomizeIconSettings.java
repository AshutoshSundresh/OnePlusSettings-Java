package com.oneplus.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.util.Log;
import android.util.OpFeatures;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPStatusBarCustomizeIconSettings extends SettingsPreferenceFragment {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new StatusBarCustomizeIndexProvider();
    private Context mContext;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_statusbar_customize_icon_settings);
        this.mContext = getActivity();
        ((SettingsActivity) getActivity()).setTitle(this.mContext.getResources().getString(C0017R$string.statusbar_icon_manager));
        customizePreferences();
    }

    private void customizePreferences() {
        boolean isSupportUstMode = OPUtils.isSupportUstMode();
        boolean isSupportUstMode2 = OPUtils.isSupportUstMode();
        boolean isSupport = true ^ OpFeatures.isSupport(new int[]{145});
        if (!isWLBPresent()) {
            Log.d("OPStatusBarCustomizeSettings", "hiding wlb icon preference");
            removePreference("wlb");
        }
        if (isSupportUstMode) {
            removePreference("vowifi");
        }
        if (isSupportUstMode2) {
            removePreference("volte");
        }
        if (isSupport) {
            removePreference("volume");
        }
    }

    private static class StatusBarCustomizeIndexProvider extends BaseSearchIndexProvider {
        boolean mIsPrimary;

        public StatusBarCustomizeIndexProvider() {
            this.mIsPrimary = UserHandle.myUserId() == 0;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (!this.mIsPrimary) {
                return arrayList;
            }
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_statusbar_customize_icon_settings;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }
    }

    private boolean isWLBPresent() {
        try {
            return this.mContext.getPackageManager().getPackageInfo("com.oneplus.opwlb", 128) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.d("OPStatusBarCustomizeSettings", "wlb app not present");
            return false;
        }
    }
}
