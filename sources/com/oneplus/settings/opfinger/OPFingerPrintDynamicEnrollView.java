package com.oneplus.settings.opfinger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.utils.OPUtils;

public class OPFingerPrintDynamicEnrollView extends RelativeLayout {
    private Context mContext;
    private LottieAnimationView mEnrollCompletedAnim;
    private Button mOPFingerInputCompletedComfirmBtn;
    private TextView mOPFingerInputTipsSubTitle;
    private TextView mOPFingerInputTipsTitle;
    private TextView mOPFingerInputTipsWarning;
    private OPFingerPrintFodBgAnimView mOPFingerPrintFodBgAnimView;
    private OPFingerPrintRecognitionContinueLottieView mOPFingerPrintRecognitionContinueLottieView;
    private OPFingerPrintRecognitionLottieView mOPFingerPrintRecognitionLottieView;
    public OnOPFingerComfirmListener mOnOPFingerComfirmListener;
    private View mView;

    public OPFingerPrintDynamicEnrollView(Context context) {
        super(context);
        initViews(context);
    }

    public OPFingerPrintDynamicEnrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPFingerPrintDynamicEnrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    public void setTitleView(TextView textView) {
        this.mOPFingerInputTipsTitle = textView;
    }

    public void setSubTitleView(TextView textView) {
        this.mOPFingerInputTipsSubTitle = textView;
    }

    public void hideHeaderView() {
        ((ImageView) findViewById(C0010R$id.setup_title_view_bg)).setVisibility(8);
        ((ImageView) findViewById(C0010R$id.setup_title_view_bg_shadow)).setVisibility(8);
        ((TextView) this.mView.findViewById(C0010R$id.opfinger_input_tips_title_tv)).setVisibility(8);
        ((TextView) this.mView.findViewById(C0010R$id.opfinger_input_tips_subtitle_tv)).setVisibility(8);
    }

