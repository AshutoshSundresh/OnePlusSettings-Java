package com.oneplus.security.network.operator;

import android.util.ArrayMap;
import com.google.android.collect.Maps;
import java.util.Map;

public class InvalidOperatorDataModel extends AbstractOperatorDataModel {
    private static OperatorModelInterface sOperatorData;

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void addQueryResultListener(int i) {
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public int getAccountDay(int i) {
        return -1;
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public long getPkgTotalInByte(int i) {
        return -1;
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public long getPkgUsedMonthlyInByte(int i) {
        return -1;
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void removeQueryResultListener(int i) {
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void setOperatorAccountDay(int i, int i2) {
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void setPackageMonthlyUsage(int i, long j) {
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void setPackageTotalUsage(int i, long j) {
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void setWarnByteValue(int i, long j) {
    }

    private InvalidOperatorDataModel() {
    }

    public static OperatorModelInterface getInstance() {
        if (sOperatorData == null) {
            synchronized (OperatorModelInterface.class) {
                if (sOperatorData == null) {
                    sOperatorData = new InvalidOperatorDataModel();
                }
            }
        }
        return sOperatorData;
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void requesetPkgMonthlyUsageAndTotalInByte(int i) {
        notifyMonthlyUsageAndTotalChanged(i, getPkgTotalInByte(i), getPkgUsedMonthlyInByte(i));
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void requesetDataUsageAndNotify(int i) {
        requesetPkgMonthlyUsageAndTotalInByte(i);
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void requestOperatorAccountDay(int i) {
        notifyAccountDayChanged(i, getAccountDay(i));
    }

    @Override // com.oneplus.security.network.operator.AbstractOperatorDataModel, com.oneplus.security.network.operator.OperatorModelInterface
    public void clearData() {
        super.clearData();
        if (sOperatorData != null) {
            sOperatorData = null;
        }
    }

    public Map<String, Object> requesetDataUsage(int i) {
        ArrayMap newArrayMap = Maps.newArrayMap();
        newArrayMap.put("total", -1);
        newArrayMap.put("used", -1);
        newArrayMap.put("slotid", Integer.valueOf(i));
        return newArrayMap;
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public Map<String, Object> requesetDataUsage(int i, boolean z) {
        return requesetDataUsage(i);
    }
}
