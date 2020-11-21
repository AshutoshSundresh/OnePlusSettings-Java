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

public class OPFingerPrintRecognitionView extends FrameLayout {
    private Context mContext;
    private FrameLayout mFingerPrintView;
    private LayoutInflater mLayoutInflater;
    private SvgView mSvgView01;
    private SvgView mSvgView02;
    private SvgView mSvgView03;
    private SvgView mSvgView04;
    private SvgView mSvgView05;
    private SvgView mSvgView06;
    private SvgView mSvgView07;
    private SvgView mSvgView08;
    private SvgView mSvgView08ForFod;
    private SvgView mSvgView09;
    private SvgView mSvgView10;

    public OPFingerPrintRecognitionView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }

    public OPFingerPrintRecognitionView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public OPFingerPrintRecognitionView(Context context) {
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

    public void initSvgView(Context context, FrameLayout frameLayout) {
        if (OPUtils.isSupportCustomFingerprint() && OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            this.mSvgView01 = getSvgView(C0016R$raw.opfinger_fod_anim_01, frameLayout);
            this.mSvgView02 = getSvgView(C0016R$raw.opfinger_fod_anim_02, frameLayout);
            this.mSvgView03 = getSvgView(C0016R$raw.opfinger_fod_anim_03, frameLayout);
            this.mSvgView04 = getSvgView(C0016R$raw.opfinger_fod_anim_04, frameLayout);
            this.mSvgView05 = getSvgView(C0016R$raw.opfinger_fod_anim_05, frameLayout);
            this.mSvgView06 = getSvgView(C0016R$raw.opfinger_fod_anim_06, frameLayout);
            this.mSvgView07 = getSvgView(C0016R$raw.opfinger_fod_anim_07, frameLayout);
            this.mSvgView08 = getSvgView(C0016R$raw.opfinger_fod_anim_08_03, frameLayout);
            this.mSvgView08ForFod = getSvgView(C0016R$raw.opfinger_fod_anim_08_04, frameLayout);
            this.mSvgView09 = getSvgView(C0016R$raw.opfinger_fod_anim_09, frameLayout);
            this.mSvgView10 = getSvgView(C0016R$raw.opfinger_fod_anim_10, frameLayout);
        } else if (!OPUtils.isSurportBackFingerprint(context) || OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            this.mSvgView01 = getSvgView(C0016R$raw.opfinger_anim_01, frameLayout);
            this.mSvgView02 = getSvgView(C0016R$raw.opfinger_anim_02, frameLayout);
            this.mSvgView03 = getSvgView(C0016R$raw.opfinger_anim_03, frameLayout);
            this.mSvgView04 = getSvgView(C0016R$raw.opfinger_anim_04, frameLayout);
            this.mSvgView05 = getSvgView(C0016R$raw.opfinger_anim_05, frameLayout);
            this.mSvgView06 = getSvgView(C0016R$raw.opfinger_anim_06, frameLayout);
            this.mSvgView07 = getSvgView(C0016R$raw.opfinger_anim_07, frameLayout);
            this.mSvgView08 = getSvgView(C0016R$raw.opfinger_anim_08, frameLayout);
            this.mSvgView09 = getSvgView(C0016R$raw.opfinger_anim_09, frameLayout);
            this.mSvgView10 = getSvgView(C0016R$raw.opfinger_anim_10, frameLayout);
        } else if (OPUtils.isFingerprintNeedEnrollTime16(context)) {
            this.mSvgView01 = getSvgView(C0016R$raw.opfinger_anim_16_01, frameLayout);
            this.mSvgView02 = getSvgView(C0016R$raw.opfinger_anim_16_02, frameLayout);
            this.mSvgView03 = getSvgView(C0016R$raw.opfinger_anim_16_03, frameLayout);
            this.mSvgView04 = getSvgView(C0016R$raw.opfinger_anim_16_04, frameLayout);
            this.mSvgView05 = getSvgView(C0016R$raw.opfinger_anim_16_05, frameLayout);
            this.mSvgView06 = getSvgView(C0016R$raw.opfinger_anim_16_06, frameLayout);
            this.mSvgView07 = getSvgView(C0016R$raw.opfinger_anim_16_07, frameLayout);
            this.mSvgView08 = getSvgView(C0016R$raw.opfinger_anim_16_08, frameLayout);
        } else {
            this.mSvgView01 = getSvgView(C0016R$raw.opfinger_anim_17801_01, frameLayout);
            this.mSvgView02 = getSvgView(C0016R$raw.opfinger_anim_17801_02, frameLayout);
            this.mSvgView03 = getSvgView(C0016R$raw.opfinger_anim_17801_03, frameLayout);
            this.mSvgView04 = getSvgView(C0016R$raw.opfinger_anim_17801_04, frameLayout);
            this.mSvgView05 = getSvgView(C0016R$raw.opfinger_anim_17801_05, frameLayout);
            this.mSvgView06 = getSvgView(C0016R$raw.opfinger_anim_17801_06, frameLayout);
            this.mSvgView07 = getSvgView(C0016R$raw.opfinger_anim_17801_07, frameLayout);
            this.mSvgView08 = getSvgView(C0016R$raw.opfinger_anim_17801_08, frameLayout);
        }
        addView(this.mSvgView01);
        addView(this.mSvgView02);
        addView(this.mSvgView03);
        addView(this.mSvgView04);
        addView(this.mSvgView05);
        addView(this.mSvgView06);
        addView(this.mSvgView07);
        addView(this.mSvgView08);
        if (OPUtils.isSupportCustomFingerprint() && OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            addView(this.mSvgView08ForFod);
            addView(this.mSvgView09);
            addView(this.mSvgView10);
        } else if (!OPUtils.isSurportBackFingerprint(context) || OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            addView(this.mSvgView09);
            addView(this.mSvgView10);
        }
        resetWithoutAnimation();
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
        this.mSvgView01.startAnimation(animationSet);
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
        this.mSvgView02.startAnimation(animationSet2);
        this.mSvgView03.startAnimation(animationSet2);
        this.mSvgView04.startAnimation(animationSet2);
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
        this.mSvgView05.startAnimation(animationSet3);
        this.mSvgView06.startAnimation(animationSet3);
        this.mSvgView07.startAnimation(animationSet3);
        this.mSvgView08.startAnimation(animationSet3);
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
        this.mSvgView08ForFod.startAnimation(animationSet4);
        this.mSvgView09.startAnimation(animationSet4);
        this.mSvgView10.startAnimation(animationSet4);
    }

    private SvgView getSvgView(int i, FrameLayout frameLayout) {
        SvgView svgView = (SvgView) this.mLayoutInflater.inflate(C0012R$layout.op_finger_input_item_svg, (ViewGroup) frameLayout, false);
        svgView.setSvgResource(i);
        return svgView;
    }

    public void resetWithoutAnimation() {
        this.mSvgView01.resetWithoutAnimation();
        this.mSvgView02.resetWithoutAnimation();
        this.mSvgView03.resetWithoutAnimation();
        this.mSvgView04.resetWithoutAnimation();
        this.mSvgView05.resetWithoutAnimation();
        this.mSvgView06.resetWithoutAnimation();
        this.mSvgView07.resetWithoutAnimation();
        this.mSvgView08.resetWithoutAnimation();
        if (OPUtils.isSupportCustomFingerprint() && OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            this.mSvgView08ForFod.resetWithoutAnimation();
            this.mSvgView09.resetWithoutAnimation();
            this.mSvgView10.resetWithoutAnimation();
        } else if (!OPUtils.isSurportBackFingerprint(this.mContext) || OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            this.mSvgView09.resetWithoutAnimation();
            this.mSvgView10.resetWithoutAnimation();
        }
    }

    public void doRecognitionByCount(int i, int i2, boolean z) {
        SvgView svgView;
        switch (i) {
            case 1:
                this.mSvgView01.reveal(z);
                return;
            case 2:
                this.mSvgView02.reveal(z);
                return;
            case 3:
                this.mSvgView03.reveal(z);
                return;
            case 4:
                this.mSvgView04.reveal(z);
                return;
            case 5:
                this.mSvgView05.reveal(z);
                return;
            case 6:
                this.mSvgView06.reveal(z);
                return;
            case 7:
                this.mSvgView07.reveal(z);
                return;
            case 8:
                this.mSvgView08.reveal(z);
                if (OPUtils.isSupportCustomFingerprint() && OPUtils.isFingerprintNeedEnrollTime20(this.mContext) && (svgView = this.mSvgView08ForFod) != null) {
                    svgView.reveal(z);
                    return;
                }
                return;
            case 9:
                SvgView svgView2 = this.mSvgView09;
                if (svgView2 != null) {
                    svgView2.reveal(z);
                    return;
                }
                return;
            case 10:
                if (this.mSvgView09 != null) {
                    this.mSvgView10.reveal(z);
                    return;
                }
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
