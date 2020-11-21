package com.android.settings.vpn2;

import com.android.internal.util.Preconditions;
import java.util.Objects;

/* access modifiers changed from: package-private */
public class AppVpnInfo implements Comparable {
    public final String packageName;
    public final int userId;

    public AppVpnInfo(int i, String str) {
        this.userId = i;
        this.packageName = (String) Preconditions.checkNotNull(str);
    }

    @Override // java.lang.Comparable
    public int compareTo(Object obj) {
        AppVpnInfo appVpnInfo = (AppVpnInfo) obj;
        int compareTo = this.packageName.compareTo(appVpnInfo.packageName);
        return compareTo == 0 ? appVpnInfo.userId - this.userId : compareTo;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AppVpnInfo)) {
            return false;
        }
        AppVpnInfo appVpnInfo = (AppVpnInfo) obj;
        if (this.userId != appVpnInfo.userId || !Objects.equals(this.packageName, appVpnInfo.packageName)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Objects.hash(this.packageName, Integer.valueOf(this.userId));
    }
}
