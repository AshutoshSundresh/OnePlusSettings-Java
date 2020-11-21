package com.google.android.material.checkbox;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;
import com.google.android.material.R$color;

public class SelectedAvatarView extends ImageView implements Checkable {
    private int mCheckMarkBackgroundColor;
    private int mCheckMarkColor;
    private boolean mChecked = false;
    private CheckableFlipDrawable mDrawable;

    public SelectedAvatarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initForFlipRes(context);
    }

    private void initForFlipRes(Context context) {
        setCheckMarkBackgroundColor(context.getResources().getColor(R$color.op_avatar_checked_background));
        setCheckMarkColor(context.getResources().getColor(R$color.op_avatar_checked_marker));
    }

    public void setCheckMarkBackgroundColor(int i) {
        this.mCheckMarkBackgroundColor = i;
        CheckableFlipDrawable checkableFlipDrawable = this.mDrawable;
        if (checkableFlipDrawable != null) {
            checkableFlipDrawable.setCheckMarkBackgroundColor(i);
        }
    }

    public void setCheckMarkColor(int i) {
        this.mCheckMarkColor = i;
        CheckableFlipDrawable checkableFlipDrawable = this.mDrawable;
        if (checkableFlipDrawable != null) {
            checkableFlipDrawable.setCheckMarkColor(i);
        }
    }

    public void setImageDrawable(Drawable drawable) {
        if (drawable != null) {
            CheckableFlipDrawable checkableFlipDrawable = this.mDrawable;
            if (checkableFlipDrawable == null) {
                this.mDrawable = new CheckableFlipDrawable(drawable, getResources(), this.mCheckMarkBackgroundColor, this.mCheckMarkColor, 150);
                applyCheckState(false);
            } else {
                checkableFlipDrawable.setFront(drawable);
            }
            drawable = this.mDrawable;
        }
        super.setImageDrawable(drawable);
    }

    public boolean isChecked() {
        return this.mChecked;
    }

    public void toggle() {
        setChecked(!this.mChecked);
    }

    public void setChecked(boolean z) {
        setChecked(z, true);
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.mChecked != z) {
            this.mChecked = z;
            applyCheckState(z2);
        }
    }

    private void applyCheckState(boolean z) {
        CheckableFlipDrawable checkableFlipDrawable = this.mDrawable;
        if (checkableFlipDrawable != null) {
            checkableFlipDrawable.flipTo(!this.mChecked);
            if (!z) {
                this.mDrawable.reset();
            }
        }
    }
}
