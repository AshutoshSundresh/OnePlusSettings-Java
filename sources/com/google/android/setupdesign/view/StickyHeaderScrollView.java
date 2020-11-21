package com.google.android.setupdesign.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;

public class StickyHeaderScrollView extends BottomScrollView {
    private int statusBarInset = 0;
    private View sticky;
    private View stickyContainer;

    public StickyHeaderScrollView(Context context) {
        super(context);
    }

    public StickyHeaderScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public StickyHeaderScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.view.BottomScrollView
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.sticky == null) {
            updateStickyView();
        }
        updateStickyHeaderPosition();
    }

    public void updateStickyView() {
        this.sticky = findViewWithTag("sticky");
        this.stickyContainer = findViewWithTag("stickyContainer");
    }

    private void updateStickyHeaderPosition() {
        View view;
        if (Build.VERSION.SDK_INT >= 11 && (view = this.sticky) != null) {
            View view2 = this.stickyContainer;
            if (view2 != null) {
                view = view2;
            }
            int top = this.stickyContainer != null ? this.sticky.getTop() : 0;
            if ((view.getTop() - getScrollY()) + top < this.statusBarInset || !view.isShown()) {
                view.setTranslationY((float) (getScrollY() - top));
            } else {
                view.setTranslationY(0.0f);
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.view.BottomScrollView
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        updateStickyHeaderPosition();
    }

    @TargetApi(21)
    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        if (!getFitsSystemWindows()) {
            return windowInsets;
        }
        this.statusBarInset = windowInsets.getSystemWindowInsetTop();
        return windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), 0, windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
    }
}
