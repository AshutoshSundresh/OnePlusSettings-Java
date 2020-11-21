package com.android.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchBlockingFrameLayout extends FrameLayout {
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public TouchBlockingFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
