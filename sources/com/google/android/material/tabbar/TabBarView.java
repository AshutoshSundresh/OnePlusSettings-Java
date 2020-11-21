package com.google.android.material.tabbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import androidx.animation.AnimatorUtils;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.view.AbsSavedState;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.R$attr;
import com.google.android.material.R$color;
import com.google.android.material.R$dimen;
import com.google.android.material.R$style;
import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.appbar.CollapsingAppbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.ripple.RippleUtils;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.MaterialShapeUtils;
import com.oneplus.common.OPViewGroupUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TabBarView extends HorizontalScrollView {
    private static final int DEF_STYLE_RES = R$style.OnePlusTabBarViewStyle;
    private boolean isInitLine;
    private ColorStateList itemRippleColor;
    private FrameLayout.LayoutParams layoutParams;
    private PagerAdapter mAdapter;
    private boolean mPlayAnimation;
    private int mSelectMenuPosition;
    private SlidingTabIndicator mSlidingTabIndicator;
    private final ColorStateList mUnCheckTintColor;
    private ValueAnimator mValueAnimator;
    private MenuBuilder menu;
    private MenuInflater menuInflater;
    private MenuItem menuItem;
    final TabBarMenuView menuView;
    private TabBarOnPageChangeListener pageChangeListener;
    private final TabBarPresenter presenter;
    private OnTabItemReselectedListener reselectedListener;
    private ValueAnimator scrollAnimator;
    private OnTabItemSelectedListener selectedListener;
    private boolean setupViewPagerImplicitly;
    Drawable tabSelectedIndicator;
    private ViewPager viewPager;

    public interface OnTabItemReselectedListener {
        void onTabItemReselected(MenuItem menuItem);
    }

    public interface OnTabItemSelectedListener {
        boolean onTabItemSelected(MenuItem menuItem);
    }

    public int getMaxItemCount() {
        return 15;
    }

    public TabBarView(Context context) {
        this(context, null);
    }

    public TabBarView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.tabBarStyle);
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public TabBarView(android.content.Context r10, android.util.AttributeSet r11, int r12) {
        /*
        // Method dump skipped, instructions count: 442
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.tabbar.TabBarView.<init>(android.content.Context, android.util.AttributeSet, int):void");
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void doSelectItemAnim(MenuBuilder menuBuilder, MenuItem menuItem2) {
        int i = 0;
        for (int i2 = 0; i2 < menuBuilder.size(); i2++) {
            if (menuBuilder.getItem(i2) == menuItem2) {
                i = i2;
            }
        }
        selectTab(i);
        if (i != this.mSelectMenuPosition) {
            setupwithVPAnimate(i);
        }
        this.mSelectMenuPosition = i;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.isInitLine) {
            this.presenter.updateMenuView(true);
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int round = Math.round(ViewUtils.dpToPx(getContext(), 24));
        int mode = View.MeasureSpec.getMode(i2);
        boolean z = false;
        if (mode != Integer.MIN_VALUE) {
            if (mode == 0) {
                i2 = View.MeasureSpec.makeMeasureSpec(round + getPaddingTop() + getPaddingBottom(), 1073741824);
            }
        } else if (getChildCount() == 1 && View.MeasureSpec.getSize(i2) >= round) {
            getChildAt(0).setMinimumHeight(round);
        }
        View.MeasureSpec.getSize(i);
        if (View.MeasureSpec.getMode(i) != 0) {
            ViewUtils.dpToPx(getContext(), 40);
        }
        super.onMeasure(i, i2);
        if (getChildCount() == 1) {
            View childAt = getChildAt(0);
            if (childAt.getMeasuredWidth() < getMeasuredWidth()) {
                z = true;
            }
            if (z) {
                childAt.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), HorizontalScrollView.getChildMeasureSpec(i2, getPaddingTop() + getPaddingBottom(), childAt.getLayoutParams().height));
            }
        }
    }

    public void setSelectedTabIndicator(Drawable drawable) {
        if (this.tabSelectedIndicator != drawable) {
            this.tabSelectedIndicator = drawable;
            ViewCompat.postInvalidateOnAnimation(this.mSlidingTabIndicator);
        }
    }

    private MaterialShapeDrawable createMaterialShapeDrawableBackground(Context context) {
        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable();
        Drawable background = getBackground();
        if (background instanceof ColorDrawable) {
            materialShapeDrawable.setFillColor(ColorStateList.valueOf(((ColorDrawable) background).getColor()));
        }
        materialShapeDrawable.initializeElevationOverlay(context);
        return materialShapeDrawable;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        MaterialShapeUtils.setParentAbsoluteElevation(this);
        if (this.viewPager == null) {
            ViewParent parent = getParent();
            if (parent instanceof ViewPager) {
                setupWithViewPager((ViewPager) parent, true);
            }
        }
    }

    public void setElevation(float f) {
        super.setElevation(f);
        MaterialShapeUtils.setElevation(this, f);
    }

    public void setTabItemSelectedListener(OnTabItemSelectedListener onTabItemSelectedListener) {
        this.selectedListener = onTabItemSelectedListener;
    }

    public void setOnTabItemReselectedListener(OnTabItemReselectedListener onTabItemReselectedListener) {
        this.reselectedListener = onTabItemReselectedListener;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public void inflateMenu(int i) {
        this.presenter.setUpdateSuspended(true);
        getMenuInflater().inflate(i, this.menu);
        this.presenter.setUpdateSuspended(false);
        this.presenter.updateMenuView(true);
    }

    public ColorStateList getItemIconTintList() {
        return this.menuView.getIconTintList();
    }

    public void setItemIconTintList(ColorStateList colorStateList) {
        this.menuView.setIconTintList(colorStateList);
    }

    public void setItemIconSize(int i) {
        this.menuView.setItemIconSize(i);
    }

    public void setItemIconSizeRes(int i) {
        setItemIconSize(getResources().getDimensionPixelSize(i));
    }

    public int getItemIconSize() {
        return this.menuView.getItemIconSize();
    }

    @Deprecated
    public int getItemBackgroundResource() {
        return this.menuView.getItemBackgroundRes();
    }

    public void setItemBackgroundResource(int i) {
        this.menuView.setItemBackgroundRes(i);
        this.itemRippleColor = null;
    }

    public Drawable getItemBackground() {
        return this.menuView.getItemBackground();
    }

    public void setItemBackground(Drawable drawable) {
        this.menuView.setItemBackground(drawable);
        this.itemRippleColor = null;
    }

    public ColorStateList getItemRippleColor() {
        return this.itemRippleColor;
    }

    public void setItemRippleColor(ColorStateList colorStateList) {
        if (this.itemRippleColor != colorStateList) {
            this.itemRippleColor = colorStateList;
            if (colorStateList == null) {
                this.menuView.setItemBackground(null);
                return;
            }
            ColorStateList convertToRippleDrawableColor = RippleUtils.convertToRippleDrawableColor(colorStateList);
            if (Build.VERSION.SDK_INT >= 21) {
                this.menuView.setItemBackground(new RippleDrawable(convertToRippleDrawableColor, null, null));
                return;
            }
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setCornerRadius(1.0E-5f);
            Drawable wrap = DrawableCompat.wrap(gradientDrawable);
            DrawableCompat.setTintList(wrap, convertToRippleDrawableColor);
            this.menuView.setItemBackground(wrap);
        } else if (colorStateList == null && this.menuView.getItemBackground() != null) {
            this.menuView.setItemBackground(null);
        }
    }

    public int getSelectedItemId() {
        return this.menuView.getSelectedItemId();
    }

    public void setSelectedItemId(int i) {
        MenuItem findItem = this.menu.findItem(i);
        if (findItem != null && !this.menu.performItemAction(findItem, this.presenter, 0)) {
            findItem.setChecked(true);
        }
    }

    public void setLabelVisibilityMode(int i) {
        if (this.menuView.getLabelVisibilityMode() != i) {
            this.menuView.setLabelVisibilityMode(i);
            this.presenter.updateMenuView(false);
        }
    }

    public int getLabelVisibilityMode() {
        return this.menuView.getLabelVisibilityMode();
    }

    public void setItemHorizontalTranslationEnabled(boolean z) {
        if (this.menuView.isItemHorizontalTranslationEnabled() != z) {
            this.menuView.setItemHorizontalTranslationEnabled(z);
            this.presenter.updateMenuView(false);
        }
    }

    private void addCompatibilityTopDivider(Context context) {
        View view = new View(context);
        view.setBackgroundColor(ContextCompat.getColor(context, R$color.design_tab_bar_shadow_color));
        view.setLayoutParams(new FrameLayout.LayoutParams(-1, getResources().getDimensionPixelSize(R$dimen.design_tab_bar_shadow_height)));
        addView(view);
    }

    private MenuInflater getMenuInflater() {
        if (this.menuInflater == null) {
            this.menuInflater = new SupportMenuInflater(getContext());
        }
        return this.menuInflater;
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        Bundle bundle = new Bundle();
        savedState.menuPresenterState = bundle;
        this.menu.savePresenterStates(bundle);
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.setupViewPagerImplicitly) {
            setupWithViewPager(null);
            this.setupViewPagerImplicitly = false;
            TabBarOnPageChangeListener tabBarOnPageChangeListener = this.pageChangeListener;
            if (tabBarOnPageChangeListener != null) {
                tabBarOnPageChangeListener.onDestory();
                this.pageChangeListener = null;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.menu.restorePresenterStates(savedState.menuPresenterState);
    }

    /* access modifiers changed from: package-private */
    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
            /* class com.google.android.material.tabbar.TabBarView.SavedState.AnonymousClass1 */

            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        Bundle menuPresenterState;

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            readFromParcel(parcel, classLoader == null ? SavedState.class.getClassLoader() : classLoader);
        }

        @Override // androidx.customview.view.AbsSavedState
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeBundle(this.menuPresenterState);
        }

        private void readFromParcel(Parcel parcel, ClassLoader classLoader) {
            this.menuPresenterState = parcel.readBundle(classLoader);
        }
    }

    public void selectTab(int i) {
        if (i >= 0 && i <= 15) {
            animateToTab(i);
            MenuItem menuItem2 = this.menuItem;
            if (menuItem2 != null) {
                menuItem2.setChecked(false);
            } else {
                this.menu.getItem(0).setChecked(false);
            }
            MenuItem item = this.menu.getItem(i);
            this.menuItem = item;
            item.setChecked(true);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0020, code lost:
        r3 = findCollapsingAppbarLayout((android.view.ViewGroup) r3);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setupwithVPAnimate(int r3) {
        /*
        // Method dump skipped, instructions count: 110
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.tabbar.TabBarView.setupwithVPAnimate(int):void");
    }

    public void animateToTab(int i) {
        if (i >= 0 && i <= 15) {
            if (getWindowToken() == null || !ViewCompat.isLaidOut(this)) {
                setScrollPosition(i, 0.0f, true);
                return;
            }
            int scrollX = getScrollX();
            int calculateScrollXForTab = calculateScrollXForTab(i, 0.0f);
            if (scrollX != calculateScrollXForTab) {
                ensureScrollAnimator();
                this.scrollAnimator.setIntValues(scrollX, calculateScrollXForTab);
                this.scrollAnimator.start();
            }
            this.mSlidingTabIndicator.animateIndicatorToPosition(i, 325);
        }
    }

    private void ensureScrollAnimator() {
        if (this.scrollAnimator == null) {
            ValueAnimator valueAnimator = new ValueAnimator();
            this.scrollAnimator = valueAnimator;
            valueAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            this.scrollAnimator.setDuration(1500L);
            this.scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                /* class com.google.android.material.tabbar.TabBarView.AnonymousClass5 */

                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TabBarView.this.scrollTo(((Integer) valueAnimator.getAnimatedValue()).intValue(), 0);
                }
            });
        }
    }

    private void setSelectedTabView(int i) {
        int childCount = this.menuView.getChildCount();
        if (i < childCount) {
            int i2 = 0;
            while (i2 < childCount) {
                View childAt = this.menuView.getChildAt(i2);
                boolean z = true;
                if (this.menu.getItem(i2).isVisible()) {
                    childAt.setSelected(i2 == i);
                    if (i2 != i) {
                        z = false;
                    }
                    childAt.setActivated(z);
                } else if (i2 == i) {
                    int i3 = i2 + 1;
                    this.menuView.getChildAt(i3).setSelected(true);
                    this.menuView.getChildAt(i3).setActivated(true);
                }
                i2++;
            }
        }
    }

    private int calculateScrollXForTab(int i, float f) {
        View childAt = this.menuView.getChildAt(i);
        int i2 = i + 1;
        View childAt2 = i2 < this.menuView.getChildCount() ? this.menuView.getChildAt(i2) : null;
        int i3 = 0;
        int width = childAt != null ? childAt.getWidth() : 0;
        if (childAt2 != null) {
            i3 = childAt2.getWidth();
        }
        int left = (childAt.getLeft() + (width / 2)) - (getWidth() / 2);
        int i4 = (int) (((float) (width + i3)) * 0.5f * f);
        return ViewCompat.getLayoutDirection(this) == 0 ? left + i4 : left - i4;
    }

    public void setScrollPosition(int i, float f, boolean z) {
        setScrollPosition(i, f, z, true);
    }

    public boolean shouldDelayChildPressedState() {
        return getTabScrollRange() > 0;
    }

    private int getTabScrollRange() {
        return Math.max(0, ((this.mSlidingTabIndicator.getWidth() - getWidth()) - getPaddingLeft()) - getPaddingRight());
    }

    public void setScrollPosition(int i, float f, boolean z, boolean z2) {
        CollapsingAppbarLayout findCollapsingAppbarLayout;
        CollapsingAppbarLayout findCollapsingAppbarLayout2;
        int round = Math.round(((float) i) + f);
        if (round >= 0 && round < this.menuView.getChildCount()) {
            if (this.mAdapter == null) {
                this.mAdapter = this.viewPager.getAdapter();
            }
            PagerAdapter pagerAdapter = this.mAdapter;
            if (pagerAdapter instanceof FragmentPagerAdapter) {
                View view = ((FragmentPagerAdapter) pagerAdapter).getItem(i).getView();
                if ((view instanceof ViewGroup) && (findCollapsingAppbarLayout2 = findCollapsingAppbarLayout((ViewGroup) view)) != null) {
                    findCollapsingAppbarLayout2.setExpandedTitleAlpha((int) ((1.0f - Math.min(2.0f * f, 1.0f)) * 255.0f));
                }
                int i2 = i + 1;
                if (i2 < this.menu.getVisibleItems().size()) {
                    View view2 = ((FragmentPagerAdapter) this.mAdapter).getItem(i2).getView();
                    if ((view2 instanceof ViewGroup) && (findCollapsingAppbarLayout = findCollapsingAppbarLayout((ViewGroup) view2)) != null) {
                        findCollapsingAppbarLayout.setExpandedTitleAlpha((int) (Math.max((((double) f) - 0.5d) * 2.0d, 0.0d) * 255.0d));
                    }
                }
            }
            if (z2) {
                this.mSlidingTabIndicator.setIndicatorPositionFromTabPosition(i, f);
            }
            ValueAnimator valueAnimator = this.scrollAnimator;
            if (valueAnimator != null && valueAnimator.isRunning()) {
                this.scrollAnimator.cancel();
            }
            if (this.menu.size() > 6) {
                scrollTo(calculateScrollXForTab(i, f), 0);
            }
            if (z) {
                setSelectedTabView(round);
            }
        }
    }

    public void setupWithViewPager(ViewPager viewPager2) {
        setupWithViewPager(viewPager2, false);
    }

    public void setMenu(MenuBuilder menuBuilder) {
        this.menu = menuBuilder;
        menuBuilder.addMenuPresenter(this.presenter);
        this.presenter.setUpdateSuspended(true);
        this.presenter.initForMenu(getContext(), this.menu);
        this.presenter.setUpdateSuspended(false);
        this.presenter.updateMenuView(true);
    }

    public void setInitWithAnim(boolean z) {
        this.mPlayAnimation = z;
        TabBarMenuView tabBarMenuView = this.menuView;
        if (tabBarMenuView != null) {
            tabBarMenuView.displayAnim(z);
        }
    }

    private void setupWithViewPager(ViewPager viewPager2, boolean z) {
        TabBarOnPageChangeListener tabBarOnPageChangeListener;
        ViewPager viewPager3 = this.viewPager;
        if (!(viewPager3 == null || (tabBarOnPageChangeListener = this.pageChangeListener) == null)) {
            viewPager3.removeOnPageChangeListener(tabBarOnPageChangeListener);
        }
        if (viewPager2 != null) {
            this.viewPager = viewPager2;
            if (this.pageChangeListener == null) {
                this.pageChangeListener = new TabBarOnPageChangeListener(this);
            }
            this.pageChangeListener.reset();
            viewPager2.addOnPageChangeListener(this.pageChangeListener);
            if (this.isInitLine) {
                setScrollPosition(viewPager2.getCurrentItem(), 0.0f, true);
            }
        }
        this.setupViewPagerImplicitly = z;
    }

    private CollapsingAppbarLayout findCollapsingAppbarLayout(ViewGroup viewGroup) {
        CollapsingAppbarLayout findCollapsingAppbarLayout;
        if (viewGroup == null) {
            return null;
        }
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof CollapsingAppbarLayout) {
                return (CollapsingAppbarLayout) childAt;
            }
            if ((childAt instanceof ViewGroup) && (findCollapsingAppbarLayout = findCollapsingAppbarLayout((ViewGroup) childAt)) != null) {
                return findCollapsingAppbarLayout;
            }
        }
        return null;
    }

    public class TabBarOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private ArrayList<FloatingActionButton> mFabList;
        private List<View> mViewList;
        private int previousScrollState;
        private int scrollState;
        private final WeakReference<TabBarView> tabBar;
        private ArrayList<Boolean> visibleList = new ArrayList<>();

        public TabBarOnPageChangeListener(TabBarView tabBarView) {
            this.tabBar = new WeakReference<>(tabBarView);
        }

        public void onDestory() {
            this.visibleList = null;
            this.mViewList = null;
            this.mFabList = null;
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
            this.previousScrollState = this.scrollState;
            this.scrollState = i;
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
            TabBarView tabBarView = this.tabBar.get();
            if (tabBarView != null) {
                boolean z = false;
                boolean z2 = this.scrollState != 2 || this.previousScrollState == 1;
                if (!(this.scrollState == 2 && this.previousScrollState == 0)) {
                    z = true;
                }
                if (TabBarView.this.isInitLine) {
                    tabBarView.setScrollPosition(i, f, z2, z);
                    if (TabBarView.this.menuView.getIconTintList() != TabBarView.this.mUnCheckTintColor) {
                        TabBarView tabBarView2 = TabBarView.this;
                        tabBarView2.menuView.setIconTintList(tabBarView2.mUnCheckTintColor);
                    }
                }
            }
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            TabBarView tabBarView = this.tabBar.get();
            if (tabBarView != null && i < tabBarView.getMenu().size()) {
                if (this.mViewList == null) {
                    this.mViewList = OPViewGroupUtils.getAllChildViews(tabBarView.getRootView());
                }
                if (this.mFabList == null) {
                    this.mFabList = new ArrayList<>();
                }
                if (this.mFabList.isEmpty()) {
                    for (int i2 = 0; i2 < this.mViewList.size(); i2++) {
                        if (this.mViewList.get(i2) instanceof FloatingActionButton) {
                            this.mFabList.add((FloatingActionButton) this.mViewList.get(i2));
                            this.visibleList.add(Boolean.TRUE);
                        }
                    }
                }
                if (!this.mFabList.isEmpty()) {
                    for (int i3 = 0; i3 < this.mFabList.size(); i3++) {
                        FloatingActionButton floatingActionButton = this.mFabList.get(i3);
                        if (floatingActionButton != null && floatingActionButton.getScrollHideBoolean()) {
                            onShow(floatingActionButton);
                        }
                    }
                }
                tabBarView.selectTab(i);
                if (!TabBarView.this.menu.getItem(i).isVisible()) {
                    TabBarView.this.menu.getItem(i + 1).setChecked(true);
                }
            }
        }

        public void onShow(final View view) {
            view.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(150).setListener(new Animator.AnimatorListener(this) {
                /* class com.google.android.material.tabbar.TabBarView.TabBarOnPageChangeListener.AnonymousClass1 */

                public void onAnimationCancel(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                }

                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                    view.setClickable(true);
                    view.setVisibility(0);
                }
            }).setInterpolator(AnimatorUtils.op_control_interpolator_linear_out_slow_in);
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.scrollState = 0;
            this.previousScrollState = 0;
        }
    }

    /* access modifiers changed from: package-private */
    public void updateTabViews(boolean z) {
        for (int i = 0; i < this.menuView.getChildCount(); i++) {
            View childAt = this.menuView.getChildAt(i);
            childAt.setMinimumWidth(getTabMinWidth());
            updateTabViewLayoutParams((LinearLayout.LayoutParams) childAt.getLayoutParams());
            if (z) {
                childAt.requestLayout();
            }
        }
    }

    private int getTabMinWidth() {
        return (int) ViewUtils.dpToPx(getContext(), 78);
    }

    private void updateTabViewLayoutParams(LinearLayout.LayoutParams layoutParams2) {
        if (this.menu.size() < 6) {
            layoutParams2.width = 0;
            layoutParams2.weight = 1.0f;
            return;
        }
        layoutParams2.width = -2;
        layoutParams2.weight = 0.0f;
    }

    /* access modifiers changed from: package-private */
    public class SlidingTabIndicator extends LinearLayout {
        private int animationStartLeft = -1;
        private int animationStartRight = -1;
        private final GradientDrawable defaultSelectionIndicator;
        ValueAnimator indicatorAnimator;
        int indicatorLeft = -1;
        int indicatorRight = -1;
        private int layoutDirection = -1;
        private int selectedIndicatorHeight;
        private final Paint selectedIndicatorPaint;
        int selectedPosition = -1;
        float selectionOffset;

        SlidingTabIndicator(Context context) {
            super(context);
            setWillNotDraw(false);
            this.selectedIndicatorPaint = new Paint();
            this.defaultSelectionIndicator = new GradientDrawable();
        }

        /* access modifiers changed from: package-private */
        public void setSelectedIndicatorColor(int i) {
            if (this.selectedIndicatorPaint.getColor() != i) {
                this.selectedIndicatorPaint.setColor(i);
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        /* access modifiers changed from: package-private */
        public void setSelectedIndicatorHeight(int i) {
            if (this.selectedIndicatorHeight != i) {
                this.selectedIndicatorHeight = i;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        /* access modifiers changed from: package-private */
        public void setIndicatorPositionFromTabPosition(int i, float f) {
            ValueAnimator valueAnimator = this.indicatorAnimator;
            if (valueAnimator != null && valueAnimator.isRunning()) {
                this.indicatorAnimator.cancel();
            }
            this.selectedPosition = i;
            this.selectionOffset = f;
            updateIndicatorPosition();
        }

        public void onRtlPropertiesChanged(int i) {
            super.onRtlPropertiesChanged(i);
            if (Build.VERSION.SDK_INT < 23 && this.layoutDirection != i) {
                requestLayout();
                this.layoutDirection = i;
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            if (View.MeasureSpec.getMode(i) == 1073741824) {
                int childCount = TabBarView.this.menuView.getChildCount();
                int i3 = 0;
                for (int i4 = 0; i4 < childCount; i4++) {
                    View childAt = TabBarView.this.menuView.getChildAt(i4);
                    if (childAt.getVisibility() == 0) {
                        i3 = Math.max(i3, childAt.getMeasuredWidth());
                    }
                }
                if (i3 > 0) {
                    boolean z = true;
                    if (i3 * childCount <= getMeasuredWidth() - (((int) ViewUtils.dpToPx(getContext(), 16)) * 2)) {
                        boolean z2 = false;
                        for (int i5 = 0; i5 < childCount; i5++) {
                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) TabBarView.this.menuView.getChildAt(i5).getLayoutParams();
                            if (layoutParams.width != i3 || layoutParams.weight != 0.0f) {
                                layoutParams.width = i3;
                                layoutParams.weight = 0.0f;
                                z2 = true;
                            }
                        }
                        z = z2;
                    } else {
                        TabBarView.this.layoutParams.gravity = 0;
                        TabBarView.this.updateTabViews(false);
                    }
                    if (z) {
                        super.onMeasure(i, i2);
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ValueAnimator valueAnimator = this.indicatorAnimator;
            if (valueAnimator == null || !valueAnimator.isRunning()) {
                updateIndicatorPosition();
            } else {
                updateOrRecreateIndicatorAnimation(false, this.selectedPosition, -1);
            }
        }

        private void updateIndicatorPosition() {
            int i;
            int i2;
            int i3;
            int i4;
            TabBarView tabBarView = TabBarView.this;
            if (tabBarView.menuView != null && tabBarView.menu.size() > 0) {
                View childAt = TabBarView.this.menuView.getChildAt(this.selectedPosition);
                if (childAt != null && childAt.getWidth() > 0) {
                    i = childAt.getLeft();
                    i2 = childAt.getRight();
                    if (this.selectionOffset > 0.0f && this.selectedPosition < TabBarView.this.menuView.getChildCount() - 1) {
                        if (!TabBarView.this.menu.getItem(this.selectedPosition + 1).isVisible()) {
                            i3 = this.selectedPosition + 2;
                        } else {
                            i3 = 1 + this.selectedPosition;
                        }
                        TabBarItemView tabBarItemView = (TabBarItemView) TabBarView.this.menuView.getChildAt(i3);
                        int left = tabBarItemView.getLeft();
                        int right = tabBarItemView.getRight();
                        float f = this.selectionOffset;
                        float f2 = (float) left;
                        int i5 = (int) ((f * f2) + ((1.0f - f) * ((float) i)));
                        float f3 = (float) right;
                        int i6 = (int) ((f * f3) + ((1.0f - f) * ((float) i2)));
                        if (((double) f) < 0.2d) {
                            i2 = (int) ((f3 * f) + ((1.0f - f) * ((float) i6)) + ((((1.0f - f) * 450.0f) * f) / 2.0f));
                            i4 = (int) (((f2 * f) + ((1.0f - f) * ((float) i5))) - ((((1.0f - f) * 450.0f) * f) / 2.0f));
                        } else {
                            float f4 = this.selectionOffset;
                            i2 = (int) ((((double) ((f3 * f) + ((1.0f - f) * ((float) i6)))) + (((double) (((1.0f - f) * 450.0f) * 1.1f)) * (1.0d - (Math.abs(0.2d - ((double) f)) * 0.1136d)))) - ((double) ((1.0f - f4) * 450.0f)));
                            i4 = (int) ((((double) ((f2 * f4) + ((1.0f - f4) * ((float) i5)))) + (((double) (((1.0f - f4) * 450.0f) * 0.9f)) * ((Math.abs(0.2d - ((double) f4)) * 0.1389d) + 1.0d))) - ((double) ((1.0f - this.selectionOffset) * 450.0f)));
                        }
                        i = i4;
                    }
                } else if (this.selectedPosition <= 0 || TabBarView.this.menu.getItem(this.selectedPosition).isVisible()) {
                    i = -1;
                    i2 = -1;
                } else {
                    TabBarView.this.menu.getItem(this.selectedPosition + 1).setChecked(true);
                    return;
                }
                setIndicatorPosition(i, i2);
            }
        }

        /* access modifiers changed from: package-private */
        public void setIndicatorPosition(int i, int i2) {
            if (i != this.indicatorLeft || i2 != this.indicatorRight) {
                this.indicatorLeft = i;
                this.indicatorRight = i2;
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

        /* access modifiers changed from: package-private */
        public void animateIndicatorToPosition(int i, int i2) {
            ValueAnimator valueAnimator = this.indicatorAnimator;
            if (valueAnimator != null && valueAnimator.isRunning()) {
                this.indicatorAnimator.cancel();
            }
            updateOrRecreateIndicatorAnimation(true, i, i2);
        }

        private void updateOrRecreateIndicatorAnimation(boolean z, final int i, int i2) {
            View view;
            TabBarView tabBarView = TabBarView.this;
            if (tabBarView.menuView != null && tabBarView.menu.size() > 0) {
                if (!TabBarView.this.menu.getItem(i).isVisible()) {
                    view = TabBarView.this.menuView.getChildAt(i + 1);
                } else {
                    view = TabBarView.this.menuView.getChildAt(i);
                }
                if (view == null) {
                    updateIndicatorPosition();
                    return;
                }
                final int left = view.getLeft();
                final int right = view.getRight();
                int i3 = this.indicatorLeft;
                int i4 = this.indicatorRight;
                if (i3 != left || i4 != right) {
                    if (z) {
                        this.animationStartLeft = i3;
                        this.animationStartRight = i4;
                    }
                    AnonymousClass1 r2 = new ValueAnimator.AnimatorUpdateListener() {
                        /* class com.google.android.material.tabbar.TabBarView.SlidingTabIndicator.AnonymousClass1 */

                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int i;
                            float animatedFraction = valueAnimator.getAnimatedFraction();
                            double d = (double) animatedFraction;
                            if (d < 0.25d) {
                                i = (int) (((float) right) + (800.0f * animatedFraction));
                            } else {
                                i = (int) ((((double) right) + ((1.0d - ((Math.abs(0.25d - d) * 4.0d) / 15.0d)) * 200.0d)) - 160.0d);
                            }
                            SlidingTabIndicator slidingTabIndicator = SlidingTabIndicator.this;
                            slidingTabIndicator.setIndicatorPosition(AnimationUtils.lerp(slidingTabIndicator.animationStartLeft, left, animatedFraction), AnimationUtils.lerp(SlidingTabIndicator.this.animationStartRight, i, animatedFraction));
                        }
                    };
                    if (z) {
                        ValueAnimator valueAnimator = new ValueAnimator();
                        this.indicatorAnimator = valueAnimator;
                        valueAnimator.setInterpolator(AnimatorUtils.op_control_interpolator_fast_out_slow_in_auxiliary);
                        valueAnimator.setDuration((long) i2);
                        valueAnimator.setFloatValues(0.0f, 1.0f);
                        valueAnimator.addUpdateListener(r2);
                        valueAnimator.addListener(new AnimatorListenerAdapter() {
                            /* class com.google.android.material.tabbar.TabBarView.SlidingTabIndicator.AnonymousClass2 */

                            public void onAnimationStart(Animator animator) {
                                SlidingTabIndicator.this.selectedPosition = i;
                            }

                            public void onAnimationEnd(Animator animator) {
                                SlidingTabIndicator slidingTabIndicator = SlidingTabIndicator.this;
                                slidingTabIndicator.selectedPosition = i;
                                slidingTabIndicator.selectionOffset = 0.0f;
                            }
                        });
                        valueAnimator.start();
                        return;
                    }
                    this.indicatorAnimator.removeAllUpdateListeners();
                    this.indicatorAnimator.addUpdateListener(r2);
                }
            }
        }

        public void draw(Canvas canvas) {
            Drawable drawable = TabBarView.this.tabSelectedIndicator;
            int intrinsicHeight = drawable != null ? drawable.getIntrinsicHeight() : 0;
            int i = this.selectedIndicatorHeight;
            if (i >= 0) {
                intrinsicHeight = i;
            }
            int height = (getHeight() - intrinsicHeight) / 2;
            int height2 = (getHeight() + intrinsicHeight) / 2;
            Log.d("chenhb", "indicatorTop = " + height + ", indicatorBottom = " + height2);
            int i2 = this.indicatorLeft;
            if (i2 >= 0 && this.indicatorRight > i2 && TabBarView.this.isInitLine) {
                Drawable drawable2 = TabBarView.this.tabSelectedIndicator;
                if (drawable2 == null) {
                    drawable2 = this.defaultSelectionIndicator;
                }
                Drawable mutate = DrawableCompat.wrap(drawable2).mutate();
                mutate.setBounds(this.indicatorLeft + ((int) ViewUtils.dpToPx(getContext(), 20)), height - getResources().getDimensionPixelOffset(R$dimen.design_tab_bar_indicator_margin), this.indicatorRight + ((int) ViewUtils.dpToPx(getContext(), 12)), height2 - getResources().getDimensionPixelOffset(R$dimen.design_tab_bar_indicator_margin));
                Paint paint = this.selectedIndicatorPaint;
                if (paint != null) {
                    if (Build.VERSION.SDK_INT == 21) {
                        mutate.setColorFilter(paint.getColor(), PorterDuff.Mode.SRC_IN);
                    } else {
                        DrawableCompat.setTint(mutate, paint.getColor());
                    }
                }
                mutate.draw(canvas);
            }
            super.draw(canvas);
        }
    }
}
