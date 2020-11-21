package com.oneplus.security.firewall;

import android.graphics.drawable.Drawable;

class AppPkgItem {
    private Drawable appIcon;
    private String appName;
    private String appSortKey;
    private boolean isSystemApp;
    private String pkgName;

    AppPkgItem() {
    }

    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String str) {
        this.appName = str;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setPkgName(String str) {
        this.pkgName = str;
    }

    public Drawable getAppIcon() {
        return this.appIcon;
    }

    public void setAppIcon(Drawable drawable) {
        this.appIcon = drawable;
    }

    public String getAppSortKey() {
        return this.appSortKey;
    }

    public void setAppSortKey(String str) {
        this.appSortKey = str;
    }

    public boolean isSystemApp() {
        return this.isSystemApp;
    }

    public void setSystemApp(boolean z) {
        this.isSystemApp = z;
    }

    public String toString() {
        return "AppPkgItem [appName=" + this.appName + ", pkgName=" + this.pkgName + ", appIcon=" + this.appIcon + ", appSortKey=" + this.appSortKey + ", isSystemApp=" + this.isSystemApp + "]";
    }
}
