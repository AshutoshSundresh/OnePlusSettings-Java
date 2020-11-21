package com.android.settings.notification.history;

import android.app.NotificationHistory;
import java.util.Comparator;

/* renamed from: com.android.settings.notification.history.-$$Lambda$NotificationHistoryAdapter$hS13J5m3lQ9o8T4ity6Z5iDBQVE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationHistoryAdapter$hS13J5m3lQ9o8T4ity6Z5iDBQVE implements Comparator {
    public static final /* synthetic */ $$Lambda$NotificationHistoryAdapter$hS13J5m3lQ9o8T4ity6Z5iDBQVE INSTANCE = new $$Lambda$NotificationHistoryAdapter$hS13J5m3lQ9o8T4ity6Z5iDBQVE();

    private /* synthetic */ $$Lambda$NotificationHistoryAdapter$hS13J5m3lQ9o8T4ity6Z5iDBQVE() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return Long.compare(((NotificationHistory.HistoricalNotification) obj2).getPostedTimeMs(), ((NotificationHistory.HistoricalNotification) obj).getPostedTimeMs());
    }
}
