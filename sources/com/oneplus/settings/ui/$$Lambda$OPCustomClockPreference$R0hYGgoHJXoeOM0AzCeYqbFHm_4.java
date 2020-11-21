package com.oneplus.settings.ui;

import android.view.MotionEvent;
import android.view.View;

/* renamed from: com.oneplus.settings.ui.-$$Lambda$OPCustomClockPreference$R0hYGgoHJXoeOM0AzCeYqbFHm_4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$OPCustomClockPreference$R0hYGgoHJXoeOM0AzCeYqbFHm_4 implements View.OnTouchListener {
    public static final /* synthetic */ $$Lambda$OPCustomClockPreference$R0hYGgoHJXoeOM0AzCeYqbFHm_4 INSTANCE = new $$Lambda$OPCustomClockPreference$R0hYGgoHJXoeOM0AzCeYqbFHm_4();

    private /* synthetic */ $$Lambda$OPCustomClockPreference$R0hYGgoHJXoeOM0AzCeYqbFHm_4() {
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return view.getParent().requestDisallowInterceptTouchEvent(true);
    }
}
