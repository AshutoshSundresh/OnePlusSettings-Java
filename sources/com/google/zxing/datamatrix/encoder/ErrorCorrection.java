package com.google.zxing.datamatrix.encoder;

import androidx.appcompat.R$styleable;

public final class ErrorCorrection {
    private static final int[] ALOG = new int[255];
    private static final int[][] FACTORS = {new int[]{228, 48, 15, 111, 62}, new int[]{23, 68, 144, 134, 240, 92, 254}, new int[]{28, 24, 185, 166, 223, 248, R$styleable.AppCompatTheme_viewInflaterClass, 255, androidx.constraintlayout.widget.R$styleable.Constraint_visibilityMode, 61}, new int[]{175, 138, 205, 12, 194, 168, 39, 245, 60, 97, R$styleable.AppCompatTheme_windowFixedHeightMajor}, new int[]{41, 153, 158, 91, 61, 42, 142, 213, 97, 178, 100, 242}, new int[]{156, 97, 192, 252, 95, 9, 157, R$styleable.AppCompatTheme_windowActionModeOverlay, 138, 45, 18, 186, 83, 185}, new int[]{83, 195, 100, 39, 188, 75, 66, 61, 241, 213, androidx.constraintlayout.widget.R$styleable.Constraint_transitionPathRotate, 129, 94, 254, 225, 48, 90, 188}, new int[]{15, 195, 244, 9, 233, 71, 168, 2, 188, 160, 153, 145, 253, 79, androidx.constraintlayout.widget.R$styleable.Constraint_transitionEasing, 82, 27, 174, 186, 172}, new int[]{52, 190, 88, 205, androidx.constraintlayout.widget.R$styleable.Constraint_transitionPathRotate, 39, 176, 21, 155, 197, 251, 223, 155, 21, 5, 172, 254, R$styleable.AppCompatTheme_windowMinWidthMajor, 12, 181, 184, 96, 50, 193}, new int[]{211, 231, 43, 97, 71, 96, androidx.constraintlayout.widget.R$styleable.Constraint_layout_goneMarginTop, 174, 37, 151, 170, 53, 75, 34, 249, R$styleable.AppCompatTheme_windowFixedHeightMinor, 17, 138, androidx.constraintlayout.widget.R$styleable.Constraint_visibilityMode, 213, 141, 136, R$styleable.AppCompatTheme_windowFixedHeightMajor, 151, 233, 168, 93, 255}, new int[]{245, 127, 242, 218, 130, 250, 162, 181, androidx.constraintlayout.widget.R$styleable.Constraint_layout_goneMarginStart, R$styleable.AppCompatTheme_windowFixedHeightMajor, 84, 179, 220, 251, 80, 182, 229, 18, 2, 4, 68, 33, androidx.constraintlayout.widget.R$styleable.Constraint_layout_goneMarginRight, 137, 95, R$styleable.AppCompatTheme_windowActionModeOverlay, 115, 44, 175, 184, 59, 25, 225, 98, 81, 112}, new int[]{77, 193, 137, 31, 19, 38, 22, 153, 247, androidx.constraintlayout.widget.R$styleable.Constraint_pathMotionArc, R$styleable.AppCompatTheme_windowFixedWidthMajor, 2, 245, 133, 242, 8, 175, 95, 100, 9, 167, androidx.constraintlayout.widget.R$styleable.Constraint_pathMotionArc, 214, 111, 57, R$styleable.AppCompatTheme_windowFixedHeightMinor, 21, 1, 253, 57, 54, androidx.constraintlayout.widget.R$styleable.Constraint_layout_goneMarginRight, 248, 202, 69, 50, 150, 177, 226, 5, 9, 5}, new int[]{245, 132, 172, 223, 96, 32, R$styleable.AppCompatTheme_windowActionBar, 22, 238, 133, 238, 231, 205, 188, 237, 87, 191, 106, 16, 147, R$styleable.AppCompatTheme_windowActionBarOverlay, 23, 37, 90, 170, 205, 131, 88, R$styleable.AppCompatTheme_windowFixedHeightMajor, 100, 66, 138, 186, 240, 82, 44, 176, 87, 187, 147, 160, 175, 69, 213, 92, 253, 225, 19}, new int[]{175, 9, 223, 238, 12, 17, 220, 208, 100, 29, 175, 170, 230, 192, 215, 235, 150, 159, 36, 223, 38, 200, 132, 54, 228, 146, 218, 234, R$styleable.AppCompatTheme_windowActionBar, 203, 29, 232, 144, 238, 22, 150, 201, R$styleable.AppCompatTheme_windowActionBar, 62, 207, 164, 13, 137, 245, 127, 67, 247, 28, 155, 43, 203, androidx.constraintlayout.widget.R$styleable.Constraint_progress, 233, 53, 143, 46}, new int[]{242, 93, 169, 50, 144, 210, 39, R$styleable.AppCompatTheme_windowActionBarOverlay, 202, 188, 201, 189, 143, androidx.constraintlayout.widget.R$styleable.Constraint_transitionEasing, 196, 37, 185, 112, 134, 230, 245, 63, 197, 190, 250, 106, 185, 221, 175, 64, 114, 71, 161, 44, 147, 6, 27, 218, 51, 63, 87, 10, 40, 130, 188, 17, 163, 31, 176, 170, 4, androidx.constraintlayout.widget.R$styleable.Constraint_progress, 232, 7, 94, 166, 224, R$styleable.AppCompatTheme_windowMinWidthMajor, 86, 47, 11, 204}, new int[]{220, 228, 173, 89, 251, 149, 159, 56, 89, 33, 147, 244, 154, 36, 73, 127, 213, 136, 248, 180, 234, 197, 158, 177, 68, R$styleable.AppCompatTheme_windowFixedWidthMajor, 93, 213, 15, 160, 227, 236, 66, 139, 153, 185, 202, 167, 179, 25, 220, 232, 96, 210, 231, 136, 223, 239, 181, 241, 59, 52, 172, 25, 49, 232, 211, 189, 64, 54, androidx.constraintlayout.widget.R$styleable.Constraint_transitionEasing, 153, 132, 63, 96, androidx.constraintlayout.widget.R$styleable.Constraint_layout_goneMarginTop, 82, 186}};
    private static final int[] FACTOR_SETS = {5, 7, 10, 11, 12, 14, 18, 20, 24, 28, 36, 42, 48, 56, 62, 68};
    private static final int[] LOG = new int[256];

