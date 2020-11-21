package androidx.slice.widget;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import java.util.ArrayList;
import java.util.Iterator;

public class LocationBasedViewTracker implements Runnable, View.OnLayoutChangeListener {
    @TargetApi(21)
    private static final SelectionLogic A11Y_FOCUS = new SelectionLogic() {
        /* class androidx.slice.widget.LocationBasedViewTracker.AnonymousClass2 */

        @Override // androidx.slice.widget.LocationBasedViewTracker.SelectionLogic
        public void selectView(View view) {
            view.performAccessibilityAction(64, null);
        }
    };
    private static final SelectionLogic INPUT_FOCUS = new SelectionLogic() {
        /* class androidx.slice.widget.LocationBasedViewTracker.AnonymousClass1 */

        @Override // androidx.slice.widget.LocationBasedViewTracker.SelectionLogic
        public void selectView(View view) {
            view.requestFocus();
        }
    };
    private final Rect mFocusRect;
    private final ViewGroup mParent;
    private final SelectionLogic mSelectionLogic;

    /* access modifiers changed from: private */
    public interface SelectionLogic {
        void selectView(View view);
    }

    private LocationBasedViewTracker(ViewGroup viewGroup, View view, SelectionLogic selectionLogic) {
        Rect rect = new Rect();
        this.mFocusRect = rect;
        this.mParent = viewGroup;
        this.mSelectionLogic = selectionLogic;
        view.getDrawingRect(rect);
        viewGroup.offsetDescendantRectToMyCoords(view, this.mFocusRect);
        this.mParent.addOnLayoutChangeListener(this);
        this.mParent.requestLayout();
    }

    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.mParent.removeOnLayoutChangeListener(this);
        this.mParent.post(this);
    }

    public void run() {
        int abs;
        ArrayList<View> arrayList = new ArrayList<>();
        this.mParent.addFocusables(arrayList, 2, 0);
        Rect rect = new Rect();
        Iterator<View> it = arrayList.iterator();
        int i = Integer.MAX_VALUE;
        View view = null;
        while (it.hasNext()) {
            View next = it.next();
            next.getDrawingRect(rect);
            this.mParent.offsetDescendantRectToMyCoords(next, rect);
            if (this.mFocusRect.intersect(rect) && i > (abs = Math.abs(this.mFocusRect.left - rect.left) + Math.abs(this.mFocusRect.right - rect.right) + Math.abs(this.mFocusRect.top - rect.top) + Math.abs(this.mFocusRect.bottom - rect.bottom))) {
                view = next;
                i = abs;
            }
        }
        if (view != null) {
            this.mSelectionLogic.selectView(view);
        }
    }

    public static void trackInputFocused(ViewGroup viewGroup) {
        View findFocus = viewGroup.findFocus();
        if (findFocus != null) {
            new LocationBasedViewTracker(viewGroup, findFocus, INPUT_FOCUS);
        }
    }

    public static void trackA11yFocus(ViewGroup viewGroup) {
        if (Build.VERSION.SDK_INT >= 21 && ((AccessibilityManager) viewGroup.getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
            ArrayList<View> arrayList = new ArrayList<>();
            viewGroup.addFocusables(arrayList, 2, 0);
            View view = null;
            Iterator<View> it = arrayList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                View next = it.next();
                if (next.isAccessibilityFocused()) {
                    view = next;
                    break;
                }
            }
            if (view != null) {
                new LocationBasedViewTracker(viewGroup, view, A11Y_FOCUS);
            }
        }
    }
}
