package com.google.android.material.floatingactionbutton;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oneplus.common.OPViewGroupUtils;
import java.util.ArrayList;
import java.util.List;

public class FabScrollBehavior extends FloatingActionButton.Behavior {
    private ArrayList<FloatingActionButton> mFabList;
    private List<View> mViewList;
    private ArrayList<Boolean> visibleList = new ArrayList<>();

    public FabScrollBehavior(Context context, AttributeSet attributeSet) {
    }

    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton floatingActionButton, View view, View view2, int i) {
        return i == 2 || super.onStartNestedScroll(coordinatorLayout, floatingActionButton, view, view2, i);
    }

    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton floatingActionButton, View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
        Boolean bool = Boolean.TRUE;
        super.onNestedScroll(coordinatorLayout, (View) floatingActionButton, view, i, i2, i3, i4, i5, iArr);
        if (this.mViewList == null) {
            this.mViewList = OPViewGroupUtils.getAllChildViews(coordinatorLayout.getRootView());
        }
        if (this.mFabList == null) {
            this.mFabList = new ArrayList<>();
        }
        if (this.mFabList.isEmpty()) {
            for (int i6 = 0; i6 < this.mViewList.size(); i6++) {
                if (this.mViewList.get(i6) instanceof FloatingActionButton) {
                    this.mFabList.add((FloatingActionButton) this.mViewList.get(i6));
                    this.visibleList.add(bool);
                }
            }
        }
        if (!this.mFabList.isEmpty()) {
            for (int i7 = 0; i7 < this.mFabList.size(); i7++) {
                FloatingActionButton floatingActionButton2 = this.mFabList.get(i7);
                if (floatingActionButton2 != null && floatingActionButton2.getScrollHideBoolean()) {
                    if (i2 > 0 && this.visibleList.get(i7).booleanValue()) {
                        this.visibleList.set(i7, Boolean.FALSE);
                        onHide(floatingActionButton2);
                    } else if (i2 < 0) {
                        this.visibleList.set(i7, bool);
                        onShow(floatingActionButton2);
                    }
                }
            }
        }
    }

    public void onHide(FloatingActionButton floatingActionButton) {
        floatingActionButton.hideWithAnim();
    }

    public void onShow(FloatingActionButton floatingActionButton) {
        floatingActionButton.showWithAnim();
    }
}
