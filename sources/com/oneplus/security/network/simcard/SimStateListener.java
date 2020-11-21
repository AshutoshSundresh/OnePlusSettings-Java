package com.oneplus.security.network.simcard;

public interface SimStateListener {
    void onSimOperatorCodeChanged(int i, String str);

    void onSimStateChanged(String str);
}
