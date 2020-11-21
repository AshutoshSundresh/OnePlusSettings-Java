package com.google.android.setupcompat.logging;

import android.content.Context;
import com.google.android.setupcompat.internal.Preconditions;
import com.google.android.setupcompat.internal.SetupCompatServiceInvoker;
import com.google.android.setupcompat.logging.internal.MetricBundleConverter;

public class SetupMetricsLogger {
    public static void logCustomEvent(Context context, CustomEvent customEvent) {
        Preconditions.checkNotNull(context, "Context cannot be null.");
        Preconditions.checkNotNull(customEvent, "CustomEvent cannot be null.");
        SetupCompatServiceInvoker.get(context).logMetricEvent(1, MetricBundleConverter.createBundleForLogging(customEvent));
    }

    public static void logDuration(Context context, MetricKey metricKey, long j) {
        Preconditions.checkNotNull(context, "Context cannot be null.");
        Preconditions.checkNotNull(metricKey, "Timer name cannot be null.");
        Preconditions.checkArgument(j >= 0, "Duration cannot be negative.");
        SetupCompatServiceInvoker.get(context).logMetricEvent(2, MetricBundleConverter.createBundleForLoggingTimer(metricKey, j));
    }
}
