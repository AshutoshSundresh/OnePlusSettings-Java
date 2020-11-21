package com.android.settingslib.net;

import android.content.Context;
import android.util.Log;
import com.android.settingslib.net.NetworkCycleDataForUid;
import com.android.settingslib.net.NetworkCycleDataLoader;
import java.util.ArrayList;
import java.util.List;

public class NetworkCycleDataForUidLoader extends NetworkCycleDataLoader<List<NetworkCycleDataForUid>> {
    private final List<NetworkCycleDataForUid> mData;
    private final boolean mRetrieveDetail;
    private final List<Integer> mUids;

    private NetworkCycleDataForUidLoader(Builder builder) {
        super(builder);
        this.mUids = builder.mUids;
        this.mRetrieveDetail = builder.mRetrieveDetail;
        this.mData = new ArrayList();
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settingslib.net.NetworkCycleDataLoader
    public void recordUsage(long j, long j2) {
        try {
            long j3 = 0;
            long j4 = 0;
            for (Integer num : this.mUids) {
                int intValue = num.intValue();
                long totalUsage = getTotalUsage(this.mNetworkStatsManager.queryDetailsForUid(this.mNetworkTemplate, j, j2, intValue));
                if (totalUsage > 0) {
                    long j5 = j3 + totalUsage;
                    if (this.mRetrieveDetail) {
                        j4 += getForegroundUsage(j, j2, intValue);
                    }
                    j3 = j5;
                }
            }
            if (j3 > 0) {
                NetworkCycleDataForUid.Builder builder = new NetworkCycleDataForUid.Builder();
                builder.setStartTime(j);
                builder.setEndTime(j2);
                builder.setTotalUsage(j3);
                if (this.mRetrieveDetail) {
                    builder.setBackgroundUsage(j3 - j4);
                    builder.setForegroundUsage(j4);
                }
                NetworkCycleDataForUid build = builder.build();
                if (build.getBackgroudUsage() >= 0) {
                    this.mData.add(build);
                }
            }
        } catch (Exception e) {
            Log.e("NetworkDataForUidLoader", "Exception querying network detail.", e);
        }
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settingslib.net.NetworkCycleDataLoader
    public List<NetworkCycleDataForUid> getCycleUsage() {
        return this.mData;
    }

    public static Builder<?> builder(Context context) {
        return new Builder<NetworkCycleDataForUidLoader>(context) {
            /* class com.android.settingslib.net.NetworkCycleDataForUidLoader.AnonymousClass1 */

            @Override // com.android.settingslib.net.NetworkCycleDataLoader.Builder
            public NetworkCycleDataForUidLoader build() {
                return new NetworkCycleDataForUidLoader(this);
            }
        };
    }

    public List<Integer> getUids() {
        return this.mUids;
    }

    private long getForegroundUsage(long j, long j2, int i) {
        return getTotalUsage(this.mNetworkStatsManager.queryDetailsForUidTagState(this.mNetworkTemplate, j, j2, i, 0, 2));
    }

    public static abstract class Builder<T extends NetworkCycleDataForUidLoader> extends NetworkCycleDataLoader.Builder<T> {
        private boolean mRetrieveDetail = true;
        private final List<Integer> mUids = new ArrayList();

        public Builder(Context context) {
            super(context);
        }

        public Builder<T> addUid(int i) {
            this.mUids.add(Integer.valueOf(i));
            return this;
        }

        public Builder<T> setRetrieveDetail(boolean z) {
            this.mRetrieveDetail = z;
            return this;
        }
    }
}
