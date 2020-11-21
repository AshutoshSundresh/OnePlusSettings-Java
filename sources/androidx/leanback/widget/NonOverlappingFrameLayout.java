package androidx.leanback.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

class NonOverlappingFrameLayout extends FrameLayout {
    public boolean hasOverlappingRendering() {
        return false;
    }

    public NonOverlappingFrameLayout(Context context) {
        this(context, null);
    }

    public NonOverlappingFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
    }

    public NonOverlappingFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }
}
