package com.android.settings.datausage;

import com.android.settings.datausage.ChartDataUsagePreference;
import java.util.function.ToLongFunction;

/* renamed from: com.android.settings.datausage.-$$Lambda$Zg9pPJv8QPElol3BBOi0EKPeRfc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$Zg9pPJv8QPElol3BBOi0EKPeRfc implements ToLongFunction {
    public static final /* synthetic */ $$Lambda$Zg9pPJv8QPElol3BBOi0EKPeRfc INSTANCE = new $$Lambda$Zg9pPJv8QPElol3BBOi0EKPeRfc();

    private /* synthetic */ $$Lambda$Zg9pPJv8QPElol3BBOi0EKPeRfc() {
    }

    @Override // java.util.function.ToLongFunction
    public final long applyAsLong(Object obj) {
        return ((ChartDataUsagePreference.DataUsageSummaryNode) obj).getEndTime();
    }
}
