package com.oneplus.settings.opfinger;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0012R$layout;
import com.android.settings.C0016R$raw;
import com.oneplus.settings.utils.OPUtils;

public class OPFingerPrintRecognitionLottieView extends FrameLayout {
    private OPLottieAnimationView mBgView;
    private Context mContext;
    private FrameLayout mFingerPrintView;
    private LayoutInflater mLayoutInflater;
    private OPLottieAnimationView mLottieAnimationView01;
    private OPLottieAnimationView mLottieAnimationView02;
    private OPLottieAnimationView mLottieAnimationView03;

    public OPFingerPrintRecognitionLottieView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }

    public OPFingerPrintRecognitionLottieView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public OPFingerPrintRecognitionLottieView(Context context) {
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
        oPLottieAnimationView.setAnimation(C0016R$raw.op_fod_fingerprint_enroll_inner_bg);
        OPLottieAnimationView lottieAnimationView = getLottieAnimationView(C0016R$raw.op_fod_fingerprint_enroll_inner_01, frameLayout);
        this.mLottieAnimationView01 = lottieAnimationView;
        lottieAnimationView.setSplitSteps(4);
        OPLottieAnimationView lottieAnimationView2 = getLottieAnimationView(C0016R$raw.op_fod_fingerprint_enroll_inner_02, frameLayout);
        this.mLottieAnimationView02 = lottieAnimationView2;
        lottieAnimationView2.setSplitSteps(3);
        OPLottieAnimationView lottieAnimationView3 = getLottieAnimationView(C0016R$raw.op_fod_fingerprint_enroll_inner_03, frameLayout);
        this.mLottieAnimationView03 = lottieAnimationView3;
        lottieAnimationView3.setSplitSteps(3);
        addView(this.mBgView);
        addView(this.mLottieAnimationView01);
        addView(this.mLottieAnimationView02);
        addView(this.mLottieAnimationView03);
    }

    private OPLottieAnimationView getLottieAnimationView(int i, FrameLayout frameLayout) {
        final OPLottieAnimationView oPLottieAnimationView = (OPLottieAnimationView) this.mLayoutInflater.inflate(C0012R$layout.op_fod_fingerprint_enroll_lottie_view, (ViewGroup) frameLayout, false);
        oPLottieAnimationView.setAnimation(i);
        oPLottieAnimationView.setSpeed(4.0f);
        oPLottieAnimationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener(this) {
            /* class com.oneplus.settings.opfinger.OPFingerPrintRecognitionLottieView.AnonymousClass1 */
            float curretProgress = 0.0f;

            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                if (animatedFraction >= this.curretProgress + (1.0f / ((float) oPLottieAnimationView.getSplitSteps()))) {
                    oPLottieAnimationView.pauseAnimation();
                    this.curretProgress = animatedFraction;
                }
                if (((double) animatedFraction) >= 1.0d) {
                    this.curretProgress = 0.0f;
                }
            }
        });
        return oPLottieAnimationView;
    }

    public void playAnimationByCount(int i, int i2, int i3, boolean z) {
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 4:
                this.mLottieAnimationView01.resumeAnimation();
                return;
            case 5:
            case 6:
            case 7:
                this.mLottieAnimationView02.resumeAnimation();
                return;
            case 8:
            case 9:
            case 10:
                this.mLottieAnimationView03.resumeAnimation();
                return;
            default:
                return;
        }
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
    }

    public void setBackGround(int i) {
        FrameLayout frameLayout = this.mFingerPrintView;
        if (frameLayout != null) {
            frameLayout.setBackgroundResource(i);
        }
    }
}
