package com.android.settings.development;

import android.content.om.OverlayInfo;
import java.util.function.Predicate;

/* renamed from: com.android.settings.development.-$$Lambda$OverlayCategoryPreferenceController$ImUcEZADd2hoyz2UKfsCeptUZ30  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$OverlayCategoryPreferenceController$ImUcEZADd2hoyz2UKfsCeptUZ30 implements Predicate {
    public static final /* synthetic */ $$Lambda$OverlayCategoryPreferenceController$ImUcEZADd2hoyz2UKfsCeptUZ30 INSTANCE = new $$Lambda$OverlayCategoryPreferenceController$ImUcEZADd2hoyz2UKfsCeptUZ30();

    private /* synthetic */ $$Lambda$OverlayCategoryPreferenceController$ImUcEZADd2hoyz2UKfsCeptUZ30() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((OverlayInfo) obj).isEnabled();
    }
}
