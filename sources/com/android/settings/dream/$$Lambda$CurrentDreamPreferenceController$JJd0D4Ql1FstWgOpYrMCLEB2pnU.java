package com.android.settings.dream;

import com.android.settingslib.dream.DreamBackend;
import java.util.function.Predicate;

/* renamed from: com.android.settings.dream.-$$Lambda$CurrentDreamPreferenceController$JJd0D4Ql1FstWgOpYrMCLEB2pnU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$CurrentDreamPreferenceController$JJd0D4Ql1FstWgOpYrMCLEB2pnU implements Predicate {
    public static final /* synthetic */ $$Lambda$CurrentDreamPreferenceController$JJd0D4Ql1FstWgOpYrMCLEB2pnU INSTANCE = new $$Lambda$CurrentDreamPreferenceController$JJd0D4Ql1FstWgOpYrMCLEB2pnU();

    private /* synthetic */ $$Lambda$CurrentDreamPreferenceController$JJd0D4Ql1FstWgOpYrMCLEB2pnU() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((DreamBackend.DreamInfo) obj).isActive;
    }
}
