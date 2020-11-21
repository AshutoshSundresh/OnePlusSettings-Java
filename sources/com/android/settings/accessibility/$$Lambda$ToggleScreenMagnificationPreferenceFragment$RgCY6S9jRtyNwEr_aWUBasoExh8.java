package com.android.settings.accessibility;

import java.util.function.Predicate;

/* renamed from: com.android.settings.accessibility.-$$Lambda$ToggleScreenMagnificationPreferenceFragment$RgCY6S9jRtyNwEr_aWUBasoExh8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ToggleScreenMagnificationPreferenceFragment$RgCY6S9jRtyNwEr_aWUBasoExh8 implements Predicate {
    public static final /* synthetic */ $$Lambda$ToggleScreenMagnificationPreferenceFragment$RgCY6S9jRtyNwEr_aWUBasoExh8 INSTANCE = new $$Lambda$ToggleScreenMagnificationPreferenceFragment$RgCY6S9jRtyNwEr_aWUBasoExh8();

    private /* synthetic */ $$Lambda$ToggleScreenMagnificationPreferenceFragment$RgCY6S9jRtyNwEr_aWUBasoExh8() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((String) obj).contains("com.android.server.accessibility.MagnificationController");
    }
}
