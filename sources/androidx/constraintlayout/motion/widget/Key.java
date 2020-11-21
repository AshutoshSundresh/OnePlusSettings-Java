package androidx.constraintlayout.motion.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.constraintlayout.widget.ConstraintAttribute;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Key {
    public static int UNSET = -1;
    HashMap<String, ConstraintAttribute> mCustomConstraints;
    int mFramePosition;
    int mTargetId;
    String mTargetString = null;

    public abstract void addValues(HashMap<String, SplineSet> hashMap);

    /* access modifiers changed from: package-private */
    public abstract void getAttributeNames(HashSet<String> hashSet);

    /* access modifiers changed from: package-private */
    public abstract void load(Context context, AttributeSet attributeSet);

    public void setInterpolation(HashMap<String, Integer> hashMap) {
    }

    public Key() {
        int i = UNSET;
        this.mFramePosition = i;
        this.mTargetId = i;
    }

    /* access modifiers changed from: package-private */
    public boolean matches(String str) {
        String str2 = this.mTargetString;
        if (str2 == null || str == null) {
            return false;
        }
        return str.matches(str2);
    }
}
