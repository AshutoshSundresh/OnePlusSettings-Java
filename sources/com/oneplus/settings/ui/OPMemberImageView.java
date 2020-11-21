package com.oneplus.settings.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

public class OPMemberImageView extends AppCompatImageView {
    public OPMemberImageView(Context context) {
        super(context);
    }

    public OPMemberImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public OPMemberImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
