package com.google.android.material.indicator;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.text.TextUtilsCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.R$color;
import com.google.android.material.R$dimen;
import com.google.android.material.indicator.IndicatorManager;
import com.google.android.material.indicator.animation.type.AnimationType;
import com.google.android.material.indicator.draw.controller.DrawController;
import com.google.android.material.indicator.draw.data.Indicator;
import com.google.android.material.indicator.draw.data.Orientation;
import com.google.android.material.indicator.draw.data.PositionSavedState;
import com.google.android.material.indicator.draw.data.RtlMode;
import com.google.android.material.indicator.utils.CoordinatesUtils;
import com.google.android.material.indicator.utils.DensityUtils;

public class PageIndicatorView extends View implements ViewPager.OnPageChangeListener, IndicatorManager.Listener, ViewPager.OnAdapterChangeListener, View.OnTouchListener {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    private Runnable idleRunnable = new Runnable() {
        /* class com.google.android.material.indicator.PageIndicatorView.AnonymousClass2 */

        public void run() {
            PageIndicatorView.this.manager.indicator().setIdle(true);
            PageIndicatorView.this.hideWithAnimation();
        }
    };
    private boolean isInteractionEnabled;
    private IndicatorManager manager;
    private DataSetObserver setObserver;
    private ViewPager viewPager;

    public PageIndicatorView(Context context) {
        super(context);
        init(context, null);
    }

