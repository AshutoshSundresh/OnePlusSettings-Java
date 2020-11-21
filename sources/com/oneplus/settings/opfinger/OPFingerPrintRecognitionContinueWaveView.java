package com.oneplus.settings.opfinger;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0012R$layout;
import com.android.settings.C0016R$raw;
import com.oneplus.settings.utils.OPUtils;

public class OPFingerPrintRecognitionContinueWaveView extends FrameLayout {
    private Context mContext;
    private FrameLayout mFingerPrintView;
    private LayoutInflater mLayoutInflater;
    private SvgView mSvgView_11_01;
    private SvgView mSvgView_11_05;
    private SvgView mSvgView_12_04;
    private SvgView mSvgView_13_03;
    private SvgView mSvgView_13_04;
    private SvgView mSvgView_14_02;
    private SvgView mSvgView_14_03;
    private SvgView mSvgView_15_02;
    private SvgView mSvgView_15_07;
    private SvgView mSvgView_16_03;
    private SvgView mSvgView_16_06;
    private SvgView mSvgView_17_05;
    private SvgView mSvgView_17_06;
    private SvgView mSvgView_18_05;
    private SvgView mSvgView_18_06;
    private SvgView mSvgView_19_05;
    private SvgView mSvgView_19_07;
    private SvgView mSvgView_20_07;

