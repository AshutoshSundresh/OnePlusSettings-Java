package com.oneplus.settings.ui;

import android.view.MotionEvent;
import android.view.View;

/* renamed from: com.oneplus.settings.ui.-$$Lambda$OPCustomShapePreference$pmKhtjUdx55CAR1Sm-4gVOgYr3Q  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$OPCustomShapePreference$pmKhtjUdx55CAR1Sm4gVOgYr3Q implements View.OnTouchListener {
    public static final /* synthetic */ $$Lambda$OPCustomShapePreference$pmKhtjUdx55CAR1Sm4gVOgYr3Q INSTANCE = new $$Lambda$OPCustomShapePreference$pmKhtjUdx55CAR1Sm4gVOgYr3Q();

    private /* synthetic */ $$Lambda$OPCustomShapePreference$pmKhtjUdx55CAR1Sm4gVOgYr3Q() {
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return view.getParent().requestDisallowInterceptTouchEvent(true);
    }
}
