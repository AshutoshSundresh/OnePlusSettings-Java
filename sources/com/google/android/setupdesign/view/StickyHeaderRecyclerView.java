package com.google.android.setupdesign.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;

public class StickyHeaderRecyclerView extends HeaderRecyclerView {
    private int statusBarInset = 0;
    private View sticky;
    private final RectF stickyRect = new RectF();

    public StickyHeaderRecyclerView(Context context) {
        super(context);
    }

    public StickyHeaderRecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StickyHeaderRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        View header;
        super.onLayout(z, i, i2, i3, i4);
        if (this.sticky == null) {
            updateStickyView();
        }
        if (this.sticky != null && (header = getHeader()) != null && header.getHeight() == 0) {
            header.layout(0, -header.getMeasuredHeight(), header.getMeasuredWidth(), 0);
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.recyclerview.widget.RecyclerView
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.sticky != null) {
            measureChild(getHeader(), i, i2);
        }
    }

    public void updateStickyView() {
        View header = getHeader();
        if (header != null) {
            this.sticky = header.findViewWithTag("sticky");
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void draw(Canvas canvas) {
        View view;
        super.draw(canvas);
        if (this.sticky != null) {
            View header = getHeader();
            int save = canvas.save();
            if (header != null) {
                view = header;
            } else {
                view = this.sticky;
            }
            int top = header != null ? this.sticky.getTop() : 0;
            if (view.getTop() + top < this.statusBarInset || !view.isShown()) {
                this.stickyRect.set(0.0f, (float) ((-top) + this.statusBarInset), (float) view.getWidth(), (float) ((view.getHeight() - top) + this.statusBarInset));
                canvas.translate(0.0f, this.stickyRect.top);
                canvas.clipRect(0, 0, view.getWidth(), view.getHeight());
                view.draw(canvas);
            } else {
                this.stickyRect.setEmpty();
            }
            canvas.restoreToCount(save);
        }
    }

    @TargetApi(21)
    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        if (getFitsSystemWindows()) {
            this.statusBarInset = windowInsets.getSystemWindowInsetTop();
            windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), 0, windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
        }
        return windowInsets;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!this.stickyRect.contains(motionEvent.getX(), motionEvent.getY())) {
            return super.dispatchTouchEvent(motionEvent);
        }
        RectF rectF = this.stickyRect;
        motionEvent.offsetLocation(-rectF.left, -rectF.top);
        return getHeader().dispatchTouchEvent(motionEvent);
    }
}
