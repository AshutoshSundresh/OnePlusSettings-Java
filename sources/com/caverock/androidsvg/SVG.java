package com.caverock.androidsvg;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.graphics.RectF;
import com.android.settings.wifi.UseOpenWifiPreferenceController;
import com.caverock.androidsvg.CSSParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.xml.sax.SAXException;

public class SVG {
    private static final List<SvgObject> EMPTY_CHILD_LIST = new ArrayList(0);
    private CSSParser.Ruleset cssRules = new CSSParser.Ruleset();
    private SVGExternalFileResolver fileResolver = null;
    private float renderDPI = 96.0f;
    private Svg rootElement = null;

    protected enum GradientSpread {
        pad,
        reflect,
        repeat
    }

    protected interface HasTransform {
        void setTransform(Matrix matrix);
    }

    protected interface NotDirectlyRendered {
    }

    /* access modifiers changed from: protected */
    public interface PathInterface {
        void arcTo(float f, float f2, float f3, boolean z, boolean z2, float f4, float f5);

        void close();

        void cubicTo(float f, float f2, float f3, float f4, float f5, float f6);

        void lineTo(float f, float f2);

        void moveTo(float f, float f2);

        void quadTo(float f, float f2, float f3, float f4);
    }

    protected interface SvgConditional {
        String getRequiredExtensions();

        Set<String> getRequiredFeatures();

        Set<String> getRequiredFonts();

        Set<String> getRequiredFormats();

        Set<String> getSystemLanguage();

        void setRequiredExtensions(String str);

        void setRequiredFeatures(Set<String> set);

        void setRequiredFonts(Set<String> set);

        void setRequiredFormats(Set<String> set);

        void setSystemLanguage(Set<String> set);
    }

    /* access modifiers changed from: protected */
    public interface SvgContainer {
        void addChild(SvgObject svgObject) throws SAXException;

        List<SvgObject> getChildren();
    }

    protected interface TextChild {
        TextRoot getTextRoot();
    }

    protected interface TextRoot {
    }

    /* access modifiers changed from: protected */
    public enum Unit {
        px,
        em,
        ex,
        in,
        cm,
        mm,
        pt,
        pc,
        percent
    }

    /* access modifiers changed from: protected */
    public void setDesc(String str) {
    }

    /* access modifiers changed from: protected */
    public void setTitle(String str) {
    }

    protected SVG() {
    }

    public static SVG getFromInputStream(InputStream inputStream) throws SVGParseException {
        return new SVGParser().parse(inputStream);
    }

    public static SVG getFromResource(Context context, int i) throws SVGParseException {
        return new SVGParser().parse(context.getResources().openRawResource(i));
    }

    public static SVG getFromAsset(AssetManager assetManager, String str) throws SVGParseException, IOException {
        SVGParser sVGParser = new SVGParser();
        InputStream open = assetManager.open(str);
        SVG parse = sVGParser.parse(open);
        open.close();
        return parse;
    }

    public Picture renderToPicture() {
        float f;
        Length length = this.rootElement.width;
        if (length == null) {
            return renderToPicture(512, 512);
        }
        float floatValue = length.floatValue(this.renderDPI);
        Svg svg = this.rootElement;
        Box box = svg.viewBox;
        if (box != null) {
            f = (box.height * floatValue) / box.width;
        } else {
            Length length2 = svg.height;
            f = length2 != null ? length2.floatValue(this.renderDPI) : floatValue;
        }
        return renderToPicture((int) Math.ceil((double) floatValue), (int) Math.ceil((double) f));
    }

    public Picture renderToPicture(int i, int i2) {
        Picture picture = new Picture();
        new SVGAndroidRenderer(picture.beginRecording(i, i2), new Box(0.0f, 0.0f, (float) i, (float) i2), this.renderDPI).renderDocument(this, null, null, false);
        picture.endRecording();
        return picture;
    }

    public void renderToCanvas(Canvas canvas) {
        renderToCanvas(canvas, null);
    }

    public void renderToCanvas(Canvas canvas, RectF rectF) {
        Box box;
        if (rectF != null) {
            box = Box.fromLimits(rectF.left, rectF.top, rectF.right, rectF.bottom);
        } else {
            box = new Box(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight());
        }
        new SVGAndroidRenderer(canvas, box, this.renderDPI).renderDocument(this, null, null, true);
    }

    public RectF getDocumentViewBox() {
        Svg svg = this.rootElement;
        if (svg != null) {
            Box box = svg.viewBox;
            if (box == null) {
                return null;
            }
            return box.toRectF();
        }
        throw new IllegalArgumentException("SVG document is empty");
    }

