package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settingslib.RestrictedPreference;
import com.oneplus.settings.utils.OPUtils;

public class OPTextViewButtonPreference extends RestrictedPreference {
    protected boolean mButtonEnable;
    protected String mButtonString;
    private boolean mButtonVisible;
    private Context mContext;
    protected Drawable mIcon;
    private ImageView mLeftIcon;
    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mOnRightIconClickListener;
    private TextView mRightButton;
    private ImageView mRightIcon;
    private boolean mRightIconVisible;
    private TextView mSummary;
    private String mSummaryString;
    private boolean mSummaryVisible;
    private ColorStateList mTextButtonColor;
    private TextView mTextTitle;
    private String mTextTitleString;
    private int resid = C0012R$layout.op_textview_button_prefrence;

    public OPTextViewButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    public OPTextViewButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context);
    }

    public OPTextViewButtonPreference(Context context) {
        super(context);
        initViews(context);
    }

    private void initViews(Context context) {
        this.mContext = context;
        setLayoutResource(this.resid);
        this.mTextTitleString = "";
        this.mSummaryString = "";
        this.mButtonString = "";
        this.mIcon = null;
        this.mButtonEnable = false;
        this.mButtonVisible = true;
        this.mSummaryVisible = false;
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
        TextView textView3 = (TextView) preferenceViewHolder.findViewById(C0010R$id.summary);
        this.mSummary = textView3;
        textView3.setText(this.mSummaryString);
        this.mSummary.setVisibility(this.mSummaryVisible ? 0 : 8);
        ImageView imageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.right_ico);
        this.mRightIcon = imageView;
        if (!this.mRightIconVisible) {
            i = 8;
        }
        imageView.setVisibility(i);
        this.mRightIcon.setOnClickListener(this.mOnRightIconClickListener);
        Drawable drawable = this.mIcon;
        if (drawable != null) {
            this.mLeftIcon.setImageDrawable(drawable);
        }
    }

    public void setRightIconVisible(boolean z) {
        this.mRightIconVisible = z;
        notifyChanged();
    }

    public void setOnRightIconClickListener(View.OnClickListener onClickListener) {
        this.mOnRightIconClickListener = onClickListener;
    }

    public void setButtonEnable(boolean z) {
        this.mButtonEnable = z;
        notifyChanged();
    }

    public void setButtonVisible(boolean z) {
        this.mButtonVisible = z;
        notifyChanged();
    }

    public void setSummaryVisible(boolean z) {
        this.mSummaryVisible = z;
        notifyChanged();
    }

    public void setLeftTextTitle(String str) {
        this.mTextTitleString = str;
        notifyChanged();
    }

    public void setSummary(String str) {
        this.mSummaryString = str;
        notifyChanged();
    }

    @Override // androidx.preference.Preference
    public String getSummary() {
        return this.mSummaryString;
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
    public CharSequence getTitle() {
        return this.mTextTitleString;
    }
}
