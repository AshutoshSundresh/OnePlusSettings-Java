package com.oneplus.security.network.operator;

import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;
import com.google.android.collect.Maps;
import com.oneplus.security.network.trafficalarm.TrafficUsageAlarmUtils;
import com.oneplus.security.network.trafficinfo.NativeTrafficDataModel;
import com.oneplus.security.network.trafficinfo.TrafficDataModelInterface;
import com.oneplus.security.utils.LogUtils;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NativeOperatorDataModel extends AbstractOperatorDataModel {
    private static NativeOperatorDataModel sOperatorData;
    private static int sReferenceCount;
    private SoftReference<Context> mContext;
    private ExecutorService mThreadPool = Executors.newCachedThreadPool();
    protected TrafficDataModelInterface mTrafficDataModel = NativeTrafficDataModel.getTrafficModelInstance();

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void addQueryResultListener(int i) {
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void removeQueryResultListener(int i) {
    }

    private NativeOperatorDataModel(Context context) {
        this.mContext = new SoftReference<>(context);
    }

    public static OperatorModelInterface getInstance(Context context) {
        if (sOperatorData == null) {
            synchronized (NativeOperatorDataModel.class) {
                if (sOperatorData == null) {
                    sOperatorData = new NativeOperatorDataModel(context);
                }
            }
        }
        synchronized (NativeOperatorDataModel.class) {
            sReferenceCount++;
            Log.d("clear_o", "a new reference incurred " + sReferenceCount);
        }
        return sOperatorData;
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void requesetPkgMonthlyUsageAndTotalInByte(final int i) {
        LogUtils.e("NativeOperatorDataModel", "requesetPkgMonthlyUsageAndTotalInByte " + i);
        synchronized (AbstractOperatorDataModel.mDoingQueryTaskLock) {
            checkThreadPoolExistence();
            if (!this.mThreadPool.isShutdown()) {
                if (i == 0 && this.isDoingQueryTaskSim1) {
                    return;
                }
                if (i != 1 || !this.isDoingQueryTaskSim2) {
                    if (i == 0) {
                        this.isDoingQueryTaskSim1 = true;
                    } else if (i == 1) {
                        this.isDoingQueryTaskSim2 = true;
                    }
                    this.mThreadPool.execute(new Runnable() {
                        /* class com.oneplus.security.network.operator.NativeOperatorDataModel.AnonymousClass1 */

                        public void run() {
                            Map<String, Object> requesetDataUsage = NativeOperatorDataModel.this.requesetDataUsage(i, false);
                            long longValue = ((Long) requesetDataUsage.get("total")).longValue();
                            long longValue2 = ((Long) requesetDataUsage.get("used")).longValue();
                            NativeOperatorDataModel.this.notifyMonthlyUsageAndTotalChanged(i, longValue, longValue2);
                            Map<String, Object> requesetDataUsage2 = NativeOperatorDataModel.this.requesetDataUsage(i);
                            long longValue3 = ((Long) requesetDataUsage2.get("total")).longValue();
                            long longValue4 = ((Long) requesetDataUsage2.get("used")).longValue();
                            if (longValue4 == longValue2 && longValue3 == longValue) {
                                LogUtils.d("NativeOperatorDataModel", "forceUpdate local datausage is not changed,no need to refresh ui.");
                            } else {
                                NativeOperatorDataModel.this.notifyMonthlyUsageAndTotalChanged(i, longValue3, longValue4);
                            }
                            int i = i;
                            if (i == 0) {
                                NativeOperatorDataModel.this.isDoingQueryTaskSim1 = false;
                            } else if (i == 1) {
                                NativeOperatorDataModel.this.isDoingQueryTaskSim2 = false;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void requesetDataUsageAndNotify(final int i) {
        LogUtils.e("NativeOperatorDataModel", "requesetDataUsageAndNotify " + i);
        synchronized (AbstractOperatorDataModel.mDoingQueryTaskLock) {
            checkThreadPoolExistence();
            if (!this.mThreadPool.isShutdown()) {
                if (i == 0 && this.isDoingQueryTaskSim1) {
                    return;
                }
                if (i != 1 || !this.isDoingQueryTaskSim2) {
                    if (i == 0) {
                        this.isDoingQueryTaskSim1 = true;
                    } else if (i == 1) {
                        this.isDoingQueryTaskSim2 = true;
                    }
                    this.mThreadPool.execute(new Runnable() {
                        /* class com.oneplus.security.network.operator.NativeOperatorDataModel.AnonymousClass2 */

                        public void run() {
                            Map<String, Object> requesetDataUsage = NativeOperatorDataModel.this.requesetDataUsage(i, false);
                            NativeOperatorDataModel.this.notifyMonthlyUsageAndTotalChanged(i, ((Long) requesetDataUsage.get("total")).longValue(), ((Long) requesetDataUsage.get("used")).longValue());
                            int i = i;
                            if (i == 0) {
                                NativeOperatorDataModel.this.isDoingQueryTaskSim1 = false;
                            } else if (i == 1) {
                                NativeOperatorDataModel.this.isDoingQueryTaskSim2 = false;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void setOperatorAccountDay(int i, int i2) {
        NativeOperatorDataManager.saveAccountDay(this.mContext.get(), i, i2);
        startOperatorAccountDayQueryThread(i);
    }

    private void startOperatorAccountDayQueryThread(final int i) {
        ExecutorService executorService = this.mThreadPool;
        if (executorService != null) {
            executorService.execute(new Runnable() {
                /* class com.oneplus.security.network.operator.NativeOperatorDataModel.AnonymousClass3 */

                public void run() {
                    LogUtils.d("NativeOperatorDataModel", "querying slotId is " + i);
                    NativeOperatorDataModel.this.notifyAccountDayChanged(i, NativeOperatorDataManager.getAccountDay((Context) NativeOperatorDataModel.this.mContext.get(), i));
                }
            });
        }
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void requestOperatorAccountDay(int i) {
        notifyAccountDayChanged(i, getAccountDay(i));
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void setPackageTotalUsage(int i, long j) {
        NativeOperatorDataManager.savePkgTotalInByte(this.mContext.get(), i, j * 1024);
        if (NativeOperatorDataManager.getPkgUsedMonthlyInByte(this.mContext.get(), i) != -1) {
            updateDataUsageSystemAlert(i);
        }
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void setWarnByteValue(int i, long j) {
        TrafficUsageAlarmUtils.setDataWarnValue(this.mContext.get(), j, i);
        if (NativeOperatorDataManager.getPkgUsedMonthlyInByte(this.mContext.get(), i) != -1) {
            updateDataUsageSystemAlert(i);
        }
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void setPackageMonthlyUsage(int i, long j) {
        NativeOperatorDataManager.savePkgUsedMonthlyInByte(this.mContext.get(), i, j * 1024);
        updateDataUsageSystemAlert(i);
    }

    private void updateDataUsageSystemAlert(int i) {
        long pkgUsedMonthlyInByte = NativeOperatorDataManager.getPkgUsedMonthlyInByte(this.mContext.get(), i);
        long nativePkgUsedInByte = getNativePkgUsedInByte(i);
        long pkgTotalInByte = getPkgTotalInByte(i) - pkgUsedMonthlyInByte;
        Log.d("NativeOperatorDataModel", "NODM updateDataUsageSystemAlert: systemLimitValue " + pkgTotalInByte);
        long j = 0;
        TrafficUsageAlarmUtils.setSystemDataLimitValue(this.mContext.get(), pkgTotalInByte > 0 ? pkgTotalInByte + nativePkgUsedInByte : 0, i);
        long dataWarnValue = TrafficUsageAlarmUtils.getDataWarnValue(this.mContext.get(), i, -1) - pkgUsedMonthlyInByte;
        if (dataWarnValue > 0) {
            j = dataWarnValue + nativePkgUsedInByte;
        }
        TrafficUsageAlarmUtils.setSystemDataWarnValue(this.mContext.get(), j, i);
    }

    @Override // com.oneplus.security.network.operator.AbstractOperatorDataModel, com.oneplus.security.network.operator.OperatorModelInterface
    public void clearData() {
        synchronized (NativeOperatorDataModel.class) {
            Log.d("clear_o", "before clear opera data " + sReferenceCount);
            int i = sReferenceCount + -1;
            sReferenceCount = i;
            if (i > 0) {
                Log.d("clear_o", "clear opera data partially " + sReferenceCount);
                return;
            }
            if (sOperatorData != null) {
                super.clearData();
                this.mTrafficDataModel.clearTrafficData();
                if (this.mThreadPool != null) {
                    this.mThreadPool = null;
                }
                sOperatorData = null;
                sReferenceCount = 0;
            }
            if (sReferenceCount < 0) {
                sReferenceCount = 0;
            }
        }
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public long getPkgTotalInByte(int i) {
        return NativeOperatorDataManager.getPkgTotalInByte(this.mContext.get(), i);
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public int getAccountDay(int i) {
        return NativeOperatorDataManager.getAccountDay(this.mContext.get(), i);
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public long getPkgUsedMonthlyInByte(int i) {
        return NativeOperatorDataManager.getPkgUsedMonthlyInByte(this.mContext.get(), i);
    }

    public long getNativePkgUsedInByte(int i) {
        long[] dataUsageSectionTimeMillByAccountDay = AccountDayLocalCache.getDataUsageSectionTimeMillByAccountDay(this.mContext.get(), i);
        return this.mTrafficDataModel.getNativeDataUsageWithinSpecificTime(i, dataUsageSectionTimeMillByAccountDay[0], dataUsageSectionTimeMillByAccountDay[1]);
    }

    private void checkThreadPoolExistence() {
        if (this.mThreadPool == null) {
            this.mThreadPool = Executors.newFixedThreadPool(1);
        }
    }

    public Map<String, Object> requesetDataUsage(int i) {
        return requesetDataUsage(i, true);
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public Map<String, Object> requesetDataUsage(int i, boolean z) {
        long pkgUsedMonthlyInByte = NativeOperatorDataManager.getPkgUsedMonthlyInByte(this.mContext.get(), i);
        long pkgTotalInByte = getPkgTotalInByte(i);
        TrafficDataModelInterface trafficDataModelInterface = this.mTrafficDataModel;
        long extraDataUsage = trafficDataModelInterface != null ? trafficDataModelInterface.getExtraDataUsage(i, pkgUsedMonthlyInByte, z) : 0;
        LogUtils.i("NativeOperatorDataModel", "requesetDataUsage total:" + pkgTotalInByte + ", used:" + pkgUsedMonthlyInByte + ", extra:" + extraDataUsage + ", slot:" + i + ", block:" + z);
        ArrayMap newArrayMap = Maps.newArrayMap();
        newArrayMap.put("total", Long.valueOf(pkgTotalInByte));
        if (pkgUsedMonthlyInByte != -1) {
            extraDataUsage += pkgUsedMonthlyInByte;
        }
        newArrayMap.put("used", Long.valueOf(extraDataUsage));
        newArrayMap.put("slotid", Integer.valueOf(i));
        return newArrayMap;
    }
}
