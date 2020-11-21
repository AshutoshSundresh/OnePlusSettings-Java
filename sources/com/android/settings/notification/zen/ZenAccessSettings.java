package com.android.settings.notification.zen;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.specialaccess.zenaccess.ZenAccessController;
import com.android.settings.applications.specialaccess.zenaccess.ZenAccessDetails;
import com.android.settings.applications.specialaccess.zenaccess.ZenAccessSettingObserverMixin;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EmptyTextSettings;
import com.android.settingslib.widget.apppreference.AppPreference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ZenAccessSettings extends EmptyTextSettings implements ZenAccessSettingObserverMixin.Listener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.zen_access_settings);
    private Context mContext;
    private NotificationManager mNoMan;
    private PackageManager mPkgMan;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 180;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        this.mPkgMan = activity.getPackageManager();
        this.mNoMan = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        getSettingsLifecycle().addObserver(new ZenAccessSettingObserverMixin(getContext(), this));
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settings.widget.EmptyTextSettings
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setEmptyText(C0017R$string.zen_access_empty_text);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.zen_access_settings;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        reloadList();
    }

    @Override // com.android.settings.applications.specialaccess.zenaccess.ZenAccessSettingObserverMixin.Listener
    public void onZenAccessPolicyChanged() {
        reloadList();
    }

    private void reloadList() {
        List<ApplicationInfo> installedApplications;
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        ArrayList arrayList = new ArrayList();
        Set<String> packagesRequestingNotificationPolicyAccess = ZenAccessController.getPackagesRequestingNotificationPolicyAccess();
        if (!packagesRequestingNotificationPolicyAccess.isEmpty() && (installedApplications = this.mPkgMan.getInstalledApplications(0)) != null) {
            for (ApplicationInfo applicationInfo : installedApplications) {
                if (packagesRequestingNotificationPolicyAccess.contains(applicationInfo.packageName)) {
                    arrayList.add(applicationInfo);
                }
            }
        }
        ArraySet arraySet = new ArraySet();
        arraySet.addAll(this.mNoMan.getEnabledNotificationListenerPackages());
        Collections.sort(arrayList, new PackageItemInfo.DisplayNameComparator(this.mPkgMan));
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            ApplicationInfo applicationInfo2 = (ApplicationInfo) it.next();
            String str = applicationInfo2.packageName;
            CharSequence loadLabel = applicationInfo2.loadLabel(this.mPkgMan);
            AppPreference appPreference = new AppPreference(getPrefContext());
            appPreference.setKey(str);
            appPreference.setIcon(applicationInfo2.loadIcon(this.mPkgMan));
            appPreference.setTitle(loadLabel);
            if (arraySet.contains(str)) {
                appPreference.setEnabled(false);
                appPreference.setSummary(getString(C0017R$string.zen_access_disabled_package_warning));
            } else {
                appPreference.setSummary(getPreferenceSummary(str));
            }
            appPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(str, applicationInfo2) {
                /* class com.android.settings.notification.zen.$$Lambda$ZenAccessSettings$Zmiquu5RYq_nVAx9ahbjK_PPuYc */
                public final /* synthetic */ String f$1;
                public final /* synthetic */ ApplicationInfo f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    return ZenAccessSettings.this.lambda$reloadList$0$ZenAccessSettings(this.f$1, this.f$2, preference);
                }
            });
            preferenceScreen.addPreference(appPreference);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$reloadList$0 */
    public /* synthetic */ boolean lambda$reloadList$0$ZenAccessSettings(String str, ApplicationInfo applicationInfo, Preference preference) {
        AppInfoBase.startAppInfoFragment(ZenAccessDetails.class, C0017R$string.manage_zen_access_title, str, applicationInfo.uid, this, -1, getMetricsCategory());
        return true;
    }

    private int getPreferenceSummary(String str) {
        if (ZenAccessController.hasAccess(getContext(), str)) {
            return C0017R$string.app_permission_summary_allowed;
        }
        return C0017R$string.app_permission_summary_not_allowed;
    }
}
