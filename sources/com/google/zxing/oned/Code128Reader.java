package com.google.zxing.oned;

import androidx.constraintlayout.widget.R$styleable;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.ArrayList;
import java.util.Map;

public final class Code128Reader extends OneDReader {
    static final int[][] CODE_PATTERNS;

    static {
        int[][] iArr = new int[R$styleable.Constraint_progress][];
        iArr[0] = new int[]{2, 1, 2, 2, 2, 2};
        iArr[1] = new int[]{2, 2, 2, 1, 2, 2};
        iArr[2] = new int[]{2, 2, 2, 2, 2, 1};
        iArr[3] = new int[]{1, 2, 1, 2, 2, 3};
        iArr[4] = new int[]{1, 2, 1, 3, 2, 2};
        iArr[5] = new int[]{1, 3, 1, 2, 2, 2};
        iArr[6] = new int[]{1, 2, 2, 2, 1, 3};
        iArr[7] = new int[]{1, 2, 2, 3, 1, 2};
        iArr[8] = new int[]{1, 3, 2, 2, 1, 2};
        iArr[9] = new int[]{2, 2, 1, 2, 1, 3};
        iArr[10] = new int[]{2, 2, 1, 3, 1, 2};
        iArr[11] = new int[]{2, 3, 1, 2, 1, 2};
        iArr[12] = new int[]{1, 1, 2, 2, 3, 2};
        iArr[13] = new int[]{1, 2, 2, 1, 3, 2};
        iArr[14] = new int[]{1, 2, 2, 2, 3, 1};
        iArr[15] = new int[]{1, 1, 3, 2, 2, 2};
        iArr[16] = new int[]{1, 2, 3, 1, 2, 2};
        iArr[17] = new int[]{1, 2, 3, 2, 2, 1};
        iArr[18] = new int[]{2, 2, 3, 2, 1, 1};
        iArr[19] = new int[]{2, 2, 1, 1, 3, 2};
        iArr[20] = new int[]{2, 2, 1, 2, 3, 1};
        iArr[21] = new int[]{2, 1, 3, 2, 1, 2};
        iArr[22] = new int[]{2, 2, 3, 1, 1, 2};
        iArr[23] = new int[]{3, 1, 2, 1, 3, 1};
        iArr[24] = new int[]{3, 1, 1, 2, 2, 2};
        iArr[25] = new int[]{3, 2, 1, 1, 2, 2};
        iArr[26] = new int[]{3, 2, 1, 2, 2, 1};
        iArr[27] = new int[]{3, 1, 2, 2, 1, 2};
        iArr[28] = new int[]{3, 2, 2, 1, 1, 2};
        iArr[29] = new int[]{3, 2, 2, 2, 1, 1};
        iArr[30] = new int[]{2, 1, 2, 1, 2, 3};
        iArr[31] = new int[]{2, 1, 2, 3, 2, 1};
        iArr[32] = new int[]{2, 3, 2, 1, 2, 1};
        iArr[33] = new int[]{1, 1, 1, 3, 2, 3};
        iArr[34] = new int[]{1, 3, 1, 1, 2, 3};
        iArr[35] = new int[]{1, 3, 1, 3, 2, 1};
        iArr[36] = new int[]{1, 1, 2, 3, 1, 3};
        iArr[37] = new int[]{1, 3, 2, 1, 1, 3};
        iArr[38] = new int[]{1, 3, 2, 3, 1, 1};
        iArr[39] = new int[]{2, 1, 1, 3, 1, 3};
        iArr[40] = new int[]{2, 3, 1, 1, 1, 3};
        iArr[41] = new int[]{2, 3, 1, 3, 1, 1};
        iArr[42] = new int[]{1, 1, 2, 1, 3, 3};
        iArr[43] = new int[]{1, 1, 2, 3, 3, 1};
        iArr[44] = new int[]{1, 3, 2, 1, 3, 1};
        iArr[45] = new int[]{1, 1, 3, 1, 2, 3};
        iArr[46] = new int[]{1, 1, 3, 3, 2, 1};
        iArr[47] = new int[]{1, 3, 3, 1, 2, 1};
        iArr[48] = new int[]{3, 1, 3, 1, 2, 1};
        iArr[49] = new int[]{2, 1, 1, 3, 3, 1};
        iArr[50] = new int[]{2, 3, 1, 1, 3, 1};
        iArr[51] = new int[]{2, 1, 3, 1, 1, 3};
        iArr[52] = new int[]{2, 1, 3, 3, 1, 1};
        iArr[53] = new int[]{2, 1, 3, 1, 3, 1};
        iArr[54] = new int[]{3, 1, 1, 1, 2, 3};
        iArr[55] = new int[]{3, 1, 1, 3, 2, 1};
        iArr[56] = new int[]{3, 3, 1, 1, 2, 1};
        iArr[57] = new int[]{3, 1, 2, 1, 1, 3};
        iArr[58] = new int[]{3, 1, 2, 3, 1, 1};
        iArr[59] = new int[]{3, 3, 2, 1, 1, 1};
        iArr[60] = new int[]{3, 1, 4, 1, 1, 1};
        iArr[61] = new int[]{2, 2, 1, 4, 1, 1};
        iArr[62] = new int[]{4, 3, 1, 1, 1, 1};
        iArr[63] = new int[]{1, 1, 1, 2, 2, 4};
        iArr[64] = new int[]{1, 1, 1, 4, 2, 2};
        iArr[65] = new int[]{1, 2, 1, 1, 2, 4};
        iArr[66] = new int[]{1, 2, 1, 4, 2, 1};
        iArr[67] = new int[]{1, 4, 1, 1, 2, 2};
        iArr[68] = new int[]{1, 4, 1, 2, 2, 1};
        iArr[69] = new int[]{1, 1, 2, 2, 1, 4};
        iArr[70] = new int[]{1, 1, 2, 4, 1, 2};
        iArr[71] = new int[]{1, 2, 2, 1, 1, 4};
        iArr[72] = new int[]{1, 2, 2, 4, 1, 1};
        iArr[73] = new int[]{1, 4, 2, 1, 1, 2};
        iArr[74] = new int[]{1, 4, 2, 2, 1, 1};
        iArr[75] = new int[]{2, 4, 1, 2, 1, 1};
        iArr[76] = new int[]{2, 2, 1, 1, 1, 4};
        iArr[77] = new int[]{4, 1, 3, 1, 1, 1};
        iArr[78] = new int[]{2, 4, 1, 1, 1, 2};
        iArr[79] = new int[]{1, 3, 4, 1, 1, 1};
        iArr[80] = new int[]{1, 1, 1, 2, 4, 2};
        iArr[81] = new int[]{1, 2, 1, 1, 4, 2};
        iArr[82] = new int[]{1, 2, 1, 2, 4, 1};
        iArr[83] = new int[]{1, 1, 4, 2, 1, 2};
        iArr[84] = new int[]{1, 2, 4, 1, 1, 2};
        iArr[85] = new int[]{1, 2, 4, 2, 1, 1};
        iArr[86] = new int[]{4, 1, 1, 2, 1, 2};
        iArr[87] = new int[]{4, 2, 1, 1, 1, 2};
        iArr[88] = new int[]{4, 2, 1, 2, 1, 1};
        iArr[89] = new int[]{2, 1, 2, 1, 4, 1};
        iArr[90] = new int[]{2, 1, 4, 1, 2, 1};
        iArr[91] = new int[]{4, 1, 2, 1, 2, 1};
        iArr[92] = new int[]{1, 1, 1, 1, 4, 3};
        iArr[93] = new int[]{1, 1, 1, 3, 4, 1};
        iArr[94] = new int[]{1, 3, 1, 1, 4, 1};
        iArr[95] = new int[]{1, 1, 4, 1, 1, 3};
        iArr[96] = new int[]{1, 1, 4, 3, 1, 1};
        iArr[97] = new int[]{4, 1, 1, 1, 1, 3};
        iArr[98] = new int[]{4, 1, 1, 3, 1, 1};
        iArr[99] = new int[]{1, 1, 3, 1, 4, 1};
        iArr[100] = new int[]{1, 1, 4, 1, 3, 1};
        iArr[101] = new int[]{3, 1, 1, 1, 4, 1};
        iArr[102] = new int[]{4, 1, 1, 1, 3, 1};
        iArr[103] = new int[]{2, 1, 1, 4, 1, 2};
        iArr[104] = new int[]{2, 1, 1, 2, 1, 4};
        iArr[105] = new int[]{2, 1, 1, 2, 3, 2};
        iArr[106] = new int[]{2, 3, 3, 1, 1, 1, 2};
        CODE_PATTERNS = iArr;
    }

