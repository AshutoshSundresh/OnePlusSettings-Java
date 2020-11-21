package com.android.settings.fuelgauge.batterytip.actions;

import android.content.Context;
import com.android.settingslib.fuelgauge.BatterySaverUtils;

public class BatterySaverAction extends BatteryTipAction {
    public BatterySaverAction(Context context) {
        super(context);
    }

    @Override // com.android.settings.fuelgauge.batterytip.actions.BatteryTipAction
    public void handlePositiveAction(int i) {
        BatterySaverUtils.setPowerSaveMode(this.mContext, true, true);
        this.mMetricsFeatureProvider.action(this.mContext, 1365, i);
    }
}
