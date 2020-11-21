package com.android.settings.wifi;

import com.android.wifitrackerlib.WifiEntry;
import java.util.function.Predicate;

/* renamed from: com.android.settings.wifi.-$$Lambda$WifiSettings2$YMjSsDH3G5ATY01tOtfOmyk7hCA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$WifiSettings2$YMjSsDH3G5ATY01tOtfOmyk7hCA implements Predicate {
    public static final /* synthetic */ $$Lambda$WifiSettings2$YMjSsDH3G5ATY01tOtfOmyk7hCA INSTANCE = new $$Lambda$WifiSettings2$YMjSsDH3G5ATY01tOtfOmyk7hCA();

    private /* synthetic */ $$Lambda$WifiSettings2$YMjSsDH3G5ATY01tOtfOmyk7hCA() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return WifiSettings2.lambda$onWifiEntriesChanged$4((WifiEntry) obj);
    }
}
