package com.android.settings.notification;

import android.app.INotificationManager;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationHistory;
import android.app.role.RoleManager;
import android.app.usage.IUsageStatsManager;
import android.app.usage.UsageEvents;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ShortcutInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.service.notification.ConversationChannelWrapper;
import android.text.TextUtils;
import android.util.IconDrawableFactory;
import android.util.Log;
import com.android.settings.applications.AppStateNotificationBridge;
import com.android.settingslib.R$dimen;
import com.android.settingslib.R$plurals;
import com.android.settingslib.R$string;
import com.android.settingslib.Utils;
import com.android.settingslib.notification.ConversationIconFactory;
import com.android.settingslib.utils.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationBackend {
    static INotificationManager sINM = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    static IUsageStatsManager sUsageStatsManager = IUsageStatsManager.Stub.asInterface(ServiceManager.getService("usagestats"));
    public String mInstantAppPKG;

    public static class AppRow extends Row {
        public boolean banned;
        public int blockedChannelCount;
        public int bubblePreference = 0;
        public int channelCount;
        public Drawable icon;
        public CharSequence label;
        public boolean lockedImportance;
        public String pkg;
        public NotificationsSentState sentByApp;
        public Map<String, NotificationsSentState> sentByChannel;
        public Intent settingsIntent;
        public boolean showBadge;
        public boolean systemApp;
        public int uid;
        public int userId;
    }

    public static class NotificationsSentState {
        public int avgSentDaily = 0;
        public int avgSentWeekly = 0;
        public boolean instantApp;
        public Drawable instantAppIcon;
        public String instantAppName;
        public long lastSent = 0;
        public int sentCount = 0;
    }

    public boolean setLedEnabled(String str, boolean z) {
        return false;
    }

    public NotificationBackend() {
    }

    public NotificationBackend(String str) {
        this.mInstantAppPKG = str;
    }

    public AppRow loadAppRow(Context context, PackageManager packageManager, ApplicationInfo applicationInfo) {
        AppRow appRow = new AppRow();
        appRow.pkg = applicationInfo.packageName;
        appRow.uid = applicationInfo.uid;
        try {
            appRow.label = applicationInfo.loadLabel(packageManager);
        } catch (Throwable th) {
            Log.e("NotificationBackend", "Error loading application label for " + appRow.pkg, th);
            appRow.label = appRow.pkg;
        }
        appRow.icon = IconDrawableFactory.newInstance(context).getBadgedIcon(applicationInfo);
        appRow.banned = getNotificationsBanned(appRow.pkg, appRow.uid);
        appRow.showBadge = canShowBadge(appRow.pkg, appRow.uid);
        appRow.bubblePreference = getBubblePreference(appRow.pkg, appRow.uid);
        appRow.userId = UserHandle.getUserId(appRow.uid);
        appRow.blockedChannelCount = getBlockedChannelCount(appRow.pkg, appRow.uid);
        appRow.channelCount = getChannelCount(appRow.pkg, appRow.uid);
        recordAggregatedUsageEvents(context, appRow);
        if (!TextUtils.isEmpty(this.mInstantAppPKG)) {
            Cursor query = context.getContentResolver().query(Uri.withAppendedPath(AppStateNotificationBridge.BASE_URI, this.mInstantAppPKG), null, null, null, null);
            if (query != null && query.moveToFirst()) {
                String string = query.getString(query.getColumnIndex("name"));
                byte[] blob = query.getBlob(query.getColumnIndex("icon"));
                boolean z = false;
                Bitmap decodeByteArray = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                NotificationsSentState notificationsSentState = appRow.sentByApp;
                notificationsSentState.instantApp = true;
                notificationsSentState.instantAppName = string;
                notificationsSentState.instantAppIcon = new BitmapDrawable(context.getResources(), decodeByteArray);
                appRow.banned = query.getInt(query.getColumnIndex("notify")) == 0;
                if (query.getInt(query.getColumnIndex("badge")) == 1) {
                    z = true;
                }
                appRow.showBadge = z;
            }
            if (query != null) {
                query.close();
            }
        }
        return appRow;
    }

    public boolean isBlockable(Context context, ApplicationInfo applicationInfo) {
        boolean notificationsBanned = getNotificationsBanned(applicationInfo.packageName, applicationInfo.uid);
        boolean isSystemApp = isSystemApp(context, applicationInfo);
        return !isSystemApp || (isSystemApp && notificationsBanned);
    }

    public AppRow loadAppRow(Context context, PackageManager packageManager, RoleManager roleManager, PackageInfo packageInfo) {
        AppRow loadAppRow = loadAppRow(context, packageManager, packageInfo.applicationInfo);
        recordCanBeBlocked(context, packageManager, roleManager, packageInfo, loadAppRow);
        return loadAppRow;
    }

    /* access modifiers changed from: package-private */
    public void recordCanBeBlocked(Context context, PackageManager packageManager, RoleManager roleManager, PackageInfo packageInfo, AppRow appRow) {
        appRow.systemApp = Utils.isSystemPackage(context.getResources(), packageManager, packageInfo);
        List heldRolesFromController = roleManager.getHeldRolesFromController(packageInfo.packageName);
        if (heldRolesFromController.contains("android.app.role.DIALER") || heldRolesFromController.contains("android.app.role.EMERGENCY")) {
            appRow.systemApp = true;
        }
        markAppRowWithBlockables(context.getResources().getStringArray(17236062), appRow, packageInfo.packageName);
    }

    static void markAppRowWithBlockables(String[] strArr, AppRow appRow, String str) {
        if (strArr != null) {
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                String str2 = strArr[i];
                if (str2 != null && !str2.contains(":") && str.equals(strArr[i])) {
                    appRow.lockedImportance = true;
                    appRow.systemApp = true;
                }
            }
        }
    }

    public boolean isSystemApp(Context context, ApplicationInfo applicationInfo) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(applicationInfo.packageName, 64);
            RoleManager roleManager = (RoleManager) context.getSystemService(RoleManager.class);
            AppRow appRow = new AppRow();
            recordCanBeBlocked(context, context.getPackageManager(), roleManager, packageInfo, appRow);
            return appRow.systemApp;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean getNotificationsBanned(String str, int i) {
        try {
            return !sINM.areNotificationsEnabledForPackage(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public boolean setNotificationsEnabledForPackage(String str, int i, boolean z) {
        try {
            if (onlyHasDefaultChannel(str, i)) {
                NotificationChannel channel = getChannel(str, i, "miscellaneous", null);
                channel.setImportance(z ? -1000 : 0);
                updateChannel(str, i, channel);
            }
            sINM.setNotificationsEnabledForPackage(str, i, z);
            return true;
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public boolean canShowBadge(String str, int i) {
        try {
            return sINM.canShowBadge(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public boolean setShowBadge(String str, int i, boolean z) {
        try {
            sINM.setShowBadge(str, i, z);
            return true;
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public int getBubblePreference(String str, int i) {
        try {
            return sINM.getBubblePreferenceForPackage(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return -1;
        }
    }

    public boolean setAllowBubbles(String str, int i, int i2) {
        try {
            sINM.setBubblesAllowed(str, i, i2);
            return true;
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public NotificationChannel getChannel(String str, int i, String str2, String str3) {
        if (str2 == null) {
            return null;
        }
        try {
            return sINM.getNotificationChannelForPackage(str, i, str2, str3, true);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return null;
        }
    }

    public NotificationChannelGroup getGroup(String str, int i, String str2) {
        if (str2 == null) {
            return null;
        }
        try {
            return sINM.getNotificationChannelGroupForPackage(str2, str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return null;
        }
    }

    public ParceledListSlice<NotificationChannelGroup> getGroups(String str, int i) {
        try {
            return sINM.getNotificationChannelGroupsForPackage(str, i, false);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return ParceledListSlice.emptyList();
        }
    }

    public ParceledListSlice<ConversationChannelWrapper> getConversations(String str, int i) {
        try {
            return sINM.getConversationsForPackage(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return ParceledListSlice.emptyList();
        }
    }

    public ParceledListSlice<ConversationChannelWrapper> getConversations(boolean z) {
        try {
            return sINM.getConversations(z);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return ParceledListSlice.emptyList();
        }
    }

    public boolean hasSentValidMsg(String str, int i) {
        try {
            return sINM.hasSentValidMsg(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public boolean isInInvalidMsgState(String str, int i) {
        try {
            return sINM.isInInvalidMsgState(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public boolean hasUserDemotedInvalidMsgApp(String str, int i) {
        try {
            return sINM.hasUserDemotedInvalidMsgApp(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public void setInvalidMsgAppDemoted(String str, int i, boolean z) {
        try {
            sINM.setInvalidMsgAppDemoted(str, i, z);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
        }
    }

    public ParceledListSlice<NotificationChannel> getNotificationChannelsBypassingDnd(String str, int i) {
        try {
            return sINM.getNotificationChannelsBypassingDnd(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return ParceledListSlice.emptyList();
        }
    }

    public void updateChannel(String str, int i, NotificationChannel notificationChannel) {
        try {
            sINM.updateNotificationChannelForPackage(str, i, notificationChannel);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
        }
    }

    public void updateChannelGroup(String str, int i, NotificationChannelGroup notificationChannelGroup) {
        try {
            sINM.updateNotificationChannelGroupForPackage(str, i, notificationChannelGroup);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
        }
    }

    public int getDeletedChannelCount(String str, int i) {
        try {
            return sINM.getDeletedChannelCount(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return 0;
        }
    }

    public int getBlockedChannelCount(String str, int i) {
        try {
            return sINM.getBlockedChannelCount(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return 0;
        }
    }

    public boolean onlyHasDefaultChannel(String str, int i) {
        try {
            return sINM.onlyHasDefaultChannel(str, i);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public int getChannelCount(String str, int i) {
        try {
            return sINM.getNumNotificationChannelsForPackage(str, i, false);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return 0;
        }
    }

    public int getBlockedAppCount() {
        try {
            return sINM.getBlockedAppCount(UserHandle.myUserId());
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return 0;
        }
    }

    public boolean shouldHideSilentStatusBarIcons(Context context) {
        try {
            return sINM.shouldHideSilentStatusIcons(context.getPackageName());
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public void setHideSilentStatusIcons(boolean z) {
        try {
            sINM.setHideSilentStatusIcons(z);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
        }
    }

    public void allowAssistantAdjustment(String str, boolean z) {
        if (z) {
            try {
                sINM.allowAssistantAdjustment(str);
            } catch (Exception e) {
                Log.w("NotificationBackend", "Error calling NoMan", e);
            }
        } else {
            sINM.disallowAssistantAdjustment(str);
        }
    }

    public List<String> getAssistantAdjustments(String str) {
        try {
            return sINM.getAllowedAssistantAdjustments(str);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return new ArrayList();
        }
    }

    public boolean showSilentInStatusBar(String str) {
        try {
            return !sINM.shouldHideSilentStatusIcons(str);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public NotificationHistory getNotificationHistory(String str, String str2) {
        try {
            return sINM.getNotificationHistory(str, str2);
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return new NotificationHistory();
        }
    }

    /* access modifiers changed from: protected */
    public void recordAggregatedUsageEvents(Context context, AppRow appRow) {
        UsageEvents usageEvents;
        long currentTimeMillis = System.currentTimeMillis();
        try {
            usageEvents = sUsageStatsManager.queryEventsForPackageForUser(currentTimeMillis - 604800000, currentTimeMillis, appRow.userId, appRow.pkg, context.getPackageName());
        } catch (RemoteException e) {
            e.printStackTrace();
            usageEvents = null;
        }
        recordAggregatedUsageEvents(usageEvents, appRow);
    }

    /* access modifiers changed from: protected */
    public void recordAggregatedUsageEvents(UsageEvents usageEvents, AppRow appRow) {
        String str;
        appRow.sentByChannel = new HashMap();
        appRow.sentByApp = new NotificationsSentState();
        if (usageEvents != null) {
            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == 12 && (str = event.mNotificationChannelId) != null) {
                    NotificationsSentState notificationsSentState = appRow.sentByChannel.get(str);
                    if (notificationsSentState == null) {
                        notificationsSentState = new NotificationsSentState();
                        appRow.sentByChannel.put(str, notificationsSentState);
                    }
                    if (event.getTimeStamp() > notificationsSentState.lastSent) {
                        notificationsSentState.lastSent = event.getTimeStamp();
                        appRow.sentByApp.lastSent = event.getTimeStamp();
                    }
                    notificationsSentState.sentCount++;
                    appRow.sentByApp.sentCount++;
                    calculateAvgSentCounts(notificationsSentState);
                }
            }
            calculateAvgSentCounts(appRow.sentByApp);
        }
    }

    public static CharSequence getSentSummary(Context context, NotificationsSentState notificationsSentState, boolean z) {
        if (notificationsSentState == null) {
            return null;
        }
        if (z) {
            if (notificationsSentState.lastSent == 0) {
                return context.getString(R$string.notifications_sent_never);
            }
            return StringUtil.formatRelativeTime(context, (double) (System.currentTimeMillis() - notificationsSentState.lastSent), true);
        } else if (notificationsSentState.avgSentDaily > 0) {
            Resources resources = context.getResources();
            int i = R$plurals.notifications_sent_daily;
            int i2 = notificationsSentState.avgSentDaily;
            return resources.getQuantityString(i, i2, Integer.valueOf(i2));
        } else {
            Resources resources2 = context.getResources();
            int i3 = R$plurals.notifications_sent_weekly;
            int i4 = notificationsSentState.avgSentWeekly;
            return resources2.getQuantityString(i3, i4, Integer.valueOf(i4));
        }
    }

    private void calculateAvgSentCounts(NotificationsSentState notificationsSentState) {
        if (notificationsSentState != null) {
            notificationsSentState.avgSentDaily = Math.round(((float) notificationsSentState.sentCount) / 7.0f);
            int i = notificationsSentState.sentCount;
            if (i < 7) {
                notificationsSentState.avgSentWeekly = i;
            }
        }
    }

    public ComponentName getAllowedNotificationAssistant() {
        try {
            return sINM.getAllowedNotificationAssistant();
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return null;
        }
    }

    public boolean setNotificationAssistantGranted(ComponentName componentName) {
        try {
            sINM.setNotificationAssistantAccessGranted(componentName, true);
            if (componentName == null) {
                return sINM.getAllowedNotificationAssistant() == null;
            }
            return componentName.equals(sINM.getAllowedNotificationAssistant());
        } catch (Exception e) {
            Log.w("NotificationBackend", "Error calling NoMan", e);
            return false;
        }
    }

    public ShortcutInfo getConversationInfo(Context context, String str, int i, String str2) {
        List<ShortcutInfo> shortcuts = ((LauncherApps) context.getSystemService(LauncherApps.class)).getShortcuts(new LauncherApps.ShortcutQuery().setPackage(str).setQueryFlags(1041).setShortcutIds(Arrays.asList(str2)), UserHandle.of(UserHandle.getUserId(i)));
        if (shortcuts == null || shortcuts.isEmpty()) {
            return null;
        }
        return shortcuts.get(0);
    }

    public Drawable getConversationDrawable(Context context, ShortcutInfo shortcutInfo, String str, int i, boolean z) {
        if (shortcutInfo == null) {
            return null;
        }
        return new ConversationIconFactory(context, (LauncherApps) context.getSystemService(LauncherApps.class), context.getPackageManager(), IconDrawableFactory.newInstance(context, false), context.getResources().getDimensionPixelSize(R$dimen.conversation_icon_size)).getConversationDrawable(shortcutInfo, str, i, z);
    }

    static class Row {
        Row() {
        }
    }
}
