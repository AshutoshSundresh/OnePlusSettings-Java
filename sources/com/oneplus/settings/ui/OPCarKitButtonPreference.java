package com.oneplus.settings.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settingslib.RestrictedPreference;
import com.oneplus.settings.utils.OPUtils;

public class OPCarKitButtonPreference extends RestrictedPreference {
    protected boolean mButtonEnable;
    protected String mButtonString;
    private boolean mButtonVisible;
    private Context mContext;
    protected Drawable mIcon;
    private ImageView mLeftIcon;
    private View.OnClickListener mOnClickListener;
    private Button mRightButton;
    private ColorStateList mTextButtonColor;
    private String mTextSummaryString;
    private TextView mTextTitle;
    private String mTextTitleString;
    private int resid = C0012R$layout.op_car_kit_button_preference;

    public OPCarKitButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context);
    }

    public OPCarKitButtonPreference(Context context, AttributeSet attributeSet) {
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
        int i = 0;
        preferenceViewHolder.setDividerAllowedBelow(false);
        preferenceViewHolder.setDividerAllowedAbove(false);
        this.mLeftIcon = (ImageView) preferenceViewHolder.findViewById(C0010R$id.left_ico);
        Button button = (Button) preferenceViewHolder.findViewById(C0010R$id.right_button);
        this.mRightButton = button;
        if (!this.mButtonVisible) {
            i = 8;
        }
        button.setVisibility(i);
        this.mRightButton.setTextColor(this.mTextButtonColor);
        this.mRightButton.setOnClickListener(this.mOnClickListener);
        this.mRightButton.setEnabled(this.mButtonEnable);
        this.mRightButton.setText(this.mButtonString);
        TextView textView = (TextView) preferenceViewHolder.findViewById(C0010R$id.lefttitle);
        this.mTextTitle = textView;
        textView.setText(this.mTextTitleString);
        Drawable drawable = this.mIcon;
        if (drawable != null) {
            this.mLeftIcon.setImageDrawable(drawable);
        }
    }

    public void setOnButtonClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setLeftTextTitle(String str) {
        this.mTextTitleString = str;
        notifyChanged();
    }

    public void setLeftTextSummary(String str) {
        this.mTextSummaryString = str;
        TextUtils.isEmpty(str);
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
