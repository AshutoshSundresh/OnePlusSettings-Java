package com.android.settings.dream;

import com.android.settings.dream.CurrentDreamPicker;
import com.android.settingslib.dream.DreamBackend;
import java.util.function.Function;

/* renamed from: com.android.settings.dream.-$$Lambda$hBSizG3ais67bSjAeIqNEa6sDBo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$hBSizG3ais67bSjAeIqNEa6sDBo implements Function {
    public static final /* synthetic */ $$Lambda$hBSizG3ais67bSjAeIqNEa6sDBo INSTANCE = new $$Lambda$hBSizG3ais67bSjAeIqNEa6sDBo();

    private /* synthetic */ $$Lambda$hBSizG3ais67bSjAeIqNEa6sDBo() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return new CurrentDreamPicker.DreamCandidateInfo((DreamBackend.DreamInfo) obj);
    }
}
