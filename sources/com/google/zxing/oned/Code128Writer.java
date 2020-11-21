package com.google.zxing.oned;

import androidx.constraintlayout.widget.R$styleable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.ArrayList;
import java.util.Map;

public final class Code128Writer extends OneDimensionalCodeWriter {
    @Override // com.google.zxing.oned.OneDimensionalCodeWriter, com.google.zxing.Writer
    public BitMatrix encode(String str, BarcodeFormat barcodeFormat, int i, int i2, Map<EncodeHintType, ?> map) throws WriterException {
        if (barcodeFormat == BarcodeFormat.CODE_128) {
            return super.encode(str, barcodeFormat, i, i2, map);
        }
        throw new IllegalArgumentException("Can only encode CODE_128, but got " + barcodeFormat);
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.google.zxing.oned.OneDimensionalCodeWriter
    public boolean[] encode(String str) {
        int length = str.length();
        if (length < 1 || length > 80) {
            throw new IllegalArgumentException("Contents length should be between 1 and 80 characters, but got " + length);
        }
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            char charAt = str.charAt(i2);
            if (charAt < ' ' || charAt > '~') {
                switch (charAt) {
                    default:
                        throw new IllegalArgumentException("Bad character in input: " + charAt);
                    case 241:
                    case 242:
                    case 243:
                    case 244:
                        break;
                }
            }
        }
        ArrayList<int[]> arrayList = new ArrayList();
        int i3 = 1;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (i4 < length) {
            int i7 = 99;
            int i8 = 100;
            if (!isDigits(str, i4, i6 == 99 ? 2 : 4)) {
                i7 = 100;
            }
            if (i7 == i6) {
                if (i6 != 100) {
                    switch (str.charAt(i4)) {
                        case 241:
                            i8 = R$styleable.Constraint_layout_goneMarginStart;
                            break;
                        case 242:
                            i8 = 97;
                            break;
                        case 243:
                            i8 = 96;
                            break;
                        case 244:
                            break;
                        default:
                            int i9 = i4 + 2;
                            i8 = Integer.parseInt(str.substring(i4, i9));
                            i4 = i9;
                            break;
                    }
                } else {
                    i8 = str.charAt(i4) - ' ';
                }
                i4++;
            } else {
                i8 = i6 == 0 ? i7 == 100 ? R$styleable.Constraint_motionStagger : R$styleable.Constraint_pathMotionArc : i7;
                i6 = i7;
            }
            arrayList.add(Code128Reader.CODE_PATTERNS[i8]);
            i5 += i8 * i3;
            if (i4 != 0) {
                i3++;
            }
        }
        arrayList.add(Code128Reader.CODE_PATTERNS[i5 % R$styleable.Constraint_layout_goneMarginTop]);
        arrayList.add(Code128Reader.CODE_PATTERNS[106]);
        int i10 = 0;
        for (int[] iArr : arrayList) {
            for (int i11 : iArr) {
                i10 += i11;
            }
        }
        boolean[] zArr = new boolean[i10];
        for (int[] iArr2 : arrayList) {
            i += OneDimensionalCodeWriter.appendPattern(zArr, i, iArr2, true);
        }
        return zArr;
    }

    private static boolean isDigits(CharSequence charSequence, int i, int i2) {
        int i3 = i2 + i;
        int length = charSequence.length();
        while (i < i3 && i < length) {
            char charAt = charSequence.charAt(i);
            if (charAt < '0' || charAt > '9') {
                if (charAt != 241) {
                    return false;
                }
                i3++;
            }
            i++;
        }
        if (i3 <= length) {
            return true;
        }
        return false;
    }
}
