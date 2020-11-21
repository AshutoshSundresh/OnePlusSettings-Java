package com.oneplus.settings.opfinger;

import android.content.Context;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;

public class OPFingerPrintItemPreference extends Preference {
    private static String BACKGROUND_COLOR = "#239ff1";
    private AlphaAnimation mAlphaAnimation;
    private View mBackGroundView;
    private boolean mHighlightBackgroundColor = false;
    private int mLayoutResId = C0012R$layout.op_fingerprint_item_preference;
    private String mOPFingerPrintSummary;
    private String mOPFingerPrintTitle;
    private TextView mSummaryView;
    private TextView mTitleView;

    public OPFingerPrintItemPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPFingerPrintItemPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    private void initViews(Context context) {
        setLayoutResource(this.mLayoutResId);
    }

    /* access modifiers changed from: protected */
    public View onCreateView(ViewGroup viewGroup) {
        return super.onCreateView(viewGroup);
    }

    /* access modifiers changed from: protected */
    public void onBindView(View view) {
        super.onBindView(view);
        this.mBackGroundView = view.findViewById(C0010R$id.opfingerprint_item_highlight_view);
        this.mTitleView = (TextView) view.findViewById(C0010R$id.opfingerprint_item_title);
        this.mSummaryView = (TextView) view.findViewById(C0010R$id.opfingerprint_item_summary);
        this.mTitleView.setText(this.mOPFingerPrintTitle);
        this.mSummaryView.setText(this.mOPFingerPrintSummary);
        this.mSummaryView.setVisibility(8);
        this.mAlphaAnimation = new AlphaAnimation(0.0f, 0.4f);
        if (this.mHighlightBackgroundColor) {
            this.mBackGroundView.setBackgroundColor(Color.parseColor(BACKGROUND_COLOR));
            this.mAlphaAnimation.setDuration(500);
            this.mAlphaAnimation.setRepeatCount(1);
            this.mAlphaAnimation.setRepeatMode(2);
            this.mAlphaAnimation.setFillAfter(true);
            this.mAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                /* class com.oneplus.settings.opfinger.OPFingerPrintItemPreference.AnonymousClass1 */

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    OPFingerPrintItemPreference.this.mHighlightBackgroundColor = false;
                }
            });
            this.mBackGroundView.setAnimation(this.mAlphaAnimation);
            this.mAlphaAnimation.start();
            return;
        }
        this.mBackGroundView.setBackgroundColor(0);
    }
}