    public PageIndicatorView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet);
    }

    public PageIndicatorView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet);
    }

    @TargetApi(21)
    public PageIndicatorView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        unRegisterSetObserver();
        super.onDetachedFromWindow();
    }

    public Parcelable onSaveInstanceState() {
        Indicator indicator = this.manager.indicator();
        PositionSavedState positionSavedState = new PositionSavedState(super.onSaveInstanceState());
        positionSavedState.setSelectedPosition(indicator.getSelectedPosition());
        positionSavedState.setSelectingPosition(indicator.getSelectingPosition());
        positionSavedState.setLastSelectedPosition(indicator.getLastSelectedPosition());
        return positionSavedState;
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof PositionSavedState) {
            Indicator indicator = this.manager.indicator();
            PositionSavedState positionSavedState = (PositionSavedState) parcelable;
            indicator.setSelectedPosition(positionSavedState.getSelectedPosition());
            indicator.setSelectingPosition(positionSavedState.getSelectingPosition());
            indicator.setLastSelectedPosition(positionSavedState.getLastSelectedPosition());
            super.onRestoreInstanceState(positionSavedState.getSuperState());
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        Pair<Integer, Integer> measureViewSize = this.manager.drawer().measureViewSize(i, i2);
        setMeasuredDimension(((Integer) measureViewSize.first).intValue(), ((Integer) measureViewSize.second).intValue());
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.manager.drawer().draw(canvas);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.manager.drawer().touch(motionEvent);
        return true;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!this.manager.indicator().isFadeOnIdle()) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action == 0) {
            stopIdleRunnable();
        } else if (action == 1) {
            startIdleRunnable();
        }
        return false;
    }

    @Override // com.google.android.material.indicator.IndicatorManager.Listener
    public void onIndicatorUpdated() {
        invalidate();
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageScrolled(int i, float f, int i2) {
        onPageScroll(i, f);
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageSelected(int i) {
        onPageSelect(i);
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageScrollStateChanged(int i) {
        if (i == 0) {
            this.manager.indicator().setInteractiveAnimation(this.isInteractionEnabled);
        }
    }

    @Override // androidx.viewpager.widget.ViewPager.OnAdapterChangeListener
    public void onAdapterChanged(ViewPager viewPager2, PagerAdapter pagerAdapter, PagerAdapter pagerAdapter2) {
        DataSetObserver dataSetObserver;
        if (this.manager.indicator().isDynamicCount()) {
            if (!(pagerAdapter == null || (dataSetObserver = this.setObserver) == null)) {
                pagerAdapter.unregisterDataSetObserver(dataSetObserver);
                this.setObserver = null;
            }
            registerSetObserver();
        }
        updateState();
    }

    public void setNumPages(int i) {
        if (i >= 0 && this.manager.indicator().getCount() != i) {
            this.manager.indicator().setCount(i);
            updateVisibility();
            requestLayout();
        }
    }

    public int getCount() {
        return this.manager.indicator().getCount();
    }

    public void setDynamicCount(boolean z) {
        this.manager.indicator().setDynamicCount(z);
        if (z) {
            registerSetObserver();
        } else {
            unRegisterSetObserver();
        }
    }

    public void setFadeOnIdle(boolean z) {
        this.manager.indicator().setFadeOnIdle(z);
        if (z) {
            startIdleRunnable();
        } else {
            stopIdleRunnable();
        }
    }

    public void setRadius(int i) {
        if (i < 0) {
            i = 0;
        }
        this.manager.indicator().setRadius(DensityUtils.dpToPx(i));
        invalidate();
    }

    public void setRadius(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.manager.indicator().setRadius((int) f);
        invalidate();
    }

    public int getRadius() {
        return this.manager.indicator().getRadius();
    }

    public void setPadding(int i) {
        if (i < 0) {
            i = 0;
        }
        this.manager.indicator().setPadding(DensityUtils.dpToPx(i));
        invalidate();
    }

    public void setPadding(float f) {
        if (f < 0.0f) {
            f = 0.0f;
        }
        this.manager.indicator().setPadding((int) f);
        invalidate();
    }

    public int getPadding() {
        return this.manager.indicator().getPadding();
    }

    public void setStrokeWidth(float f) {
        int radius = this.manager.indicator().getRadius();
        if (f < 0.0f) {
            f = 0.0f;
        } else {
            float f2 = (float) radius;
            if (f > f2) {
                f = f2;
            }
        }
        this.manager.indicator().setStroke((int) f);
        invalidate();
    }

    public void setStrokeWidth(int i) {
        int dpToPx = DensityUtils.dpToPx(i);
        int radius = this.manager.indicator().getRadius();
        if (dpToPx < 0) {
            dpToPx = 0;
        } else if (dpToPx > radius) {
            dpToPx = radius;
        }
        this.manager.indicator().setStroke(dpToPx);
        invalidate();
    }

    public int getStrokeWidth() {
        return this.manager.indicator().getStroke();
    }

    public void setSelectedColor(int i) {
        this.manager.indicator().setSelectedColor(i);
        invalidate();
    }

    public int getSelectedColor() {
        return this.manager.indicator().getSelectedColor();
    }

    public void setUnselectedColor(int i) {
        this.manager.indicator().setUnselectedColor(i);
        invalidate();
    }

    public int getUnselectedColor() {
        return this.manager.indicator().getUnselectedColor();
    }

    public void setAutoVisibility(boolean z) {
        if (!z) {
            setVisibility(0);
        }
        this.manager.indicator().setAutoVisibility(z);
        updateVisibility();
    }

    public void setOrientation(Orientation orientation) {
        if (orientation != null) {
            this.manager.indicator().setOrientation(orientation);
            requestLayout();
        }
    }

    public void setAnimationDuration(long j) {
        this.manager.indicator().setAnimationDuration(j);
    }

    public void setIdleDuration(long j) {
        this.manager.indicator().setIdleDuration(j);
        if (this.manager.indicator().isFadeOnIdle()) {
            startIdleRunnable();
        } else {
            stopIdleRunnable();
        }
    }

    public long getAnimationDuration() {
        return this.manager.indicator().getAnimationDuration();
    }

    public void setAnimationType(AnimationType animationType) {
        this.manager.onValueUpdated(null);
        if (animationType != null) {
            this.manager.indicator().setAnimationType(animationType);
        } else {
            this.manager.indicator().setAnimationType(AnimationType.NONE);
        }
        invalidate();
    }

    public void setInteractiveAnimation(boolean z) {
        this.manager.indicator().setInteractiveAnimation(z);
        this.isInteractionEnabled = z;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public void setViewPager(ViewPager viewPager2) {
        releaseViewPager();
        if (viewPager2 != null) {
            this.viewPager = viewPager2;
            viewPager2.addOnPageChangeListener(this);
            this.viewPager.addOnAdapterChangeListener(this);
            this.viewPager.setOnTouchListener(this);
            this.manager.indicator().setViewPagerId(this.viewPager.getId());
            setDynamicCount(this.manager.indicator().isDynamicCount());
            updateState();
        }
    }

    public void releaseViewPager() {
        ViewPager viewPager2 = this.viewPager;
        if (viewPager2 != null) {
            viewPager2.removeOnPageChangeListener(this);
            this.viewPager.removeOnAdapterChangeListener(this);
            this.viewPager = null;
        }
    }

    public void setRtlMode(RtlMode rtlMode) {
        Indicator indicator = this.manager.indicator();
        if (rtlMode == null) {
            indicator.setRtlMode(RtlMode.Off);
        } else {
            indicator.setRtlMode(rtlMode);
        }
        if (this.viewPager != null) {
            int selectedPosition = indicator.getSelectedPosition();
            if (isRtl()) {
                selectedPosition = (indicator.getCount() - 1) - selectedPosition;
            } else {
                ViewPager viewPager2 = this.viewPager;
                if (viewPager2 != null) {
                    selectedPosition = viewPager2.getCurrentItem();
                }
            }
            indicator.setLastSelectedPosition(selectedPosition);
            indicator.setSelectingPosition(selectedPosition);
            indicator.setSelectedPosition(selectedPosition);
            invalidate();
        }
    }

    public int getSelection() {
        return this.manager.indicator().getSelectedPosition();
    }

    public void setSelection(int i) {
        Indicator indicator = this.manager.indicator();
        int adjustPosition = adjustPosition(i);
        if (adjustPosition != indicator.getSelectedPosition() && adjustPosition != indicator.getSelectingPosition()) {
            indicator.setInteractiveAnimation(false);
            indicator.setLastSelectedPosition(indicator.getSelectedPosition());
            indicator.setSelectingPosition(adjustPosition);
            indicator.setSelectedPosition(adjustPosition);
            this.manager.animate().basic();
        }
    }

    public void setPosition(int i) {
        Indicator indicator = this.manager.indicator();
        AnimationType animationType = indicator.getAnimationType();
        indicator.setAnimationType(AnimationType.NONE);
        setSelection(i);
        indicator.setAnimationType(animationType);
    }

    public void setProgress(int i, float f) {
        Indicator indicator = this.manager.indicator();
        if (indicator.isInteractiveAnimation()) {
            int count = indicator.getCount();
            if (count <= 0 || i < 0) {
                i = 0;
            } else {
                int i2 = count - 1;
                if (i > i2) {
                    i = i2;
                }
            }
            if (f < 0.0f) {
                f = 0.0f;
            } else if (f > 1.0f) {
                f = 1.0f;
            }
            if (f == 1.0f) {
                indicator.setLastSelectedPosition(indicator.getSelectedPosition());
                indicator.setSelectedPosition(i);
            }
            indicator.setSelectingPosition(i);
            this.manager.animate().interactive(f);
        }
    }

    public void setClickListener(DrawController.ClickListener clickListener) {
        this.manager.drawer().setClickListener(clickListener);
    }

    private void init(Context context, AttributeSet attributeSet) {
        initIndicatorManager(context, attributeSet);
        if (this.manager.indicator().isFadeOnIdle()) {
            startIdleRunnable();
        }
    }

    private void initIndicatorManager(Context context, AttributeSet attributeSet) {
        IndicatorManager indicatorManager = new IndicatorManager(this);
        this.manager = indicatorManager;
        Indicator indicator = indicatorManager.indicator();
        indicator.setPaddingLeft(getPaddingLeft());
        indicator.setPaddingTop(getPaddingTop());
        indicator.setPaddingRight(getPaddingRight());
        indicator.setPaddingBottom(getPaddingBottom());
        indicator.setInteractiveAnimation(false);
        indicator.setAutoVisibility(true);
        indicator.setDynamicCount(false);
        indicator.setAnimationType(AnimationType.WORM);
        indicator.setRtlMode(RtlMode.Auto);
        indicator.setFadeOnIdle(false);
        indicator.setIdleDuration(3000);
        indicator.setRadius(7);
        indicator.setOrientation(Orientation.HORIZONTAL);
        indicator.setPadding(context.getResources().getDimensionPixelOffset(R$dimen.op_control_margin_space1));
        indicator.setAnimationDuration(125);
        indicator.setSelectedColor(context.getColor(R$color.op_control_icon_color_active_default));
        indicator.setUnselectedColor(context.getColor(R$color.op_control_icon_color_disable_default));
        this.isInteractionEnabled = indicator.isInteractiveAnimation();
    }

    private void registerSetObserver() {
        ViewPager viewPager2;
        if (this.setObserver == null && (viewPager2 = this.viewPager) != null && viewPager2.getAdapter() != null) {
            this.setObserver = new DataSetObserver() {
                /* class com.google.android.material.indicator.PageIndicatorView.AnonymousClass1 */

                public void onChanged() {
                    PageIndicatorView.this.updateState();
                }
            };
            try {
                this.viewPager.getAdapter().registerDataSetObserver(this.setObserver);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void unRegisterSetObserver() {
        ViewPager viewPager2;
        if (this.setObserver != null && (viewPager2 = this.viewPager) != null && viewPager2.getAdapter() != null) {
            try {
                this.viewPager.getAdapter().unregisterDataSetObserver(this.setObserver);
                this.setObserver = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateState() {
        ViewPager viewPager2 = this.viewPager;
        if (viewPager2 != null && viewPager2.getAdapter() != null) {
            int count = this.viewPager.getAdapter().getCount();
            int currentItem = isRtl() ? (count - 1) - this.viewPager.getCurrentItem() : this.viewPager.getCurrentItem();
            this.manager.indicator().setSelectedPosition(currentItem);
            this.manager.indicator().setSelectingPosition(currentItem);
            this.manager.indicator().setLastSelectedPosition(currentItem);
            this.manager.indicator().setCount(count);
            this.manager.animate().end();
            updateVisibility();
            requestLayout();
        }
    }

    private void updateVisibility() {
        if (this.manager.indicator().isAutoVisibility()) {
            int count = this.manager.indicator().getCount();
            int visibility = getVisibility();
            if (visibility != 0 && count > 1) {
                setVisibility(0);
            } else if (visibility != 4 && count <= 1) {
                setVisibility(4);
            }
        }
    }

    private void onPageSelect(int i) {
        Indicator indicator = this.manager.indicator();
        boolean isViewMeasured = isViewMeasured();
        int count = indicator.getCount();
        if (isViewMeasured) {
            if (isRtl()) {
                i = (count - 1) - i;
            }
            setSelection(i);
        }
    }

    private void onPageScroll(int i, float f) {
        Indicator indicator = this.manager.indicator();
        if (isViewMeasured() && indicator.isInteractiveAnimation() && indicator.getAnimationType() != AnimationType.NONE) {
            Pair<Integer, Float> progress = CoordinatesUtils.getProgress(indicator, i, f, isRtl());
            setProgress(((Integer) progress.first).intValue(), ((Float) progress.second).floatValue());
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: com.google.android.material.indicator.PageIndicatorView$3  reason: invalid class name */
    public static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$google$android$material$indicator$draw$data$RtlMode;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                com.google.android.material.indicator.draw.data.RtlMode[] r0 = com.google.android.material.indicator.draw.data.RtlMode.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.google.android.material.indicator.PageIndicatorView.AnonymousClass3.$SwitchMap$com$google$android$material$indicator$draw$data$RtlMode = r0
                com.google.android.material.indicator.draw.data.RtlMode r1 = com.google.android.material.indicator.draw.data.RtlMode.On     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = com.google.android.material.indicator.PageIndicatorView.AnonymousClass3.$SwitchMap$com$google$android$material$indicator$draw$data$RtlMode     // Catch:{ NoSuchFieldError -> 0x001d }
                com.google.android.material.indicator.draw.data.RtlMode r1 = com.google.android.material.indicator.draw.data.RtlMode.Off     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = com.google.android.material.indicator.PageIndicatorView.AnonymousClass3.$SwitchMap$com$google$android$material$indicator$draw$data$RtlMode     // Catch:{ NoSuchFieldError -> 0x0028 }
                com.google.android.material.indicator.draw.data.RtlMode r1 = com.google.android.material.indicator.draw.data.RtlMode.Auto     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.indicator.PageIndicatorView.AnonymousClass3.<clinit>():void");
        }
    }

    private boolean isRtl() {
        int i = AnonymousClass3.$SwitchMap$com$google$android$material$indicator$draw$data$RtlMode[this.manager.indicator().getRtlMode().ordinal()];
        if (i == 1) {
            return true;
        }
        if (i != 3) {
            return false;
        }
        return TextUtilsCompat.getLayoutDirectionFromLocale(getContext().getResources().getConfiguration().locale) == 1;
    }

    private boolean isViewMeasured() {
        return (getMeasuredHeight() == 0 && getMeasuredWidth() == 0) ? false : true;
    }

    private int adjustPosition(int i) {
        int count = this.manager.indicator().getCount() - 1;
        if (i < 0) {
            return 0;
        }
        return i > count ? count : i;
    }

    private void displayWithAnimation() {
        animate().cancel();
        animate().alpha(1.0f).setDuration(250);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void hideWithAnimation() {
        animate().cancel();
        animate().alpha(0.0f).setDuration(250);
    }

    private void startIdleRunnable() {
        HANDLER.removeCallbacks(this.idleRunnable);
        HANDLER.postDelayed(this.idleRunnable, this.manager.indicator().getIdleDuration());
    }

    private void stopIdleRunnable() {
        HANDLER.removeCallbacks(this.idleRunnable);
        displayWithAnimation();
    }
}
