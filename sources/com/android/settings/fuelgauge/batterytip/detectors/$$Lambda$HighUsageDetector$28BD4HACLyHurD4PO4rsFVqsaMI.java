package com.android.settings.fuelgauge.batterytip.detectors;

import com.android.internal.os.BatterySipper;
import java.util.Comparator;

/* renamed from: com.android.settings.fuelgauge.batterytip.detectors.-$$Lambda$HighUsageDetector$28BD4HACLyHurD4PO4rsFVqsaMI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$HighUsageDetector$28BD4HACLyHurD4PO4rsFVqsaMI implements Comparator {
    public static final /* synthetic */ $$Lambda$HighUsageDetector$28BD4HACLyHurD4PO4rsFVqsaMI INSTANCE = new $$Lambda$HighUsageDetector$28BD4HACLyHurD4PO4rsFVqsaMI();

    private /* synthetic */ $$Lambda$HighUsageDetector$28BD4HACLyHurD4PO4rsFVqsaMI() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return Double.compare(((BatterySipper) obj2).totalSmearedPowerMah, ((BatterySipper) obj).totalSmearedPowerMah);
    }
}
