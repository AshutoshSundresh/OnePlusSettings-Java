package com.oneplus.security.network.trafficinfo;

public interface TrafficDataModelInterface {
    void clearTrafficData();

    long getExtraDataUsage(int i, long j, boolean z);

    long getNativeDataUsageWithinSpecificTime(int i, long j, long j2);
}
