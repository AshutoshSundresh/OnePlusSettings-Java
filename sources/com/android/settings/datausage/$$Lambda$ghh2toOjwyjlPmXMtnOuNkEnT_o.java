package com.android.settings.datausage;

import com.android.settingslib.net.NetworkCycleData;
import java.util.function.ToLongFunction;

/* renamed from: com.android.settings.datausage.-$$Lambda$ghh2toOjwyjlPmXMtnOuNkEnT_o  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ghh2toOjwyjlPmXMtnOuNkEnT_o implements ToLongFunction {
    public static final /* synthetic */ $$Lambda$ghh2toOjwyjlPmXMtnOuNkEnT_o INSTANCE = new $$Lambda$ghh2toOjwyjlPmXMtnOuNkEnT_o();

    private /* synthetic */ $$Lambda$ghh2toOjwyjlPmXMtnOuNkEnT_o() {
    }

    @Override // java.util.function.ToLongFunction
    public final long applyAsLong(Object obj) {
        return ((NetworkCycleData) obj).getTotalUsage();
    }
}
