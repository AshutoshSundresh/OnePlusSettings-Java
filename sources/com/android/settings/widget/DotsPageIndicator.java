package com.android.settings.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.R$styleable;
import com.android.settings.wifi.UseOpenWifiPreferenceController;
import java.util.Arrays;

public class DotsPageIndicator extends View implements ViewPager.OnPageChangeListener {
    private long animDuration;
    private long animHalfDuration;
    private boolean attachedState;
    private final Path combinedUnselectedPath;
    float controlX1;
    float controlX2;
    float controlY1;
    float controlY2;
    private int currentPage;
    private float dotBottomY;
    private float[] dotCenterX;
    private float dotCenterY;
    private int dotDiameter;
    private float dotRadius;
    private float[] dotRevealFractions;
    private float dotTopY;
    float endX1;
    float endX2;
    float endY1;
    float endY2;
    private int gap;
    private float halfDotRadius;
    private final Interpolator interpolator;
    private AnimatorSet joiningAnimationSet;
    private ValueAnimator[] joiningAnimations;
    private float[] joiningFractions;
    private ValueAnimator moveAnimation;
    private ViewPager.OnPageChangeListener pageChangeListener;
    private int pageCount;
    private final RectF rectF;
    private PendingRetreatAnimator retreatAnimation;
    private float retreatingJoinX1;
    private float retreatingJoinX2;
    private PendingRevealAnimator[] revealAnimations;
    private int selectedColour;
    private boolean selectedDotInPosition;
    private float selectedDotX;
    private final Paint selectedPaint;
    private int unselectedColour;
    private final Path unselectedDotLeftPath;
    private final Path unselectedDotPath;
    private final Path unselectedDotRightPath;
    private final Paint unselectedPaint;
    private ViewPager viewPager;

    public DotsPageIndicator(Context context) {
        this(context, null, 0);
    }

    public DotsPageIndicator(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DotsPageIndicator(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        int i2 = (int) context.getResources().getDisplayMetrics().scaledDensity;
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.DotsPageIndicator, i, 0);
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R$styleable.DotsPageIndicator_dotDiameter, i2 * 8);
        this.dotDiameter = dimensionPixelSize;
        float f = (float) (dimensionPixelSize / 2);
        this.dotRadius = f;
        this.halfDotRadius = f / 2.0f;
        this.gap = obtainStyledAttributes.getDimensionPixelSize(R$styleable.DotsPageIndicator_dotGap, i2 * 12);
        long integer = (long) obtainStyledAttributes.getInteger(R$styleable.DotsPageIndicator_animationDuration, UseOpenWifiPreferenceController.REQUEST_CODE_OPEN_WIFI_AUTOMATICALLY);
        this.animDuration = integer;
        this.animHalfDuration = integer / 2;
        this.unselectedColour = obtainStyledAttributes.getColor(R$styleable.DotsPageIndicator_pageIndicatorColor, -2130706433);
        this.selectedColour = obtainStyledAttributes.getColor(R$styleable.DotsPageIndicator_currentPageIndicatorColor, -1);
        obtainStyledAttributes.recycle();
        Paint paint = new Paint(1);
        this.unselectedPaint = paint;
        paint.setColor(this.unselectedColour);
        Paint paint2 = new Paint(1);
        this.selectedPaint = paint2;
        paint2.setColor(this.selectedColour);
        if (Build.VERSION.SDK_INT >= 21) {
            this.interpolator = AnimationUtils.loadInterpolator(context, 17563661);
        } else {
            this.interpolator = AnimationUtils.loadInterpolator(context, 17432580);
        }
        this.combinedUnselectedPath = new Path();
        this.unselectedDotPath = new Path();
        this.unselectedDotLeftPath = new Path();
        this.unselectedDotRightPath = new Path();
        this.rectF = new RectF();
        addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            /* class com.android.settings.widget.DotsPageIndicator.AnonymousClass1 */

            public void onViewAttachedToWindow(View view) {
                DotsPageIndicator.this.attachedState = true;
            }

