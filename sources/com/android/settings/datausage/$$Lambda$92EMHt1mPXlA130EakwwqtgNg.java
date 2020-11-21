package com.android.settings.datausage;

import com.android.settings.datausage.ChartDataUsagePreference;
import java.util.function.Function;

/* renamed from: com.android.settings.datausage.-$$Lambda$92E--MHt1mPXlA130EakwwqtgNg  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$92EMHt1mPXlA130EakwwqtgNg implements Function {
    public static final /* synthetic */ $$Lambda$92EMHt1mPXlA130EakwwqtgNg INSTANCE = new $$Lambda$92EMHt1mPXlA130EakwwqtgNg();

    private /* synthetic */ $$Lambda$92EMHt1mPXlA130EakwwqtgNg() {
    }

    @Override // java.util.function.Function
    public final Object apply(Object obj) {
        return Integer.valueOf(((ChartDataUsagePreference.DataUsageSummaryNode) obj).getDataUsagePercentage());
    }
}
