package com.oneplus.settings.defaultapp.apptype;

import android.content.Intent;
import android.content.IntentFilter;
import java.util.List;

public abstract class DefaultAppTypeInfo {
    public abstract List<IntentFilter> getAppFilter();

    public abstract List<Intent> getAppIntent();

    public abstract List<Integer> getAppMatchParam();
}
