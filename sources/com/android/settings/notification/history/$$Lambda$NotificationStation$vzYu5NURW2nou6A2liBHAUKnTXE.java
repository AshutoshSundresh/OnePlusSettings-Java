package com.android.settings.notification.history;

import com.android.settings.notification.history.NotificationStation;
import java.util.Comparator;

/* renamed from: com.android.settings.notification.history.-$$Lambda$NotificationStation$vzYu5NURW2nou6A2liBHAUKnTXE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationStation$vzYu5NURW2nou6A2liBHAUKnTXE implements Comparator {
    public static final /* synthetic */ $$Lambda$NotificationStation$vzYu5NURW2nou6A2liBHAUKnTXE INSTANCE = new $$Lambda$NotificationStation$vzYu5NURW2nou6A2liBHAUKnTXE();

    private /* synthetic */ $$Lambda$NotificationStation$vzYu5NURW2nou6A2liBHAUKnTXE() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return Long.compare(((NotificationStation.HistoricalNotificationInfo) obj2).timestamp, ((NotificationStation.HistoricalNotificationInfo) obj).timestamp);
    }
}
