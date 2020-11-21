package com.google.android.setupdesign.template;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.setupcompat.internal.TemplateLayout;
import com.google.android.setupcompat.template.Mixin;
import com.google.android.setupdesign.R$id;
import com.google.android.setupdesign.R$styleable;
import com.google.android.setupdesign.util.HeaderAreaStyler;
import com.google.android.setupdesign.util.PartnerStyleHelper;

public class HeaderMixin implements Mixin {
    private final TemplateLayout templateLayout;

    public HeaderMixin(TemplateLayout templateLayout2, AttributeSet attributeSet, int i) {
        this.templateLayout = templateLayout2;
        TypedArray obtainStyledAttributes = templateLayout2.getContext().obtainStyledAttributes(attributeSet, R$styleable.SucHeaderMixin, i, 0);
        CharSequence text = obtainStyledAttributes.getText(R$styleable.SucHeaderMixin_sucHeaderText);
        if (text != null) {
            setText(text);
        }
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R$styleable.SucHeaderMixin_sucHeaderTextColor);
        if (colorStateList != null) {
            setTextColor(colorStateList);
        }
        obtainStyledAttributes.recycle();
    }

    public void tryApplyPartnerCustomizationStyle() {
        if (PartnerStyleHelper.isPartnerHeavyThemeLayout(this.templateLayout)) {
            TextView textView = (TextView) this.templateLayout.findManagedViewById(R$id.suc_layout_title);
            if (textView != null) {
                HeaderAreaStyler.applyPartnerCustomizationHeaderStyle(textView);
            }
            LinearLayout linearLayout = (LinearLayout) this.templateLayout.findManagedViewById(R$id.sud_layout_header);
            if (linearLayout != null) {
                HeaderAreaStyler.applyPartnerCustomizationHeaderAreaStyle(linearLayout);
            }
        }
    }

    public TextView getTextView() {
        return (TextView) this.templateLayout.findManagedViewById(R$id.suc_layout_title);
    }

    public void setText(int i) {
        TextView textView = getTextView();
        if (textView != null) {
            textView.setText(i);
        }
    }

    public void setText(CharSequence charSequence) {
        TextView textView = getTextView();
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public CharSequence getText() {
        TextView textView = getTextView();
        if (textView != null) {
            return textView.getText();
        }
        return null;
    }

    public void setTextColor(ColorStateList colorStateList) {
        TextView textView = getTextView();
        if (textView != null) {
            textView.setTextColor(colorStateList);
        }
    }

    public ColorStateList getTextColor() {
        TextView textView = getTextView();
        if (textView != null) {
            return textView.getTextColors();
        }
        return null;
    }
}
