package com.airbnb.lottie;

import android.graphics.Typeface;

public class FontAssetDelegate {
    public abstract Typeface fetchFont(String str);

    public abstract String getFontPath(String str);
}
