package com.android.settings.homepage.contextualcards;

import java.util.Comparator;

/* renamed from: com.android.settings.homepage.contextualcards.-$$Lambda$ContextualCardManager$Gw08Tb6P3Z00HUKmrUC8W3DcoSw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ContextualCardManager$Gw08Tb6P3Z00HUKmrUC8W3DcoSw implements Comparator {
    public static final /* synthetic */ $$Lambda$ContextualCardManager$Gw08Tb6P3Z00HUKmrUC8W3DcoSw INSTANCE = new $$Lambda$ContextualCardManager$Gw08Tb6P3Z00HUKmrUC8W3DcoSw();

    private /* synthetic */ $$Lambda$ContextualCardManager$Gw08Tb6P3Z00HUKmrUC8W3DcoSw() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return Double.compare(((ContextualCard) obj2).getRankingScore(), ((ContextualCard) obj).getRankingScore());
    }
}
