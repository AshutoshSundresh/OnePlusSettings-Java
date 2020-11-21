package com.google.zxing.aztec;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.aztec.encoder.AztecCode;
import com.google.zxing.aztec.encoder.Encoder;
import com.google.zxing.common.BitMatrix;
import java.nio.charset.Charset;
import java.util.Map;

public final class AztecWriter implements Writer {
    private static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");

    @Override // com.google.zxing.Writer
    public BitMatrix encode(String str, BarcodeFormat barcodeFormat, int i, int i2, Map<EncodeHintType, ?> map) {
        Number number;
        Integer num = null;
        String str2 = map == null ? null : (String) map.get(EncodeHintType.CHARACTER_SET);
        if (map == null) {
            number = null;
        } else {
            number = (Number) map.get(EncodeHintType.ERROR_CORRECTION);
        }
        if (map != null) {
            num = (Integer) map.get(EncodeHintType.AZTEC_LAYERS);
        }
        return encode(str, barcodeFormat, i, i2, str2 == null ? DEFAULT_CHARSET : Charset.forName(str2), number == null ? 33 : number.intValue(), num == null ? 0 : num.intValue());
    }

    private static BitMatrix encode(String str, BarcodeFormat barcodeFormat, int i, int i2, Charset charset, int i3, int i4) {
        if (barcodeFormat == BarcodeFormat.AZTEC) {
            return renderResult(Encoder.encode(str.getBytes(charset), i3, i4), i, i2);
        }
        throw new IllegalArgumentException("Can only encode AZTEC, but got " + barcodeFormat);
    }

    private static BitMatrix renderResult(AztecCode aztecCode, int i, int i2) {
        BitMatrix matrix = aztecCode.getMatrix();
        if (matrix != null) {
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int max = Math.max(i, width);
            int max2 = Math.max(i2, height);
            int min = Math.min(max / width, max2 / height);
            int i3 = (max - (width * min)) / 2;
            int i4 = (max2 - (height * min)) / 2;
            BitMatrix bitMatrix = new BitMatrix(max, max2);
            int i5 = 0;
            while (i5 < height) {
                int i6 = 0;
                int i7 = i3;
                while (i6 < width) {
                    if (matrix.get(i6, i5)) {
                        bitMatrix.setRegion(i7, i4, min, min);
                    }
                    i6++;
                    i7 += min;
                }
                i5++;
                i4 += min;
            }
            return bitMatrix;
        }
        throw new IllegalStateException();
    }
}
