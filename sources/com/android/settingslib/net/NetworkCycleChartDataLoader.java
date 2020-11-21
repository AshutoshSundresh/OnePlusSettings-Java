package com.android.settingslib.net;

import android.app.usage.NetworkStats;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import com.android.settingslib.net.NetworkCycleChartData;
import com.android.settingslib.net.NetworkCycleData;
import com.android.settingslib.net.NetworkCycleDataLoader;
import com.android.settingslib.utils.ProductUtils;
import java.util.ArrayList;
import java.util.List;

public class NetworkCycleChartDataLoader extends NetworkCycleDataLoader<List<NetworkCycleChartData>> {
    private final List<NetworkCycleChartData> mData;

    private NetworkCycleChartDataLoader(Builder builder) {
        super(builder);
        this.mData = new ArrayList();
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settingslib.net.NetworkCycleDataLoader
    public void recordUsage(long j, long j2) {
        long j3;
        try {
            if (!this.mIsHotspot || !ProductUtils.isUsvMode()) {
                NetworkStats.Bucket querySummaryForDevice = this.mNetworkStatsManager.querySummaryForDevice(this.mNetworkTemplate, j, j2);
                if (querySummaryForDevice == null) {
                    j3 = 0;
                } else {
                    j3 = querySummaryForDevice.getRxBytes() + querySummaryForDevice.getTxBytes();
                }
            } else {
                NetworkStats queryDetailsForUid = this.mNetworkStatsManager.queryDetailsForUid(0, this.telephonyManager.getSubscriberId(), j, j2, -5);
                j3 = getTotalTetheringBytes(queryDetailsForUid);
                queryDetailsForUid.close();
            }
            if (j3 > 0) {
                NetworkCycleChartData.Builder builder = new NetworkCycleChartData.Builder();
                builder.setUsageBuckets(getUsageBuckets(j, j2));
                builder.setStartTime(j);
                builder.setEndTime(j2);
                builder.setTotalUsage(j3);
                this.mData.add(builder.build());
            }
        } catch (RemoteException e) {
            Log.e("NetworkCycleChartLoader", "Exception querying network detail.", e);
        }
    }

    private long getTotalTetheringBytes(NetworkStats networkStats) {
        long j = 0;
        if (networkStats == null) {
            return 0;
        }
        NetworkStats.Bucket bucket = new NetworkStats.Bucket();
        while (networkStats.hasNextBucket() && networkStats.getNextBucket(bucket)) {
            j += bucket.getRxBytes() + bucket.getTxBytes();
        }
        return j;
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settingslib.net.NetworkCycleDataLoader
    public List<NetworkCycleChartData> getCycleUsage() {
        return this.mData;
    }

    public static Builder<?> builder(Context context) {
        return new Builder<NetworkCycleChartDataLoader>(context) {
            /* class com.android.settingslib.net.NetworkCycleChartDataLoader.AnonymousClass1 */

            @Override // com.android.settingslib.net.NetworkCycleDataLoader.Builder
            public NetworkCycleChartDataLoader build() {
                return new NetworkCycleChartDataLoader(this);
            }
        };
    }

    private List<NetworkCycleData> getUsageBuckets(long j, long j2) {
        NetworkStats.Bucket bucket;
        ArrayList arrayList = new ArrayList();
        long j3 = j;
        for (long j4 = j + NetworkCycleChartData.BUCKET_DURATION_MS; j4 <= j2; j4 = NetworkCycleChartData.BUCKET_DURATION_MS + j4) {
            long j5 = 0;
            try {
                if (!this.mIsHotspot || !ProductUtils.isUsvMode()) {
                    bucket = this.mNetworkStatsManager.querySummaryForDevice(this.mNetworkTemplate, j3, j4);
                } else {
                    bucket = new NetworkStats.Bucket();
                    NetworkStats queryDetailsForUid = this.mNetworkStatsManager.queryDetailsForUid(0, this.telephonyManager.getSubscriberId(), j3, j4, -5);
                    queryDetailsForUid.getNextBucket(bucket);
                    queryDetailsForUid.close();
                }
                if (bucket != null) {
                    j5 = bucket.getRxBytes() + bucket.getTxBytes();
                }
            } catch (RemoteException e) {
                Log.e("NetworkCycleChartLoader", "Exception querying network detail.", e);
            }
            NetworkCycleData.Builder builder = new NetworkCycleData.Builder();
            builder.setStartTime(j3);
            builder.setEndTime(j4);
            builder.setTotalUsage(j5);
            arrayList.add(builder.build());
            j3 = j4;
        }
        return arrayList;
    }

    public static abstract class Builder<T extends NetworkCycleChartDataLoader> extends NetworkCycleDataLoader.Builder<T> {
        public Builder(Context context) {
            super(context);
        }
    }
}
