package com.oneplus.settings.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.oneplus.settings.utils.OPUtils;

public class OPEsportModeIntroductionCategory extends Preference {
    private ContentResolver mContentResolver;
    private Context mContext;
    private ImageView mEsportmodeIntroductionImageView;
    private int mLayoutResId = C0012R$layout.op_esport_mode_instruction_category;

    public OPEsportModeIntroductionCategory(Context context) {
        super(context);
        initViews(context);
    }

    public OPEsportModeIntroductionCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPEsportModeIntroductionCategory(Context context, AttributeSet attributeSet, int i) {
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
        this.mEsportmodeIntroductionImageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.esportmode_introduction_imageview);
        TextView textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.esportmode_introduction_summary);
        if (OPUtils.isBlackModeOn(this.mContentResolver)) {
            this.mEsportmodeIntroductionImageView.setImageDrawable(this.mContext.getResources().getDrawable(C0008R$drawable.op_esport_mode_introduction_dark));
        } else {
            this.mEsportmodeIntroductionImageView.setImageDrawable(this.mContext.getResources().getDrawable(C0008R$drawable.op_esport_mode_introduction_light));
        }
        preferenceViewHolder.setDividerAllowedBelow(false);
    }
}
