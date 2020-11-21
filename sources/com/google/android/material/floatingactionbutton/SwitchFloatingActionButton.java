package com.google.android.material.floatingactionbutton;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.google.android.material.R$attr;
import com.google.android.material.R$dimen;
import com.google.android.material.R$drawable;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;
import com.google.android.material.R$style;
import com.google.android.material.R$styleable;

public class SwitchFloatingActionButton extends RelativeLayout {
    private static final int[] FOCUSED_ENABLED_STATE_SET = {16842908, 16842910};
    private boolean mIsSwitchState;
    private ImageView mNormalImageView;
    private ImageView mSwitchImageView;

    static {
        new PathInterpolator(0.0f, 0.0f, 0.4f, 1.0f);
    }

    public SwitchFloatingActionButton(Context context) {
        this(context, null);
    }

    public SwitchFloatingActionButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.switchFloatingActionButtonStyle);
    }

    public SwitchFloatingActionButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsSwitchState = false;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.switchFloatingActionButton, i, R$style.Widget_Design_SwitchFloatingActionButton);
        float dimension = getResources().getDimension(R$dimen.op_control_shadow_z5);
        ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(R$styleable.switchFloatingActionButton_tintColor);
        Drawable mutate = getResources().getDrawable(R$drawable.switch_floating_action_button).mutate();
        mutate.setTintList(colorStateList);
        setBackground(new RippleDrawable(ColorStateList.valueOf(getResources().getColor(17170443)), mutate, null));
        setElevation(dimension);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R$layout.float_switch_button, this);
        this.mNormalImageView = (ImageView) findViewById(R$id.normal_imageview);
        this.mNormalImageView.setImageDrawable(obtainStyledAttributes.getDrawable(R$styleable.switchFloatingActionButton_image));
        this.mSwitchImageView = (ImageView) findViewById(R$id.switch_imageview);
        obtainStyledAttributes.recycle();
    }

    public void setOpTintColor(int i) {
        ColorStateList valueOf = ColorStateList.valueOf(i);
        Drawable mutate = getResources().getDrawable(R$drawable.switch_floating_action_button).mutate();
        mutate.setTintList(valueOf);
        setBackground(new RippleDrawable(ColorStateList.valueOf(getResources().getColor(17170443)), mutate, null));
    }

    private void setPressedTranslationZ(float f) {
        StateListAnimator stateListAnimator = new StateListAnimator();
        int[] iArr = RelativeLayout.PRESSED_ENABLED_STATE_SET;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "translationZ", 0.0f);
        setupAnimator(ofFloat);
        stateListAnimator.addState(iArr, ofFloat);
        int[] iArr2 = FOCUSED_ENABLED_STATE_SET;
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this, "translationZ", 0.0f);
        setupAnimator(ofFloat2);
        stateListAnimator.addState(iArr2, ofFloat2);
        int[] iArr3 = RelativeLayout.EMPTY_STATE_SET;
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this, "translationZ", f);
        setupAnimator(ofFloat3);
        stateListAnimator.addState(iArr3, ofFloat3);
        setStateListAnimator(stateListAnimator);
    }

    private Animator setupAnimator(Animator animator) {
        animator.setDuration(75);
        return animator;
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
            this.mSwitchImageView.setScaleX(0.5f);
            this.mSwitchImageView.setScaleY(0.5f);
            this.mSwitchImageView.setAlpha(0.0f);
        }
    }

    public void setSwitchImageView(Drawable drawable) {
        this.mSwitchImageView.setImageDrawable(drawable);
        if (!this.mIsSwitchState) {
            this.mSwitchImageView.setScaleX(0.5f);
            this.mSwitchImageView.setScaleY(0.5f);
            this.mSwitchImageView.setAlpha(0.0f);
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
