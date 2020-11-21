package androidx.animation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.InflateException;
import androidx.core.graphics.PathParser;

public class PathInterpolator {
    private float[] mData;

    public PathInterpolator(Context context, AttributeSet attributeSet) {
        this(context.getResources(), context.getTheme(), attributeSet);
    }

    PathInterpolator(Resources resources, Resources.Theme theme, AttributeSet attributeSet) {
        TypedArray typedArray;
        if (theme != null) {
            typedArray = theme.obtainStyledAttributes(attributeSet, AndroidResources.STYLEABLE_PATH_INTERPOLATOR, 0, 0);
        } else {
            typedArray = resources.obtainAttributes(attributeSet, AndroidResources.STYLEABLE_PATH_INTERPOLATOR);
        }
        parseInterpolatorFromTypeArray(typedArray);
        typedArray.recycle();
    }

    private void parseInterpolatorFromTypeArray(TypedArray typedArray) {
        if (typedArray.hasValue(4)) {
            String string = typedArray.getString(4);
            Path createPathFromPathData = PathParser.createPathFromPathData(string);
            if (createPathFromPathData != null) {
                initPath(createPathFromPathData);
                return;
            }
            throw new InflateException("The path is null, which is created from " + string);
        } else if (!typedArray.hasValue(0)) {
            throw new InflateException("pathInterpolator requires the controlX1 attribute");
        } else if (typedArray.hasValue(1)) {
            float f = typedArray.getFloat(0, 0.0f);
            float f2 = typedArray.getFloat(1, 0.0f);
            boolean hasValue = typedArray.hasValue(2);
            if (hasValue != typedArray.hasValue(3)) {
                throw new InflateException("pathInterpolator requires both controlX2 and controlY2 for cubic Beziers.");
            } else if (!hasValue) {
                initQuad(f, f2);
            } else {
                initCubic(f, f2, typedArray.getFloat(2, 0.0f), typedArray.getFloat(3, 0.0f));
            }
        } else {
            throw new InflateException("pathInterpolator requires the controlY1 attribute");
        }
    }

    private void initQuad(float f, float f2) {
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.quadTo(f, f2, 1.0f, 1.0f);
        initPath(path);
    }

    private void initCubic(float f, float f2, float f3, float f4) {
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.cubicTo(f, f2, f3, f4, 1.0f, 1.0f);
        initPath(path);
    }

    private void initPath(Path path) {
        this.mData = PathUtils.createKeyFrameData(path, 0.002f);
        int numOfPoints = getNumOfPoints();
        int i = 0;
        float f = 0.0f;
        if (floatEquals(getXAtIndex(0), 0.0f) && floatEquals(getYAtIndex(0), 0.0f)) {
            int i2 = numOfPoints - 1;
            if (floatEquals(getXAtIndex(i2), 1.0f) && floatEquals(getYAtIndex(i2), 1.0f)) {
                float f2 = 0.0f;
                while (i < numOfPoints) {
                    float fractionAtIndex = getFractionAtIndex(i);
                    float xAtIndex = getXAtIndex(i);
                    if (fractionAtIndex == f && xAtIndex != f2) {
                        throw new IllegalArgumentException("The Path cannot have discontinuity in the X axis.");
                    } else if (xAtIndex >= f2) {
                        i++;
                        f = fractionAtIndex;
                        f2 = xAtIndex;
                    } else {
                        throw new IllegalArgumentException("The Path cannot loop back on itself.");
                    }
                }
                return;
            }
        }
        throw new IllegalArgumentException("The Path must start at (0,0) and end at (1,1)");
    }

    private float getFractionAtIndex(int i) {
        return this.mData[i * 3];
    }

    private float getXAtIndex(int i) {
        return this.mData[(i * 3) + 1];
    }

    private float getYAtIndex(int i) {
        return this.mData[(i * 3) + 2];
    }

    private int getNumOfPoints() {
        return this.mData.length / 3;
    }

    private static boolean floatEquals(float f, float f2) {
        return Math.abs(f - f2) < 0.01f;
    }
}
