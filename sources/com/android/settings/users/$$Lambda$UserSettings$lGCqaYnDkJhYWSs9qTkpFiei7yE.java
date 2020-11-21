package com.android.settings.users;

import android.content.pm.UserInfo;
import java.util.function.Predicate;

/* renamed from: com.android.settings.users.-$$Lambda$UserSettings$lGCqaYnDkJhYWSs9qTkpFiei7yE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$UserSettings$lGCqaYnDkJhYWSs9qTkpFiei7yE implements Predicate {
    public static final /* synthetic */ $$Lambda$UserSettings$lGCqaYnDkJhYWSs9qTkpFiei7yE INSTANCE = new $$Lambda$UserSettings$lGCqaYnDkJhYWSs9qTkpFiei7yE();

    private /* synthetic */ $$Lambda$UserSettings$lGCqaYnDkJhYWSs9qTkpFiei7yE() {
    }

    @Override // java.util.function.Predicate
    public final boolean test(Object obj) {
        return UserSettings.lambda$getRealUsersCount$0((UserInfo) obj);
    }
}
