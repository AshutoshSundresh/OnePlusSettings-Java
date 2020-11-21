package com.oneplus.security.network.operator;

import java.util.Map;

public interface OperatorModelInterface {
    void addQueryResultListener(int i);

    void addTrafficUsageUpdater(OperatorPackageUsageUpdater operatorPackageUsageUpdater);

    void clearData();

    int getAccountDay(int i);

    long getPkgTotalInByte(int i);

    long getPkgUsedMonthlyInByte(int i);

    void registerOperatorAccountDayUpdater(OperatorAccountDayUpdater operatorAccountDayUpdater);

    void removeOperatorAccountDayUpdater(OperatorAccountDayUpdater operatorAccountDayUpdater);

    void removeQueryResultListener(int i);

    void removeTrafficUsageUpdater(OperatorPackageUsageUpdater operatorPackageUsageUpdater);

    Map<String, Object> requesetDataUsage(int i, boolean z);

    void requesetDataUsageAndNotify(int i);

    void requesetPkgMonthlyUsageAndTotalInByte(int i);

    void requestOperatorAccountDay(int i);

    void setOperatorAccountDay(int i, int i2);

    void setPackageMonthlyUsage(int i, long j);

    void setPackageTotalUsage(int i, long j);

    void setWarnByteValue(int i, long j);
}