            public void onViewDetachedFromWindow(View view) {
                DotsPageIndicator.this.attachedState = false;
            }
        });
    }

    public void setViewPager(ViewPager viewPager2) {
        this.viewPager = viewPager2;
        viewPager2.setOnPageChangeListener(this);
        setPageCount(viewPager2.getAdapter().getCount());
        viewPager2.getAdapter().registerDataSetObserver(new DataSetObserver() {
            /* class com.android.settings.widget.DotsPageIndicator.AnonymousClass2 */

            public void onChanged() {
                DotsPageIndicator dotsPageIndicator = DotsPageIndicator.this;
                dotsPageIndicator.setPageCount(dotsPageIndicator.viewPager.getAdapter().getCount());
            }
        });
        setCurrentPageImmediate();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.pageChangeListener = onPageChangeListener;
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageScrolled(int i, float f, int i2) {
        ViewPager.OnPageChangeListener onPageChangeListener = this.pageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(i, f, i2);
        }
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageSelected(int i) {
        if (this.attachedState) {
            setSelectedPage(i);
        } else {
            setCurrentPageImmediate();
        }
        ViewPager.OnPageChangeListener onPageChangeListener = this.pageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(i);
        }
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageScrollStateChanged(int i) {
        ViewPager.OnPageChangeListener onPageChangeListener = this.pageChangeListener;
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(i);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setPageCount(int i) {
        this.pageCount = i;
        calculateDotPositions();
        resetState();
    }

    private void calculateDotPositions() {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        float width = ((float) (paddingLeft + ((((getWidth() - getPaddingRight()) - paddingLeft) - getRequiredWidth()) / 2))) + this.dotRadius;
        this.dotCenterX = new float[this.pageCount];
        for (int i = 0; i < this.pageCount; i++) {
            this.dotCenterX[i] = ((float) ((this.dotDiameter + this.gap) * i)) + width;
        }
        float f = (float) paddingTop;
        this.dotTopY = f;
        this.dotCenterY = f + this.dotRadius;
        this.dotBottomY = (float) (paddingTop + this.dotDiameter);
        setCurrentPageImmediate();
    }

    private void setCurrentPageImmediate() {
        ViewPager viewPager2 = this.viewPager;
        if (viewPager2 != null) {
            this.currentPage = viewPager2.getCurrentItem();
        } else {
            this.currentPage = 0;
        }
        if (this.pageCount > 0) {
            this.selectedDotX = this.dotCenterX[this.currentPage];
        }
    }

    private void resetState() {
        int i = this.pageCount;
        if (i > 0) {
            float[] fArr = new float[(i - 1)];
            this.joiningFractions = fArr;
            Arrays.fill(fArr, 0.0f);
            float[] fArr2 = new float[this.pageCount];
            this.dotRevealFractions = fArr2;
            Arrays.fill(fArr2, 0.0f);
            this.retreatingJoinX1 = -1.0f;
            this.retreatingJoinX2 = -1.0f;
            this.selectedDotInPosition = true;
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int desiredHeight = getDesiredHeight();
        int mode = View.MeasureSpec.getMode(i2);
        if (mode == Integer.MIN_VALUE) {
            desiredHeight = Math.min(desiredHeight, View.MeasureSpec.getSize(i2));
        } else if (mode == 1073741824) {
            desiredHeight = View.MeasureSpec.getSize(i2);
        }
        int desiredWidth = getDesiredWidth();
        int mode2 = View.MeasureSpec.getMode(i);
        if (mode2 == Integer.MIN_VALUE) {
            desiredWidth = Math.min(desiredWidth, View.MeasureSpec.getSize(i));
        } else if (mode2 == 1073741824) {
            desiredWidth = View.MeasureSpec.getSize(i);
        }
        setMeasuredDimension(desiredWidth, desiredHeight);
        calculateDotPositions();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        setMeasuredDimension(i, i2);
        calculateDotPositions();
    }

    public void clearAnimation() {
        super.clearAnimation();
        if (Build.VERSION.SDK_INT >= 16) {
            cancelRunningAnimations();
        }
    }

    private int getDesiredHeight() {
        return getPaddingTop() + this.dotDiameter + getPaddingBottom();
    }

    private int getRequiredWidth() {
        int i = this.pageCount;
        return (this.dotDiameter * i) + ((i - 1) * this.gap);
    }

    private int getDesiredWidth() {
        return getPaddingLeft() + getRequiredWidth() + getPaddingRight();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.viewPager != null && this.pageCount != 0) {
            drawUnselected(canvas);
            drawSelected(canvas);
        }
    }

    private void drawUnselected(Canvas canvas) {
        int i = Build.VERSION.SDK_INT;
        this.combinedUnselectedPath.rewind();
        int i2 = 0;
        while (true) {
            int i3 = this.pageCount;
            if (i2 >= i3) {
                break;
            }
            int i4 = i2 == i3 + -1 ? i2 : i2 + 1;
            if (i >= 21) {
                float[] fArr = this.dotCenterX;
                this.combinedUnselectedPath.op(getUnselectedPath(i2, fArr[i2], fArr[i4], i2 == this.pageCount + -1 ? -1.0f : this.joiningFractions[i2], this.dotRevealFractions[i2]), Path.Op.UNION);
            } else {
                canvas.drawCircle(this.dotCenterX[i2], this.dotCenterY, this.dotRadius, this.unselectedPaint);
            }
            i2++;
        }
        if (this.retreatingJoinX1 != -1.0f && i >= 21) {
            this.combinedUnselectedPath.op(getRetreatingJoinPath(), Path.Op.UNION);
        }
        canvas.drawPath(this.combinedUnselectedPath, this.unselectedPaint);
    }

    private Path getUnselectedPath(int i, float f, float f2, float f3, float f4) {
        int i2 = Build.VERSION.SDK_INT;
        this.unselectedDotPath.rewind();
        int i3 = (f3 > 0.0f ? 1 : (f3 == 0.0f ? 0 : -1));
        if ((i3 == 0 || f3 == -1.0f) && f4 == 0.0f && !(i == this.currentPage && this.selectedDotInPosition)) {
            this.unselectedDotPath.addCircle(this.dotCenterX[i], this.dotCenterY, this.dotRadius, Path.Direction.CW);
        }
        if (i3 > 0 && f3 < 0.5f && this.retreatingJoinX1 == -1.0f) {
            this.unselectedDotLeftPath.rewind();
            this.unselectedDotLeftPath.moveTo(f, this.dotBottomY);
            RectF rectF2 = this.rectF;
            float f5 = this.dotRadius;
            rectF2.set(f - f5, this.dotTopY, f5 + f, this.dotBottomY);
            this.unselectedDotLeftPath.arcTo(this.rectF, 90.0f, 180.0f, true);
            float f6 = this.dotRadius + f + (((float) this.gap) * f3);
            this.endX1 = f6;
            float f7 = this.dotCenterY;
            this.endY1 = f7;
            float f8 = this.halfDotRadius;
            float f9 = f + f8;
            this.controlX1 = f9;
            float f10 = this.dotTopY;
            this.controlY1 = f10;
            this.controlX2 = f6;
            float f11 = f7 - f8;
            this.controlY2 = f11;
            this.unselectedDotLeftPath.cubicTo(f9, f10, f6, f11, f6, f7);
            this.endX2 = f;
            float f12 = this.dotBottomY;
            this.endY2 = f12;
            float f13 = this.endX1;
            this.controlX1 = f13;
            float f14 = this.endY1;
            float f15 = this.halfDotRadius;
            float f16 = f14 + f15;
            this.controlY1 = f16;
            float f17 = f + f15;
            this.controlX2 = f17;
            this.controlY2 = f12;
            this.unselectedDotLeftPath.cubicTo(f13, f16, f17, f12, f, f12);
            if (i2 >= 21) {
                this.unselectedDotPath.op(this.unselectedDotLeftPath, Path.Op.UNION);
            }
            this.unselectedDotRightPath.rewind();
            this.unselectedDotRightPath.moveTo(f2, this.dotBottomY);
            RectF rectF3 = this.rectF;
            float f18 = this.dotRadius;
            rectF3.set(f2 - f18, this.dotTopY, f18 + f2, this.dotBottomY);
            this.unselectedDotRightPath.arcTo(this.rectF, 90.0f, -180.0f, true);
            float f19 = (f2 - this.dotRadius) - (((float) this.gap) * f3);
            this.endX1 = f19;
            float f20 = this.dotCenterY;
            this.endY1 = f20;
            float f21 = this.halfDotRadius;
            float f22 = f2 - f21;
            this.controlX1 = f22;
            float f23 = this.dotTopY;
            this.controlY1 = f23;
            this.controlX2 = f19;
            float f24 = f20 - f21;
            this.controlY2 = f24;
            this.unselectedDotRightPath.cubicTo(f22, f23, f19, f24, f19, f20);
            this.endX2 = f2;
            float f25 = this.dotBottomY;
            this.endY2 = f25;
            float f26 = this.endX1;
            this.controlX1 = f26;
            float f27 = this.endY1;
            float f28 = this.halfDotRadius;
            float f29 = f27 + f28;
            this.controlY1 = f29;
            float f30 = f2 - f28;
            this.controlX2 = f30;
            this.controlY2 = f25;
            this.unselectedDotRightPath.cubicTo(f26, f29, f30, f25, f2, f25);
            if (i2 >= 21) {
                this.unselectedDotPath.op(this.unselectedDotRightPath, Path.Op.UNION);
            }
        }
        if (f3 > 0.5f && f3 < 1.0f && this.retreatingJoinX1 == -1.0f) {
            this.unselectedDotPath.moveTo(f, this.dotBottomY);
            RectF rectF4 = this.rectF;
            float f31 = this.dotRadius;
            rectF4.set(f - f31, this.dotTopY, f31 + f, this.dotBottomY);
            this.unselectedDotPath.arcTo(this.rectF, 90.0f, 180.0f, true);
            float f32 = this.dotRadius;
            float f33 = f + f32 + ((float) (this.gap / 2));
            this.endX1 = f33;
            float f34 = this.dotCenterY - (f3 * f32);
            this.endY1 = f34;
            float f35 = f33 - (f3 * f32);
            this.controlX1 = f35;
            float f36 = this.dotTopY;
            this.controlY1 = f36;
            float f37 = 1.0f - f3;
            float f38 = f33 - (f32 * f37);
            this.controlX2 = f38;
            this.controlY2 = f34;
            this.unselectedDotPath.cubicTo(f35, f36, f38, f34, f33, f34);
            this.endX2 = f2;
            float f39 = this.dotTopY;
            this.endY2 = f39;
            float f40 = this.endX1;
            float f41 = this.dotRadius;
            float f42 = (f37 * f41) + f40;
            this.controlX1 = f42;
            float f43 = this.endY1;
            this.controlY1 = f43;
            float f44 = f40 + (f41 * f3);
            this.controlX2 = f44;
            this.controlY2 = f39;
            this.unselectedDotPath.cubicTo(f42, f43, f44, f39, f2, f39);
            RectF rectF5 = this.rectF;
            float f45 = this.dotRadius;
            rectF5.set(f2 - f45, this.dotTopY, f45 + f2, this.dotBottomY);
            this.unselectedDotPath.arcTo(this.rectF, 270.0f, 180.0f, true);
            float f46 = this.dotCenterY;
            float f47 = this.dotRadius;
            float f48 = f46 + (f3 * f47);
            this.endY1 = f48;
            float f49 = this.endX1;
            float f50 = f49 + (f3 * f47);
            this.controlX1 = f50;
            float f51 = this.dotBottomY;
            this.controlY1 = f51;
            float f52 = (f47 * f37) + f49;
            this.controlX2 = f52;
            this.controlY2 = f48;
            this.unselectedDotPath.cubicTo(f50, f51, f52, f48, f49, f48);
            this.endX2 = f;
            float f53 = this.dotBottomY;
            this.endY2 = f53;
            float f54 = this.endX1;
            float f55 = this.dotRadius;
            float f56 = f54 - (f37 * f55);
            this.controlX1 = f56;
            float f57 = this.endY1;
            this.controlY1 = f57;
            float f58 = f54 - (f55 * f3);
            this.controlX2 = f58;
            this.controlY2 = f53;
            this.unselectedDotPath.cubicTo(f56, f57, f58, f53, f, f53);
        }
        if (f3 == 1.0f && this.retreatingJoinX1 == -1.0f) {
            RectF rectF6 = this.rectF;
            float f59 = this.dotRadius;
            rectF6.set(f - f59, this.dotTopY, f59 + f2, this.dotBottomY);
            Path path = this.unselectedDotPath;
            RectF rectF7 = this.rectF;
            float f60 = this.dotRadius;
            path.addRoundRect(rectF7, f60, f60, Path.Direction.CW);
        }
        if (f4 > 1.0E-5f) {
            this.unselectedDotPath.addCircle(f, this.dotCenterY, this.dotRadius * f4, Path.Direction.CW);
        }
        return this.unselectedDotPath;
    }

    private Path getRetreatingJoinPath() {
        this.unselectedDotPath.rewind();
        this.rectF.set(this.retreatingJoinX1, this.dotTopY, this.retreatingJoinX2, this.dotBottomY);
        Path path = this.unselectedDotPath;
        RectF rectF2 = this.rectF;
        float f = this.dotRadius;
        path.addRoundRect(rectF2, f, f, Path.Direction.CW);
        return this.unselectedDotPath;
    }

    private void drawSelected(Canvas canvas) {
        canvas.drawCircle(this.selectedDotX, this.dotCenterY, this.dotRadius, this.selectedPaint);
    }

    private void setSelectedPage(int i) {
        int i2 = this.currentPage;
        if (!(i == i2 || this.pageCount == 0)) {
            this.currentPage = i;
            if (Build.VERSION.SDK_INT >= 16) {
                cancelRunningAnimations();
                int abs = Math.abs(i - i2);
                this.moveAnimation = createMoveSelectedAnimator(this.dotCenterX[i], i2, i, abs);
                this.joiningAnimations = new ValueAnimator[abs];
                for (int i3 = 0; i3 < abs; i3++) {
                    this.joiningAnimations[i3] = createJoiningAnimator(i > i2 ? i2 + i3 : (i2 - 1) - i3, ((long) i3) * (this.animDuration / 8));
                }
                this.moveAnimation.start();
                startJoiningAnimations();
                return;
            }
            setCurrentPageImmediate();
            invalidate();
        }
    }

    private ValueAnimator createMoveSelectedAnimator(float f, int i, int i2, int i3) {
        StartPredicate startPredicate;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.selectedDotX, f);
        if (i2 > i) {
            startPredicate = new RightwardStartPredicate(this, f - ((f - this.selectedDotX) * 0.25f));
        } else {
            startPredicate = new LeftwardStartPredicate(this, f + ((this.selectedDotX - f) * 0.25f));
        }
        this.retreatAnimation = new PendingRetreatAnimator(i, i2, i3, startPredicate);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            /* class com.android.settings.widget.DotsPageIndicator.AnonymousClass3 */

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                DotsPageIndicator.this.selectedDotX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                DotsPageIndicator.this.retreatAnimation.startIfNecessary(DotsPageIndicator.this.selectedDotX);
                DotsPageIndicator.this.postInvalidateOnAnimation();
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            /* class com.android.settings.widget.DotsPageIndicator.AnonymousClass4 */

            public void onAnimationStart(Animator animator) {
                DotsPageIndicator.this.selectedDotInPosition = false;
            }

            public void onAnimationEnd(Animator animator) {
                DotsPageIndicator.this.selectedDotInPosition = true;
            }
        });
        ofFloat.setStartDelay(this.selectedDotInPosition ? this.animDuration / 4 : 0);
        ofFloat.setDuration((this.animDuration * 3) / 4);
        ofFloat.setInterpolator(this.interpolator);
        return ofFloat;
    }

    private ValueAnimator createJoiningAnimator(final int i, long j) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            /* class com.android.settings.widget.DotsPageIndicator.AnonymousClass5 */

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                DotsPageIndicator.this.setJoiningFraction(i, valueAnimator.getAnimatedFraction());
            }
        });
        ofFloat.setDuration(this.animHalfDuration);
        ofFloat.setStartDelay(j);
        ofFloat.setInterpolator(this.interpolator);
        return ofFloat;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setJoiningFraction(int i, float f) {
        this.joiningFractions[i] = f;
        postInvalidateOnAnimation();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void clearJoiningFractions() {
        Arrays.fill(this.joiningFractions, 0.0f);
        postInvalidateOnAnimation();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setDotRevealFraction(int i, float f) {
        this.dotRevealFractions[i] = f;
        postInvalidateOnAnimation();
    }

    private void cancelRunningAnimations() {
        cancelMoveAnimation();
        cancelJoiningAnimations();
        cancelRetreatAnimation();
        cancelRevealAnimations();
        resetState();
    }

    private void cancelMoveAnimation() {
        ValueAnimator valueAnimator = this.moveAnimation;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.moveAnimation.cancel();
        }
    }

    private void startJoiningAnimations() {
        AnimatorSet animatorSet = new AnimatorSet();
        this.joiningAnimationSet = animatorSet;
        animatorSet.playTogether(this.joiningAnimations);
        this.joiningAnimationSet.start();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void cancelJoiningAnimations() {
        AnimatorSet animatorSet = this.joiningAnimationSet;
        if (animatorSet != null && animatorSet.isRunning()) {
            this.joiningAnimationSet.cancel();
        }
    }

    private void cancelRetreatAnimation() {
        PendingRetreatAnimator pendingRetreatAnimator = this.retreatAnimation;
        if (pendingRetreatAnimator != null && pendingRetreatAnimator.isRunning()) {
            this.retreatAnimation.cancel();
        }
    }

    private void cancelRevealAnimations() {
        PendingRevealAnimator[] pendingRevealAnimatorArr = this.revealAnimations;
        if (pendingRevealAnimatorArr != null) {
            for (PendingRevealAnimator pendingRevealAnimator : pendingRevealAnimatorArr) {
                pendingRevealAnimator.cancel();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int getUnselectedColour() {
        return this.unselectedColour;
    }

    /* access modifiers changed from: package-private */
    public int getSelectedColour() {
        return this.selectedColour;
    }

    /* access modifiers changed from: package-private */
    public float getDotCenterY() {
        return this.dotCenterY;
    }

    /* access modifiers changed from: package-private */
    public float getSelectedDotX() {
        return this.selectedDotX;
    }

    /* access modifiers changed from: package-private */
    public int getCurrentPage() {
        return this.currentPage;
    }

    public abstract class PendingStartAnimator extends ValueAnimator {
        protected boolean hasStarted = false;
        protected StartPredicate predicate;

        public PendingStartAnimator(DotsPageIndicator dotsPageIndicator, StartPredicate startPredicate) {
            this.predicate = startPredicate;
        }

        public void startIfNecessary(float f) {
            if (!this.hasStarted && this.predicate.shouldStart(f)) {
                start();
                this.hasStarted = true;
            }
        }
    }

    public class PendingRetreatAnimator extends PendingStartAnimator {
        public PendingRetreatAnimator(int i, int i2, int i3, StartPredicate startPredicate) {
            super(DotsPageIndicator.this, startPredicate);
            float f;
            float f2;
            float f3;
            float f4;
            float f5;
            float f6;
            float f7;
            float f8;
            setDuration(DotsPageIndicator.this.animHalfDuration);
            setInterpolator(DotsPageIndicator.this.interpolator);
            if (i2 > i) {
                f = Math.min(DotsPageIndicator.this.dotCenterX[i], DotsPageIndicator.this.selectedDotX);
                f2 = DotsPageIndicator.this.dotRadius;
            } else {
                f = DotsPageIndicator.this.dotCenterX[i2];
                f2 = DotsPageIndicator.this.dotRadius;
            }
            float f9 = f - f2;
            if (i2 > i) {
                f3 = DotsPageIndicator.this.dotCenterX[i2];
                f4 = DotsPageIndicator.this.dotRadius;
            } else {
                f3 = DotsPageIndicator.this.dotCenterX[i2];
                f4 = DotsPageIndicator.this.dotRadius;
            }
            float f10 = f3 - f4;
            if (i2 > i) {
                f6 = DotsPageIndicator.this.dotCenterX[i2];
                f5 = DotsPageIndicator.this.dotRadius;
            } else {
                f6 = Math.max(DotsPageIndicator.this.dotCenterX[i], DotsPageIndicator.this.selectedDotX);
                f5 = DotsPageIndicator.this.dotRadius;
            }
            float f11 = f6 + f5;
            if (i2 > i) {
                f7 = DotsPageIndicator.this.dotCenterX[i2];
                f8 = DotsPageIndicator.this.dotRadius;
            } else {
                f7 = DotsPageIndicator.this.dotCenterX[i2];
                f8 = DotsPageIndicator.this.dotRadius;
            }
            float f12 = f7 + f8;
            DotsPageIndicator.this.revealAnimations = new PendingRevealAnimator[i3];
            int[] iArr = new int[i3];
            int i4 = 0;
            if (f9 != f10) {
                setFloatValues(f9, f10);
                while (i4 < i3) {
                    int i5 = i + i4;
                    DotsPageIndicator.this.revealAnimations[i4] = new PendingRevealAnimator(i5, new RightwardStartPredicate(DotsPageIndicator.this, DotsPageIndicator.this.dotCenterX[i5]));
                    iArr[i4] = i5;
                    i4++;
                }
                addUpdateListener(new ValueAnimator.AnimatorUpdateListener(DotsPageIndicator.this) {
                    /* class com.android.settings.widget.DotsPageIndicator.PendingRetreatAnimator.AnonymousClass1 */

                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        DotsPageIndicator.this.retreatingJoinX1 = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                        DotsPageIndicator.this.postInvalidateOnAnimation();
                        for (PendingRevealAnimator pendingRevealAnimator : DotsPageIndicator.this.revealAnimations) {
                            pendingRevealAnimator.startIfNecessary(DotsPageIndicator.this.retreatingJoinX1);
                        }
                    }
                });
            } else {
                setFloatValues(f11, f12);
                while (i4 < i3) {
                    int i6 = i - i4;
                    DotsPageIndicator.this.revealAnimations[i4] = new PendingRevealAnimator(i6, new LeftwardStartPredicate(DotsPageIndicator.this, DotsPageIndicator.this.dotCenterX[i6]));
                    iArr[i4] = i6;
                    i4++;
                }
                addUpdateListener(new ValueAnimator.AnimatorUpdateListener(DotsPageIndicator.this) {
                    /* class com.android.settings.widget.DotsPageIndicator.PendingRetreatAnimator.AnonymousClass2 */

                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        DotsPageIndicator.this.retreatingJoinX2 = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                        DotsPageIndicator.this.postInvalidateOnAnimation();
                        for (PendingRevealAnimator pendingRevealAnimator : DotsPageIndicator.this.revealAnimations) {
                            pendingRevealAnimator.startIfNecessary(DotsPageIndicator.this.retreatingJoinX2);
                        }
                    }
                });
            }
            addListener(new AnimatorListenerAdapter(DotsPageIndicator.this, iArr, f9, f11) {
                /* class com.android.settings.widget.DotsPageIndicator.PendingRetreatAnimator.AnonymousClass3 */
                final /* synthetic */ int[] val$dotsToHide;
                final /* synthetic */ float val$initialX1;
                final /* synthetic */ float val$initialX2;

                {
                    this.val$dotsToHide = r3;
                    this.val$initialX1 = r4;
                    this.val$initialX2 = r5;
                }

                public void onAnimationStart(Animator animator) {
                    DotsPageIndicator.this.cancelJoiningAnimations();
                    DotsPageIndicator.this.clearJoiningFractions();
                    for (int i : this.val$dotsToHide) {
                        DotsPageIndicator.this.setDotRevealFraction(i, 1.0E-5f);
                    }
                    DotsPageIndicator.this.retreatingJoinX1 = this.val$initialX1;
                    DotsPageIndicator.this.retreatingJoinX2 = this.val$initialX2;
                    DotsPageIndicator.this.postInvalidateOnAnimation();
                }

                public void onAnimationEnd(Animator animator) {
                    DotsPageIndicator.this.retreatingJoinX1 = -1.0f;
                    DotsPageIndicator.this.retreatingJoinX2 = -1.0f;
                    DotsPageIndicator.this.postInvalidateOnAnimation();
                }
            });
        }
    }

    public class PendingRevealAnimator extends PendingStartAnimator {
        private final int dot;

        public PendingRevealAnimator(int i, StartPredicate startPredicate) {
            super(DotsPageIndicator.this, startPredicate);
            this.dot = i;
            setFloatValues(1.0E-5f, 1.0f);
            setDuration(DotsPageIndicator.this.animHalfDuration);
            setInterpolator(DotsPageIndicator.this.interpolator);
            addUpdateListener(new ValueAnimator.AnimatorUpdateListener(DotsPageIndicator.this) {
                /* class com.android.settings.widget.DotsPageIndicator.PendingRevealAnimator.AnonymousClass1 */

                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PendingRevealAnimator pendingRevealAnimator = PendingRevealAnimator.this;
                    DotsPageIndicator.this.setDotRevealFraction(pendingRevealAnimator.dot, ((Float) valueAnimator.getAnimatedValue()).floatValue());
                }
            });
            addListener(new AnimatorListenerAdapter(DotsPageIndicator.this) {
                /* class com.android.settings.widget.DotsPageIndicator.PendingRevealAnimator.AnonymousClass2 */

                public void onAnimationEnd(Animator animator) {
                    PendingRevealAnimator pendingRevealAnimator = PendingRevealAnimator.this;
                    DotsPageIndicator.this.setDotRevealFraction(pendingRevealAnimator.dot, 0.0f);
                    DotsPageIndicator.this.postInvalidateOnAnimation();
                }
            });
        }
    }

    public abstract class StartPredicate {
        protected float thresholdValue;

        /* access modifiers changed from: package-private */
        public abstract boolean shouldStart(float f);

        public StartPredicate(DotsPageIndicator dotsPageIndicator, float f) {
            this.thresholdValue = f;
        }
    }

    public class RightwardStartPredicate extends StartPredicate {
        public RightwardStartPredicate(DotsPageIndicator dotsPageIndicator, float f) {
            super(dotsPageIndicator, f);
        }

        /* access modifiers changed from: package-private */
        @Override // com.android.settings.widget.DotsPageIndicator.StartPredicate
        public boolean shouldStart(float f) {
            return f > this.thresholdValue;
        }
    }

    public class LeftwardStartPredicate extends StartPredicate {
        public LeftwardStartPredicate(DotsPageIndicator dotsPageIndicator, float f) {
            super(dotsPageIndicator, f);
        }

        /* access modifiers changed from: package-private */
        @Override // com.android.settings.widget.DotsPageIndicator.StartPredicate
        public boolean shouldStart(float f) {
            return f < this.thresholdValue;
        }
    }
}
