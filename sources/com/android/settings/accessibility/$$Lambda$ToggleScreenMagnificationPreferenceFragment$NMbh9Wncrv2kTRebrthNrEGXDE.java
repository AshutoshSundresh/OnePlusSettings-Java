package com.android.settings.accessibility;

import java.util.function.Predicate;

/* renamed from: com.android.settings.accessibility.-$$Lambda$ToggleScreenMagnificationPreferenceFragment$NMbh9Wncrv2k-TRebrthNrEGXDE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ToggleScreenMagnificationPreferenceFragment$NMbh9Wncrv2kTRebrthNrEGXDE implements Predicate {
    public static final /* synthetic */ $$Lambda$ToggleScreenMagnificationPreferenceFragment$NMbh9Wncrv2kTRebrthNrEGXDE INSTANCE = new $$Lambda$ToggleScreenMagnificationPreferenceFragment$NMbh9Wncrv2kTRebrthNrEGXDE();

    private /* synthetic */ $$Lambda$ToggleScreenMagnificationPreferenceFragment$NMbh9Wncrv2kTRebrthNrEGXDE() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((String) obj).contains("com.android.server.accessibility.MagnificationController");
    }
}
