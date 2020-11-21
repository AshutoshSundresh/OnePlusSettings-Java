package com.android.settingslib.net;

import com.android.settingslib.net.NetworkCycleData;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NetworkCycleChartData extends NetworkCycleData {
    public static final long BUCKET_DURATION_MS = TimeUnit.DAYS.toMillis(1);
    private List<NetworkCycleData> mUsageBuckets;

    private NetworkCycleChartData() {
    }

    public List<NetworkCycleData> getUsageBuckets() {
        return this.mUsageBuckets;
    }

    public static class Builder extends NetworkCycleData.Builder {
        private NetworkCycleChartData mObject = new NetworkCycleChartData();

        public Builder setUsageBuckets(List<NetworkCycleData> list) {
            getObject().mUsageBuckets = list;
            return this;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settingslib.net.NetworkCycleData.Builder
        public NetworkCycleChartData getObject() {
            return this.mObject;
        }

        @Override // com.android.settingslib.net.NetworkCycleData.Builder
        public NetworkCycleChartData build() {
            return getObject();
        }
    }
}
