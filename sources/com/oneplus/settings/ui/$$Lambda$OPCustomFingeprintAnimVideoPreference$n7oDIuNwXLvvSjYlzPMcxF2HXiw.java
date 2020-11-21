package com.oneplus.settings.ui;

import android.view.MotionEvent;
import android.view.View;

/* renamed from: com.oneplus.settings.ui.-$$Lambda$OPCustomFingeprintAnimVideoPreference$n7oDIuNwXLvvSjYlzPMcxF2HXiw  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$OPCustomFingeprintAnimVideoPreference$n7oDIuNwXLvvSjYlzPMcxF2HXiw implements View.OnTouchListener {
    public static final /* synthetic */ $$Lambda$OPCustomFingeprintAnimVideoPreference$n7oDIuNwXLvvSjYlzPMcxF2HXiw INSTANCE = new $$Lambda$OPCustomFingeprintAnimVideoPreference$n7oDIuNwXLvvSjYlzPMcxF2HXiw();

    private /* synthetic */ $$Lambda$OPCustomFingeprintAnimVideoPreference$n7oDIuNwXLvvSjYlzPMcxF2HXiw() {
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return view.getParent().requestDisallowInterceptTouchEvent(true);
    }
}
