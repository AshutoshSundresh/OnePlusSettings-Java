package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.C0012R$layout;

public final class SlidingTabLayout extends FrameLayout implements View.OnClickListener {
    private final View mIndicatorView = this.mLayoutInflater.inflate(C0012R$layout.sliding_tab_indicator_view, (ViewGroup) this, false);
    private final LayoutInflater mLayoutInflater;
    private int mSelectedPosition;
    private float mSelectionOffset;
    private final LinearLayout mTitleView;
    private RtlCompatibleViewPager mViewPager;

    public SlidingTabLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLayoutInflater = LayoutInflater.from(context);
        LinearLayout linearLayout = new LinearLayout(context);
        this.mTitleView = linearLayout;
        linearLayout.setGravity(1);
        addView(this.mTitleView, -1, -2);
        View view = this.mIndicatorView;
        addView(view, view.getLayoutParams());
    }

    public void setViewPager(RtlCompatibleViewPager rtlCompatibleViewPager) {
        this.mTitleView.removeAllViews();
        this.mViewPager = rtlCompatibleViewPager;
        if (rtlCompatibleViewPager != null) {
            rtlCompatibleViewPager.addOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int childCount = this.mTitleView.getChildCount();
        if (childCount > 0) {
            this.mIndicatorView.measure(View.MeasureSpec.makeMeasureSpec(this.mTitleView.getMeasuredWidth() / childCount, 1073741824), View.MeasureSpec.makeMeasureSpec(this.mIndicatorView.getMeasuredHeight(), 1073741824));
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mTitleView.getChildCount() > 0) {
            int measuredHeight = getMeasuredHeight();
            int measuredHeight2 = this.mIndicatorView.getMeasuredHeight();
            int measuredWidth = this.mIndicatorView.getMeasuredWidth();
            int measuredWidth2 = getMeasuredWidth();
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            LinearLayout linearLayout = this.mTitleView;
            linearLayout.layout(paddingLeft, 0, linearLayout.getMeasuredWidth() + paddingRight, this.mTitleView.getMeasuredHeight());
            if (isRtlMode()) {
                this.mIndicatorView.layout(measuredWidth2 - measuredWidth, measuredHeight - measuredHeight2, measuredWidth2, measuredHeight);
            } else {
                this.mIndicatorView.layout(0, measuredHeight - measuredHeight2, measuredWidth, measuredHeight);
            }
        }
    }

    public void onClick(View view) {
        int childCount = this.mTitleView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (view == this.mTitleView.getChildAt(i)) {
                this.mViewPager.setCurrentItem(i);
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void onViewPagerPageChanged(int i, float f) {
        this.mSelectedPosition = i;
        this.mSelectionOffset = f;
        this.mIndicatorView.setTranslationX((float) (isRtlMode() ? -getIndicatorLeft() : getIndicatorLeft()));
    }

    private void populateTabStrip() {
        PagerAdapter adapter = this.mViewPager.getAdapter();
        int i = 0;
        while (i < adapter.getCount()) {
            TextView textView = (TextView) this.mLayoutInflater.inflate(C0012R$layout.sliding_tab_title_view, (ViewGroup) this.mTitleView, false);
            textView.setText(adapter.getPageTitle(i));
            textView.setOnClickListener(this);
            this.mTitleView.addView(textView);
            textView.setSelected(i == this.mViewPager.getCurrentItem());
            i++;
        }
    }

    private int getIndicatorLeft() {
        int left = this.mTitleView.getChildAt(this.mSelectedPosition).getLeft();
        if (this.mSelectionOffset <= 0.0f || this.mSelectedPosition >= getChildCount() - 1) {
            return left;
        }
        return (int) ((this.mSelectionOffset * ((float) this.mTitleView.getChildAt(this.mSelectedPosition + 1).getLeft())) + ((1.0f - this.mSelectionOffset) * ((float) left)));
    }

    private boolean isRtlMode() {
        return getLayoutDirection() == 1;
    }

    /* access modifiers changed from: private */
    public final class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        private InternalViewPagerListener() {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
            int childCount = SlidingTabLayout.this.mTitleView.getChildCount();
            if (childCount != 0 && i >= 0 && i < childCount) {
                SlidingTabLayout.this.onViewPagerPageChanged(i, f);
            }
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
            this.mScrollState = i;
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            int rtlAwareIndex = SlidingTabLayout.this.mViewPager.getRtlAwareIndex(i);
            if (this.mScrollState == 0) {
                SlidingTabLayout.this.onViewPagerPageChanged(rtlAwareIndex, 0.0f);
            }
            int childCount = SlidingTabLayout.this.mTitleView.getChildCount();
            int i2 = 0;
            while (i2 < childCount) {
                SlidingTabLayout.this.mTitleView.getChildAt(i2).setSelected(rtlAwareIndex == i2);
                i2++;
            }
        }
    }
}
