package androidx.leanback.widget;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import androidx.leanback.widget.GridLayoutManager;
import androidx.leanback.widget.ItemAlignmentFacet;

/* access modifiers changed from: package-private */
public class ItemAlignmentFacetHelper {
    private static Rect sRect = new Rect();

    static int getAlignmentPosition(View view, ItemAlignmentFacet.ItemAlignmentDef itemAlignmentDef, int i) {
        View view2;
        int i2;
        int i3;
        int i4;
        int i5;
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        int i6 = itemAlignmentDef.mViewId;
        if (i6 == 0 || (view2 = view.findViewById(i6)) == null) {
            view2 = view;
        }
        int i7 = itemAlignmentDef.mOffset;
        if (i != 0) {
            if (itemAlignmentDef.mOffsetWithPadding) {
                float f = itemAlignmentDef.mOffsetPercent;
                if (f == 0.0f) {
                    i7 += view2.getPaddingTop();
                } else if (f == 100.0f) {
                    i7 -= view2.getPaddingBottom();
                }
            }
            if (itemAlignmentDef.mOffsetPercent != -1.0f) {
                i7 += (int) ((((float) (view2 == view ? layoutParams.getOpticalHeight(view2) : view2.getHeight())) * itemAlignmentDef.mOffsetPercent) / 100.0f);
            }
            if (view != view2) {
                Rect rect = sRect;
                rect.top = i7;
                ((ViewGroup) view).offsetDescendantRectToMyCoords(view2, rect);
                i2 = sRect.top - layoutParams.getOpticalTopInset();
            } else {
                i2 = i7;
            }
            return itemAlignmentDef.isAlignedToTextViewBaseLine() ? i2 + view2.getBaseline() : i2;
        } else if (view.getLayoutDirection() == 1) {
            if (view2 == view) {
                i4 = layoutParams.getOpticalWidth(view2);
            } else {
                i4 = view2.getWidth();
            }
            int i8 = i4 - i7;
            if (itemAlignmentDef.mOffsetWithPadding) {
                float f2 = itemAlignmentDef.mOffsetPercent;
                if (f2 == 0.0f) {
                    i8 -= view2.getPaddingRight();
                } else if (f2 == 100.0f) {
                    i8 += view2.getPaddingLeft();
                }
            }
            if (itemAlignmentDef.mOffsetPercent != -1.0f) {
                if (view2 == view) {
                    i5 = layoutParams.getOpticalWidth(view2);
                } else {
                    i5 = view2.getWidth();
                }
                i8 -= (int) ((((float) i5) * itemAlignmentDef.mOffsetPercent) / 100.0f);
            }
            if (view == view2) {
                return i8;
            }
            Rect rect2 = sRect;
            rect2.right = i8;
            ((ViewGroup) view).offsetDescendantRectToMyCoords(view2, rect2);
            return sRect.right + layoutParams.getOpticalRightInset();
        } else {
            if (itemAlignmentDef.mOffsetWithPadding) {
                float f3 = itemAlignmentDef.mOffsetPercent;
                if (f3 == 0.0f) {
                    i7 += view2.getPaddingLeft();
                } else if (f3 == 100.0f) {
                    i7 -= view2.getPaddingRight();
                }
            }
            if (itemAlignmentDef.mOffsetPercent != -1.0f) {
                if (view2 == view) {
                    i3 = layoutParams.getOpticalWidth(view2);
                } else {
                    i3 = view2.getWidth();
                }
                i7 += (int) ((((float) i3) * itemAlignmentDef.mOffsetPercent) / 100.0f);
            }
            if (view == view2) {
                return i7;
            }
            Rect rect3 = sRect;
            rect3.left = i7;
            ((ViewGroup) view).offsetDescendantRectToMyCoords(view2, rect3);
            return sRect.left - layoutParams.getOpticalLeftInset();
        }
    }
}
