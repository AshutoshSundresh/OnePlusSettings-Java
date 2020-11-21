package com.oneplus.settings.defaultapp;

import android.content.pm.ActivityInfo;
import java.util.ArrayList;
import java.util.List;

public class DefaultAppActivityInfo {
    private final List<ActivityInfo> mActivityInfoList = new ArrayList();

    public void addActivityInfo(ActivityInfo activityInfo) {
        this.mActivityInfoList.add(activityInfo);
    }

    public List<ActivityInfo> getActivityInfo() {
        return this.mActivityInfoList;
    }
}
