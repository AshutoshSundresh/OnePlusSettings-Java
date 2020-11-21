package com.oneplus.settings.quickpay;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.oneplus.common.ReflectUtil;
import com.oneplus.settings.utils.OPUtils;

public class QuickPayLottieAnimPreference extends Preference implements View.OnClickListener {
    private String animFile = "op_quickpay_instroduction_anim_enchilada_white.json";
    private LottieAnimationView anim_quickpay_instructions;
    private ImageView img_quickpay_play;
    private Context mContext;
    Handler mHandler = new Handler() {
        /* class com.oneplus.settings.quickpay.QuickPayLottieAnimPreference.AnonymousClass1 */

        public void handleMessage(Message message) {
            if (message.what == 0) {
                QuickPayLottieAnimPreference.this.startOrStopAnim();
            }
        }
    };
    private OnPreferenceViewClickListener mListener;
    private int resid = C0012R$layout.op_quickpay_instructions_lottie;

    public interface OnPreferenceViewClickListener {
        void onPreferenceViewClick(View view);
    }

    private void setAnimFile(String str) {
        this.animFile = str;
    }

    public QuickPayLottieAnimPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    public QuickPayLottieAnimPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public QuickPayLottieAnimPreference(Context context) {
        super(context);
        initViews(context);
    }

    private void initViews(Context context) {
        this.mContext = context;
        setLayoutResource(this.resid);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.img_quickpay_play = (ImageView) preferenceViewHolder.findViewById(C0010R$id.img_quickpay_play);
        this.anim_quickpay_instructions = (LottieAnimationView) preferenceViewHolder.findViewById(C0010R$id.anim_quickpay_instructions);
        this.img_quickpay_play.setOnClickListener(this);
        this.img_quickpay_play.setEnabled(false);
        this.anim_quickpay_instructions.setOnClickListener(this);
        boolean isBlackModeOn = OPUtils.isBlackModeOn(this.mContext.getContentResolver());
        if (OPUtils.isSupportCustomFingerprint()) {
            if (isBlackModeOn) {
                setAnimFile("op_quickpay_instroduction_anim_custom_black.json");
            } else {
                setAnimFile("op_quickpay_instroduction_anim_custom_white.json");
            }
        } else if (ReflectUtil.isFeatureSupported("OP_FEATURE_SETTINGS_QUICKPAY_ANIM_FOR_ENCHILADA")) {
            if (isBlackModeOn) {
                setAnimFile("op_quickpay_instroduction_anim_enchilada_black.json");
            } else {
                setAnimFile("op_quickpay_instroduction_anim_enchilada_white.json");
            }
        } else if (OPUtils.isSurportBackFingerprint(this.mContext)) {
            if (isBlackModeOn) {
                setAnimFile("op_quickpay_instroduction_anim_dumpling_black.json");
            } else {
                setAnimFile("op_quickpay_instroduction_anim_dumpling_white.json");
            }
        } else if (isBlackModeOn) {
            setAnimFile("op_quickpay_instroduction_anim_cheeseburger_black.json");
        } else {
            setAnimFile("op_quickpay_instroduction_anim_cheeseburger_white.json");
        }
        LottieComposition.Factory.fromAssetFileName(this.mContext, this.animFile, new OnCompositionLoadedListener() {
            /* class com.oneplus.settings.quickpay.QuickPayLottieAnimPreference.AnonymousClass2 */

            @Override // com.airbnb.lottie.OnCompositionLoadedListener
            public void onCompositionLoaded(LottieComposition lottieComposition) {
                QuickPayLottieAnimPreference.this.anim_quickpay_instructions.setComposition(lottieComposition);
                QuickPayLottieAnimPreference.this.anim_quickpay_instructions.setProgress(0.1f);
                QuickPayLottieAnimPreference.this.img_quickpay_play.setEnabled(true);
            }
        });
    }

    public void setViewOnClick(OnPreferenceViewClickListener onPreferenceViewClickListener) {
        this.mListener = onPreferenceViewClickListener;
    }

    public void playOrStopAnim() {
        this.mHandler.removeMessages(0);
        this.mHandler.sendEmptyMessage(0);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startOrStopAnim() {
        if (this.anim_quickpay_instructions.isAnimating()) {
            stopAnim();
        } else {
            startAnim();
        }
    }

    private void startAnim() {
        this.img_quickpay_play.setVisibility(8);
        this.anim_quickpay_instructions.resumeAnimation();
    }

    public void stopAnim() {
        LottieAnimationView lottieAnimationView = this.anim_quickpay_instructions;
        if (lottieAnimationView != null) {
            lottieAnimationView.pauseAnimation();
        }
        ImageView imageView = this.img_quickpay_play;
        if (imageView != null) {
            imageView.setVisibility(0);
        }
    }

    public void onClick(View view) {
        OnPreferenceViewClickListener onPreferenceViewClickListener = this.mListener;
        if (onPreferenceViewClickListener != null) {
            onPreferenceViewClickListener.onPreferenceViewClick(view);
        }
    }
}
