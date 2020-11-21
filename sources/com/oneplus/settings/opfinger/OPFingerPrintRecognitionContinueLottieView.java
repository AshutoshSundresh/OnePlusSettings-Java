package com.oneplus.settings.opfinger;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0012R$layout;
import com.android.settings.C0016R$raw;
import com.oneplus.settings.utils.OPUtils;

public class OPFingerPrintRecognitionContinueLottieView extends FrameLayout {
    private OPLottieAnimationView mBgView;
    private Context mContext;
    private FrameLayout mFingerPrintView;
    private int mLastExcessStep = -1;
    private LayoutInflater mLayoutInflater;
    private OPLottieAnimationView mLottieAnimationView01;
    private OPLottieAnimationView mLottieAnimationView02;
    private OPLottieAnimationView mLottieAnimationView03;
    private OPLottieAnimationView mLottieAnimationView04;
    private OPLottieAnimationView mLottieAnimationView05;

    public OPFingerPrintRecognitionContinueLottieView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }

    public OPFingerPrintRecognitionContinueLottieView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public OPFingerPrintRecognitionContinueLottieView(Context context) {
        super(context);
        initView(context);
    }

    public void initView(Context context) {
        this.mContext = context;
        LayoutInflater from = LayoutInflater.from(context);
        this.mLayoutInflater = from;
        this.mFingerPrintView = (FrameLayout) from.inflate(C0012R$layout.op_finger_input_anim_layout, this);
        setEnrollAnimBgColor("#414141");
        initLottieAnimationView(context, this.mFingerPrintView);
    }

    public void setEnrollAnimBgColor(String str) {
        Drawable drawable;
        if ((!OPUtils.isSupportCustomFingerprint() || !OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) && !OPUtils.isSupportDynamicEnrollAnimation()) {
            Drawable drawable2 = this.mContext.getResources().getDrawable(C0008R$drawable.opfinger_anim_color_bg);
            FrameLayout frameLayout = this.mFingerPrintView;
            if (frameLayout != null) {
                frameLayout.setBackgroundDrawable(drawable2);
            }
            drawable = drawable2;
        } else {
            drawable = this.mContext.getResources().getDrawable(C0008R$drawable.opfinger_anim_color_fod_bg);
        }
        if (OPUtils.isSupportCustomFingerprint()) {
            drawable.setTint(Color.parseColor(str));
        }
    }

    public void initLottieAnimationView(Context context, FrameLayout frameLayout) {
        OPLottieAnimationView oPLottieAnimationView = (OPLottieAnimationView) this.mLayoutInflater.inflate(C0012R$layout.op_fod_fingerprint_enroll_lottie_view, (ViewGroup) frameLayout, false);
        this.mBgView = oPLottieAnimationView;
        oPLottieAnimationView.setAnimation(C0016R$raw.op_fod_fingerprint_enroll_outer_bg);
        OPLottieAnimationView lottieAnimationView = getLottieAnimationView(C0016R$raw.op_fod_fingerprint_enroll_outer_01, frameLayout);
        this.mLottieAnimationView01 = lottieAnimationView;
        lottieAnimationView.setSplitSteps(4);
        OPLottieAnimationView lottieAnimationView2 = getLottieAnimationView(C0016R$raw.op_fod_fingerprint_enroll_outer_02, frameLayout);
        this.mLottieAnimationView02 = lottieAnimationView2;
        lottieAnimationView2.setSplitSteps(3);
        OPLottieAnimationView lottieAnimationView3 = getLottieAnimationView(C0016R$raw.op_fod_fingerprint_enroll_outer_03, frameLayout);
        this.mLottieAnimationView03 = lottieAnimationView3;
        lottieAnimationView3.setSplitSteps(3);
        this.mLottieAnimationView04 = getLottieAnimationView(C0016R$raw.op_fod_fingerprint_enroll_outer_04, frameLayout);
        this.mLottieAnimationView05 = getLottieAnimationView(C0016R$raw.op_fod_fingerprint_enroll_outer_05, frameLayout);
        addView(this.mBgView);
        addView(this.mLottieAnimationView01);
        addView(this.mLottieAnimationView02);
        addView(this.mLottieAnimationView03);
        addView(this.mLottieAnimationView04);
        addView(this.mLottieAnimationView05);
    }

    public void playContinueAnimation() {
        OPLottieAnimationView oPLottieAnimationView = this.mBgView;
        if (oPLottieAnimationView != null) {
            oPLottieAnimationView.playAnimation();
        }
    }

    private OPLottieAnimationView getLottieAnimationView(int i, FrameLayout frameLayout) {
        final OPLottieAnimationView oPLottieAnimationView = (OPLottieAnimationView) this.mLayoutInflater.inflate(C0012R$layout.op_fod_fingerprint_enroll_lottie_view, (ViewGroup) frameLayout, false);
        oPLottieAnimationView.setAnimation(i);
        oPLottieAnimationView.setSpeed(3.0f);
        oPLottieAnimationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener(this) {
            /* class com.oneplus.settings.opfinger.OPFingerPrintRecognitionContinueLottieView.AnonymousClass1 */
            float curretProgress = 0.0f;

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                float splitSteps = 1.0f / ((float) oPLottieAnimationView.getSplitSteps());
                if (animatedFraction >= this.curretProgress + splitSteps) {
                    oPLottieAnimationView.pauseAnimation();
                    this.curretProgress = animatedFraction;
                }
                if (((double) animatedFraction) >= 1.0d) {
                    this.curretProgress = 0.0f;
                    oPLottieAnimationView.setFillCompleted(true);
                }
                Log.d("MainActivity", "OPFingerPrintRecognitionContinueLottieView splitProgress:" + splitSteps + " curretProgress:" + this.curretProgress);
            }
        });
        return oPLottieAnimationView;
    }

    public void playAnimationByCount(int i, int i2, int i3, boolean z) {
        dynamicSetLottieSplit(i2, i3);
        if (!this.mLottieAnimationView01.isFillCompleted()) {
            this.mLottieAnimationView01.resumeAnimation();
        }
        if (this.mLottieAnimationView01.isFillCompleted() && !this.mLottieAnimationView02.isFillCompleted()) {
            this.mLottieAnimationView02.resumeAnimation();
        }
        if (this.mLottieAnimationView02.isFillCompleted() && !this.mLottieAnimationView03.isFillCompleted()) {
            this.mLottieAnimationView03.resumeAnimation();
        }
        if (this.mLottieAnimationView03.isFillCompleted() && !this.mLottieAnimationView04.isFillCompleted()) {
            this.mLottieAnimationView04.resumeAnimation();
        }
        if (this.mLottieAnimationView04.isFillCompleted() && !this.mLottieAnimationView05.isFillCompleted()) {
            this.mLottieAnimationView05.resumeAnimation();
        }
    }

    public void dynamicSetLottieSplit(int i, int i2) {
        int unFullFillViewCount = getUnFullFillViewCount();
        if (unFullFillViewCount != 0) {
            int i3 = (i + 0) / unFullFillViewCount;
            if (i3 <= 0) {
                i3 = 1;
            }
            int i4 = i3 + 0;
            if (!this.mLottieAnimationView01.isFillCompleted()) {
                this.mLottieAnimationView01.setSplitSteps(i4);
                this.mLottieAnimationView01.setSpeed((float) (12 / i4));
            }
            int i5 = i3 + 0;
            if (!this.mLottieAnimationView02.isFillCompleted()) {
                this.mLottieAnimationView02.setSplitSteps(i5);
                this.mLottieAnimationView02.setSpeed((float) (12 / i5));
            }
            int i6 = i3 + 0;
            if (!this.mLottieAnimationView03.isFillCompleted()) {
                this.mLottieAnimationView03.setSplitSteps(i5);
                this.mLottieAnimationView03.setSpeed((float) (12 / i6));
            }
            int i7 = i3 + 0;
            if (!this.mLottieAnimationView04.isFillCompleted()) {
                this.mLottieAnimationView04.setSplitSteps(i7);
                this.mLottieAnimationView04.setSpeed((float) (12 / i7));
            }
            if (unFullFillViewCount == 1 && this.mLastExcessStep == -1) {
                int i8 = i + 1;
                this.mLastExcessStep = i8;
                this.mLottieAnimationView05.setSplitSteps(i8);
                this.mLottieAnimationView05.setSpeed((float) (12 / this.mLastExcessStep));
            }
        }
    }

    public int getUnFullFillViewCount() {
        int i = this.mLottieAnimationView01.isFillCompleted() ? 4 : 5;
        if (this.mLottieAnimationView02.isFillCompleted()) {
            i--;
        }
        if (this.mLottieAnimationView03.isFillCompleted()) {
            i--;
        }
        if (this.mLottieAnimationView04.isFillCompleted()) {
            i--;
        }
        return this.mLottieAnimationView05.isFillCompleted() ? i - 1 : i;
    }

    public void releaseFingerprintLottieAnimation() {
        OPLottieAnimationView oPLottieAnimationView = this.mLottieAnimationView01;
        if (oPLottieAnimationView != null) {
            oPLottieAnimationView.cancelAnimation();
            this.mLottieAnimationView01 = null;
        }
        OPLottieAnimationView oPLottieAnimationView2 = this.mLottieAnimationView02;
        if (oPLottieAnimationView2 != null) {
            oPLottieAnimationView2.cancelAnimation();
            this.mLottieAnimationView02 = null;
        }
        OPLottieAnimationView oPLottieAnimationView3 = this.mLottieAnimationView03;
        if (oPLottieAnimationView3 != null) {
            oPLottieAnimationView3.cancelAnimation();
            this.mLottieAnimationView03 = null;
        }
        OPLottieAnimationView oPLottieAnimationView4 = this.mLottieAnimationView04;
        if (oPLottieAnimationView4 != null) {
            oPLottieAnimationView4.cancelAnimation();
            this.mLottieAnimationView04 = null;
        }
        OPLottieAnimationView oPLottieAnimationView5 = this.mLottieAnimationView05;
        if (oPLottieAnimationView5 != null) {
            oPLottieAnimationView5.cancelAnimation();
            this.mLottieAnimationView05 = null;
        }
    }

    public void setBackGround(int i) {
        FrameLayout frameLayout = this.mFingerPrintView;
        if (frameLayout != null) {
            frameLayout.setBackgroundResource(i);
        }
    }
}
