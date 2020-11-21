package com.android.settings.fuelgauge.batterytip.actions;

import android.content.Context;
import com.android.internal.util.CollectionUtils;
import com.android.settings.fuelgauge.BatteryUtils;
import com.android.settings.fuelgauge.batterytip.AppInfo;
import com.android.settings.fuelgauge.batterytip.BatteryDatabaseManager;
import com.android.settings.fuelgauge.batterytip.tips.RestrictAppTip;
import java.util.Iterator;
import java.util.List;

public class RestrictAppAction extends BatteryTipAction {
    BatteryDatabaseManager mBatteryDatabaseManager;
    BatteryUtils mBatteryUtils;
    private RestrictAppTip mRestrictAppTip;

    public RestrictAppAction(Context context, RestrictAppTip restrictAppTip) {
        super(context);
        this.mRestrictAppTip = restrictAppTip;
        this.mBatteryUtils = BatteryUtils.getInstance(context);
        this.mBatteryDatabaseManager = BatteryDatabaseManager.getInstance(context);
    }

    @Override // com.android.settings.fuelgauge.batterytip.actions.BatteryTipAction
    public void handlePositiveAction(int i) {
        List<AppInfo> restrictAppList = this.mRestrictAppTip.getRestrictAppList();
        int size = restrictAppList.size();
        for (int i2 = 0; i2 < size; i2++) {
            AppInfo appInfo = restrictAppList.get(i2);
            String str = appInfo.packageName;
            this.mBatteryUtils.setForceAppStandby(appInfo.uid, str, 1);
            if (CollectionUtils.isEmpty(appInfo.anomalyTypes)) {
                this.mMetricsFeatureProvider.action(0, 1362, i, str, 0);
            } else {
                Iterator<Integer> it = appInfo.anomalyTypes.iterator();
                while (it.hasNext()) {
                    this.mMetricsFeatureProvider.action(0, 1362, i, str, it.next().intValue());
                }
            }
        }
        this.mBatteryDatabaseManager.updateAnomalies(restrictAppList, 1);
    }
}
