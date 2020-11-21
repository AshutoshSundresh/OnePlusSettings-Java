package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;

/* access modifiers changed from: package-private */
public class Chain {
    static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i) {
        ChainHead[] chainHeadArr;
        int i2;
        int i3;
        if (i == 0) {
            int i4 = constraintWidgetContainer.mHorizontalChainsSize;
            chainHeadArr = constraintWidgetContainer.mHorizontalChainsArray;
            i2 = i4;
            i3 = 0;
        } else {
            i3 = 2;
            i2 = constraintWidgetContainer.mVerticalChainsSize;
            chainHeadArr = constraintWidgetContainer.mVerticalChainsArray;
        }
        for (int i5 = 0; i5 < i2; i5++) {
            ChainHead chainHead = chainHeadArr[i5];
            chainHead.define();
            applyChainConstraints(constraintWidgetContainer, linearSystem, i, i3, chainHead);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v58, resolved type: androidx.constraintlayout.solver.widgets.ConstraintWidget */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0035, code lost:
        if (r2.mHorizontalChainStyle == 2) goto L_0x004a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0048, code lost:
        if (r2.mVerticalChainStyle == 2) goto L_0x004a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x004c, code lost:
        r5 = false;
     */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x019d  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x01c4  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01d6  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0267 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02bc A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x03ad  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x03be  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x03cb  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0495  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x04ca  */
    /* JADX WARNING: Removed duplicated region for block: B:282:0x04dd A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x04ef  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x04f2  */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x04f8  */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x04fb  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x04ff  */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x050e  */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x0511  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x051e A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x03ae A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:325:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void applyChainConstraints(androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer r37, androidx.constraintlayout.solver.LinearSystem r38, int r39, int r40, androidx.constraintlayout.solver.widgets.ChainHead r41) {
        /*
        // Method dump skipped, instructions count: 1346
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.solver.widgets.Chain.applyChainConstraints(androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer, androidx.constraintlayout.solver.LinearSystem, int, int, androidx.constraintlayout.solver.widgets.ChainHead):void");
    }
}
