package com.android.settings.development;

import android.content.om.OverlayInfo;
import java.util.function.ToIntFunction;

/* renamed from: com.android.settings.development.-$$Lambda$OverlayCategoryPreferenceController$RCMrfsrPVQZYqDXv-YOMB7C2Md8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$OverlayCategoryPreferenceController$RCMrfsrPVQZYqDXvYOMB7C2Md8 implements ToIntFunction {
    public static final /* synthetic */ $$Lambda$OverlayCategoryPreferenceController$RCMrfsrPVQZYqDXvYOMB7C2Md8 INSTANCE = new $$Lambda$OverlayCategoryPreferenceController$RCMrfsrPVQZYqDXvYOMB7C2Md8();

    private /* synthetic */ $$Lambda$OverlayCategoryPreferenceController$RCMrfsrPVQZYqDXvYOMB7C2Md8() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        return ((OverlayInfo) obj).priority;
    }
}
