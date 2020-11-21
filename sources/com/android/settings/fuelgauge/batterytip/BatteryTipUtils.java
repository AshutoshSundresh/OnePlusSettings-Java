package com.android.settings.fuelgauge.batterytip;

import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.StatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.internal.util.CollectionUtils;
import com.android.settings.SettingsActivity;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.fuelgauge.batterytip.AppInfo;
import com.android.settings.fuelgauge.batterytip.actions.BatterySaverAction;
import com.android.settings.fuelgauge.batterytip.actions.BatteryTipAction;
import com.android.settings.fuelgauge.batterytip.actions.OpenBatterySaverAction;
import com.android.settings.fuelgauge.batterytip.actions.OpenRestrictAppFragmentAction;
import com.android.settings.fuelgauge.batterytip.actions.RestrictAppAction;
import com.android.settings.fuelgauge.batterytip.actions.SmartBatteryAction;
import com.android.settings.fuelgauge.batterytip.actions.UnrestrictAppAction;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settings.fuelgauge.batterytip.tips.RestrictAppTip;
import com.android.settings.fuelgauge.batterytip.tips.UnrestrictAppTip;
import java.util.ArrayList;
import java.util.List;

public class BatteryTipUtils {
    public static List<AppInfo> getRestrictedAppsList(AppOpsManager appOpsManager, UserManager userManager) {
        List<UserHandle> userProfiles = userManager.getUserProfiles();
        List packagesForOps = appOpsManager.getPackagesForOps(new int[]{70});
        ArrayList arrayList = new ArrayList();
        int size = CollectionUtils.size(packagesForOps);
        for (int i = 0; i < size; i++) {
            AppOpsManager.PackageOps packageOps = (AppOpsManager.PackageOps) packagesForOps.get(i);
            List ops = packageOps.getOps();
            int size2 = ops.size();
            for (int i2 = 0; i2 < size2; i2++) {
                AppOpsManager.OpEntry opEntry = (AppOpsManager.OpEntry) ops.get(i2);
                if (opEntry.getOp() == 70 && opEntry.getMode() != 0 && userProfiles.contains(new UserHandle(UserHandle.getUserId(packageOps.getUid())))) {
                    AppInfo.Builder builder = new AppInfo.Builder();
                    builder.setPackageName(packageOps.getPackageName());
                    builder.setUid(packageOps.getUid());
                    arrayList.add(builder.build());
                }
            }
        }
        return arrayList;
    }

    public static BatteryTipAction getActionForBatteryTip(BatteryTip batteryTip, SettingsActivity settingsActivity, InstrumentedPreferenceFragment instrumentedPreferenceFragment) {
        int type = batteryTip.getType();
        if (type == 0) {
            return new SmartBatteryAction(settingsActivity, instrumentedPreferenceFragment);
        }
        if (type != 1) {
            if (type == 3 || type == 5) {
                if (batteryTip.getState() == 1) {
                    return new OpenBatterySaverAction(settingsActivity);
                }
                return new BatterySaverAction(settingsActivity);
            } else if (type != 7) {
                return null;
            } else {
                return new UnrestrictAppAction(settingsActivity, (UnrestrictAppTip) batteryTip);
            }
        } else if (batteryTip.getState() == 1) {
            return new OpenRestrictAppFragmentAction(instrumentedPreferenceFragment, (RestrictAppTip) batteryTip);
        } else {
            return new RestrictAppAction(settingsActivity, (RestrictAppTip) batteryTip);
        }
    }

    public static void uploadAnomalyPendingIntent(Context context, StatsManager statsManager) throws StatsManager.StatsUnavailableException {
        statsManager.setBroadcastSubscriber(PendingIntent.getBroadcast(context, 0, new Intent(context, AnomalyDetectionReceiver.class), 134217728), 1, 1);
    }
}