    public void setDocumentPreserveAspectRatio(PreserveAspectRatio preserveAspectRatio) {
        Svg svg = this.rootElement;
        if (svg != null) {
            svg.preserveAspectRatio = preserveAspectRatio;
            return;
        }
        throw new IllegalArgumentException("SVG document is empty");
    }

    /* access modifiers changed from: protected */
    public Svg getRootElement() {
        return this.rootElement;
    }

    /* access modifiers changed from: protected */
    public void setRootElement(Svg svg) {
        this.rootElement = svg;
    }

    /* access modifiers changed from: protected */
    public SvgObject resolveIRI(String str) {
        if (str != null && str.length() > 1 && str.startsWith("#")) {
            return getElementById(str.substring(1));
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void addCSSRules(CSSParser.Ruleset ruleset) {
        this.cssRules.addAll(ruleset);
    }

    /* access modifiers changed from: protected */
    public List<CSSParser.Rule> getCSSRules() {
        return this.cssRules.getRules();
    }

    /* access modifiers changed from: protected */
    public boolean hasCSSRules() {
        return !this.cssRules.isEmpty();
    }

    /* access modifiers changed from: protected */
    public static class Box implements Cloneable {
        public float height;
        public float minX;
        public float minY;
        public float width;

        public Box(float f, float f2, float f3, float f4) {
            this.minX = f;
            this.minY = f2;
            this.width = f3;
            this.height = f4;
        }

        public static Box fromLimits(float f, float f2, float f3, float f4) {
            return new Box(f, f2, f3 - f, f4 - f2);
        }

        public RectF toRectF() {
            return new RectF(this.minX, this.minY, maxX(), maxY());
        }

        public float maxX() {
            return this.minX + this.width;
        }

        public float maxY() {
            return this.minY + this.height;
        }

        public void union(Box box) {
            float f = box.minX;
            if (f < this.minX) {
                this.minX = f;
            }
            float f2 = box.minY;
            if (f2 < this.minY) {
                this.minY = f2;
            }
            if (box.maxX() > maxX()) {
                this.width = box.maxX() - this.minX;
            }
            if (box.maxY() > maxY()) {
                this.height = box.maxY() - this.minY;
            }
        }

        public String toString() {
            return "[" + this.minX + " " + this.minY + " " + this.width + " " + this.height + "]";
        }
    }

    protected static class Style implements Cloneable {
        public CSSClipRect clip;
        public String clipPath;
        public FillRule clipRule;
        public Colour color;
        public TextDirection direction;
        public Boolean display;
        public SvgPaint fill;
        public Float fillOpacity;
        public FillRule fillRule;
        public List<String> fontFamily;
        public Length fontSize;
        public FontStyle fontStyle;
        public Integer fontWeight;
        public String markerEnd;
        public String markerMid;
        public String markerStart;
        public String mask;
        public Float opacity;
        public Boolean overflow;
        public SvgPaint solidColor;
        public Float solidOpacity;
        public long specifiedFlags = 0;
        public SvgPaint stopColor;
        public Float stopOpacity;
        public SvgPaint stroke;
        public Length[] strokeDashArray;
        public Length strokeDashOffset;
        public LineCaps strokeLineCap;
        public LineJoin strokeLineJoin;
        public Float strokeMiterLimit;
        public Float strokeOpacity;
        public Length strokeWidth;
        public TextAnchor textAnchor;
        public TextDecoration textDecoration;
        public VectorEffect vectorEffect;
        public SvgPaint viewportFill;
        public Float viewportFillOpacity;
        public Boolean visibility;

        public enum FillRule {
            NonZero,
            EvenOdd
        }

        public enum FontStyle {
            Normal,
            Italic,
            Oblique
        }

        public enum LineCaps {
            Butt,
            Round,
            Square
        }

        public enum LineJoin {
            Miter,
            Round,
            Bevel
        }

        public enum TextAnchor {
            Start,
            Middle,
            End
        }

        public enum TextDecoration {
            None,
            Underline,
            Overline,
            LineThrough,
            Blink
        }

        public enum TextDirection {
            LTR,
            RTL
        }

        public enum VectorEffect {
            None,
            NonScalingStroke
        }

        protected Style() {
        }

        public static Style getDefaultStyle() {
            Style style = new Style();
            style.specifiedFlags = -1;
            style.fill = Colour.BLACK;
            style.fillRule = FillRule.NonZero;
            Float valueOf = Float.valueOf(1.0f);
            style.fillOpacity = valueOf;
            style.stroke = null;
            style.strokeOpacity = valueOf;
            style.strokeWidth = new Length(1.0f);
            style.strokeLineCap = LineCaps.Butt;
            style.strokeLineJoin = LineJoin.Miter;
            style.strokeMiterLimit = Float.valueOf(4.0f);
            style.strokeDashArray = null;
            style.strokeDashOffset = new Length(0.0f);
            style.opacity = valueOf;
            style.color = Colour.BLACK;
            style.fontFamily = null;
            style.fontSize = new Length(12.0f, Unit.pt);
            style.fontWeight = Integer.valueOf((int) UseOpenWifiPreferenceController.REQUEST_CODE_OPEN_WIFI_AUTOMATICALLY);
            style.fontStyle = FontStyle.Normal;
            style.textDecoration = TextDecoration.None;
            style.direction = TextDirection.LTR;
            style.textAnchor = TextAnchor.Start;
            Boolean bool = Boolean.TRUE;
            style.overflow = bool;
            style.clip = null;
            style.markerStart = null;
            style.markerMid = null;
            style.markerEnd = null;
            style.display = bool;
            style.visibility = bool;
            style.stopColor = Colour.BLACK;
            style.stopOpacity = valueOf;
            style.clipPath = null;
            style.clipRule = FillRule.NonZero;
            style.mask = null;
            style.solidColor = null;
            style.solidOpacity = valueOf;
            style.viewportFill = null;
            style.viewportFillOpacity = valueOf;
            style.vectorEffect = VectorEffect.None;
            return style;
        }

        public void resetNonInheritingProperties(boolean z) {
            Boolean bool = Boolean.TRUE;
            this.display = bool;
            if (!z) {
                bool = Boolean.FALSE;
            }
            this.overflow = bool;
            this.clip = null;
            this.clipPath = null;
            this.opacity = Float.valueOf(1.0f);
            this.stopColor = Colour.BLACK;
            this.stopOpacity = Float.valueOf(1.0f);
            this.mask = null;
            this.solidColor = null;
            this.solidOpacity = Float.valueOf(1.0f);
            this.viewportFill = null;
            this.viewportFillOpacity = Float.valueOf(1.0f);
            this.vectorEffect = VectorEffect.None;
        }

        /* access modifiers changed from: protected */
        @Override // java.lang.Object
        public Object clone() {
            try {
                Style style = (Style) super.clone();
                if (this.strokeDashArray != null) {
                    style.strokeDashArray = (Length[]) this.strokeDashArray.clone();
                }
                return style;
            } catch (CloneNotSupportedException e) {
                throw new InternalError(e.toString());
            }
        }
    }

    protected static abstract class SvgPaint implements Cloneable {
        protected SvgPaint() {
        }
    }

    protected static class Colour extends SvgPaint {
        public static final Colour BLACK = new Colour(0);
        public int colour;

        public Colour(int i) {
            this.colour = i;
        }

        public String toString() {
            return String.format("#%06x", Integer.valueOf(this.colour));
        }
    }

    protected static class CurrentColor extends SvgPaint {
        private static CurrentColor instance = new CurrentColor();

        private CurrentColor() {
        }

        public static CurrentColor getInstance() {
            return instance;
        }
    }

    protected static class PaintReference extends SvgPaint {
        public SvgPaint fallback;
        public String href;

        public PaintReference(String str, SvgPaint svgPaint) {
            this.href = str;
            this.fallback = svgPaint;
        }

        public String toString() {
            return String.valueOf(this.href) + " " + this.fallback;
        }
    }

    /* access modifiers changed from: protected */
    public static class Length implements Cloneable {
        private static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$SVG$Unit;
        Unit unit;
        float value;

        /* JADX WARNING: Can't wrap try/catch for region: R(21:3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|24) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x0027 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:12:0x0030 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:14:0x0039 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:16:0x0043 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:18:0x004d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:20:0x0056 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x0015 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:8:0x001e */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$SVG$Unit() {
            /*
                int[] r0 = com.caverock.androidsvg.SVG.Length.$SWITCH_TABLE$com$caverock$androidsvg$SVG$Unit
                if (r0 == 0) goto L_0x0005
                return r0
            L_0x0005:
                com.caverock.androidsvg.SVG$Unit[] r0 = com.caverock.androidsvg.SVG.Unit.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.caverock.androidsvg.SVG$Unit r1 = com.caverock.androidsvg.SVG.Unit.cm     // Catch:{ NoSuchFieldError -> 0x0015 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0015 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0015 }
            L_0x0015:
                com.caverock.androidsvg.SVG$Unit r1 = com.caverock.androidsvg.SVG.Unit.em     // Catch:{ NoSuchFieldError -> 0x001e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001e }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001e }
            L_0x001e:
                com.caverock.androidsvg.SVG$Unit r1 = com.caverock.androidsvg.SVG.Unit.ex     // Catch:{ NoSuchFieldError -> 0x0027 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0027 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0027 }
            L_0x0027:
                com.caverock.androidsvg.SVG$Unit r1 = com.caverock.androidsvg.SVG.Unit.in     // Catch:{ NoSuchFieldError -> 0x0030 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0030 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0030 }
            L_0x0030:
                com.caverock.androidsvg.SVG$Unit r1 = com.caverock.androidsvg.SVG.Unit.mm     // Catch:{ NoSuchFieldError -> 0x0039 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0039 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0039 }
            L_0x0039:
                com.caverock.androidsvg.SVG$Unit r1 = com.caverock.androidsvg.SVG.Unit.pc     // Catch:{ NoSuchFieldError -> 0x0043 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0043 }
                r2 = 8
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0043 }
            L_0x0043:
                com.caverock.androidsvg.SVG$Unit r1 = com.caverock.androidsvg.SVG.Unit.percent     // Catch:{ NoSuchFieldError -> 0x004d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004d }
                r2 = 9
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004d }
            L_0x004d:
                com.caverock.androidsvg.SVG$Unit r1 = com.caverock.androidsvg.SVG.Unit.pt     // Catch:{ NoSuchFieldError -> 0x0056 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0056 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0056 }
            L_0x0056:
                com.caverock.androidsvg.SVG$Unit r1 = com.caverock.androidsvg.SVG.Unit.px     // Catch:{ NoSuchFieldError -> 0x005f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x005f }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x005f }
            L_0x005f:
                com.caverock.androidsvg.SVG.Length.$SWITCH_TABLE$com$caverock$androidsvg$SVG$Unit = r0
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVG.Length.$SWITCH_TABLE$com$caverock$androidsvg$SVG$Unit():int[]");
        }

        public Length(float f, Unit unit2) {
            this.value = 0.0f;
            this.unit = Unit.px;
            this.value = f;
            this.unit = unit2;
        }

        public Length(float f) {
            this.value = 0.0f;
            Unit unit2 = Unit.px;
            this.unit = unit2;
            this.value = f;
            this.unit = unit2;
        }

        public float floatValue() {
            return this.value;
        }

        public float floatValueX(SVGAndroidRenderer sVGAndroidRenderer) {
            switch ($SWITCH_TABLE$com$caverock$androidsvg$SVG$Unit()[this.unit.ordinal()]) {
                case 1:
                    return this.value;
                case 2:
                    return this.value * sVGAndroidRenderer.getCurrentFontSize();
                case 3:
                    return this.value * sVGAndroidRenderer.getCurrentFontXHeight();
                case 4:
                    return this.value * sVGAndroidRenderer.getDPI();
                case 5:
                    return (this.value * sVGAndroidRenderer.getDPI()) / 2.54f;
                case 6:
                    return (this.value * sVGAndroidRenderer.getDPI()) / 25.4f;
                case 7:
                    return (this.value * sVGAndroidRenderer.getDPI()) / 72.0f;
                case 8:
                    return (this.value * sVGAndroidRenderer.getDPI()) / 6.0f;
                case 9:
                    Box currentViewPortInUserUnits = sVGAndroidRenderer.getCurrentViewPortInUserUnits();
                    if (currentViewPortInUserUnits == null) {
                        return this.value;
                    }
                    return (this.value * currentViewPortInUserUnits.width) / 100.0f;
                default:
                    return this.value;
            }
        }

        public float floatValueY(SVGAndroidRenderer sVGAndroidRenderer) {
            if (this.unit != Unit.percent) {
                return floatValueX(sVGAndroidRenderer);
            }
            Box currentViewPortInUserUnits = sVGAndroidRenderer.getCurrentViewPortInUserUnits();
            if (currentViewPortInUserUnits == null) {
                return this.value;
            }
            return (this.value * currentViewPortInUserUnits.height) / 100.0f;
        }

        public float floatValue(SVGAndroidRenderer sVGAndroidRenderer) {
            float sqrt;
            if (this.unit != Unit.percent) {
                return floatValueX(sVGAndroidRenderer);
            }
            Box currentViewPortInUserUnits = sVGAndroidRenderer.getCurrentViewPortInUserUnits();
            if (currentViewPortInUserUnits == null) {
                return this.value;
            }
            float f = currentViewPortInUserUnits.width;
            float f2 = currentViewPortInUserUnits.height;
            if (f == f2) {
                sqrt = this.value * f;
            } else {
                sqrt = this.value * ((float) (Math.sqrt((double) ((f * f) + (f2 * f2))) / 1.414213562373095d));
            }
            return sqrt / 100.0f;
        }

        public float floatValue(SVGAndroidRenderer sVGAndroidRenderer, float f) {
            if (this.unit == Unit.percent) {
                return (this.value * f) / 100.0f;
            }
            return floatValueX(sVGAndroidRenderer);
        }

        public float floatValue(float f) {
            int i = $SWITCH_TABLE$com$caverock$androidsvg$SVG$Unit()[this.unit.ordinal()];
            if (i == 1) {
                return this.value;
            }
            switch (i) {
                case 4:
                    return this.value * f;
                case 5:
                    return (this.value * f) / 2.54f;
                case 6:
                    return (this.value * f) / 25.4f;
                case 7:
                    return (this.value * f) / 72.0f;
                case 8:
                    return (this.value * f) / 6.0f;
                default:
                    return this.value;
            }
        }

        public boolean isZero() {
            return this.value == 0.0f;
        }

        public boolean isNegative() {
            return this.value < 0.0f;
        }

        public String toString() {
            return String.valueOf(String.valueOf(this.value)) + this.unit;
        }
    }

    protected static class CSSClipRect {
        public Length bottom;
        public Length left;
        public Length right;
        public Length top;

        public CSSClipRect(Length length, Length length2, Length length3, Length length4) {
            this.top = length;
            this.right = length2;
            this.bottom = length3;
            this.left = length4;
        }
    }

    /* access modifiers changed from: protected */
    public static class SvgObject {
        public SVG document;
        public SvgContainer parent;

        protected SvgObject() {
        }

        public String toString() {
            return getClass().getSimpleName();
        }
    }

    /* access modifiers changed from: protected */
    public static class SvgElementBase extends SvgObject {
        public Style baseStyle = null;
        public List<String> classNames = null;
        public String id = null;
        public Boolean spacePreserve = null;
        public Style style = null;

        protected SvgElementBase() {
        }
    }

    protected static class SvgElement extends SvgElementBase {
        public Box boundingBox = null;

        protected SvgElement() {
        }
    }

    protected static class SvgConditionalElement extends SvgElement implements SvgConditional {
        public String requiredExtensions = null;
        public Set<String> requiredFeatures = null;
        public Set<String> requiredFonts = null;
        public Set<String> requiredFormats = null;
        public Set<String> systemLanguage = null;

        protected SvgConditionalElement() {
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public void setRequiredFeatures(Set<String> set) {
            this.requiredFeatures = set;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public Set<String> getRequiredFeatures() {
            return this.requiredFeatures;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public void setRequiredExtensions(String str) {
            this.requiredExtensions = str;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public String getRequiredExtensions() {
            return this.requiredExtensions;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public void setSystemLanguage(Set<String> set) {
            this.systemLanguage = set;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public Set<String> getSystemLanguage() {
            return this.systemLanguage;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public void setRequiredFormats(Set<String> set) {
            this.requiredFormats = set;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public Set<String> getRequiredFormats() {
            return this.requiredFormats;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public void setRequiredFonts(Set<String> set) {
            this.requiredFonts = set;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public Set<String> getRequiredFonts() {
            return this.requiredFonts;
        }
    }

    protected static class SvgConditionalContainer extends SvgElement implements SvgContainer, SvgConditional {
        public List<SvgObject> children = new ArrayList();
        public String requiredExtensions = null;
        public Set<String> requiredFeatures = null;
        public Set<String> requiredFonts = null;
        public Set<String> requiredFormats = null;

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public Set<String> getSystemLanguage() {
            return null;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public void setSystemLanguage(Set<String> set) {
        }

        protected SvgConditionalContainer() {
        }

        @Override // com.caverock.androidsvg.SVG.SvgContainer
        public List<SvgObject> getChildren() {
            return this.children;
        }

        @Override // com.caverock.androidsvg.SVG.SvgContainer
        public void addChild(SvgObject svgObject) throws SAXException {
            this.children.add(svgObject);
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public void setRequiredFeatures(Set<String> set) {
            this.requiredFeatures = set;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public Set<String> getRequiredFeatures() {
            return this.requiredFeatures;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public void setRequiredExtensions(String str) {
            this.requiredExtensions = str;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public String getRequiredExtensions() {
            return this.requiredExtensions;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public void setRequiredFormats(Set<String> set) {
            this.requiredFormats = set;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public Set<String> getRequiredFormats() {
            return this.requiredFormats;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public void setRequiredFonts(Set<String> set) {
            this.requiredFonts = set;
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditional
        public Set<String> getRequiredFonts() {
            return this.requiredFonts;
        }
    }

    /* access modifiers changed from: protected */
    public static class SvgPreserveAspectRatioContainer extends SvgConditionalContainer {
        public PreserveAspectRatio preserveAspectRatio = null;

        protected SvgPreserveAspectRatioContainer() {
        }
    }

    /* access modifiers changed from: protected */
    public static class SvgViewBoxContainer extends SvgPreserveAspectRatioContainer {
        public Box viewBox;

        protected SvgViewBoxContainer() {
        }
    }

    /* access modifiers changed from: protected */
    public static class Svg extends SvgViewBoxContainer {
        public Length height;
        public String version;
        public Length width;
        public Length x;
        public Length y;

        protected Svg() {
        }
    }

    protected static class Group extends SvgConditionalContainer implements HasTransform {
        public Matrix transform;

        protected Group() {
        }

        @Override // com.caverock.androidsvg.SVG.HasTransform
        public void setTransform(Matrix matrix) {
            this.transform = matrix;
        }
    }

    protected static class Defs extends Group implements NotDirectlyRendered {
        protected Defs() {
        }
    }

    protected static abstract class GraphicsElement extends SvgConditionalElement implements HasTransform {
        public Matrix transform;

        protected GraphicsElement() {
        }

        @Override // com.caverock.androidsvg.SVG.HasTransform
        public void setTransform(Matrix matrix) {
            this.transform = matrix;
        }
    }

    protected static class Use extends Group {
        public Length height;
        public String href;
        public Length width;
        public Length x;
        public Length y;

        protected Use() {
        }
    }

    protected static class Path extends GraphicsElement {
        public PathDefinition d;
        public Float pathLength;

        protected Path() {
        }
    }

    protected static class Rect extends GraphicsElement {
        public Length height;
        public Length rx;
        public Length ry;
        public Length width;
        public Length x;
        public Length y;

        protected Rect() {
        }
    }

    protected static class Circle extends GraphicsElement {
        public Length cx;
        public Length cy;
        public Length r;

        protected Circle() {
        }
    }

    protected static class Ellipse extends GraphicsElement {
        public Length cx;
        public Length cy;
        public Length rx;
        public Length ry;

        protected Ellipse() {
        }
    }

    protected static class Line extends GraphicsElement {
        public Length x1;
        public Length x2;
        public Length y1;
        public Length y2;

        protected Line() {
        }
    }

    protected static class PolyLine extends GraphicsElement {
        public float[] points;

        protected PolyLine() {
        }
    }

    protected static class Polygon extends PolyLine {
        protected Polygon() {
        }
    }

    protected static class TextContainer extends SvgConditionalContainer {
        protected TextContainer() {
        }

        @Override // com.caverock.androidsvg.SVG.SvgConditionalContainer, com.caverock.androidsvg.SVG.SvgContainer
        public void addChild(SvgObject svgObject) throws SAXException {
            if (svgObject instanceof TextChild) {
                this.children.add(svgObject);
                return;
            }
            throw new SAXException("Text content elements cannot contain " + svgObject + " elements.");
        }
    }

    protected static class TextPositionedContainer extends TextContainer {
        public List<Length> dx;
        public List<Length> dy;
        public List<Length> x;
        public List<Length> y;

        protected TextPositionedContainer() {
        }
    }

    protected static class Text extends TextPositionedContainer implements TextRoot, HasTransform {
        public Matrix transform;

        protected Text() {
        }

        @Override // com.caverock.androidsvg.SVG.HasTransform
        public void setTransform(Matrix matrix) {
            this.transform = matrix;
        }
    }

    protected static class TSpan extends TextPositionedContainer implements TextChild {
        private TextRoot textRoot;

        protected TSpan() {
        }

        public void setTextRoot(TextRoot textRoot2) {
            this.textRoot = textRoot2;
        }

        @Override // com.caverock.androidsvg.SVG.TextChild
        public TextRoot getTextRoot() {
            return this.textRoot;
        }
    }

    protected static class TextSequence extends SvgObject implements TextChild {
        public String text;
        private TextRoot textRoot;

        public TextSequence(String str) {
            this.text = str;
        }

        @Override // com.caverock.androidsvg.SVG.SvgObject
        public String toString() {
            return String.valueOf(TextSequence.class.getSimpleName()) + " '" + this.text + "'";
        }

        @Override // com.caverock.androidsvg.SVG.TextChild
        public TextRoot getTextRoot() {
            return this.textRoot;
        }
    }

    protected static class TRef extends TextContainer implements TextChild {
        public String href;
        private TextRoot textRoot;

        protected TRef() {
        }

        public void setTextRoot(TextRoot textRoot2) {
            this.textRoot = textRoot2;
        }

        @Override // com.caverock.androidsvg.SVG.TextChild
        public TextRoot getTextRoot() {
            return this.textRoot;
        }
    }

    protected static class TextPath extends TextContainer implements TextChild {
        public String href;
        public Length startOffset;
        private TextRoot textRoot;

        protected TextPath() {
        }

        public void setTextRoot(TextRoot textRoot2) {
            this.textRoot = textRoot2;
        }

        @Override // com.caverock.androidsvg.SVG.TextChild
        public TextRoot getTextRoot() {
            return this.textRoot;
        }
    }

    protected static class Switch extends Group {
        protected Switch() {
        }
    }

    protected static class Symbol extends SvgViewBoxContainer implements NotDirectlyRendered {
        protected Symbol() {
        }
    }

    protected static class Marker extends SvgViewBoxContainer implements NotDirectlyRendered {
        public Length markerHeight;
        public boolean markerUnitsAreUser;
        public Length markerWidth;
        public Float orient;
        public Length refX;
        public Length refY;

        protected Marker() {
        }
    }

    protected static class GradientElement extends SvgElementBase implements SvgContainer {
        public List<SvgObject> children = new ArrayList();
        public Matrix gradientTransform;
        public Boolean gradientUnitsAreUser;
        public String href;
        public GradientSpread spreadMethod;

        protected GradientElement() {
        }

        @Override // com.caverock.androidsvg.SVG.SvgContainer
        public List<SvgObject> getChildren() {
            return this.children;
        }

        @Override // com.caverock.androidsvg.SVG.SvgContainer
        public void addChild(SvgObject svgObject) throws SAXException {
            if (svgObject instanceof Stop) {
                this.children.add(svgObject);
                return;
            }
            throw new SAXException("Gradient elements cannot contain " + svgObject + " elements.");
        }
    }

    protected static class Stop extends SvgElementBase implements SvgContainer {
        public Float offset;

        @Override // com.caverock.androidsvg.SVG.SvgContainer
        public void addChild(SvgObject svgObject) throws SAXException {
        }

        protected Stop() {
        }

        @Override // com.caverock.androidsvg.SVG.SvgContainer
        public List<SvgObject> getChildren() {
            return SVG.EMPTY_CHILD_LIST;
        }
    }

    protected static class SvgLinearGradient extends GradientElement {
        public Length x1;
        public Length x2;
        public Length y1;
        public Length y2;

        protected SvgLinearGradient() {
        }
    }

    protected static class SvgRadialGradient extends GradientElement {
        public Length cx;
        public Length cy;
        public Length fx;
        public Length fy;
        public Length r;

        protected SvgRadialGradient() {
        }
    }

    protected static class ClipPath extends Group implements NotDirectlyRendered {
        public Boolean clipPathUnitsAreUser;

        protected ClipPath() {
        }
    }

    protected static class Pattern extends SvgViewBoxContainer implements NotDirectlyRendered {
        public Length height;
        public String href;
        public Boolean patternContentUnitsAreUser;
        public Matrix patternTransform;
        public Boolean patternUnitsAreUser;
        public Length width;
        public Length x;
        public Length y;

        protected Pattern() {
        }
    }

    protected static class Image extends SvgPreserveAspectRatioContainer implements HasTransform {
        public Length height;
        public String href;
        public Matrix transform;
        public Length width;
        public Length x;
        public Length y;

        protected Image() {
        }

        @Override // com.caverock.androidsvg.SVG.HasTransform
        public void setTransform(Matrix matrix) {
            this.transform = matrix;
        }
    }

    protected static class View extends SvgViewBoxContainer implements NotDirectlyRendered {
        protected View() {
        }
    }

    protected static class Mask extends SvgConditionalContainer implements NotDirectlyRendered {
        public Length height;
        public Boolean maskContentUnitsAreUser;
        public Boolean maskUnitsAreUser;
        public Length width;
        public Length x;
        public Length y;

        protected Mask() {
        }
    }

    protected static class SolidColor extends SvgElementBase implements SvgContainer {
        @Override // com.caverock.androidsvg.SVG.SvgContainer
        public void addChild(SvgObject svgObject) throws SAXException {
        }

        protected SolidColor() {
        }

        @Override // com.caverock.androidsvg.SVG.SvgContainer
        public List<SvgObject> getChildren() {
            return SVG.EMPTY_CHILD_LIST;
        }
    }

    /* access modifiers changed from: protected */
    public SVGExternalFileResolver getFileResolver() {
        return this.fileResolver;
    }

    protected static class PathDefinition implements PathInterface {
        private List<Byte> commands;
        private List<Float> coords;

        public PathDefinition() {
            this.commands = null;
            this.coords = null;
            this.commands = new ArrayList();
            this.coords = new ArrayList();
        }

        public boolean isEmpty() {
            return this.commands.isEmpty();
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void moveTo(float f, float f2) {
            this.commands.add((byte) 0);
            this.coords.add(Float.valueOf(f));
            this.coords.add(Float.valueOf(f2));
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void lineTo(float f, float f2) {
            this.commands.add((byte) 1);
            this.coords.add(Float.valueOf(f));
            this.coords.add(Float.valueOf(f2));
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void cubicTo(float f, float f2, float f3, float f4, float f5, float f6) {
            this.commands.add((byte) 2);
            this.coords.add(Float.valueOf(f));
            this.coords.add(Float.valueOf(f2));
            this.coords.add(Float.valueOf(f3));
            this.coords.add(Float.valueOf(f4));
            this.coords.add(Float.valueOf(f5));
            this.coords.add(Float.valueOf(f6));
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void quadTo(float f, float f2, float f3, float f4) {
            this.commands.add((byte) 3);
            this.coords.add(Float.valueOf(f));
            this.coords.add(Float.valueOf(f2));
            this.coords.add(Float.valueOf(f3));
            this.coords.add(Float.valueOf(f4));
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void arcTo(float f, float f2, float f3, boolean z, boolean z2, float f4, float f5) {
            this.commands.add(Byte.valueOf((byte) ((z ? 2 : 0) | 4 | (z2 ? 1 : 0))));
            this.coords.add(Float.valueOf(f));
            this.coords.add(Float.valueOf(f2));
            this.coords.add(Float.valueOf(f3));
            this.coords.add(Float.valueOf(f4));
            this.coords.add(Float.valueOf(f5));
        }

        @Override // com.caverock.androidsvg.SVG.PathInterface
        public void close() {
            this.commands.add((byte) 8);
        }

        public void enumeratePath(PathInterface pathInterface) {
            Iterator<Float> it = this.coords.iterator();
            for (Byte b : this.commands) {
                byte byteValue = b.byteValue();
                if (byteValue == 0) {
                    pathInterface.moveTo(it.next().floatValue(), it.next().floatValue());
                } else if (byteValue == 1) {
                    pathInterface.lineTo(it.next().floatValue(), it.next().floatValue());
                } else if (byteValue == 2) {
                    pathInterface.cubicTo(it.next().floatValue(), it.next().floatValue(), it.next().floatValue(), it.next().floatValue(), it.next().floatValue(), it.next().floatValue());
                } else if (byteValue == 3) {
                    pathInterface.quadTo(it.next().floatValue(), it.next().floatValue(), it.next().floatValue(), it.next().floatValue());
                } else if (byteValue != 8) {
                    pathInterface.arcTo(it.next().floatValue(), it.next().floatValue(), it.next().floatValue(), (byteValue & 2) != 0, (byteValue & 1) != 0, it.next().floatValue(), it.next().floatValue());
                } else {
                    pathInterface.close();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public SvgObject getElementById(String str) {
        if (str.equals(this.rootElement.id)) {
            return this.rootElement;
        }
        return getElementById(this.rootElement, str);
    }

    private SvgElementBase getElementById(SvgContainer svgContainer, String str) {
        SvgElementBase elementById;
        SvgElementBase svgElementBase = (SvgElementBase) svgContainer;
        if (str.equals(svgElementBase.id)) {
            return svgElementBase;
        }
        for (SvgObject svgObject : svgContainer.getChildren()) {
            if (svgObject instanceof SvgElementBase) {
                SvgElementBase svgElementBase2 = (SvgElementBase) svgObject;
                if (str.equals(svgElementBase2.id)) {
                    return svgElementBase2;
                }
                if ((svgObject instanceof SvgContainer) && (elementById = getElementById((SvgContainer) svgObject, str)) != null) {
                    return elementById;
                }
            }
        }
        return null;
    }
}
