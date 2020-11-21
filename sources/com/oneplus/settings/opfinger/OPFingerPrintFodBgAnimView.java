package com.oneplus.settings.opfinger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class OPFingerPrintFodBgAnimView extends FrameLayout {
    private FrameLayout mFodBgView;
    private LayoutInflater mLayoutInflater;
    private ImageView mPath_1;
    private ImageView mPath_10;
    private ImageView mPath_11;
    private ImageView mPath_2;
    private ImageView mPath_3;
    private ImageView mPath_4;
    private ImageView mPath_5;
    private ImageView mPath_6;
    private ImageView mPath_7;
    private ImageView mPath_8;
    private ImageView mPath_9;

    public OPFingerPrintFodBgAnimView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }

    public OPFingerPrintFodBgAnimView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public OPFingerPrintFodBgAnimView(Context context) {
        super(context);
        initView(context);
    }

    public void initView(Context context) {
        LayoutInflater from = LayoutInflater.from(context);
        this.mLayoutInflater = from;
        FrameLayout frameLayout = (FrameLayout) from.inflate(C0012R$layout.op_finger_enroll_fod_bg_anim_view, this);
        this.mFodBgView = frameLayout;
        this.mPath_1 = (ImageView) frameLayout.findViewById(C0010R$id.opfinger_fod_anim_bg_01);
        this.mPath_2 = (ImageView) this.mFodBgView.findViewById(C0010R$id.opfinger_fod_anim_bg_02);
        this.mPath_3 = (ImageView) this.mFodBgView.findViewById(C0010R$id.opfinger_fod_anim_bg_03);
        this.mPath_4 = (ImageView) this.mFodBgView.findViewById(C0010R$id.opfinger_fod_anim_bg_04);
        this.mPath_5 = (ImageView) this.mFodBgView.findViewById(C0010R$id.opfinger_fod_anim_bg_05);
        this.mPath_6 = (ImageView) this.mFodBgView.findViewById(C0010R$id.opfinger_fod_anim_bg_06);
        this.mPath_7 = (ImageView) this.mFodBgView.findViewById(C0010R$id.opfinger_fod_anim_bg_07);
        this.mPath_8 = (ImageView) this.mFodBgView.findViewById(C0010R$id.opfinger_fod_anim_bg_08);
        this.mPath_9 = (ImageView) this.mFodBgView.findViewById(C0010R$id.opfinger_fod_anim_bg_09);
        this.mPath_10 = (ImageView) this.mFodBgView.findViewById(C0010R$id.opfinger_fod_anim_bg_10);
        this.mPath_11 = (ImageView) this.mFodBgView.findViewById(C0010R$id.opfinger_fod_anim_bg_11);
        initBgAnimView(context, this.mFodBgView);
    }

    public void initBgAnimView(Context context, FrameLayout frameLayout) {
        setCenterVisible(true);
    }

    public void setCenterVisible(boolean z) {
        int i = 0;
        this.mPath_1.setVisibility(z ? 0 : 8);
        this.mPath_2.setVisibility(z ? 0 : 8);
        this.mPath_3.setVisibility(z ? 0 : 8);
        ImageView imageView = this.mPath_4;
        if (!z) {
            i = 8;
        }
        imageView.setVisibility(i);
        this.mPath_5.setVisibility(8);
        this.mPath_6.setVisibility(8);
        this.mPath_7.setVisibility(8);
        this.mPath_8.setVisibility(8);
        this.mPath_9.setVisibility(8);
        this.mPath_10.setVisibility(8);
        this.mPath_11.setVisibility(8);
    }

    public void setEdgeVisible(boolean z) {
        int i = 8;
        this.mPath_1.setVisibility(8);
        this.mPath_2.setVisibility(8);
        this.mPath_3.setVisibility(8);
        this.mPath_4.setVisibility(8);
        this.mPath_5.setVisibility(z ? 0 : 8);
        this.mPath_6.setVisibility(z ? 0 : 8);
        this.mPath_7.setVisibility(z ? 0 : 8);
        this.mPath_8.setVisibility(z ? 0 : 8);
        this.mPath_9.setVisibility(z ? 0 : 8);
        this.mPath_10.setVisibility(z ? 0 : 8);
        ImageView imageView = this.mPath_11;
        if (z) {
            i = 0;
        }
        imageView.setVisibility(i);
    }

    public void startTouchDownAnim() {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(300);
        scaleAnimation.setStartOffset(0);
        animationSet.addAnimation(scaleAnimation);
        ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation2.setFillAfter(false);
        scaleAnimation2.setDuration(150);
        scaleAnimation2.setStartOffset(542);
        animationSet.addAnimation(scaleAnimation2);
        this.mPath_1.startAnimation(animationSet);
        this.mPath_5.startAnimation(animationSet);
        AnimationSet animationSet2 = new AnimationSet(true);
        ScaleAnimation scaleAnimation3 = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation3.setFillAfter(true);
        scaleAnimation3.setDuration(300);
        scaleAnimation3.setStartOffset(32);
        animationSet2.addAnimation(scaleAnimation3);
        ScaleAnimation scaleAnimation4 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation4.setFillAfter(false);
        scaleAnimation4.setDuration(150);
        scaleAnimation4.setStartOffset(510);
        animationSet2.addAnimation(scaleAnimation4);
        this.mPath_2.startAnimation(animationSet2);
        this.mPath_6.startAnimation(animationSet2);
        AnimationSet animationSet3 = new AnimationSet(true);
        ScaleAnimation scaleAnimation5 = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation5.setFillAfter(true);
        scaleAnimation5.setDuration(300);
        scaleAnimation5.setStartOffset(64);
        animationSet3.addAnimation(scaleAnimation5);
        ScaleAnimation scaleAnimation6 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation6.setFillAfter(false);
        scaleAnimation6.setDuration(150);
        scaleAnimation6.setStartOffset(478);
        animationSet3.addAnimation(scaleAnimation6);
        this.mPath_3.startAnimation(animationSet3);
        this.mPath_7.startAnimation(animationSet3);
        AnimationSet animationSet4 = new AnimationSet(true);
        ScaleAnimation scaleAnimation7 = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation7.setFillAfter(true);
        scaleAnimation7.setDuration(300);
        scaleAnimation7.setStartOffset(96);
        animationSet4.addAnimation(scaleAnimation7);
        ScaleAnimation scaleAnimation8 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation8.setFillAfter(false);
        scaleAnimation8.setDuration(150);
        scaleAnimation8.setStartOffset(446);
        animationSet4.addAnimation(scaleAnimation8);
        this.mPath_4.startAnimation(animationSet4);
        this.mPath_8.startAnimation(animationSet4);
        AnimationSet animationSet5 = new AnimationSet(true);
        ScaleAnimation scaleAnimation9 = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation9.setFillAfter(true);
        scaleAnimation9.setDuration(300);
        scaleAnimation9.setStartOffset(128);
        animationSet5.addAnimation(scaleAnimation9);
        ScaleAnimation scaleAnimation10 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation10.setFillAfter(false);
        scaleAnimation10.setDuration(150);
        scaleAnimation10.setStartOffset(414);
        animationSet5.addAnimation(scaleAnimation10);
        this.mPath_9.startAnimation(animationSet5);
        AnimationSet animationSet6 = new AnimationSet(true);
        ScaleAnimation scaleAnimation11 = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation11.setFillAfter(true);
        scaleAnimation11.setDuration(300);
        scaleAnimation11.setStartOffset(160);
        animationSet6.addAnimation(scaleAnimation11);
        ScaleAnimation scaleAnimation12 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation12.setFillAfter(false);
        scaleAnimation12.setDuration(150);
        scaleAnimation12.setStartOffset(382);
        animationSet6.addAnimation(scaleAnimation12);
        this.mPath_10.startAnimation(animationSet6);
        AnimationSet animationSet7 = new AnimationSet(true);
        ScaleAnimation scaleAnimation13 = new ScaleAnimation(1.0f, 0.94f, 1.0f, 0.94f, 2, 0.5f, 2, 0.5f);
        scaleAnimation13.setFillAfter(true);
        scaleAnimation13.setDuration(300);
        scaleAnimation13.setStartOffset(192);
        animationSet7.addAnimation(scaleAnimation13);
        ScaleAnimation scaleAnimation14 = new ScaleAnimation(1.0f, 1.0638298f, 1.0f, 1.0638298f, 2, 0.5f, 2, 0.5f);
        scaleAnimation14.setFillAfter(false);
        scaleAnimation14.setDuration(150);
        scaleAnimation14.setStartOffset(350);
        animationSet7.addAnimation(scaleAnimation14);
        this.mPath_11.startAnimation(animationSet7);
    }
}
