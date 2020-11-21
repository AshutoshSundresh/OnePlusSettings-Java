package com.android.settings.wifi.qrcode;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public final class QrCodeGenerator {
    public static Bitmap encodeQrCode(String str, int i) throws WriterException, IllegalArgumentException {
        HashMap hashMap = new HashMap();
        if (!isIso88591(str)) {
            hashMap.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        }
        BitMatrix encode = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, i, i, hashMap);
        Bitmap createBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.RGB_565);
        for (int i2 = 0; i2 < i; i2++) {
            for (int i3 = 0; i3 < i; i3++) {
                createBitmap.setPixel(i2, i3, encode.get(i2, i3) ? -16777216 : -1);
            }
        }
        return createBitmap;
    }

    private static boolean isIso88591(String str) {
        return StandardCharsets.ISO_8859_1.newEncoder().canEncode(str);
    }
}