    private void initViews(Context context) {
        this.mContext = context;
        View inflate = LayoutInflater.from(context).inflate(C0012R$layout.op_fingerprint_dynamic_input_category, this);
        this.mView = inflate;
        this.mOPFingerPrintFodBgAnimView = (OPFingerPrintFodBgAnimView) inflate.findViewById(C0010R$id.op_finger_fod_bg_view);
        this.mOPFingerPrintRecognitionLottieView = (OPFingerPrintRecognitionLottieView) this.mView.findViewById(C0010R$id.op_finger_recognition_lottie_view);
        this.mOPFingerPrintRecognitionContinueLottieView = (OPFingerPrintRecognitionContinueLottieView) this.mView.findViewById(C0010R$id.op_finger_recognition_continue_lottie_view);
        this.mOPFingerInputTipsTitle = (TextView) this.mView.findViewById(C0010R$id.opfinger_input_tips_title_tv);
        this.mOPFingerInputTipsSubTitle = (TextView) this.mView.findViewById(C0010R$id.opfinger_input_tips_subtitle_tv);
        this.mOPFingerInputTipsWarning = (TextView) this.mView.findViewById(C0010R$id.opfinger_input_tips_warning);
        Button button = (Button) this.mView.findViewById(C0010R$id.opfinger_input_completed_comfirm_btn);
        this.mOPFingerInputCompletedComfirmBtn = button;
        button.setOnClickListener(new View.OnClickListener() {
            /* class com.oneplus.settings.opfinger.$$Lambda$OPFingerPrintDynamicEnrollView$eKKvrHSp4Wm1p9Ark3E310fwwnc */

            public final void onClick(View view) {
                OPFingerPrintDynamicEnrollView.this.lambda$initViews$0$OPFingerPrintDynamicEnrollView(view);
            }
        });
        this.mEnrollCompletedAnim = (LottieAnimationView) findViewById(C0010R$id.opfinger_fod_enroll_completed_anim);
        if (!isNeedWaveEffect()) {
            this.mOPFingerPrintFodBgAnimView.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initViews$0 */
    public /* synthetic */ void lambda$initViews$0$OPFingerPrintDynamicEnrollView(View view) {
        OnOPFingerComfirmListener onOPFingerComfirmListener = this.mOnOPFingerComfirmListener;
        if (onOPFingerComfirmListener != null) {
            onOPFingerComfirmListener.onOPFingerComfirmClick();
        }
    }

    public void playEnrollCompletedAnim() {
        OPFingerPrintRecognitionLottieView oPFingerPrintRecognitionLottieView = this.mOPFingerPrintRecognitionLottieView;
        if (oPFingerPrintRecognitionLottieView != null) {
            oPFingerPrintRecognitionLottieView.setVisibility(8);
        }
        OPFingerPrintRecognitionContinueLottieView oPFingerPrintRecognitionContinueLottieView = this.mOPFingerPrintRecognitionContinueLottieView;
        if (oPFingerPrintRecognitionContinueLottieView != null) {
            oPFingerPrintRecognitionContinueLottieView.setVisibility(8);
        }
        LottieAnimationView lottieAnimationView = this.mEnrollCompletedAnim;
        if (lottieAnimationView != null) {
            lottieAnimationView.setVisibility(0);
            this.mEnrollCompletedAnim.playAnimation();
        }
    }

    public void releaseEnrollCompletedAnim() {
        LottieAnimationView lottieAnimationView = this.mEnrollCompletedAnim;
        if (lottieAnimationView != null) {
            lottieAnimationView.cancelAnimation();
            this.mEnrollCompletedAnim = null;
        }
        OPFingerPrintRecognitionLottieView oPFingerPrintRecognitionLottieView = this.mOPFingerPrintRecognitionLottieView;
        if (oPFingerPrintRecognitionLottieView != null) {
            oPFingerPrintRecognitionLottieView.releaseFingerprintLottieAnimation();
        }
        OPFingerPrintRecognitionContinueLottieView oPFingerPrintRecognitionContinueLottieView = this.mOPFingerPrintRecognitionContinueLottieView;
        if (oPFingerPrintRecognitionContinueLottieView != null) {
            oPFingerPrintRecognitionContinueLottieView.releaseFingerprintLottieAnimation();
        }
    }

    public void setCenterVisible(boolean z) {
        if (this.mOPFingerPrintFodBgAnimView != null && isNeedWaveEffect()) {
            this.mOPFingerPrintFodBgAnimView.setCenterVisible(z);
        }
    }

    public void setEdgeVisible(boolean z) {
        if (this.mOPFingerPrintFodBgAnimView != null && isNeedWaveEffect()) {
            this.mOPFingerPrintFodBgAnimView.setEdgeVisible(z);
        }
    }

    public boolean isNeedWaveEffect() {
        return OPUtils.isSupportCustomFingerprint() && OPUtils.isFingerprintNeedEnrollTime20(this.mContext);
    }

    public void setEnrollAnimVisibility(boolean z) {
        OPFingerPrintRecognitionLottieView oPFingerPrintRecognitionLottieView = this.mOPFingerPrintRecognitionLottieView;
        if (oPFingerPrintRecognitionLottieView != null) {
            oPFingerPrintRecognitionLottieView.setVisibility(z ? 0 : 8);
        }
    }

    public void setEnrollAnimBgColor(String str) {
        OPFingerPrintRecognitionLottieView oPFingerPrintRecognitionLottieView = this.mOPFingerPrintRecognitionLottieView;
        if (oPFingerPrintRecognitionLottieView != null) {
            oPFingerPrintRecognitionLottieView.setEnrollAnimBgColor(str);
        }
        OPFingerPrintRecognitionContinueLottieView oPFingerPrintRecognitionContinueLottieView = this.mOPFingerPrintRecognitionContinueLottieView;
        if (oPFingerPrintRecognitionContinueLottieView != null) {
            oPFingerPrintRecognitionContinueLottieView.setEnrollAnimBgColor(str);
        }
    }

    public void setOnOPFingerComfirmListener(OnOPFingerComfirmListener onOPFingerComfirmListener) {
        this.mOnOPFingerComfirmListener = onOPFingerComfirmListener;
    }

    public void hideWarningTips() {
        TextView textView = this.mOPFingerInputTipsWarning;
        if (textView != null) {
            textView.setText("");
            this.mOPFingerInputTipsWarning.setVisibility(4);
        }
    }

    public TextView getWarningTipsView() {
        return this.mOPFingerInputTipsWarning;
    }

    public void setTipsStatusContent(int i) {
        if (i == 1) {
            this.mOPFingerInputTipsTitle.setText(C0017R$string.oneplus_opfinger_input_setting_tips_title);
            this.mOPFingerInputTipsSubTitle.setText(C0017R$string.oneplus_opfinger_input_setting_tips_sub);
        } else if (i == 3) {
            this.mOPFingerInputTipsTitle.setText(C0017R$string.oneplus_opfinger_input_up_title);
            this.mOPFingerInputTipsSubTitle.setText(C0017R$string.oneplus_opfinger_input_up_sub);
        }
    }

    public void setTipsContinueContent() {
        this.mOPFingerInputTipsTitle.setText(C0017R$string.oneplus_opfinger_input_recognize_continue_title);
        this.mOPFingerInputTipsSubTitle.setText(C0017R$string.oneplus_opfinger_input_recognize_continue_sub);
        this.mOPFingerInputCompletedComfirmBtn.setVisibility(8);
    }

    public void showContinueView() {
        this.mOPFingerPrintRecognitionLottieView.setVisibility(8);
        this.mOPFingerPrintRecognitionContinueLottieView.setVisibility(0);
        this.mOPFingerPrintRecognitionContinueLottieView.playContinueAnimation();
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(500);
        animationSet.addAnimation(alphaAnimation);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setDuration(500);
        animationSet.addAnimation(scaleAnimation);
        animationSet.start();
    }

    public void doRecognition(int i, int i2, int i3, boolean z) {
        int fingerprintScaleAnimStep = OPUtils.getFingerprintScaleAnimStep(this.mContext);
        this.mOPFingerInputTipsSubTitle.setText(C0017R$string.oneplus_fingerprint_enroll_summary);
        if (i <= fingerprintScaleAnimStep) {
            OPFingerPrintRecognitionLottieView oPFingerPrintRecognitionLottieView = this.mOPFingerPrintRecognitionLottieView;
            if (oPFingerPrintRecognitionLottieView != null) {
                oPFingerPrintRecognitionLottieView.playAnimationByCount(i, i2, i3, z);
                return;
            }
            return;
        }
        OPFingerPrintRecognitionContinueLottieView oPFingerPrintRecognitionContinueLottieView = this.mOPFingerPrintRecognitionContinueLottieView;
        if (oPFingerPrintRecognitionContinueLottieView != null) {
            oPFingerPrintRecognitionContinueLottieView.playAnimationByCount(i, i2, i3, z);
        }
    }

    public void resetTextAndBtn() {
        int i;
        TextView textView = this.mOPFingerInputTipsTitle;
        if (textView != null) {
            textView.setText(C0017R$string.oneplus_opfinger_input_setting_tips_title);
        }
        if (this.mOPFingerInputTipsSubTitle != null) {
            if (OPUtils.isSupportCustomFingerprint()) {
                i = C0017R$string.oneplus_fingerprint_enroll_summary;
            } else if (OPUtils.isSurportBackFingerprint(this.mContext)) {
                i = C0017R$string.oneplus_opfinger_input_setting_back_tips_sub;
            } else {
                i = C0017R$string.oneplus_opfinger_input_setting_tips_sub;
            }
            this.mOPFingerInputTipsSubTitle.setText(i);
        }
        Button button = this.mOPFingerInputCompletedComfirmBtn;
        if (button != null) {
            button.setVisibility(8);
        }
    }

    public void resetWithoutAnimation() {
        resetTextAndBtn();
        OPFingerPrintRecognitionContinueLottieView oPFingerPrintRecognitionContinueLottieView = this.mOPFingerPrintRecognitionContinueLottieView;
        if (oPFingerPrintRecognitionContinueLottieView != null) {
            oPFingerPrintRecognitionContinueLottieView.setVisibility(8);
        }
    }
}
