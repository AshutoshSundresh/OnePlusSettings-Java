package com.android.settingslib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class TwoTargetPreference extends Preference {
    private int mIconSize;
    private int mMediumIconSize;
    private int mSmallIconSize;

    /* access modifiers changed from: protected */
    public int getSecondTargetResId() {
        return 0;
    }

    public TwoTargetPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(context);
    }

    public TwoTargetPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    public TwoTargetPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public TwoTargetPreference(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setLayoutResource(R$layout.preference_two_target);
        this.mSmallIconSize = context.getResources().getDimensionPixelSize(R$dimen.two_target_pref_small_icon_size);
        this.mMediumIconSize = context.getResources().getDimensionPixelSize(R$dimen.two_target_pref_medium_icon_size);
        int secondTargetResId = getSecondTargetResId();
        if (secondTargetResId != 0) {
            setWidgetLayoutResource(secondTargetResId);
        }
    }

    public void setIconSize(int i) {
        this.mIconSize = i;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ImageView imageView = (ImageView) preferenceViewHolder.itemView.findViewById(16908294);
        int i = this.mIconSize;
        if (i == 1) {
            int i2 = this.mMediumIconSize;
            imageView.setLayoutParams(new LinearLayout.LayoutParams(i2, i2));
        } else if (i == 2) {
            int i3 = this.mSmallIconSize;
            imageView.setLayoutParams(new LinearLayout.LayoutParams(i3, i3));
        }
        View findViewById = preferenceViewHolder.findViewById(R$id.two_target_divider);
        View findViewById2 = preferenceViewHolder.findViewById(16908312);
        boolean shouldHideSecondTarget = shouldHideSecondTarget();
        int i4 = 8;
        if (findViewById != null) {
            findViewById.setVisibility(shouldHideSecondTarget ? 8 : 0);
        }
        if (findViewById2 != null) {
            if (!shouldHideSecondTarget) {
                i4 = 0;
            }
            findViewById2.setVisibility(i4);
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldHideSecondTarget() {
        return getSecondTargetResId() == 0;
    }
}
