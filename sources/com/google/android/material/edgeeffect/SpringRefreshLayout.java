package com.google.android.material.edgeeffect;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class SpringRefreshLayout extends SwipeRefreshLayout {
    private View mChildwithOverScrolling;

    public SpringRefreshLayout(Context context) {
        this(context, null);
    }

    public SpringRefreshLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mChildwithOverScrolling = null;
    }

    @Override // androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    public boolean onTouchEvent(MotionEvent motionEvent) {
        View view = this.mChildwithOverScrolling;
        if (view != null) {
            view.onTouchEvent(motionEvent);
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setOverScrollChild(View view) {
        this.mChildwithOverScrolling = view;
    }
}
