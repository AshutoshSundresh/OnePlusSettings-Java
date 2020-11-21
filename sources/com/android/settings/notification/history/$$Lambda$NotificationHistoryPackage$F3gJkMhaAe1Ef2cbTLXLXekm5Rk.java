package com.android.settings.notification.history;

import android.app.NotificationHistory;
import java.util.Comparator;

/* renamed from: com.android.settings.notification.history.-$$Lambda$NotificationHistoryPackage$F3gJkMhaAe1Ef2cbTLXLXekm5Rk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$NotificationHistoryPackage$F3gJkMhaAe1Ef2cbTLXLXekm5Rk implements Comparator {
    public static final /* synthetic */ $$Lambda$NotificationHistoryPackage$F3gJkMhaAe1Ef2cbTLXLXekm5Rk INSTANCE = new $$Lambda$NotificationHistoryPackage$F3gJkMhaAe1Ef2cbTLXLXekm5Rk();

    private /* synthetic */ $$Lambda$NotificationHistoryPackage$F3gJkMhaAe1Ef2cbTLXLXekm5Rk() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return Long.compare(((NotificationHistory.HistoricalNotification) obj2).getPostedTimeMs(), ((NotificationHistory.HistoricalNotification) obj).getPostedTimeMs());
    }
}
