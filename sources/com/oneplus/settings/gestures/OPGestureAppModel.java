package com.oneplus.settings.gestures;

import android.graphics.drawable.Drawable;

public class OPGestureAppModel {
    private String ShortCutId;
    private Drawable appIcon;
    private String pkgName;
    private String title;

    public OPGestureAppModel(String str, String str2, String str3, int i) {
        this.pkgName = str;
        this.title = str2;
        this.ShortCutId = str3;
    }

    public Drawable getAppIcon() {
        return this.appIcon;
    }

    public void setAppIcon(Drawable drawable) {
        this.appIcon = drawable;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public String getTitle() {
        return this.title;
    }

    public String getShortCutId() {
        return this.ShortCutId;
    }
}
