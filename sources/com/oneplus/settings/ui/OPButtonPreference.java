package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settingslib.RestrictedPreference;
import com.oneplus.settings.utils.OPUtils;

public class OPButtonPreference extends RestrictedPreference {
    private boolean mButtonEnable;
    private String mButtonString;
    private boolean mButtonVisible;
    private Context mContext;
    private Drawable mIcon;
    private ImageView mLeftIcon;
    private View.OnClickListener mOnClickListener;
    private TextView mRightButton;
    private ColorStateList mTextButtonColor;
    private TextView mTextSummary;
    private String mTextSummaryString;
    private boolean mTextSummaryVisible;
    private TextView mTextTitle;
    private String mTextTitleString;
    private int resid = C0012R$layout.op_button_preference;

    public OPButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    public OPButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    private void initViews(Context context) {
        this.mContext = context;
        setLayoutResource(this.resid);
        this.mTextTitleString = "";
        this.mTextSummaryString = "";
        this.mButtonString = "";
        this.mIcon = null;
        this.mButtonEnable = false;
        this.mButtonVisible = true;
        this.mTextButtonColor = OPUtils.creatOneplusPrimaryColorStateList(this.mContext);
    }

    @Override // com.android.settingslib.TwoTargetPreference, com.android.settingslib.RestrictedPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mLeftIcon = (ImageView) preferenceViewHolder.findViewById(C0010R$id.left_ico);
        TextView textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.right_button);
        this.mRightButton = textView;
        int i = 0;
        textView.setVisibility(this.mButtonVisible ? 0 : 8);
        this.mRightButton.setTextColor(this.mTextButtonColor);
        this.mRightButton.setOnClickListener(this.mOnClickListener);
        this.mRightButton.setEnabled(this.mButtonEnable);
        this.mRightButton.setText(this.mButtonString);
        TextView textView2 = (TextView) preferenceViewHolder.findViewById(C0010R$id.lefttitle);
        this.mTextTitle = textView2;
        textView2.setText(this.mTextTitleString);
        TextView textView3 = (TextView) preferenceViewHolder.findViewById(C0010R$id.leftsummary);
        this.mTextSummary = textView3;
        if (!this.mTextSummaryVisible) {
            i = 8;
        }
        textView3.setVisibility(i);
        this.mTextSummary.setText(this.mTextSummaryString);
        Drawable drawable = this.mIcon;
        if (drawable != null) {
            this.mLeftIcon.setImageDrawable(drawable);
        }
    }

    public void setLeftTextTitle(String str) {
        this.mTextTitleString = str;
        notifyChanged();
    }

    public void setLeftTextSummary(String str) {
        this.mTextSummaryString = str;
        if (!TextUtils.isEmpty(str)) {
            this.mTextSummaryVisible = true;
        } else {
            this.mTextSummaryVisible = false;
        }
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public void setIcon(Drawable drawable) {
        this.mIcon = drawable;
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public Drawable getIcon() {
        return this.mIcon;
    }

    @Override // androidx.preference.Preference
    public void setTitle(CharSequence charSequence) {
        setLeftTextTitle(charSequence.toString());
    }

    @Override // androidx.preference.Preference
    public void setSummary(CharSequence charSequence) {
        setLeftTextSummary(charSequence == null ? null : charSequence.toString());
    }

    @Override // androidx.preference.Preference
    public CharSequence getTitle() {
        return this.mTextTitleString;
    }

    @Override // androidx.preference.Preference
    public CharSequence getSummary() {
        return this.mTextSummaryString;
    }
}
