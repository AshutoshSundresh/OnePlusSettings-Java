package com.android.settings.notification.history;

import android.app.NotificationHistory;
import android.graphics.drawable.Drawable;
import java.util.Objects;
import java.util.TreeSet;

public class NotificationHistoryPackage {
    Drawable icon;
    CharSequence label;
    TreeSet<NotificationHistory.HistoricalNotification> notifications = new TreeSet<>($$Lambda$NotificationHistoryPackage$F3gJkMhaAe1Ef2cbTLXLXekm5Rk.INSTANCE);
    String pkgName;
    int uid;

    public NotificationHistoryPackage(String str, int i) {
        this.pkgName = str;
        this.uid = i;
    }

    public long getMostRecent() {
        if (this.notifications.isEmpty()) {
            return 0;
        }
        return this.notifications.first().getPostedTimeMs();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || NotificationHistoryPackage.class != obj.getClass()) {
            return false;
        }
        NotificationHistoryPackage notificationHistoryPackage = (NotificationHistoryPackage) obj;
        return this.uid == notificationHistoryPackage.uid && Objects.equals(this.pkgName, notificationHistoryPackage.pkgName) && Objects.equals(this.notifications, notificationHistoryPackage.notifications) && Objects.equals(this.label, notificationHistoryPackage.label) && Objects.equals(this.icon, notificationHistoryPackage.icon);
    }

    public int hashCode() {
        return Objects.hash(this.pkgName, Integer.valueOf(this.uid), this.notifications, this.label, this.icon);
    }
}
