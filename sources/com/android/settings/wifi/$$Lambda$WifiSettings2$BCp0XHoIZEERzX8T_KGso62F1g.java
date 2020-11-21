package com.android.settings.wifi;

import com.android.wifitrackerlib.WifiEntry;
import java.util.function.Predicate;

/* renamed from: com.android.settings.wifi.-$$Lambda$WifiSettings2$BCp0XHoI-ZEERzX8T_KGso62F1g  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$WifiSettings2$BCp0XHoIZEERzX8T_KGso62F1g implements Predicate {
    public static final /* synthetic */ $$Lambda$WifiSettings2$BCp0XHoIZEERzX8T_KGso62F1g INSTANCE = new $$Lambda$WifiSettings2$BCp0XHoIZEERzX8T_KGso62F1g();

    private /* synthetic */ $$Lambda$WifiSettings2$BCp0XHoIZEERzX8T_KGso62F1g() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return WifiSettings2.lambda$onWifiEntriesChanged$3((WifiEntry) obj);
    }
}
