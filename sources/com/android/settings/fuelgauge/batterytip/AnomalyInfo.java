package com.android.settings.fuelgauge.batterytip;

import android.util.KeyValueListParser;
import android.util.Log;

public class AnomalyInfo {
    public final Integer anomalyType;
    public final boolean autoRestriction;

    public AnomalyInfo(String str) {
        Log.i("AnomalyInfo", "anomalyInfo: " + str);
        KeyValueListParser keyValueListParser = new KeyValueListParser(',');
        keyValueListParser.setString(str);
        this.anomalyType = Integer.valueOf(keyValueListParser.getInt("anomaly_type", -1));
        this.autoRestriction = keyValueListParser.getBoolean("auto_restriction", false);
    }
}
