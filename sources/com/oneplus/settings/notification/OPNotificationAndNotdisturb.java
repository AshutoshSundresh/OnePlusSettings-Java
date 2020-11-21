package com.oneplus.settings.notification;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPNotificationAndNotdisturb extends SettingsPreferenceFragment {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new NotificationAndNotdisturbSearchIndexProvider();
    private Preference mDonotdisturbSetings;
    private Preference mVibrationSettings;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_notification_not_disturb);
        findPreference("ring_settings");
        findPreference("silent_settings");
        this.mVibrationSettings = findPreference("vibration_settings");
        this.mDonotdisturbSetings = findPreference("do_not_disturb_settings");
        PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference("mode_settings_category");
        if (OPUtils.isGuestMode()) {
            preferenceGroup.removePreference(findPreference("ring_settings"));
        }
        if (OPUtils.isSupportSocTriState()) {
            preferenceGroup.removePreference(this.mDonotdisturbSetings);
            return;
        }
        getActivity().getActionBar().setTitle(getResources().getString(C0017R$string.alertslider_settings));
        preferenceGroup.removePreference(this.mVibrationSettings);
    }

    private static class NotificationAndNotdisturbSearchIndexProvider extends BaseSearchIndexProvider {
        boolean mIsPrimary;

        public NotificationAndNotdisturbSearchIndexProvider() {
            this.mIsPrimary = UserHandle.myUserId() == 0;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (!this.mIsPrimary) {
                return arrayList;
            }
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_notification_not_disturb;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            ArrayList arrayList = new ArrayList();
            if (OPUtils.isGuestMode()) {
                arrayList.add("ring_settings");
            }
            if (OPUtils.isSupportSocTriState()) {
                arrayList.add("do_not_disturb_settings");
            } else {
                arrayList.add("vibration_settings");
            }
            return arrayList;
        }
    }
}
