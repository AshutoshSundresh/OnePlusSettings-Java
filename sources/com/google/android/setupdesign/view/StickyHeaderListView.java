package com.google.android.setupdesign.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ListView;
import com.google.android.setupdesign.R$styleable;

public class StickyHeaderListView extends ListView {
    private int statusBarInset = 0;
    private View sticky;
    private View stickyContainer;
    private final RectF stickyRect = new RectF();

    public StickyHeaderListView(Context context) {
        super(context);
        init(null, 16842868);
    }

    public StickyHeaderListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet, 16842868);
    }

    public StickyHeaderListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet, i);
    }

    private void init(AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.SudStickyHeaderListView, i, 0);
        int resourceId = obtainStyledAttributes.getResourceId(R$styleable.SudStickyHeaderListView_sudHeader, 0);
        if (resourceId != 0) {
            addHeaderView(LayoutInflater.from(getContext()).inflate(resourceId, (ViewGroup) this, false), null, false);
        }
        obtainStyledAttributes.recycle();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.sticky == null) {
            updateStickyView();
        }
    }

    public void updateStickyView() {
        this.sticky = findViewWithTag("sticky");
        this.stickyContainer = findViewWithTag("stickyContainer");
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!this.stickyRect.contains(motionEvent.getX(), motionEvent.getY())) {
            return super.dispatchTouchEvent(motionEvent);
        }
        RectF rectF = this.stickyRect;
        motionEvent.offsetLocation(-rectF.left, -rectF.top);
        return this.stickyContainer.dispatchTouchEvent(motionEvent);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.sticky != null) {
            int save = canvas.save();
            View view = this.stickyContainer;
            if (view == null) {
                view = this.sticky;
            }
            int top = this.stickyContainer != null ? this.sticky.getTop() : 0;
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

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        int i = this.sticky != null ? 1 : 0;
        accessibilityEvent.setItemCount(accessibilityEvent.getItemCount() - i);
        accessibilityEvent.setFromIndex(Math.max(accessibilityEvent.getFromIndex() - i, 0));
        if (Build.VERSION.SDK_INT >= 14) {
            accessibilityEvent.setToIndex(Math.max(accessibilityEvent.getToIndex() - i, 0));
        }
    }
}
