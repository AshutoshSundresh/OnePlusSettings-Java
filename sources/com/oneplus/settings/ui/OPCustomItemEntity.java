package com.oneplus.settings.ui;

public class OPCustomItemEntity {
    public int index;
    public String name;
    public int resId;
    public boolean selected = false;

    public OPCustomItemEntity(String str, int i, int i2) {
        this.name = str;
        this.resId = i;
        this.index = i2;
    }
}
