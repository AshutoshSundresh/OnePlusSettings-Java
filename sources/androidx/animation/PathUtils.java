package androidx.animation;

import java.util.List;

class PathUtils {
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0182 A[LOOP:1: B:8:0x005f->B:34:0x0182, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x018d A[EDGE_INSN: B:43:0x018d->B:36:0x018d ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static float[] createKeyFrameData(android.graphics.Path r22, float r23) {
        /*
        // Method dump skipped, instructions count: 426
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.animation.PathUtils.createKeyFrameData(android.graphics.Path, float):float[]");
    }

    private static boolean twoPointsOnTheSameLinePath(float[] fArr, float[] fArr2, float f, float f2, float f3, float f4) {
        return Math.abs(fArr[0] - fArr2[0]) <= 1.0E-4f && Math.abs(fArr[1] - fArr2[1]) <= 1.0E-4f && Math.abs(((f - f3) * fArr[1]) - ((f2 - f4) * fArr[0])) < 1.0E-4f;
    }

    private static void addDataEntry(List<Float> list, float f, float f2, float f3) {
        list.add(Float.valueOf(f));
        list.add(Float.valueOf(f2));
        list.add(Float.valueOf(f3));
    }
}
