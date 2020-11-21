package com.oneplus.settings;

import android.content.Context;

public class OneplusColorManager {
    private Context mContext;

    public OneplusColorManager(Context context) {
        this.mContext = context;
        context.getPackageManager().hasSystemFeature("oem.read_mode.support");
    }
}
