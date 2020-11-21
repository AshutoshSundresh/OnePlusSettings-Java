package com.oneplus.settings.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.oneplus.settings.utils.OPUtils;

public class OPGamingModeNotificationWayCategory extends Preference {
    private ContentResolver mContentResolver;
    private Context mContext;
    private boolean mHasInited = false;
    private int mLayoutResId = C0012R$layout.op_gaming_mode_notification_way_instructions_category;
    private TextView mNoficationWaySummary;
    private ImageView mShieldingNotificationImageView;
    private LottieAnimationView mSuspensionNoticeAnim;
    private LottieAnimationView mWeakTextRemindingAnim;

    public OPGamingModeNotificationWayCategory(Context context) {
        super(context);
        initViews(context);
    }

    public OPGamingModeNotificationWayCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPGamingModeNotificationWayCategory(Context context, AttributeSet attributeSet, int i) {
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
        preferenceViewHolder.itemView.setClickable(false);
        this.mSuspensionNoticeAnim = (LottieAnimationView) preferenceViewHolder.findViewById(C0010R$id.suspension_notice_anim);
        this.mWeakTextRemindingAnim = (LottieAnimationView) preferenceViewHolder.findViewById(C0010R$id.weak_text_reminding_anim);
        this.mShieldingNotificationImageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.shielding_notificationimageview);
        this.mNoficationWaySummary = (TextView) preferenceViewHolder.findViewById(C0010R$id.nofication_way_summary);
        if (OPUtils.isBlackModeOn(this.mContentResolver)) {
            this.mSuspensionNoticeAnim.setAnimation("op_suspension_notice_anim_dark.json");
        } else {
            this.mSuspensionNoticeAnim.setAnimation("op_suspension_notice_anim_light.json");
        }
        if (OPUtils.isBlackModeOn(this.mContentResolver)) {
            this.mWeakTextRemindingAnim.setAnimation("op_weak_text_reminding_anim_dark.json");
        } else {
            this.mWeakTextRemindingAnim.setAnimation("op_weak_text_reminding_anim_light.json");
        }
        if (OPUtils.isBlackModeOn(this.mContentResolver)) {
            this.mShieldingNotificationImageView.setImageDrawable(this.mContext.getResources().getDrawable(C0008R$drawable.op_shielding_notification_dark));
        } else {
            this.mShieldingNotificationImageView.setImageDrawable(this.mContext.getResources().getDrawable(C0008R$drawable.op_shielding_notification_light));
        }
        this.mWeakTextRemindingAnim.loop(true);
        this.mWeakTextRemindingAnim.playAnimation();
        preferenceViewHolder.setDividerAllowedBelow(false);
        this.mHasInited = true;
        startAnim();
    }

    public void startAnim() {
        if (this.mHasInited) {
            setAnimTypes(Settings.System.getIntForUser(this.mContext.getContentResolver(), "game_mode_block_notification", 0, -2));
        }
    }

    public void stopAnim() {
        LottieAnimationView lottieAnimationView = this.mSuspensionNoticeAnim;
        if (lottieAnimationView != null) {
            lottieAnimationView.cancelAnimation();
        }
        LottieAnimationView lottieAnimationView2 = this.mWeakTextRemindingAnim;
        if (lottieAnimationView2 != null) {
            lottieAnimationView2.cancelAnimation();
        }
    }

    public void releaseAnim() {
        LottieAnimationView lottieAnimationView = this.mSuspensionNoticeAnim;
        if (lottieAnimationView != null) {
            lottieAnimationView.cancelAnimation();
        }
        LottieAnimationView lottieAnimationView2 = this.mWeakTextRemindingAnim;
        if (lottieAnimationView2 != null) {
            lottieAnimationView2.cancelAnimation();
        }
        this.mSuspensionNoticeAnim = null;
        this.mWeakTextRemindingAnim = null;
    }

    public void setAnimTypes(int i) {
        if (this.mHasInited) {
            stopAnim();
            if (i == 0) {
                this.mWeakTextRemindingAnim.setVisibility(8);
                this.mShieldingNotificationImageView.setVisibility(8);
                this.mSuspensionNoticeAnim.setVisibility(0);
                this.mSuspensionNoticeAnim.playAnimation();
                this.mNoficationWaySummary.setText(C0017R$string.oneplus_suspension_notice_summary);
            } else if (i == 1) {
                this.mSuspensionNoticeAnim.setVisibility(8);
                this.mWeakTextRemindingAnim.setVisibility(8);
                this.mShieldingNotificationImageView.setVisibility(0);
                this.mNoficationWaySummary.setText(C0017R$string.oneplus_shielding_notification_summary);
            } else if (i == 2) {
                this.mSuspensionNoticeAnim.setVisibility(8);
                this.mShieldingNotificationImageView.setVisibility(8);
                this.mWeakTextRemindingAnim.setVisibility(0);
                this.mWeakTextRemindingAnim.playAnimation();
                this.mNoficationWaySummary.setText(C0017R$string.oneplus_weak_text_reminding_summary);
            }
        }
    }
}
