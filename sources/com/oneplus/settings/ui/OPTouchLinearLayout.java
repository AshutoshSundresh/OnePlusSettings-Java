package com.oneplus.settings.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class OPTouchLinearLayout extends OPSquarelLinearLayout {
    /* access modifiers changed from: protected */
    public void dispatchSetPressed(boolean z) {
    }

    public OPTouchLinearLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public OPTouchLinearLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i, 0);
    }

    public OPTouchLinearLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0, 0);
    }

    public OPTouchLinearLayout(Context context) {
        super(context, null, 0, 0);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        super.onInterceptTouchEvent(motionEvent);
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        super.dispatchTouchEvent(motionEvent);
        return true;
    }
}
