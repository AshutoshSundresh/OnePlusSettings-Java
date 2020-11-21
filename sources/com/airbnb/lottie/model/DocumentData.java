package com.airbnb.lottie.model;

public class DocumentData {
    public final double baselineShift;
    public final int color;
    public final String fontName;
    public final Justification justification;
    public final double lineHeight;
    public final double size;
    public final int strokeColor;
    public final boolean strokeOverFill;
    public final double strokeWidth;
    public final String text;
    public final int tracking;

    public enum Justification {
        LEFT_ALIGN,
        RIGHT_ALIGN,
        CENTER
    }

    public DocumentData(String str, String str2, double d, Justification justification2, int i, double d2, double d3, int i2, int i3, double d4, boolean z) {
        this.text = str;
        this.fontName = str2;
        this.size = d;
        this.justification = justification2;
        this.tracking = i;
        this.lineHeight = d2;
        this.baselineShift = d3;
        this.color = i2;
        this.strokeColor = i3;
        this.strokeWidth = d4;
        this.strokeOverFill = z;
    }

    public int hashCode() {
        int hashCode = (((((int) (((double) (((this.text.hashCode() * 31) + this.fontName.hashCode()) * 31)) + this.size)) * 31) + this.justification.ordinal()) * 31) + this.tracking;
        long doubleToLongBits = Double.doubleToLongBits(this.lineHeight);
        return (((hashCode * 31) + ((int) (doubleToLongBits ^ (doubleToLongBits >>> 32)))) * 31) + this.color;
    }
}