    public OPFingerPrintRecognitionContinueWaveView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }

    public OPFingerPrintRecognitionContinueWaveView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public OPFingerPrintRecognitionContinueWaveView(Context context) {
        super(context);
        initView(context);
    }

    public void initView(Context context) {
        this.mContext = context;
        LayoutInflater from = LayoutInflater.from(context);
        this.mLayoutInflater = from;
        this.mFingerPrintView = (FrameLayout) from.inflate(C0012R$layout.op_finger_input_anim_layout, this);
        setEnrollAnimBgColor("#414141");
        initSvgView(context, this.mFingerPrintView);
    }

    public void setEnrollAnimBgColor(String str) {
        Drawable drawable;
        if (!OPUtils.isSupportCustomFingerprint() || !OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            drawable = this.mContext.getResources().getDrawable(C0008R$drawable.opfinger_anim_color_bg_2);
        } else {
            drawable = this.mContext.getResources().getDrawable(C0008R$drawable.opfinger_anim_color_fod_bg_2);
        }
        if (OPUtils.isSupportCustomFingerprint()) {
            drawable.setTint(Color.parseColor(str));
        }
    }

    public void initSvgView(Context context, FrameLayout frameLayout) {
        this.mSvgView_11_01 = getSvgView(C0016R$raw.opfinger_fod_anim_11_01, frameLayout);
        this.mSvgView_11_05 = getSvgView(C0016R$raw.opfinger_fod_anim_11_05, frameLayout);
        this.mSvgView_12_04 = getSvgView(C0016R$raw.opfinger_fod_anim_12_04, frameLayout);
        this.mSvgView_13_03 = getSvgView(C0016R$raw.opfinger_fod_anim_13_03, frameLayout);
        this.mSvgView_13_04 = getSvgView(C0016R$raw.opfinger_fod_anim_13_04, frameLayout);
        this.mSvgView_14_02 = getSvgView(C0016R$raw.opfinger_fod_anim_14_02, frameLayout);
        this.mSvgView_14_03 = getSvgView(C0016R$raw.opfinger_fod_anim_14_03, frameLayout);
        this.mSvgView_15_02 = getSvgView(C0016R$raw.opfinger_fod_anim_15_02, frameLayout);
        this.mSvgView_15_07 = getSvgView(C0016R$raw.opfinger_fod_anim_15_07, frameLayout);
        this.mSvgView_16_03 = getSvgView(C0016R$raw.opfinger_fod_anim_16_03, frameLayout);
        this.mSvgView_16_06 = getSvgView(C0016R$raw.opfinger_fod_anim_16_06, frameLayout);
        this.mSvgView_17_05 = getSvgView(C0016R$raw.opfinger_fod_anim_17_05, frameLayout);
        this.mSvgView_17_06 = getSvgView(C0016R$raw.opfinger_fod_anim_17_06, frameLayout);
        this.mSvgView_18_05 = getSvgView(C0016R$raw.opfinger_fod_anim_18_05, frameLayout);
        this.mSvgView_18_06 = getSvgView(C0016R$raw.opfinger_fod_anim_18_06, frameLayout);
        this.mSvgView_19_05 = getSvgView(C0016R$raw.opfinger_fod_anim_19_05, frameLayout);
        this.mSvgView_19_07 = getSvgView(C0016R$raw.opfinger_fod_anim_19_07, frameLayout);
        this.mSvgView_20_07 = getSvgView(C0016R$raw.opfinger_fod_anim_20_07, frameLayout);
        addView(this.mSvgView_11_01);
        addView(this.mSvgView_11_05);
        addView(this.mSvgView_12_04);
        addView(this.mSvgView_13_03);
        addView(this.mSvgView_13_04);
        addView(this.mSvgView_14_02);
        addView(this.mSvgView_14_03);
        addView(this.mSvgView_15_02);
        addView(this.mSvgView_15_07);
        addView(this.mSvgView_16_03);
        addView(this.mSvgView_16_06);
        addView(this.mSvgView_17_05);
        addView(this.mSvgView_17_06);
        addView(this.mSvgView_18_05);
        addView(this.mSvgView_18_06);
        addView(this.mSvgView_19_05);
        addView(this.mSvgView_19_07);
        addView(this.mSvgView_20_07);
    }

    public void startTouchDownAnim() {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation.setDuration(300);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setStartOffset(0);
        animationSet.addAnimation(scaleAnimation);
        ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation2.setDuration(150);
        scaleAnimation2.setStartOffset(542);
        animationSet.addAnimation(scaleAnimation2);
        this.mSvgView_11_01.startAnimation(animationSet);
        AnimationSet animationSet2 = new AnimationSet(true);
        ScaleAnimation scaleAnimation3 = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation3.setDuration(300);
        scaleAnimation3.setFillAfter(true);
        scaleAnimation3.setStartOffset(32);
        animationSet2.addAnimation(scaleAnimation3);
        ScaleAnimation scaleAnimation4 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation4.setDuration(150);
        scaleAnimation4.setStartOffset(510);
        animationSet2.addAnimation(scaleAnimation4);
        this.mSvgView_14_02.startAnimation(animationSet2);
        this.mSvgView_15_02.startAnimation(animationSet2);
        AnimationSet animationSet3 = new AnimationSet(true);
        ScaleAnimation scaleAnimation5 = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation5.setDuration(300);
        scaleAnimation5.setFillAfter(true);
        scaleAnimation5.setStartOffset(64);
        animationSet3.addAnimation(scaleAnimation5);
        ScaleAnimation scaleAnimation6 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation6.setDuration(150);
        scaleAnimation6.setStartOffset(478);
        animationSet3.addAnimation(scaleAnimation6);
        this.mSvgView_14_03.startAnimation(animationSet3);
        this.mSvgView_16_03.startAnimation(animationSet3);
        this.mSvgView_13_03.startAnimation(animationSet3);
        AnimationSet animationSet4 = new AnimationSet(true);
        ScaleAnimation scaleAnimation7 = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation7.setDuration(300);
        scaleAnimation7.setFillAfter(true);
        scaleAnimation7.setStartOffset(96);
        animationSet4.addAnimation(scaleAnimation7);
        ScaleAnimation scaleAnimation8 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation8.setDuration(150);
        scaleAnimation8.setStartOffset(446);
        animationSet4.addAnimation(scaleAnimation8);
        this.mSvgView_12_04.startAnimation(animationSet4);
        this.mSvgView_13_04.startAnimation(animationSet4);
        AnimationSet animationSet5 = new AnimationSet(true);
        ScaleAnimation scaleAnimation9 = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation9.setDuration(300);
        scaleAnimation9.setFillAfter(true);
        scaleAnimation9.setStartOffset(128);
        animationSet5.addAnimation(scaleAnimation9);
        ScaleAnimation scaleAnimation10 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation10.setDuration(150);
        scaleAnimation10.setStartOffset(414);
        animationSet5.addAnimation(scaleAnimation10);
        this.mSvgView_11_05.startAnimation(animationSet5);
        this.mSvgView_17_05.startAnimation(animationSet5);
        this.mSvgView_18_05.startAnimation(animationSet5);
        this.mSvgView_19_05.startAnimation(animationSet5);
        AnimationSet animationSet6 = new AnimationSet(true);
        ScaleAnimation scaleAnimation11 = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation11.setDuration(300);
        scaleAnimation11.setFillAfter(true);
        scaleAnimation11.setStartOffset(160);
        animationSet6.addAnimation(scaleAnimation11);
        ScaleAnimation scaleAnimation12 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation12.setDuration(150);
        scaleAnimation12.setStartOffset(382);
        animationSet6.addAnimation(scaleAnimation12);
        this.mSvgView_16_06.startAnimation(animationSet6);
        this.mSvgView_17_06.startAnimation(animationSet6);
        this.mSvgView_18_06.startAnimation(animationSet6);
        AnimationSet animationSet7 = new AnimationSet(true);
        ScaleAnimation scaleAnimation13 = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation13.setDuration(300);
        scaleAnimation13.setFillAfter(true);
        scaleAnimation13.setStartOffset(192);
        animationSet7.addAnimation(scaleAnimation13);
        ScaleAnimation scaleAnimation14 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation14.setDuration(150);
        scaleAnimation14.setStartOffset(350);
        animationSet7.addAnimation(scaleAnimation14);
        this.mSvgView_15_07.startAnimation(animationSet7);
        this.mSvgView_19_07.startAnimation(animationSet7);
        this.mSvgView_20_07.startAnimation(animationSet7);
    }

    private SvgView getSvgView(int i, FrameLayout frameLayout) {
        SvgView svgView = (SvgView) this.mLayoutInflater.inflate(C0012R$layout.op_finger_input_item_svg, (ViewGroup) frameLayout, false);
        svgView.setSvgResource(i);
        return svgView;
    }

    public void resetWithoutAnimation() {
        this.mSvgView_11_01.resetWithoutAnimation();
        this.mSvgView_11_05.resetWithoutAnimation();
        this.mSvgView_12_04.resetWithoutAnimation();
        this.mSvgView_13_03.resetWithoutAnimation();
        this.mSvgView_13_04.resetWithoutAnimation();
        this.mSvgView_14_02.resetWithoutAnimation();
        this.mSvgView_14_03.resetWithoutAnimation();
        this.mSvgView_15_02.resetWithoutAnimation();
        this.mSvgView_15_07.resetWithoutAnimation();
        this.mSvgView_16_03.resetWithoutAnimation();
        this.mSvgView_16_06.resetWithoutAnimation();
        this.mSvgView_17_05.resetWithoutAnimation();
        this.mSvgView_17_06.resetWithoutAnimation();
        this.mSvgView_18_05.resetWithoutAnimation();
        this.mSvgView_18_06.resetWithoutAnimation();
        this.mSvgView_19_05.resetWithoutAnimation();
        this.mSvgView_19_07.resetWithoutAnimation();
        this.mSvgView_20_07.resetWithoutAnimation();
    }

    public void doRecognitionByCount(int i, int i2, boolean z) {
        if (OPUtils.getFingerprintScaleAnimStep(this.mContext) == 8) {
            i += 2;
        }
        switch (i) {
            case 11:
                this.mSvgView_11_01.reveal(z);
                this.mSvgView_11_05.reveal(z);
                return;
            case 12:
                this.mSvgView_12_04.reveal(z);
                return;
            case 13:
                this.mSvgView_13_03.reveal(z);
                this.mSvgView_13_04.reveal(z);
                return;
            case 14:
                this.mSvgView_14_02.reveal(z);
                this.mSvgView_14_03.reveal(z);
                return;
            case 15:
                this.mSvgView_15_02.reveal(z);
                this.mSvgView_15_07.reveal(z);
                return;
            case 16:
                this.mSvgView_16_03.reveal(z);
                this.mSvgView_16_06.reveal(z);
                return;
            case 17:
                this.mSvgView_17_05.reveal(z);
                this.mSvgView_17_06.reveal(z);
                return;
            case 18:
                this.mSvgView_18_05.reveal(z);
                this.mSvgView_18_06.reveal(z);
                return;
            case 19:
                this.mSvgView_19_05.reveal(z);
                this.mSvgView_19_07.reveal(z);
                return;
            case 20:
                this.mSvgView_20_07.reveal(z);
                return;
            default:
                return;
        }
    }

    public void setBackGround(int i) {
        FrameLayout frameLayout = this.mFingerPrintView;
        if (frameLayout != null) {
            frameLayout.setBackgroundResource(i);
        }
    }
}