    static {
        int i = 1;
        for (int i2 = 0; i2 < 255; i2++) {
            ALOG[i2] = i;
            LOG[i] = i2;
            i <<= 1;
            if (i >= 256) {
                i ^= 301;
            }
        }
    }

    public static String encodeECC200(String str, SymbolInfo symbolInfo) {
        if (str.length() == symbolInfo.getDataCapacity()) {
            StringBuilder sb = new StringBuilder(symbolInfo.getDataCapacity() + symbolInfo.getErrorCodewords());
            sb.append(str);
            int interleavedBlockCount = symbolInfo.getInterleavedBlockCount();
            if (interleavedBlockCount == 1) {
                sb.append(createECCBlock(str, symbolInfo.getErrorCodewords()));
            } else {
                sb.setLength(sb.capacity());
                int[] iArr = new int[interleavedBlockCount];
                int[] iArr2 = new int[interleavedBlockCount];
                int[] iArr3 = new int[interleavedBlockCount];
                int i = 0;
                while (i < interleavedBlockCount) {
                    int i2 = i + 1;
                    iArr[i] = symbolInfo.getDataLengthForInterleavedBlock(i2);
                    iArr2[i] = symbolInfo.getErrorLengthForInterleavedBlock(i2);
                    iArr3[i] = 0;
                    if (i > 0) {
                        iArr3[i] = iArr3[i - 1] + iArr[i];
                    }
                    i = i2;
                }
                for (int i3 = 0; i3 < interleavedBlockCount; i3++) {
                    StringBuilder sb2 = new StringBuilder(iArr[i3]);
                    for (int i4 = i3; i4 < symbolInfo.getDataCapacity(); i4 += interleavedBlockCount) {
                        sb2.append(str.charAt(i4));
                    }
                    String createECCBlock = createECCBlock(sb2.toString(), iArr2[i3]);
                    int i5 = i3;
                    int i6 = 0;
                    while (i5 < iArr2[i3] * interleavedBlockCount) {
                        sb.setCharAt(symbolInfo.getDataCapacity() + i5, createECCBlock.charAt(i6));
                        i5 += interleavedBlockCount;
                        i6++;
                    }
                }
            }
            return sb.toString();
        }
        throw new IllegalArgumentException("The number of codewords does not match the selected symbol");
    }

    private static String createECCBlock(CharSequence charSequence, int i) {
        return createECCBlock(charSequence, 0, charSequence.length(), i);
    }

    private static String createECCBlock(CharSequence charSequence, int i, int i2, int i3) {
        int[] iArr = LOG;
        int[] iArr2 = ALOG;
        int i4 = 0;
        while (true) {
            int[] iArr3 = FACTOR_SETS;
            if (i4 >= iArr3.length) {
                i4 = -1;
                break;
            } else if (iArr3[i4] == i3) {
                break;
            } else {
                i4++;
            }
        }
        if (i4 >= 0) {
            int[] iArr4 = FACTORS[i4];
            char[] cArr = new char[i3];
            for (int i5 = 0; i5 < i3; i5++) {
                cArr[i5] = 0;
            }
            for (int i6 = i; i6 < i + i2; i6++) {
                int i7 = i3 - 1;
                int charAt = cArr[i7] ^ charSequence.charAt(i6);
                while (i7 > 0) {
                    if (charAt == 0 || iArr4[i7] == 0) {
                        cArr[i7] = cArr[i7 - 1];
                    } else {
                        cArr[i7] = (char) (cArr[i7 - 1] ^ iArr2[(iArr[charAt] + iArr[iArr4[i7]]) % 255]);
                    }
                    i7--;
                }
                if (charAt == 0 || iArr4[0] == 0) {
                    cArr[0] = 0;
                } else {
                    cArr[0] = (char) iArr2[(iArr[charAt] + iArr[iArr4[0]]) % 255];
                }
            }
            char[] cArr2 = new char[i3];
            for (int i8 = 0; i8 < i3; i8++) {
                cArr2[i8] = cArr[(i3 - i8) - 1];
            }
            return String.valueOf(cArr2);
        }
        throw new IllegalArgumentException("Illegal number of error correction codewords specified: " + i3);
    }
}
