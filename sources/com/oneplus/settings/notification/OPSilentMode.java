package com.oneplus.settings.notification;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchIndexableResource;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.notification.SettingPref;
import com.android.settings.search.BaseSearchIndexProvider;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OPSilentMode extends SettingsPreferenceFragment {
    private static final SettingPref[] PREFS;
    private static final SettingPref PREF_MEDIA_RING_SETTING = new SettingPref(2, "media_ring", "oem_zen_media_switch", 0, new int[0]);
    private static final SettingPref PREF_NOISE_TIPS_SETTING;
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.notification.OPSilentMode.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.op_silent_mode;
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            super.getNonIndexableKeys(context);
            ArrayList arrayList = new ArrayList();
            if (OPUtils.isSupportSocTriState()) {
                arrayList.add("noise_tips");
            }
            return arrayList;
        }
    };
    private PrefSettingsObserver mPrefSettingsObserver = new PrefSettingsObserver();

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 76;
    }

    static {
        SettingPref settingPref = new SettingPref(2, "noise_tips", "oem_vibrate_under_silent", 0, new int[0]);
        PREF_NOISE_TIPS_SETTING = settingPref;
        PREFS = new SettingPref[]{PREF_MEDIA_RING_SETTING, settingPref};
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_silent_mode);
        if (OPUtils.isSupportSocTriState()) {
            removePreference("noise_tips");
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        for (SettingPref settingPref : PREFS) {
            settingPref.init(this);
        }
        this.mPrefSettingsObserver.register(true);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        this.mPrefSettingsObserver.register(false);
        super.onPause();
    }

    private final class PrefSettingsObserver extends ContentObserver {
        public PrefSettingsObserver() {
            super(new Handler());
        }

        public void register(boolean z) {
            ContentResolver contentResolver = OPSilentMode.this.getContentResolver();
            if (z) {
                for (SettingPref settingPref : OPSilentMode.PREFS) {
                    contentResolver.registerContentObserver(settingPref.getUri(), false, this);
                }
                return;
            }
            contentResolver.unregisterContentObserver(this);
        }

        public void onChange(boolean z, Uri uri) {
            SettingPref[] settingPrefArr = OPSilentMode.PREFS;
            for (SettingPref settingPref : settingPrefArr) {
                if (settingPref.getUri().equals(uri)) {
                    settingPref.update(OPSilentMode.this.getActivity());
                    return;
                }
            }
        }
    }
}
