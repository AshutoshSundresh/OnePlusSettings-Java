package androidx.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import androidx.leanback.R$attr;
import androidx.leanback.R$integer;
import androidx.leanback.R$styleable;
import java.util.ArrayList;

public class BaseCardView extends FrameLayout {
    private static final int[] LB_PRESSED_STATE_SET = {16842919};
    private final int mActivatedAnimDuration;
    private Animation mAnim;
    private final Runnable mAnimationTrigger;
    private int mCardType;
    private boolean mDelaySelectedAnim;
    ArrayList<View> mExtraViewList;
    private int mExtraVisibility;
    float mInfoAlpha;
    float mInfoOffset;
    ArrayList<View> mInfoViewList;
    float mInfoVisFraction;
    private int mInfoVisibility;
    private ArrayList<View> mMainViewList;
    private int mMeasuredHeight;
    private int mMeasuredWidth;
    private final int mSelectedAnimDuration;
    private int mSelectedAnimationDelay;

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public BaseCardView(Context context) {
        this(context, null);
    }

    public BaseCardView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.baseCardViewStyle);
    }

    /* JADX INFO: finally extract failed */
    public BaseCardView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAnimationTrigger = new Runnable() {
            /* class androidx.leanback.widget.BaseCardView.AnonymousClass1 */

            public void run() {
                BaseCardView.this.animateInfoOffset(true);
            }
        };
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.lbBaseCardView, i, 0);
        try {
            this.mCardType = obtainStyledAttributes.getInteger(R$styleable.lbBaseCardView_cardType, 0);
            Drawable drawable = obtainStyledAttributes.getDrawable(R$styleable.lbBaseCardView_cardForeground);
            if (drawable != null) {
                setForeground(drawable);
            }
            Drawable drawable2 = obtainStyledAttributes.getDrawable(R$styleable.lbBaseCardView_cardBackground);
            if (drawable2 != null) {
                setBackground(drawable2);
            }
            this.mInfoVisibility = obtainStyledAttributes.getInteger(R$styleable.lbBaseCardView_infoVisibility, 1);
            int integer = obtainStyledAttributes.getInteger(R$styleable.lbBaseCardView_extraVisibility, 2);
            this.mExtraVisibility = integer;
            if (integer < this.mInfoVisibility) {
                this.mExtraVisibility = this.mInfoVisibility;
            }
            this.mSelectedAnimationDelay = obtainStyledAttributes.getInteger(R$styleable.lbBaseCardView_selectedAnimationDelay, getResources().getInteger(R$integer.lb_card_selected_animation_delay));
            this.mSelectedAnimDuration = obtainStyledAttributes.getInteger(R$styleable.lbBaseCardView_selectedAnimationDuration, getResources().getInteger(R$integer.lb_card_selected_animation_duration));
            this.mActivatedAnimDuration = obtainStyledAttributes.getInteger(R$styleable.lbBaseCardView_activatedAnimationDuration, getResources().getInteger(R$integer.lb_card_activated_animation_duration));
            obtainStyledAttributes.recycle();
            this.mDelaySelectedAnim = true;
            this.mMainViewList = new ArrayList<>();
            this.mInfoViewList = new ArrayList<>();
            this.mExtraViewList = new ArrayList<>();
            this.mInfoOffset = 0.0f;
            this.mInfoVisFraction = getFinalInfoVisFraction();
            this.mInfoAlpha = getFinalInfoAlpha();
        } catch (Throwable th) {
            obtainStyledAttributes.recycle();
            throw th;
        }
    }

    public void setSelectedAnimationDelayed(boolean z) {
        this.mDelaySelectedAnim = z;
    }

    public void setCardType(int i) {
        if (this.mCardType != i) {
            if (i < 0 || i >= 4) {
                Log.e("BaseCardView", "Invalid card type specified: " + i + ". Defaulting to type CARD_TYPE_MAIN_ONLY.");
                this.mCardType = 0;
            } else {
                this.mCardType = i;
            }
            requestLayout();
        }
    }

    public int getCardType() {
        return this.mCardType;
    }

    public void setInfoVisibility(int i) {
        if (this.mInfoVisibility != i) {
            cancelAnimations();
            this.mInfoVisibility = i;
            this.mInfoVisFraction = getFinalInfoVisFraction();
            requestLayout();
            float finalInfoAlpha = getFinalInfoAlpha();
            if (finalInfoAlpha != this.mInfoAlpha) {
                this.mInfoAlpha = finalInfoAlpha;
                for (int i2 = 0; i2 < this.mInfoViewList.size(); i2++) {
                    this.mInfoViewList.get(i2).setAlpha(this.mInfoAlpha);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final float getFinalInfoVisFraction() {
        return (this.mCardType == 2 && this.mInfoVisibility == 2 && !isSelected()) ? 0.0f : 1.0f;
    }

    /* access modifiers changed from: package-private */
    public final float getFinalInfoAlpha() {
        return (this.mCardType == 1 && this.mInfoVisibility == 2 && !isSelected()) ? 0.0f : 1.0f;
    }

    public int getInfoVisibility() {
        return this.mInfoVisibility;
    }

    @Deprecated
    public void setExtraVisibility(int i) {
        if (this.mExtraVisibility != i) {
            this.mExtraVisibility = i;
        }
    }

    @Deprecated
    public int getExtraVisibility() {
        return this.mExtraVisibility;
    }

    public void setActivated(boolean z) {
        if (z != isActivated()) {
            super.setActivated(z);
            applyActiveState();
        }
    }

    public void setSelected(boolean z) {
        if (z != isSelected()) {
            super.setSelected(z);
            applySelectedState(isSelected());
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int i4;
        float f;
        boolean z = false;
        this.mMeasuredWidth = 0;
        this.mMeasuredHeight = 0;
        findChildrenViews();
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        int i5 = 0;
        int i6 = 0;
        for (int i7 = 0; i7 < this.mMainViewList.size(); i7++) {
            View view = this.mMainViewList.get(i7);
            if (view.getVisibility() != 8) {
                measureChild(view, makeMeasureSpec, makeMeasureSpec);
                this.mMeasuredWidth = Math.max(this.mMeasuredWidth, view.getMeasuredWidth());
                i5 += view.getMeasuredHeight();
                i6 = View.combineMeasuredStates(i6, view.getMeasuredState());
            }
        }
        setPivotX((float) (this.mMeasuredWidth / 2));
        setPivotY((float) (i5 / 2));
        int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(this.mMeasuredWidth, 1073741824);
        if (hasInfoRegion()) {
            i4 = 0;
            for (int i8 = 0; i8 < this.mInfoViewList.size(); i8++) {
                View view2 = this.mInfoViewList.get(i8);
                if (view2.getVisibility() != 8) {
                    measureChild(view2, makeMeasureSpec2, makeMeasureSpec);
                    if (this.mCardType != 1) {
                        i4 += view2.getMeasuredHeight();
                    }
                    i6 = View.combineMeasuredStates(i6, view2.getMeasuredState());
                }
            }
            if (hasExtraRegion()) {
                i3 = 0;
                for (int i9 = 0; i9 < this.mExtraViewList.size(); i9++) {
                    View view3 = this.mExtraViewList.get(i9);
                    if (view3.getVisibility() != 8) {
                        measureChild(view3, makeMeasureSpec2, makeMeasureSpec);
                        i3 += view3.getMeasuredHeight();
                        i6 = View.combineMeasuredStates(i6, view3.getMeasuredState());
                    }
                }
            } else {
                i3 = 0;
            }
        } else {
            i4 = 0;
            i3 = 0;
        }
        if (hasInfoRegion() && this.mInfoVisibility == 2) {
            z = true;
        }
        float f2 = (float) i5;
        float f3 = (float) i4;
        if (z) {
            f3 *= this.mInfoVisFraction;
        }
        float f4 = f2 + f3 + ((float) i3);
        if (z) {
            f = 0.0f;
        } else {
            f = this.mInfoOffset;
        }
        this.mMeasuredHeight = (int) (f4 - f);
        setMeasuredDimension(View.resolveSizeAndState(this.mMeasuredWidth + getPaddingLeft() + getPaddingRight(), i, i6), View.resolveSizeAndState(this.mMeasuredHeight + getPaddingTop() + getPaddingBottom(), i2, i6 << 16));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float paddingTop = (float) getPaddingTop();
        for (int i5 = 0; i5 < this.mMainViewList.size(); i5++) {
            View view = this.mMainViewList.get(i5);
            if (view.getVisibility() != 8) {
                view.layout(getPaddingLeft(), (int) paddingTop, this.mMeasuredWidth + getPaddingLeft(), (int) (((float) view.getMeasuredHeight()) + paddingTop));
                paddingTop += (float) view.getMeasuredHeight();
            }
        }
        if (hasInfoRegion()) {
            float f = 0.0f;
            for (int i6 = 0; i6 < this.mInfoViewList.size(); i6++) {
                f += (float) this.mInfoViewList.get(i6).getMeasuredHeight();
            }
            int i7 = this.mCardType;
            if (i7 == 1) {
                paddingTop -= f;
                if (paddingTop < 0.0f) {
                    paddingTop = 0.0f;
                }
            } else if (i7 != 2) {
                paddingTop -= this.mInfoOffset;
            } else if (this.mInfoVisibility == 2) {
                f *= this.mInfoVisFraction;
            }
            for (int i8 = 0; i8 < this.mInfoViewList.size(); i8++) {
                View view2 = this.mInfoViewList.get(i8);
                if (view2.getVisibility() != 8) {
                    int measuredHeight = view2.getMeasuredHeight();
                    if (((float) measuredHeight) > f) {
                        measuredHeight = (int) f;
                    }
                    float f2 = (float) measuredHeight;
                    paddingTop += f2;
                    view2.layout(getPaddingLeft(), (int) paddingTop, this.mMeasuredWidth + getPaddingLeft(), (int) paddingTop);
                    f -= f2;
                    if (f <= 0.0f) {
                        break;
                    }
                }
            }
            if (hasExtraRegion()) {
                for (int i9 = 0; i9 < this.mExtraViewList.size(); i9++) {
                    View view3 = this.mExtraViewList.get(i9);
                    if (view3.getVisibility() != 8) {
                        view3.layout(getPaddingLeft(), (int) paddingTop, this.mMeasuredWidth + getPaddingLeft(), (int) (((float) view3.getMeasuredHeight()) + paddingTop));
                        paddingTop += (float) view3.getMeasuredHeight();
                    }
                }
            }
        }
        onSizeChanged(0, 0, i3 - i, i4 - i2);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mAnimationTrigger);
        cancelAnimations();
    }

    private boolean hasInfoRegion() {
        return this.mCardType != 0;
    }

    private boolean hasExtraRegion() {
        return this.mCardType == 3;
    }

    private boolean isRegionVisible(int i) {
        if (i == 0) {
            return true;
        }
        if (i == 1) {
            return isActivated();
        }
        if (i != 2) {
            return false;
        }
        return isSelected();
    }

    private boolean isCurrentRegionVisible(int i) {
        if (i == 0) {
            return true;
        }
        if (i == 1) {
            return isActivated();
        }
        if (i != 2) {
            return false;
        }
        if (this.mCardType == 2) {
            return this.mInfoVisFraction > 0.0f;
        }
        return isSelected();
    }

    private void findChildrenViews() {
        this.mMainViewList.clear();
        this.mInfoViewList.clear();
        this.mExtraViewList.clear();
        int childCount = getChildCount();
        boolean z = hasInfoRegion() && isCurrentRegionVisible(this.mInfoVisibility);
        boolean z2 = hasExtraRegion() && this.mInfoOffset > 0.0f;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt != null) {
                int i2 = ((LayoutParams) childAt.getLayoutParams()).viewType;
                int i3 = 8;
                if (i2 == 1) {
                    childAt.setAlpha(this.mInfoAlpha);
                    this.mInfoViewList.add(childAt);
                    if (z) {
                        i3 = 0;
                    }
                    childAt.setVisibility(i3);
                } else if (i2 == 2) {
                    this.mExtraViewList.add(childAt);
                    if (z2) {
                        i3 = 0;
                    }
                    childAt.setVisibility(i3);
                } else {
                    this.mMainViewList.add(childAt);
                    childAt.setVisibility(0);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public int[] onCreateDrawableState(int i) {
        int[] onCreateDrawableState = super.onCreateDrawableState(i);
        int length = onCreateDrawableState.length;
        boolean z = false;
        boolean z2 = false;
        for (int i2 = 0; i2 < length; i2++) {
            if (onCreateDrawableState[i2] == 16842919) {
                z = true;
            }
            if (onCreateDrawableState[i2] == 16842910) {
                z2 = true;
            }
        }
        if (z && z2) {
            return View.PRESSED_ENABLED_STATE_SET;
        }
        if (z) {
            return LB_PRESSED_STATE_SET;
        }
        if (z2) {
            return View.ENABLED_STATE_SET;
        }
        return View.EMPTY_STATE_SET;
    }

    private void applyActiveState() {
        int i;
        if (hasInfoRegion() && (i = this.mInfoVisibility) == 1) {
            setInfoViewVisibility(isRegionVisible(i));
        }
    }

    private void setInfoViewVisibility(boolean z) {
        int i = this.mCardType;
        if (i == 3) {
            if (z) {
                for (int i2 = 0; i2 < this.mInfoViewList.size(); i2++) {
                    this.mInfoViewList.get(i2).setVisibility(0);
                }
                return;
            }
            for (int i3 = 0; i3 < this.mInfoViewList.size(); i3++) {
                this.mInfoViewList.get(i3).setVisibility(8);
            }
            for (int i4 = 0; i4 < this.mExtraViewList.size(); i4++) {
                this.mExtraViewList.get(i4).setVisibility(8);
            }
            this.mInfoOffset = 0.0f;
        } else if (i == 2) {
            if (this.mInfoVisibility == 2) {
                animateInfoHeight(z);
                return;
            }
            for (int i5 = 0; i5 < this.mInfoViewList.size(); i5++) {
                this.mInfoViewList.get(i5).setVisibility(z ? 0 : 8);
            }
        } else if (i == 1) {
            animateInfoAlpha(z);
        }
    }

    private void applySelectedState(boolean z) {
        removeCallbacks(this.mAnimationTrigger);
        if (this.mCardType == 3) {
            if (!z) {
                animateInfoOffset(false);
            } else if (!this.mDelaySelectedAnim) {
                post(this.mAnimationTrigger);
                this.mDelaySelectedAnim = true;
            } else {
                postDelayed(this.mAnimationTrigger, (long) this.mSelectedAnimationDelay);
            }
        } else if (this.mInfoVisibility == 2) {
            setInfoViewVisibility(z);
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelAnimations() {
        Animation animation = this.mAnim;
        if (animation != null) {
            animation.cancel();
            this.mAnim = null;
            clearAnimation();
        }
    }

    /* access modifiers changed from: package-private */
    public void animateInfoOffset(boolean z) {
        cancelAnimations();
        int i = 0;
        if (z) {
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mMeasuredWidth, 1073741824);
            int makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(0, 0);
            int i2 = 0;
            for (int i3 = 0; i3 < this.mExtraViewList.size(); i3++) {
                View view = this.mExtraViewList.get(i3);
                view.setVisibility(0);
                view.measure(makeMeasureSpec, makeMeasureSpec2);
                i2 = Math.max(i2, view.getMeasuredHeight());
            }
            i = i2;
        }
        InfoOffsetAnimation infoOffsetAnimation = new InfoOffsetAnimation(this.mInfoOffset, z ? (float) i : 0.0f);
        this.mAnim = infoOffsetAnimation;
        infoOffsetAnimation.setDuration((long) this.mSelectedAnimDuration);
        this.mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        this.mAnim.setAnimationListener(new Animation.AnimationListener() {
            /* class androidx.leanback.widget.BaseCardView.AnonymousClass2 */

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                if (BaseCardView.this.mInfoOffset == 0.0f) {
                    for (int i = 0; i < BaseCardView.this.mExtraViewList.size(); i++) {
                        BaseCardView.this.mExtraViewList.get(i).setVisibility(8);
                    }
                }
            }
        });
        startAnimation(this.mAnim);
    }

    private void animateInfoHeight(boolean z) {
        cancelAnimations();
        if (z) {
            for (int i = 0; i < this.mInfoViewList.size(); i++) {
                this.mInfoViewList.get(i).setVisibility(0);
            }
        }
        float f = z ? 1.0f : 0.0f;
        if (this.mInfoVisFraction != f) {
            InfoHeightAnimation infoHeightAnimation = new InfoHeightAnimation(this.mInfoVisFraction, f);
            this.mAnim = infoHeightAnimation;
            infoHeightAnimation.setDuration((long) this.mSelectedAnimDuration);
            this.mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            this.mAnim.setAnimationListener(new Animation.AnimationListener() {
                /* class androidx.leanback.widget.BaseCardView.AnonymousClass3 */

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    if (BaseCardView.this.mInfoVisFraction == 0.0f) {
                        for (int i = 0; i < BaseCardView.this.mInfoViewList.size(); i++) {
                            BaseCardView.this.mInfoViewList.get(i).setVisibility(8);
                        }
                    }
                }
            });
            startAnimation(this.mAnim);
        }
    }

    private void animateInfoAlpha(boolean z) {
        cancelAnimations();
        if (z) {
            for (int i = 0; i < this.mInfoViewList.size(); i++) {
                this.mInfoViewList.get(i).setVisibility(0);
            }
        }
        float f = 1.0f;
        if ((z ? 1.0f : 0.0f) != this.mInfoAlpha) {
            float f2 = this.mInfoAlpha;
            if (!z) {
                f = 0.0f;
            }
            InfoAlphaAnimation infoAlphaAnimation = new InfoAlphaAnimation(f2, f);
            this.mAnim = infoAlphaAnimation;
            infoAlphaAnimation.setDuration((long) this.mActivatedAnimDuration);
            this.mAnim.setInterpolator(new DecelerateInterpolator());
            this.mAnim.setAnimationListener(new Animation.AnimationListener() {
                /* class androidx.leanback.widget.BaseCardView.AnonymousClass4 */

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    if (((double) BaseCardView.this.mInfoAlpha) == 0.0d) {
                        for (int i = 0; i < BaseCardView.this.mInfoViewList.size(); i++) {
                            BaseCardView.this.mInfoViewList.get(i).setVisibility(8);
                        }
                    }
                }
            });
            startAnimation(this.mAnim);
        }
    }

    @Override // android.widget.FrameLayout, android.widget.FrameLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.widget.FrameLayout
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        @ViewDebug.ExportedProperty(category = "layout", mapping = {@ViewDebug.IntToString(from = 0, to = "MAIN"), @ViewDebug.IntToString(from = 1, to = "INFO"), @ViewDebug.IntToString(from = 2, to = "EXTRA")})
        public int viewType = 0;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.lbBaseCardView_Layout);
            this.viewType = obtainStyledAttributes.getInt(R$styleable.lbBaseCardView_Layout_layout_viewType, 0);
            obtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((ViewGroup.MarginLayoutParams) layoutParams);
            this.viewType = layoutParams.viewType;
        }
    }

    class AnimationBase extends Animation {
        AnimationBase() {
        }

        /* access modifiers changed from: package-private */
        public final void mockStart() {
            getTransformation(0, null);
        }

        /* access modifiers changed from: package-private */
        public final void mockEnd() {
            applyTransformation(1.0f, null);
            BaseCardView.this.cancelAnimations();
        }
    }

    /* access modifiers changed from: package-private */
    public final class InfoOffsetAnimation extends AnimationBase {
        private float mDelta;
        private float mStartValue;

        public InfoOffsetAnimation(float f, float f2) {
            super();
            this.mStartValue = f;
            this.mDelta = f2 - f;
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float f, Transformation transformation) {
            BaseCardView baseCardView = BaseCardView.this;
            baseCardView.mInfoOffset = this.mStartValue + (f * this.mDelta);
            baseCardView.requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public final class InfoHeightAnimation extends AnimationBase {
        private float mDelta;
        private float mStartValue;

        public InfoHeightAnimation(float f, float f2) {
            super();
            this.mStartValue = f;
            this.mDelta = f2 - f;
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float f, Transformation transformation) {
            BaseCardView baseCardView = BaseCardView.this;
            baseCardView.mInfoVisFraction = this.mStartValue + (f * this.mDelta);
            baseCardView.requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public final class InfoAlphaAnimation extends AnimationBase {
        private float mDelta;
        private float mStartValue;

        public InfoAlphaAnimation(float f, float f2) {
            super();
            this.mStartValue = f;
            this.mDelta = f2 - f;
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float f, Transformation transformation) {
            BaseCardView.this.mInfoAlpha = this.mStartValue + (f * this.mDelta);
            for (int i = 0; i < BaseCardView.this.mInfoViewList.size(); i++) {
                BaseCardView.this.mInfoViewList.get(i).setAlpha(BaseCardView.this.mInfoAlpha);
            }
        }
    }

    public String toString() {
        return super.toString();
    }
}
