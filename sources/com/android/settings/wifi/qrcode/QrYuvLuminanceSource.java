package com.android.settings.wifi.qrcode;

import com.google.zxing.LuminanceSource;

public class QrYuvLuminanceSource extends LuminanceSource {
    private int mHeight;
    private int mWidth;
    private byte[] mYuvData;

    public QrYuvLuminanceSource(byte[] bArr, int i, int i2) {
        super(i, i2);
        this.mWidth = i;
        this.mHeight = i2;
        this.mYuvData = bArr;
    }

    public LuminanceSource crop(int i, int i2, int i3, int i4) {
        byte[] bArr = new byte[(i3 * i4)];
        int i5 = this.mWidth;
        int i6 = (i2 * i5) + i;
        if (i + i3 > i5 || i2 + i4 > this.mHeight) {
            throw new IllegalArgumentException("cropped rectangle does not fit within image data.");
        }
        for (int i7 = 0; i7 < i4; i7++) {
            System.arraycopy(this.mYuvData, i6, bArr, i7 * i3, i3);
            i6 += this.mWidth;
        }
        return new QrYuvLuminanceSource(bArr, i3, i4);
    }

    @Override // com.google.zxing.LuminanceSource
    public byte[] getRow(int i, byte[] bArr) {
        if (i < 0 || i >= this.mHeight) {
            throw new IllegalArgumentException("Requested row is outside the image: " + i);
        }
        if (bArr == null || bArr.length < this.mWidth) {
            bArr = new byte[this.mWidth];
        }
        byte[] bArr2 = this.mYuvData;
        int i2 = this.mWidth;
        System.arraycopy(bArr2, i * i2, bArr, 0, i2);
        return bArr;
    }

    @Override // com.google.zxing.LuminanceSource
    public byte[] getMatrix() {
        return this.mYuvData;
    }
}
