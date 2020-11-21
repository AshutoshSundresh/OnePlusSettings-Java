package androidx.leanback.widget;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class Util {
    public static boolean isDescendant(ViewGroup viewGroup, View view) {
        while (view != null) {
            if (view == viewGroup) {
                return true;
            }
            ViewParent parent = view.getParent();
            if (!(parent instanceof View)) {
                return false;
            }
            view = (View) parent;
        }
        return false;
    }
}
