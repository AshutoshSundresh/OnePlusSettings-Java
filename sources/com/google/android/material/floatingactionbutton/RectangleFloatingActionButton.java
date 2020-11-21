package com.google.android.material.floatingactionbutton;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.google.android.material.R$attr;
import com.google.android.material.R$drawable;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;
import com.google.android.material.R$style;
import com.google.android.material.R$styleable;

public class RectangleFloatingActionButton extends RelativeLayout {
    private boolean mIsSwitchState;
    private ImageView mNormalImageView;
    private ImageView mSwitchImageView;

    public RectangleFloatingActionButton(Context context) {
        this(context, null);
    }

    public RectangleFloatingActionButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.rectangleFloatingActionButtonStyle);
    }

    public RectangleFloatingActionButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsSwitchState = false;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.rectangleFloatingActionButton, i, R$style.Widget_Design_RectangleFloatingActionButton);
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R$styleable.rectangleFloatingActionButton_tintColor);
        Drawable mutate = getResources().getDrawable(R$drawable.rectangle_floating_action_button).mutate();
        mutate.setTintList(colorStateList);
        setBackground(new RippleDrawable(ColorStateList.valueOf(getResources().getColor(17170443)), mutate, null));
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R$layout.float_switch_button, this);
        this.mNormalImageView = (ImageView) findViewById(R$id.normal_imageview);
        this.mNormalImageView.setImageDrawable(obtainStyledAttributes.getDrawable(R$styleable.rectangleFloatingActionButton_image));
        this.mSwitchImageView = (ImageView) findViewById(R$id.switch_imageview);
        obtainStyledAttributes.recycle();
    }

    public void setImageResource(int i) {
        this.mNormalImageView.setImageResource(i);
    }

    public void setNormalImageView(int i) {
        this.mNormalImageView.setImageResource(i);
    }

    public void setNormalImageView(Drawable drawable) {
        this.mNormalImageView.setImageDrawable(drawable);
    }

    public void setSwitchImageView(int i) {
        this.mSwitchImageView.setImageResource(i);
        if (!this.mIsSwitchState) {
            this.mSwitchImageView.setScaleX(0.0f);
            this.mSwitchImageView.setScaleY(0.0f);
        }
    }

    public void setSwitchImageView(Drawable drawable) {
        this.mSwitchImageView.setImageDrawable(drawable);
        if (!this.mIsSwitchState) {
            this.mSwitchImageView.setScaleX(0.0f);
            this.mSwitchImageView.setScaleY(0.0f);
        }
    }

    public void setPivotType(int i) {
        switch (i) {
            case 1:
                setPivotY(0.0f);
                setPivotX(0.0f);
                return;
            case 2:
                setPivotY(0.0f);
                setPivotX((float) (getWidth() / 2));
                return;
            case 3:
                setPivotY(0.0f);
                setPivotX((float) getWidth());
                return;
            case 4:
                setPivotY((float) (getHeight() / 2));
                setPivotX(0.0f);
                return;
            case 5:
                setPivotY((float) (getHeight() / 2));
                setPivotX((float) (getWidth() / 2));
                return;
            case 6:
                setPivotY((float) (getHeight() / 2));
                setPivotX((float) getWidth());
                return;
            case 7:
                setPivotY((float) getHeight());
                setPivotX(0.0f);
                return;
            case 8:
                setPivotY((float) getHeight());
                setPivotX((float) (getWidth() / 2));
                return;
            case 9:
                setPivotY((float) getHeight());
                setPivotX((float) getWidth());
                return;
            default:
                return;
        }
    }
}
