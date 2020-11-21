package com.oneplus.settings.system;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.airbnb.lottie.LottieAnimationView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.oneplus.settings.utils.OPUtils;

public class OPAutoAwesomeBatteryLottieAnimPreference extends Preference implements View.OnClickListener {
    private LottieAnimationView anim_res;
    private ImageView img_play;
    private Context mContext;
    private OnPreferenceViewClickListener mListener;
    private int resid = C0012R$layout.op_preference_auto_awesome_battery_lottie;

    public interface OnPreferenceViewClickListener {
        void onPreferenceViewClick(View view);
    }

    public OPAutoAwesomeBatteryLottieAnimPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        new Handler() {
            /* class com.oneplus.settings.system.OPAutoAwesomeBatteryLottieAnimPreference.AnonymousClass1 */

            public void handleMessage(Message message) {
                if (message.what == 0) {
                    OPAutoAwesomeBatteryLottieAnimPreference.this.startOrStopAnim();
                }
            }
        };
        initViews(context);
    }

    public OPAutoAwesomeBatteryLottieAnimPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        new Handler() {
            /* class com.oneplus.settings.system.OPAutoAwesomeBatteryLottieAnimPreference.AnonymousClass1 */

            public void handleMessage(Message message) {
                if (message.what == 0) {
                    OPAutoAwesomeBatteryLottieAnimPreference.this.startOrStopAnim();
                }
            }
        };
        initViews(context);
    }

    public OPAutoAwesomeBatteryLottieAnimPreference(Context context) {
        super(context);
        new Handler() {
            /* class com.oneplus.settings.system.OPAutoAwesomeBatteryLottieAnimPreference.AnonymousClass1 */

            public void handleMessage(Message message) {
                if (message.what == 0) {
                    OPAutoAwesomeBatteryLottieAnimPreference.this.startOrStopAnim();
                }
            }
        };
        initViews(context);
    }

    private void initViews(Context context) {
        this.mContext = context;
        setLayoutResource(this.resid);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.img_play = (ImageView) preferenceViewHolder.findViewById(C0010R$id.img_play);
        this.anim_res = (LottieAnimationView) preferenceViewHolder.findViewById(C0010R$id.anim_res);
        if (OPUtils.isBlackModeOn(this.mContext.getContentResolver())) {
            this.anim_res.setAnimation("auto_awesome_battery_dark.json");
        } else {
            this.anim_res.setAnimation("auto_awesome_battery_light.json");
        }
        this.anim_res.loop(true);
        startAnim();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void startOrStopAnim() {
        if (this.anim_res.isAnimating()) {
            stopAnim();
        } else {
            startAnim();
        }
    }

    private void startAnim() {
        this.img_play.setVisibility(8);
        this.anim_res.resumeAnimation();
    }

    public void stopAnim() {
        LottieAnimationView lottieAnimationView = this.anim_res;
        if (lottieAnimationView != null) {
            lottieAnimationView.pauseAnimation();
        }
        ImageView imageView = this.img_play;
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
