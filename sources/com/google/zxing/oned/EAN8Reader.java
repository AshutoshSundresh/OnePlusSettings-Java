package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.BitArray;

public final class EAN8Reader extends UPCEANReader {
    private final int[] decodeMiddleCounters = new int[4];

    /* access modifiers changed from: protected */
    @Override // com.google.zxing.oned.UPCEANReader
    public int decodeMiddle(BitArray bitArray, int[] iArr, StringBuilder sb) throws NotFoundException {
        int[][] iArr2 = UPCEANReader.L_PATTERNS;
        int[] iArr3 = this.decodeMiddleCounters;
        iArr3[0] = 0;
        iArr3[1] = 0;
        iArr3[2] = 0;
        iArr3[3] = 0;
        int size = bitArray.getSize();
        int i = iArr[1];
        for (int i2 = 0; i2 < 4 && i < size; i2++) {
            sb.append((char) (UPCEANReader.decodeDigit(bitArray, iArr3, i, iArr2) + 48));
            for (int i3 : iArr3) {
                i += i3;
            }
        }
        int i4 = UPCEANReader.findGuardPattern(bitArray, i, true, UPCEANReader.MIDDLE_PATTERN)[1];
        for (int i5 = 0; i5 < 4 && i4 < size; i5++) {
            sb.append((char) (UPCEANReader.decodeDigit(bitArray, iArr3, i4, iArr2) + 48));
            for (int i6 : iArr3) {
                i4 += i6;
            }
        }
        return i4;
    }

    /* access modifiers changed from: package-private */
    @Override // com.google.zxing.oned.UPCEANReader
    public BarcodeFormat getBarcodeFormat() {
        return BarcodeFormat.EAN_8;
    }
}
