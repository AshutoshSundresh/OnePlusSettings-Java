package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.SparseIntArray;
import com.android.internal.os.BatterySipper;
import com.android.internal.util.ArrayUtils;
import com.android.settingslib.fuelgauge.Estimate;

public class PowerUsageFeatureProviderImpl implements PowerUsageFeatureProvider {
    private static final String[] PACKAGES_SYSTEM = {"com.android.providers.media", "com.android.providers.calendar", "com.android.systemui"};
    protected Context mContext;
    protected PackageManager mPackageManager;

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public String getAdvancedUsageScreenInfoString() {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean getEarlyWarningSignal(Context context, String str) {
        return false;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public Estimate getEnhancedBatteryPrediction(Context context) {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public SparseIntArray getEnhancedBatteryPredictionCurve(Context context, long j) {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public String getEnhancedEstimateDebugString(String str) {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public String getOldEstimateDebugString(String str) {
        return null;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isEnhancedBatteryPredictionEnabled(Context context) {
        return false;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isEstimateDebugEnabled() {
        return false;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isTypeService(BatterySipper batterySipper) {
        return false;
    }

    public PowerUsageFeatureProviderImpl(Context context) {
        this.mPackageManager = context.getPackageManager();
        this.mContext = context.getApplicationContext();
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isTypeSystem(BatterySipper batterySipper) {
        int uid = batterySipper.uidObj == null ? -1 : batterySipper.getUid();
        batterySipper.mPackages = this.mPackageManager.getPackagesForUid(uid);
        if (uid >= 0 && uid < 10000) {
            return true;
        }
        String[] strArr = batterySipper.mPackages;
        if (strArr != null) {
            for (String str : strArr) {
                if (ArrayUtils.contains(PACKAGES_SYSTEM, str)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageFeatureProvider
    public boolean isSmartBatterySupported() {
        return this.mContext.getResources().getBoolean(17891537);
    }
}
