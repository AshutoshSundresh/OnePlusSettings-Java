package com.android.settings.notification.history;

import android.app.NotificationHistory;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import android.util.Slog;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.history.HistoryLoader;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HistoryLoader {
    private final NotificationBackend mBackend;
    private final Context mContext;
    private final PackageManager mPm;

    /* access modifiers changed from: package-private */
    public interface OnHistoryLoaderListener {
        void onHistoryLoaded(List<NotificationHistoryPackage> list);
    }

    public HistoryLoader(Context context, NotificationBackend notificationBackend, PackageManager packageManager) {
        this.mContext = context;
        this.mBackend = notificationBackend;
        this.mPm = packageManager;
    }

    public void load(OnHistoryLoaderListener onHistoryLoaderListener) {
        ThreadUtils.postOnBackgroundThread(new Runnable(onHistoryLoaderListener) {
            /* class com.android.settings.notification.history.$$Lambda$HistoryLoader$uQIOoUuQa7GJVkIYWinaA87t1w */
            public final /* synthetic */ HistoryLoader.OnHistoryLoaderListener f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                HistoryLoader.this.lambda$load$2$HistoryLoader(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$load$2 */
    public /* synthetic */ void lambda$load$2$HistoryLoader(OnHistoryLoaderListener onHistoryLoaderListener) {
        try {
            HashMap hashMap = new HashMap();
            NotificationHistory notificationHistory = this.mBackend.getNotificationHistory(this.mContext.getPackageName(), this.mContext.getAttributionTag());
            while (notificationHistory.hasNextNotification()) {
                NotificationHistory.HistoricalNotification nextNotification = notificationHistory.getNextNotification();
                String str = nextNotification.getPackage() + "|" + nextNotification.getUid();
                NotificationHistoryPackage notificationHistoryPackage = (NotificationHistoryPackage) hashMap.getOrDefault(str, new NotificationHistoryPackage(nextNotification.getPackage(), nextNotification.getUid()));
                notificationHistoryPackage.notifications.add(nextNotification);
                hashMap.put(str, notificationHistoryPackage);
            }
            ArrayList<NotificationHistoryPackage> arrayList = new ArrayList(hashMap.values());
            Collections.sort(arrayList, $$Lambda$HistoryLoader$3P0zrtX5h9vNKXuBLjowRXTNVB4.INSTANCE);
            for (NotificationHistoryPackage notificationHistoryPackage2 : arrayList) {
                try {
                    ApplicationInfo applicationInfoAsUser = this.mPm.getApplicationInfoAsUser(notificationHistoryPackage2.pkgName, 795136, UserHandle.getUserId(notificationHistoryPackage2.uid));
                    if (applicationInfoAsUser != null) {
                        notificationHistoryPackage2.label = String.valueOf(this.mPm.getApplicationLabel(applicationInfoAsUser));
                        notificationHistoryPackage2.icon = this.mPm.getUserBadgedIcon(this.mPm.getApplicationIcon(applicationInfoAsUser), UserHandle.of(UserHandle.getUserId(notificationHistoryPackage2.uid)));
                    }
                } catch (PackageManager.NameNotFoundException unused) {
                    notificationHistoryPackage2.icon = this.mPm.getDefaultActivityIcon();
                }
            }
            ThreadUtils.postOnMainThread(new Runnable(arrayList) {
                /* class com.android.settings.notification.history.$$Lambda$HistoryLoader$BdJnbuIW0bGWnhQE_rnOq_5juo */
                public final /* synthetic */ List f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    HistoryLoader.OnHistoryLoaderListener.this.onHistoryLoaded(this.f$1);
                }
            });
        } catch (Exception e) {
            Slog.e("HistoryLoader", "Error loading history", e);
        }
    }

    static /* synthetic */ int lambda$load$0(NotificationHistoryPackage notificationHistoryPackage, NotificationHistoryPackage notificationHistoryPackage2) {
        return Long.compare(notificationHistoryPackage.getMostRecent(), notificationHistoryPackage2.getMostRecent()) * -1;
    }
}
