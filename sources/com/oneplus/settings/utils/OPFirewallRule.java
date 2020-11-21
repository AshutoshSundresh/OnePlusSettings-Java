package com.oneplus.settings.utils;

public class OPFirewallRule {
    private Integer mobile;
    private String pkg;
    private Integer wlan;

    public OPFirewallRule(String str, Integer num, Integer num2) {
        this.pkg = str;
        this.wlan = num;
        this.mobile = num2;
    }

    public OPFirewallRule(Integer num, String str, Integer num2, Integer num3) {
        this.pkg = str;
        this.wlan = num2;
        this.mobile = num3;
    }

    public String getPkg() {
        return this.pkg;
    }

    public Integer getWlan() {
        return this.wlan;
    }

    public Integer getMobile() {
        return this.mobile;
    }
}
