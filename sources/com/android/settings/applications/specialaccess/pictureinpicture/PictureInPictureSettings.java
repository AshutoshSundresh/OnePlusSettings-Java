package com.android.settings.applications.specialaccess.pictureinpicture;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.IconDrawableFactory;
import android.util.Pair;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EmptyTextSettings;
import com.android.settingslib.widget.apppreference.AppPreference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class PictureInPictureSettings extends EmptyTextSettings {
    static final List<String> IGNORE_PACKAGE_LIST;
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.picture_in_picture_settings);
    private Context mContext;
    private IconDrawableFactory mIconDrawableFactory;
    private PackageManager mPackageManager;
    private UserManager mUserManager;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 812;
    }

    static {
        ArrayList arrayList = new ArrayList();
        IGNORE_PACKAGE_LIST = arrayList;
        arrayList.add("com.android.systemui");
    }

    static class AppComparator implements Comparator<Pair<ApplicationInfo, Integer>> {
        private final Collator mCollator = Collator.getInstance();
        private final PackageManager mPm;

        public AppComparator(PackageManager packageManager) {
            this.mPm = packageManager;
        }

        public final int compare(Pair<ApplicationInfo, Integer> pair, Pair<ApplicationInfo, Integer> pair2) {
            CharSequence loadLabel = ((ApplicationInfo) pair.first).loadLabel(this.mPm);
            if (loadLabel == null) {
                loadLabel = ((ApplicationInfo) pair.first).name;
            }
            CharSequence loadLabel2 = ((ApplicationInfo) pair2.first).loadLabel(this.mPm);
            if (loadLabel2 == null) {
                loadLabel2 = ((ApplicationInfo) pair2.first).name;
            }
            int compare = this.mCollator.compare(loadLabel.toString(), loadLabel2.toString());
            if (compare != 0) {
                return compare;
            }
            return ((Integer) pair.second).intValue() - ((Integer) pair2.second).intValue();
        }
    }

    public static boolean checkPackageHasPictureInPictureActivities(String str, ActivityInfo[] activityInfoArr) {
        if (!IGNORE_PACKAGE_LIST.contains(str) && activityInfoArr != null) {
            for (int length = activityInfoArr.length - 1; length >= 0; length--) {
                if (activityInfoArr[length].supportsPictureInPicture()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        this.mPackageManager = activity.getPackageManager();
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        this.mIconDrawableFactory = IconDrawableFactory.newInstance(this.mContext);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        ArrayList<Pair<ApplicationInfo, Integer>> collectPipApps = collectPipApps(UserHandle.myUserId());
        Collections.sort(collectPipApps, new AppComparator(this.mPackageManager));
        Context prefContext = getPrefContext();
        Iterator<Pair<ApplicationInfo, Integer>> it = collectPipApps.iterator();
        while (it.hasNext()) {
            Pair<ApplicationInfo, Integer> next = it.next();
            final ApplicationInfo applicationInfo = (ApplicationInfo) next.first;
            int intValue = ((Integer) next.second).intValue();
            UserHandle of = UserHandle.of(intValue);
            final String str = applicationInfo.packageName;
            CharSequence loadLabel = applicationInfo.loadLabel(this.mPackageManager);
            AppPreference appPreference = new AppPreference(prefContext);
            appPreference.setIcon(this.mIconDrawableFactory.getBadgedIcon(applicationInfo, intValue));
            appPreference.setTitle(this.mPackageManager.getUserBadgedLabel(loadLabel, of));
            appPreference.setSummary(PictureInPictureDetails.getPreferenceSummary(prefContext, applicationInfo.uid, str));
            appPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                /* class com.android.settings.applications.specialaccess.pictureinpicture.PictureInPictureSettings.AnonymousClass1 */

                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    int i = C0017R$string.picture_in_picture_app_detail_title;
                    String str = str;
                    int i2 = applicationInfo.uid;
                    PictureInPictureSettings pictureInPictureSettings = PictureInPictureSettings.this;
                    AppInfoBase.startAppInfoFragment(PictureInPictureDetails.class, i, str, i2, pictureInPictureSettings, -1, pictureInPictureSettings.getMetricsCategory());
                    return true;
                }
            });
            preferenceScreen.addPreference(appPreference);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settings.widget.EmptyTextSettings
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setEmptyText(C0017R$string.picture_in_picture_empty_text);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.picture_in_picture_settings;
    }

    /* access modifiers changed from: package-private */
    public ArrayList<Pair<ApplicationInfo, Integer>> collectPipApps(int i) {
        ArrayList<Pair<ApplicationInfo, Integer>> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        for (UserInfo userInfo : this.mUserManager.getProfiles(i)) {
            arrayList2.add(Integer.valueOf(userInfo.id));
        }
        Iterator it = arrayList2.iterator();
        while (it.hasNext()) {
            int intValue = ((Integer) it.next()).intValue();
            for (PackageInfo packageInfo : this.mPackageManager.getInstalledPackagesAsUser(1, intValue)) {
                if (checkPackageHasPictureInPictureActivities(packageInfo.packageName, packageInfo.activities)) {
                    arrayList.add(new Pair<>(packageInfo.applicationInfo, Integer.valueOf(intValue)));
                }
            }
        }
        return arrayList;
    }
}
