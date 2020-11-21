package com.google.zxing.oned.rss.expanded.decoders;

/* access modifiers changed from: package-private */
public final class DecodedNumeric extends DecodedObject {
    private final int firstDigit;
    private final int secondDigit;

    DecodedNumeric(int i, int i2, int i3) {
        super(i);
        this.firstDigit = i2;
        this.secondDigit = i3;
        if (i2 < 0 || i2 > 10) {
            throw new IllegalArgumentException("Invalid firstDigit: " + i2);
        } else if (i3 < 0 || i3 > 10) {
            throw new IllegalArgumentException("Invalid secondDigit: " + i3);
        }
    }

    /* access modifiers changed from: package-private */
    public int getFirstDigit() {
        return this.firstDigit;
    }

    /* access modifiers changed from: package-private */
    public int getSecondDigit() {
        return this.secondDigit;
    }

    /* access modifiers changed from: package-private */
    public boolean isFirstDigitFNC1() {
        return this.firstDigit == 10;
    }

    /* access modifiers changed from: package-private */
    public boolean isSecondDigitFNC1() {
        return this.secondDigit == 10;
    }
}
