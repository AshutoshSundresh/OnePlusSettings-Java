package com.android.settings.accessibility;

import com.android.settings.accessibility.VibrationPreferenceFragment;
import java.util.function.Function;

/* renamed from: com.android.settings.accessibility.-$$Lambda$_Oh9z60fg9jQX72D1CuzQSHZqtM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$_Oh9z60fg9jQX72D1CuzQSHZqtM implements Function {
    public static final /* synthetic */ $$Lambda$_Oh9z60fg9jQX72D1CuzQSHZqtM INSTANCE = new $$Lambda$_Oh9z60fg9jQX72D1CuzQSHZqtM();

    private /* synthetic */ $$Lambda$_Oh9z60fg9jQX72D1CuzQSHZqtM() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return Integer.valueOf(((VibrationPreferenceFragment.VibrationIntensityCandidateInfo) obj).getIntensity());
    }
}
