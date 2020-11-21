package com.google.android.material.emptyview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class EmptyImageView extends ImageView {
    private boolean isSetGoneFromUser = false;

    public EmptyImageView(Context context) {
        super(context);
    }

    public EmptyImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public EmptyImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public EmptyImageView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public void setVisibility(int i) {
        if (i == 8) {
            this.isSetGoneFromUser = true;
        }
        super.setVisibility(i);
    }

    public void setHideForNoSpace() {
        super.setVisibility(8);
    }

    public boolean isSetGoneFromUser() {
        return this.isSetGoneFromUser;
    }
}
