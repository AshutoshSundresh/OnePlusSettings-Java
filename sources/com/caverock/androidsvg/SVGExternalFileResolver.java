package com.caverock.androidsvg;

import android.graphics.Bitmap;
import android.graphics.Typeface;

public abstract class SVGExternalFileResolver {
    public abstract boolean isFormatSupported(String str);

    public abstract Typeface resolveFont(String str, int i, String str2);

    public abstract Bitmap resolveImage(String str);
}
