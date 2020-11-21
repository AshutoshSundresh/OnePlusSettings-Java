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
import com.android.settings.widget.AppPreference;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

public class ZenModeAddBypassingAppsPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private Preference mAddPreference;
    private ApplicationsState.Session mAppSession;
    private final ApplicationsState.Callbacks mAppSessionCallbacks;
    ApplicationsState mApplicationsState;
    private Fragment mHostFragment;
    private final NotificationBackend mNotificationBackend;
    Context mPrefContext;
    PreferenceCategory mPreferenceCategory;
    PreferenceScreen mPreferenceScreen;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_mode_non_bypassing_apps_list";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public ZenModeAddBypassingAppsPreferenceController(Context context, Application application, Fragment fragment, NotificationBackend notificationBackend) {
        this(context, application == null ? null : ApplicationsState.getInstance(application), fragment, notificationBackend);
    }

    private ZenModeAddBypassingAppsPreferenceController(Context context, ApplicationsState applicationsState, Fragment fragment, NotificationBackend notificationBackend) {
        super(context);
        this.mAppSessionCallbacks = new ApplicationsState.Callbacks() {
            /* class com.android.settings.notification.zen.ZenModeAddBypassingAppsPreferenceController.AnonymousClass2 */

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onAllSizesComputed() {
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onRunningStateChanged(boolean z) {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageListChanged() {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList(arrayList);
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageIconChanged() {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onPackageSizeChanged(String str) {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onLauncherInfoChanged() {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList();
            }

            @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
            public void onLoadEntriesCompleted() {
                ZenModeAddBypassingAppsPreferenceController.this.updateAppList();
            }
        };
        this.mNotificationBackend = notificationBackend;
        this.mApplicationsState = applicationsState;
        this.mHostFragment = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceScreen = preferenceScreen;
        Preference findPreference = preferenceScreen.findPreference("zen_mode_bypassing_apps_add");
        this.mAddPreference = findPreference;
        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.notification.zen.ZenModeAddBypassingAppsPreferenceController.AnonymousClass1 */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                ZenModeAddBypassingAppsPreferenceController.this.mAddPreference.setVisible(false);
                ZenModeAddBypassingAppsPreferenceController zenModeAddBypassingAppsPreferenceController = ZenModeAddBypassingAppsPreferenceController.this;
                if (zenModeAddBypassingAppsPreferenceController.mApplicationsState == null || zenModeAddBypassingAppsPreferenceController.mHostFragment == null) {
                    return true;
                }
                ZenModeAddBypassingAppsPreferenceController zenModeAddBypassingAppsPreferenceController2 = ZenModeAddBypassingAppsPreferenceController.this;
                zenModeAddBypassingAppsPreferenceController2.mAppSession = zenModeAddBypassingAppsPreferenceController2.mApplicationsState.newSession(zenModeAddBypassingAppsPreferenceController2.mAppSessionCallbacks, ZenModeAddBypassingAppsPreferenceController.this.mHostFragment.getLifecycle());
                return true;
            }
        });
        this.mPrefContext = preferenceScreen.getContext();
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
        if (list != null) {
            if (this.mPreferenceCategory == null) {
                PreferenceCategory preferenceCategory = new PreferenceCategory(this.mPrefContext);
                this.mPreferenceCategory = preferenceCategory;
                preferenceCategory.setTitle(C0017R$string.zen_mode_bypassing_apps_add_header);
                this.mPreferenceScreen.addPreference(this.mPreferenceCategory);
            }
            ArrayList<Preference> arrayList = new ArrayList();
            for (ApplicationsState.AppEntry appEntry : list) {
                String str = appEntry.info.packageName;
                this.mApplicationsState.ensureIcon(appEntry);
                int channelCount = this.mNotificationBackend.getChannelCount(str, appEntry.info.uid);
                if (this.mNotificationBackend.getNotificationChannelsBypassingDnd(str, appEntry.info.uid).getList().size() == 0 && channelCount > 0) {
                    ZenModeAllBypassingAppsPreferenceController.getKey(str);
                    Preference findPreference = this.mPreferenceCategory.findPreference("");
                    if (findPreference == null) {
                        findPreference = new AppPreference(this.mPrefContext);
                        findPreference.setKey(str);
                        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(appEntry) {
                            /* class com.android.settings.notification.zen.$$Lambda$ZenModeAddBypassingAppsPreferenceController$4kCGTSpljpVUXjFEYxWSmKFoY */
                            public final /* synthetic */ ApplicationsState.AppEntry f$1;

                            {
                                this.f$1 = r2;
                            }

                            @Override // androidx.preference.Preference.OnPreferenceClickListener
                            public final boolean onPreferenceClick(Preference preference) {
                                return ZenModeAddBypassingAppsPreferenceController.this.lambda$updateAppList$0$ZenModeAddBypassingAppsPreferenceController(this.f$1, preference);
                            }
                        });
                    }
                    findPreference.setTitle(BidiFormatter.getInstance().unicodeWrap(appEntry.label));
                    findPreference.setIcon(appEntry.icon);
                    arrayList.add(findPreference);
                }
            }
            if (arrayList.size() == 0) {
                Preference findPreference2 = this.mPreferenceCategory.findPreference(ZenModeAllBypassingAppsPreferenceController.KEY_NO_APPS);
                if (findPreference2 == null) {
                    findPreference2 = new Preference(this.mPrefContext);
                    findPreference2.setKey(ZenModeAllBypassingAppsPreferenceController.KEY_NO_APPS);
                    findPreference2.setTitle(C0017R$string.zen_mode_bypassing_apps_subtext_none);
                }
                this.mPreferenceCategory.addPreference(findPreference2);
            }
            if (ZenModeAllBypassingAppsPreferenceController.hasAppListChanged(arrayList, this.mPreferenceCategory)) {
                this.mPreferenceCategory.removeAll();
                for (Preference preference : arrayList) {
                    this.mPreferenceCategory.addPreference(preference);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateAppList$0 */
    public /* synthetic */ boolean lambda$updateAppList$0$ZenModeAddBypassingAppsPreferenceController(ApplicationsState.AppEntry appEntry, Preference preference) {
        Bundle bundle = new Bundle();
        bundle.putString("package", appEntry.info.packageName);
        bundle.putInt("uid", appEntry.info.uid);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(AppChannelsBypassingDndSettings.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setResultListener(this.mHostFragment, 0);
        subSettingLauncher.setUserHandle(new UserHandle(UserHandle.getUserId(appEntry.info.uid)));
        subSettingLauncher.setSourceMetricsCategory(1589);
        subSettingLauncher.launch();
        return true;
    }
}
