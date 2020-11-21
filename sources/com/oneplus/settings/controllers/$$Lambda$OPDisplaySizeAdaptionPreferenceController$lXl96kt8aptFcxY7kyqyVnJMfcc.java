package com.oneplus.settings.controllers;

import android.content.om.OverlayInfo;
import java.util.function.ToIntFunction;

/* renamed from: com.oneplus.settings.controllers.-$$Lambda$OPDisplaySizeAdaptionPreferenceController$lXl96kt8aptFcxY7kyqyVnJMfcc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$OPDisplaySizeAdaptionPreferenceController$lXl96kt8aptFcxY7kyqyVnJMfcc implements ToIntFunction {
    public static final /* synthetic */ $$Lambda$OPDisplaySizeAdaptionPreferenceController$lXl96kt8aptFcxY7kyqyVnJMfcc INSTANCE = new $$Lambda$OPDisplaySizeAdaptionPreferenceController$lXl96kt8aptFcxY7kyqyVnJMfcc();

    private /* synthetic */ $$Lambda$OPDisplaySizeAdaptionPreferenceController$lXl96kt8aptFcxY7kyqyVnJMfcc() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        return ((OverlayInfo) obj).priority;
    }
}
