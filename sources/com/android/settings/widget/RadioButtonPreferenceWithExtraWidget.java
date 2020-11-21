package com.android.settings.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;

public class RadioButtonPreferenceWithExtraWidget extends RadioButtonPreference {
    private ImageView mExtraWidget;
    private View mExtraWidgetDivider;
    private View.OnClickListener mExtraWidgetOnClickListener;
    private int mExtraWidgetVisibility = 0;

    public RadioButtonPreferenceWithExtraWidget(Context context) {
        super(context, null);
        setLayoutResource(C0012R$layout.preference_radio_with_extra_widget);
    }

    @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mExtraWidget = (ImageView) preferenceViewHolder.findViewById(C0010R$id.radio_extra_widget);
        this.mExtraWidgetDivider = preferenceViewHolder.findViewById(C0010R$id.radio_extra_widget_divider);
        setExtraWidgetVisibility(this.mExtraWidgetVisibility);
        View.OnClickListener onClickListener = this.mExtraWidgetOnClickListener;
        if (onClickListener != null) {
            setExtraWidgetOnClickListener(onClickListener);
        }
    }

    public void setExtraWidgetVisibility(int i) {
        this.mExtraWidgetVisibility = i;
        ImageView imageView = this.mExtraWidget;
        if (imageView != null && this.mExtraWidgetDivider != null) {
            if (i == 0) {
                imageView.setClickable(false);
                this.mExtraWidget.setVisibility(8);
                this.mExtraWidgetDivider.setVisibility(8);
                return;
            }
            imageView.setClickable(true);
            this.mExtraWidget.setVisibility(0);
            this.mExtraWidgetDivider.setVisibility(0);
            int i2 = this.mExtraWidgetVisibility;
            if (i2 == 1) {
                this.mExtraWidget.setImageResource(C0008R$drawable.ic_settings_about);
                this.mExtraWidget.setContentDescription(getContext().getResources().getText(C0017R$string.information_label));
            } else if (i2 == 2) {
                this.mExtraWidget.setImageResource(C0008R$drawable.ic_settings_accent);
                this.mExtraWidget.setContentDescription(getContext().getResources().getText(C0017R$string.settings_label));
            }
        }
    }

    public void setExtraWidgetOnClickListener(View.OnClickListener onClickListener) {
        this.mExtraWidgetOnClickListener = onClickListener;
        ImageView imageView = this.mExtraWidget;
        if (imageView != null) {
            imageView.setEnabled(true);
            this.mExtraWidget.setOnClickListener(onClickListener);
        }
    }
}
