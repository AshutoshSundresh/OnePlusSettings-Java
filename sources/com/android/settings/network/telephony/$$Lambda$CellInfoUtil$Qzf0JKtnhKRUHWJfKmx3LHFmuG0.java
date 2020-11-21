package com.android.settings.network.telephony;

import android.telephony.CellInfo;
import java.util.function.Function;

/* renamed from: com.android.settings.network.telephony.-$$Lambda$CellInfoUtil$Qzf0JKtnhKRUHWJfKmx3LHFmuG0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$CellInfoUtil$Qzf0JKtnhKRUHWJfKmx3LHFmuG0 implements Function {
    public static final /* synthetic */ $$Lambda$CellInfoUtil$Qzf0JKtnhKRUHWJfKmx3LHFmuG0 INSTANCE = new $$Lambda$CellInfoUtil$Qzf0JKtnhKRUHWJfKmx3LHFmuG0();

    private /* synthetic */ $$Lambda$CellInfoUtil$Qzf0JKtnhKRUHWJfKmx3LHFmuG0() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return CellInfoUtil.cellInfoToString((CellInfo) obj);
    }
}
