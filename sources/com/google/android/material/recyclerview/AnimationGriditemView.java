package com.google.android.material.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.Interpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.animation.AnimatorUtils;
import com.google.android.material.R$id;
import com.google.android.material.R$layout;
import com.google.android.material.R$styleable;
import com.oneplus.commonctrl.R$dimen;
import com.oneplus.commonctrl.R$integer;

public class AnimationGriditemView extends FrameLayout {
    private static final int ANIMATION_DURATION_RES = R$integer.op_control_time_225;
    private static final int RADIUS_RES = R$dimen.op_control_radius_r12;
    private static final RadiusMode[] sRadiusModeTypeArray = {RadiusMode.NONE, RadiusMode.RADIUS};
    private static final ImageView.ScaleType[] sScaleTypeArray = {ImageView.ScaleType.MATRIX, ImageView.ScaleType.FIT_XY, ImageView.ScaleType.FIT_START, ImageView.ScaleType.FIT_CENTER, ImageView.ScaleType.FIT_END, ImageView.ScaleType.CENTER, ImageView.ScaleType.CENTER_CROP, ImageView.ScaleType.CENTER_INSIDE};
    private CheckBox mCheckBox;
    private boolean mChecked;
    private ImageView mImage;
    private View mMantleView;
    private int mRadius;
    private RadiusMode mRadiusMode = RadiusMode.NONE;

    public enum RadiusMode {
        NONE(0),
        RADIUS(1);
        
        final int nativeInt;

        private RadiusMode(int i) {
            this.nativeInt = i;
        }
    }

    public AnimationGriditemView(Context context) {
        super(context);
        Interpolator interpolator = AnimatorUtils.GRID_ITEM_ANIMATION_INTERPOLATOR;
        init(null);
    }

    public AnimationGriditemView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Interpolator interpolator = AnimatorUtils.GRID_ITEM_ANIMATION_INTERPOLATOR;
        init(attributeSet);
    }

    public AnimationGriditemView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        Interpolator interpolator = AnimatorUtils.GRID_ITEM_ANIMATION_INTERPOLATOR;
        init(attributeSet);
    }

    public AnimationGriditemView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        Interpolator interpolator = AnimatorUtils.GRID_ITEM_ANIMATION_INTERPOLATOR;
        init(attributeSet);
    }

    private void init(AttributeSet attributeSet) {
        LayoutInflater.from(getContext()).inflate(R$layout.op_animation_grid_list_item, (ViewGroup) this, true);
        this.mImage = (ImageView) findViewById(R$id.grid_item_img);
        this.mMantleView = findViewById(R$id.mantle);
        this.mCheckBox = (CheckBox) findViewById(R$id.grid_item_checkbox);
        this.mRadius = getResources().getDimensionPixelOffset(RADIUS_RES);
        getResources().getInteger(ANIMATION_DURATION_RES);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R$styleable.AnimationGridItemView, 0, 0);
        Drawable drawable = obtainStyledAttributes.getDrawable(R$styleable.AnimationGridItemView_android_src);
        if (drawable != null) {
            setImageDrawable(drawable);
        }
        int i = obtainStyledAttributes.getInt(R$styleable.AnimationGridItemView_android_scaleType, 1);
        if (i >= 0) {
            this.mImage.setScaleType(sScaleTypeArray[i]);
        }
        int i2 = obtainStyledAttributes.getInt(R$styleable.AnimationGridItemView_radiusMode, -1);
        if (i2 >= 0) {
            setRadiusMode(sRadiusModeTypeArray[i2]);
        }
        obtainStyledAttributes.recycle();
    }

    public void setImageDrawable(int i) {
        this.mImage.setImageResource(i);
    }

    public void setImageDrawable(Drawable drawable) {
        this.mImage.setImageDrawable(drawable);
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        this.mImage.setScaleType(scaleType);
    }

    public void setRadiusMode(RadiusMode radiusMode) {
        if (this.mRadiusMode != radiusMode) {
            this.mRadiusMode = radiusMode;
            scheduleRadiusChange();
        }
    }

    public void setChecked(boolean z) {
        if (this.mChecked != z) {
            this.mChecked = z;
            this.mCheckBox.setChecked(z);
            scheduleCheckedAnimation();
        }
    }

    public ImageView getmImageView() {
        return this.mImage;
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.mCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private void scheduleRadiusChange() {
        if (this.mRadiusMode == RadiusMode.RADIUS) {
            setOutlineProvider(new RoundRectOutlineProvider(this.mRadius));
            setClipToOutline(true);
            this.mImage.setOutlineProvider(new RoundRectOutlineProvider(this.mRadius));
            this.mImage.setClipToOutline(true);
        }
    }

    private void scheduleCheckedAnimation() {
        if (this.mImage != null) {
            if (this.mChecked) {
                this.mMantleView.setVisibility(0);
            } else {
                this.mMantleView.setVisibility(8);
            }
        }
    }

    /* access modifiers changed from: private */
    public static class RoundRectOutlineProvider extends ViewOutlineProvider {
        private int mRadius;

        public RoundRectOutlineProvider(int i) {
            this.mRadius = i;
        }

        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (float) this.mRadius);
        }
    }
}
