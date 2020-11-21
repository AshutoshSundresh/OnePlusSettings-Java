package com.oneplus.security.network.operator;

import android.util.Log;
import com.oneplus.security.utils.LogUtils;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOperatorDataModel implements OperatorModelInterface {
    protected static Object mDoingQueryTaskLock = new byte[0];
    protected boolean isDoingQueryTaskSim1 = false;
    protected boolean isDoingQueryTaskSim2 = false;
    protected final List<OperatorAccountDayUpdater> mOperatorAccountDayUpdaterList = new ArrayList();
    protected final List<OperatorPackageUsageUpdater> mOperatorQueryResultUpdaterList = new ArrayList();

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void addTrafficUsageUpdater(OperatorPackageUsageUpdater operatorPackageUsageUpdater) {
        synchronized (this.mOperatorQueryResultUpdaterList) {
            if (!this.mOperatorQueryResultUpdaterList.contains(operatorPackageUsageUpdater)) {
                this.mOperatorQueryResultUpdaterList.add(operatorPackageUsageUpdater);
            }
        }
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void removeTrafficUsageUpdater(OperatorPackageUsageUpdater operatorPackageUsageUpdater) {
        synchronized (this.mOperatorQueryResultUpdaterList) {
            this.mOperatorQueryResultUpdaterList.remove(operatorPackageUsageUpdater);
        }
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void registerOperatorAccountDayUpdater(OperatorAccountDayUpdater operatorAccountDayUpdater) {
        synchronized (this.mOperatorAccountDayUpdaterList) {
            if (!this.mOperatorAccountDayUpdaterList.contains(operatorAccountDayUpdater)) {
                this.mOperatorAccountDayUpdaterList.add(operatorAccountDayUpdater);
            }
        }
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void removeOperatorAccountDayUpdater(OperatorAccountDayUpdater operatorAccountDayUpdater) {
        synchronized (this.mOperatorAccountDayUpdaterList) {
            this.mOperatorAccountDayUpdaterList.remove(operatorAccountDayUpdater);
        }
    }

    /* access modifiers changed from: protected */
    public void notifyAccountDayChanged(int i, int i2) {
        synchronized (this.mOperatorAccountDayUpdaterList) {
            for (OperatorAccountDayUpdater operatorAccountDayUpdater : this.mOperatorAccountDayUpdaterList) {
                Log.d("AbstractOperatorModel", "querying slotId is " + i + " day " + i2);
                operatorAccountDayUpdater.onAccountDayUpdate(i, i2);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void notifyMonthlyUsageAndTotalChanged(int i, long j, long j2) {
        synchronized (this.mOperatorQueryResultUpdaterList) {
            LogUtils.d("AbstractOperatorModel", "mOperatorQueryResultUpdaterList size:" + this.mOperatorQueryResultUpdaterList.size());
            for (OperatorPackageUsageUpdater operatorPackageUsageUpdater : this.mOperatorQueryResultUpdaterList) {
                operatorPackageUsageUpdater.onTrafficTotalAndUsedUpdate(j, j2, i);
            }
        }
    }

    @Override // com.oneplus.security.network.operator.OperatorModelInterface
    public void clearData() {
        synchronized (this.mOperatorQueryResultUpdaterList) {
            this.mOperatorQueryResultUpdaterList.clear();
        }
        synchronized (this.mOperatorAccountDayUpdaterList) {
            this.mOperatorAccountDayUpdaterList.clear();
        }
    }
}
