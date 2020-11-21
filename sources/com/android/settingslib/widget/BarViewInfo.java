package com.android.settingslib.widget;

import android.graphics.drawable.Drawable;
import android.view.View;

public class BarViewInfo implements Comparable<BarViewInfo> {
    /* access modifiers changed from: package-private */
    public abstract View.OnClickListener getClickListener();

    public abstract CharSequence getContentDescription();

    /* access modifiers changed from: package-private */
    public abstract int getHeight();

    /* access modifiers changed from: package-private */
    public abstract Drawable getIcon();

    /* access modifiers changed from: package-private */
    public abstract int getNormalizedHeight();

    /* access modifiers changed from: package-private */
    public abstract CharSequence getSummary();

    /* access modifiers changed from: package-private */
    public abstract CharSequence getTitle();

    /* access modifiers changed from: package-private */
    public abstract void setNormalizedHeight(int i);
}
