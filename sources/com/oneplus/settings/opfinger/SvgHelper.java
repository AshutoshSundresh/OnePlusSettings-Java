package com.oneplus.settings.opfinger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;
import com.caverock.androidsvg.PreserveAspectRatio;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import java.util.ArrayList;
import java.util.List;

public class SvgHelper {
    private final List<SvgPath> mPaths = new ArrayList();
    private final Paint mSourcePaint;
    private SVG mSvg;

    public SvgHelper(Paint paint) {
        this.mSourcePaint = paint;
    }

    public void load(Context context, int i) {
        if (this.mSvg == null) {
            try {
                SVG fromResource = SVG.getFromResource(context, i);
                this.mSvg = fromResource;
                fromResource.setDocumentPreserveAspectRatio(PreserveAspectRatio.UNSCALED);
            } catch (SVGParseException e) {
                Log.e("SVG", "Could not load specified SVG resource", e);
            }
        }
    }

    public static class SvgPath {
        private static final Region sMaxClip = new Region(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        private static final Region sRegion = new Region();
        final float length;
        final PathMeasure measure;
        final Paint paint;
        final Path renderPath = new Path();

        SvgPath(Path path, Paint paint2) {
            this.paint = paint2;
            PathMeasure pathMeasure = new PathMeasure(path, false);
            this.measure = pathMeasure;
            this.length = pathMeasure.getLength();
            sRegion.setPath(path, sMaxClip);
            sRegion.getBounds();
        }
    }

    public List<SvgPath> getPathsForViewport(final int i, final int i2) {
        this.mPaths.clear();
        AnonymousClass1 r0 = new Canvas() {
            /* class com.oneplus.settings.opfinger.SvgHelper.AnonymousClass1 */
            private final Matrix mMatrix = new Matrix();

            public int getWidth() {
                return i;
            }

            public int getHeight() {
                return i2;
            }

            public void drawPath(Path path, Paint paint) {
                Path path2 = new Path();
                getMatrix(this.mMatrix);
                path.transform(this.mMatrix, path2);
                SvgHelper.this.mPaths.add(new SvgPath(path2, new Paint(SvgHelper.this.mSourcePaint)));
            }
        };
        RectF documentViewBox = this.mSvg.getDocumentViewBox();
        float f = (float) i;
        float f2 = (float) i2;
        float min = Math.min(f / documentViewBox.width(), f2 / documentViewBox.height());
        r0.translate((f - (documentViewBox.width() * min)) / 2.0f, (f2 - (documentViewBox.height() * min)) / 2.0f);
        r0.scale(min, min);
        this.mSvg.renderToCanvas(r0);
        return this.mPaths;
    }
}
