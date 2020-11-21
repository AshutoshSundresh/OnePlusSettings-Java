package com.caverock.androidsvg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import com.caverock.androidsvg.CSSParser;
import com.caverock.androidsvg.SVG;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;

public class SVGAndroidRenderer {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$PreserveAspectRatio$Alignment;
    private static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$FillRule;
    private static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineCaps;
    private static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineJoin;
    private Stack<Bitmap> bitmapStack;
    private Canvas canvas;
    private Stack<Canvas> canvasStack;
    private SVG.Box canvasViewPort;
    private boolean directRenderingMode;
    private SVG document;
    private float dpi;
    private Stack<Matrix> matrixStack;
    private Stack<SVG.SvgContainer> parentStack;
    private RendererState state;
    private Stack<RendererState> stateStack;

    private int clamp255(float f) {
        int i = (int) (f * 256.0f);
        if (i < 0) {
            return 0;
        }
        if (i > 255) {
            return 255;
        }
        return i;
    }

    /* access modifiers changed from: private */
    public static void debug(String str, Object... objArr) {
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(22:3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|(2:22|23)|24|26) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x0028 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:12:0x0031 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:14:0x003b */
    /* JADX WARNING: Missing exception handler attribute for start block: B:16:0x0044 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:18:0x004d */
    /* JADX WARNING: Missing exception handler attribute for start block: B:20:0x0057 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:22:0x0060 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x0015 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:8:0x001f */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$PreserveAspectRatio$Alignment() {
        /*
        // Method dump skipped, instructions count: 108
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGAndroidRenderer.$SWITCH_TABLE$com$caverock$androidsvg$PreserveAspectRatio$Alignment():int[]");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(7:3|4|5|6|7|8|10) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x0015 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$FillRule() {
        /*
            int[] r0 = com.caverock.androidsvg.SVGAndroidRenderer.$SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$FillRule
            if (r0 == 0) goto L_0x0005
            return r0
        L_0x0005:
            com.caverock.androidsvg.SVG$Style$FillRule[] r0 = com.caverock.androidsvg.SVG.Style.FillRule.values()
            int r0 = r0.length
            int[] r0 = new int[r0]
            com.caverock.androidsvg.SVG$Style$FillRule r1 = com.caverock.androidsvg.SVG.Style.FillRule.EvenOdd     // Catch:{ NoSuchFieldError -> 0x0015 }
            int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0015 }
            r2 = 2
            r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0015 }
        L_0x0015:
            com.caverock.androidsvg.SVG$Style$FillRule r1 = com.caverock.androidsvg.SVG.Style.FillRule.NonZero     // Catch:{ NoSuchFieldError -> 0x001e }
            int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001e }
            r2 = 1
            r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001e }
        L_0x001e:
            com.caverock.androidsvg.SVGAndroidRenderer.$SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$FillRule = r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGAndroidRenderer.$SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$FillRule():int[]");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(9:3|4|5|6|7|8|9|10|12) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x0015 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:8:0x001e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineCaps() {
        /*
            int[] r0 = com.caverock.androidsvg.SVGAndroidRenderer.$SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineCaps
            if (r0 == 0) goto L_0x0005
            return r0
        L_0x0005:
            com.caverock.androidsvg.SVG$Style$LineCaps[] r0 = com.caverock.androidsvg.SVG.Style.LineCaps.values()
            int r0 = r0.length
            int[] r0 = new int[r0]
            com.caverock.androidsvg.SVG$Style$LineCaps r1 = com.caverock.androidsvg.SVG.Style.LineCaps.Butt     // Catch:{ NoSuchFieldError -> 0x0015 }
            int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0015 }
            r2 = 1
            r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0015 }
        L_0x0015:
            com.caverock.androidsvg.SVG$Style$LineCaps r1 = com.caverock.androidsvg.SVG.Style.LineCaps.Round     // Catch:{ NoSuchFieldError -> 0x001e }
            int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001e }
            r2 = 2
            r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001e }
        L_0x001e:
            com.caverock.androidsvg.SVG$Style$LineCaps r1 = com.caverock.androidsvg.SVG.Style.LineCaps.Square     // Catch:{ NoSuchFieldError -> 0x0027 }
            int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0027 }
            r2 = 3
            r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0027 }
        L_0x0027:
            com.caverock.androidsvg.SVGAndroidRenderer.$SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineCaps = r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGAndroidRenderer.$SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineCaps():int[]");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(9:3|4|5|6|7|8|9|10|12) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x0015 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:8:0x001e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineJoin() {
        /*
            int[] r0 = com.caverock.androidsvg.SVGAndroidRenderer.$SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineJoin
            if (r0 == 0) goto L_0x0005
            return r0
        L_0x0005:
            com.caverock.androidsvg.SVG$Style$LineJoin[] r0 = com.caverock.androidsvg.SVG.Style.LineJoin.values()
            int r0 = r0.length
            int[] r0 = new int[r0]
            com.caverock.androidsvg.SVG$Style$LineJoin r1 = com.caverock.androidsvg.SVG.Style.LineJoin.Bevel     // Catch:{ NoSuchFieldError -> 0x0015 }
            int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0015 }
            r2 = 3
            r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0015 }
        L_0x0015:
            com.caverock.androidsvg.SVG$Style$LineJoin r1 = com.caverock.androidsvg.SVG.Style.LineJoin.Miter     // Catch:{ NoSuchFieldError -> 0x001e }
            int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001e }
            r2 = 1
            r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001e }
        L_0x001e:
            com.caverock.androidsvg.SVG$Style$LineJoin r1 = com.caverock.androidsvg.SVG.Style.LineJoin.Round     // Catch:{ NoSuchFieldError -> 0x0027 }
            int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0027 }
            r2 = 2
            r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0027 }
        L_0x0027:
            com.caverock.androidsvg.SVGAndroidRenderer.$SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineJoin = r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGAndroidRenderer.$SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineJoin():int[]");
    }

    /* access modifiers changed from: private */
    public class RendererState implements Cloneable {
        public boolean directRendering;
        public Paint fillPaint;
        public boolean hasFill;
        public boolean hasStroke;
        public boolean spacePreserve;
        public Paint strokePaint;
        public SVG.Style style = SVG.Style.getDefaultStyle();
        public SVG.Box viewBox;
        public SVG.Box viewPort;

        public RendererState(SVGAndroidRenderer sVGAndroidRenderer) {
            Paint paint = new Paint();
            this.fillPaint = paint;
            paint.setFlags(385);
            this.fillPaint.setStyle(Paint.Style.FILL);
            this.fillPaint.setTypeface(Typeface.DEFAULT);
            Paint paint2 = new Paint();
            this.strokePaint = paint2;
            paint2.setFlags(385);
            this.strokePaint.setStyle(Paint.Style.STROKE);
            this.strokePaint.setTypeface(Typeface.DEFAULT);
        }

        /* access modifiers changed from: protected */
        @Override // java.lang.Object
        public Object clone() {
            try {
                RendererState rendererState = (RendererState) super.clone();
                rendererState.style = (SVG.Style) this.style.clone();
                rendererState.fillPaint = new Paint(this.fillPaint);
                rendererState.strokePaint = new Paint(this.strokePaint);
                return rendererState;
            } catch (CloneNotSupportedException e) {
                throw new InternalError(e.toString());
            }
        }
    }

    private void resetState() {
        this.state = new RendererState(this);
        this.stateStack = new Stack<>();
        updateStyle(this.state, SVG.Style.getDefaultStyle());
        RendererState rendererState = this.state;
        rendererState.viewPort = this.canvasViewPort;
        rendererState.spacePreserve = false;
        rendererState.directRendering = this.directRenderingMode;
        this.stateStack.push((RendererState) rendererState.clone());
        this.canvasStack = new Stack<>();
        this.bitmapStack = new Stack<>();
        this.matrixStack = new Stack<>();
        this.parentStack = new Stack<>();
    }

    protected SVGAndroidRenderer(Canvas canvas2, SVG.Box box, float f) {
        this.canvas = canvas2;
        this.dpi = f;
        this.canvasViewPort = box;
    }

    /* access modifiers changed from: protected */
    public float getDPI() {
        return this.dpi;
    }

    /* access modifiers changed from: protected */
    public float getCurrentFontSize() {
        return this.state.fillPaint.getTextSize();
    }

    /* access modifiers changed from: protected */
    public float getCurrentFontXHeight() {
        return this.state.fillPaint.getTextSize() / 2.0f;
    }

    /* access modifiers changed from: protected */
    public SVG.Box getCurrentViewPortInUserUnits() {
        RendererState rendererState = this.state;
        SVG.Box box = rendererState.viewBox;
        if (box != null) {
            return box;
        }
        return rendererState.viewPort;
    }

    /* access modifiers changed from: protected */
    public void renderDocument(SVG svg, SVG.Box box, PreserveAspectRatio preserveAspectRatio, boolean z) {
        this.document = svg;
        this.directRenderingMode = z;
        SVG.Svg rootElement = svg.getRootElement();
        if (rootElement == null) {
            warn("Nothing to render. Document is empty.", new Object[0]);
            return;
        }
        resetState();
        checkXMLSpaceAttribute(rootElement);
        SVG.Length length = rootElement.width;
        SVG.Length length2 = rootElement.height;
        if (box == null) {
            box = rootElement.viewBox;
        }
        if (preserveAspectRatio == null) {
            preserveAspectRatio = rootElement.preserveAspectRatio;
        }
        render(rootElement, length, length2, box, preserveAspectRatio);
    }

    private void render(SVG.SvgObject svgObject) {
        if (!(svgObject instanceof SVG.NotDirectlyRendered)) {
            statePush();
            checkXMLSpaceAttribute(svgObject);
            if (svgObject instanceof SVG.Svg) {
                render((SVG.Svg) svgObject);
            } else if (svgObject instanceof SVG.Use) {
                render((SVG.Use) svgObject);
            } else if (svgObject instanceof SVG.Switch) {
                render((SVG.Switch) svgObject);
            } else if (svgObject instanceof SVG.Group) {
                render((SVG.Group) svgObject);
            } else if (svgObject instanceof SVG.Image) {
                render((SVG.Image) svgObject);
            } else if (svgObject instanceof SVG.Path) {
                render((SVG.Path) svgObject);
            } else if (svgObject instanceof SVG.Rect) {
                render((SVG.Rect) svgObject);
            } else if (svgObject instanceof SVG.Circle) {
                render((SVG.Circle) svgObject);
            } else if (svgObject instanceof SVG.Ellipse) {
                render((SVG.Ellipse) svgObject);
            } else if (svgObject instanceof SVG.Line) {
                render((SVG.Line) svgObject);
            } else if (svgObject instanceof SVG.Polygon) {
                render((SVG.Polygon) svgObject);
            } else if (svgObject instanceof SVG.PolyLine) {
                render((SVG.PolyLine) svgObject);
            } else if (svgObject instanceof SVG.Text) {
                render((SVG.Text) svgObject);
            }
            statePop();
        }
    }

    private void renderChildren(SVG.SvgContainer svgContainer, boolean z) {
        if (z) {
            parentPush(svgContainer);
        }
        for (SVG.SvgObject svgObject : svgContainer.getChildren()) {
            render(svgObject);
        }
        if (z) {
            parentPop();
        }
    }

    private void statePush() {
        this.canvas.save();
        this.stateStack.push(this.state);
        this.state = (RendererState) this.state.clone();
    }

    private void statePop() {
        this.canvas.restore();
        this.state = this.stateStack.pop();
    }

    private void parentPush(SVG.SvgContainer svgContainer) {
        this.parentStack.push(svgContainer);
        this.matrixStack.push(this.canvas.getMatrix());
    }

    private void parentPop() {
        this.parentStack.pop();
        this.matrixStack.pop();
    }

    private void updateStyleForElement(RendererState rendererState, SVG.SvgElementBase svgElementBase) {
        rendererState.style.resetNonInheritingProperties(svgElementBase.parent == null);
        SVG.Style style = svgElementBase.baseStyle;
        if (style != null) {
            updateStyle(rendererState, style);
        }
        if (this.document.hasCSSRules()) {
            for (CSSParser.Rule rule : this.document.getCSSRules()) {
                if (CSSParser.ruleMatch(rule.selector, svgElementBase)) {
                    updateStyle(rendererState, rule.style);
                }
            }
        }
        SVG.Style style2 = svgElementBase.style;
        if (style2 != null) {
            updateStyle(rendererState, style2);
        }
    }

    private void checkXMLSpaceAttribute(SVG.SvgObject svgObject) {
        Boolean bool;
        if ((svgObject instanceof SVG.SvgElementBase) && (bool = ((SVG.SvgElementBase) svgObject).spacePreserve) != null) {
            this.state.spacePreserve = bool.booleanValue();
        }
    }

    private void doFilledPath(SVG.SvgElement svgElement, Path path) {
        SVG.SvgPaint svgPaint = this.state.style.fill;
        if (svgPaint instanceof SVG.PaintReference) {
            SVG.SvgObject resolveIRI = this.document.resolveIRI(((SVG.PaintReference) svgPaint).href);
            if (resolveIRI instanceof SVG.Pattern) {
                fillWithPattern(svgElement, path, (SVG.Pattern) resolveIRI);
                return;
            }
        }
        this.canvas.drawPath(path, this.state.fillPaint);
    }

    private void doStroke(Path path) {
        RendererState rendererState = this.state;
        if (rendererState.style.vectorEffect == SVG.Style.VectorEffect.NonScalingStroke) {
            Matrix matrix = this.canvas.getMatrix();
            Path path2 = new Path();
            path.transform(matrix, path2);
            this.canvas.setMatrix(new Matrix());
            Shader shader = this.state.strokePaint.getShader();
            Matrix matrix2 = new Matrix();
            if (shader != null) {
                shader.getLocalMatrix(matrix2);
                Matrix matrix3 = new Matrix(matrix2);
                matrix3.postConcat(matrix);
                shader.setLocalMatrix(matrix3);
            }
            this.canvas.drawPath(path2, this.state.strokePaint);
            this.canvas.setMatrix(matrix);
            if (shader != null) {
                shader.setLocalMatrix(matrix2);
                return;
            }
            return;
        }
        this.canvas.drawPath(path, rendererState.strokePaint);
    }

    /* access modifiers changed from: private */
    public static void warn(String str, Object... objArr) {
        Log.w("SVGAndroidRenderer", String.format(str, objArr));
    }

    /* access modifiers changed from: private */
    public static void error(String str, Object... objArr) {
        Log.e("SVGAndroidRenderer", String.format(str, objArr));
    }

    private void render(SVG.Svg svg) {
        render(svg, svg.width, svg.height);
    }

    private void render(SVG.Svg svg, SVG.Length length, SVG.Length length2) {
        render(svg, length, length2, svg.viewBox, svg.preserveAspectRatio);
    }

    private void render(SVG.Svg svg, SVG.Length length, SVG.Length length2, SVG.Box box, PreserveAspectRatio preserveAspectRatio) {
        float f;
        debug("Svg render", new Object[0]);
        if (length != null && length.isZero()) {
            return;
        }
        if (length2 == null || !length2.isZero()) {
            if (preserveAspectRatio == null && (preserveAspectRatio = svg.preserveAspectRatio) == null) {
                preserveAspectRatio = PreserveAspectRatio.LETTERBOX;
            }
            updateStyleForElement(this.state, svg);
            if (display()) {
                float f2 = 0.0f;
                if (svg.parent != null) {
                    SVG.Length length3 = svg.x;
                    float floatValueX = length3 != null ? length3.floatValueX(this) : 0.0f;
                    SVG.Length length4 = svg.y;
                    if (length4 != null) {
                        f2 = length4.floatValueY(this);
                    }
                    f2 = floatValueX;
                    f = f2;
                } else {
                    f = 0.0f;
                }
                SVG.Box currentViewPortInUserUnits = getCurrentViewPortInUserUnits();
                this.state.viewPort = new SVG.Box(f2, f, length != null ? length.floatValueX(this) : currentViewPortInUserUnits.width, length2 != null ? length2.floatValueY(this) : currentViewPortInUserUnits.height);
                if (!this.state.style.overflow.booleanValue()) {
                    SVG.Box box2 = this.state.viewPort;
                    setClipRect(box2.minX, box2.minY, box2.width, box2.height);
                }
                checkForClipPath(svg, this.state.viewPort);
                if (box != null) {
                    this.canvas.concat(calculateViewBoxTransform(this.state.viewPort, box, preserveAspectRatio));
                    this.state.viewBox = svg.viewBox;
                }
                boolean pushLayer = pushLayer();
                viewportFill();
                renderChildren(svg, true);
                if (pushLayer) {
                    popLayer(svg);
                }
                updateParentBoundingBox(svg);
            }
        }
    }

    private void render(SVG.Group group) {
        debug("Group render", new Object[0]);
        updateStyleForElement(this.state, group);
        if (display()) {
            Matrix matrix = group.transform;
            if (matrix != null) {
                this.canvas.concat(matrix);
            }
            checkForClipPath(group);
            boolean pushLayer = pushLayer();
            renderChildren(group, true);
            if (pushLayer) {
                popLayer(group);
            }
            updateParentBoundingBox(group);
        }
    }

    private void updateParentBoundingBox(SVG.SvgElement svgElement) {
        if (svgElement.parent != null && svgElement.boundingBox != null) {
            Matrix matrix = new Matrix();
            if (this.matrixStack.peek().invert(matrix)) {
                SVG.Box box = svgElement.boundingBox;
                SVG.Box box2 = svgElement.boundingBox;
                SVG.Box box3 = svgElement.boundingBox;
                float[] fArr = {box.minX, box.minY, box.maxX(), box2.minY, box2.maxX(), svgElement.boundingBox.maxY(), box3.minX, box3.maxY()};
                matrix.preConcat(this.canvas.getMatrix());
                matrix.mapPoints(fArr);
                RectF rectF = new RectF(fArr[0], fArr[1], fArr[0], fArr[1]);
                for (int i = 2; i <= 6; i += 2) {
                    if (fArr[i] < rectF.left) {
                        rectF.left = fArr[i];
                    }
                    if (fArr[i] > rectF.right) {
                        rectF.right = fArr[i];
                    }
                    int i2 = i + 1;
                    if (fArr[i2] < rectF.top) {
                        rectF.top = fArr[i2];
                    }
                    if (fArr[i2] > rectF.bottom) {
                        rectF.bottom = fArr[i2];
                    }
                }
                SVG.SvgElement svgElement2 = (SVG.SvgElement) this.parentStack.peek();
                SVG.Box box4 = svgElement2.boundingBox;
                if (box4 == null) {
                    svgElement2.boundingBox = SVG.Box.fromLimits(rectF.left, rectF.top, rectF.right, rectF.bottom);
                } else {
                    box4.union(SVG.Box.fromLimits(rectF.left, rectF.top, rectF.right, rectF.bottom));
                }
            }
        }
    }

    private boolean pushLayer() {
        if (!requiresCompositing()) {
            return false;
        }
        this.canvas.saveLayerAlpha(null, clamp255(this.state.style.opacity.floatValue()), 4);
        this.stateStack.push(this.state);
        RendererState rendererState = (RendererState) this.state.clone();
        this.state = rendererState;
        String str = rendererState.style.mask;
        if (str != null && rendererState.directRendering) {
            SVG.SvgObject resolveIRI = this.document.resolveIRI(str);
            if (resolveIRI == null || !(resolveIRI instanceof SVG.Mask)) {
                error("Mask reference '%s' not found", this.state.style.mask);
                this.state.style.mask = null;
            } else {
                this.canvasStack.push(this.canvas);
                duplicateCanvas();
            }
        }
        return true;
    }

    private void popLayer(SVG.SvgElement svgElement) {
        RendererState rendererState = this.state;
        String str = rendererState.style.mask;
        if (str != null && rendererState.directRendering) {
            SVG.SvgObject resolveIRI = this.document.resolveIRI(str);
            duplicateCanvas();
            renderMask((SVG.Mask) resolveIRI, svgElement);
            Bitmap processMaskBitmaps = processMaskBitmaps();
            Canvas pop = this.canvasStack.pop();
            this.canvas = pop;
            pop.save();
            this.canvas.setMatrix(new Matrix());
            this.canvas.drawBitmap(processMaskBitmaps, 0.0f, 0.0f, this.state.fillPaint);
            processMaskBitmaps.recycle();
            this.canvas.restore();
        }
        statePop();
    }

    private boolean requiresCompositing() {
        RendererState rendererState = this.state;
        if (rendererState.style.mask != null && !rendererState.directRendering) {
            warn("Masks are not supported when using getPicture()", new Object[0]);
        }
        if (this.state.style.opacity.floatValue() < 1.0f) {
            return true;
        }
        RendererState rendererState2 = this.state;
        if (rendererState2.style.mask == null || !rendererState2.directRendering) {
            return false;
        }
        return true;
    }

    private void duplicateCanvas() {
        try {
            Bitmap createBitmap = Bitmap.createBitmap(this.canvas.getWidth(), this.canvas.getHeight(), Bitmap.Config.ARGB_8888);
            this.bitmapStack.push(createBitmap);
            Canvas canvas2 = new Canvas(createBitmap);
            canvas2.setMatrix(this.canvas.getMatrix());
            this.canvas = canvas2;
        } catch (OutOfMemoryError e) {
            error("Not enough memory to create temporary bitmaps for mask processing", new Object[0]);
            throw e;
        }
    }

    private Bitmap processMaskBitmaps() {
        Bitmap pop = this.bitmapStack.pop();
        Bitmap pop2 = this.bitmapStack.pop();
        int width = pop.getWidth();
        int height = pop.getHeight();
        int[] iArr = new int[width];
        int[] iArr2 = new int[width];
        for (int i = 0; i < height; i++) {
            pop.getPixels(iArr, 0, width, 0, i, width, 1);
            pop2.getPixels(iArr2, 0, width, 0, i, width, 1);
            for (int i2 = 0; i2 < width; i2++) {
                int i3 = iArr[i2];
                int i4 = i3 & 255;
                int i5 = (i3 >> 8) & 255;
                int i6 = (i3 >> 16) & 255;
                int i7 = (i3 >> 24) & 255;
                if (i7 == 0) {
                    iArr2[i2] = 0;
                } else {
                    int i8 = iArr2[i2];
                    iArr2[i2] = (i8 & 16777215) | (((((i8 >> 24) & 255) * (((((i6 * 6963) + (i5 * 23442)) + (i4 * 2362)) * i7) / 8355840)) / 255) << 24);
                }
            }
            pop2.setPixels(iArr2, 0, width, 0, i, width, 1);
        }
        pop.recycle();
        return pop2;
    }

    private void render(SVG.Switch r3) {
        debug("Switch render", new Object[0]);
        updateStyleForElement(this.state, r3);
        if (display()) {
            Matrix matrix = r3.transform;
            if (matrix != null) {
                this.canvas.concat(matrix);
            }
            checkForClipPath(r3);
            boolean pushLayer = pushLayer();
            renderSwitchChild(r3);
            if (pushLayer) {
                popLayer(r3);
            }
            updateParentBoundingBox(r3);
        }
    }

    private void renderSwitchChild(SVG.Switch r8) {
        Set<String> systemLanguage;
        String language = Locale.getDefault().getLanguage();
        SVGExternalFileResolver fileResolver = this.document.getFileResolver();
        for (SVG.SvgObject svgObject : r8.getChildren()) {
            if (svgObject instanceof SVG.SvgConditional) {
                SVG.SvgConditional svgConditional = (SVG.SvgConditional) svgObject;
                if (svgConditional.getRequiredExtensions() == null && ((systemLanguage = svgConditional.getSystemLanguage()) == null || (!systemLanguage.isEmpty() && systemLanguage.contains(language)))) {
                    Set<String> requiredFeatures = svgConditional.getRequiredFeatures();
                    if (requiredFeatures == null || (!requiredFeatures.isEmpty() && SVGParser.supportedFeatures.containsAll(requiredFeatures))) {
                        Set<String> requiredFormats = svgConditional.getRequiredFormats();
                        if (requiredFormats != null) {
                            if (!requiredFormats.isEmpty() && fileResolver != null) {
                                Iterator<String> it = requiredFormats.iterator();
                                while (true) {
                                    if (it.hasNext()) {
                                        if (!fileResolver.isFormatSupported(it.next())) {
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                        Set<String> requiredFonts = svgConditional.getRequiredFonts();
                        if (requiredFonts != null) {
                            if (!requiredFonts.isEmpty() && fileResolver != null) {
                                for (String str : requiredFonts) {
                                    if (fileResolver.resolveFont(str, this.state.style.fontWeight.intValue(), String.valueOf(this.state.style.fontStyle)) == null) {
                                    }
                                }
                            }
                        }
                        render(svgObject);
                        return;
                    }
                }
            }
        }
    }

    private void render(SVG.Use use) {
        debug("Use render", new Object[0]);
        SVG.Length length = use.width;
        if (length == null || !length.isZero()) {
            SVG.Length length2 = use.height;
            if (length2 == null || !length2.isZero()) {
                updateStyleForElement(this.state, use);
                if (display()) {
                    SVG.SvgObject resolveIRI = use.document.resolveIRI(use.href);
                    if (resolveIRI == null) {
                        error("Use reference '%s' not found", use.href);
                        return;
                    }
                    Matrix matrix = use.transform;
                    if (matrix != null) {
                        this.canvas.concat(matrix);
                    }
                    Matrix matrix2 = new Matrix();
                    SVG.Length length3 = use.x;
                    float f = 0.0f;
                    float floatValueX = length3 != null ? length3.floatValueX(this) : 0.0f;
                    SVG.Length length4 = use.y;
                    if (length4 != null) {
                        f = length4.floatValueY(this);
                    }
                    matrix2.preTranslate(floatValueX, f);
                    this.canvas.concat(matrix2);
                    checkForClipPath(use);
                    boolean pushLayer = pushLayer();
                    parentPush(use);
                    if (resolveIRI instanceof SVG.Svg) {
                        statePush();
                        SVG.Svg svg = (SVG.Svg) resolveIRI;
                        SVG.Length length5 = use.width;
                        if (length5 == null) {
                            length5 = svg.width;
                        }
                        SVG.Length length6 = use.height;
                        if (length6 == null) {
                            length6 = svg.height;
                        }
                        render(svg, length5, length6);
                        statePop();
                    } else if (resolveIRI instanceof SVG.Symbol) {
                        SVG.Length length7 = use.width;
                        if (length7 == null) {
                            length7 = new SVG.Length(100.0f, SVG.Unit.percent);
                        }
                        SVG.Length length8 = use.height;
                        if (length8 == null) {
                            length8 = new SVG.Length(100.0f, SVG.Unit.percent);
                        }
                        statePush();
                        render((SVG.Symbol) resolveIRI, length7, length8);
                        statePop();
                    } else {
                        render(resolveIRI);
                    }
                    parentPop();
                    if (pushLayer) {
                        popLayer(use);
                    }
                    updateParentBoundingBox(use);
                }
            }
        }
    }

    private void render(SVG.Path path) {
        debug("Path render", new Object[0]);
        updateStyleForElement(this.state, path);
        if (display() && visible()) {
            RendererState rendererState = this.state;
            if (rendererState.hasStroke || rendererState.hasFill) {
                Matrix matrix = path.transform;
                if (matrix != null) {
                    this.canvas.concat(matrix);
                }
                Path path2 = new PathConverter(this, path.d).getPath();
                if (path.boundingBox == null) {
                    path.boundingBox = calculatePathBounds(path2);
                }
                updateParentBoundingBox(path);
                checkForGradiantsAndPatterns(path);
                checkForClipPath(path);
                boolean pushLayer = pushLayer();
                if (this.state.hasFill) {
                    path2.setFillType(getFillTypeFromState());
                    doFilledPath(path, path2);
                }
                if (this.state.hasStroke) {
                    doStroke(path2);
                }
                renderMarkers(path);
                if (pushLayer) {
                    popLayer(path);
                }
            }
        }
    }

    private SVG.Box calculatePathBounds(Path path) {
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        return new SVG.Box(rectF.left, rectF.top, rectF.width(), rectF.height());
    }

    private void render(SVG.Rect rect) {
        debug("Rect render", new Object[0]);
        SVG.Length length = rect.width;
        if (length != null && rect.height != null && !length.isZero() && !rect.height.isZero()) {
            updateStyleForElement(this.state, rect);
            if (display() && visible()) {
                Matrix matrix = rect.transform;
                if (matrix != null) {
                    this.canvas.concat(matrix);
                }
                Path makePathAndBoundingBox = makePathAndBoundingBox(rect);
                updateParentBoundingBox(rect);
                checkForGradiantsAndPatterns(rect);
                checkForClipPath(rect);
                boolean pushLayer = pushLayer();
                if (this.state.hasFill) {
                    doFilledPath(rect, makePathAndBoundingBox);
                }
                if (this.state.hasStroke) {
                    doStroke(makePathAndBoundingBox);
                }
                if (pushLayer) {
                    popLayer(rect);
                }
            }
        }
    }

    private void render(SVG.Circle circle) {
        debug("Circle render", new Object[0]);
        SVG.Length length = circle.r;
        if (length != null && !length.isZero()) {
            updateStyleForElement(this.state, circle);
            if (display() && visible()) {
                Matrix matrix = circle.transform;
                if (matrix != null) {
                    this.canvas.concat(matrix);
                }
                Path makePathAndBoundingBox = makePathAndBoundingBox(circle);
                updateParentBoundingBox(circle);
                checkForGradiantsAndPatterns(circle);
                checkForClipPath(circle);
                boolean pushLayer = pushLayer();
                if (this.state.hasFill) {
                    doFilledPath(circle, makePathAndBoundingBox);
                }
                if (this.state.hasStroke) {
                    doStroke(makePathAndBoundingBox);
                }
                if (pushLayer) {
                    popLayer(circle);
                }
            }
        }
    }

    private void render(SVG.Ellipse ellipse) {
        debug("Ellipse render", new Object[0]);
        SVG.Length length = ellipse.rx;
        if (length != null && ellipse.ry != null && !length.isZero() && !ellipse.ry.isZero()) {
            updateStyleForElement(this.state, ellipse);
            if (display() && visible()) {
                Matrix matrix = ellipse.transform;
                if (matrix != null) {
                    this.canvas.concat(matrix);
                }
                Path makePathAndBoundingBox = makePathAndBoundingBox(ellipse);
                updateParentBoundingBox(ellipse);
                checkForGradiantsAndPatterns(ellipse);
                checkForClipPath(ellipse);
                boolean pushLayer = pushLayer();
                if (this.state.hasFill) {
                    doFilledPath(ellipse, makePathAndBoundingBox);
                }
                if (this.state.hasStroke) {
                    doStroke(makePathAndBoundingBox);
                }
                if (pushLayer) {
                    popLayer(ellipse);
                }
            }
        }
    }

    private void render(SVG.Line line) {
        debug("Line render", new Object[0]);
        updateStyleForElement(this.state, line);
        if (display() && visible() && this.state.hasStroke) {
            Matrix matrix = line.transform;
            if (matrix != null) {
                this.canvas.concat(matrix);
            }
            Path makePathAndBoundingBox = makePathAndBoundingBox(line);
            updateParentBoundingBox(line);
            checkForGradiantsAndPatterns(line);
            checkForClipPath(line);
            boolean pushLayer = pushLayer();
            doStroke(makePathAndBoundingBox);
            renderMarkers(line);
            if (pushLayer) {
                popLayer(line);
            }
        }
    }

    private List<MarkerVector> calculateMarkerPositions(SVG.Line line) {
        SVG.Length length = line.x1;
        float f = 0.0f;
        float floatValueX = length != null ? length.floatValueX(this) : 0.0f;
        SVG.Length length2 = line.y1;
        float floatValueY = length2 != null ? length2.floatValueY(this) : 0.0f;
        SVG.Length length3 = line.x2;
        float floatValueX2 = length3 != null ? length3.floatValueX(this) : 0.0f;
        SVG.Length length4 = line.y2;
        if (length4 != null) {
            f = length4.floatValueY(this);
        }
        ArrayList arrayList = new ArrayList(2);
        float f2 = floatValueX2 - floatValueX;
        float f3 = f - floatValueY;
        arrayList.add(new MarkerVector(this, floatValueX, floatValueY, f2, f3));
        arrayList.add(new MarkerVector(this, floatValueX2, f, f2, f3));
        return arrayList;
    }

    private void render(SVG.PolyLine polyLine) {
        debug("PolyLine render", new Object[0]);
        updateStyleForElement(this.state, polyLine);
        if (display() && visible()) {
            RendererState rendererState = this.state;
            if (rendererState.hasStroke || rendererState.hasFill) {
                Matrix matrix = polyLine.transform;
                if (matrix != null) {
                    this.canvas.concat(matrix);
                }
                if (polyLine.points.length >= 2) {
                    Path makePathAndBoundingBox = makePathAndBoundingBox(polyLine);
                    updateParentBoundingBox(polyLine);
                    checkForGradiantsAndPatterns(polyLine);
                    checkForClipPath(polyLine);
                    boolean pushLayer = pushLayer();
                    if (this.state.hasFill) {
                        doFilledPath(polyLine, makePathAndBoundingBox);
                    }
                    if (this.state.hasStroke) {
                        doStroke(makePathAndBoundingBox);
                    }
                    renderMarkers(polyLine);
                    if (pushLayer) {
                        popLayer(polyLine);
                    }
                }
            }
        }
    }

    private List<MarkerVector> calculateMarkerPositions(SVG.PolyLine polyLine) {
        int length = polyLine.points.length;
        int i = 2;
        if (length < 2) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        float[] fArr = polyLine.points;
        MarkerVector markerVector = new MarkerVector(this, fArr[0], fArr[1], 0.0f, 0.0f);
        float f = 0.0f;
        float f2 = 0.0f;
        while (i < length) {
            float[] fArr2 = polyLine.points;
            float f3 = fArr2[i];
            float f4 = fArr2[i + 1];
            markerVector.add(f3, f4);
            arrayList.add(markerVector);
            i += 2;
            markerVector = new MarkerVector(this, f3, f4, f3 - markerVector.x, f4 - markerVector.y);
            f2 = f4;
            f = f3;
        }
        if (polyLine instanceof SVG.Polygon) {
            float[] fArr3 = polyLine.points;
            if (!(f == fArr3[0] || f2 == fArr3[1])) {
                float f5 = fArr3[0];
                float f6 = fArr3[1];
                markerVector.add(f5, f6);
                arrayList.add(markerVector);
                MarkerVector markerVector2 = new MarkerVector(this, f5, f6, f5 - markerVector.x, f6 - markerVector.y);
                markerVector2.add((MarkerVector) arrayList.get(0));
                arrayList.add(markerVector2);
                arrayList.set(0, markerVector2);
            }
        } else {
            arrayList.add(markerVector);
        }
        return arrayList;
    }

    private void render(SVG.Polygon polygon) {
        debug("Polygon render", new Object[0]);
        updateStyleForElement(this.state, polygon);
        if (display() && visible()) {
            RendererState rendererState = this.state;
            if (rendererState.hasStroke || rendererState.hasFill) {
                Matrix matrix = polygon.transform;
                if (matrix != null) {
                    this.canvas.concat(matrix);
                }
                if (polygon.points.length >= 2) {
                    Path makePathAndBoundingBox = makePathAndBoundingBox(polygon);
                    updateParentBoundingBox(polygon);
                    checkForGradiantsAndPatterns(polygon);
                    checkForClipPath(polygon);
                    boolean pushLayer = pushLayer();
                    if (this.state.hasFill) {
                        doFilledPath(polygon, makePathAndBoundingBox);
                    }
                    if (this.state.hasStroke) {
                        doStroke(makePathAndBoundingBox);
                    }
                    renderMarkers(polygon);
                    if (pushLayer) {
                        popLayer(polygon);
                    }
                }
            }
        }
    }

    private void render(SVG.Text text) {
        debug("Text render", new Object[0]);
        updateStyleForElement(this.state, text);
        if (display()) {
            Matrix matrix = text.transform;
            if (matrix != null) {
                this.canvas.concat(matrix);
            }
            List<SVG.Length> list = text.x;
            float f = 0.0f;
            float floatValueX = (list == null || list.size() == 0) ? 0.0f : text.x.get(0).floatValueX(this);
            List<SVG.Length> list2 = text.y;
            float floatValueY = (list2 == null || list2.size() == 0) ? 0.0f : text.y.get(0).floatValueY(this);
            List<SVG.Length> list3 = text.dx;
            float floatValueX2 = (list3 == null || list3.size() == 0) ? 0.0f : text.dx.get(0).floatValueX(this);
            List<SVG.Length> list4 = text.dy;
            if (!(list4 == null || list4.size() == 0)) {
                f = text.dy.get(0).floatValueY(this);
            }
            SVG.Style.TextAnchor anchorPosition = getAnchorPosition();
            if (anchorPosition != SVG.Style.TextAnchor.Start) {
                float calculateTextWidth = calculateTextWidth(text);
                if (anchorPosition == SVG.Style.TextAnchor.Middle) {
                    calculateTextWidth /= 2.0f;
                }
                floatValueX -= calculateTextWidth;
            }
            if (text.boundingBox == null) {
                TextBoundsCalculator textBoundsCalculator = new TextBoundsCalculator(floatValueX, floatValueY);
                enumerateTextSpans(text, textBoundsCalculator);
                RectF rectF = textBoundsCalculator.bbox;
                text.boundingBox = new SVG.Box(rectF.left, rectF.top, rectF.width(), textBoundsCalculator.bbox.height());
            }
            updateParentBoundingBox(text);
            checkForGradiantsAndPatterns(text);
            checkForClipPath(text);
            boolean pushLayer = pushLayer();
            enumerateTextSpans(text, new PlainTextDrawer(floatValueX + floatValueX2, floatValueY + f));
            if (pushLayer) {
                popLayer(text);
            }
        }
    }

    private SVG.Style.TextAnchor getAnchorPosition() {
        SVG.Style.TextAnchor textAnchor;
        SVG.Style style = this.state.style;
        if (style.direction == SVG.Style.TextDirection.LTR || (textAnchor = style.textAnchor) == SVG.Style.TextAnchor.Middle) {
            return this.state.style.textAnchor;
        }
        SVG.Style.TextAnchor textAnchor2 = SVG.Style.TextAnchor.Start;
        return textAnchor == textAnchor2 ? SVG.Style.TextAnchor.End : textAnchor2;
    }

    /* access modifiers changed from: private */
    public class PlainTextDrawer extends TextProcessor {
        public float x;
        public float y;

        public PlainTextDrawer(float f, float f2) {
            super(SVGAndroidRenderer.this, null);
            this.x = f;
            this.y = f2;
        }

        @Override // com.caverock.androidsvg.SVGAndroidRenderer.TextProcessor
        public void processText(String str) {
            SVGAndroidRenderer.debug("TextSequence render", new Object[0]);
            if (SVGAndroidRenderer.this.visible()) {
                if (SVGAndroidRenderer.this.state.hasFill) {
                    SVGAndroidRenderer.this.canvas.drawText(str, this.x, this.y, SVGAndroidRenderer.this.state.fillPaint);
                }
                if (SVGAndroidRenderer.this.state.hasStroke) {
                    SVGAndroidRenderer.this.canvas.drawText(str, this.x, this.y, SVGAndroidRenderer.this.state.strokePaint);
                }
            }
            this.x += SVGAndroidRenderer.this.state.fillPaint.measureText(str);
        }
    }

    /* access modifiers changed from: private */
    public abstract class TextProcessor {
        public boolean doTextContainer(SVG.TextContainer textContainer) {
            return true;
        }

        public abstract void processText(String str);

        private TextProcessor(SVGAndroidRenderer sVGAndroidRenderer) {
        }

        /* synthetic */ TextProcessor(SVGAndroidRenderer sVGAndroidRenderer, TextProcessor textProcessor) {
            this(sVGAndroidRenderer);
        }
    }

    private void enumerateTextSpans(SVG.TextContainer textContainer, TextProcessor textProcessor) {
        if (display()) {
            Iterator<SVG.SvgObject> it = textContainer.children.iterator();
            boolean z = true;
            while (it.hasNext()) {
                SVG.SvgObject next = it.next();
                if (next instanceof SVG.TextSequence) {
                    textProcessor.processText(textXMLSpaceTransform(((SVG.TextSequence) next).text, z, !it.hasNext()));
                } else {
                    processTextChild(next, textProcessor);
                }
                z = false;
            }
        }
    }

    private void processTextChild(SVG.SvgObject svgObject, TextProcessor textProcessor) {
        float f;
        float f2;
        float f3;
        if (textProcessor.doTextContainer((SVG.TextContainer) svgObject)) {
            if (svgObject instanceof SVG.TextPath) {
                statePush();
                renderTextPath((SVG.TextPath) svgObject);
                statePop();
            } else if (svgObject instanceof SVG.TSpan) {
                debug("TSpan render", new Object[0]);
                statePush();
                SVG.TSpan tSpan = (SVG.TSpan) svgObject;
                updateStyleForElement(this.state, tSpan);
                if (display()) {
                    boolean z = textProcessor instanceof PlainTextDrawer;
                    float f4 = 0.0f;
                    if (z) {
                        List<SVG.Length> list = tSpan.x;
                        float floatValueX = (list == null || list.size() == 0) ? ((PlainTextDrawer) textProcessor).x : tSpan.x.get(0).floatValueX(this);
                        List<SVG.Length> list2 = tSpan.y;
                        f2 = (list2 == null || list2.size() == 0) ? ((PlainTextDrawer) textProcessor).y : tSpan.y.get(0).floatValueY(this);
                        List<SVG.Length> list3 = tSpan.dx;
                        f = (list3 == null || list3.size() == 0) ? 0.0f : tSpan.dx.get(0).floatValueX(this);
                        List<SVG.Length> list4 = tSpan.dy;
                        if (!(list4 == null || list4.size() == 0)) {
                            f4 = tSpan.dy.get(0).floatValueY(this);
                        }
                        f3 = f4;
                        f4 = floatValueX;
                    } else {
                        f3 = 0.0f;
                        f2 = 0.0f;
                        f = 0.0f;
                    }
                    checkForGradiantsAndPatterns((SVG.SvgElement) tSpan.getTextRoot());
                    if (z) {
                        PlainTextDrawer plainTextDrawer = (PlainTextDrawer) textProcessor;
                        plainTextDrawer.x = f4 + f;
                        plainTextDrawer.y = f2 + f3;
                    }
                    boolean pushLayer = pushLayer();
                    enumerateTextSpans(tSpan, textProcessor);
                    if (pushLayer) {
                        popLayer(tSpan);
                    }
                }
                statePop();
            } else if (svgObject instanceof SVG.TRef) {
                statePush();
                SVG.TRef tRef = (SVG.TRef) svgObject;
                updateStyleForElement(this.state, tRef);
                if (display()) {
                    checkForGradiantsAndPatterns((SVG.SvgElement) tRef.getTextRoot());
                    SVG.SvgObject resolveIRI = svgObject.document.resolveIRI(tRef.href);
                    if (resolveIRI == null || !(resolveIRI instanceof SVG.TextContainer)) {
                        error("Tref reference '%s' not found", tRef.href);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        extractRawText((SVG.TextContainer) resolveIRI, sb);
                        if (sb.length() > 0) {
                            textProcessor.processText(sb.toString());
                        }
                    }
                }
                statePop();
            }
        }
    }

    private void renderTextPath(SVG.TextPath textPath) {
        debug("TextPath render", new Object[0]);
        updateStyleForElement(this.state, textPath);
        if (display() && visible()) {
            SVG.SvgObject resolveIRI = textPath.document.resolveIRI(textPath.href);
            if (resolveIRI == null) {
                error("TextPath reference '%s' not found", textPath.href);
                return;
            }
            SVG.Path path = (SVG.Path) resolveIRI;
            Path path2 = new PathConverter(this, path.d).getPath();
            Matrix matrix = path.transform;
            if (matrix != null) {
                path2.transform(matrix);
            }
            PathMeasure pathMeasure = new PathMeasure(path2, false);
            SVG.Length length = textPath.startOffset;
            float floatValue = length != null ? length.floatValue(this, pathMeasure.getLength()) : 0.0f;
            SVG.Style.TextAnchor anchorPosition = getAnchorPosition();
            if (anchorPosition != SVG.Style.TextAnchor.Start) {
                float calculateTextWidth = calculateTextWidth(textPath);
                if (anchorPosition == SVG.Style.TextAnchor.Middle) {
                    calculateTextWidth /= 2.0f;
                }
                floatValue -= calculateTextWidth;
            }
            checkForGradiantsAndPatterns((SVG.SvgElement) textPath.getTextRoot());
            boolean pushLayer = pushLayer();
            enumerateTextSpans(textPath, new PathTextDrawer(path2, floatValue, 0.0f));
            if (pushLayer) {
                popLayer(textPath);
            }
        }
    }

    /* access modifiers changed from: private */
    public class PathTextDrawer extends PlainTextDrawer {
        private Path path;

        public PathTextDrawer(Path path2, float f, float f2) {
            super(f, f2);
            this.path = path2;
        }

        @Override // com.caverock.androidsvg.SVGAndroidRenderer.PlainTextDrawer, com.caverock.androidsvg.SVGAndroidRenderer.TextProcessor
        public void processText(String str) {
            if (SVGAndroidRenderer.this.visible()) {
                if (SVGAndroidRenderer.this.state.hasFill) {
                    SVGAndroidRenderer.this.canvas.drawTextOnPath(str, this.path, this.x, this.y, SVGAndroidRenderer.this.state.fillPaint);
                }
                if (SVGAndroidRenderer.this.state.hasStroke) {
                    SVGAndroidRenderer.this.canvas.drawTextOnPath(str, this.path, this.x, this.y, SVGAndroidRenderer.this.state.strokePaint);
                }
            }
            this.x += SVGAndroidRenderer.this.state.fillPaint.measureText(str);
        }
    }

    private float calculateTextWidth(SVG.TextContainer textContainer) {
        TextWidthCalculator textWidthCalculator = new TextWidthCalculator(this, null);
        enumerateTextSpans(textContainer, textWidthCalculator);
        return textWidthCalculator.x;
    }

    /* access modifiers changed from: private */
    public class TextWidthCalculator extends TextProcessor {
        public float x;

        private TextWidthCalculator() {
            super(SVGAndroidRenderer.this, null);
            this.x = 0.0f;
        }

        /* synthetic */ TextWidthCalculator(SVGAndroidRenderer sVGAndroidRenderer, TextWidthCalculator textWidthCalculator) {
            this();
        }

        @Override // com.caverock.androidsvg.SVGAndroidRenderer.TextProcessor
        public void processText(String str) {
            this.x += SVGAndroidRenderer.this.state.fillPaint.measureText(str);
        }
    }

    /* access modifiers changed from: private */
    public class TextBoundsCalculator extends TextProcessor {
        RectF bbox = new RectF();
        float x;
        float y;

        public TextBoundsCalculator(float f, float f2) {
            super(SVGAndroidRenderer.this, null);
            this.x = f;
            this.y = f2;
        }

        @Override // com.caverock.androidsvg.SVGAndroidRenderer.TextProcessor
        public boolean doTextContainer(SVG.TextContainer textContainer) {
            if (!(textContainer instanceof SVG.TextPath)) {
                return true;
            }
            SVG.TextPath textPath = (SVG.TextPath) textContainer;
            SVG.SvgObject resolveIRI = textContainer.document.resolveIRI(textPath.href);
            if (resolveIRI == null) {
                SVGAndroidRenderer.error("TextPath path reference '%s' not found", new Object[]{textPath.href});
                return false;
            }
            SVG.Path path = (SVG.Path) resolveIRI;
            Path path2 = new PathConverter(SVGAndroidRenderer.this, path.d).getPath();
            Matrix matrix = path.transform;
            if (matrix != null) {
                path2.transform(matrix);
            }
            RectF rectF = new RectF();
            path2.computeBounds(rectF, true);
            this.bbox.union(rectF);
            return false;
        }

        @Override // com.caverock.androidsvg.SVGAndroidRenderer.TextProcessor
        public void processText(String str) {
            if (SVGAndroidRenderer.this.visible()) {
                Rect rect = new Rect();
                SVGAndroidRenderer.this.state.fillPaint.getTextBounds(str, 0, str.length(), rect);
                RectF rectF = new RectF(rect);
                rectF.offset(this.x, this.y);
                this.bbox.union(rectF);
            }
            this.x += SVGAndroidRenderer.this.state.fillPaint.measureText(str);
        }
    }

    private void extractRawText(SVG.TextContainer textContainer, StringBuilder sb) {
        Iterator<SVG.SvgObject> it = textContainer.children.iterator();
        boolean z = true;
        while (it.hasNext()) {
            SVG.SvgObject next = it.next();
            if (next instanceof SVG.TextContainer) {
                extractRawText((SVG.TextContainer) next, sb);
            } else if (next instanceof SVG.TextSequence) {
                sb.append(textXMLSpaceTransform(((SVG.TextSequence) next).text, z, !it.hasNext()));
            }
            z = false;
        }
    }

    private String textXMLSpaceTransform(String str, boolean z, boolean z2) {
        if (this.state.spacePreserve) {
            return str.replaceAll("[\\n\\t]", " ");
        }
        String replaceAll = str.replaceAll("\\n", "").replaceAll("\\t", " ");
        if (z) {
            replaceAll = replaceAll.replaceAll("^\\s+", "");
        }
        if (z2) {
            replaceAll = replaceAll.replaceAll("\\s+$", "");
        }
        return replaceAll.replaceAll("\\s{2,}", " ");
    }

    private void render(SVG.Symbol symbol, SVG.Length length, SVG.Length length2) {
        debug("Symbol render", new Object[0]);
        if (length != null && length.isZero()) {
            return;
        }
        if (length2 == null || !length2.isZero()) {
            PreserveAspectRatio preserveAspectRatio = symbol.preserveAspectRatio;
            if (preserveAspectRatio == null) {
                preserveAspectRatio = PreserveAspectRatio.LETTERBOX;
            }
            updateStyleForElement(this.state, symbol);
            this.state.viewPort = new SVG.Box(0.0f, 0.0f, length != null ? length.floatValueX(this) : this.state.viewPort.width, length2 != null ? length2.floatValueX(this) : this.state.viewPort.height);
            if (!this.state.style.overflow.booleanValue()) {
                SVG.Box box = this.state.viewPort;
                setClipRect(box.minX, box.minY, box.width, box.height);
            }
            SVG.Box box2 = symbol.viewBox;
            if (box2 != null) {
                this.canvas.concat(calculateViewBoxTransform(this.state.viewPort, box2, preserveAspectRatio));
                this.state.viewBox = symbol.viewBox;
            }
            boolean pushLayer = pushLayer();
            renderChildren(symbol, true);
            if (pushLayer) {
                popLayer(symbol);
            }
            updateParentBoundingBox(symbol);
        }
    }

    private void render(SVG.Image image) {
        SVG.Length length;
        debug("Image render", new Object[0]);
        SVG.Length length2 = image.width;
        if (length2 != null && !length2.isZero() && (length = image.height) != null && !length.isZero() && image.href != null) {
            PreserveAspectRatio preserveAspectRatio = image.preserveAspectRatio;
            if (preserveAspectRatio == null) {
                preserveAspectRatio = PreserveAspectRatio.LETTERBOX;
            }
            Bitmap checkForImageDataURL = checkForImageDataURL(image.href);
            if (checkForImageDataURL == null) {
                SVGExternalFileResolver fileResolver = this.document.getFileResolver();
                if (fileResolver != null) {
                    checkForImageDataURL = fileResolver.resolveImage(image.href);
                } else {
                    return;
                }
            }
            if (checkForImageDataURL == null) {
                error("Could not locate image '%s'", image.href);
                return;
            }
            updateStyleForElement(this.state, image);
            if (display() && visible()) {
                Matrix matrix = image.transform;
                if (matrix != null) {
                    this.canvas.concat(matrix);
                }
                SVG.Length length3 = image.x;
                float floatValueX = length3 != null ? length3.floatValueX(this) : 0.0f;
                SVG.Length length4 = image.y;
                this.state.viewPort = new SVG.Box(floatValueX, length4 != null ? length4.floatValueY(this) : 0.0f, image.width.floatValueX(this), image.height.floatValueX(this));
                if (!this.state.style.overflow.booleanValue()) {
                    SVG.Box box = this.state.viewPort;
                    setClipRect(box.minX, box.minY, box.width, box.height);
                }
                SVG.Box box2 = new SVG.Box(0.0f, 0.0f, (float) checkForImageDataURL.getWidth(), (float) checkForImageDataURL.getHeight());
                image.boundingBox = box2;
                this.canvas.concat(calculateViewBoxTransform(this.state.viewPort, box2, preserveAspectRatio));
                updateParentBoundingBox(image);
                checkForClipPath(image);
                boolean pushLayer = pushLayer();
                viewportFill();
                this.canvas.drawBitmap(checkForImageDataURL, 0.0f, 0.0f, this.state.fillPaint);
                if (pushLayer) {
                    popLayer(image);
                }
            }
        }
    }

    private Bitmap checkForImageDataURL(String str) {
        int indexOf;
        if (!str.startsWith("data:") || str.length() < 14 || (indexOf = str.indexOf(44)) == -1 || indexOf < 12 || !";base64".equals(str.substring(indexOf - 7, indexOf))) {
            return null;
        }
        byte[] decode = Base64.decode(str.substring(indexOf + 1), 0);
        return BitmapFactory.decodeByteArray(decode, 0, decode.length);
    }

    private boolean display() {
        Boolean bool = this.state.style.display;
        if (bool != null) {
            return bool.booleanValue();
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean visible() {
        Boolean bool = this.state.style.visibility;
        if (bool != null) {
            return bool.booleanValue();
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x006e, code lost:
        if (r5 != 10) goto L_0x007a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x008c  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0090  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0095 A[PHI: r3 
      PHI: (r3v2 float) = (r3v1 float), (r3v3 float) binds: [B:28:0x0088, B:31:0x0094] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.Matrix calculateViewBoxTransform(com.caverock.androidsvg.SVG.Box r9, com.caverock.androidsvg.SVG.Box r10, com.caverock.androidsvg.PreserveAspectRatio r11) {
        /*
        // Method dump skipped, instructions count: 180
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGAndroidRenderer.calculateViewBoxTransform(com.caverock.androidsvg.SVG$Box, com.caverock.androidsvg.SVG$Box, com.caverock.androidsvg.PreserveAspectRatio):android.graphics.Matrix");
    }

    private boolean isSpecified(SVG.Style style, long j) {
        return (style.specifiedFlags & j) != 0;
    }

    private void updateStyle(RendererState rendererState, SVG.Style style) {
        SVG svg;
        if (isSpecified(style, 4096)) {
            rendererState.style.color = style.color;
        }
        if (isSpecified(style, 2048)) {
            rendererState.style.opacity = style.opacity;
        }
        boolean z = false;
        if (isSpecified(style, 1)) {
            rendererState.style.fill = style.fill;
            rendererState.hasFill = style.fill != null;
        }
        if (isSpecified(style, 4)) {
            rendererState.style.fillOpacity = style.fillOpacity;
        }
        if (isSpecified(style, 6149)) {
            setPaintColour(rendererState, true, rendererState.style.fill);
        }
        if (isSpecified(style, 2)) {
            rendererState.style.fillRule = style.fillRule;
        }
        if (isSpecified(style, 8)) {
            rendererState.style.stroke = style.stroke;
            rendererState.hasStroke = style.stroke != null;
        }
        if (isSpecified(style, 16)) {
            rendererState.style.strokeOpacity = style.strokeOpacity;
        }
        if (isSpecified(style, 6168)) {
            setPaintColour(rendererState, false, rendererState.style.stroke);
        }
        if (isSpecified(style, 34359738368L)) {
            rendererState.style.vectorEffect = style.vectorEffect;
        }
        if (isSpecified(style, 32)) {
            SVG.Style style2 = rendererState.style;
            SVG.Length length = style.strokeWidth;
            style2.strokeWidth = length;
            rendererState.strokePaint.setStrokeWidth(length.floatValue(this));
        }
        if (isSpecified(style, 64)) {
            rendererState.style.strokeLineCap = style.strokeLineCap;
            int i = $SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineCaps()[style.strokeLineCap.ordinal()];
            if (i == 1) {
                rendererState.strokePaint.setStrokeCap(Paint.Cap.BUTT);
            } else if (i == 2) {
                rendererState.strokePaint.setStrokeCap(Paint.Cap.ROUND);
            } else if (i == 3) {
                rendererState.strokePaint.setStrokeCap(Paint.Cap.SQUARE);
            }
        }
        if (isSpecified(style, 128)) {
            rendererState.style.strokeLineJoin = style.strokeLineJoin;
            int i2 = $SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$LineJoin()[style.strokeLineJoin.ordinal()];
            if (i2 == 1) {
                rendererState.strokePaint.setStrokeJoin(Paint.Join.MITER);
            } else if (i2 == 2) {
                rendererState.strokePaint.setStrokeJoin(Paint.Join.ROUND);
            } else if (i2 == 3) {
                rendererState.strokePaint.setStrokeJoin(Paint.Join.BEVEL);
            }
        }
        if (isSpecified(style, 256)) {
            rendererState.style.strokeMiterLimit = style.strokeMiterLimit;
            rendererState.strokePaint.setStrokeMiter(style.strokeMiterLimit.floatValue());
        }
        if (isSpecified(style, 512)) {
            rendererState.style.strokeDashArray = style.strokeDashArray;
        }
        if (isSpecified(style, 1024)) {
            rendererState.style.strokeDashOffset = style.strokeDashOffset;
        }
        Typeface typeface = null;
        if (isSpecified(style, 1536)) {
            SVG.Length[] lengthArr = rendererState.style.strokeDashArray;
            if (lengthArr == null) {
                rendererState.strokePaint.setPathEffect(null);
            } else {
                int length2 = lengthArr.length;
                int i3 = length2 % 2 == 0 ? length2 : length2 * 2;
                float[] fArr = new float[i3];
                float f = 0.0f;
                for (int i4 = 0; i4 < i3; i4++) {
                    fArr[i4] = rendererState.style.strokeDashArray[i4 % length2].floatValue(this);
                    f += fArr[i4];
                }
                if (f == 0.0f) {
                    rendererState.strokePaint.setPathEffect(null);
                } else {
                    float floatValue = rendererState.style.strokeDashOffset.floatValue(this);
                    if (floatValue < 0.0f) {
                        floatValue = (floatValue % f) + f;
                    }
                    rendererState.strokePaint.setPathEffect(new DashPathEffect(fArr, floatValue));
                }
            }
        }
        if (isSpecified(style, 16384)) {
            float currentFontSize = getCurrentFontSize();
            rendererState.style.fontSize = style.fontSize;
            rendererState.fillPaint.setTextSize(style.fontSize.floatValue(this, currentFontSize));
            rendererState.strokePaint.setTextSize(style.fontSize.floatValue(this, currentFontSize));
        }
        if (isSpecified(style, 8192)) {
            rendererState.style.fontFamily = style.fontFamily;
        }
        if (isSpecified(style, 32768)) {
            if (style.fontWeight.intValue() == -1 && rendererState.style.fontWeight.intValue() > 100) {
                SVG.Style style3 = rendererState.style;
                style3.fontWeight = Integer.valueOf(style3.fontWeight.intValue() - 100);
            } else if (style.fontWeight.intValue() != 1 || rendererState.style.fontWeight.intValue() >= 900) {
                rendererState.style.fontWeight = style.fontWeight;
            } else {
                SVG.Style style4 = rendererState.style;
                style4.fontWeight = Integer.valueOf(style4.fontWeight.intValue() + 100);
            }
        }
        if (isSpecified(style, 65536)) {
            rendererState.style.fontStyle = style.fontStyle;
        }
        if (isSpecified(style, 106496)) {
            if (rendererState.style.fontFamily != null && (svg = this.document) != null) {
                SVGExternalFileResolver fileResolver = svg.getFileResolver();
                for (String str : rendererState.style.fontFamily) {
                    SVG.Style style5 = rendererState.style;
                    Typeface checkGenericFont = checkGenericFont(str, style5.fontWeight, style5.fontStyle);
                    if (checkGenericFont != null || fileResolver == null) {
                        typeface = checkGenericFont;
                        continue;
                    } else {
                        typeface = fileResolver.resolveFont(str, rendererState.style.fontWeight.intValue(), String.valueOf(rendererState.style.fontStyle));
                        continue;
                    }
                    if (typeface != null) {
                        break;
                    }
                }
            }
            if (typeface == null) {
                SVG.Style style6 = rendererState.style;
                typeface = checkGenericFont("sans-serif", style6.fontWeight, style6.fontStyle);
            }
            rendererState.fillPaint.setTypeface(typeface);
            rendererState.strokePaint.setTypeface(typeface);
        }
        if (isSpecified(style, 131072)) {
            rendererState.style.textDecoration = style.textDecoration;
            rendererState.fillPaint.setStrikeThruText(style.textDecoration == SVG.Style.TextDecoration.LineThrough);
            rendererState.fillPaint.setUnderlineText(style.textDecoration == SVG.Style.TextDecoration.Underline);
            if (Build.VERSION.SDK_INT >= 17) {
                rendererState.strokePaint.setStrikeThruText(style.textDecoration == SVG.Style.TextDecoration.LineThrough);
                Paint paint = rendererState.strokePaint;
                if (style.textDecoration == SVG.Style.TextDecoration.Underline) {
                    z = true;
                }
                paint.setUnderlineText(z);
            }
        }
        if (isSpecified(style, 68719476736L)) {
            rendererState.style.direction = style.direction;
        }
        if (isSpecified(style, 262144)) {
            rendererState.style.textAnchor = style.textAnchor;
        }
        if (isSpecified(style, 524288)) {
            rendererState.style.overflow = style.overflow;
        }
        if (isSpecified(style, 2097152)) {
            rendererState.style.markerStart = style.markerStart;
        }
        if (isSpecified(style, 4194304)) {
            rendererState.style.markerMid = style.markerMid;
        }
        if (isSpecified(style, 8388608)) {
            rendererState.style.markerEnd = style.markerEnd;
        }
        if (isSpecified(style, 16777216)) {
            rendererState.style.display = style.display;
        }
        if (isSpecified(style, 33554432)) {
            rendererState.style.visibility = style.visibility;
        }
        if (isSpecified(style, 1048576)) {
            rendererState.style.clip = style.clip;
        }
        if (isSpecified(style, 268435456)) {
            rendererState.style.clipPath = style.clipPath;
        }
        if (isSpecified(style, 536870912)) {
            rendererState.style.clipRule = style.clipRule;
        }
        if (isSpecified(style, 1073741824)) {
            rendererState.style.mask = style.mask;
        }
        if (isSpecified(style, 67108864)) {
            rendererState.style.stopColor = style.stopColor;
        }
        if (isSpecified(style, 134217728)) {
            rendererState.style.stopOpacity = style.stopOpacity;
        }
        if (isSpecified(style, 8589934592L)) {
            rendererState.style.viewportFill = style.viewportFill;
        }
        if (isSpecified(style, 17179869184L)) {
            rendererState.style.viewportFillOpacity = style.viewportFillOpacity;
        }
    }

    private void setPaintColour(RendererState rendererState, boolean z, SVG.SvgPaint svgPaint) {
        int i;
        SVG.Style style = rendererState.style;
        float floatValue = (z ? style.fillOpacity : style.strokeOpacity).floatValue();
        if (svgPaint instanceof SVG.Colour) {
            i = ((SVG.Colour) svgPaint).colour;
        } else if (svgPaint instanceof SVG.CurrentColor) {
            i = rendererState.style.color.colour;
        } else {
            return;
        }
        int clamp255 = (clamp255(floatValue) << 24) | i;
        if (z) {
            rendererState.fillPaint.setColor(clamp255);
        } else {
            rendererState.strokePaint.setColor(clamp255);
        }
    }

    private Typeface checkGenericFont(String str, Integer num, SVG.Style.FontStyle fontStyle) {
        int i = 1;
        boolean z = fontStyle == SVG.Style.FontStyle.Italic;
        if (num.intValue() <= 500) {
            i = z ? 2 : 0;
        } else if (z) {
            i = 3;
        }
        if (str.equals("serif")) {
            return Typeface.create(Typeface.SERIF, i);
        }
        if (str.equals("sans-serif")) {
            return Typeface.create(Typeface.SANS_SERIF, i);
        }
        if (str.equals("monospace")) {
            return Typeface.create(Typeface.MONOSPACE, i);
        }
        if (str.equals("cursive")) {
            return Typeface.create(Typeface.SANS_SERIF, i);
        }
        if (str.equals("fantasy")) {
            return Typeface.create(Typeface.SANS_SERIF, i);
        }
        return null;
    }

    private Path.FillType getFillTypeFromState() {
        if (this.state.style.fillRule == null) {
            return Path.FillType.WINDING;
        }
        if ($SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$FillRule()[this.state.style.fillRule.ordinal()] != 2) {
            return Path.FillType.WINDING;
        }
        return Path.FillType.EVEN_ODD;
    }

    private void setClipRect(float f, float f2, float f3, float f4) {
        float f5 = f3 + f;
        float f6 = f4 + f2;
        SVG.CSSClipRect cSSClipRect = this.state.style.clip;
        if (cSSClipRect != null) {
            f += cSSClipRect.left.floatValueX(this);
            f2 += this.state.style.clip.top.floatValueY(this);
            f5 -= this.state.style.clip.right.floatValueX(this);
            f6 -= this.state.style.clip.bottom.floatValueY(this);
        }
        this.canvas.clipRect(f, f2, f5, f6);
    }

    private void viewportFill() {
        int i;
        SVG.Style style = this.state.style;
        SVG.SvgPaint svgPaint = style.viewportFill;
        if (svgPaint instanceof SVG.Colour) {
            i = ((SVG.Colour) svgPaint).colour;
        } else if (svgPaint instanceof SVG.CurrentColor) {
            i = style.color.colour;
        } else {
            return;
        }
        Float f = this.state.style.viewportFillOpacity;
        if (f != null) {
            i |= clamp255(f.floatValue()) << 24;
        }
        this.canvas.drawColor(i);
    }

    /* access modifiers changed from: private */
    public class PathConverter implements SVG.PathInterface {
        float lastX;
        float lastY;
        Path path = new Path();

        public PathConverter(SVGAndroidRenderer sVGAndroidRenderer, SVG.PathDefinition pathDefinition) {
            pathDefinition.enumeratePath(this);
        }

        public Path getPath() {
            return this.path;
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void moveTo(float f, float f2) {
            this.path.moveTo(f, f2);
            this.lastX = f;
            this.lastY = f2;
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void lineTo(float f, float f2) {
            this.path.lineTo(f, f2);
            this.lastX = f;
            this.lastY = f2;
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void cubicTo(float f, float f2, float f3, float f4, float f5, float f6) {
            this.path.cubicTo(f, f2, f3, f4, f5, f6);
            this.lastX = f5;
            this.lastY = f6;
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void quadTo(float f, float f2, float f3, float f4) {
            this.path.quadTo(f, f2, f3, f4);
            this.lastX = f3;
            this.lastY = f4;
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void arcTo(float f, float f2, float f3, boolean z, boolean z2, float f4, float f5) {
            SVGAndroidRenderer.arcTo(this.lastX, this.lastY, f, f2, f3, z, z2, f4, f5, this);
            this.lastX = f4;
            this.lastY = f5;
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void close() {
            this.path.close();
        }
    }

    /* access modifiers changed from: private */
    public static void arcTo(float f, float f2, float f3, float f4, float f5, boolean z, boolean z2, float f6, float f7, SVG.PathInterface pathInterface) {
        double d;
        if (!(f == f6 && f2 == f7)) {
            if (f3 == 0.0f || f4 == 0.0f) {
                pathInterface.lineTo(f6, f7);
                return;
            }
            float abs = Math.abs(f3);
            float abs2 = Math.abs(f4);
            double radians = (double) ((float) Math.toRadians(((double) f5) % 360.0d));
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            double d2 = ((double) (f - f6)) / 2.0d;
            double d3 = ((double) (f2 - f7)) / 2.0d;
            double d4 = (cos * d2) + (sin * d3);
            double d5 = ((-sin) * d2) + (d3 * cos);
            double d6 = (double) (abs * abs);
            double d7 = (double) (abs2 * abs2);
            double d8 = d4 * d4;
            double d9 = d5 * d5;
            double d10 = (d8 / d6) + (d9 / d7);
            double d11 = 1.0d;
            if (d10 > 1.0d) {
                abs *= (float) Math.sqrt(d10);
                abs2 *= (float) Math.sqrt(d10);
                d6 = (double) (abs * abs);
                d7 = (double) (abs2 * abs2);
            }
            double d12 = (double) (z == z2 ? -1 : 1);
            double d13 = d6 * d7;
            double d14 = d6 * d9;
            double d15 = d7 * d8;
            double d16 = ((d13 - d14) - d15) / (d14 + d15);
            if (d16 < 0.0d) {
                d16 = 0.0d;
            }
            double sqrt = d12 * Math.sqrt(d16);
            double d17 = (double) abs;
            double d18 = (double) abs2;
            double d19 = ((d17 * d5) / d18) * sqrt;
            double d20 = sqrt * (-((d18 * d4) / d17));
            double d21 = (((double) (f + f6)) / 2.0d) + ((cos * d19) - (sin * d20));
            double d22 = (((double) (f2 + f7)) / 2.0d) + (sin * d19) + (cos * d20);
            double d23 = (d4 - d19) / d17;
            double d24 = (d5 - d20) / d18;
            double d25 = ((-d4) - d19) / d17;
            double d26 = ((-d5) - d20) / d18;
            double d27 = (d23 * d23) + (d24 * d24);
            double degrees = Math.toDegrees((d24 < 0.0d ? -1.0d : 1.0d) * Math.acos(d23 / Math.sqrt(d27)));
            double sqrt2 = Math.sqrt(d27 * ((d25 * d25) + (d26 * d26)));
            double d28 = (d23 * d25) + (d24 * d26);
            if ((d23 * d26) - (d24 * d25) < 0.0d) {
                d11 = -1.0d;
            }
            double degrees2 = Math.toDegrees(d11 * Math.acos(d28 / sqrt2));
            if (z2 || degrees2 <= 0.0d) {
                d = 360.0d;
                if (z2 && degrees2 < 0.0d) {
                    degrees2 += 360.0d;
                }
            } else {
                d = 360.0d;
                degrees2 -= 360.0d;
            }
            float[] arcToBeziers = arcToBeziers(degrees % d, degrees2 % d);
            Matrix matrix = new Matrix();
            matrix.postScale(abs, abs2);
            matrix.postRotate(f5);
            matrix.postTranslate((float) d21, (float) d22);
            matrix.mapPoints(arcToBeziers);
            arcToBeziers[arcToBeziers.length - 2] = f6;
            arcToBeziers[arcToBeziers.length - 1] = f7;
            for (int i = 0; i < arcToBeziers.length; i += 6) {
                pathInterface.cubicTo(arcToBeziers[i], arcToBeziers[i + 1], arcToBeziers[i + 2], arcToBeziers[i + 3], arcToBeziers[i + 4], arcToBeziers[i + 5]);
            }
        }
    }

    private static float[] arcToBeziers(double d, double d2) {
        int ceil = (int) Math.ceil(Math.abs(d2) / 90.0d);
        double radians = Math.toRadians(d);
        float radians2 = (float) (Math.toRadians(d2) / ((double) ceil));
        double d3 = (double) radians2;
        double d4 = d3 / 2.0d;
        double sin = (Math.sin(d4) * 1.3333333333333333d) / (Math.cos(d4) + 1.0d);
        float[] fArr = new float[(ceil * 6)];
        int i = 0;
        int i2 = 0;
        while (i < ceil) {
            double d5 = ((double) (((float) i) * radians2)) + radians;
            double cos = Math.cos(d5);
            double sin2 = Math.sin(d5);
            int i3 = i2 + 1;
            fArr[i2] = (float) (cos - (sin * sin2));
            int i4 = i3 + 1;
            fArr[i3] = (float) (sin2 + (cos * sin));
            double d6 = d5 + d3;
            double cos2 = Math.cos(d6);
            double sin3 = Math.sin(d6);
            int i5 = i4 + 1;
            fArr[i4] = (float) ((sin * sin3) + cos2);
            int i6 = i5 + 1;
            fArr[i5] = (float) (sin3 - (sin * cos2));
            int i7 = i6 + 1;
            fArr[i6] = (float) cos2;
            fArr[i7] = (float) sin3;
            i++;
            radians = radians;
            i2 = i7 + 1;
            ceil = ceil;
        }
        return fArr;
    }

    /* access modifiers changed from: private */
    public class MarkerVector {
        public float dx = 0.0f;
        public float dy = 0.0f;
        public float x;
        public float y;

        public MarkerVector(SVGAndroidRenderer sVGAndroidRenderer, float f, float f2, float f3, float f4) {
            this.x = f;
            this.y = f2;
            double sqrt = Math.sqrt((double) ((f3 * f3) + (f4 * f4)));
            if (sqrt != 0.0d) {
                this.dx = (float) (((double) f3) / sqrt);
                this.dy = (float) (((double) f4) / sqrt);
            }
        }

        public void add(float f, float f2) {
            float f3 = f - this.x;
            float f4 = f2 - this.y;
            double sqrt = Math.sqrt((double) ((f3 * f3) + (f4 * f4)));
            if (sqrt != 0.0d) {
                this.dx += (float) (((double) f3) / sqrt);
                this.dy += (float) (((double) f4) / sqrt);
            }
        }

        public void add(MarkerVector markerVector) {
            this.dx += markerVector.dx;
            this.dy += markerVector.dy;
        }

        public String toString() {
            return "(" + this.x + "," + this.y + " " + this.dx + "," + this.dy + ")";
        }
    }

    /* access modifiers changed from: private */
    public class MarkerPositionCalculator implements SVG.PathInterface {
        private boolean closepathReAdjustPending;
        private MarkerVector lastPos = null;
        private List<MarkerVector> markers = new ArrayList();
        private boolean normalCubic = true;
        private boolean startArc = false;
        private float startX;
        private float startY;
        private int subpathStartIndex = -1;

        public MarkerPositionCalculator(SVG.PathDefinition pathDefinition) {
            pathDefinition.enumeratePath(this);
            if (this.closepathReAdjustPending) {
                this.lastPos.add(this.markers.get(this.subpathStartIndex));
                this.markers.set(this.subpathStartIndex, this.lastPos);
                this.closepathReAdjustPending = false;
            }
            MarkerVector markerVector = this.lastPos;
            if (markerVector != null) {
                this.markers.add(markerVector);
            }
        }

        public List<MarkerVector> getMarkers() {
            return this.markers;
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void moveTo(float f, float f2) {
            if (this.closepathReAdjustPending) {
                this.lastPos.add(this.markers.get(this.subpathStartIndex));
                this.markers.set(this.subpathStartIndex, this.lastPos);
                this.closepathReAdjustPending = false;
            }
            MarkerVector markerVector = this.lastPos;
            if (markerVector != null) {
                this.markers.add(markerVector);
            }
            this.startX = f;
            this.startY = f2;
            this.lastPos = new MarkerVector(SVGAndroidRenderer.this, f, f2, 0.0f, 0.0f);
            this.subpathStartIndex = this.markers.size();
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void lineTo(float f, float f2) {
            this.lastPos.add(f, f2);
            this.markers.add(this.lastPos);
            SVGAndroidRenderer sVGAndroidRenderer = SVGAndroidRenderer.this;
            MarkerVector markerVector = this.lastPos;
            this.lastPos = new MarkerVector(sVGAndroidRenderer, f, f2, f - markerVector.x, f2 - markerVector.y);
            this.closepathReAdjustPending = false;
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void cubicTo(float f, float f2, float f3, float f4, float f5, float f6) {
            if (this.normalCubic || this.startArc) {
                this.lastPos.add(f, f2);
                this.markers.add(this.lastPos);
                this.startArc = false;
            }
            this.lastPos = new MarkerVector(SVGAndroidRenderer.this, f5, f6, f5 - f3, f6 - f4);
            this.closepathReAdjustPending = false;
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void quadTo(float f, float f2, float f3, float f4) {
            this.lastPos.add(f, f2);
            this.markers.add(this.lastPos);
            this.lastPos = new MarkerVector(SVGAndroidRenderer.this, f3, f4, f3 - f, f4 - f2);
            this.closepathReAdjustPending = false;
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void arcTo(float f, float f2, float f3, boolean z, boolean z2, float f4, float f5) {
            this.startArc = true;
            this.normalCubic = false;
            MarkerVector markerVector = this.lastPos;
            SVGAndroidRenderer.arcTo(markerVector.x, markerVector.y, f, f2, f3, z, z2, f4, f5, this);
            this.normalCubic = true;
            this.closepathReAdjustPending = false;
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void close() {
            this.markers.add(this.lastPos);
            lineTo(this.startX, this.startY);
            this.closepathReAdjustPending = true;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x003f  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x008b  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x009e A[ADDED_TO_REGION, RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00bd  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00d1  */
    /* JADX WARNING: Removed duplicated region for block: B:52:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void renderMarkers(com.caverock.androidsvg.SVG.GraphicsElement r9) {
        /*
        // Method dump skipped, instructions count: 220
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGAndroidRenderer.renderMarkers(com.caverock.androidsvg.SVG$GraphicsElement):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x003a  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x003d  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x006a  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x006f  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00a9  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0119  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x011c  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0138  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void renderMarker(com.caverock.androidsvg.SVG.Marker r12, com.caverock.androidsvg.SVGAndroidRenderer.MarkerVector r13) {
        /*
        // Method dump skipped, instructions count: 336
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGAndroidRenderer.renderMarker(com.caverock.androidsvg.SVG$Marker, com.caverock.androidsvg.SVGAndroidRenderer$MarkerVector):void");
    }

    private RendererState findInheritFromAncestorState(SVG.SvgObject svgObject) {
        RendererState rendererState = new RendererState(this);
        updateStyle(rendererState, SVG.Style.getDefaultStyle());
        findInheritFromAncestorState(svgObject, rendererState);
        return rendererState;
    }

    private RendererState findInheritFromAncestorState(SVG.SvgObject svgObject, RendererState rendererState) {
        ArrayList<SVG.SvgElementBase> arrayList = new ArrayList();
        while (true) {
            if (svgObject instanceof SVG.SvgElementBase) {
                arrayList.add(0, (SVG.SvgElementBase) svgObject);
            }
            SVG.SvgContainer svgContainer = svgObject.parent;
            if (svgContainer == null) {
                break;
            }
            svgObject = (SVG.SvgObject) svgContainer;
        }
        for (SVG.SvgElementBase svgElementBase : arrayList) {
            updateStyleForElement(rendererState, svgElementBase);
        }
        SVG.Box box = this.document.getRootElement().viewBox;
        rendererState.viewBox = box;
        if (box == null) {
            rendererState.viewBox = this.canvasViewPort;
        }
        rendererState.viewPort = this.canvasViewPort;
        rendererState.directRendering = this.state.directRendering;
        return rendererState;
    }

    private void checkForGradiantsAndPatterns(SVG.SvgElement svgElement) {
        SVG.SvgPaint svgPaint = this.state.style.fill;
        if (svgPaint instanceof SVG.PaintReference) {
            decodePaintReference(true, svgElement.boundingBox, (SVG.PaintReference) svgPaint);
        }
        SVG.SvgPaint svgPaint2 = this.state.style.stroke;
        if (svgPaint2 instanceof SVG.PaintReference) {
            decodePaintReference(false, svgElement.boundingBox, (SVG.PaintReference) svgPaint2);
        }
    }

    private void decodePaintReference(boolean z, SVG.Box box, SVG.PaintReference paintReference) {
        SVG.SvgObject resolveIRI = this.document.resolveIRI(paintReference.href);
        if (resolveIRI == null) {
            Object[] objArr = new Object[2];
            objArr[0] = z ? "Fill" : "Stroke";
            objArr[1] = paintReference.href;
            error("%s reference '%s' not found", objArr);
            SVG.SvgPaint svgPaint = paintReference.fallback;
            if (svgPaint != null) {
                setPaintColour(this.state, z, svgPaint);
            } else if (z) {
                this.state.hasFill = false;
            } else {
                this.state.hasStroke = false;
            }
        } else {
            if (resolveIRI instanceof SVG.SvgLinearGradient) {
                makeLinearGradiant(z, box, (SVG.SvgLinearGradient) resolveIRI);
            }
            if (resolveIRI instanceof SVG.SvgRadialGradient) {
                makeRadialGradiant(z, box, (SVG.SvgRadialGradient) resolveIRI);
            }
            if (resolveIRI instanceof SVG.SolidColor) {
                setSolidColor(z, (SVG.SolidColor) resolveIRI);
            }
        }
    }

    private void makeLinearGradiant(boolean z, SVG.Box box, SVG.SvgLinearGradient svgLinearGradient) {
        float f;
        float f2;
        float f3;
        float f4;
        String str = svgLinearGradient.href;
        if (str != null) {
            fillInChainedGradientFields(svgLinearGradient, str);
        }
        Boolean bool = svgLinearGradient.gradientUnitsAreUser;
        int i = 0;
        boolean z2 = bool != null && bool.booleanValue();
        RendererState rendererState = this.state;
        Paint paint = z ? rendererState.fillPaint : rendererState.strokePaint;
        float f5 = 0.0f;
        if (z2) {
            SVG.Box currentViewPortInUserUnits = getCurrentViewPortInUserUnits();
            SVG.Length length = svgLinearGradient.x1;
            float floatValueX = length != null ? length.floatValueX(this) : 0.0f;
            SVG.Length length2 = svgLinearGradient.y1;
            float floatValueY = length2 != null ? length2.floatValueY(this) : 0.0f;
            SVG.Length length3 = svgLinearGradient.x2;
            float floatValueX2 = length3 != null ? length3.floatValueX(this) : currentViewPortInUserUnits.width;
            SVG.Length length4 = svgLinearGradient.y2;
            if (length4 != null) {
                f5 = length4.floatValueY(this);
            }
            f = f5;
            f2 = floatValueX2;
            f4 = floatValueX;
            f3 = floatValueY;
        } else {
            SVG.Length length5 = svgLinearGradient.x1;
            float floatValue = length5 != null ? length5.floatValue(this, 1.0f) : 0.0f;
            SVG.Length length6 = svgLinearGradient.y1;
            float floatValue2 = length6 != null ? length6.floatValue(this, 1.0f) : 0.0f;
            SVG.Length length7 = svgLinearGradient.x2;
            float floatValue3 = length7 != null ? length7.floatValue(this, 1.0f) : 1.0f;
            SVG.Length length8 = svgLinearGradient.y2;
            if (length8 != null) {
                f5 = length8.floatValue(this, 1.0f);
            }
            f = f5;
            f4 = floatValue;
            f3 = floatValue2;
            f2 = floatValue3;
        }
        statePush();
        this.state = findInheritFromAncestorState(svgLinearGradient);
        Matrix matrix = new Matrix();
        if (!z2) {
            matrix.preTranslate(box.minX, box.minY);
            matrix.preScale(box.width, box.height);
        }
        Matrix matrix2 = svgLinearGradient.gradientTransform;
        if (matrix2 != null) {
            matrix.preConcat(matrix2);
        }
        int size = svgLinearGradient.children.size();
        if (size == 0) {
            statePop();
            if (z) {
                this.state.hasFill = false;
            } else {
                this.state.hasStroke = false;
            }
        } else {
            int[] iArr = new int[size];
            float[] fArr = new float[size];
            float f6 = -1.0f;
            Iterator<SVG.SvgObject> it = svgLinearGradient.children.iterator();
            while (it.hasNext()) {
                SVG.Stop stop = (SVG.Stop) it.next();
                if (i == 0 || stop.offset.floatValue() >= f6) {
                    fArr[i] = stop.offset.floatValue();
                    f6 = stop.offset.floatValue();
                } else {
                    fArr[i] = f6;
                }
                statePush();
                updateStyleForElement(this.state, stop);
                SVG.Colour colour = (SVG.Colour) this.state.style.stopColor;
                if (colour == null) {
                    colour = SVG.Colour.BLACK;
                }
                iArr[i] = colour.colour | (clamp255(this.state.style.stopOpacity.floatValue()) << 24);
                i++;
                statePop();
            }
            if ((f4 == f2 && f3 == f) || size == 1) {
                statePop();
                paint.setColor(iArr[size - 1]);
                return;
            }
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            SVG.GradientSpread gradientSpread = svgLinearGradient.spreadMethod;
            if (gradientSpread != null) {
                if (gradientSpread == SVG.GradientSpread.reflect) {
                    tileMode = Shader.TileMode.MIRROR;
                } else if (gradientSpread == SVG.GradientSpread.repeat) {
                    tileMode = Shader.TileMode.REPEAT;
                }
            }
            statePop();
            LinearGradient linearGradient = new LinearGradient(f4, f3, f2, f, iArr, fArr, tileMode);
            linearGradient.setLocalMatrix(matrix);
            paint.setShader(linearGradient);
        }
    }

    private void makeRadialGradiant(boolean z, SVG.Box box, SVG.SvgRadialGradient svgRadialGradient) {
        float f;
        float f2;
        float f3;
        String str = svgRadialGradient.href;
        if (str != null) {
            fillInChainedGradientFields(svgRadialGradient, str);
        }
        Boolean bool = svgRadialGradient.gradientUnitsAreUser;
        int i = 0;
        boolean z2 = bool != null && bool.booleanValue();
        RendererState rendererState = this.state;
        Paint paint = z ? rendererState.fillPaint : rendererState.strokePaint;
        if (z2) {
            SVG.Length length = new SVG.Length(50.0f, SVG.Unit.percent);
            SVG.Length length2 = svgRadialGradient.cx;
            float floatValueX = length2 != null ? length2.floatValueX(this) : length.floatValueX(this);
            SVG.Length length3 = svgRadialGradient.cy;
            float floatValueY = length3 != null ? length3.floatValueY(this) : length.floatValueY(this);
            SVG.Length length4 = svgRadialGradient.r;
            f = length4 != null ? length4.floatValue(this) : length.floatValue(this);
            f3 = floatValueX;
            f2 = floatValueY;
        } else {
            SVG.Length length5 = svgRadialGradient.cx;
            float floatValue = length5 != null ? length5.floatValue(this, 1.0f) : 0.5f;
            SVG.Length length6 = svgRadialGradient.cy;
            float floatValue2 = length6 != null ? length6.floatValue(this, 1.0f) : 0.5f;
            SVG.Length length7 = svgRadialGradient.r;
            f3 = floatValue;
            f = length7 != null ? length7.floatValue(this, 1.0f) : 0.5f;
            f2 = floatValue2;
        }
        statePush();
        this.state = findInheritFromAncestorState(svgRadialGradient);
        Matrix matrix = new Matrix();
        if (!z2) {
            matrix.preTranslate(box.minX, box.minY);
            matrix.preScale(box.width, box.height);
        }
        Matrix matrix2 = svgRadialGradient.gradientTransform;
        if (matrix2 != null) {
            matrix.preConcat(matrix2);
        }
        int size = svgRadialGradient.children.size();
        if (size == 0) {
            statePop();
            if (z) {
                this.state.hasFill = false;
            } else {
                this.state.hasStroke = false;
            }
        } else {
            int[] iArr = new int[size];
            float[] fArr = new float[size];
            float f4 = -1.0f;
            Iterator<SVG.SvgObject> it = svgRadialGradient.children.iterator();
            while (it.hasNext()) {
                SVG.Stop stop = (SVG.Stop) it.next();
                if (i == 0 || stop.offset.floatValue() >= f4) {
                    fArr[i] = stop.offset.floatValue();
                    f4 = stop.offset.floatValue();
                } else {
                    fArr[i] = f4;
                }
                statePush();
                updateStyleForElement(this.state, stop);
                SVG.Colour colour = (SVG.Colour) this.state.style.stopColor;
                if (colour == null) {
                    colour = SVG.Colour.BLACK;
                }
                iArr[i] = colour.colour | (clamp255(this.state.style.stopOpacity.floatValue()) << 24);
                i++;
                statePop();
            }
            if (f == 0.0f || size == 1) {
                statePop();
                paint.setColor(iArr[size - 1]);
                return;
            }
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            SVG.GradientSpread gradientSpread = svgRadialGradient.spreadMethod;
            if (gradientSpread != null) {
                if (gradientSpread == SVG.GradientSpread.reflect) {
                    tileMode = Shader.TileMode.MIRROR;
                } else if (gradientSpread == SVG.GradientSpread.repeat) {
                    tileMode = Shader.TileMode.REPEAT;
                }
            }
            statePop();
            RadialGradient radialGradient = new RadialGradient(f3, f2, f, iArr, fArr, tileMode);
            radialGradient.setLocalMatrix(matrix);
            paint.setShader(radialGradient);
        }
    }

    private void fillInChainedGradientFields(SVG.GradientElement gradientElement, String str) {
        SVG.SvgObject resolveIRI = gradientElement.document.resolveIRI(str);
        if (resolveIRI == null) {
            warn("Gradient reference '%s' not found", str);
        } else if (!(resolveIRI instanceof SVG.GradientElement)) {
            error("Gradient href attributes must point to other gradient elements", new Object[0]);
        } else if (resolveIRI == gradientElement) {
            error("Circular reference in gradient href attribute '%s'", str);
        } else {
            SVG.GradientElement gradientElement2 = (SVG.GradientElement) resolveIRI;
            if (gradientElement.gradientUnitsAreUser == null) {
                gradientElement.gradientUnitsAreUser = gradientElement2.gradientUnitsAreUser;
            }
            if (gradientElement.gradientTransform == null) {
                gradientElement.gradientTransform = gradientElement2.gradientTransform;
            }
            if (gradientElement.spreadMethod == null) {
                gradientElement.spreadMethod = gradientElement2.spreadMethod;
            }
            if (gradientElement.children.isEmpty()) {
                gradientElement.children = gradientElement2.children;
            }
            try {
                if (gradientElement instanceof SVG.SvgLinearGradient) {
                    fillInChainedGradientFields((SVG.SvgLinearGradient) gradientElement, (SVG.SvgLinearGradient) resolveIRI);
                } else {
                    fillInChainedGradientFields((SVG.SvgRadialGradient) gradientElement, (SVG.SvgRadialGradient) resolveIRI);
                }
            } catch (ClassCastException unused) {
            }
            String str2 = gradientElement2.href;
            if (str2 != null) {
                fillInChainedGradientFields(gradientElement, str2);
            }
        }
    }

    private void fillInChainedGradientFields(SVG.SvgLinearGradient svgLinearGradient, SVG.SvgLinearGradient svgLinearGradient2) {
        if (svgLinearGradient.x1 == null) {
            svgLinearGradient.x1 = svgLinearGradient2.x1;
        }
        if (svgLinearGradient.y1 == null) {
            svgLinearGradient.y1 = svgLinearGradient2.y1;
        }
        if (svgLinearGradient.x2 == null) {
            svgLinearGradient.x2 = svgLinearGradient2.x2;
        }
        if (svgLinearGradient.y2 == null) {
            svgLinearGradient.y2 = svgLinearGradient2.y2;
        }
    }

    private void fillInChainedGradientFields(SVG.SvgRadialGradient svgRadialGradient, SVG.SvgRadialGradient svgRadialGradient2) {
        if (svgRadialGradient.cx == null) {
            svgRadialGradient.cx = svgRadialGradient2.cx;
        }
        if (svgRadialGradient.cy == null) {
            svgRadialGradient.cy = svgRadialGradient2.cy;
        }
        if (svgRadialGradient.r == null) {
            svgRadialGradient.r = svgRadialGradient2.r;
        }
        if (svgRadialGradient.fx == null) {
            svgRadialGradient.fx = svgRadialGradient2.fx;
        }
        if (svgRadialGradient.fy == null) {
            svgRadialGradient.fy = svgRadialGradient2.fy;
        }
    }

    private void setSolidColor(boolean z, SVG.SolidColor solidColor) {
        boolean z2 = true;
        if (z) {
            if (isSpecified(solidColor.baseStyle, 2147483648L)) {
                RendererState rendererState = this.state;
                SVG.Style style = rendererState.style;
                SVG.SvgPaint svgPaint = solidColor.baseStyle.solidColor;
                style.fill = svgPaint;
                if (svgPaint == null) {
                    z2 = false;
                }
                rendererState.hasFill = z2;
            }
            if (isSpecified(solidColor.baseStyle, 4294967296L)) {
                this.state.style.fillOpacity = solidColor.baseStyle.solidOpacity;
            }
            if (isSpecified(solidColor.baseStyle, 6442450944L)) {
                RendererState rendererState2 = this.state;
                setPaintColour(rendererState2, z, rendererState2.style.fill);
                return;
            }
            return;
        }
        if (isSpecified(solidColor.baseStyle, 2147483648L)) {
            RendererState rendererState3 = this.state;
            SVG.Style style2 = rendererState3.style;
            SVG.SvgPaint svgPaint2 = solidColor.baseStyle.solidColor;
            style2.stroke = svgPaint2;
            if (svgPaint2 == null) {
                z2 = false;
            }
            rendererState3.hasStroke = z2;
        }
        if (isSpecified(solidColor.baseStyle, 4294967296L)) {
            this.state.style.strokeOpacity = solidColor.baseStyle.solidOpacity;
        }
        if (isSpecified(solidColor.baseStyle, 6442450944L)) {
            RendererState rendererState4 = this.state;
            setPaintColour(rendererState4, z, rendererState4.style.stroke);
        }
    }

    private void checkForClipPath(SVG.SvgElement svgElement) {
        checkForClipPath(svgElement, svgElement.boundingBox);
    }

    private void checkForClipPath(SVG.SvgElement svgElement, SVG.Box box) {
        String str = this.state.style.clipPath;
        if (str != null) {
            SVG.SvgObject resolveIRI = svgElement.document.resolveIRI(str);
            if (resolveIRI == null) {
                error("ClipPath reference '%s' not found", this.state.style.clipPath);
                return;
            }
            SVG.ClipPath clipPath = (SVG.ClipPath) resolveIRI;
            if (clipPath.children.isEmpty()) {
                this.canvas.clipRect(0, 0, 0, 0);
                return;
            }
            Boolean bool = clipPath.clipPathUnitsAreUser;
            boolean z = bool == null || bool.booleanValue();
            if (!(svgElement instanceof SVG.Group) || z) {
                clipStatePush();
                if (!z) {
                    Matrix matrix = new Matrix();
                    matrix.preTranslate(box.minX, box.minY);
                    matrix.preScale(box.width, box.height);
                    this.canvas.concat(matrix);
                }
                Matrix matrix2 = clipPath.transform;
                if (matrix2 != null) {
                    this.canvas.concat(matrix2);
                }
                this.state = findInheritFromAncestorState(clipPath);
                checkForClipPath(clipPath);
                Path path = new Path();
                for (SVG.SvgObject svgObject : clipPath.children) {
                    addObjectToClip(svgObject, true, path, new Matrix());
                }
                this.canvas.clipPath(path);
                clipStatePop();
                return;
            }
            warn("<clipPath clipPathUnits=\"objectBoundingBox\"> is not supported when referenced from container elements (like %s)", svgElement.getClass().getSimpleName());
        }
    }

    private void addObjectToClip(SVG.SvgObject svgObject, boolean z, Path path, Matrix matrix) {
        if (display()) {
            clipStatePush();
            if (svgObject instanceof SVG.Use) {
                if (z) {
                    addObjectToClip((SVG.Use) svgObject, path, matrix);
                } else {
                    error("<use> elements inside a <clipPath> cannot reference another <use>", new Object[0]);
                }
            } else if (svgObject instanceof SVG.Path) {
                addObjectToClip((SVG.Path) svgObject, path, matrix);
            } else if (svgObject instanceof SVG.Text) {
                addObjectToClip((SVG.Text) svgObject, path, matrix);
            } else if (svgObject instanceof SVG.GraphicsElement) {
                addObjectToClip((SVG.GraphicsElement) svgObject, path, matrix);
            } else {
                error("Invalid %s element found in clipPath definition", svgObject.getClass().getSimpleName());
            }
            clipStatePop();
        }
    }

    private void clipStatePush() {
        this.canvas.save(1);
        this.stateStack.push(this.state);
        this.state = (RendererState) this.state.clone();
    }

    private void clipStatePop() {
        this.canvas.restore();
        this.state = this.stateStack.pop();
    }

    private Path.FillType getClipRuleFromState() {
        if (this.state.style.clipRule == null) {
            return Path.FillType.WINDING;
        }
        if ($SWITCH_TABLE$com$caverock$androidsvg$SVG$Style$FillRule()[this.state.style.clipRule.ordinal()] != 2) {
            return Path.FillType.WINDING;
        }
        return Path.FillType.EVEN_ODD;
    }

    private void addObjectToClip(SVG.Path path, Path path2, Matrix matrix) {
        updateStyleForElement(this.state, path);
        if (display() && visible()) {
            Matrix matrix2 = path.transform;
            if (matrix2 != null) {
                matrix.preConcat(matrix2);
            }
            Path path3 = new PathConverter(this, path.d).getPath();
            if (path.boundingBox == null) {
                path.boundingBox = calculatePathBounds(path3);
            }
            checkForClipPath(path);
            path2.setFillType(getClipRuleFromState());
            path2.addPath(path3, matrix);
        }
    }

    private void addObjectToClip(SVG.GraphicsElement graphicsElement, Path path, Matrix matrix) {
        Path path2;
        updateStyleForElement(this.state, graphicsElement);
        if (display() && visible()) {
            Matrix matrix2 = graphicsElement.transform;
            if (matrix2 != null) {
                matrix.preConcat(matrix2);
            }
            if (graphicsElement instanceof SVG.Rect) {
                path2 = makePathAndBoundingBox((SVG.Rect) graphicsElement);
            } else if (graphicsElement instanceof SVG.Circle) {
                path2 = makePathAndBoundingBox((SVG.Circle) graphicsElement);
            } else if (graphicsElement instanceof SVG.Ellipse) {
                path2 = makePathAndBoundingBox((SVG.Ellipse) graphicsElement);
            } else if (graphicsElement instanceof SVG.PolyLine) {
                path2 = makePathAndBoundingBox((SVG.PolyLine) graphicsElement);
            } else {
                return;
            }
            checkForClipPath(graphicsElement);
            path.setFillType(path2.getFillType());
            path.addPath(path2, matrix);
        }
    }

    private void addObjectToClip(SVG.Use use, Path path, Matrix matrix) {
        updateStyleForElement(this.state, use);
        if (display() && visible()) {
            Matrix matrix2 = use.transform;
            if (matrix2 != null) {
                matrix.preConcat(matrix2);
            }
            SVG.SvgObject resolveIRI = use.document.resolveIRI(use.href);
            if (resolveIRI == null) {
                error("Use reference '%s' not found", use.href);
                return;
            }
            checkForClipPath(use);
            addObjectToClip(resolveIRI, false, path, matrix);
        }
    }

    private void addObjectToClip(SVG.Text text, Path path, Matrix matrix) {
        updateStyleForElement(this.state, text);
        if (display()) {
            Matrix matrix2 = text.transform;
            if (matrix2 != null) {
                matrix.preConcat(matrix2);
            }
            List<SVG.Length> list = text.x;
            float f = 0.0f;
            float floatValueX = (list == null || list.size() == 0) ? 0.0f : text.x.get(0).floatValueX(this);
            List<SVG.Length> list2 = text.y;
            float floatValueY = (list2 == null || list2.size() == 0) ? 0.0f : text.y.get(0).floatValueY(this);
            List<SVG.Length> list3 = text.dx;
            float floatValueX2 = (list3 == null || list3.size() == 0) ? 0.0f : text.dx.get(0).floatValueX(this);
            List<SVG.Length> list4 = text.dy;
            if (!(list4 == null || list4.size() == 0)) {
                f = text.dy.get(0).floatValueY(this);
            }
            if (this.state.style.textAnchor != SVG.Style.TextAnchor.Start) {
                float calculateTextWidth = calculateTextWidth(text);
                if (this.state.style.textAnchor == SVG.Style.TextAnchor.Middle) {
                    calculateTextWidth /= 2.0f;
                }
                floatValueX -= calculateTextWidth;
            }
            if (text.boundingBox == null) {
                TextBoundsCalculator textBoundsCalculator = new TextBoundsCalculator(floatValueX, floatValueY);
                enumerateTextSpans(text, textBoundsCalculator);
                RectF rectF = textBoundsCalculator.bbox;
                text.boundingBox = new SVG.Box(rectF.left, rectF.top, rectF.width(), textBoundsCalculator.bbox.height());
            }
            checkForClipPath(text);
            Path path2 = new Path();
            enumerateTextSpans(text, new PlainTextToPath(floatValueX + floatValueX2, floatValueY + f, path2));
            path.setFillType(getClipRuleFromState());
            path.addPath(path2, matrix);
        }
    }

    /* access modifiers changed from: private */
    public class PlainTextToPath extends TextProcessor {
        public Path textAsPath;
        public float x;
        public float y;

        public PlainTextToPath(float f, float f2, Path path) {
            super(SVGAndroidRenderer.this, null);
            this.x = f;
            this.y = f2;
            this.textAsPath = path;
        }

        @Override // com.caverock.androidsvg.SVGAndroidRenderer.TextProcessor
        public boolean doTextContainer(SVG.TextContainer textContainer) {
            if (!(textContainer instanceof SVG.TextPath)) {
                return true;
            }
            SVGAndroidRenderer.warn("Using <textPath> elements in a clip path is not supported.", new Object[0]);
            return false;
        }

        @Override // com.caverock.androidsvg.SVGAndroidRenderer.TextProcessor
        public void processText(String str) {
            if (SVGAndroidRenderer.this.visible()) {
                Path path = new Path();
                SVGAndroidRenderer.this.state.fillPaint.getTextPath(str, 0, str.length(), this.x, this.y, path);
                this.textAsPath.addPath(path);
            }
            this.x += SVGAndroidRenderer.this.state.fillPaint.measureText(str);
        }
    }

    private Path makePathAndBoundingBox(SVG.Line line) {
        SVG.Length length = line.x1;
        float f = 0.0f;
        float floatValue = length == null ? 0.0f : length.floatValue(this);
        SVG.Length length2 = line.y1;
        float floatValue2 = length2 == null ? 0.0f : length2.floatValue(this);
        SVG.Length length3 = line.x2;
        float floatValue3 = length3 == null ? 0.0f : length3.floatValue(this);
        SVG.Length length4 = line.y2;
        if (length4 != null) {
            f = length4.floatValue(this);
        }
        if (line.boundingBox == null) {
            line.boundingBox = new SVG.Box(Math.min(floatValue, floatValue2), Math.min(floatValue2, f), Math.abs(floatValue3 - floatValue), Math.abs(f - floatValue2));
        }
        Path path = new Path();
        path.moveTo(floatValue, floatValue2);
        path.lineTo(floatValue3, f);
        return path;
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x004f  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0054  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x005a  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x006b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.Path makePathAndBoundingBox(com.caverock.androidsvg.SVG.Rect r23) {
        /*
        // Method dump skipped, instructions count: 239
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGAndroidRenderer.makePathAndBoundingBox(com.caverock.androidsvg.SVG$Rect):android.graphics.Path");
    }

    private Path makePathAndBoundingBox(SVG.Circle circle) {
        SVG.Length length = circle.cx;
        float f = 0.0f;
        float floatValueX = length != null ? length.floatValueX(this) : 0.0f;
        SVG.Length length2 = circle.cy;
        if (length2 != null) {
            f = length2.floatValueY(this);
        }
        float floatValue = circle.r.floatValue(this);
        float f2 = floatValueX - floatValue;
        float f3 = f - floatValue;
        float f4 = floatValueX + floatValue;
        float f5 = f + floatValue;
        if (circle.boundingBox == null) {
            float f6 = 2.0f * floatValue;
            circle.boundingBox = new SVG.Box(f2, f3, f6, f6);
        }
        float f7 = floatValue * 0.5522848f;
        Path path = new Path();
        path.moveTo(floatValueX, f3);
        float f8 = floatValueX + f7;
        float f9 = f - f7;
        path.cubicTo(f8, f3, f4, f9, f4, f);
        float f10 = f + f7;
        path.cubicTo(f4, f10, f8, f5, floatValueX, f5);
        float f11 = floatValueX - f7;
        path.cubicTo(f11, f5, f2, f10, f2, f);
        path.cubicTo(f2, f9, f11, f3, floatValueX, f3);
        path.close();
        return path;
    }

    private Path makePathAndBoundingBox(SVG.Ellipse ellipse) {
        SVG.Length length = ellipse.cx;
        float f = 0.0f;
        float floatValueX = length != null ? length.floatValueX(this) : 0.0f;
        SVG.Length length2 = ellipse.cy;
        if (length2 != null) {
            f = length2.floatValueY(this);
        }
        float floatValueX2 = ellipse.rx.floatValueX(this);
        float floatValueY = ellipse.ry.floatValueY(this);
        float f2 = floatValueX - floatValueX2;
        float f3 = f - floatValueY;
        float f4 = floatValueX + floatValueX2;
        float f5 = f + floatValueY;
        if (ellipse.boundingBox == null) {
            ellipse.boundingBox = new SVG.Box(f2, f3, floatValueX2 * 2.0f, 2.0f * floatValueY);
        }
        float f6 = floatValueX2 * 0.5522848f;
        float f7 = floatValueY * 0.5522848f;
        Path path = new Path();
        path.moveTo(floatValueX, f3);
        float f8 = floatValueX + f6;
        float f9 = f - f7;
        path.cubicTo(f8, f3, f4, f9, f4, f);
        float f10 = f7 + f;
        path.cubicTo(f4, f10, f8, f5, floatValueX, f5);
        float f11 = floatValueX - f6;
        path.cubicTo(f11, f5, f2, f10, f2, f);
        path.cubicTo(f2, f9, f11, f3, floatValueX, f3);
        path.close();
        return path;
    }

    private Path makePathAndBoundingBox(SVG.PolyLine polyLine) {
        Path path = new Path();
        float[] fArr = polyLine.points;
        path.moveTo(fArr[0], fArr[1]);
        int i = 2;
        while (true) {
            float[] fArr2 = polyLine.points;
            if (i >= fArr2.length) {
                break;
            }
            path.lineTo(fArr2[i], fArr2[i + 1]);
            i += 2;
        }
        if (polyLine instanceof SVG.Polygon) {
            path.close();
        }
        if (polyLine.boundingBox == null) {
            polyLine.boundingBox = calculatePathBounds(path);
        }
        path.setFillType(getClipRuleFromState());
        return path;
    }

    private void fillWithPattern(SVG.SvgElement svgElement, Path path, SVG.Pattern pattern) {
        float f;
        float f2;
        float f3;
        float f4;
        Boolean bool = pattern.patternUnitsAreUser;
        boolean z = bool != null && bool.booleanValue();
        String str = pattern.href;
        if (str != null) {
            fillInChainedPatternFields(pattern, str);
        }
        if (z) {
            SVG.Length length = pattern.x;
            f4 = length != null ? length.floatValueX(this) : 0.0f;
            SVG.Length length2 = pattern.y;
            f3 = length2 != null ? length2.floatValueY(this) : 0.0f;
            SVG.Length length3 = pattern.width;
            f2 = length3 != null ? length3.floatValueX(this) : 0.0f;
            SVG.Length length4 = pattern.height;
            f = length4 != null ? length4.floatValueY(this) : 0.0f;
        } else {
            SVG.Length length5 = pattern.x;
            float floatValue = length5 != null ? length5.floatValue(this, 1.0f) : 0.0f;
            SVG.Length length6 = pattern.y;
            float floatValue2 = length6 != null ? length6.floatValue(this, 1.0f) : 0.0f;
            SVG.Length length7 = pattern.width;
            float floatValue3 = length7 != null ? length7.floatValue(this, 1.0f) : 0.0f;
            SVG.Length length8 = pattern.height;
            float floatValue4 = length8 != null ? length8.floatValue(this, 1.0f) : 0.0f;
            SVG.Box box = svgElement.boundingBox;
            float f5 = box.minX;
            float f6 = box.width;
            f4 = (floatValue * f6) + f5;
            float f7 = box.minY;
            float f8 = box.height;
            float f9 = floatValue3 * f6;
            f = floatValue4 * f8;
            f3 = (floatValue2 * f8) + f7;
            f2 = f9;
        }
        if (!(f2 == 0.0f || f == 0.0f)) {
            PreserveAspectRatio preserveAspectRatio = pattern.preserveAspectRatio;
            if (preserveAspectRatio == null) {
                preserveAspectRatio = PreserveAspectRatio.LETTERBOX;
            }
            statePush();
            this.canvas.clipPath(path);
            RendererState rendererState = new RendererState(this);
            updateStyle(rendererState, SVG.Style.getDefaultStyle());
            rendererState.style.overflow = Boolean.FALSE;
            findInheritFromAncestorState(pattern, rendererState);
            this.state = rendererState;
            SVG.Box box2 = svgElement.boundingBox;
            Matrix matrix = pattern.patternTransform;
            if (matrix != null) {
                this.canvas.concat(matrix);
                Matrix matrix2 = new Matrix();
                if (pattern.patternTransform.invert(matrix2)) {
                    SVG.Box box3 = svgElement.boundingBox;
                    SVG.Box box4 = svgElement.boundingBox;
                    SVG.Box box5 = svgElement.boundingBox;
                    float[] fArr = {box3.minX, box3.minY, box3.maxX(), box4.minY, box4.maxX(), svgElement.boundingBox.maxY(), box5.minX, box5.maxY()};
                    matrix2.mapPoints(fArr);
                    RectF rectF = new RectF(fArr[0], fArr[1], fArr[0], fArr[1]);
                    for (int i = 2; i <= 6; i += 2) {
                        if (fArr[i] < rectF.left) {
                            rectF.left = fArr[i];
                        }
                        if (fArr[i] > rectF.right) {
                            rectF.right = fArr[i];
                        }
                        int i2 = i + 1;
                        if (fArr[i2] < rectF.top) {
                            rectF.top = fArr[i2];
                        }
                        if (fArr[i2] > rectF.bottom) {
                            rectF.bottom = fArr[i2];
                        }
                    }
                    float f10 = rectF.left;
                    float f11 = rectF.top;
                    box2 = new SVG.Box(f10, f11, rectF.right - f10, rectF.bottom - f11);
                }
            }
            float floor = f4 + (((float) Math.floor((double) ((box2.minX - f4) / f2))) * f2);
            float maxX = box2.maxX();
            float maxY = box2.maxY();
            SVG.Box box6 = new SVG.Box(0.0f, 0.0f, f2, f);
            for (float floor2 = f3 + (((float) Math.floor((double) ((box2.minY - f3) / f))) * f); floor2 < maxY; floor2 += f) {
                for (float f12 = floor; f12 < maxX; f12 += f2) {
                    box6.minX = f12;
                    box6.minY = floor2;
                    statePush();
                    if (!this.state.style.overflow.booleanValue()) {
                        setClipRect(box6.minX, box6.minY, box6.width, box6.height);
                    }
                    SVG.Box box7 = pattern.viewBox;
                    if (box7 != null) {
                        this.canvas.concat(calculateViewBoxTransform(box6, box7, preserveAspectRatio));
                    } else {
                        Boolean bool2 = pattern.patternContentUnitsAreUser;
                        boolean z2 = bool2 == null || bool2.booleanValue();
                        this.canvas.translate(f12, floor2);
                        if (!z2) {
                            Canvas canvas2 = this.canvas;
                            SVG.Box box8 = svgElement.boundingBox;
                            canvas2.scale(box8.width, box8.height);
                        }
                    }
                    boolean pushLayer = pushLayer();
                    for (SVG.SvgObject svgObject : pattern.children) {
                        render(svgObject);
                    }
                    if (pushLayer) {
                        popLayer(pattern);
                    }
                    statePop();
                }
            }
            statePop();
        }
    }

    private void fillInChainedPatternFields(SVG.Pattern pattern, String str) {
        SVG.SvgObject resolveIRI = pattern.document.resolveIRI(str);
        if (resolveIRI == null) {
            warn("Pattern reference '%s' not found", str);
        } else if (!(resolveIRI instanceof SVG.Pattern)) {
            error("Pattern href attributes must point to other pattern elements", new Object[0]);
        } else if (resolveIRI == pattern) {
            error("Circular reference in pattern href attribute '%s'", str);
        } else {
            SVG.Pattern pattern2 = (SVG.Pattern) resolveIRI;
            if (pattern.patternUnitsAreUser == null) {
                pattern.patternUnitsAreUser = pattern2.patternUnitsAreUser;
            }
            if (pattern.patternContentUnitsAreUser == null) {
                pattern.patternContentUnitsAreUser = pattern2.patternContentUnitsAreUser;
            }
            if (pattern.patternTransform == null) {
                pattern.patternTransform = pattern2.patternTransform;
            }
            if (pattern.x == null) {
                pattern.x = pattern2.x;
            }
            if (pattern.y == null) {
                pattern.y = pattern2.y;
            }
            if (pattern.width == null) {
                pattern.width = pattern2.width;
            }
            if (pattern.height == null) {
                pattern.height = pattern2.height;
            }
            if (pattern.children.isEmpty()) {
                pattern.children = pattern2.children;
            }
            if (pattern.viewBox == null) {
                pattern.viewBox = pattern2.viewBox;
            }
            if (pattern.preserveAspectRatio == null) {
                pattern.preserveAspectRatio = pattern2.preserveAspectRatio;
            }
            String str2 = pattern2.href;
            if (str2 != null) {
                fillInChainedPatternFields(pattern, str2);
            }
        }
    }

    private void renderMask(SVG.Mask mask, SVG.SvgElement svgElement) {
        float f;
        float f2;
        debug("Mask render", new Object[0]);
        Boolean bool = mask.maskUnitsAreUser;
        boolean z = true;
        if (bool != null && bool.booleanValue()) {
            SVG.Length length = mask.width;
            f2 = length != null ? length.floatValueX(this) : svgElement.boundingBox.width;
            SVG.Length length2 = mask.height;
            f = length2 != null ? length2.floatValueY(this) : svgElement.boundingBox.height;
            SVG.Length length3 = mask.x;
            if (length3 != null) {
                length3.floatValueX(this);
            } else {
                SVG.Box box = svgElement.boundingBox;
                float f3 = box.minX;
                float f4 = box.width;
            }
            SVG.Length length4 = mask.y;
            if (length4 != null) {
                length4.floatValueY(this);
            } else {
                SVG.Box box2 = svgElement.boundingBox;
                float f5 = box2.minY;
                float f6 = box2.height;
            }
        } else {
            SVG.Length length5 = mask.x;
            if (length5 != null) {
                length5.floatValue(this, 1.0f);
            }
            SVG.Length length6 = mask.y;
            if (length6 != null) {
                length6.floatValue(this, 1.0f);
            }
            SVG.Length length7 = mask.width;
            float f7 = 1.2f;
            float floatValue = length7 != null ? length7.floatValue(this, 1.0f) : 1.2f;
            SVG.Length length8 = mask.height;
            if (length8 != null) {
                f7 = length8.floatValue(this, 1.0f);
            }
            SVG.Box box3 = svgElement.boundingBox;
            float f8 = box3.minX;
            float f9 = box3.width;
            float f10 = box3.minY;
            f2 = floatValue * f9;
            f = f7 * box3.height;
        }
        if (f2 != 0.0f && f != 0.0f) {
            statePush();
            RendererState findInheritFromAncestorState = findInheritFromAncestorState(mask);
            this.state = findInheritFromAncestorState;
            findInheritFromAncestorState.style.opacity = Float.valueOf(1.0f);
            Boolean bool2 = mask.maskContentUnitsAreUser;
            if (bool2 != null && !bool2.booleanValue()) {
                z = false;
            }
            if (!z) {
                Canvas canvas2 = this.canvas;
                SVG.Box box4 = svgElement.boundingBox;
                canvas2.translate(box4.minX, box4.minY);
                Canvas canvas3 = this.canvas;
                SVG.Box box5 = svgElement.boundingBox;
                canvas3.scale(box5.width, box5.height);
            }
            renderChildren(mask, false);
            statePop();
        }
    }
}
