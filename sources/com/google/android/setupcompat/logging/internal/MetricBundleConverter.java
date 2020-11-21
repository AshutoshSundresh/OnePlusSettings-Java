package com.google.android.setupcompat.logging.internal;

import android.os.Bundle;
import com.google.android.setupcompat.logging.CustomEvent;
import com.google.android.setupcompat.logging.MetricKey;

public final class MetricBundleConverter {
    public static Bundle createBundleForLogging(CustomEvent customEvent) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("CustomEvent_bundle", CustomEvent.toBundle(customEvent));
        return bundle;
    }

    public static Bundle createBundleForLoggingTimer(MetricKey metricKey, long j) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("MetricKey_bundle", MetricKey.fromMetricKey(metricKey));
        bundle.putLong("timeMillis", j);
        return bundle;
    }
}
