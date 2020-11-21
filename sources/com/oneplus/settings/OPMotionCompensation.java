package com.oneplus.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.oneplus.android.context.IOneplusContext;
import com.oneplus.android.context.OneplusContext;
import com.oneplus.iris.IOneplusIrisManager;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OPMotionCompensation extends DashboardFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.oneplus.settings.OPMotionCompensation.AnonymousClass1 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            if (OPUtils.isSupportMotionGraphicsCompensation()) {
                searchIndexableResource.xmlResId = C0019R$xml.op_motion_compensation;
            }
            return Arrays.asList(searchIndexableResource);
        }

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            return new ArrayList();
        }
    };
    private Context mContext;
    private PreferenceCategory mSupportVideo;
    SwitchPreference mVideo;

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OPMotionCompensation";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("oneplus_memc_video");
        this.mVideo = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        this.mContext = getActivity();
        this.mSupportVideo = (PreferenceCategory) findPreference("support_video");
        findPreference("get_more_video").setOnPreferenceClickListener(this);
        addSupportAppPreference(getSupportVideo(0));
        super.onActivityCreated(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        if (Settings.System.getInt(getPrefContext().getContentResolver(), "op_iris_video_memc_status", 0) == 0) {
            this.mVideo.setChecked(false);
        } else {
            this.mVideo.setChecked(true);
        }
        super.onResume();
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if ("get_more_video".equals(key)) {
            String videoAppUrl = getVideoAppUrl();
            if (TextUtils.isEmpty(videoAppUrl)) {
                Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage("com.heytap.market");
                if (launchIntentForPackage != null) {
                    this.mContext.startActivity(launchIntentForPackage);
                } else {
                    Intent launchIntentForPackage2 = getPackageManager().getLaunchIntentForPackage("com.android.vending");
                    if (launchIntentForPackage2 != null) {
                        this.mContext.startActivity(launchIntentForPackage2);
                    }
                }
            } else {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(videoAppUrl));
                this.mContext.startActivity(intent);
            }
            return true;
        } else if (key == null) {
            return false;
        } else {
            Intent launchIntentForPackage3 = getPackageManager().getLaunchIntentForPackage(key);
            if (launchIntentForPackage3 != null) {
                this.mContext.startActivity(launchIntentForPackage3);
            }
            return true;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (key.equals("oneplus_memc_video")) {
            if (booleanValue) {
                Settings.System.putInt(getPrefContext().getContentResolver(), "op_iris_video_memc_status", 1);
            } else {
                Settings.System.putInt(getPrefContext().getContentResolver(), "op_iris_video_memc_status", 0);
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.op_motion_compensation;
    }

    private void addSupportAppPreference(ArrayList<String> arrayList) {
        ApplicationInfo applicationInfo;
        this.mSupportVideo.removeAll();
        if (arrayList == null || arrayList.size() == 0) {
            Preference preference = new Preference(getPrefContext());
            preference.setTitle(this.mContext.getString(C0017R$string.oneplus_memc_support_no_apps));
            preference.setEnabled(false);
            this.mSupportVideo.addPreference(preference);
            return;
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        int i = 0;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            String str = arrayList.get(i2);
            try {
                applicationInfo = packageManager.getApplicationInfo(str, 0);
            } catch (Exception unused) {
                applicationInfo = null;
            }
            if (applicationInfo != null) {
                i++;
                Preference preference2 = new Preference(this.mContext);
                preference2.setTitle(applicationInfo.loadLabel(packageManager));
                preference2.setKey(str);
                preference2.setIcon(applicationInfo.loadIcon(packageManager));
                preference2.setOnPreferenceClickListener(this);
                this.mSupportVideo.addPreference(preference2);
            }
        }
        if (i == 0) {
            Preference preference3 = new Preference(getPrefContext());
            preference3.setTitle(this.mContext.getString(C0017R$string.oneplus_memc_support_no_apps));
            preference3.setEnabled(false);
            this.mSupportVideo.addPreference(preference3);
        }
    }

    private ArrayList<String> getSupportVideo(int i) {
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Map memcRateMap = ((IOneplusIrisManager) OneplusContext.queryInterface(IOneplusContext.EType.ONEPLUS_IRIS_SERVICE)).getMemcRateMap();
            if (memcRateMap != null && memcRateMap.size() > 0) {
                for (String str : memcRateMap.keySet()) {
                    Log.d("OPMotionCompensation", "getSupportVideo name = " + str);
                    String str2 = (String) memcRateMap.get(str);
                    if (i == 0) {
                        if (str2.equals("0")) {
                            arrayList.add(str);
                        }
                        if (str2.equals("1")) {
                            arrayList.add(str);
                        }
                    } else if (str2.equals("1")) {
                        arrayList.add(str);
                    }
                }
            }
        } catch (Exception e) {
            Log.d("OPMotionCompensation", "getSupportGame e = " + e);
        }
        return arrayList;
    }

    private String getVideoAppUrl() {
        try {
            IOneplusIrisManager iOneplusIrisManager = (IOneplusIrisManager) OneplusContext.queryInterface(IOneplusContext.EType.ONEPLUS_IRIS_SERVICE);
            Object invoke = iOneplusIrisManager.getClass().getMethod("getUrl", new Class[0]).invoke(iOneplusIrisManager, new Object[0]);
            Log.d("OPMotionCompensation", "getVideoAppUrl url = " + invoke);
            return (String) invoke;
        } catch (Exception e) {
            Log.d("OPMotionCompensation", "getVideoAppUrl e = " + e);
            return null;
        }
    }
}
