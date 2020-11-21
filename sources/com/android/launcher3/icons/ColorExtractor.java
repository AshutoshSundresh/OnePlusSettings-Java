package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseArray;
import java.util.Arrays;

public class ColorExtractor {
    private final float[] mTmpHsv = new float[3];
    private final float[] mTmpHueScoreHistogram = new float[360];
    private final int[] mTmpPixels = new int[20];
    private final SparseArray<Float> mTmpRgbScores = new SparseArray<>();

    public int findDominantColorByHue(Bitmap bitmap) {
        return findDominantColorByHue(bitmap, 20);
    }

    public int findDominantColorByHue(Bitmap bitmap, int i) {
        int i2;
        int i3;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int sqrt = (int) Math.sqrt((double) ((height * width) / i));
        if (sqrt < 1) {
            sqrt = 1;
        }
        float[] fArr = this.mTmpHsv;
        Arrays.fill(fArr, 0.0f);
        float[] fArr2 = this.mTmpHueScoreHistogram;
        Arrays.fill(fArr2, 0.0f);
        int i4 = -1;
        int[] iArr = this.mTmpPixels;
        int i5 = 0;
        Arrays.fill(iArr, 0);
        int i6 = 0;
        int i7 = 0;
        float f = -1.0f;
        while (true) {
            i2 = -16777216;
            if (i6 >= height) {
                break;
            }
            int i8 = i5;
            while (i8 < width) {
                int pixel = bitmap.getPixel(i8, i6);
                if (((pixel >> 24) & 255) < 128) {
                    i3 = height;
                } else {
                    int i9 = pixel | -16777216;
                    Color.colorToHSV(i9, fArr);
                    i3 = height;
                    int i10 = (int) fArr[0];
                    if (i10 >= 0 && i10 < fArr2.length) {
                        if (i7 < i) {
                            iArr[i7] = i9;
                            i7++;
                        }
                        fArr2[i10] = fArr2[i10] + (fArr[1] * fArr[2]);
                        if (fArr2[i10] > f) {
                            i4 = i10;
                            f = fArr2[i10];
                        }
                    }
                }
                i8 += sqrt;
                height = i3;
            }
            i6 += sqrt;
            i5 = 0;
        }
        SparseArray<Float> sparseArray = this.mTmpRgbScores;
        sparseArray.clear();
        float f2 = -1.0f;
        for (int i11 = 0; i11 < i7; i11++) {
            int i12 = iArr[i11];
            Color.colorToHSV(i12, fArr);
            if (((int) fArr[0]) == i4) {
                float f3 = fArr[1];
                float f4 = fArr[2];
                int i13 = ((int) (100.0f * f3)) + ((int) (10000.0f * f4));
                float f5 = f3 * f4;
                Float f6 = sparseArray.get(i13);
                if (f6 != null) {
                    f5 += f6.floatValue();
                }
                sparseArray.put(i13, Float.valueOf(f5));
                if (f5 > f2) {
                    i2 = i12;
                    f2 = f5;
                }
            }
        }
        return i2;
    }
}
