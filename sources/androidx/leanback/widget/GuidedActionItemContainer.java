package androidx.leanback.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/* access modifiers changed from: package-private */
public class GuidedActionItemContainer extends NonOverlappingLinearLayoutWithForeground {
    private boolean mFocusOutAllowed;

    public GuidedActionItemContainer(Context context) {
        this(context, null);
    }

    public GuidedActionItemContainer(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public GuidedActionItemContainer(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mFocusOutAllowed = true;
    }

    public View focusSearch(View view, int i) {
        if (this.mFocusOutAllowed || !Util.isDescendant(this, view)) {
            return super.focusSearch(view, i);
        }
        View focusSearch = super.focusSearch(view, i);
        if (Util.isDescendant(this, focusSearch)) {
            return focusSearch;
        }
        return null;
    }

    public void setFocusOutAllowed(boolean z) {
        this.mFocusOutAllowed = z;
    }
}
