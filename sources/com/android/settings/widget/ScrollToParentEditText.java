package com.android.settings.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImeAwareEditText;

public class ScrollToParentEditText extends ImeAwareEditText {
    private Rect mRect = new Rect();

    public ScrollToParentEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public boolean requestRectangleOnScreen(Rect rect, boolean z) {
        ViewParent parent = getParent();
        if (!(parent instanceof View)) {
            return ScrollToParentEditText.super.requestRectangleOnScreen(rect, z);
        }
        View view = (View) parent;
        view.getDrawingRect(this.mRect);
        return view.requestRectangleOnScreen(this.mRect, z);
    }
}