    private static int[] findStartPattern(BitArray bitArray) throws NotFoundException {
        int size = bitArray.getSize();
        int nextSet = bitArray.getNextSet(0);
        int[] iArr = new int[6];
        boolean z = false;
        int i = 0;
        int i2 = nextSet;
        while (nextSet < size) {
            if (bitArray.get(nextSet) ^ z) {
                iArr[i] = iArr[i] + 1;
            } else {
                if (i == 5) {
                    int i3 = 64;
                    int i4 = -1;
                    for (int i5 = R$styleable.Constraint_layout_goneMarginTop; i5 <= 105; i5++) {
                        int patternMatchVariance = OneDReader.patternMatchVariance(iArr, CODE_PATTERNS[i5], 179);
                        if (patternMatchVariance < i3) {
                            i4 = i5;
                            i3 = patternMatchVariance;
                        }
                    }
                    if (i4 < 0 || !bitArray.isRange(Math.max(0, i2 - ((nextSet - i2) / 2)), i2, false)) {
                        i2 += iArr[0] + iArr[1];
                        System.arraycopy(iArr, 2, iArr, 0, 4);
                        iArr[4] = 0;
                        iArr[5] = 0;
                        i--;
                    } else {
                        return new int[]{i2, nextSet, i4};
                    }
                } else {
                    i++;
                }
                iArr[i] = 1;
                z = !z;
            }
            nextSet++;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    private static int decodeCode(BitArray bitArray, int[] iArr, int i) throws NotFoundException {
        OneDReader.recordPattern(bitArray, i, iArr);
        int i2 = 64;
        int i3 = -1;
        int i4 = 0;
        while (true) {
            int[][] iArr2 = CODE_PATTERNS;
            if (i4 >= iArr2.length) {
                break;
            }
            int patternMatchVariance = OneDReader.patternMatchVariance(iArr, iArr2[i4], 179);
            if (patternMatchVariance < i2) {
                i3 = i4;
                i2 = patternMatchVariance;
            }
            i4++;
        }
        if (i3 >= 0) {
            return i3;
        }
        throw NotFoundException.getNotFoundInstance();
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.google.zxing.oned.OneDReader
    public Result decodeRow(int i, BitArray bitArray, Map<DecodeHintType, ?> map) throws NotFoundException, FormatException, ChecksumException {
        char c;
        char c2;
        boolean z;
        boolean z2 = map != null && map.containsKey(DecodeHintType.ASSUME_GS1);
        int[] findStartPattern = findStartPattern(bitArray);
        int i2 = findStartPattern[2];
        switch (i2) {
            case R$styleable.Constraint_layout_goneMarginTop /* 103 */:
                c = 'e';
                break;
            case R$styleable.Constraint_motionStagger /* 104 */:
                c = 'd';
                break;
            case R$styleable.Constraint_pathMotionArc /* 105 */:
                c = 'c';
                break;
            default:
                throw FormatException.getFormatInstance();
        }
        StringBuilder sb = new StringBuilder(20);
        ArrayList arrayList = new ArrayList(20);
        int i3 = 6;
        int[] iArr = new int[6];
        boolean z3 = false;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = findStartPattern[0];
        int i8 = i2;
        int i9 = findStartPattern[1];
        boolean z4 = false;
        char c3 = c;
        boolean z5 = true;
        while (!z4) {
            int decodeCode = decodeCode(bitArray, iArr, i9);
            arrayList.add(Byte.valueOf((byte) decodeCode));
            if (decodeCode != 106) {
                z5 = true;
            }
            if (decodeCode != 106) {
                i5++;
                i8 += i5 * decodeCode;
            }
            int i10 = i9;
            for (int i11 = 0; i11 < i3; i11++) {
                i10 += iArr[i11];
            }
            switch (decodeCode) {
                case R$styleable.Constraint_layout_goneMarginTop /* 103 */:
                case R$styleable.Constraint_motionStagger /* 104 */:
                case R$styleable.Constraint_pathMotionArc /* 105 */:
                    throw FormatException.getFormatInstance();
                default:
                    switch (c3) {
                        case R$styleable.Constraint_layout_goneMarginEnd /* 99 */:
                            c2 = 'd';
                            if (decodeCode < 100) {
                                if (decodeCode < 10) {
                                    sb.append('0');
                                }
                                sb.append(decodeCode);
                            } else {
                                if (decodeCode != 106) {
                                    z5 = false;
                                }
                                if (decodeCode != 106) {
                                    switch (decodeCode) {
                                        case R$styleable.Constraint_layout_goneMarginLeft /* 100 */:
                                            c3 = 'd';
                                            break;
                                        case R$styleable.Constraint_layout_goneMarginRight /* 101 */:
                                            z = false;
                                            c3 = 'e';
                                            break;
                                        case R$styleable.Constraint_layout_goneMarginStart /* 102 */:
                                            if (z2) {
                                                if (sb.length() == 0) {
                                                    sb.append("]C1");
                                                    break;
                                                } else {
                                                    sb.append((char) 29);
                                                    break;
                                                }
                                            }
                                            break;
                                    }
                                } else {
                                    z4 = true;
                                }
                            }
                            z = false;
                            break;
                        case R$styleable.Constraint_layout_goneMarginLeft /* 100 */:
                            if (decodeCode < 96) {
                                sb.append((char) (decodeCode + 32));
                                c2 = 'd';
                                z = false;
                                break;
                            } else {
                                if (decodeCode != 106) {
                                    z5 = false;
                                }
                                if (decodeCode != 98) {
                                    if (decodeCode != 99) {
                                        if (decodeCode != 101) {
                                            if (decodeCode != 102) {
                                                if (decodeCode == 106) {
                                                    z4 = true;
                                                }
                                            } else if (z2) {
                                                if (sb.length() == 0) {
                                                    sb.append("]C1");
                                                } else {
                                                    sb.append((char) 29);
                                                }
                                            }
                                            z = false;
                                            c2 = 'd';
                                            break;
                                        } else {
                                            z = false;
                                        }
                                    }
                                    z = false;
                                    c3 = 'c';
                                    c2 = 'd';
                                } else {
                                    z = true;
                                }
                                c3 = 'e';
                                c2 = 'd';
                            }
                        case R$styleable.Constraint_layout_goneMarginRight /* 101 */:
                            if (decodeCode < 64) {
                                sb.append((char) (decodeCode + 32));
                            } else if (decodeCode < 96) {
                                sb.append((char) (decodeCode - 64));
                            } else {
                                if (decodeCode != 106) {
                                    z5 = false;
                                }
                                if (decodeCode != 102) {
                                    if (decodeCode != 106) {
                                        switch (decodeCode) {
                                            case R$styleable.Constraint_layout_goneMarginBottom /* 98 */:
                                                z = true;
                                                c3 = 'd';
                                                break;
                                            case R$styleable.Constraint_layout_goneMarginEnd /* 99 */:
                                                z = false;
                                                c3 = 'c';
                                                break;
                                            case R$styleable.Constraint_layout_goneMarginLeft /* 100 */:
                                                z = false;
                                                c3 = 'd';
                                                break;
                                        }
                                        c2 = 'd';
                                        break;
                                    } else {
                                        z4 = true;
                                    }
                                } else if (z2) {
                                    if (sb.length() == 0) {
                                        sb.append("]C1");
                                    } else {
                                        sb.append((char) 29);
                                    }
                                }
                                z = false;
                                c2 = 'd';
                            }
                            c2 = 'd';
                            z = false;
                            break;
                        default:
                            c2 = 'd';
                            z = false;
                            break;
                    }
                    if (z3) {
                        c3 = c3 == 'e' ? c2 : 'e';
                    }
                    z3 = z;
                    i6 = i4;
                    i3 = 6;
                    i4 = decodeCode;
                    i7 = i9;
                    i9 = i10;
            }
        }
        int nextUnset = bitArray.getNextUnset(i9);
        if (!bitArray.isRange(nextUnset, Math.min(bitArray.getSize(), ((nextUnset - i7) / 2) + nextUnset), false)) {
            throw NotFoundException.getNotFoundInstance();
        } else if ((i8 - (i5 * i6)) % R$styleable.Constraint_layout_goneMarginTop == i6) {
            int length = sb.length();
            if (length != 0) {
                if (length > 0 && z5) {
                    if (c3 == 'c') {
                        sb.delete(length - 2, length);
                    } else {
                        sb.delete(length - 1, length);
                    }
                }
                float f = ((float) (findStartPattern[1] + findStartPattern[0])) / 2.0f;
                float f2 = ((float) (nextUnset + i7)) / 2.0f;
                int size = arrayList.size();
                byte[] bArr = new byte[size];
                for (int i12 = 0; i12 < size; i12++) {
                    bArr[i12] = ((Byte) arrayList.get(i12)).byteValue();
                }
                float f3 = (float) i;
                return new Result(sb.toString(), bArr, new ResultPoint[]{new ResultPoint(f, f3), new ResultPoint(f2, f3)}, BarcodeFormat.CODE_128);
            }
            throw NotFoundException.getNotFoundInstance();
        } else {
            throw ChecksumException.getChecksumInstance();
        }
    }
}
