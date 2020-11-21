package com.android.settings.datausage.backgrounddata.bean;

public class BackgroundDataBean {
    private int id;
    private String package_name;
    private int type;
    private int uid;

    public BackgroundDataBean() {
    }

    public BackgroundDataBean(int i, String str, int i2, int i3) {
        this.id = i;
        this.package_name = str;
        this.uid = i2;
        this.type = i3;
    }

    public BackgroundDataBean(String str, int i, int i2) {
        this.package_name = str;
        this.uid = i;
        this.type = i2;
    }

    public int getId() {
        return this.id;
    }

    public String getPackage_name() {
        return this.package_name;
    }

    public int getUid() {
        return this.uid;
    }

    public int getType() {
        return this.type;
    }
}
