package com.android.settings.notification;

import android.app.Application;
import android.app.usage.IUsageStatsManager;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.notification.NotifyingApp;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.util.Slog;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.notification.app.AppNotificationSettings;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.StringUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class RecentNotifyingAppsPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    static final String KEY_SEE_ALL = "all_notifications";
    private final ApplicationsState mApplicationsState;
    List<NotifyingApp> mApps;
    private Calendar mCal;
    private PreferenceCategory mCategory;
    private final Fragment mHost;
    private final IconDrawableFactory mIconDrawableFactory;
    private final NotificationBackend mNotificationBackend;
    private Preference mSeeAllPref;
    private IUsageStatsManager mUsageStatsManager;
    protected List<Integer> mUserIds;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "recent_notifications_category";
    }

    static {
        new ArraySet();
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public RecentNotifyingAppsPreferenceController(Context context, NotificationBackend notificationBackend, IUsageStatsManager iUsageStatsManager, UserManager userManager, Application application, Fragment fragment) {
        this(context, notificationBackend, iUsageStatsManager, userManager, application == null ? null : ApplicationsState.getInstance(application), fragment);
    }

    RecentNotifyingAppsPreferenceController(Context context, NotificationBackend notificationBackend, IUsageStatsManager iUsageStatsManager, UserManager userManager, ApplicationsState applicationsState, Fragment fragment) {
        super(context);
        this.mIconDrawableFactory = IconDrawableFactory.newInstance(context);
        context.getPackageManager();
        this.mHost = fragment;
        this.mApplicationsState = applicationsState;
        this.mNotificationBackend = notificationBackend;
        this.mUsageStatsManager = iUsageStatsManager;
        ArrayList arrayList = new ArrayList();
        this.mUserIds = arrayList;
        arrayList.add(Integer.valueOf(this.mContext.getUserId()));
        int managedProfileId = Utils.getManagedProfileId(userManager, this.mContext.getUserId());
        if (managedProfileId != -10000) {
            this.mUserIds.add(Integer.valueOf(managedProfileId));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mApplicationsState != null;
    }

    @Override // com.android.settings.core.PreferenceControllerMixin
    public void updateNonIndexableKeys(List<String> list) {
        super.updateNonIndexableKeys(list);
        list.add("recent_notifications_category");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mSeeAllPref = preferenceScreen.findPreference(KEY_SEE_ALL);
        super.displayPreference(preferenceScreen);
        refreshUi(this.mCategory.getContext());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        refreshUi(this.mCategory.getContext());
        this.mSeeAllPref.setTitle(this.mContext.getString(C0017R$string.recent_notifications_see_all_title));
    }

    /* access modifiers changed from: package-private */
    public void refreshUi(Context context) {
        reloadData();
        List<NotifyingApp> displayableRecentAppList = getDisplayableRecentAppList();
        if (displayableRecentAppList == null || displayableRecentAppList.isEmpty()) {
            displayOnlyAllAppsLink();
        } else {
            displayRecentApps(context, displayableRecentAppList);
        }
    }

    /* access modifiers changed from: package-private */
    public void reloadData() {
        this.mApps = new ArrayList();
        Calendar instance = Calendar.getInstance();
        this.mCal = instance;
        instance.add(6, -3);
        for (Integer num : this.mUserIds) {
            int intValue = num.intValue();
            UsageEvents usageEvents = null;
            try {
                usageEvents = this.mUsageStatsManager.queryEventsForUser(this.mCal.getTimeInMillis(), System.currentTimeMillis(), intValue, this.mContext.getPackageName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (usageEvents != null) {
                ArrayMap arrayMap = new ArrayMap();
                UsageEvents.Event event = new UsageEvents.Event();
                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(event);
                    if (event.getEventType() == 12) {
                        NotifyingApp notifyingApp = (NotifyingApp) arrayMap.get(getKey(intValue, event.getPackageName()));
                        if (notifyingApp == null) {
                            notifyingApp = new NotifyingApp();
                            arrayMap.put(getKey(intValue, event.getPackageName()), notifyingApp);
                            notifyingApp.setPackage(event.getPackageName());
                            notifyingApp.setUserId(intValue);
                        }
                        if (event.getTimeStamp() > notifyingApp.getLastNotified()) {
                            notifyingApp.setLastNotified(event.getTimeStamp());
                        }
                    }
                }
                this.mApps.addAll(arrayMap.values());
            }
        }
    }

    static String getKey(int i, String str) {
        return i + "|" + str;
    }

    private void displayOnlyAllAppsLink() {
        this.mCategory.setTitle((CharSequence) null);
        this.mSeeAllPref.setTitle(C0017R$string.notifications_title);
        this.mSeeAllPref.setIcon((Drawable) null);
        for (int preferenceCount = this.mCategory.getPreferenceCount() - 1; preferenceCount >= 0; preferenceCount--) {
            Preference preference = this.mCategory.getPreference(preferenceCount);
            if (!TextUtils.equals(preference.getKey(), KEY_SEE_ALL)) {
                this.mCategory.removePreference(preference);
            }
        }
    }

    private void displayRecentApps(Context context, List<NotifyingApp> list) {
        boolean z;
        this.mCategory.setTitle(C0017R$string.recent_notifications);
        this.mSeeAllPref.setSummary((CharSequence) null);
        this.mSeeAllPref.setIcon(C0008R$drawable.ic_chevron_right_24dp);
        ArrayMap arrayMap = new ArrayMap();
        int preferenceCount = this.mCategory.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = this.mCategory.getPreference(i);
            String key = preference.getKey();
            if (!TextUtils.equals(key, KEY_SEE_ALL)) {
                arrayMap.put(key, (NotificationAppPreference) preference);
            }
        }
        int size = list.size();
        for (int i2 = 0; i2 < size; i2++) {
            NotifyingApp notifyingApp = list.get(i2);
            String str = notifyingApp.getPackage();
            ApplicationsState.AppEntry entry = this.mApplicationsState.getEntry(notifyingApp.getPackage(), notifyingApp.getUserId());
            if (entry != null) {
                NotificationAppPreference notificationAppPreference = (NotificationAppPreference) arrayMap.remove(getKey(notifyingApp.getUserId(), str));
                if (notificationAppPreference == null) {
                    notificationAppPreference = new NotificationAppPreference(context);
                    z = false;
                } else {
                    z = true;
                }
                notificationAppPreference.setKey(getKey(notifyingApp.getUserId(), str));
                notificationAppPreference.setTitle(entry.label);
                notificationAppPreference.setIcon(this.mIconDrawableFactory.getBadgedIcon(entry.info));
                notificationAppPreference.setSummary(StringUtil.formatRelativeTime(this.mContext, (double) (System.currentTimeMillis() - notifyingApp.getLastNotified()), true));
                notificationAppPreference.setOrder(i2);
                Bundle bundle = new Bundle();
                bundle.putString("package", str);
                bundle.putInt("uid", entry.info.uid);
                notificationAppPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(bundle, entry) {
                    /* class com.android.settings.notification.$$Lambda$RecentNotifyingAppsPreferenceController$dQunHGEGFX2f24KdzEd4pihLnrA */
                    public final /* synthetic */ Bundle f$1;
                    public final /* synthetic */ ApplicationsState.AppEntry f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public final boolean onPreferenceClick(Preference preference) {
                        return RecentNotifyingAppsPreferenceController.this.lambda$displayRecentApps$0$RecentNotifyingAppsPreferenceController(this.f$1, this.f$2, preference);
                    }
                });
                if ("com.oneplus.deskclock".equals(str) || "com.android.incallui".equals(str) || "com.google.android.calendar".equals(str) || "com.oneplus.calendar".equals(str) || "com.android.dialer".equals(str) || "com.google.android.dialer".equals(str) || "com.oneplus.dialer".equals(str)) {
                    notificationAppPreference.setSwitchEnabled(false);
                } else {
                    notificationAppPreference.setSwitchEnabled(this.mNotificationBackend.isBlockable(this.mContext, entry.info));
                }
                notificationAppPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(str, entry) {
                    /* class com.android.settings.notification.$$Lambda$RecentNotifyingAppsPreferenceController$UdU6DZrFo8MFPUbBfDGnhrNJuYY */
                    public final /* synthetic */ String f$1;
                    public final /* synthetic */ ApplicationsState.AppEntry f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        return RecentNotifyingAppsPreferenceController.this.lambda$displayRecentApps$1$RecentNotifyingAppsPreferenceController(this.f$1, this.f$2, preference, obj);
                    }
                });
                notificationAppPreference.setChecked(!this.mNotificationBackend.getNotificationsBanned(str, entry.info.uid));
                if (!z) {
                    this.mCategory.addPreference(notificationAppPreference);
                }
            }
        }
        for (Preference preference2 : arrayMap.values()) {
            this.mCategory.removePreference(preference2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayRecentApps$0 */
    public /* synthetic */ boolean lambda$displayRecentApps$0$RecentNotifyingAppsPreferenceController(Bundle bundle, ApplicationsState.AppEntry appEntry, Preference preference) {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mHost.getActivity());
        subSettingLauncher.setDestination(AppNotificationSettings.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.notifications_title);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setUserHandle(new UserHandle(UserHandle.getUserId(appEntry.info.uid)));
        subSettingLauncher.setSourceMetricsCategory(133);
        subSettingLauncher.launch();
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayRecentApps$1 */
    public /* synthetic */ boolean lambda$displayRecentApps$1$RecentNotifyingAppsPreferenceController(String str, ApplicationsState.AppEntry appEntry, Preference preference, Object obj) {
        this.mNotificationBackend.setNotificationsEnabledForPackage(str, appEntry.info.uid, ((Boolean) obj).booleanValue());
        return true;
    }

    private List<NotifyingApp> getDisplayableRecentAppList() {
        Collections.sort(this.mApps);
        ArrayList arrayList = new ArrayList(3);
        int i = 0;
        for (NotifyingApp notifyingApp : this.mApps) {
            try {
                if (this.mApplicationsState.getEntry(notifyingApp.getPackage(), notifyingApp.getUserId()) != null) {
                    arrayList.add(notifyingApp);
                    i++;
                    if (i >= 3) {
                        break;
                    }
                }
            } catch (Exception e) {
                Slog.e("RecentNotisCtrl", "Failed to find app " + notifyingApp.getPackage() + "/" + notifyingApp.getUserId(), e);
            }
        }
        return arrayList;
    }
}
