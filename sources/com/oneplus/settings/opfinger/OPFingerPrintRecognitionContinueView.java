package com.oneplus.settings.opfinger;

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

public class OPFingerPrintRecognitionContinueView extends FrameLayout {
    private Context mContext;
    private FrameLayout mFingerPrintView;
    private LayoutInflater mLayoutInflater;
    private SvgView mSvgView11;
    private SvgView mSvgView12;
    private SvgView mSvgView13;
    private SvgView mSvgView14;
    private SvgView mSvgView15;
    private SvgView mSvgView16;
    private SvgView mSvgView17;
    private SvgView mSvgView18;
    private SvgView mSvgView19;
    private SvgView mSvgView20;

    public OPFingerPrintRecognitionContinueView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }

    public OPFingerPrintRecognitionContinueView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public OPFingerPrintRecognitionContinueView(Context context) {
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
        FrameLayout frameLayout = this.mFingerPrintView;
        if (frameLayout != null) {
            frameLayout.setBackgroundDrawable(drawable);
        }
    }

    public void initSvgView(Context context, FrameLayout frameLayout) {
        if (OPUtils.isSupportCustomFingerprint() && OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            this.mSvgView11 = getSvgView(C0016R$raw.opfinger_fod_anim_11, frameLayout);
            this.mSvgView12 = getSvgView(C0016R$raw.opfinger_fod_anim_12, frameLayout);
            this.mSvgView13 = getSvgView(C0016R$raw.opfinger_fod_anim_13, frameLayout);
            this.mSvgView14 = getSvgView(C0016R$raw.opfinger_fod_anim_14, frameLayout);
            this.mSvgView15 = getSvgView(C0016R$raw.opfinger_fod_anim_15, frameLayout);
            this.mSvgView16 = getSvgView(C0016R$raw.opfinger_fod_anim_16, frameLayout);
            this.mSvgView17 = getSvgView(C0016R$raw.opfinger_fod_anim_17, frameLayout);
            this.mSvgView18 = getSvgView(C0016R$raw.opfinger_fod_anim_18, frameLayout);
            this.mSvgView19 = getSvgView(C0016R$raw.opfinger_fod_anim_19, frameLayout);
            this.mSvgView20 = getSvgView(C0016R$raw.opfinger_fod_anim_20, frameLayout);
        } else if (OPUtils.isSupportCustomFingerprint() && !OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            this.mSvgView11 = getSvgView(C0016R$raw.opfinger_anim_16_09, frameLayout);
            this.mSvgView12 = getSvgView(C0016R$raw.opfinger_anim_16_10, frameLayout);
            this.mSvgView13 = getSvgView(C0016R$raw.opfinger_anim_16_11, frameLayout);
            this.mSvgView14 = getSvgView(C0016R$raw.opfinger_anim_16_12, frameLayout);
            this.mSvgView15 = getSvgView(C0016R$raw.opfinger_anim_17_13, frameLayout);
            this.mSvgView16 = getSvgView(C0016R$raw.opfinger_anim_17_14, frameLayout);
            this.mSvgView17 = getSvgView(C0016R$raw.opfinger_anim_17_15, frameLayout);
            this.mSvgView18 = getSvgView(C0016R$raw.opfinger_anim_17_16, frameLayout);
            this.mSvgView19 = getSvgView(C0016R$raw.opfinger_anim_17_17, frameLayout);
        } else if (!OPUtils.isSurportBackFingerprint(context) || OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            this.mSvgView11 = getSvgView(C0016R$raw.opfinger_anim_11, frameLayout);
            this.mSvgView12 = getSvgView(C0016R$raw.opfinger_anim_12, frameLayout);
            this.mSvgView13 = getSvgView(C0016R$raw.opfinger_anim_13, frameLayout);
            this.mSvgView14 = getSvgView(C0016R$raw.opfinger_anim_14, frameLayout);
            this.mSvgView15 = getSvgView(C0016R$raw.opfinger_anim_15, frameLayout);
            this.mSvgView16 = getSvgView(C0016R$raw.opfinger_anim_16, frameLayout);
            this.mSvgView17 = getSvgView(C0016R$raw.opfinger_anim_17, frameLayout);
            this.mSvgView18 = getSvgView(C0016R$raw.opfinger_anim_18, frameLayout);
            this.mSvgView19 = getSvgView(C0016R$raw.opfinger_anim_19, frameLayout);
            this.mSvgView20 = getSvgView(C0016R$raw.opfinger_anim_20, frameLayout);
        } else if (OPUtils.isFingerprintNeedEnrollTime16(context)) {
            this.mSvgView11 = getSvgView(C0016R$raw.opfinger_anim_16_09, frameLayout);
            this.mSvgView12 = getSvgView(C0016R$raw.opfinger_anim_16_10, frameLayout);
            this.mSvgView13 = getSvgView(C0016R$raw.opfinger_anim_16_11, frameLayout);
            this.mSvgView14 = getSvgView(C0016R$raw.opfinger_anim_16_12, frameLayout);
            this.mSvgView15 = getSvgView(C0016R$raw.opfinger_anim_16_13, frameLayout);
            this.mSvgView16 = getSvgView(C0016R$raw.opfinger_anim_16_14, frameLayout);
            this.mSvgView17 = getSvgView(C0016R$raw.opfinger_anim_16_15, frameLayout);
            this.mSvgView18 = getSvgView(C0016R$raw.opfinger_anim_16_16, frameLayout);
        } else {
            this.mSvgView11 = getSvgView(C0016R$raw.opfinger_anim_17801_09, frameLayout);
            this.mSvgView12 = getSvgView(C0016R$raw.opfinger_anim_17801_10, frameLayout);
            this.mSvgView13 = getSvgView(C0016R$raw.opfinger_anim_17801_11, frameLayout);
            this.mSvgView14 = getSvgView(C0016R$raw.opfinger_anim_17801_12, frameLayout);
        }
        addView(this.mSvgView11);
        addView(this.mSvgView12);
        addView(this.mSvgView13);
        addView(this.mSvgView14);
        if (OPUtils.isSupportCustomFingerprint() && OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            addView(this.mSvgView15);
            addView(this.mSvgView16);
            addView(this.mSvgView17);
            addView(this.mSvgView18);
            addView(this.mSvgView19);
            addView(this.mSvgView20);
        } else if (OPUtils.isSupportCustomFingerprint() && !OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            addView(this.mSvgView15);
            addView(this.mSvgView16);
            addView(this.mSvgView17);
            addView(this.mSvgView18);
            addView(this.mSvgView19);
        } else if (OPUtils.isFingerprintNeedEnrollTime16(context) && !OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            addView(this.mSvgView15);
            addView(this.mSvgView16);
            addView(this.mSvgView17);
            addView(this.mSvgView18);
        } else if (!OPUtils.isSurportBackFingerprint(context) || OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            addView(this.mSvgView15);
            addView(this.mSvgView16);
            addView(this.mSvgView17);
            addView(this.mSvgView18);
            addView(this.mSvgView19);
            addView(this.mSvgView20);
        }
    }

    private SvgView getSvgView(int i, FrameLayout frameLayout) {
        SvgView svgView = (SvgView) this.mLayoutInflater.inflate(C0012R$layout.op_finger_input_item_svg, (ViewGroup) frameLayout, false);
        svgView.setSvgResource(i);
        return svgView;
    }

    public void resetWithoutAnimation() {
        this.mSvgView11.resetWithoutAnimation();
        this.mSvgView12.resetWithoutAnimation();
        this.mSvgView13.resetWithoutAnimation();
        this.mSvgView14.resetWithoutAnimation();
        if (OPUtils.isSupportCustomFingerprint() && OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            this.mSvgView15.resetWithoutAnimation();
            this.mSvgView16.resetWithoutAnimation();
            this.mSvgView17.resetWithoutAnimation();
            this.mSvgView18.resetWithoutAnimation();
            this.mSvgView19.resetWithoutAnimation();
            this.mSvgView20.resetWithoutAnimation();
        } else if (OPUtils.isSupportCustomFingerprint() && !OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            this.mSvgView15.revealWithoutAnimation();
            this.mSvgView16.revealWithoutAnimation();
            this.mSvgView17.revealWithoutAnimation();
            this.mSvgView18.revealWithoutAnimation();
            this.mSvgView19.resetWithoutAnimation();
        } else if (OPUtils.isFingerprintNeedEnrollTime16(this.mContext) && !OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            this.mSvgView15.revealWithoutAnimation();
            this.mSvgView16.revealWithoutAnimation();
            this.mSvgView17.revealWithoutAnimation();
            this.mSvgView18.revealWithoutAnimation();
        } else if (!OPUtils.isSurportBackFingerprint(this.mContext) || OPUtils.isFingerprintNeedEnrollTime20(this.mContext)) {
            this.mSvgView15.resetWithoutAnimation();
            this.mSvgView16.resetWithoutAnimation();
            this.mSvgView17.resetWithoutAnimation();
            this.mSvgView18.resetWithoutAnimation();
            this.mSvgView19.resetWithoutAnimation();
            this.mSvgView20.resetWithoutAnimation();
        }
    }

    public void doRecognitionByCount(int i, int i2, boolean z) {
        if (OPUtils.getFingerprintScaleAnimStep(this.mContext) == 8) {
            i += 2;
        }
        switch (i) {
            case 11:
                this.mSvgView11.reveal(z);
                return;
            case 12:
                this.mSvgView12.reveal(z);
                return;
            case 13:
                this.mSvgView13.reveal(z);
                return;
            case 14:
                this.mSvgView14.reveal(z);
                return;
            case 15:
                SvgView svgView = this.mSvgView15;
                if (svgView != null) {
                    svgView.reveal(z);
                    return;
                }
                return;
            case 16:
                SvgView svgView2 = this.mSvgView16;
                if (svgView2 != null) {
                    svgView2.reveal(z);
                    return;
                }
                return;
            case 17:
                SvgView svgView3 = this.mSvgView17;
                if (svgView3 != null) {
                    svgView3.reveal(z);
                    return;
                }
                return;
            case 18:
                if (i2 >= 100) {
                    SvgView svgView4 = this.mSvgView18;
                    if (svgView4 != null) {
                        svgView4.reveal(z);
                    }
                    SvgView svgView5 = this.mSvgView19;
                    if (svgView5 != null) {
                        svgView5.reveal(z);
                    }
                    SvgView svgView6 = this.mSvgView20;
                    if (svgView6 != null) {
                        svgView6.reveal(z);
                        return;
                    }
                    return;
                }
                SvgView svgView7 = this.mSvgView18;
                if (svgView7 != null) {
                    svgView7.reveal(z);
                    return;
                }
                return;
            case 19:
                if (i2 >= 100) {
                    SvgView svgView8 = this.mSvgView19;
                    if (svgView8 != null) {
                        svgView8.reveal(z);
                    }
                    SvgView svgView9 = this.mSvgView20;
                    if (svgView9 != null) {
                        svgView9.reveal(z);
                        return;
                    }
                    return;
                }
                SvgView svgView10 = this.mSvgView19;
                if (svgView10 != null) {
                    svgView10.reveal(z);
                    return;
                }
                return;
            case 20:
                SvgView svgView11 = this.mSvgView20;
                if (svgView11 != null) {
                    svgView11.reveal(z);
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
