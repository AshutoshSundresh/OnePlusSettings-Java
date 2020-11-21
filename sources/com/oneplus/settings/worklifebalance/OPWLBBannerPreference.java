package com.oneplus.settings.worklifebalance;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.internal.PreferenceImageView;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;

public class OPWLBBannerPreference extends Preference {
    private Context mContext;
    private PreferenceImageView mIcon;
    private LinearLayout mLlClickBg;
    private LinearLayout mLlContent;
    private TextView mTvContent;
    private TextView mTvNew;
    private TextView mTvTitle;

    public OPWLBBannerPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initViews(context);
    }

    public OPWLBBannerPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    public OPWLBBannerPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPWLBBannerPreference(Context context) {
        super(context);
        initViews(context);
    }

    private void initViews(Context context) {
        this.mContext = context;
        setLayoutResource(C0012R$layout.op_preference_wlb);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mLlContent = (LinearLayout) preferenceViewHolder.findViewById(C0010R$id.ll_content);
        this.mLlClickBg = (LinearLayout) preferenceViewHolder.findViewById(C0010R$id.ll_click_bg);
        this.mTvTitle = (TextView) preferenceViewHolder.findViewById(C0010R$id.tv_title);
        this.mTvContent = (TextView) preferenceViewHolder.findViewById(C0010R$id.tv_content);
        this.mIcon = (PreferenceImageView) preferenceViewHolder.findViewById(C0010R$id.icon);
        this.mTvNew = (TextView) preferenceViewHolder.findViewById(C0010R$id.tv_new);
        if (PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean("wlb_banner_new", true)) {
            this.mTvNew.setVisibility(0);
        }
        this.mTvTitle.setText(C0017R$string.work_life_balance);
        this.mTvContent.setText(C0017R$string.work_life_banner_summary);
        this.mIcon.setImageResource(C0008R$drawable.op_ic_homepage_opwlb);
        setClickBgHeight();
    }

    public void clearNew() {
        this.mTvNew.setVisibility(8);
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edit.putBoolean("wlb_banner_new", false);
        edit.apply();
    }

    private void setClickBgHeight() {
        LinearLayout linearLayout = this.mLlContent;
        if (linearLayout != null && this.mLlClickBg != null) {
            linearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                /* class com.oneplus.settings.worklifebalance.OPWLBBannerPreference.AnonymousClass1 */

                public void onGlobalLayout() {
                    Log.d("OPMemberPreference", "mLlContent.getHeight():" + OPWLBBannerPreference.this.mLlContent.getHeight());
                    ViewGroup.LayoutParams layoutParams = OPWLBBannerPreference.this.mLlClickBg.getLayoutParams();
                    layoutParams.height = OPWLBBannerPreference.this.mLlContent.getHeight();
                    OPWLBBannerPreference.this.mLlClickBg.setLayoutParams(layoutParams);
                    OPWLBBannerPreference.this.mLlContent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }
}
