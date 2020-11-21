package androidx.transition;

import android.graphics.Rect;
import android.view.ViewGroup;

public class SidePropagation extends VisibilityPropagation {
    private float mPropagationSpeed = 3.0f;
    private int mSide = 80;

    public void setSide(int i) {
        this.mSide = i;
    }

    @Override // androidx.transition.TransitionPropagation
    public long getStartDelay(ViewGroup viewGroup, Transition transition, TransitionValues transitionValues, TransitionValues transitionValues2) {
        int i;
        int i2;
        int i3;
        TransitionValues transitionValues3 = transitionValues;
        if (transitionValues3 == null && transitionValues2 == null) {
            return 0;
        }
        Rect epicenter = transition.getEpicenter();
        if (transitionValues2 == null || getViewVisibility(transitionValues3) == 0) {
            i = -1;
        } else {
            transitionValues3 = transitionValues2;
            i = 1;
        }
        int viewX = getViewX(transitionValues3);
        int viewY = getViewY(transitionValues3);
        int[] iArr = new int[2];
        viewGroup.getLocationOnScreen(iArr);
        int round = iArr[0] + Math.round(viewGroup.getTranslationX());
        int round2 = iArr[1] + Math.round(viewGroup.getTranslationY());
        int width = round + viewGroup.getWidth();
        int height = round2 + viewGroup.getHeight();
        if (epicenter != null) {
            i3 = epicenter.centerX();
            i2 = epicenter.centerY();
        } else {
            i3 = (round + width) / 2;
            i2 = (round2 + height) / 2;
        }
        float distance = ((float) distance(viewGroup, viewX, viewY, i3, i2, round, round2, width, height)) / ((float) getMaxDistance(viewGroup));
        long duration = transition.getDuration();
        if (duration < 0) {
            duration = 300;
        }
        return (long) Math.round((((float) (duration * ((long) i))) / this.mPropagationSpeed) * distance);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0026, code lost:
        if (r3 != false) goto L_0x0017;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0013, code lost:
        if (r3 != false) goto L_0x0015;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0017, code lost:
        r5 = 3;
     */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x002b  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0051  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int distance(android.view.View r6, int r7, int r8, int r9, int r10, int r11, int r12, int r13, int r14) {
        /*
            r5 = this;
            int r5 = r5.mSide
            r0 = 5
            r1 = 3
            r2 = 0
            r3 = 1
            r4 = 8388611(0x800003, float:1.1754948E-38)
            if (r5 != r4) goto L_0x0019
            int r5 = androidx.core.view.ViewCompat.getLayoutDirection(r6)
            if (r5 != r3) goto L_0x0012
            goto L_0x0013
        L_0x0012:
            r3 = r2
        L_0x0013:
            if (r3 == 0) goto L_0x0017
        L_0x0015:
            r5 = r0
            goto L_0x0029
        L_0x0017:
            r5 = r1
            goto L_0x0029
        L_0x0019:
            r4 = 8388613(0x800005, float:1.175495E-38)
            if (r5 != r4) goto L_0x0029
            int r5 = androidx.core.view.ViewCompat.getLayoutDirection(r6)
            if (r5 != r3) goto L_0x0025
            goto L_0x0026
        L_0x0025:
            r3 = r2
        L_0x0026:
            if (r3 == 0) goto L_0x0015
            goto L_0x0017
        L_0x0029:
            if (r5 == r1) goto L_0x0051
            if (r5 == r0) goto L_0x0048
            r6 = 48
            if (r5 == r6) goto L_0x003f
            r6 = 80
            if (r5 == r6) goto L_0x0036
            goto L_0x0059
        L_0x0036:
            int r8 = r8 - r12
            int r9 = r9 - r7
            int r5 = java.lang.Math.abs(r9)
            int r2 = r8 + r5
            goto L_0x0059
        L_0x003f:
            int r14 = r14 - r8
            int r9 = r9 - r7
            int r5 = java.lang.Math.abs(r9)
            int r2 = r14 + r5
            goto L_0x0059
        L_0x0048:
            int r7 = r7 - r11
            int r10 = r10 - r8
            int r5 = java.lang.Math.abs(r10)
            int r2 = r7 + r5
            goto L_0x0059
        L_0x0051:
            int r13 = r13 - r7
            int r10 = r10 - r8
            int r5 = java.lang.Math.abs(r10)
            int r2 = r13 + r5
        L_0x0059:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.transition.SidePropagation.distance(android.view.View, int, int, int, int, int, int, int, int):int");
    }

    private int getMaxDistance(ViewGroup viewGroup) {
        int i = this.mSide;
        if (i == 3 || i == 5 || i == 8388611 || i == 8388613) {
            return viewGroup.getWidth();
        }
        return viewGroup.getHeight();
    }
}
