package com.android.settingslib.net;

import com.android.settingslib.net.NetworkCycleData;

public class NetworkCycleDataForUid extends NetworkCycleData {
    private long mBackgroudUsage;
    private long mForegroudUsage;

    private NetworkCycleDataForUid() {
    }

    public long getBackgroudUsage() {
        return this.mBackgroudUsage;
    }

    public long getForegroudUsage() {
        return this.mForegroudUsage;
    }

    public static class Builder extends NetworkCycleData.Builder {
        private NetworkCycleDataForUid mObject = new NetworkCycleDataForUid();

        public Builder setBackgroundUsage(long j) {
            getObject().mBackgroudUsage = j;
            return this;
        }

        public Builder setForegroundUsage(long j) {
            getObject().mForegroudUsage = j;
            return this;
        }

        @Override // com.android.settingslib.net.NetworkCycleData.Builder
        public NetworkCycleDataForUid getObject() {
            return this.mObject;
        }

        @Override // com.android.settingslib.net.NetworkCycleData.Builder
        public NetworkCycleDataForUid build() {
            return getObject();
        }
    }
}
