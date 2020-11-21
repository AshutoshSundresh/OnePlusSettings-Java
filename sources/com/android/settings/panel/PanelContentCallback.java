package com.android.settings.panel;

public interface PanelContentCallback {
    void forceClose();

    void onCustomizedButtonStateChanged();

    void onHeaderChanged();
}
