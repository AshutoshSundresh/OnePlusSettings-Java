package com.android.settings.applications.specialaccess.interactacrossprofiles;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.CrossProfileApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.IconDrawableFactory;
import android.util.Pair;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EmptyTextSettings;
import com.android.settingslib.widget.apppreference.AppPreference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class InteractAcrossProfilesSettings extends EmptyTextSettings {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.interact_across_profiles);
    private Context mContext;
    private CrossProfileApps mCrossProfileApps;
    private IconDrawableFactory mIconDrawableFactory;
    private PackageManager mPackageManager;
    private UserManager mUserManager;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1829;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        this.mIconDrawableFactory = IconDrawableFactory.newInstance(this.mContext);
        this.mCrossProfileApps = (CrossProfileApps) this.mContext.getSystemService(CrossProfileApps.class);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        ArrayList<Pair<ApplicationInfo, UserHandle>> collectConfigurableApps = collectConfigurableApps(this.mPackageManager, this.mUserManager, this.mCrossProfileApps);
        Context prefContext = getPrefContext();
        Iterator<Pair<ApplicationInfo, UserHandle>> it = collectConfigurableApps.iterator();
        while (it.hasNext()) {
            Pair<ApplicationInfo, UserHandle> next = it.next();
            final ApplicationInfo applicationInfo = (ApplicationInfo) next.first;
            UserHandle userHandle = (UserHandle) next.second;
            final String str = applicationInfo.packageName;
            CharSequence loadLabel = applicationInfo.loadLabel(this.mPackageManager);
            AppPreference appPreference = new AppPreference(prefContext);
            appPreference.setIcon(this.mIconDrawableFactory.getBadgedIcon(applicationInfo, userHandle.getIdentifier()));
            appPreference.setTitle(this.mPackageManager.getUserBadgedLabel(loadLabel, userHandle));
            appPreference.setSummary(InteractAcrossProfilesDetails.getPreferenceSummary(prefContext, str));
            appPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                /* class com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesSettings.AnonymousClass1 */

                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    int i = C0017R$string.interact_across_profiles_title;
                    String str = str;
                    int i2 = applicationInfo.uid;
                    InteractAcrossProfilesSettings interactAcrossProfilesSettings = InteractAcrossProfilesSettings.this;
                    AppInfoBase.startAppInfoFragment(InteractAcrossProfilesDetails.class, i, str, i2, interactAcrossProfilesSettings, -1, interactAcrossProfilesSettings.getMetricsCategory());
                    return true;
                }
            });
            preferenceScreen.addPreference(appPreference);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settings.widget.EmptyTextSettings
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setEmptyText(C0017R$string.interact_across_profiles_empty_text);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.interact_across_profiles;
    }

    static ArrayList<Pair<ApplicationInfo, UserHandle>> collectConfigurableApps(PackageManager packageManager, UserManager userManager, CrossProfileApps crossProfileApps) {
        UserHandle workProfile = getWorkProfile(userManager);
        if (workProfile == null) {
            return new ArrayList<>();
        }
        UserHandle profileParent = userManager.getProfileParent(workProfile);
        if (profileParent == null) {
            return new ArrayList<>();
        }
        ArrayList<Pair<ApplicationInfo, UserHandle>> arrayList = new ArrayList<>();
        for (PackageInfo packageInfo : getAllInstalledPackages(packageManager, profileParent, workProfile)) {
            if (crossProfileApps.canUserAttemptToConfigureInteractAcrossProfiles(packageInfo.packageName)) {
                arrayList.add(new Pair<>(packageInfo.applicationInfo, profileParent));
            }
        }
        return arrayList;
    }

    private static List<PackageInfo> getAllInstalledPackages(PackageManager packageManager, UserHandle userHandle, UserHandle userHandle2) {
        List installedPackagesAsUser = packageManager.getInstalledPackagesAsUser(1, userHandle.getIdentifier());
        List<PackageInfo> installedPackagesAsUser2 = packageManager.getInstalledPackagesAsUser(1, userHandle2.getIdentifier());
        ArrayList arrayList = new ArrayList(installedPackagesAsUser);
        for (PackageInfo packageInfo : installedPackagesAsUser2) {
            if (arrayList.stream().noneMatch(new Predicate(packageInfo) {
                /* class com.android.settings.applications.specialaccess.interactacrossprofiles.$$Lambda$InteractAcrossProfilesSettings$xlnIeHoD4vv7cF3NKasggsraJI */
                public final /* synthetic */ PackageInfo f$0;

                {
                    this.f$0 = r1;
                }

                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return this.f$0.packageName.equals(((PackageInfo) obj).packageName);
                }
            })) {
                arrayList.add(packageInfo);
            }
        }
        return arrayList;
    }

    static int getNumberOfEnabledApps(Context context, PackageManager packageManager, UserManager userManager, CrossProfileApps crossProfileApps) {
        UserHandle workProfile = getWorkProfile(userManager);
        if (workProfile == null || userManager.getProfileParent(workProfile) == null) {
            return 0;
        }
        ArrayList<Pair<ApplicationInfo, UserHandle>> collectConfigurableApps = collectConfigurableApps(packageManager, userManager, crossProfileApps);
        collectConfigurableApps.removeIf(new Predicate(context, crossProfileApps) {
            /* class com.android.settings.applications.specialaccess.interactacrossprofiles.$$Lambda$InteractAcrossProfilesSettings$gkLibnxLwvlW1ZTKtF6N8pY6KMU */
            public final /* synthetic */ Context f$0;
            public final /* synthetic */ CrossProfileApps f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return InteractAcrossProfilesSettings.lambda$getNumberOfEnabledApps$1(this.f$0, this.f$1, (Pair) obj);
            }
        });
        return collectConfigurableApps.size();
    }

    static /* synthetic */ boolean lambda$getNumberOfEnabledApps$1(Context context, CrossProfileApps crossProfileApps, Pair pair) {
        return !InteractAcrossProfilesDetails.isInteractAcrossProfilesEnabled(context, ((ApplicationInfo) pair.first).packageName) || !crossProfileApps.canConfigureInteractAcrossProfiles(((ApplicationInfo) pair.first).packageName);
    }

    static UserHandle getWorkProfile(UserManager userManager) {
        for (UserInfo userInfo : userManager.getProfiles(UserHandle.myUserId())) {
            if (userManager.isManagedProfile(userInfo.id)) {
                return userInfo.getUserHandle();
            }
        }
        return null;
    }
}
