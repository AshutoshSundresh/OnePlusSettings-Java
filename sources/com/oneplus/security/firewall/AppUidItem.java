package com.oneplus.security.firewall;

import com.google.android.collect.Lists;
import java.util.List;

public class AppUidItem {
    private int appUid;
    private List<AppPkgItem> apps;
    private boolean dataEnable;
    private boolean wlanEnable;

    public int getAppUid() {
        return this.appUid;
    }

    public void setAppUid(int i) {
        this.appUid = i;
    }

    public boolean isDataEnable() {
        return this.dataEnable;
    }

    public void setDataEnable(boolean z) {
        this.dataEnable = z;
    }

    public boolean isWlanEnable() {
        return this.wlanEnable;
    }

    public void setWlanEnable(boolean z) {
        this.wlanEnable = z;
    }

    public List<AppPkgItem> getApps() {
        if (this.apps == null) {
            this.apps = Lists.newArrayList();
        }
        return this.apps;
    }

    public String toString() {
        return "AppUidItem [appUid=" + this.appUid + ", dataEnable=" + this.dataEnable + ", wlanEnable=" + this.wlanEnable + ", apps=" + this.apps + "]";
    }
}
