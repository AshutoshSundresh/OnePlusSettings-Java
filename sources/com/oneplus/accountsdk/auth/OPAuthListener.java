package com.oneplus.accountsdk.auth;

public interface OPAuthListener<T> {
    void onReqComplete();

    void onReqFinish(T t);
}
