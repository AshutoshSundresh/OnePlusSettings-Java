package com.android.settings.datausage;

import com.android.settings.datausage.ChartDataUsagePreference;
import java.util.function.ToLongFunction;

/* renamed from: com.android.settings.datausage.-$$Lambda$0fsS-g0zNz3crDgww2accED2sC8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$0fsSg0zNz3crDgww2accED2sC8 implements ToLongFunction {
    public static final /* synthetic */ $$Lambda$0fsSg0zNz3crDgww2accED2sC8 INSTANCE = new $$Lambda$0fsSg0zNz3crDgww2accED2sC8();

    private /* synthetic */ $$Lambda$0fsSg0zNz3crDgww2accED2sC8() {
    }

    @Override // java.util.function.ToLongFunction
    public final long applyAsLong(Object obj) {
        return ((ChartDataUsagePreference.DataUsageSummaryNode) obj).getStartTime();
    }
}
