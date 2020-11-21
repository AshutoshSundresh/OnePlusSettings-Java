package com.oneplus.security.widget;

public class WidgetData {
    private String title;
    private int type;
    private String units;
    private String value;

    public WidgetData() {
    }

    public WidgetData(int i, String str, String str2) {
        this.type = i;
        this.value = str;
        this.units = str2;
    }

    public WidgetData(int i, String str, String str2, String str3) {
        this.type = i;
        this.value = str;
        this.units = str2;
        this.title = str3;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String str) {
        this.value = str;
    }

    public String getUnits() {
        return this.units;
    }

    public void setUnits(String str) {
        this.units = str;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    public String toString() {
        return "WidgetData [type=" + this.type + ", value=" + this.value + ", units=" + this.units + "]";
    }
}
