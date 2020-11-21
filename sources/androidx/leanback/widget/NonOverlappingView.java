package androidx.leanback.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

class NonOverlappingView extends View {
    public boolean hasOverlappingRendering() {
        return false;
    }

    public NonOverlappingView(Context context) {
        this(context, null);
    }

    public NonOverlappingView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
    }

    public NonOverlappingView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }
}
