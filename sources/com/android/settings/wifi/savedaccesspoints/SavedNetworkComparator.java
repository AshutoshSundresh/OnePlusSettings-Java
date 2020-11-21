package com.android.settings.wifi.savedaccesspoints;

import android.icu.text.Collator;
import com.android.settingslib.wifi.AccessPoint;
import java.util.Comparator;

public final class SavedNetworkComparator {
    public static final Comparator<AccessPoint> INSTANCE = new Comparator<AccessPoint>() {
        /* class com.android.settings.wifi.savedaccesspoints.SavedNetworkComparator.AnonymousClass1 */
        final Collator mCollator = Collator.getInstance();

        private String nullToEmpty(String str) {
            return str == null ? "" : str;
        }

        public int compare(AccessPoint accessPoint, AccessPoint accessPoint2) {
            return this.mCollator.compare(nullToEmpty(accessPoint.getTitle()), nullToEmpty(accessPoint2.getTitle()));
        }
    };
}
