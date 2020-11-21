package com.android.settingslib.fuelgauge;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import java.time.Duration;
import java.time.Instant;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: Estimate.kt */
public final class Estimate {
    public static final Companion Companion = new Companion(null);
    private final long averageDischargeTime;
    private final long estimateMillis;
    private final boolean isBasedOnUsage;

    @Nullable
    public static final Estimate getCachedEstimateIfAvailable(@NotNull Context context) {
        return Companion.getCachedEstimateIfAvailable(context);
    }

    @NotNull
    public static final Instant getLastCacheUpdateTime(@NotNull Context context) {
        return Companion.getLastCacheUpdateTime(context);
    }

    public static final void storeCachedEstimate(@NotNull Context context, @NotNull Estimate estimate) {
        Companion.storeCachedEstimate(context, estimate);
    }

    public Estimate(long j, boolean z, long j2) {
        this.estimateMillis = j;
        this.isBasedOnUsage = z;
        this.averageDischargeTime = j2;
    }

    public final long getEstimateMillis() {
        return this.estimateMillis;
    }

    public final boolean isBasedOnUsage() {
        return this.isBasedOnUsage;
    }

    public final long getAverageDischargeTime() {
        return this.averageDischargeTime;
    }

    /* compiled from: Estimate.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        @Nullable
        public final Estimate getCachedEstimateIfAvailable(@NotNull Context context) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            ContentResolver contentResolver = context.getContentResolver();
            if (Duration.between(Instant.ofEpochMilli(Settings.Global.getLong(contentResolver, "battery_estimates_last_update_time", -1)), Instant.now()).compareTo(Duration.ofMinutes(1)) > 0) {
                return null;
            }
            long j = (long) -1;
            long j2 = Settings.Global.getLong(contentResolver, "time_remaining_estimate_millis", j);
            boolean z = false;
            if (Settings.Global.getInt(contentResolver, "time_remaining_estimate_based_on_usage", 0) == 1) {
                z = true;
            }
            return new Estimate(j2, z, Settings.Global.getLong(contentResolver, "average_time_to_discharge", j));
        }

        public final void storeCachedEstimate(@NotNull Context context, @NotNull Estimate estimate) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(estimate, "estimate");
            ContentResolver contentResolver = context.getContentResolver();
            Settings.Global.putLong(contentResolver, "time_remaining_estimate_millis", estimate.getEstimateMillis());
            Settings.Global.putInt(contentResolver, "time_remaining_estimate_based_on_usage", estimate.isBasedOnUsage() ? 1 : 0);
            Settings.Global.putLong(contentResolver, "average_time_to_discharge", estimate.getAverageDischargeTime());
            Settings.Global.putLong(contentResolver, "battery_estimates_last_update_time", System.currentTimeMillis());
        }

        @NotNull
        public final Instant getLastCacheUpdateTime(@NotNull Context context) {
            Intrinsics.checkParameterIsNotNull(context, "context");
            Instant ofEpochMilli = Instant.ofEpochMilli(Settings.Global.getLong(context.getContentResolver(), "battery_estimates_last_update_time", -1));
            Intrinsics.checkExpressionValueIsNotNull(ofEpochMilli, "Instant.ofEpochMilli(\n  …                     -1))");
            return ofEpochMilli;
        }
    }
}
