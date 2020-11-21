package com.android.settings.datausage;

import com.android.settings.datausage.ChartDataUsagePreference;
import java.util.function.ToIntFunction;

/* renamed from: com.android.settings.datausage.-$$Lambda$7k7RmT7rN3prw6YvDhpJTq7E_bI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$7k7RmT7rN3prw6YvDhpJTq7E_bI implements ToIntFunction {
    public static final /* synthetic */ $$Lambda$7k7RmT7rN3prw6YvDhpJTq7E_bI INSTANCE = new $$Lambda$7k7RmT7rN3prw6YvDhpJTq7E_bI();

    private /* synthetic */ $$Lambda$7k7RmT7rN3prw6YvDhpJTq7E_bI() {
    }

    @Override // java.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        return ((ChartDataUsagePreference.DataUsageSummaryNode) obj).getDataUsagePercentage();
    }
}
