package com.google.android.material.hintsearchview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.R$attr;
import com.google.android.material.R$color;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;
import com.google.android.material.R$style;
import com.google.android.material.R$styleable;

@SuppressLint({"NewApi"})
public class HintSearchView extends LinearLayout {
    private String mHintText;
    private TextView mHintView;
    private ImageView mIconSearch;
    private Drawable mSearchIconDrawable;
    private int mSearchIconTintColor;

    public HintSearchView(Context context) {
        super(context);
    }

    public HintSearchView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.hintSearchViewStyle);
    }

    public HintSearchView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R$layout.op_persistent_search_view, (ViewGroup) this, true);
        setOrientation(0);
        setFocusable(true);
        setClickable(true);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.HintSearchView, i, R$style.Oneplus_Widget_Desgin_HintSearchView);
        this.mSearchIconDrawable = obtainStyledAttributes.getDrawable(R$styleable.HintSearchView_android_icon);
        this.mHintText = obtainStyledAttributes.getString(R$styleable.HintSearchView_android_text);
        this.mSearchIconTintColor = obtainStyledAttributes.getColor(R$styleable.HintSearchView_iconTintColor, getResources().getColor(R$color.op_control_icon_color_active_light, getContext().getTheme()));
        int color = obtainStyledAttributes.getColor(R$styleable.HintSearchView_android_textColorHint, getResources().getColor(R$color.op_control_text_color_hint_light, context.getTheme()));
        obtainStyledAttributes.recycle();
        this.mHintView = (TextView) findViewById(R$id.persistent_search_hint);
        this.mIconSearch = (ImageView) findViewById(R$id.persistent_search_icon1);
        this.mHintView.setTextColor(color);
        setHintText(this.mHintText);
        Drawable drawable = this.mSearchIconDrawable;
        if (drawable != null) {
            drawable.setTint(this.mSearchIconTintColor);
        }
        setSearchIcon(this.mSearchIconDrawable);
    }

    public void setHintText(CharSequence charSequence) {
        TextView textView = this.mHintView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public void setHintText(int i) {
        if (i != 0 && this.mHintView != null) {
            setHintText(getResources().getString(i));
        }
    }

    public void setSearchIcon(Drawable drawable) {
        ImageView imageView = this.mIconSearch;
        if (imageView != null && drawable != null) {
            imageView.setImageDrawable(drawable);
        }
    }

    public void setSearchIcon(int i) {
        if (i != 0 && this.mIconSearch != null) {
            setSearchIcon(getResources().getDrawable(i, getContext().getTheme()));
        }
    }
}
