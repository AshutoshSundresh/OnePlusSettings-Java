package com.android.settings.notification.zen;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import androidx.core.text.BidiFormatter;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.AppChannelsBypassingDndSettings;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.apppreference.AppPreference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ZenModeAllBypassingAppsPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    public static final String KEY_NO_APPS = "none";
    private ApplicationsState.Session mAppSession;
    private final ApplicationsState.Callbacks mAppSessionCallbacks;
    ApplicationsState mApplicationsState;
    private Fragment mHostFragment;
    private final NotificationBackend mNotificationBackend;
    Context mPrefContext;
    PreferenceCategory mPreferenceCategory;

    static String getKey(String str) {
        return str;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_mode_bypassing_apps_list";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    static {
        getKey("none");
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public ZenModeAllBypassingAppsPreferenceController(Context context, Application application, Fragment fragment, NotificationBackend notificationBackend) {
        this(context, application == null ? null : ApplicationsState.getInstance(application), fragment, notificationBackend);
    }

    private ZenModeAllBypassingAppsPreferenceController(Context context, ApplicationsState applicationsState, Fragment fragment, NotificationBackend notificationBackend) {
        super(context);
        AnonymousClass1 r1 = new ApplicationsState.Callbacks() {
            /* class com.android.settings.notification.zen.ZenModeAllBypassingAppsPreferenceController.AnonymousClass1 */

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onAllSizesComputed() {
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onRunningStateChanged(boolean z) {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageListChanged() {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList(arrayList);
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageIconChanged() {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageSizeChanged(String str) {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onLauncherInfoChanged() {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onLoadEntriesCompleted() {
                ZenModeAllBypassingAppsPreferenceController.this.updateAppList();
            }
        };
        this.mAppSessionCallbacks = r1;
        this.mNotificationBackend = notificationBackend;
        this.mApplicationsState = applicationsState;
        this.mHostFragment = fragment;
        if (applicationsState != null && fragment != null) {
            this.mAppSession = applicationsState.newSession(r1, fragment.getLifecycle());
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("zen_mode_bypassing_apps_list");
        this.mPrefContext = preferenceScreen.getContext();
        updateAppList();
        super.displayPreference(preferenceScreen);
    }

    public void updateAppList() {
        ApplicationsState.Session session = this.mAppSession;
        if (session != null) {
            updateAppList(session.rebuild(ApplicationsState.FILTER_ALL_ENABLED, ApplicationsState.ALPHA_COMPARATOR));
        }
    }

    /* access modifiers changed from: package-private */
    public void updateAppList(List<ApplicationsState.AppEntry> list) {
        if (!(this.mPreferenceCategory == null || list == null)) {
            ArrayList<Preference> arrayList = new ArrayList();
            for (ApplicationsState.AppEntry appEntry : list) {
                String str = appEntry.info.packageName;
                this.mApplicationsState.ensureIcon(appEntry);
                int channelCount = this.mNotificationBackend.getChannelCount(str, appEntry.info.uid);
                int size = this.mNotificationBackend.getNotificationChannelsBypassingDnd(str, appEntry.info.uid).getList().size();
                if (size > 0) {
                    getKey(str);
                    Preference findPreference = this.mPreferenceCategory.findPreference(str);
                    if (findPreference == null) {
                        findPreference = new AppPreference(this.mPrefContext);
                        findPreference.setKey(str);
                        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(appEntry) {
                            /* class com.android.settings.notification.zen.$$Lambda$ZenModeAllBypassingAppsPreferenceController$usGr5p3Qvprs4OGuE54umNNPg */
                            public final /* synthetic */ ApplicationsState.AppEntry f$1;

                            {
                                this.f$1 = r2;
                            }

                            @Override // androidx.preference.Preference.OnPreferenceClickListener
                            public final boolean onPreferenceClick(Preference preference) {
                                return ZenModeAllBypassingAppsPreferenceController.this.lambda$updateAppList$0$ZenModeAllBypassingAppsPreferenceController(this.f$1, preference);
                            }
                        });
                    }
                    findPreference.setTitle(BidiFormatter.getInstance().unicodeWrap(appEntry.label));
                    findPreference.setIcon(appEntry.icon);
                    if (channelCount > size) {
                        findPreference.setSummary(C0017R$string.zen_mode_bypassing_apps_summary_some);
                    } else {
                        findPreference.setSummary(C0017R$string.zen_mode_bypassing_apps_summary_all);
                    }
                    arrayList.add(findPreference);
                }
            }
            if (arrayList.size() == 0) {
                Preference findPreference2 = this.mPreferenceCategory.findPreference(KEY_NO_APPS);
                if (findPreference2 == null) {
                    findPreference2 = new Preference(this.mPrefContext);
                    findPreference2.setKey(KEY_NO_APPS);
                    findPreference2.setTitle(C0017R$string.zen_mode_bypassing_apps_none);
                }
                arrayList.add(findPreference2);
            }
            if (hasAppListChanged(arrayList, this.mPreferenceCategory)) {
                this.mPreferenceCategory.removeAll();
                for (Preference preference : arrayList) {
                    this.mPreferenceCategory.addPreference(preference);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateAppList$0 */
    public /* synthetic */ boolean lambda$updateAppList$0$ZenModeAllBypassingAppsPreferenceController(ApplicationsState.AppEntry appEntry, Preference preference) {
        Bundle bundle = new Bundle();
        bundle.putString("package", appEntry.info.packageName);
        bundle.putInt("uid", appEntry.info.uid);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(AppChannelsBypassingDndSettings.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setUserHandle(UserHandle.getUserHandleForUid(appEntry.info.uid));
        subSettingLauncher.setResultListener(this.mHostFragment, 0);
        subSettingLauncher.setSourceMetricsCategory(1589);
        subSettingLauncher.launch();
        return true;
    }

    static boolean hasAppListChanged(List<Preference> list, PreferenceCategory preferenceCategory) {
        if (list.size() != preferenceCategory.getPreferenceCount()) {
            return true;
        }
        for (int i = 0; i < list.size(); i++) {
            if (!Objects.equals(list.get(i).getKey(), preferenceCategory.getPreference(i).getKey())) {
                return true;
            }
        }
        return false;
    }
}
