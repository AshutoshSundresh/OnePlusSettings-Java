package com.google.zxing.aztec.encoder;

import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;

public final class Encoder {
    private static final int[] WORD_SIZE = {4, 6, 6, 8, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12};

    private static int totalBitsInLayer(int i, boolean z) {
        return ((z ? 88 : 112) + (i * 16)) * i;
    }

    public static AztecCode encode(byte[] bArr, int i, int i2) {
        int i3;
        int i4;
        boolean z;
        BitArray bitArray;
        int i5;
        int i6;
        BitArray bitArray2;
        int i7;
        int[] iArr = WORD_SIZE;
        BitArray encode = new HighLevelEncoder(bArr).encode();
        int size = ((encode.getSize() * i) / 100) + 11;
        int size2 = encode.getSize() + size;
        int i8 = 32;
        int i9 = 4;
        boolean z2 = 0;
        if (i2 != 0) {
            z = i2 < 0;
            i3 = Math.abs(i2);
            if (z) {
                i8 = 4;
            }
            if (i3 <= i8) {
                i4 = totalBitsInLayer(i3, z);
                i5 = iArr[i3];
                int i10 = i4 - (i4 % i5);
                bitArray = stuffBits(encode, i5);
                if (bitArray.getSize() + size > i10) {
                    throw new IllegalArgumentException("Data to large for user specified layer");
                } else if (z && bitArray.getSize() > i5 * 64) {
                    throw new IllegalArgumentException("Data to large for user specified layer");
                }
            } else {
                throw new IllegalArgumentException(String.format("Illegal value %s for layers", Integer.valueOf(i2)));
            }
        } else {
            BitArray bitArray3 = null;
            int i11 = 0;
            int i12 = 0;
            while (i11 <= i8) {
                boolean z3 = i11 <= 3 ? true : z2;
                int i13 = z3 ? i11 + 1 : i11;
                int i14 = totalBitsInLayer(i13, z3);
                if (size2 <= i14) {
                    if (i12 != iArr[i13]) {
                        i7 = iArr[i13];
                        bitArray2 = stuffBits(encode, i7);
                    } else {
                        bitArray2 = bitArray3;
                        i7 = i12;
                    }
                    int i15 = i14 - (i14 % i7);
                    if ((!z3 || bitArray2.getSize() <= i7 * 64) && bitArray2.getSize() + size <= i15) {
                        i5 = i7;
                        bitArray = bitArray2;
                        z = z3;
                        i3 = i13;
                        i4 = i14;
                    } else {
                        i12 = i7;
                        bitArray3 = bitArray2;
                    }
                }
                i11++;
                i8 = 32;
                i9 = 4;
                z2 = 0;
            }
            throw new IllegalArgumentException("Data too large for an Aztec code");
        }
        BitArray generateCheckWords = generateCheckWords(bitArray, i4, i5);
        int size3 = bitArray.getSize() / i5;
        BitArray generateModeMessage = generateModeMessage(z, i3, size3);
        int i16 = z ? (i3 * 4) + 11 : (i3 * 4) + 14;
        int[] iArr2 = new int[i16];
        int i17 = 2;
        if (z) {
            for (int i18 = z2; i18 < i16; i18++) {
                iArr2[i18] = i18;
            }
            i6 = i16;
        } else {
            int i19 = i16 / 2;
            i6 = i16 + 1 + (((i19 - 1) / 15) * 2);
            int i20 = i6 / 2;
            for (int i21 = z2; i21 < i19; i21++) {
                int i22 = (i21 / 15) + i21;
                iArr2[(i19 - i21) - 1] = (i20 - i22) - 1;
                iArr2[i19 + i21] = i22 + i20 + 1;
            }
        }
        BitMatrix bitMatrix = new BitMatrix(i6);
        int i23 = z2;
        int i24 = i23;
        while (i23 < i3) {
            int i25 = (i3 - i23) * i9;
            int i26 = z ? i25 + 9 : i25 + 12;
            int i27 = z2;
            while (i27 < i26) {
                int i28 = i27 * 2;
                while (z2 < i17) {
                    if (generateCheckWords.get(i24 + i28 + z2)) {
                        int i29 = i23 * 2;
                        bitMatrix.set(iArr2[i29 + z2], iArr2[i29 + i27]);
                    }
                    if (generateCheckWords.get((i26 * 2) + i24 + i28 + z2)) {
                        int i30 = i23 * 2;
                        bitMatrix.set(iArr2[i30 + i27], iArr2[((i16 - 1) - i30) - z2]);
                    }
                    if (generateCheckWords.get((i26 * 4) + i24 + i28 + z2)) {
                        int i31 = (i16 - 1) - (i23 * 2);
                        bitMatrix.set(iArr2[i31 - z2], iArr2[i31 - i27]);
                    }
                    if (generateCheckWords.get((i26 * 6) + i24 + i28 + z2)) {
                        int i32 = i23 * 2;
                        bitMatrix.set(iArr2[((i16 - 1) - i32) - i27], iArr2[i32 + z2]);
                    }
                    z2++;
                    i17 = 2;
                }
                i27++;
                z2 = 0;
                i17 = 2;
            }
            i24 += i26 * 8;
            i23++;
            i9 = 4;
            z2 = 0;
            i17 = 2;
        }
        drawModeMessage(bitMatrix, z, i6, generateModeMessage);
        if (z) {
            drawBullsEye(bitMatrix, i6 / 2, 5);
        } else {
            int i33 = i6 / 2;
            drawBullsEye(bitMatrix, i33, 7);
            int i34 = 0;
            int i35 = 0;
            while (i34 < (i16 / 2) - 1) {
                for (int i36 = i33 & 1; i36 < i6; i36 += 2) {
                    int i37 = i33 - i35;
                    bitMatrix.set(i37, i36);
                    int i38 = i33 + i35;
                    bitMatrix.set(i38, i36);
                    bitMatrix.set(i36, i37);
                    bitMatrix.set(i36, i38);
                }
                i34 += 15;
                i35 += 16;
            }
        }
        AztecCode aztecCode = new AztecCode();
        aztecCode.setCompact(z);
        aztecCode.setSize(i6);
        aztecCode.setLayers(i3);
        aztecCode.setCodeWords(size3);
        aztecCode.setMatrix(bitMatrix);
        return aztecCode;
    }

