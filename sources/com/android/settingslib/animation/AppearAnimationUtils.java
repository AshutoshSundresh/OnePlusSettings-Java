package com.android.settingslib.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.RenderNodeAnimator;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.settingslib.R$dimen;

public class AppearAnimationUtils implements AppearAnimationCreator<View> {
    protected boolean mAppearing;
    protected final float mDelayScale;
    private final long mDuration;
    private final Interpolator mInterpolator;
    private final AppearAnimationProperties mProperties = new AppearAnimationProperties(this);
    protected RowTranslationScaler mRowTranslationScaler;
    private final float mStartTranslation;

    public interface RowTranslationScaler {
        float getRowTranslationScale(int i, int i2);
    }

    public AppearAnimationUtils(Context context, long j, float f, float f2, Interpolator interpolator) {
        this.mInterpolator = interpolator;
        this.mStartTranslation = ((float) context.getResources().getDimensionPixelOffset(R$dimen.appear_y_translation_start)) * f;
        this.mDelayScale = f2;
        this.mDuration = j;
        this.mAppearing = true;
    }

    public void startAnimation(View[] viewArr, Runnable runnable) {
        startAnimation(viewArr, runnable, this);
    }

    public <T> void startAnimation2d(T[][] tArr, Runnable runnable, AppearAnimationCreator<T> appearAnimationCreator) {
        startAnimations(getDelays((Object[][]) tArr), (Object[][]) tArr, runnable, (AppearAnimationCreator) appearAnimationCreator);
    }

    public <T> void startAnimation(T[] tArr, Runnable runnable, AppearAnimationCreator<T> appearAnimationCreator) {
        startAnimations(getDelays(tArr), tArr, runnable, appearAnimationCreator);
    }

    private <T> void startAnimations(AppearAnimationProperties appearAnimationProperties, T[] tArr, Runnable runnable, AppearAnimationCreator<T> appearAnimationCreator) {
        if (appearAnimationProperties.maxDelayRowIndex == -1 || appearAnimationProperties.maxDelayColIndex == -1) {
            runnable.run();
            return;
        }
        int i = 0;
        while (true) {
            long[][] jArr = appearAnimationProperties.delays;
            if (i < jArr.length) {
                long j = jArr[i][0];
                Runnable runnable2 = (appearAnimationProperties.maxDelayRowIndex == i && appearAnimationProperties.maxDelayColIndex == 0) ? runnable : null;
                RowTranslationScaler rowTranslationScaler = this.mRowTranslationScaler;
                float rowTranslationScale = (rowTranslationScaler != null ? rowTranslationScaler.getRowTranslationScale(i, appearAnimationProperties.delays.length) : 1.0f) * this.mStartTranslation;
                T t = tArr[i];
                long j2 = this.mDuration;
                if (!this.mAppearing) {
                    rowTranslationScale = -rowTranslationScale;
                }
                appearAnimationCreator.createAnimation(t, j, j2, rowTranslationScale, this.mAppearing, this.mInterpolator, runnable2);
                i++;
            } else {
                return;
            }
        }
    }

    private <T> void startAnimations(AppearAnimationProperties appearAnimationProperties, T[][] tArr, Runnable runnable, AppearAnimationCreator<T> appearAnimationCreator) {
        if (appearAnimationProperties.maxDelayRowIndex == -1 || appearAnimationProperties.maxDelayColIndex == -1) {
            runnable.run();
            return;
        }
        int i = 0;
        while (true) {
            long[][] jArr = appearAnimationProperties.delays;
            if (i < jArr.length) {
                long[] jArr2 = jArr[i];
                RowTranslationScaler rowTranslationScaler = this.mRowTranslationScaler;
                float rowTranslationScale = (rowTranslationScaler != null ? rowTranslationScaler.getRowTranslationScale(i, jArr.length) : 1.0f) * this.mStartTranslation;
                int i2 = 0;
                while (i2 < jArr2.length) {
                    appearAnimationCreator.createAnimation(tArr[i][i2], jArr2[i2], this.mDuration, this.mAppearing ? rowTranslationScale : -rowTranslationScale, this.mAppearing, this.mInterpolator, (appearAnimationProperties.maxDelayRowIndex == i && appearAnimationProperties.maxDelayColIndex == i2) ? runnable : null);
                    i2++;
                }
                i++;
            } else {
                return;
            }
        }
    }

