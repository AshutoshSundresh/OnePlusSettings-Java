package com.oneplus.security.network.simcard;

public interface SimcardDataModelInterface {
    int getCurrentTrafficRunningSlotId();

    String getSlotOperatorName(int i);

    boolean isSlotOperatorSupportedBySdk(int i);

    boolean isSlotSimInserted(int i);

    boolean isSlotSimReady(int i);

    void registerSimStateListener(SimStateListener simStateListener);

    void removeSimStateListener(SimStateListener simStateListener);

    void setDataEnabled(boolean z);
}