    private static void drawBullsEye(BitMatrix bitMatrix, int i, int i2) {
        for (int i3 = 0; i3 < i2; i3 += 2) {
            int i4 = i - i3;
            int i5 = i4;
            while (true) {
                int i6 = i + i3;
                if (i5 > i6) {
                    break;
                }
                bitMatrix.set(i5, i4);
                bitMatrix.set(i5, i6);
                bitMatrix.set(i4, i5);
                bitMatrix.set(i6, i5);
                i5++;
            }
        }
        int i7 = i - i2;
        bitMatrix.set(i7, i7);
        int i8 = i7 + 1;
        bitMatrix.set(i8, i7);
        bitMatrix.set(i7, i8);
        int i9 = i + i2;
        bitMatrix.set(i9, i7);
        bitMatrix.set(i9, i8);
        bitMatrix.set(i9, i9 - 1);
    }

    static BitArray generateModeMessage(boolean z, int i, int i2) {
        BitArray bitArray = new BitArray();
        if (z) {
            bitArray.appendBits(i - 1, 2);
            bitArray.appendBits(i2 - 1, 6);
            return generateCheckWords(bitArray, 28, 4);
        }
        bitArray.appendBits(i - 1, 5);
        bitArray.appendBits(i2 - 1, 11);
        return generateCheckWords(bitArray, 40, 4);
    }

    private static void drawModeMessage(BitMatrix bitMatrix, boolean z, int i, BitArray bitArray) {
        int i2 = i / 2;
        int i3 = 0;
        if (z) {
            while (i3 < 7) {
                int i4 = (i2 - 3) + i3;
                if (bitArray.get(i3)) {
                    bitMatrix.set(i4, i2 - 5);
                }
                if (bitArray.get(i3 + 7)) {
                    bitMatrix.set(i2 + 5, i4);
                }
                if (bitArray.get(20 - i3)) {
                    bitMatrix.set(i4, i2 + 5);
                }
                if (bitArray.get(27 - i3)) {
                    bitMatrix.set(i2 - 5, i4);
                }
                i3++;
            }
            return;
        }
        while (i3 < 10) {
            int i5 = (i2 - 5) + i3 + (i3 / 5);
            if (bitArray.get(i3)) {
                bitMatrix.set(i5, i2 - 7);
            }
            if (bitArray.get(i3 + 10)) {
                bitMatrix.set(i2 + 7, i5);
            }
            if (bitArray.get(29 - i3)) {
                bitMatrix.set(i5, i2 + 7);
            }
            if (bitArray.get(39 - i3)) {
                bitMatrix.set(i2 - 7, i5);
            }
            i3++;
        }
    }

    private static BitArray generateCheckWords(BitArray bitArray, int i, int i2) {
        ReedSolomonEncoder reedSolomonEncoder = new ReedSolomonEncoder(getGF(i2));
        int i3 = i / i2;
        int[] bitsToWords = bitsToWords(bitArray, i2, i3);
        reedSolomonEncoder.encode(bitsToWords, i3 - (bitArray.getSize() / i2));
        BitArray bitArray2 = new BitArray();
        bitArray2.appendBits(0, i % i2);
        for (int i4 : bitsToWords) {
            bitArray2.appendBits(i4, i2);
        }
        return bitArray2;
    }

    private static int[] bitsToWords(BitArray bitArray, int i, int i2) {
        int[] iArr = new int[i2];
        int size = bitArray.getSize() / i;
        for (int i3 = 0; i3 < size; i3++) {
            int i4 = 0;
            for (int i5 = 0; i5 < i; i5++) {
                i4 |= bitArray.get((i3 * i) + i5) ? 1 << ((i - i5) - 1) : 0;
            }
            iArr[i3] = i4;
        }
        return iArr;
    }

    private static GenericGF getGF(int i) {
        if (i == 4) {
            return GenericGF.AZTEC_PARAM;
        }
        if (i == 6) {
            return GenericGF.AZTEC_DATA_6;
        }
        if (i == 8) {
            return GenericGF.AZTEC_DATA_8;
        }
        if (i == 10) {
            return GenericGF.AZTEC_DATA_10;
        }
        if (i != 12) {
            return null;
        }
        return GenericGF.AZTEC_DATA_12;
    }

    static BitArray stuffBits(BitArray bitArray, int i) {
        BitArray bitArray2 = new BitArray();
        int size = bitArray.getSize();
        int i2 = (1 << i) - 2;
        int i3 = 0;
        while (i3 < size) {
            int i4 = 0;
            for (int i5 = 0; i5 < i; i5++) {
                int i6 = i3 + i5;
                if (i6 >= size || bitArray.get(i6)) {
                    i4 |= 1 << ((i - 1) - i5);
                }
            }
            int i7 = i4 & i2;
            if (i7 == i2) {
                bitArray2.appendBits(i7, i);
            } else if (i7 == 0) {
                bitArray2.appendBits(i4 | 1, i);
            } else {
                bitArray2.appendBits(i4, i);
                i3 += i;
            }
            i3--;
            i3 += i;
        }
        return bitArray2;
    }
}
