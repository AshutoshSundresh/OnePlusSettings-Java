package com.android.settings.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.android.settings.C0007R$dimen;

public class MessageBubbleBackground extends LinearLayout {
    private final int mSnapWidthPixels;

    public MessageBubbleBackground(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSnapWidthPixels = context.getResources().getDimensionPixelSize(C0007R$dimen.conversation_bubble_width_snap);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i) - paddingLeft, (int) (Math.ceil((double) (((float) (getMeasuredWidth() - paddingLeft)) / ((float) this.mSnapWidthPixels))) * ((double) this.mSnapWidthPixels))) + paddingLeft, 1073741824), i2);
    }
}
