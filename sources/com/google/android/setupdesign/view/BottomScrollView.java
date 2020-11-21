package com.google.android.setupdesign.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class BottomScrollView extends ScrollView {
    private final Runnable checkScrollRunnable = new Runnable() {
        /* class com.google.android.setupdesign.view.BottomScrollView.AnonymousClass1 */

        public void run() {
            BottomScrollView.this.checkScroll();
        }
    };
    private BottomScrollListener listener;
    private boolean requiringScroll = false;
    private int scrollThreshold;

    public interface BottomScrollListener {
        void onRequiresScroll();

        void onScrolledToBottom();
    }

    public BottomScrollView(Context context) {
        super(context);
    }

    public BottomScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public BottomScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setBottomScrollListener(BottomScrollListener bottomScrollListener) {
        this.listener = bottomScrollListener;
    }

    public BottomScrollListener getBottomScrollListener() {
        return this.listener;
    }

    public int getScrollThreshold() {
        return this.scrollThreshold;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        View childAt = getChildAt(0);
        if (childAt != null) {
            this.scrollThreshold = Math.max(0, ((childAt.getMeasuredHeight() - i4) + i2) - getPaddingBottom());
        }
        if (i4 - i2 > 0) {
            post(this.checkScrollRunnable);
        }
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (i4 != i2) {
            checkScroll();
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void checkScroll() {
        if (this.listener == null) {
            return;
        }
        if (getScrollY() >= this.scrollThreshold) {
            this.listener.onScrolledToBottom();
        } else if (!this.requiringScroll) {
            this.requiringScroll = true;
            this.listener.onRequiresScroll();
        }
    }
}
