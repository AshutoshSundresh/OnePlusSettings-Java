package androidx.leanback.widget;

import android.view.View;
import androidx.leanback.widget.ItemAlignmentFacet;

/* access modifiers changed from: package-private */
public class ItemAlignment {
    public final Axis horizontal;
    private Axis mMainAxis;
    private int mOrientation = 0;
    public final Axis vertical = new Axis(1);

    ItemAlignment() {
        Axis axis = new Axis(0);
        this.horizontal = axis;
        this.mMainAxis = axis;
    }

    /* access modifiers changed from: package-private */
    public static final class Axis extends ItemAlignmentFacet.ItemAlignmentDef {
        private int mOrientation;

        Axis(int i) {
            this.mOrientation = i;
        }

        public int getAlignmentPosition(View view) {
            return ItemAlignmentFacetHelper.getAlignmentPosition(view, this, this.mOrientation);
        }
    }

    public final Axis mainAxis() {
        return this.mMainAxis;
    }

    public final void setOrientation(int i) {
        this.mOrientation = i;
        if (i == 0) {
            this.mMainAxis = this.horizontal;
        } else {
            this.mMainAxis = this.vertical;
        }
    }
}
