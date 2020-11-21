package com.android.settingslib.applications;

import android.content.res.Configuration;
import android.content.res.Resources;

public class InterestingConfigChanges {
    private final int mFlags;
    private final Configuration mLastConfiguration;
    private int mLastDensity;

    public InterestingConfigChanges() {
        this(-2147482876);
    }

    public InterestingConfigChanges(int i) {
        this.mLastConfiguration = new Configuration();
        this.mFlags = i;
    }

    public boolean applyNewConfig(Resources resources) {
        Configuration configuration = this.mLastConfiguration;
        int updateFrom = configuration.updateFrom(Configuration.generateDelta(configuration, resources.getConfiguration()));
        if (!(this.mLastDensity != resources.getDisplayMetrics().densityDpi) && (updateFrom & this.mFlags) == 0) {
            return false;
        }
        this.mLastDensity = resources.getDisplayMetrics().densityDpi;
        return true;
    }
}
