package com.android.settings.users;

import android.content.pm.UserInfo;
import java.util.function.Predicate;

/* renamed from: com.android.settings.users.-$$Lambda$r_pzZf2EH57SXB-6m9pn4NfJPfk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$r_pzZf2EH57SXB6m9pn4NfJPfk implements Predicate {
    public static final /* synthetic */ $$Lambda$r_pzZf2EH57SXB6m9pn4NfJPfk INSTANCE = new $$Lambda$r_pzZf2EH57SXB6m9pn4NfJPfk();

    private /* synthetic */ $$Lambda$r_pzZf2EH57SXB6m9pn4NfJPfk() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return ((UserInfo) obj).isGuest();
    }
}
