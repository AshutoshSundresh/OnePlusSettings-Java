package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;

public class FloatingAppBarScrollingViewBehavior extends AppBarLayout.ScrollingViewBehavior {
    private boolean initialized;

    /* access modifiers changed from: protected */
    @Override // com.google.android.material.appbar.HeaderScrollingViewBehavior
    public boolean shouldHeaderOverlapScrollingChild() {
        return true;
    }

    public FloatingAppBarScrollingViewBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior, com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior
    public boolean onDependentViewChanged(CoordinatorLayout coordinatorLayout, View view, View view2) {
        boolean onDependentViewChanged = super.onDependentViewChanged(coordinatorLayout, view, view2);
        if (!this.initialized && (view2 instanceof AppBarLayout)) {
            this.initialized = true;
            setAppBarLayoutTransparent((AppBarLayout) view2);
        }
        return onDependentViewChanged;
    }

    /* access modifiers changed from: package-private */
    public void setAppBarLayoutTransparent(AppBarLayout appBarLayout) {
        appBarLayout.setBackgroundColor(0);
        appBarLayout.setTargetElevation(0.0f);
    }
}
