package com.oneplus.settings.opfinger;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.android.settings.R$styleable;
import com.oneplus.settings.opfinger.SvgHelper;
import java.util.ArrayList;
import java.util.List;

public class SvgView extends View {
    private int mDuration;
    private float mFadeFactor;
    private boolean mHaveMoved;
    private Thread mLoader;
    private float mOffsetY;
    private final Paint mPaint;
    private float mParallax;
    private List<SvgHelper.SvgPath> mPaths;
    private float mPhase;
    private final SvgHelper mSvg;
    private ObjectAnimator mSvgAnimator;
    private final Object mSvgLock;
    private ObjectAnimator mSvgResetAnimator;
    private int mSvgResource;

    public SvgView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SvgView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Paint paint = new Paint(1);
        this.mPaint = paint;
        this.mSvg = new SvgHelper(paint);
        this.mSvgLock = new Object();
        this.mPaths = new ArrayList(0);
        this.mParallax = 1.0f;
        this.mHaveMoved = false;
        this.mPaint.setStyle(Paint.Style.STROKE);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.SvgView, i, 0);
        if (obtainStyledAttributes != null) {
            try {
                this.mPaint.setStrokeWidth((float) (getResources().getDisplayMetrics().densityDpi / 50));
                this.mPaint.setColor(obtainStyledAttributes.getColor(R$styleable.SvgView_lineColor, -16777216));
                this.mPhase = obtainStyledAttributes.getFloat(R$styleable.SvgView_phase, 1.0f);
                this.mDuration = obtainStyledAttributes.getInt(R$styleable.SvgView_duration, 4000);
                this.mFadeFactor = obtainStyledAttributes.getFloat(R$styleable.SvgView_fadeFactor, 10.0f);
            } catch (Throwable th) {
                if (obtainStyledAttributes != null) {
                    obtainStyledAttributes.recycle();
                }
                throw th;
            }
        }
        if (obtainStyledAttributes != null) {
            obtainStyledAttributes.recycle();
        }
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        new LinearGradient(0.0f, 0.0f, 100.0f, 100.0f, new int[]{-65536, -16711936, -16776961, -256}, (float[]) null, Shader.TileMode.REPEAT);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updatePathsPhaseLocked() {
        int size = this.mPaths.size();
        for (int i = 0; i < size; i++) {
            SvgHelper.SvgPath svgPath = this.mPaths.get(i);
            svgPath.renderPath.reset();
            svgPath.measure.getSegment(0.0f, svgPath.length * this.mPhase, svgPath.renderPath, true);
            svgPath.renderPath.rLineTo(0.0f, 0.0f);
        }
    }

    public float getParallax() {
        return this.mParallax;
    }

    public void setParallax(float f) {
        this.mParallax = f;
        invalidate();
    }

    public float getPhase() {
        return this.mPhase;
    }

    public void setPhase(float f) {
        this.mPhase = f;
        synchronized (this.mSvgLock) {
            updatePathsPhaseLocked();
        }
        invalidate();
    }

    public int getSvgResource() {
        return this.mSvgResource;
    }

    public void setSvgResource(int i) {
        this.mSvgResource = i;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(final int i, final int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        Thread thread = this.mLoader;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Log.e("SvgView", "Unexpected error", e);
            }
        }
        Thread thread2 = new Thread(new Runnable() {
            /* class com.oneplus.settings.opfinger.SvgView.AnonymousClass1 */

            public void run() {
                SvgView.this.mSvg.load(SvgView.this.getContext(), SvgView.this.mSvgResource);
                synchronized (SvgView.this.mSvgLock) {
                    SvgView.this.mPaths = SvgView.this.mSvg.getPathsForViewport((i - SvgView.this.getPaddingLeft()) - SvgView.this.getPaddingRight(), (i2 - SvgView.this.getPaddingTop()) - SvgView.this.getPaddingBottom());
                    SvgView.this.updatePathsPhaseLocked();
                }
            }
        }, "SVG Loader");
        this.mLoader = thread2;
        thread2.start();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (this.mSvgLock) {
            canvas.save();
            canvas.translate((float) getPaddingLeft(), ((float) getPaddingTop()) + this.mOffsetY);
            int size = this.mPaths.size();
            for (int i = 0; i < size; i++) {
                SvgHelper.SvgPath svgPath = this.mPaths.get(i);
                svgPath.paint.setAlpha((int) (((float) ((int) (Math.min(this.mPhase * this.mFadeFactor, 1.0f) * 255.0f))) * this.mParallax));
                canvas.drawPath(svgPath.renderPath, svgPath.paint);
            }
            canvas.restore();
        }
    }

    public void reveal(boolean z) {
        if (!this.mHaveMoved) {
            this.mSvgAnimator = null;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "phase", 0.0f, 1.0f);
            this.mSvgAnimator = ofFloat;
            ofFloat.setDuration((long) this.mDuration);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this, "phase", 1.0f, 0.0f);
            ofFloat2.setDuration((long) this.mDuration);
            if (z) {
                this.mSvgAnimator.start();
            } else {
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(this.mSvgAnimator, ofFloat2);
                animatorSet.start();
            }
        } else {
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this, "phase", 0.0f, 1.0f);
            this.mSvgAnimator = ofFloat3;
            ofFloat3.setDuration((long) this.mDuration);
            ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this, "phase", 1.0f, 0.0f);
            ofFloat4.setDuration((long) this.mDuration);
            if (z) {
                this.mSvgAnimator.start();
            } else {
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playSequentially(this.mSvgAnimator, ofFloat4);
                animatorSet2.start();
            }
        }
        this.mHaveMoved = true;
    }

    public void revealWithoutAnimation() {
        this.mSvgResetAnimator = null;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "phase", 0.0f, 1.0f);
        this.mSvgAnimator = ofFloat;
        ofFloat.setDuration(0L);
        this.mSvgAnimator.start();
    }

    public void resetWithoutAnimation() {
        this.mSvgResetAnimator = null;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "phase", 1.0f, 0.0f);
        this.mSvgResetAnimator = ofFloat;
        ofFloat.setDuration(0L);
        this.mSvgResetAnimator.start();
        this.mHaveMoved = false;
    }
}
