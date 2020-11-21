package com.android.settings.biometrics.fingerprint;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0009R$fraction;
import com.android.settingslib.Utils;

public class FingerprintLocationAnimationView extends View implements FingerprintFindSensorAnimation {
    private ValueAnimator mAlphaAnimator;
    private final Paint mDotPaint = new Paint();
    private final int mDotRadius = getResources().getDimensionPixelSize(C0007R$dimen.fingerprint_dot_radius);
    private final Interpolator mFastOutSlowInInterpolator;
    private final float mFractionCenterX = getResources().getFraction(C0009R$fraction.fingerprint_sensor_location_fraction_x, 1, 1);
    private final float mFractionCenterY = getResources().getFraction(C0009R$fraction.fingerprint_sensor_location_fraction_y, 1, 1);
    private final Interpolator mLinearOutSlowInInterpolator;
    private final int mMaxPulseRadius = getResources().getDimensionPixelSize(C0007R$dimen.fingerprint_pulse_radius);
    private final Paint mPulsePaint = new Paint();
    private float mPulseRadius;
    private ValueAnimator mRadiusAnimator;
    private final Runnable mStartPhaseRunnable = new Runnable() {
        /* class com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationView.AnonymousClass5 */

        public void run() {
            FingerprintLocationAnimationView.this.startPhase();
        }
    };

    public FingerprintLocationAnimationView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        int colorAccentDefaultColor = Utils.getColorAccentDefaultColor(context);
        this.mDotPaint.setAntiAlias(true);
        this.mPulsePaint.setAntiAlias(true);
        this.mDotPaint.setColor(colorAccentDefaultColor);
        this.mPulsePaint.setColor(colorAccentDefaultColor);
        this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
        this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        drawPulse(canvas);
        drawDot(canvas);
    }

    private void drawDot(Canvas canvas) {
        canvas.drawCircle(getCenterX(), getCenterY(), (float) this.mDotRadius, this.mDotPaint);
    }

    private void drawPulse(Canvas canvas) {
        canvas.drawCircle(getCenterX(), getCenterY(), this.mPulseRadius, this.mPulsePaint);
    }

    private float getCenterX() {
        return ((float) getWidth()) * this.mFractionCenterX;
    }

    private float getCenterY() {
        return ((float) getHeight()) * this.mFractionCenterY;
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintFindSensorAnimation
    public void startAnimation() {
        startPhase();
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintFindSensorAnimation
    public void stopAnimation() {
        removeCallbacks(this.mStartPhaseRunnable);
        ValueAnimator valueAnimator = this.mRadiusAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator valueAnimator2 = this.mAlphaAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
        }
    }

    @Override // com.android.settings.biometrics.fingerprint.FingerprintFindSensorAnimation
    public void pauseAnimation() {
        stopAnimation();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startPhase() {
        startRadiusAnimation();
        startAlphaAnimation();
    }

    private void startRadiusAnimation() {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, (float) this.mMaxPulseRadius);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationView.AnonymousClass1 */

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                FingerprintLocationAnimationView.this.mPulseRadius = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                FingerprintLocationAnimationView.this.invalidate();
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationView.AnonymousClass2 */
            boolean mCancelled;

            public void onAnimationCancel(Animator animator) {
                this.mCancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                FingerprintLocationAnimationView.this.mRadiusAnimator = null;
                if (!this.mCancelled) {
                    FingerprintLocationAnimationView fingerprintLocationAnimationView = FingerprintLocationAnimationView.this;
                    fingerprintLocationAnimationView.postDelayed(fingerprintLocationAnimationView.mStartPhaseRunnable, 1000);
                }
            }
        });
        ofFloat.setDuration(1000L);
        ofFloat.setInterpolator(this.mLinearOutSlowInInterpolator);
        ofFloat.start();
        this.mRadiusAnimator = ofFloat;
    }

    private void startAlphaAnimation() {
        this.mPulsePaint.setAlpha(38);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.15f, 0.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationView.AnonymousClass3 */

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                FingerprintLocationAnimationView.this.mPulsePaint.setAlpha((int) (((Float) valueAnimator.getAnimatedValue()).floatValue() * 255.0f));
                FingerprintLocationAnimationView.this.invalidate();
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            /* class com.android.settings.biometrics.fingerprint.FingerprintLocationAnimationView.AnonymousClass4 */

            public void onAnimationEnd(Animator animator) {
                FingerprintLocationAnimationView.this.mAlphaAnimator = null;
            }
        });
        ofFloat.setDuration(750L);
        ofFloat.setInterpolator(this.mFastOutSlowInInterpolator);
        ofFloat.setStartDelay(250);
        ofFloat.start();
        this.mAlphaAnimator = ofFloat;
    }
}
