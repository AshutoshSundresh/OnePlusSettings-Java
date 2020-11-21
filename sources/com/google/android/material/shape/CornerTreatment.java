package com.google.android.material.shape;

import android.graphics.RectF;

public class CornerTreatment {
    public abstract void getCornerPath(ShapePath shapePath, float f, float f2, float f3);

    public void getCornerPath(ShapePath shapePath, float f, float f2, RectF rectF, CornerSize cornerSize) {
        getCornerPath(shapePath, f, f2, cornerSize.getCornerSize(rectF));
    }
}
