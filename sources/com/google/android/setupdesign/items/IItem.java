package com.google.android.setupdesign.items;

import android.view.View;

public interface IItem {
    int getLayoutResource();

    boolean isEnabled();

    void onBindView(View view);
}
