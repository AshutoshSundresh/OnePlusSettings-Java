package com.oneplus.settings.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.oneplus.settings.utils.OPUtils;

public class OPFaceUnlockModeLottieViewCategory extends Preference {
    private ContentResolver mContentResolver;
    private Context mContext;
    private boolean mHasInited = false;
    private int mLayoutResId = C0012R$layout.op_single_lottie_instructions_category;
    private LottieAnimationView mLottieAnim;

    public OPFaceUnlockModeLottieViewCategory(Context context) {
        super(context);
        initViews(context);
    }

    public OPFaceUnlockModeLottieViewCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPFaceUnlockModeLottieViewCategory(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        setLayoutResource(this.mLayoutResId);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mLottieAnim = (LottieAnimationView) preferenceViewHolder.findViewById(C0010R$id.op_single_lottie_view);
        preferenceViewHolder.setDividerAllowedBelow(false);
        this.mHasInited = true;
        startAnim();
    }

    public int getUnlockMode() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "oneplus_face_unlock_powerkey_recognize_enable", 0);
    }

    private void setAnimationResource() {
        if (this.mHasInited) {
            if (getUnlockMode() == 0) {
                if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                    this.mLottieAnim.setAnimation("op_face_unlock_by_swipe_up_dark.json");
                } else {
                    this.mLottieAnim.setAnimation("op_face_unlock_by_swipe_up_light.json");
                }
            } else if (OPUtils.isBlackModeOn(this.mContentResolver)) {
                this.mLottieAnim.setAnimation("op_face_unlock_by_use_power_key_dark.json");
            } else {
                this.mLottieAnim.setAnimation("op_face_unlock_by_use_power_key_light.json");
            }
        }
    }

    public void startAnim() {
        if (this.mHasInited) {
            setViewType(getUnlockMode());
        }
    }

    public void setViewType(int i) {
        if (this.mHasInited) {
            stopAnim();
            setAnimationResource();
            this.mLottieAnim.playAnimation();
        }
    }

    public void stopAnim() {
        if (this.mHasInited) {
            this.mLottieAnim.cancelAnimation();
        }
    }

    public void releaseAnim() {
        if (this.mHasInited) {
            this.mLottieAnim.cancelAnimation();
            this.mLottieAnim = null;
        }
    }
}