    private <T> AppearAnimationProperties getDelays(T[] tArr) {
        AppearAnimationProperties appearAnimationProperties = this.mProperties;
        appearAnimationProperties.maxDelayColIndex = -1;
        appearAnimationProperties.maxDelayRowIndex = -1;
        appearAnimationProperties.delays = new long[tArr.length][];
        long j = -1;
        for (int i = 0; i < tArr.length; i++) {
            this.mProperties.delays[i] = new long[1];
            long calculateDelay = calculateDelay(i, 0);
            AppearAnimationProperties appearAnimationProperties2 = this.mProperties;
            appearAnimationProperties2.delays[i][0] = calculateDelay;
            if (tArr[i] != null && calculateDelay > j) {
                appearAnimationProperties2.maxDelayColIndex = 0;
                appearAnimationProperties2.maxDelayRowIndex = i;
                j = calculateDelay;
            }
        }
        return this.mProperties;
    }

    private <T> AppearAnimationProperties getDelays(T[][] tArr) {
        AppearAnimationProperties appearAnimationProperties = this.mProperties;
        appearAnimationProperties.maxDelayColIndex = -1;
        appearAnimationProperties.maxDelayRowIndex = -1;
        appearAnimationProperties.delays = new long[tArr.length][];
        long j = -1;
        for (int i = 0; i < tArr.length; i++) {
            T[] tArr2 = tArr[i];
            this.mProperties.delays[i] = new long[tArr2.length];
            for (int i2 = 0; i2 < tArr2.length; i2++) {
                long calculateDelay = calculateDelay(i, i2);
                AppearAnimationProperties appearAnimationProperties2 = this.mProperties;
                appearAnimationProperties2.delays[i][i2] = calculateDelay;
                if (tArr[i][i2] != null && calculateDelay > j) {
                    appearAnimationProperties2.maxDelayColIndex = i2;
                    appearAnimationProperties2.maxDelayRowIndex = i;
                    j = calculateDelay;
                }
            }
        }
        return this.mProperties;
    }

    /* access modifiers changed from: protected */
    public long calculateDelay(int i, int i2) {
        return (long) ((((double) (i * 40)) + (((double) i2) * (Math.pow((double) i, 0.4d) + 0.4d) * 20.0d)) * ((double) this.mDelayScale));
    }

    public void createAnimation(final View view, long j, long j2, float f, boolean z, Interpolator interpolator, final Runnable runnable) {
        RenderNodeAnimator renderNodeAnimator;
        if (view != null) {
            float f2 = 1.0f;
            view.setAlpha(z ? 0.0f : 1.0f);
            view.setTranslationY(z ? f : 0.0f);
            if (!z) {
                f2 = 0.0f;
            }
            if (view.isHardwareAccelerated()) {
                renderNodeAnimator = new RenderNodeAnimator(11, f2);
                renderNodeAnimator.setTarget(view);
            } else {
                renderNodeAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, view.getAlpha(), f2);
            }
            renderNodeAnimator.setInterpolator(interpolator);
            renderNodeAnimator.setDuration(j2);
            renderNodeAnimator.setStartDelay(j);
            if (view.hasOverlappingRendering()) {
                view.setLayerType(2, null);
                renderNodeAnimator.addListener(new AnimatorListenerAdapter(this) {
                    /* class com.android.settingslib.animation.AppearAnimationUtils.AnonymousClass1 */

                    public void onAnimationEnd(Animator animator) {
                        view.setLayerType(0, null);
                    }
                });
            }
            if (runnable != null) {
                renderNodeAnimator.addListener(new AnimatorListenerAdapter(this) {
                    /* class com.android.settingslib.animation.AppearAnimationUtils.AnonymousClass2 */

                    public void onAnimationEnd(Animator animator) {
                        runnable.run();
                    }
                });
            }
            renderNodeAnimator.start();
            startTranslationYAnimation(view, j, j2, z ? 0.0f : f, interpolator);
        }
    }

    public static void startTranslationYAnimation(View view, long j, long j2, float f, Interpolator interpolator) {
        RenderNodeAnimator renderNodeAnimator;
        if (view.isHardwareAccelerated()) {
            renderNodeAnimator = new RenderNodeAnimator(1, f);
            renderNodeAnimator.setTarget(view);
        } else {
            renderNodeAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.getTranslationY(), f);
        }
        renderNodeAnimator.setInterpolator(interpolator);
        renderNodeAnimator.setDuration(j2);
        renderNodeAnimator.setStartDelay(j);
        renderNodeAnimator.start();
    }

    public class AppearAnimationProperties {
        public long[][] delays;
        public int maxDelayColIndex;
        public int maxDelayRowIndex;

        public AppearAnimationProperties(AppearAnimationUtils appearAnimationUtils) {
        }
    }
}
