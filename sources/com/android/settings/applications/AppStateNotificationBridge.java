package com.android.settings.applications;

import android.app.usage.IUsageStatsManager;
import android.app.usage.UsageEvents;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.SwitchCompat;
import com.android.settings.C0010R$id;
import com.android.settings.C0015R$plurals;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.utils.StringUtil;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AppStateNotificationBridge extends AppStateBaseBridge {
    public static final Uri BASE_URI = Uri.parse("content://com.nearme.instant.setting/notification");
    public static final ApplicationsState.AppFilter FILTER_APP_NOTIFICATION_BLOCKED = new ApplicationsState.AppFilter() {
        /* class com.android.settings.applications.AppStateNotificationBridge.AnonymousClass3 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            NotificationsSentState notificationsSentState = AppStateNotificationBridge.getNotificationsSentState(appEntry);
            if (notificationsSentState != null) {
                return notificationsSentState.blocked;
            }
            return false;
        }
    };
    public static final ApplicationsState.AppFilter FILTER_APP_NOTIFICATION_FREQUENCY = new ApplicationsState.AppFilter() {
        /* class com.android.settings.applications.AppStateNotificationBridge.AnonymousClass2 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            NotificationsSentState notificationsSentState = AppStateNotificationBridge.getNotificationsSentState(appEntry);
            if (notificationsSentState == null || notificationsSentState.sentCount == 0) {
                return false;
            }
            return true;
        }
    };
    public static final ApplicationsState.AppFilter FILTER_APP_NOTIFICATION_RECENCY = new ApplicationsState.AppFilter() {
        /* class com.android.settings.applications.AppStateNotificationBridge.AnonymousClass1 */

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(ApplicationsState.AppEntry appEntry) {
            NotificationsSentState notificationsSentState = AppStateNotificationBridge.getNotificationsSentState(appEntry);
            if (notificationsSentState == null || notificationsSentState.lastSent == 0) {
                return false;
            }
            return true;
        }
    };
    public static final Comparator<ApplicationsState.AppEntry> FREQUENCY_NOTIFICATION_COMPARATOR = new Comparator<ApplicationsState.AppEntry>() {
        /* class com.android.settings.applications.AppStateNotificationBridge.AnonymousClass5 */

        public int compare(ApplicationsState.AppEntry appEntry, ApplicationsState.AppEntry appEntry2) {
            NotificationsSentState notificationsSentState = AppStateNotificationBridge.getNotificationsSentState(appEntry);
            NotificationsSentState notificationsSentState2 = AppStateNotificationBridge.getNotificationsSentState(appEntry2);
            if (notificationsSentState == null && notificationsSentState2 != null) {
                return -1;
            }
            if (notificationsSentState != null && notificationsSentState2 == null) {
                return 1;
            }
            if (!(notificationsSentState == null || notificationsSentState2 == null)) {
                int i = notificationsSentState.sentCount;
                int i2 = notificationsSentState2.sentCount;
                if (i < i2) {
                    return 1;
                }
                if (i > i2) {
                    return -1;
                }
            }
            return ApplicationsState.ALPHA_COMPARATOR.compare(appEntry, appEntry2);
        }
    };
    public static final Comparator<ApplicationsState.AppEntry> RECENT_NOTIFICATION_COMPARATOR = new Comparator<ApplicationsState.AppEntry>() {
        /* class com.android.settings.applications.AppStateNotificationBridge.AnonymousClass4 */

        public int compare(ApplicationsState.AppEntry appEntry, ApplicationsState.AppEntry appEntry2) {
            NotificationsSentState notificationsSentState = AppStateNotificationBridge.getNotificationsSentState(appEntry);
            NotificationsSentState notificationsSentState2 = AppStateNotificationBridge.getNotificationsSentState(appEntry2);
            if (notificationsSentState == null && notificationsSentState2 != null) {
                return -1;
            }
            if (notificationsSentState != null && notificationsSentState2 == null) {
                return 1;
            }
            if (!(notificationsSentState == null || notificationsSentState2 == null)) {
                long j = notificationsSentState.lastSent;
                long j2 = notificationsSentState2.lastSent;
                if (j < j2) {
                    return 1;
                }
                if (j > j2) {
                    return -1;
                }
            }
            return ApplicationsState.ALPHA_COMPARATOR.compare(appEntry, appEntry2);
        }
    };
    private NotificationBackend mBackend;
    private final Context mContext;
    private IUsageStatsManager mUsageStatsManager;
    protected List<Integer> mUserIds;

    public static class NotificationsSentState {
        public int avgSentDaily = 0;
        public int avgSentWeekly = 0;
        public boolean blockable;
        public boolean blocked;
        public boolean instantApp;
        public Drawable instantAppIcon;
        public String instantAppName;
        public String instantAppPKG;
        public long lastSent = 0;
        public int sentCount = 0;
        public boolean systemApp;
    }

    public AppStateNotificationBridge(Context context, ApplicationsState applicationsState, AppStateBaseBridge.Callback callback, IUsageStatsManager iUsageStatsManager, UserManager userManager, NotificationBackend notificationBackend) {
        super(applicationsState, callback);
        this.mContext = context;
        this.mUsageStatsManager = iUsageStatsManager;
        this.mBackend = notificationBackend;
        ArrayList arrayList = new ArrayList();
        this.mUserIds = arrayList;
        arrayList.add(Integer.valueOf(this.mContext.getUserId()));
        int managedProfileId = Utils.getManagedProfileId(userManager, this.mContext.getUserId());
        if (managedProfileId != -10000) {
            this.mUserIds.add(Integer.valueOf(managedProfileId));
        }
        if (OPUtils.isSupportXVibrate()) {
            Vibrator vibrator = (Vibrator) context.getSystemService("vibrator");
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void loadAllExtraInfo() {
        ArrayList<ApplicationsState.AppEntry> allApps = this.mAppSession.getAllApps();
        if (allApps != null) {
            Map<String, NotificationsSentState> aggregatedUsageEvents = getAggregatedUsageEvents();
            Iterator<ApplicationsState.AppEntry> it = allApps.iterator();
            while (it.hasNext()) {
                ApplicationsState.AppEntry next = it.next();
                NotificationsSentState notificationsSentState = aggregatedUsageEvents.get(getKey(UserHandle.getUserId(next.info.uid), next.info.packageName));
                if (notificationsSentState == null) {
                    notificationsSentState = new NotificationsSentState();
                }
                calculateAvgSentCounts(notificationsSentState);
                addBlockStatus(next, notificationsSentState);
                next.extraInfo = notificationsSentState;
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppStateBaseBridge
    public void updateExtraInfo(ApplicationsState.AppEntry appEntry, String str, int i) {
        NotificationsSentState aggregatedUsageEvents = getAggregatedUsageEvents(UserHandle.getUserId(appEntry.info.uid), appEntry.info.packageName);
        calculateAvgSentCounts(aggregatedUsageEvents);
        addBlockStatus(appEntry, aggregatedUsageEvents);
        appEntry.extraInfo = aggregatedUsageEvents;
    }

    public static CharSequence getSummary(Context context, NotificationsSentState notificationsSentState, int i) {
        if (notificationsSentState.instantApp) {
            return getAppName(context, "com.nearme.instant.platform");
        }
        if (i == C0010R$id.sort_order_recent_notification) {
            if (notificationsSentState.lastSent == 0) {
                return context.getString(C0017R$string.notifications_sent_never);
            }
            return StringUtil.formatRelativeTime(context, (double) (System.currentTimeMillis() - notificationsSentState.lastSent), true);
        } else if (i != C0010R$id.sort_order_frequent_notification) {
            return "";
        } else {
            if (notificationsSentState.avgSentDaily > 0) {
                Resources resources = context.getResources();
                int i2 = C0015R$plurals.notifications_sent_daily;
                int i3 = notificationsSentState.avgSentDaily;
                return resources.getQuantityString(i2, i3, Integer.valueOf(i3));
            }
            Resources resources2 = context.getResources();
            int i4 = C0015R$plurals.notifications_sent_weekly;
            int i5 = notificationsSentState.avgSentWeekly;
            return resources2.getQuantityString(i4, i5, Integer.valueOf(i5));
        }
    }

    private static String getAppName(Context context, String str) {
        ApplicationInfo applicationInfo;
        PackageManager packageManager = context.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            applicationInfo = null;
        }
        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "(unknown)");
    }

    private void addBlockStatus(ApplicationsState.AppEntry appEntry, NotificationsSentState notificationsSentState) {
        if ((notificationsSentState == null || !notificationsSentState.instantApp) && notificationsSentState != null) {
            NotificationBackend notificationBackend = this.mBackend;
            ApplicationInfo applicationInfo = appEntry.info;
            notificationsSentState.blocked = notificationBackend.getNotificationsBanned(applicationInfo.packageName, applicationInfo.uid);
            boolean isSystemApp = this.mBackend.isSystemApp(this.mContext, appEntry.info);
            notificationsSentState.systemApp = isSystemApp;
            notificationsSentState.blockable = !isSystemApp || notificationsSentState.blocked;
        }
    }

    private void calculateAvgSentCounts(NotificationsSentState notificationsSentState) {
        if ((notificationsSentState == null || !notificationsSentState.instantApp) && notificationsSentState != null) {
            notificationsSentState.avgSentDaily = Math.round(((float) notificationsSentState.sentCount) / 7.0f);
            int i = notificationsSentState.sentCount;
            if (i < 7) {
                notificationsSentState.avgSentWeekly = i;
            }
        }
    }

    /* access modifiers changed from: protected */
    public Map<String, NotificationsSentState> getAggregatedUsageEvents() {
        ArrayMap arrayMap = new ArrayMap();
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - 604800000;
        for (Integer num : this.mUserIds) {
            int intValue = num.intValue();
            UsageEvents usageEvents = null;
            try {
                usageEvents = this.mUsageStatsManager.queryEventsForUser(j, currentTimeMillis, intValue, this.mContext.getPackageName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (usageEvents != null) {
                UsageEvents.Event event = new UsageEvents.Event();
                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(event);
                    NotificationsSentState notificationsSentState = (NotificationsSentState) arrayMap.get(getKey(intValue, event.getPackageName()));
                    if (notificationsSentState == null) {
                        notificationsSentState = new NotificationsSentState();
                        arrayMap.put(getKey(intValue, event.getPackageName()), notificationsSentState);
                    }
                    if (event.getEventType() == 12) {
                        if (event.getTimeStamp() > notificationsSentState.lastSent) {
                            notificationsSentState.lastSent = event.getTimeStamp();
                        }
                        notificationsSentState.sentCount++;
                    }
                }
            }
        }
        return arrayMap;
    }

    /* access modifiers changed from: protected */
    public NotificationsSentState getAggregatedUsageEvents(int i, String str) {
        UsageEvents usageEvents;
        long currentTimeMillis = System.currentTimeMillis();
        NotificationsSentState notificationsSentState = null;
        try {
            usageEvents = this.mUsageStatsManager.queryEventsForPackageForUser(currentTimeMillis - 604800000, currentTimeMillis, i, str, this.mContext.getPackageName());
        } catch (RemoteException e) {
            e.printStackTrace();
            usageEvents = null;
        }
        if (usageEvents != null) {
            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == 12) {
                    if (notificationsSentState == null) {
                        notificationsSentState = new NotificationsSentState();
                    }
                    if (event.getTimeStamp() > notificationsSentState.lastSent) {
                        notificationsSentState.lastSent = event.getTimeStamp();
                    }
                    notificationsSentState.sentCount++;
                }
            }
        }
        return notificationsSentState;
    }

    /* access modifiers changed from: private */
    public static NotificationsSentState getNotificationsSentState(ApplicationsState.AppEntry appEntry) {
        Object obj;
        if (appEntry == null || (obj = appEntry.extraInfo) == null || !(obj instanceof NotificationsSentState)) {
            return null;
        }
        return (NotificationsSentState) obj;
    }

    protected static String getKey(int i, String str) {
        return i + "|" + str;
    }

    public View.OnClickListener getSwitchOnClickListener(ApplicationsState.AppEntry appEntry) {
        if (appEntry != null) {
            return new View.OnClickListener(appEntry) {
                /* class com.android.settings.applications.$$Lambda$AppStateNotificationBridge$3yb6PrF82n91FG3YEHY_Ccl1JyI */
                public final /* synthetic */ ApplicationsState.AppEntry f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    AppStateNotificationBridge.this.lambda$getSwitchOnClickListener$0$AppStateNotificationBridge(this.f$1, view);
                }
            };
        }
        return null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getSwitchOnClickListener$0 */
    public /* synthetic */ void lambda$getSwitchOnClickListener$0$AppStateNotificationBridge(ApplicationsState.AppEntry appEntry, View view) {
        SwitchCompat switchCompat = (SwitchCompat) ((ViewGroup) view).findViewById(C0010R$id.switchWidget);
        if (switchCompat != null && switchCompat.isEnabled()) {
            switchCompat.toggle();
            NotificationsSentState notificationsSentState = getNotificationsSentState(appEntry);
            if (notificationsSentState == null || !notificationsSentState.instantApp) {
                NotificationBackend notificationBackend = this.mBackend;
                ApplicationInfo applicationInfo = appEntry.info;
                notificationBackend.setNotificationsEnabledForPackage(applicationInfo.packageName, applicationInfo.uid, switchCompat.isChecked());
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put("notify", Integer.valueOf(switchCompat.isChecked() ? 1 : 0));
                this.mContext.getContentResolver().update(Uri.withAppendedPath(BASE_URI, notificationsSentState.instantAppPKG), contentValues, null, null);
            }
            if (notificationsSentState != null) {
                notificationsSentState.blocked = !switchCompat.isChecked();
            }
        }
    }

    public static final boolean enableSwitch(ApplicationsState.AppEntry appEntry) {
        NotificationsSentState notificationsSentState = getNotificationsSentState(appEntry);
        if (notificationsSentState == null) {
            return false;
        }
        return notificationsSentState.blockable;
    }

    public static final boolean checkSwitch(ApplicationsState.AppEntry appEntry) {
        NotificationsSentState notificationsSentState = getNotificationsSentState(appEntry);
        if (notificationsSentState == null) {
            return false;
        }
        return !notificationsSentState.blocked;
    }
}
