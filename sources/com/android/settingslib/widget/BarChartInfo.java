package com.android.settingslib.widget;

import android.view.View;

public class BarChartInfo {
    public abstract BarViewInfo[] getBarViewInfos();

    public abstract int getDetails();

    public abstract View.OnClickListener getDetailsOnClickListener();

    public abstract int getEmptyText();

    public abstract int getTitle();
}
