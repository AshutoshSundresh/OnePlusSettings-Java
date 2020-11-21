package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.oneplus.settings.utils.OPUtils;

public class NotificationLottiePreference extends Preference {
    private LottieAnimationView mNotificationView;

    public NotificationLottiePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView();
    }

    public NotificationLottiePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public NotificationLottiePreference(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        setLayoutResource(C0012R$layout.op_notification_lottie);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mNotificationView = (LottieAnimationView) preferenceViewHolder.findViewById(C0010R$id.anim_notification_lottie);
        if (OPUtils.isBlackModeOn(getContext().getContentResolver())) {
            this.mNotificationView.setAnimation("op_notification_lottie_dark.json");
        } else {
            this.mNotificationView.setAnimation("op_notification_lottie_light.json");
        }
        this.mNotificationView.playAnimation();
    }
}
