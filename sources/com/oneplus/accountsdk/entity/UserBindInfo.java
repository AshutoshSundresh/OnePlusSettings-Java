package com.oneplus.accountsdk.entity;

public class UserBindInfo {
    public boolean bindSuccess;
    public String resultCode;
    public String resultMsg;

    public String toString() {
        return "UserBindInfo{resultCode='" + this.resultCode + "', resultMsg='" + this.resultMsg + "', bindSuccess=" + this.bindSuccess + '}';
    }
}
