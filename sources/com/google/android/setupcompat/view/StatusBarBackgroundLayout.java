package com.google.android.setupcompat.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

public class StatusBarBackgroundLayout extends FrameLayout {
    private Object lastInsets;
    private Drawable statusBarBackground;

    public StatusBarBackgroundLayout(Context context) {
        super(context);
    }

    public StatusBarBackgroundLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @TargetApi(11)
    public StatusBarBackgroundLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (Build.VERSION.SDK_INT >= 21 && this.lastInsets == null) {
            requestApplyInsets();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Object obj;
        int systemWindowInsetTop;
        super.onDraw(canvas);
        if (Build.VERSION.SDK_INT >= 21 && (obj = this.lastInsets) != null && (systemWindowInsetTop = ((WindowInsets) obj).getSystemWindowInsetTop()) > 0) {
            this.statusBarBackground.setBounds(0, 0, getWidth(), systemWindowInsetTop);
            this.statusBarBackground.draw(canvas);
        }
    }

    public void setStatusBarBackground(Drawable drawable) {
        this.statusBarBackground = drawable;
        if (Build.VERSION.SDK_INT >= 21) {
            boolean z = true;
            setWillNotDraw(drawable == null);
            if (drawable == null) {
                z = false;
            }
            setFitsSystemWindows(z);
            invalidate();
        }
    }

    public Drawable getStatusBarBackground() {
        return this.statusBarBackground;
    }

    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        this.lastInsets = windowInsets;
        return super.onApplyWindowInsets(windowInsets);
    }
}
