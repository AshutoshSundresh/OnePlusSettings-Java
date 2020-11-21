package com.caverock.androidsvg;

import android.graphics.Matrix;
import android.util.Log;
import androidx.constraintlayout.widget.R$styleable;
import com.android.settings.wifi.UseOpenWifiPreferenceController;
import com.caverock.androidsvg.CSSParser;
import com.caverock.androidsvg.PreserveAspectRatio;
import com.caverock.androidsvg.SVG;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;

public class SVGParser extends DefaultHandler2 {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr;
    private static HashMap<String, PreserveAspectRatio.Alignment> aspectRatioKeywords = new HashMap<>();
    private static HashMap<String, Integer> colourKeywords = new HashMap<>();
    private static HashMap<String, SVG.Length> fontSizeKeywords = new HashMap<>(9);
    private static HashMap<String, SVG.Style.FontStyle> fontStyleKeywords = new HashMap<>(3);
    private static HashMap<String, Integer> fontWeightKeywords = new HashMap<>(13);
    protected static HashSet<String> supportedFeatures = new HashSet<>();
    private SVG.SvgContainer currentElement = null;
    private int ignoreDepth;
    private boolean ignoring = false;
    private boolean inMetadataElement = false;
    private boolean inStyleElement = false;
    private StringBuilder metadataElementContents = null;
    private String metadataTag = null;
    private StringBuilder styleElementContents = null;
    private SVG svgDocument = null;

    private void debug(String str, Object... objArr) {
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(186:3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|71|72|73|74|75|76|77|78|79|80|81|82|83|84|85|86|87|88|89|90|91|92|93|94|95|96|97|98|99|100|101|102|103|104|105|106|107|108|109|110|111|112|113|114|115|116|117|118|119|120|121|122|123|124|125|126|127|128|129|130|131|132|133|134|135|136|137|138|139|140|141|142|143|144|145|146|147|148|149|150|151|152|153|154|155|156|157|158|159|160|161|162|163|164|165|166|167|168|169|170|171|172|173|174|175|176|177|178|179|180|181|182|183|184|185|(2:186|187)|188|190) */
    /* JADX WARNING: Can't wrap try/catch for region: R(187:3|4|5|6|7|8|9|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59|60|61|62|63|64|65|66|67|68|69|70|71|72|73|74|75|76|77|78|79|80|81|82|83|84|85|86|87|88|89|90|91|92|93|94|95|96|97|98|99|100|101|102|103|104|105|106|107|108|109|110|111|112|113|114|115|116|117|118|119|120|121|122|123|124|125|126|127|128|129|130|131|132|133|134|135|136|137|138|139|140|141|142|143|144|145|146|147|148|149|150|151|152|153|154|155|156|157|158|159|160|161|162|163|164|165|166|167|168|169|170|171|172|173|174|175|176|177|178|179|180|181|182|183|184|185|186|187|188|190) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:100:0x01e5 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:102:0x01ef */
    /* JADX WARNING: Missing exception handler attribute for start block: B:104:0x01f9 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:106:0x0203 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:108:0x020d */
    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x0028 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:110:0x0217 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:112:0x0221 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:114:0x022b */
    /* JADX WARNING: Missing exception handler attribute for start block: B:116:0x0235 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:118:0x023f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:120:0x0249 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:122:0x0253 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:124:0x025d */
    /* JADX WARNING: Missing exception handler attribute for start block: B:126:0x0267 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:128:0x0271 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:12:0x0031 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:130:0x027b */
    /* JADX WARNING: Missing exception handler attribute for start block: B:132:0x0285 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:134:0x028f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:136:0x0299 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:138:0x02a3 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:140:0x02ad */
    /* JADX WARNING: Missing exception handler attribute for start block: B:142:0x02b7 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:144:0x02c1 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:146:0x02cb */
    /* JADX WARNING: Missing exception handler attribute for start block: B:148:0x02d5 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:14:0x003a */
    /* JADX WARNING: Missing exception handler attribute for start block: B:150:0x02df */
    /* JADX WARNING: Missing exception handler attribute for start block: B:152:0x02e9 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:154:0x02f3 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:156:0x02fd */
    /* JADX WARNING: Missing exception handler attribute for start block: B:158:0x0307 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:160:0x0311 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:162:0x031b */
    /* JADX WARNING: Missing exception handler attribute for start block: B:164:0x0325 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:166:0x032f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:168:0x0339 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:16:0x0043 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:170:0x0343 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:172:0x034d */
    /* JADX WARNING: Missing exception handler attribute for start block: B:174:0x0357 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:176:0x0361 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:178:0x036b */
    /* JADX WARNING: Missing exception handler attribute for start block: B:180:0x0375 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:182:0x037f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:184:0x0389 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:186:0x0393 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:18:0x004c */
    /* JADX WARNING: Missing exception handler attribute for start block: B:20:0x0055 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:22:0x005f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:24:0x0069 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:26:0x0073 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:28:0x007d */
    /* JADX WARNING: Missing exception handler attribute for start block: B:30:0x0087 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:32:0x0091 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:34:0x009b */
    /* JADX WARNING: Missing exception handler attribute for start block: B:36:0x00a5 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:38:0x00af */
    /* JADX WARNING: Missing exception handler attribute for start block: B:40:0x00b9 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:42:0x00c3 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:44:0x00cd */
    /* JADX WARNING: Missing exception handler attribute for start block: B:46:0x00d7 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:48:0x00e1 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:50:0x00eb */
    /* JADX WARNING: Missing exception handler attribute for start block: B:52:0x00f5 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:54:0x00ff */
    /* JADX WARNING: Missing exception handler attribute for start block: B:56:0x0109 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:58:0x0113 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:60:0x011d */
    /* JADX WARNING: Missing exception handler attribute for start block: B:62:0x0127 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:64:0x0131 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:66:0x013b */
    /* JADX WARNING: Missing exception handler attribute for start block: B:68:0x0145 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x0015 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:70:0x014f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:72:0x0159 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:74:0x0163 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:76:0x016d */
    /* JADX WARNING: Missing exception handler attribute for start block: B:78:0x0177 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:80:0x0181 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:82:0x018b */
    /* JADX WARNING: Missing exception handler attribute for start block: B:84:0x0195 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:86:0x019f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:88:0x01a9 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:8:0x001f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:90:0x01b3 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:92:0x01bd */
    /* JADX WARNING: Missing exception handler attribute for start block: B:94:0x01c7 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:96:0x01d1 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:98:0x01db */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr() {
        /*
        // Method dump skipped, instructions count: 928
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGParser.$SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr():int[]");
    }

    /* access modifiers changed from: private */
    public enum SVGAttr {
        CLASS,
        clip,
        clip_path,
        clipPathUnits,
        clip_rule,
        color,
        cx,
        cy,
        direction,
        dx,
        dy,
        fx,
        fy,
        d,
        display,
        fill,
        fill_rule,
        fill_opacity,
        font,
        font_family,
        font_size,
        font_weight,
        font_style,
        gradientTransform,
        gradientUnits,
        height,
        href,
        id,
        marker,
        marker_start,
        marker_mid,
        marker_end,
        markerHeight,
        markerUnits,
        markerWidth,
        mask,
        maskContentUnits,
        maskUnits,
        media,
        offset,
        opacity,
        orient,
        overflow,
        pathLength,
        patternContentUnits,
        patternTransform,
        patternUnits,
        points,
        preserveAspectRatio,
        r,
        refX,
        refY,
        requiredFeatures,
        requiredExtensions,
        requiredFormats,
        requiredFonts,
        rx,
        ry,
        solid_color,
        solid_opacity,
        spreadMethod,
        startOffset,
        stop_color,
        stop_opacity,
        stroke,
        stroke_dasharray,
        stroke_dashoffset,
        stroke_linecap,
        stroke_linejoin,
        stroke_miterlimit,
        stroke_opacity,
        stroke_width,
        style,
        systemLanguage,
        text_anchor,
        text_decoration,
        transform,
        type,
        vector_effect,
        version,
        viewBox,
        width,
        x,
        y,
        x1,
        y1,
        x2,
        y2,
        viewport_fill,
        viewport_fill_opacity,
        visibility,
        UNSUPPORTED;

        public static SVGAttr fromString(String str) {
            SVGAttr sVGAttr = UNSUPPORTED;
            if (str.equals("class")) {
                return CLASS;
            }
            if (str.indexOf(95) != -1) {
                return sVGAttr;
            }
            try {
                return valueOf(str.replace('-', '_'));
            } catch (IllegalArgumentException unused) {
                return sVGAttr;
            }
        }
    }

    static {
        colourKeywords.put("aliceblue", 15792383);
        colourKeywords.put("antiquewhite", 16444375);
        colourKeywords.put("aqua", 65535);
        colourKeywords.put("aquamarine", 8388564);
        colourKeywords.put("azure", 15794175);
        colourKeywords.put("beige", 16119260);
        colourKeywords.put("bisque", 16770244);
        colourKeywords.put("black", 0);
        colourKeywords.put("blanchedalmond", 16772045);
        colourKeywords.put("blue", 255);
        colourKeywords.put("blueviolet", 9055202);
        colourKeywords.put("brown", 10824234);
        colourKeywords.put("burlywood", 14596231);
        colourKeywords.put("cadetblue", 6266528);
        colourKeywords.put("chartreuse", 8388352);
        colourKeywords.put("chocolate", 13789470);
        colourKeywords.put("coral", 16744272);
        colourKeywords.put("cornflowerblue", 6591981);
        colourKeywords.put("cornsilk", 16775388);
        colourKeywords.put("crimson", 14423100);
        colourKeywords.put("cyan", 65535);
        colourKeywords.put("darkblue", 139);
        colourKeywords.put("darkcyan", 35723);
        colourKeywords.put("darkgoldenrod", 12092939);
        colourKeywords.put("darkgray", 11119017);
        colourKeywords.put("darkgreen", 25600);
        colourKeywords.put("darkgrey", 11119017);
        colourKeywords.put("darkkhaki", 12433259);
        colourKeywords.put("darkmagenta", 9109643);
        colourKeywords.put("darkolivegreen", 5597999);
        colourKeywords.put("darkorange", 16747520);
        colourKeywords.put("darkorchid", 10040012);
        colourKeywords.put("darkred", 9109504);
        colourKeywords.put("darksalmon", 15308410);
        colourKeywords.put("darkseagreen", 9419919);
        colourKeywords.put("darkslateblue", 4734347);
        colourKeywords.put("darkslategray", 3100495);
        colourKeywords.put("darkslategrey", 3100495);
        colourKeywords.put("darkturquoise", 52945);
        colourKeywords.put("darkviolet", 9699539);
        colourKeywords.put("deeppink", 16716947);
        colourKeywords.put("deepskyblue", 49151);
        colourKeywords.put("dimgray", 6908265);
        colourKeywords.put("dimgrey", 6908265);
        colourKeywords.put("dodgerblue", 2003199);
        colourKeywords.put("firebrick", 11674146);
        colourKeywords.put("floralwhite", 16775920);
        colourKeywords.put("forestgreen", 2263842);
        colourKeywords.put("fuchsia", 16711935);
        colourKeywords.put("gainsboro", 14474460);
        colourKeywords.put("ghostwhite", 16316671);
        colourKeywords.put("gold", 16766720);
        colourKeywords.put("goldenrod", 14329120);
        colourKeywords.put("gray", 8421504);
        colourKeywords.put("green", 32768);
        colourKeywords.put("greenyellow", 11403055);
        colourKeywords.put("grey", 8421504);
        colourKeywords.put("honeydew", 15794160);
        colourKeywords.put("hotpink", 16738740);
        colourKeywords.put("indianred", 13458524);
        colourKeywords.put("indigo", 4915330);
        colourKeywords.put("ivory", 16777200);
        colourKeywords.put("khaki", 15787660);
        colourKeywords.put("lavender", 15132410);
        colourKeywords.put("lavenderblush", 16773365);
        colourKeywords.put("lawngreen", 8190976);
        colourKeywords.put("lemonchiffon", 16775885);
        colourKeywords.put("lightblue", 11393254);
        colourKeywords.put("lightcoral", 15761536);
        colourKeywords.put("lightcyan", 14745599);
        colourKeywords.put("lightgoldenrodyellow", 16448210);
        colourKeywords.put("lightgray", 13882323);
        colourKeywords.put("lightgreen", 9498256);
        colourKeywords.put("lightgrey", 13882323);
        colourKeywords.put("lightpink", 16758465);
        colourKeywords.put("lightsalmon", 16752762);
        colourKeywords.put("lightseagreen", 2142890);
        colourKeywords.put("lightskyblue", 8900346);
        colourKeywords.put("lightslategray", 7833753);
        colourKeywords.put("lightslategrey", 7833753);
        colourKeywords.put("lightsteelblue", 11584734);
        colourKeywords.put("lightyellow", 16777184);
        colourKeywords.put("lime", 65280);
        colourKeywords.put("limegreen", 3329330);
        colourKeywords.put("linen", 16445670);
        colourKeywords.put("magenta", 16711935);
        colourKeywords.put("maroon", 8388608);
        colourKeywords.put("mediumaquamarine", 6737322);
        colourKeywords.put("mediumblue", 205);
        colourKeywords.put("mediumorchid", 12211667);
        colourKeywords.put("mediumpurple", 9662683);
        colourKeywords.put("mediumseagreen", 3978097);
        colourKeywords.put("mediumslateblue", 8087790);
        colourKeywords.put("mediumspringgreen", 64154);
        colourKeywords.put("mediumturquoise", 4772300);
        colourKeywords.put("mediumvioletred", 13047173);
        colourKeywords.put("midnightblue", 1644912);
        colourKeywords.put("mintcream", 16121850);
        colourKeywords.put("mistyrose", 16770273);
        colourKeywords.put("moccasin", 16770229);
        colourKeywords.put("navajowhite", 16768685);
        colourKeywords.put("navy", 128);
        colourKeywords.put("oldlace", 16643558);
        colourKeywords.put("olive", 8421376);
        colourKeywords.put("olivedrab", 7048739);
        colourKeywords.put("orange", 16753920);
        colourKeywords.put("orangered", 16729344);
        colourKeywords.put("orchid", 14315734);
        colourKeywords.put("palegoldenrod", 15657130);
        colourKeywords.put("palegreen", 10025880);
        colourKeywords.put("paleturquoise", 11529966);
        colourKeywords.put("palevioletred", 14381203);
        colourKeywords.put("papayawhip", 16773077);
        colourKeywords.put("peachpuff", 16767673);
        colourKeywords.put("peru", 13468991);
        colourKeywords.put("pink", 16761035);
        colourKeywords.put("plum", 14524637);
        colourKeywords.put("powderblue", 11591910);
        colourKeywords.put("purple", 8388736);
        colourKeywords.put("red", 16711680);
        colourKeywords.put("rosybrown", 12357519);
        colourKeywords.put("royalblue", 4286945);
        colourKeywords.put("saddlebrown", 9127187);
        colourKeywords.put("salmon", 16416882);
        colourKeywords.put("sandybrown", 16032864);
        colourKeywords.put("seagreen", 3050327);
        colourKeywords.put("seashell", 16774638);
        colourKeywords.put("sienna", 10506797);
        colourKeywords.put("silver", 12632256);
        colourKeywords.put("skyblue", 8900331);
        colourKeywords.put("slateblue", 6970061);
        colourKeywords.put("slategray", 7372944);
        colourKeywords.put("slategrey", 7372944);
        colourKeywords.put("snow", 16775930);
        colourKeywords.put("springgreen", 65407);
        colourKeywords.put("steelblue", 4620980);
        colourKeywords.put("tan", 13808780);
        colourKeywords.put("teal", 32896);
        colourKeywords.put("thistle", 14204888);
        colourKeywords.put("tomato", 16737095);
        colourKeywords.put("turquoise", 4251856);
        colourKeywords.put("violet", 15631086);
        colourKeywords.put("wheat", 16113331);
        colourKeywords.put("white", 16777215);
        colourKeywords.put("whitesmoke", 16119285);
        colourKeywords.put("yellow", 16776960);
        colourKeywords.put("yellowgreen", 10145074);
        fontSizeKeywords.put("xx-small", new SVG.Length(0.694f, SVG.Unit.pt));
        fontSizeKeywords.put("x-small", new SVG.Length(0.833f, SVG.Unit.pt));
        fontSizeKeywords.put("small", new SVG.Length(10.0f, SVG.Unit.pt));
        fontSizeKeywords.put("medium", new SVG.Length(12.0f, SVG.Unit.pt));
        fontSizeKeywords.put("large", new SVG.Length(14.4f, SVG.Unit.pt));
        fontSizeKeywords.put("x-large", new SVG.Length(17.3f, SVG.Unit.pt));
        fontSizeKeywords.put("xx-large", new SVG.Length(20.7f, SVG.Unit.pt));
        fontSizeKeywords.put("smaller", new SVG.Length(83.33f, SVG.Unit.percent));
        fontSizeKeywords.put("larger", new SVG.Length(120.0f, SVG.Unit.percent));
        HashMap<String, Integer> hashMap = fontWeightKeywords;
        Integer valueOf = Integer.valueOf((int) UseOpenWifiPreferenceController.REQUEST_CODE_OPEN_WIFI_AUTOMATICALLY);
        hashMap.put("normal", valueOf);
        fontWeightKeywords.put("bold", 700);
        fontWeightKeywords.put("bolder", 1);
        fontWeightKeywords.put("lighter", -1);
        fontWeightKeywords.put("100", 100);
        fontWeightKeywords.put("200", 200);
        fontWeightKeywords.put("300", 300);
        fontWeightKeywords.put("400", valueOf);
        fontWeightKeywords.put("500", 500);
        fontWeightKeywords.put("600", 600);
        fontWeightKeywords.put("700", 700);
        fontWeightKeywords.put("800", 800);
        fontWeightKeywords.put("900", 900);
        fontStyleKeywords.put("normal", SVG.Style.FontStyle.Normal);
        fontStyleKeywords.put("italic", SVG.Style.FontStyle.Italic);
        fontStyleKeywords.put("oblique", SVG.Style.FontStyle.Oblique);
        aspectRatioKeywords.put("none", PreserveAspectRatio.Alignment.None);
        aspectRatioKeywords.put("xMinYMin", PreserveAspectRatio.Alignment.XMinYMin);
        aspectRatioKeywords.put("xMidYMin", PreserveAspectRatio.Alignment.XMidYMin);
        aspectRatioKeywords.put("xMaxYMin", PreserveAspectRatio.Alignment.XMaxYMin);
        aspectRatioKeywords.put("xMinYMid", PreserveAspectRatio.Alignment.XMinYMid);
        aspectRatioKeywords.put("xMidYMid", PreserveAspectRatio.Alignment.XMidYMid);
        aspectRatioKeywords.put("xMaxYMid", PreserveAspectRatio.Alignment.XMaxYMid);
        aspectRatioKeywords.put("xMinYMax", PreserveAspectRatio.Alignment.XMinYMax);
        aspectRatioKeywords.put("xMidYMax", PreserveAspectRatio.Alignment.XMidYMax);
        aspectRatioKeywords.put("xMaxYMax", PreserveAspectRatio.Alignment.XMaxYMax);
        supportedFeatures.add("Structure");
        supportedFeatures.add("BasicStructure");
        supportedFeatures.add("ConditionalProcessing");
        supportedFeatures.add("Image");
        supportedFeatures.add("Style");
        supportedFeatures.add("ViewportAttribute");
        supportedFeatures.add("Shape");
        supportedFeatures.add("BasicText");
        supportedFeatures.add("PaintAttribute");
        supportedFeatures.add("BasicPaintAttribute");
        supportedFeatures.add("OpacityAttribute");
        supportedFeatures.add("BasicGraphicsAttribute");
        supportedFeatures.add("Marker");
        supportedFeatures.add("Gradient");
        supportedFeatures.add("Pattern");
        supportedFeatures.add("Clip");
        supportedFeatures.add("BasicClip");
        supportedFeatures.add("Mask");
        supportedFeatures.add("View");
    }

    /* access modifiers changed from: protected */
    public SVG parse(InputStream inputStream) throws SVGParseException {
        try {
            XMLReader xMLReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            xMLReader.setContentHandler(this);
            xMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
            xMLReader.parse(new InputSource(inputStream));
            return this.svgDocument;
        } catch (IOException e) {
            throw new SVGParseException("File error", e);
        } catch (ParserConfigurationException e2) {
            throw new SVGParseException("XML Parser problem", e2);
        } catch (SAXException e3) {
            throw new SVGParseException("SVG parse error: " + e3.getMessage(), e3);
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
        super.startDocument();
        this.svgDocument = new SVG();
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
        super.startElement(str, str2, str3, attributes);
        if (this.ignoring) {
            this.ignoreDepth++;
        } else if (!"http://www.w3.org/2000/svg".equals(str) && !"".equals(str)) {
        } else {
            if (str2.equals("svg")) {
                svg(attributes);
            } else if (str2.equals("g")) {
                g(attributes);
            } else if (str2.equals("defs")) {
                defs(attributes);
            } else if (str2.equals("use")) {
                use(attributes);
            } else if (str2.equals("path")) {
                path(attributes);
            } else if (str2.equals("rect")) {
                rect(attributes);
            } else if (str2.equals("circle")) {
                circle(attributes);
            } else if (str2.equals("ellipse")) {
                ellipse(attributes);
            } else if (str2.equals("line")) {
                line(attributes);
            } else if (str2.equals("polyline")) {
                polyline(attributes);
            } else if (str2.equals("polygon")) {
                polygon(attributes);
            } else if (str2.equals("text")) {
                text(attributes);
            } else if (str2.equals("tspan")) {
                tspan(attributes);
            } else if (str2.equals("tref")) {
                tref(attributes);
            } else if (str2.equals("switch")) {
                zwitch(attributes);
            } else if (str2.equals("symbol")) {
                symbol(attributes);
            } else if (str2.equals("marker")) {
                marker(attributes);
            } else if (str2.equals("linearGradient")) {
                linearGradient(attributes);
            } else if (str2.equals("radialGradient")) {
                radialGradient(attributes);
            } else if (str2.equals("stop")) {
                stop(attributes);
            } else if (str2.equals("a")) {
                g(attributes);
            } else if (str2.equals("title") || str2.equals("desc")) {
                this.inMetadataElement = true;
                this.metadataTag = str2;
            } else if (str2.equals("clipPath")) {
                clipPath(attributes);
            } else if (str2.equals("textPath")) {
                textPath(attributes);
            } else if (str2.equals("pattern")) {
                pattern(attributes);
            } else if (str2.equals("image")) {
                image(attributes);
            } else if (str2.equals("view")) {
                view(attributes);
            } else if (str2.equals("mask")) {
                mask(attributes);
            } else if (str2.equals("style")) {
                style(attributes);
            } else if (str2.equals("solidColor")) {
                solidColor(attributes);
            } else {
                this.ignoring = true;
                this.ignoreDepth = 1;
            }
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] cArr, int i, int i2) throws SAXException {
        SVG.SvgObject svgObject;
        if (!this.ignoring) {
            if (this.inMetadataElement) {
                if (this.metadataElementContents == null) {
                    this.metadataElementContents = new StringBuilder(i2);
                }
                this.metadataElementContents.append(cArr, i, i2);
            } else if (this.inStyleElement) {
                if (this.styleElementContents == null) {
                    this.styleElementContents = new StringBuilder(i2);
                }
                this.styleElementContents.append(cArr, i, i2);
            } else {
                SVG.SvgContainer svgContainer = this.currentElement;
                if (svgContainer instanceof SVG.TextContainer) {
                    SVG.SvgConditionalContainer svgConditionalContainer = (SVG.SvgConditionalContainer) svgContainer;
                    int size = svgConditionalContainer.children.size();
                    if (size == 0) {
                        svgObject = null;
                    } else {
                        svgObject = svgConditionalContainer.children.get(size - 1);
                    }
                    if (svgObject instanceof SVG.TextSequence) {
                        SVG.TextSequence textSequence = (SVG.TextSequence) svgObject;
                        textSequence.text = String.valueOf(textSequence.text) + new String(cArr, i, i2);
                        return;
                    }
                    ((SVG.SvgConditionalContainer) this.currentElement).addChild(new SVG.TextSequence(new String(cArr, i, i2)));
                }
            }
        }
    }

    @Override // org.xml.sax.ext.LexicalHandler, org.xml.sax.ext.DefaultHandler2
    public void comment(char[] cArr, int i, int i2) throws SAXException {
        if (!this.ignoring && this.inStyleElement) {
            if (this.styleElementContents == null) {
                this.styleElementContents = new StringBuilder(i2);
            }
            this.styleElementContents.append(cArr, i, i2);
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String str, String str2, String str3) throws SAXException {
        StringBuilder sb;
        super.endElement(str, str2, str3);
        if (this.ignoring) {
            int i = this.ignoreDepth - 1;
            this.ignoreDepth = i;
            if (i == 0) {
                this.ignoring = false;
                return;
            }
        }
        if (!"http://www.w3.org/2000/svg".equals(str) && !"".equals(str)) {
            return;
        }
        if (str2.equals("title") || str2.equals("desc")) {
            this.inMetadataElement = false;
            if (this.metadataTag.equals("title")) {
                this.svgDocument.setTitle(this.metadataElementContents.toString());
            } else if (this.metadataTag.equals("desc")) {
                this.svgDocument.setDesc(this.metadataElementContents.toString());
            }
            this.metadataElementContents.setLength(0);
        } else if (str2.equals("style") && (sb = this.styleElementContents) != null) {
            this.inStyleElement = false;
            parseCSSStyleSheet(sb.toString());
            this.styleElementContents.setLength(0);
        } else if (str2.equals("svg") || str2.equals("defs") || str2.equals("g") || str2.equals("use") || str2.equals("image") || str2.equals("text") || str2.equals("tspan") || str2.equals("switch") || str2.equals("symbol") || str2.equals("marker") || str2.equals("linearGradient") || str2.equals("radialGradient") || str2.equals("stop") || str2.equals("clipPath") || str2.equals("textPath") || str2.equals("pattern") || str2.equals("view") || str2.equals("mask") || str2.equals("solidColor")) {
            this.currentElement = ((SVG.SvgObject) this.currentElement).parent;
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    private void svg(Attributes attributes) throws SAXException {
        debug("<svg>", new Object[0]);
        SVG.Svg svg = new SVG.Svg();
        svg.document = this.svgDocument;
        svg.parent = this.currentElement;
        parseAttributesCore(svg, attributes);
        parseAttributesStyle(svg, attributes);
        parseAttributesConditional(svg, attributes);
        parseAttributesViewBox(svg, attributes);
        parseAttributesSVG(svg, attributes);
        SVG.SvgContainer svgContainer = this.currentElement;
        if (svgContainer == null) {
            this.svgDocument.setRootElement(svg);
        } else {
            svgContainer.addChild(svg);
        }
        this.currentElement = svg;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0060, code lost:
        continue;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void parseAttributesSVG(com.caverock.androidsvg.SVG.Svg r4, org.xml.sax.Attributes r5) throws org.xml.sax.SAXException {
        /*
        // Method dump skipped, instructions count: 118
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGParser.parseAttributesSVG(com.caverock.androidsvg.SVG$Svg, org.xml.sax.Attributes):void");
    }

    private void g(Attributes attributes) throws SAXException {
        debug("<g>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Group group = new SVG.Group();
            group.document = this.svgDocument;
            group.parent = this.currentElement;
            parseAttributesCore(group, attributes);
            parseAttributesStyle(group, attributes);
            parseAttributesTransform(group, attributes);
            parseAttributesConditional(group, attributes);
            this.currentElement.addChild(group);
            this.currentElement = group;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void defs(Attributes attributes) throws SAXException {
        debug("<defs>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Defs defs = new SVG.Defs();
            defs.document = this.svgDocument;
            defs.parent = this.currentElement;
            parseAttributesCore(defs, attributes);
            parseAttributesStyle(defs, attributes);
            parseAttributesTransform(defs, attributes);
            this.currentElement.addChild(defs);
            this.currentElement = defs;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void use(Attributes attributes) throws SAXException {
        debug("<use>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Use use = new SVG.Use();
            use.document = this.svgDocument;
            use.parent = this.currentElement;
            parseAttributesCore(use, attributes);
            parseAttributesStyle(use, attributes);
            parseAttributesTransform(use, attributes);
            parseAttributesConditional(use, attributes);
            parseAttributesUse(use, attributes);
            this.currentElement.addChild(use);
            this.currentElement = use;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x006d, code lost:
        continue;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void parseAttributesUse(com.caverock.androidsvg.SVG.Use r4, org.xml.sax.Attributes r5) throws org.xml.sax.SAXException {
        /*
        // Method dump skipped, instructions count: 130
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGParser.parseAttributesUse(com.caverock.androidsvg.SVG$Use, org.xml.sax.Attributes):void");
    }

    private void image(Attributes attributes) throws SAXException {
        debug("<image>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Image image = new SVG.Image();
            image.document = this.svgDocument;
            image.parent = this.currentElement;
            parseAttributesCore(image, attributes);
            parseAttributesStyle(image, attributes);
            parseAttributesTransform(image, attributes);
            parseAttributesConditional(image, attributes);
            parseAttributesImage(image, attributes);
            this.currentElement.addChild(image);
            this.currentElement = image;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0075, code lost:
        continue;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void parseAttributesImage(com.caverock.androidsvg.SVG.Image r4, org.xml.sax.Attributes r5) throws org.xml.sax.SAXException {
        /*
        // Method dump skipped, instructions count: 138
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGParser.parseAttributesImage(com.caverock.androidsvg.SVG$Image, org.xml.sax.Attributes):void");
    }

    private void path(Attributes attributes) throws SAXException {
        debug("<path>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Path path = new SVG.Path();
            path.document = this.svgDocument;
            path.parent = this.currentElement;
            parseAttributesCore(path, attributes);
            parseAttributesStyle(path, attributes);
            parseAttributesTransform(path, attributes);
            parseAttributesConditional(path, attributes);
            parseAttributesPath(path, attributes);
            this.currentElement.addChild(path);
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void parseAttributesPath(SVG.Path path, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            int i2 = $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()];
            if (i2 == 14) {
                path.d = parsePath(trim);
            } else if (i2 != 44) {
                continue;
            } else {
                Float valueOf = Float.valueOf(parseFloat(trim));
                path.pathLength = valueOf;
                if (valueOf.floatValue() < 0.0f) {
                    throw new SAXException("Invalid <path> element. pathLength cannot be negative");
                }
            }
        }
    }

    private void rect(Attributes attributes) throws SAXException {
        debug("<rect>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Rect rect = new SVG.Rect();
            rect.document = this.svgDocument;
            rect.parent = this.currentElement;
            parseAttributesCore(rect, attributes);
            parseAttributesStyle(rect, attributes);
            parseAttributesTransform(rect, attributes);
            parseAttributesConditional(rect, attributes);
            parseAttributesRect(rect, attributes);
            this.currentElement.addChild(rect);
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:38:0x008b, code lost:
        continue;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void parseAttributesRect(com.caverock.androidsvg.SVG.Rect r4, org.xml.sax.Attributes r5) throws org.xml.sax.SAXException {
        /*
        // Method dump skipped, instructions count: 162
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGParser.parseAttributesRect(com.caverock.androidsvg.SVG$Rect, org.xml.sax.Attributes):void");
    }

    private void circle(Attributes attributes) throws SAXException {
        debug("<circle>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Circle circle = new SVG.Circle();
            circle.document = this.svgDocument;
            circle.parent = this.currentElement;
            parseAttributesCore(circle, attributes);
            parseAttributesStyle(circle, attributes);
            parseAttributesTransform(circle, attributes);
            parseAttributesConditional(circle, attributes);
            parseAttributesCircle(circle, attributes);
            this.currentElement.addChild(circle);
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void parseAttributesCircle(SVG.Circle circle, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            int i2 = $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()];
            if (i2 == 7) {
                circle.cx = parseLength(trim);
            } else if (i2 == 8) {
                circle.cy = parseLength(trim);
            } else if (i2 != 50) {
                continue;
            } else {
                SVG.Length parseLength = parseLength(trim);
                circle.r = parseLength;
                if (parseLength.isNegative()) {
                    throw new SAXException("Invalid <circle> element. r cannot be negative");
                }
            }
        }
    }

    private void ellipse(Attributes attributes) throws SAXException {
        debug("<ellipse>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Ellipse ellipse = new SVG.Ellipse();
            ellipse.document = this.svgDocument;
            ellipse.parent = this.currentElement;
            parseAttributesCore(ellipse, attributes);
            parseAttributesStyle(ellipse, attributes);
            parseAttributesTransform(ellipse, attributes);
            parseAttributesConditional(ellipse, attributes);
            parseAttributesEllipse(ellipse, attributes);
            this.currentElement.addChild(ellipse);
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void parseAttributesEllipse(SVG.Ellipse ellipse, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            int i2 = $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()];
            if (i2 == 7) {
                ellipse.cx = parseLength(trim);
            } else if (i2 == 8) {
                ellipse.cy = parseLength(trim);
            } else if (i2 == 57) {
                SVG.Length parseLength = parseLength(trim);
                ellipse.rx = parseLength;
                if (parseLength.isNegative()) {
                    throw new SAXException("Invalid <ellipse> element. rx cannot be negative");
                }
            } else if (i2 != 58) {
                continue;
            } else {
                SVG.Length parseLength2 = parseLength(trim);
                ellipse.ry = parseLength2;
                if (parseLength2.isNegative()) {
                    throw new SAXException("Invalid <ellipse> element. ry cannot be negative");
                }
            }
        }
    }

    private void line(Attributes attributes) throws SAXException {
        debug("<line>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Line line = new SVG.Line();
            line.document = this.svgDocument;
            line.parent = this.currentElement;
            parseAttributesCore(line, attributes);
            parseAttributesStyle(line, attributes);
            parseAttributesTransform(line, attributes);
            parseAttributesConditional(line, attributes);
            parseAttributesLine(line, attributes);
            this.currentElement.addChild(line);
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void parseAttributesLine(SVG.Line line, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            switch ($SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()]) {
                case 85:
                    line.x1 = parseLength(trim);
                    break;
                case 86:
                    line.y1 = parseLength(trim);
                    break;
                case 87:
                    line.x2 = parseLength(trim);
                    break;
                case 88:
                    line.y2 = parseLength(trim);
                    break;
            }
        }
    }

    private void polyline(Attributes attributes) throws SAXException {
        debug("<polyline>", new Object[0]);
        if (this.currentElement != null) {
            SVG.PolyLine polyLine = new SVG.PolyLine();
            polyLine.document = this.svgDocument;
            polyLine.parent = this.currentElement;
            parseAttributesCore(polyLine, attributes);
            parseAttributesStyle(polyLine, attributes);
            parseAttributesTransform(polyLine, attributes);
            parseAttributesConditional(polyLine, attributes);
            parseAttributesPolyLine(polyLine, attributes, "polyline");
            this.currentElement.addChild(polyLine);
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void parseAttributesPolyLine(SVG.PolyLine polyLine, Attributes attributes, String str) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            if (SVGAttr.fromString(attributes.getLocalName(i)) == SVGAttr.points) {
                TextScanner textScanner = new TextScanner(attributes.getValue(i));
                ArrayList<Float> arrayList = new ArrayList();
                textScanner.skipWhitespace();
                while (!textScanner.empty()) {
                    Float nextFloat = textScanner.nextFloat();
                    if (nextFloat != null) {
                        textScanner.skipCommaWhitespace();
                        Float nextFloat2 = textScanner.nextFloat();
                        if (nextFloat2 != null) {
                            textScanner.skipCommaWhitespace();
                            arrayList.add(nextFloat);
                            arrayList.add(nextFloat2);
                        } else {
                            throw new SAXException("Invalid <" + str + "> points attribute. There should be an even number of coordinates.");
                        }
                    } else {
                        throw new SAXException("Invalid <" + str + "> points attribute. Non-coordinate content found in list.");
                    }
                }
                polyLine.points = new float[arrayList.size()];
                int i2 = 0;
                for (Float f : arrayList) {
                    polyLine.points[i2] = f.floatValue();
                    i2++;
                }
            }
        }
    }

    private void polygon(Attributes attributes) throws SAXException {
        debug("<polygon>", new Object[0]);
        if (this.currentElement != null) {
            SVG.PolyLine polygon = new SVG.Polygon();
            polygon.document = this.svgDocument;
            polygon.parent = this.currentElement;
            parseAttributesCore(polygon, attributes);
            parseAttributesStyle(polygon, attributes);
            parseAttributesTransform(polygon, attributes);
            parseAttributesConditional(polygon, attributes);
            parseAttributesPolyLine(polygon, attributes, "polygon");
            this.currentElement.addChild(polygon);
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void text(Attributes attributes) throws SAXException {
        debug("<text>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Text text = new SVG.Text();
            text.document = this.svgDocument;
            text.parent = this.currentElement;
            parseAttributesCore(text, attributes);
            parseAttributesStyle(text, attributes);
            parseAttributesTransform(text, attributes);
            parseAttributesConditional(text, attributes);
            parseAttributesTextPosition(text, attributes);
            this.currentElement.addChild(text);
            this.currentElement = text;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void parseAttributesTextPosition(SVG.TextPositionedContainer textPositionedContainer, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            int i2 = $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()];
            if (i2 == 10) {
                textPositionedContainer.dx = parseLengthList(trim);
            } else if (i2 == 11) {
                textPositionedContainer.dy = parseLengthList(trim);
            } else if (i2 == 83) {
                textPositionedContainer.x = parseLengthList(trim);
            } else if (i2 == 84) {
                textPositionedContainer.y = parseLengthList(trim);
            }
        }
    }

    private void tspan(Attributes attributes) throws SAXException {
        debug("<tspan>", new Object[0]);
        SVG.SvgContainer svgContainer = this.currentElement;
        if (svgContainer == null) {
            throw new SAXException("Invalid document. Root element must be <svg>");
        } else if (svgContainer instanceof SVG.TextContainer) {
            SVG.TSpan tSpan = new SVG.TSpan();
            tSpan.document = this.svgDocument;
            tSpan.parent = this.currentElement;
            parseAttributesCore(tSpan, attributes);
            parseAttributesStyle(tSpan, attributes);
            parseAttributesConditional(tSpan, attributes);
            parseAttributesTextPosition(tSpan, attributes);
            this.currentElement.addChild(tSpan);
            this.currentElement = tSpan;
            SVG.SvgContainer svgContainer2 = tSpan.parent;
            if (svgContainer2 instanceof SVG.TextRoot) {
                tSpan.setTextRoot((SVG.TextRoot) svgContainer2);
            } else {
                tSpan.setTextRoot(((SVG.TextChild) svgContainer2).getTextRoot());
            }
        } else {
            throw new SAXException("Invalid document. <tspan> elements are only valid inside <text> or other <tspan> elements.");
        }
    }

    private void tref(Attributes attributes) throws SAXException {
        debug("<tref>", new Object[0]);
        SVG.SvgContainer svgContainer = this.currentElement;
        if (svgContainer == null) {
            throw new SAXException("Invalid document. Root element must be <svg>");
        } else if (svgContainer instanceof SVG.TextContainer) {
            SVG.TRef tRef = new SVG.TRef();
            tRef.document = this.svgDocument;
            tRef.parent = this.currentElement;
            parseAttributesCore(tRef, attributes);
            parseAttributesStyle(tRef, attributes);
            parseAttributesConditional(tRef, attributes);
            parseAttributesTRef(tRef, attributes);
            this.currentElement.addChild(tRef);
            SVG.SvgContainer svgContainer2 = tRef.parent;
            if (svgContainer2 instanceof SVG.TextRoot) {
                tRef.setTextRoot((SVG.TextRoot) svgContainer2);
            } else {
                tRef.setTextRoot(((SVG.TextChild) svgContainer2).getTextRoot());
            }
        } else {
            throw new SAXException("Invalid document. <tref> elements are only valid inside <text> or <tspan> elements.");
        }
    }

    private void parseAttributesTRef(SVG.TRef tRef, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            if ($SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()] == 27 && "http://www.w3.org/1999/xlink".equals(attributes.getURI(i))) {
                tRef.href = trim;
            }
        }
    }

    private void zwitch(Attributes attributes) throws SAXException {
        debug("<switch>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Switch r0 = new SVG.Switch();
            r0.document = this.svgDocument;
            r0.parent = this.currentElement;
            parseAttributesCore(r0, attributes);
            parseAttributesStyle(r0, attributes);
            parseAttributesTransform(r0, attributes);
            parseAttributesConditional(r0, attributes);
            this.currentElement.addChild(r0);
            this.currentElement = r0;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void parseAttributesConditional(SVG.SvgConditional svgConditional, Attributes attributes) throws SAXException {
        HashSet hashSet;
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            int i2 = $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()];
            if (i2 != 74) {
                switch (i2) {
                    case R$styleable.ConstraintLayout_Layout_layout_constraintGuide_end /* 53 */:
                        svgConditional.setRequiredFeatures(parseRequiredFeatures(trim));
                        continue;
                    case 54:
                        svgConditional.setRequiredExtensions(trim);
                        continue;
                    case 55:
                        svgConditional.setRequiredFormats(parseRequiredFormats(trim));
                        continue;
                    case 56:
                        if (parseFontFamily(trim) == null) {
                            hashSet = new HashSet(0);
                        }
                        svgConditional.setRequiredFonts(hashSet);
                        continue;
                }
            } else {
                svgConditional.setSystemLanguage(parseSystemLanguage(trim));
            }
        }
    }

    private void symbol(Attributes attributes) throws SAXException {
        debug("<symbol>", new Object[0]);
        if (this.currentElement != null) {
            SVG.SvgViewBoxContainer symbol = new SVG.Symbol();
            symbol.document = this.svgDocument;
            symbol.parent = this.currentElement;
            parseAttributesCore(symbol, attributes);
            parseAttributesStyle(symbol, attributes);
            parseAttributesConditional(symbol, attributes);
            parseAttributesViewBox(symbol, attributes);
            this.currentElement.addChild(symbol);
            this.currentElement = symbol;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void marker(Attributes attributes) throws SAXException {
        debug("<marker>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Marker marker = new SVG.Marker();
            marker.document = this.svgDocument;
            marker.parent = this.currentElement;
            parseAttributesCore(marker, attributes);
            parseAttributesStyle(marker, attributes);
            parseAttributesConditional(marker, attributes);
            parseAttributesViewBox(marker, attributes);
            parseAttributesMarker(marker, attributes);
            this.currentElement.addChild(marker);
            this.currentElement = marker;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00a7, code lost:
        continue;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void parseAttributesMarker(com.caverock.androidsvg.SVG.Marker r5, org.xml.sax.Attributes r6) throws org.xml.sax.SAXException {
        /*
        // Method dump skipped, instructions count: 182
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGParser.parseAttributesMarker(com.caverock.androidsvg.SVG$Marker, org.xml.sax.Attributes):void");
    }

    private void linearGradient(Attributes attributes) throws SAXException {
        debug("<linearGradiant>", new Object[0]);
        if (this.currentElement != null) {
            SVG.SvgLinearGradient svgLinearGradient = new SVG.SvgLinearGradient();
            svgLinearGradient.document = this.svgDocument;
            svgLinearGradient.parent = this.currentElement;
            parseAttributesCore(svgLinearGradient, attributes);
            parseAttributesStyle(svgLinearGradient, attributes);
            parseAttributesGradient(svgLinearGradient, attributes);
            parseAttributesLinearGradient(svgLinearGradient, attributes);
            this.currentElement.addChild(svgLinearGradient);
            this.currentElement = svgLinearGradient;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void parseAttributesGradient(SVG.GradientElement gradientElement, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            int i2 = $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()];
            if (i2 == 24) {
                gradientElement.gradientTransform = parseTransformList(trim);
            } else if (i2 != 25) {
                if (i2 != 27) {
                    if (i2 == 61) {
                        try {
                            gradientElement.spreadMethod = SVG.GradientSpread.valueOf(trim);
                        } catch (IllegalArgumentException unused) {
                            throw new SAXException("Invalid spreadMethod attribute. \"" + trim + "\" is not a valid value.");
                        }
                    }
                } else if ("http://www.w3.org/1999/xlink".equals(attributes.getURI(i))) {
                    gradientElement.href = trim;
                }
            } else if ("objectBoundingBox".equals(trim)) {
                gradientElement.gradientUnitsAreUser = Boolean.FALSE;
            } else if ("userSpaceOnUse".equals(trim)) {
                gradientElement.gradientUnitsAreUser = Boolean.TRUE;
            } else {
                throw new SAXException("Invalid value for attribute gradientUnits");
            }
        }
    }

    private void parseAttributesLinearGradient(SVG.SvgLinearGradient svgLinearGradient, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            switch ($SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()]) {
                case 85:
                    svgLinearGradient.x1 = parseLength(trim);
                    break;
                case 86:
                    svgLinearGradient.y1 = parseLength(trim);
                    break;
                case 87:
                    svgLinearGradient.x2 = parseLength(trim);
                    break;
                case 88:
                    svgLinearGradient.y2 = parseLength(trim);
                    break;
            }
        }
    }

    private void radialGradient(Attributes attributes) throws SAXException {
        debug("<radialGradient>", new Object[0]);
        if (this.currentElement != null) {
            SVG.SvgRadialGradient svgRadialGradient = new SVG.SvgRadialGradient();
            svgRadialGradient.document = this.svgDocument;
            svgRadialGradient.parent = this.currentElement;
            parseAttributesCore(svgRadialGradient, attributes);
            parseAttributesStyle(svgRadialGradient, attributes);
            parseAttributesGradient(svgRadialGradient, attributes);
            parseAttributesRadialGradient(svgRadialGradient, attributes);
            this.currentElement.addChild(svgRadialGradient);
            this.currentElement = svgRadialGradient;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void parseAttributesRadialGradient(SVG.SvgRadialGradient svgRadialGradient, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            int i2 = $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()];
            if (i2 == 7) {
                svgRadialGradient.cx = parseLength(trim);
            } else if (i2 == 8) {
                svgRadialGradient.cy = parseLength(trim);
            } else if (i2 == 12) {
                svgRadialGradient.fx = parseLength(trim);
            } else if (i2 == 13) {
                svgRadialGradient.fy = parseLength(trim);
            } else if (i2 != 50) {
                continue;
            } else {
                SVG.Length parseLength = parseLength(trim);
                svgRadialGradient.r = parseLength;
                if (parseLength.isNegative()) {
                    throw new SAXException("Invalid <radialGradient> element. r cannot be negative");
                }
            }
        }
    }

    private void stop(Attributes attributes) throws SAXException {
        debug("<stop>", new Object[0]);
        SVG.SvgContainer svgContainer = this.currentElement;
        if (svgContainer == null) {
            throw new SAXException("Invalid document. Root element must be <svg>");
        } else if (svgContainer instanceof SVG.GradientElement) {
            SVG.Stop stop = new SVG.Stop();
            stop.document = this.svgDocument;
            stop.parent = this.currentElement;
            parseAttributesCore(stop, attributes);
            parseAttributesStyle(stop, attributes);
            parseAttributesStop(stop, attributes);
            this.currentElement.addChild(stop);
            this.currentElement = stop;
        } else {
            throw new SAXException("Invalid document. <stop> elements are only valid inside <linearGradiant> or <radialGradient> elements.");
        }
    }

    private void parseAttributesStop(SVG.Stop stop, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            if ($SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()] == 40) {
                stop.offset = parseGradiantOffset(trim);
            }
        }
    }

    private Float parseGradiantOffset(String str) throws SAXException {
        if (str.length() != 0) {
            int length = str.length();
            boolean z = true;
            if (str.charAt(str.length() - 1) == '%') {
                length--;
            } else {
                z = false;
            }
            try {
                float parseFloat = Float.parseFloat(str.substring(0, length));
                float f = 100.0f;
                if (z) {
                    parseFloat /= 100.0f;
                }
                if (parseFloat < 0.0f) {
                    f = 0.0f;
                } else if (parseFloat <= 100.0f) {
                    f = parseFloat;
                }
                return Float.valueOf(f);
            } catch (NumberFormatException e) {
                throw new SAXException("Invalid offset value in <stop>: " + str, e);
            }
        } else {
            throw new SAXException("Invalid offset value in <stop> (empty string)");
        }
    }

    private void solidColor(Attributes attributes) throws SAXException {
        debug("<solidColor>", new Object[0]);
        if (this.currentElement != null) {
            SVG.SolidColor solidColor = new SVG.SolidColor();
            solidColor.document = this.svgDocument;
            solidColor.parent = this.currentElement;
            parseAttributesCore(solidColor, attributes);
            parseAttributesStyle(solidColor, attributes);
            this.currentElement.addChild(solidColor);
            this.currentElement = solidColor;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void clipPath(Attributes attributes) throws SAXException {
        debug("<clipPath>", new Object[0]);
        if (this.currentElement != null) {
            SVG.ClipPath clipPath = new SVG.ClipPath();
            clipPath.document = this.svgDocument;
            clipPath.parent = this.currentElement;
            parseAttributesCore(clipPath, attributes);
            parseAttributesStyle(clipPath, attributes);
            parseAttributesTransform(clipPath, attributes);
            parseAttributesConditional(clipPath, attributes);
            parseAttributesClipPath(clipPath, attributes);
            this.currentElement.addChild(clipPath);
            this.currentElement = clipPath;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void parseAttributesClipPath(SVG.ClipPath clipPath, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            if ($SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()] == 4) {
                if ("objectBoundingBox".equals(trim)) {
                    clipPath.clipPathUnitsAreUser = Boolean.FALSE;
                } else if ("userSpaceOnUse".equals(trim)) {
                    clipPath.clipPathUnitsAreUser = Boolean.TRUE;
                } else {
                    throw new SAXException("Invalid value for attribute clipPathUnits");
                }
            }
        }
    }

    private void textPath(Attributes attributes) throws SAXException {
        debug("<textPath>", new Object[0]);
        if (this.currentElement != null) {
            SVG.TextPath textPath = new SVG.TextPath();
            textPath.document = this.svgDocument;
            textPath.parent = this.currentElement;
            parseAttributesCore(textPath, attributes);
            parseAttributesStyle(textPath, attributes);
            parseAttributesConditional(textPath, attributes);
            parseAttributesTextPath(textPath, attributes);
            this.currentElement.addChild(textPath);
            this.currentElement = textPath;
            SVG.SvgContainer svgContainer = textPath.parent;
            if (svgContainer instanceof SVG.TextRoot) {
                textPath.setTextRoot((SVG.TextRoot) svgContainer);
            } else {
                textPath.setTextRoot(((SVG.TextChild) svgContainer).getTextRoot());
            }
        } else {
            throw new SAXException("Invalid document. Root element must be <svg>");
        }
    }

    private void parseAttributesTextPath(SVG.TextPath textPath, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            int i2 = $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()];
            if (i2 != 27) {
                if (i2 == 62) {
                    textPath.startOffset = parseLength(trim);
                }
            } else if ("http://www.w3.org/1999/xlink".equals(attributes.getURI(i))) {
                textPath.href = trim;
            }
        }
    }

    private void pattern(Attributes attributes) throws SAXException {
        debug("<pattern>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Pattern pattern = new SVG.Pattern();
            pattern.document = this.svgDocument;
            pattern.parent = this.currentElement;
            parseAttributesCore(pattern, attributes);
            parseAttributesStyle(pattern, attributes);
            parseAttributesConditional(pattern, attributes);
            parseAttributesViewBox(pattern, attributes);
            parseAttributesPattern(pattern, attributes);
            this.currentElement.addChild(pattern);
            this.currentElement = pattern;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00b7, code lost:
        continue;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void parseAttributesPattern(com.caverock.androidsvg.SVG.Pattern r8, org.xml.sax.Attributes r9) throws org.xml.sax.SAXException {
        /*
        // Method dump skipped, instructions count: 216
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGParser.parseAttributesPattern(com.caverock.androidsvg.SVG$Pattern, org.xml.sax.Attributes):void");
    }

    private void view(Attributes attributes) throws SAXException {
        debug("<view>", new Object[0]);
        if (this.currentElement != null) {
            SVG.SvgViewBoxContainer view = new SVG.View();
            view.document = this.svgDocument;
            view.parent = this.currentElement;
            parseAttributesCore(view, attributes);
            parseAttributesConditional(view, attributes);
            parseAttributesViewBox(view, attributes);
            this.currentElement.addChild(view);
            this.currentElement = view;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void mask(Attributes attributes) throws SAXException {
        debug("<mask>", new Object[0]);
        if (this.currentElement != null) {
            SVG.Mask mask = new SVG.Mask();
            mask.document = this.svgDocument;
            mask.parent = this.currentElement;
            parseAttributesCore(mask, attributes);
            parseAttributesStyle(mask, attributes);
            parseAttributesConditional(mask, attributes);
            parseAttributesMask(mask, attributes);
            this.currentElement.addChild(mask);
            this.currentElement = mask;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:50:0x009e, code lost:
        continue;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void parseAttributesMask(com.caverock.androidsvg.SVG.Mask r8, org.xml.sax.Attributes r9) throws org.xml.sax.SAXException {
        /*
        // Method dump skipped, instructions count: 180
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGParser.parseAttributesMask(com.caverock.androidsvg.SVG$Mask, org.xml.sax.Attributes):void");
    }

    /* access modifiers changed from: protected */
    public static class TextScanner {
        protected String input;
        protected int position = 0;

        /* access modifiers changed from: protected */
        public boolean isEOL(int i) {
            return i == 10 || i == 13;
        }

        /* access modifiers changed from: protected */
        public boolean isWhitespace(int i) {
            return i == 32 || i == 10 || i == 13 || i == 9;
        }

        public TextScanner(String str) {
            this.input = str.trim();
        }

        public boolean empty() {
            return this.position == this.input.length();
        }

        public void skipWhitespace() {
            while (this.position < this.input.length() && isWhitespace(this.input.charAt(this.position))) {
                this.position++;
            }
        }

        public boolean skipCommaWhitespace() {
            skipWhitespace();
            if (this.position == this.input.length() || this.input.charAt(this.position) != ',') {
                return false;
            }
            this.position++;
            skipWhitespace();
            return true;
        }

        public Float nextFloat() {
            int scanForFloat = scanForFloat();
            int i = this.position;
            if (scanForFloat == i) {
                return null;
            }
            Float valueOf = Float.valueOf(Float.parseFloat(this.input.substring(i, scanForFloat)));
            this.position = scanForFloat;
            return valueOf;
        }

        public Float possibleNextFloat() {
            int i = this.position;
            skipCommaWhitespace();
            Float nextFloat = nextFloat();
            if (nextFloat != null) {
                return nextFloat;
            }
            this.position = i;
            return null;
        }

        public Integer nextInteger() {
            int scanForInteger = scanForInteger();
            int i = this.position;
            if (scanForInteger == i) {
                return null;
            }
            Integer valueOf = Integer.valueOf(Integer.parseInt(this.input.substring(i, scanForInteger)));
            this.position = scanForInteger;
            return valueOf;
        }

        public Integer nextChar() {
            if (this.position == this.input.length()) {
                return null;
            }
            String str = this.input;
            int i = this.position;
            this.position = i + 1;
            return Integer.valueOf(str.charAt(i));
        }

        public SVG.Length nextLength() {
            Float nextFloat = nextFloat();
            if (nextFloat == null) {
                return null;
            }
            SVG.Unit nextUnit = nextUnit();
            if (nextUnit == null) {
                return new SVG.Length(nextFloat.floatValue(), SVG.Unit.px);
            }
            return new SVG.Length(nextFloat.floatValue(), nextUnit);
        }

        public Boolean nextFlag() {
            if (this.position == this.input.length()) {
                return null;
            }
            char charAt = this.input.charAt(this.position);
            if (charAt != '0' && charAt != '1') {
                return null;
            }
            boolean z = true;
            this.position++;
            if (charAt != '1') {
                z = false;
            }
            return Boolean.valueOf(z);
        }

        public boolean consume(char c) {
            boolean z = this.position < this.input.length() && this.input.charAt(this.position) == c;
            if (z) {
                this.position++;
            }
            return z;
        }

        /* JADX WARNING: Removed duplicated region for block: B:7:0x0024  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean consume(java.lang.String r5) {
            /*
                r4 = this;
                int r0 = r5.length()
                int r1 = r4.position
                java.lang.String r2 = r4.input
                int r2 = r2.length()
                int r2 = r2 - r0
                if (r1 > r2) goto L_0x0021
                java.lang.String r1 = r4.input
                int r2 = r4.position
                int r3 = r2 + r0
                java.lang.String r1 = r1.substring(r2, r3)
                boolean r5 = r1.equals(r5)
                if (r5 == 0) goto L_0x0021
                r5 = 1
                goto L_0x0022
            L_0x0021:
                r5 = 0
            L_0x0022:
                if (r5 == 0) goto L_0x0029
                int r1 = r4.position
                int r1 = r1 + r0
                r4.position = r1
            L_0x0029:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.SVGParser.TextScanner.consume(java.lang.String):boolean");
        }

        /* access modifiers changed from: protected */
        public int advanceChar() {
            if (this.position == this.input.length()) {
                return -1;
            }
            int i = this.position + 1;
            this.position = i;
            if (i < this.input.length()) {
                return this.input.charAt(this.position);
            }
            return -1;
        }

        public String nextToken() {
            return nextToken(' ');
        }

        public String nextToken(char c) {
            if (empty()) {
                return null;
            }
            char charAt = this.input.charAt(this.position);
            if (isWhitespace(charAt) || charAt == c) {
                return null;
            }
            int i = this.position;
            int advanceChar = advanceChar();
            while (advanceChar != -1 && advanceChar != c && !isWhitespace(advanceChar)) {
                advanceChar = advanceChar();
            }
            return this.input.substring(i, this.position);
        }

        public String nextFunction() {
            if (empty()) {
                return null;
            }
            int i = this.position;
            int charAt = this.input.charAt(i);
            while (true) {
                if ((charAt < 97 || charAt > 122) && (charAt < 65 || charAt > 90)) {
                    int i2 = this.position;
                } else {
                    charAt = advanceChar();
                }
            }
            int i22 = this.position;
            while (isWhitespace(charAt)) {
                charAt = advanceChar();
            }
            if (charAt == 40) {
                this.position++;
                return this.input.substring(i, i22);
            }
            this.position = i;
            return null;
        }

        private int scanForFloat() {
            int i;
            if (empty()) {
                return this.position;
            }
            int i2 = this.position;
            int charAt = this.input.charAt(i2);
            if (charAt == 45 || charAt == 43) {
                charAt = advanceChar();
            }
            if (Character.isDigit(charAt)) {
                i = this.position + 1;
                charAt = advanceChar();
                while (Character.isDigit(charAt)) {
                    i = this.position + 1;
                    charAt = advanceChar();
                }
            } else {
                i = i2;
            }
            if (charAt == 46) {
                i = this.position + 1;
                charAt = advanceChar();
                while (Character.isDigit(charAt)) {
                    i = this.position + 1;
                    charAt = advanceChar();
                }
            }
            if (charAt == 101 || charAt == 69) {
                int advanceChar = advanceChar();
                if (advanceChar == 45 || advanceChar == 43) {
                    advanceChar = advanceChar();
                }
                if (Character.isDigit(advanceChar)) {
                    int advanceChar2 = advanceChar();
                    i = this.position + 1;
                    while (Character.isDigit(advanceChar2)) {
                        i = this.position + 1;
                        advanceChar2 = advanceChar();
                    }
                }
            }
            this.position = i2;
            return i;
        }

        private int scanForInteger() {
            int i;
            if (empty()) {
                return this.position;
            }
            int i2 = this.position;
            int charAt = this.input.charAt(i2);
            if (charAt == 45 || charAt == 43) {
                charAt = advanceChar();
            }
            if (Character.isDigit(charAt)) {
                i = this.position + 1;
                int advanceChar = advanceChar();
                while (Character.isDigit(advanceChar)) {
                    i = this.position + 1;
                    advanceChar = advanceChar();
                }
            } else {
                i = i2;
            }
            this.position = i2;
            return i;
        }

        public String ahead() {
            int i = this.position;
            while (!empty() && !isWhitespace(this.input.charAt(this.position))) {
                this.position++;
            }
            String substring = this.input.substring(i, this.position);
            this.position = i;
            return substring;
        }

        public SVG.Unit nextUnit() {
            if (empty()) {
                return null;
            }
            if (this.input.charAt(this.position) == '%') {
                this.position++;
                return SVG.Unit.percent;
            } else if (this.position > this.input.length() - 2) {
                return null;
            } else {
                try {
                    SVG.Unit valueOf = SVG.Unit.valueOf(this.input.substring(this.position, this.position + 2).toLowerCase(Locale.US));
                    this.position += 2;
                    return valueOf;
                } catch (IllegalArgumentException unused) {
                    return null;
                }
            }
        }

        public boolean hasLetter() {
            if (this.position == this.input.length()) {
                return false;
            }
            char charAt = this.input.charAt(this.position);
            if (charAt >= 'a' && charAt <= 'z') {
                return true;
            }
            if (charAt < 'A' || charAt > 'Z') {
                return false;
            }
            return true;
        }

        public String nextQuotedString() {
            if (empty()) {
                return null;
            }
            int i = this.position;
            char charAt = this.input.charAt(i);
            if (charAt != '\'' && charAt != '\"') {
                return null;
            }
            int advanceChar = advanceChar();
            while (advanceChar != -1 && advanceChar != charAt) {
                advanceChar = advanceChar();
            }
            if (advanceChar == -1) {
                this.position = i;
                return null;
            }
            int i2 = this.position + 1;
            this.position = i2;
            return this.input.substring(i + 1, i2 - 1);
        }

        public String restOfText() {
            if (empty()) {
                return null;
            }
            int i = this.position;
            this.position = this.input.length();
            return this.input.substring(i);
        }
    }

    private void parseAttributesCore(SVG.SvgElementBase svgElementBase, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String qName = attributes.getQName(i);
            if (qName.equals("id") || qName.equals("xml:id")) {
                svgElementBase.id = attributes.getValue(i).trim();
                return;
            } else if (qName.equals("xml:space")) {
                String trim = attributes.getValue(i).trim();
                if ("default".equals(trim)) {
                    svgElementBase.spacePreserve = Boolean.FALSE;
                    return;
                } else if ("preserve".equals(trim)) {
                    svgElementBase.spacePreserve = Boolean.TRUE;
                    return;
                } else {
                    throw new SAXException("Invalid value for \"xml:space\" attribute: " + trim);
                }
            }
        }
    }

    private void parseAttributesStyle(SVG.SvgElementBase svgElementBase, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            if (trim.length() != 0) {
                int i2 = $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()];
                if (i2 == 1) {
                    svgElementBase.classNames = CSSParser.parseClassAttribute(trim);
                } else if (i2 != 73) {
                    if (svgElementBase.baseStyle == null) {
                        svgElementBase.baseStyle = new SVG.Style();
                    }
                    processStyleProperty(svgElementBase.baseStyle, attributes.getLocalName(i), attributes.getValue(i).trim());
                } else {
                    parseStyle(svgElementBase, trim);
                }
            }
        }
    }

    private static void parseStyle(SVG.SvgElementBase svgElementBase, String str) throws SAXException {
        TextScanner textScanner = new TextScanner(str.replaceAll("/\\*.*?\\*/", ""));
        while (true) {
            String nextToken = textScanner.nextToken(':');
            textScanner.skipWhitespace();
            if (textScanner.consume(':')) {
                textScanner.skipWhitespace();
                String nextToken2 = textScanner.nextToken(';');
                if (nextToken2 != null) {
                    textScanner.skipWhitespace();
                    if (textScanner.empty() || textScanner.consume(';')) {
                        if (svgElementBase.style == null) {
                            svgElementBase.style = new SVG.Style();
                        }
                        processStyleProperty(svgElementBase.style, nextToken, nextToken2);
                        textScanner.skipWhitespace();
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    protected static void processStyleProperty(SVG.Style style, String str, String str2) throws SAXException {
        if (str2.length() != 0 && !str2.equals("inherit")) {
            int i = $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(str).ordinal()];
            if (i == 2) {
                style.clip = parseClip(str2);
                style.specifiedFlags |= 1048576;
            } else if (i == 3) {
                style.clipPath = parseFunctionalIRI(str2, str);
                style.specifiedFlags |= 268435456;
            } else if (i == 5) {
                style.clipRule = parseFillRule(str2);
                style.specifiedFlags |= 536870912;
            } else if (i == 6) {
                style.color = parseColour(str2);
                style.specifiedFlags |= 4096;
            } else if (i == 9) {
                style.direction = parseTextDirection(str2);
                style.specifiedFlags |= 68719476736L;
            } else if (i == 36) {
                style.mask = parseFunctionalIRI(str2, str);
                style.specifiedFlags |= 1073741824;
            } else if (i == 41) {
                style.opacity = Float.valueOf(parseOpacity(str2));
                style.specifiedFlags |= 2048;
            } else if (i == 43) {
                style.overflow = parseOverflow(str2);
                style.specifiedFlags |= 524288;
            } else if (i == 79) {
                style.vectorEffect = parseVectorEffect(str2);
                style.specifiedFlags |= 34359738368L;
            } else if (i == 59) {
                if (str2.equals("currentColor")) {
                    style.solidColor = SVG.CurrentColor.getInstance();
                } else {
                    style.solidColor = parseColour(str2);
                }
                style.specifiedFlags |= 2147483648L;
            } else if (i == 60) {
                style.solidOpacity = Float.valueOf(parseOpacity(str2));
                style.specifiedFlags |= 4294967296L;
            } else if (i == 75) {
                style.textAnchor = parseTextAnchor(str2);
                style.specifiedFlags |= 262144;
            } else if (i != 76) {
                switch (i) {
                    case 15:
                        if (str2.indexOf(androidx.appcompat.R$styleable.AppCompatTheme_windowMinWidthMajor) < 0) {
                            if ("|inline|block|list-item|run-in|compact|marker|table|inline-table|table-row-group|table-header-group|table-footer-group|table-row|table-column-group|table-column|table-cell|table-caption|none|".indexOf(String.valueOf('|') + str2 + '|') != -1) {
                                style.display = Boolean.valueOf(!str2.equals("none"));
                                style.specifiedFlags |= 16777216;
                                return;
                            }
                        }
                        throw new SAXException("Invalid value for \"display\" attribute: " + str2);
                    case 16:
                        style.fill = parsePaintSpecifier(str2, "fill");
                        style.specifiedFlags |= 1;
                        return;
                    case 17:
                        style.fillRule = parseFillRule(str2);
                        style.specifiedFlags |= 2;
                        return;
                    case 18:
                        style.fillOpacity = Float.valueOf(parseOpacity(str2));
                        style.specifiedFlags |= 4;
                        return;
                    case 19:
                        parseFont(style, str2);
                        return;
                    case 20:
                        style.fontFamily = parseFontFamily(str2);
                        style.specifiedFlags |= 8192;
                        return;
                    case 21:
                        style.fontSize = parseFontSize(str2);
                        style.specifiedFlags |= 16384;
                        return;
                    case 22:
                        style.fontWeight = parseFontWeight(str2);
                        style.specifiedFlags |= 32768;
                        return;
                    case 23:
                        style.fontStyle = parseFontStyle(str2);
                        style.specifiedFlags |= 65536;
                        return;
                    default:
                        switch (i) {
                            case 29:
                                String parseFunctionalIRI = parseFunctionalIRI(str2, str);
                                style.markerStart = parseFunctionalIRI;
                                style.markerMid = parseFunctionalIRI;
                                style.markerEnd = parseFunctionalIRI;
                                style.specifiedFlags |= 14680064;
                                return;
                            case 30:
                                style.markerStart = parseFunctionalIRI(str2, str);
                                style.specifiedFlags |= 2097152;
                                return;
                            case 31:
                                style.markerMid = parseFunctionalIRI(str2, str);
                                style.specifiedFlags |= 4194304;
                                return;
                            case 32:
                                style.markerEnd = parseFunctionalIRI(str2, str);
                                style.specifiedFlags |= 8388608;
                                return;
                            default:
                                switch (i) {
                                    case 63:
                                        if (str2.equals("currentColor")) {
                                            style.stopColor = SVG.CurrentColor.getInstance();
                                        } else {
                                            style.stopColor = parseColour(str2);
                                        }
                                        style.specifiedFlags |= 67108864;
                                        return;
                                    case 64:
                                        style.stopOpacity = Float.valueOf(parseOpacity(str2));
                                        style.specifiedFlags |= 134217728;
                                        return;
                                    case 65:
                                        style.stroke = parsePaintSpecifier(str2, "stroke");
                                        style.specifiedFlags |= 8;
                                        return;
                                    case 66:
                                        if ("none".equals(str2)) {
                                            style.strokeDashArray = null;
                                        } else {
                                            style.strokeDashArray = parseStrokeDashArray(str2);
                                        }
                                        style.specifiedFlags |= 512;
                                        return;
                                    case 67:
                                        style.strokeDashOffset = parseLength(str2);
                                        style.specifiedFlags |= 1024;
                                        return;
                                    case 68:
                                        style.strokeLineCap = parseStrokeLineCap(str2);
                                        style.specifiedFlags |= 64;
                                        return;
                                    case 69:
                                        style.strokeLineJoin = parseStrokeLineJoin(str2);
                                        style.specifiedFlags |= 128;
                                        return;
                                    case 70:
                                        style.strokeMiterLimit = Float.valueOf(parseFloat(str2));
                                        style.specifiedFlags |= 256;
                                        return;
                                    case 71:
                                        style.strokeOpacity = Float.valueOf(parseOpacity(str2));
                                        style.specifiedFlags |= 16;
                                        return;
                                    case 72:
                                        style.strokeWidth = parseLength(str2);
                                        style.specifiedFlags |= 32;
                                        return;
                                    default:
                                        switch (i) {
                                            case 89:
                                                if (str2.equals("currentColor")) {
                                                    style.viewportFill = SVG.CurrentColor.getInstance();
                                                } else {
                                                    style.viewportFill = parseColour(str2);
                                                }
                                                style.specifiedFlags |= 8589934592L;
                                                return;
                                            case R$styleable.Constraint_layout_constraintVertical_chainStyle /* 90 */:
                                                style.viewportFillOpacity = Float.valueOf(parseOpacity(str2));
                                                style.specifiedFlags |= 17179869184L;
                                                return;
                                            case R$styleable.Constraint_layout_constraintVertical_weight /* 91 */:
                                                if (str2.indexOf(androidx.appcompat.R$styleable.AppCompatTheme_windowMinWidthMajor) < 0) {
                                                    if ("|visible|hidden|collapse|".indexOf(String.valueOf('|') + str2 + '|') != -1) {
                                                        style.visibility = Boolean.valueOf(str2.equals("visible"));
                                                        style.specifiedFlags |= 33554432;
                                                        return;
                                                    }
                                                }
                                                throw new SAXException("Invalid value for \"visibility\" attribute: " + str2);
                                            default:
                                                return;
                                        }
                                }
                        }
                }
            } else {
                style.textDecoration = parseTextDecoration(str2);
                style.specifiedFlags |= 131072;
            }
        }
    }

    private void parseAttributesViewBox(SVG.SvgViewBoxContainer svgViewBoxContainer, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            String trim = attributes.getValue(i).trim();
            int i2 = $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()];
            if (i2 == 49) {
                parsePreserveAspectRatio(svgViewBoxContainer, trim);
            } else if (i2 == 81) {
                svgViewBoxContainer.viewBox = parseViewBox(trim);
            }
        }
    }

    private void parseAttributesTransform(SVG.HasTransform hasTransform, Attributes attributes) throws SAXException {
        for (int i = 0; i < attributes.getLength(); i++) {
            if (SVGAttr.fromString(attributes.getLocalName(i)) == SVGAttr.transform) {
                hasTransform.setTransform(parseTransformList(attributes.getValue(i)));
            }
        }
    }

    private Matrix parseTransformList(String str) throws SAXException {
        Matrix matrix = new Matrix();
        TextScanner textScanner = new TextScanner(str);
        textScanner.skipWhitespace();
        while (!textScanner.empty()) {
            String nextFunction = textScanner.nextFunction();
            if (nextFunction != null) {
                if (nextFunction.equals("matrix")) {
                    textScanner.skipWhitespace();
                    Float nextFloat = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat2 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat3 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat4 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat5 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat6 = textScanner.nextFloat();
                    textScanner.skipWhitespace();
                    if (nextFloat6 == null || !textScanner.consume(')')) {
                        throw new SAXException("Invalid transform list: " + str);
                    }
                    Matrix matrix2 = new Matrix();
                    matrix2.setValues(new float[]{nextFloat.floatValue(), nextFloat3.floatValue(), nextFloat5.floatValue(), nextFloat2.floatValue(), nextFloat4.floatValue(), nextFloat6.floatValue(), 0.0f, 0.0f, 1.0f});
                    matrix.preConcat(matrix2);
                } else if (nextFunction.equals("translate")) {
                    textScanner.skipWhitespace();
                    Float nextFloat7 = textScanner.nextFloat();
                    Float possibleNextFloat = textScanner.possibleNextFloat();
                    textScanner.skipWhitespace();
                    if (nextFloat7 == null || !textScanner.consume(')')) {
                        throw new SAXException("Invalid transform list: " + str);
                    } else if (possibleNextFloat == null) {
                        matrix.preTranslate(nextFloat7.floatValue(), 0.0f);
                    } else {
                        matrix.preTranslate(nextFloat7.floatValue(), possibleNextFloat.floatValue());
                    }
                } else if (nextFunction.equals("scale")) {
                    textScanner.skipWhitespace();
                    Float nextFloat8 = textScanner.nextFloat();
                    Float possibleNextFloat2 = textScanner.possibleNextFloat();
                    textScanner.skipWhitespace();
                    if (nextFloat8 == null || !textScanner.consume(')')) {
                        throw new SAXException("Invalid transform list: " + str);
                    } else if (possibleNextFloat2 == null) {
                        matrix.preScale(nextFloat8.floatValue(), nextFloat8.floatValue());
                    } else {
                        matrix.preScale(nextFloat8.floatValue(), possibleNextFloat2.floatValue());
                    }
                } else if (nextFunction.equals("rotate")) {
                    textScanner.skipWhitespace();
                    Float nextFloat9 = textScanner.nextFloat();
                    Float possibleNextFloat3 = textScanner.possibleNextFloat();
                    Float possibleNextFloat4 = textScanner.possibleNextFloat();
                    textScanner.skipWhitespace();
                    if (nextFloat9 == null || !textScanner.consume(')')) {
                        throw new SAXException("Invalid transform list: " + str);
                    } else if (possibleNextFloat3 == null) {
                        matrix.preRotate(nextFloat9.floatValue());
                    } else if (possibleNextFloat4 != null) {
                        matrix.preRotate(nextFloat9.floatValue(), possibleNextFloat3.floatValue(), possibleNextFloat4.floatValue());
                    } else {
                        throw new SAXException("Invalid transform list: " + str);
                    }
                } else if (nextFunction.equals("skewX")) {
                    textScanner.skipWhitespace();
                    Float nextFloat10 = textScanner.nextFloat();
                    textScanner.skipWhitespace();
                    if (nextFloat10 == null || !textScanner.consume(')')) {
                        throw new SAXException("Invalid transform list: " + str);
                    }
                    matrix.preSkew((float) Math.tan(Math.toRadians((double) nextFloat10.floatValue())), 0.0f);
                } else if (nextFunction.equals("skewY")) {
                    textScanner.skipWhitespace();
                    Float nextFloat11 = textScanner.nextFloat();
                    textScanner.skipWhitespace();
                    if (nextFloat11 == null || !textScanner.consume(')')) {
                        throw new SAXException("Invalid transform list: " + str);
                    }
                    matrix.preSkew(0.0f, (float) Math.tan(Math.toRadians((double) nextFloat11.floatValue())));
                } else if (nextFunction != null) {
                    throw new SAXException("Invalid transform list fn: " + nextFunction + ")");
                }
                if (textScanner.empty()) {
                    break;
                }
                textScanner.skipCommaWhitespace();
            } else {
                throw new SAXException("Bad transform function encountered in transform list: " + str);
            }
        }
        return matrix;
    }

    protected static SVG.Length parseLength(String str) throws SAXException {
        if (str.length() != 0) {
            int length = str.length();
            SVG.Unit unit = SVG.Unit.px;
            char charAt = str.charAt(length - 1);
            if (charAt == '%') {
                length--;
                unit = SVG.Unit.percent;
            } else if (length > 2 && Character.isLetter(charAt) && Character.isLetter(str.charAt(length - 2))) {
                length -= 2;
                try {
                    unit = SVG.Unit.valueOf(str.substring(length).toLowerCase(Locale.US));
                } catch (IllegalArgumentException unused) {
                    throw new SAXException("Invalid length unit specifier: " + str);
                }
            }
            try {
                return new SVG.Length(Float.parseFloat(str.substring(0, length)), unit);
            } catch (NumberFormatException e) {
                throw new SAXException("Invalid length value: " + str, e);
            }
        } else {
            throw new SAXException("Invalid length value (empty string)");
        }
    }

    private static List<SVG.Length> parseLengthList(String str) throws SAXException {
        if (str.length() != 0) {
            ArrayList arrayList = new ArrayList(1);
            TextScanner textScanner = new TextScanner(str);
            textScanner.skipWhitespace();
            while (!textScanner.empty()) {
                Float nextFloat = textScanner.nextFloat();
                if (nextFloat != null) {
                    SVG.Unit nextUnit = textScanner.nextUnit();
                    if (nextUnit == null) {
                        nextUnit = SVG.Unit.px;
                    }
                    arrayList.add(new SVG.Length(nextFloat.floatValue(), nextUnit));
                    textScanner.skipCommaWhitespace();
                } else {
                    throw new SAXException("Invalid length list value: " + textScanner.ahead());
                }
            }
            return arrayList;
        }
        throw new SAXException("Invalid length list (empty string)");
    }

    private static float parseFloat(String str) throws SAXException {
        if (str.length() != 0) {
            try {
                return Float.parseFloat(str);
            } catch (NumberFormatException e) {
                throw new SAXException("Invalid float value: " + str, e);
            }
        } else {
            throw new SAXException("Invalid float value (empty string)");
        }
    }

    private static float parseOpacity(String str) throws SAXException {
        float parseFloat = parseFloat(str);
        if (parseFloat < 0.0f) {
            return 0.0f;
        }
        if (parseFloat > 1.0f) {
            return 1.0f;
        }
        return parseFloat;
    }

    private static SVG.Box parseViewBox(String str) throws SAXException {
        TextScanner textScanner = new TextScanner(str);
        textScanner.skipWhitespace();
        Float nextFloat = textScanner.nextFloat();
        textScanner.skipCommaWhitespace();
        Float nextFloat2 = textScanner.nextFloat();
        textScanner.skipCommaWhitespace();
        Float nextFloat3 = textScanner.nextFloat();
        textScanner.skipCommaWhitespace();
        Float nextFloat4 = textScanner.nextFloat();
        if (nextFloat == null || nextFloat2 == null || nextFloat3 == null || nextFloat4 == null) {
            throw new SAXException("Invalid viewBox definition - should have four numbers");
        } else if (nextFloat3.floatValue() < 0.0f) {
            throw new SAXException("Invalid viewBox. width cannot be negative");
        } else if (nextFloat4.floatValue() >= 0.0f) {
            return new SVG.Box(nextFloat.floatValue(), nextFloat2.floatValue(), nextFloat3.floatValue(), nextFloat4.floatValue());
        } else {
            throw new SAXException("Invalid viewBox. height cannot be negative");
        }
    }

    private static void parsePreserveAspectRatio(SVG.SvgPreserveAspectRatioContainer svgPreserveAspectRatioContainer, String str) throws SAXException {
        PreserveAspectRatio.Scale scale;
        TextScanner textScanner = new TextScanner(str);
        textScanner.skipWhitespace();
        String nextToken = textScanner.nextToken();
        if ("defer".equals(nextToken)) {
            textScanner.skipWhitespace();
            nextToken = textScanner.nextToken();
        }
        PreserveAspectRatio.Alignment alignment = aspectRatioKeywords.get(nextToken);
        textScanner.skipWhitespace();
        if (!textScanner.empty()) {
            String nextToken2 = textScanner.nextToken();
            if (nextToken2.equals("meet")) {
                scale = PreserveAspectRatio.Scale.Meet;
            } else if (nextToken2.equals("slice")) {
                scale = PreserveAspectRatio.Scale.Slice;
            } else {
                throw new SAXException("Invalid preserveAspectRatio definition: " + str);
            }
        } else {
            scale = null;
        }
        svgPreserveAspectRatioContainer.preserveAspectRatio = new PreserveAspectRatio(alignment, scale);
    }

    private static SVG.SvgPaint parsePaintSpecifier(String str, String str2) throws SAXException {
        if (!str.startsWith("url(")) {
            return parseColourSpecifer(str);
        }
        int indexOf = str.indexOf(")");
        if (indexOf != -1) {
            String trim = str.substring(4, indexOf).trim();
            SVG.SvgPaint svgPaint = null;
            String trim2 = str.substring(indexOf + 1).trim();
            if (trim2.length() > 0) {
                svgPaint = parseColourSpecifer(trim2);
            }
            return new SVG.PaintReference(trim, svgPaint);
        }
        throw new SAXException("Bad " + str2 + " attribute. Unterminated url() reference");
    }

    private static SVG.SvgPaint parseColourSpecifer(String str) throws SAXException {
        if (str.equals("none")) {
            return null;
        }
        if (str.equals("currentColor")) {
            return SVG.CurrentColor.getInstance();
        }
        return parseColour(str);
    }

    private static SVG.Colour parseColour(String str) throws SAXException {
        if (str.charAt(0) == '#') {
            try {
                if (str.length() == 7) {
                    return new SVG.Colour(Integer.parseInt(str.substring(1), 16));
                }
                if (str.length() == 4) {
                    int parseInt = Integer.parseInt(str.substring(1), 16);
                    int i = parseInt & 3840;
                    int i2 = parseInt & 240;
                    int i3 = parseInt & 15;
                    return new SVG.Colour(i3 | (i << 12) | (i << 16) | (i2 << 8) | (i2 << 4) | (i3 << 4));
                }
                throw new SAXException("Bad hex colour value: " + str);
            } catch (NumberFormatException unused) {
                throw new SAXException("Bad colour value: " + str);
            }
        } else if (!str.toLowerCase(Locale.US).startsWith("rgb(")) {
            return parseColourKeyword(str);
        } else {
            TextScanner textScanner = new TextScanner(str.substring(4));
            textScanner.skipWhitespace();
            int parseColourComponent = parseColourComponent(textScanner);
            textScanner.skipCommaWhitespace();
            int parseColourComponent2 = parseColourComponent(textScanner);
            textScanner.skipCommaWhitespace();
            int parseColourComponent3 = parseColourComponent(textScanner);
            textScanner.skipWhitespace();
            if (textScanner.consume(')')) {
                return new SVG.Colour((parseColourComponent << 16) | (parseColourComponent2 << 8) | parseColourComponent3);
            }
            throw new SAXException("Bad rgb() colour value: " + str);
        }
    }

    private static int parseColourComponent(TextScanner textScanner) throws SAXException {
        int intValue = textScanner.nextInteger().intValue();
        if (textScanner.consume('%')) {
            if (intValue < 0) {
                intValue = 0;
            } else if (intValue > 100) {
                intValue = 100;
            }
            return (intValue * 255) / 100;
        } else if (intValue < 0) {
            return 0;
        } else {
            if (intValue > 255) {
                return 255;
            }
            return intValue;
        }
    }

    private static SVG.Colour parseColourKeyword(String str) throws SAXException {
        Integer num = colourKeywords.get(str.toLowerCase(Locale.US));
        if (num != null) {
            return new SVG.Colour(num.intValue());
        }
        throw new SAXException("Invalid colour keyword: " + str);
    }

    private static void parseFont(SVG.Style style, String str) throws SAXException {
        String nextToken;
        int i;
        if ("|caption|icon|menu|message-box|small-caption|status-bar|".indexOf(String.valueOf('|') + str + '|') == -1) {
            TextScanner textScanner = new TextScanner(str);
            Integer num = null;
            SVG.Style.FontStyle fontStyle = null;
            String str2 = null;
            while (true) {
                nextToken = textScanner.nextToken('/');
                textScanner.skipWhitespace();
                if (nextToken != null) {
                    if (num == null || fontStyle == null) {
                        if (!nextToken.equals("normal") && ((num != null || (num = fontWeightKeywords.get(nextToken)) == null) && (fontStyle != null || (fontStyle = fontStyleKeywords.get(nextToken)) == null))) {
                            if (str2 != null || !nextToken.equals("small-caps")) {
                                break;
                            }
                            str2 = nextToken;
                        }
                    } else {
                        break;
                    }
                } else {
                    throw new SAXException("Invalid font style attribute: missing font size and family");
                }
            }
            SVG.Length parseFontSize = parseFontSize(nextToken);
            if (textScanner.consume('/')) {
                textScanner.skipWhitespace();
                String nextToken2 = textScanner.nextToken();
                if (nextToken2 != null) {
                    parseLength(nextToken2);
                    textScanner.skipWhitespace();
                } else {
                    throw new SAXException("Invalid font style attribute: missing line-height");
                }
            }
            style.fontFamily = parseFontFamily(textScanner.restOfText());
            style.fontSize = parseFontSize;
            if (num == null) {
                i = UseOpenWifiPreferenceController.REQUEST_CODE_OPEN_WIFI_AUTOMATICALLY;
            } else {
                i = num.intValue();
            }
            style.fontWeight = Integer.valueOf(i);
            if (fontStyle == null) {
                fontStyle = SVG.Style.FontStyle.Normal;
            }
            style.fontStyle = fontStyle;
            style.specifiedFlags |= 122880;
        }
    }

    private static List<String> parseFontFamily(String str) throws SAXException {
        TextScanner textScanner = new TextScanner(str);
        ArrayList arrayList = null;
        do {
            String nextQuotedString = textScanner.nextQuotedString();
            if (nextQuotedString == null) {
                nextQuotedString = textScanner.nextToken(',');
            }
            if (nextQuotedString == null) {
                break;
            }
            if (arrayList == null) {
                arrayList = new ArrayList();
            }
            arrayList.add(nextQuotedString);
            textScanner.skipCommaWhitespace();
        } while (!textScanner.empty());
        return arrayList;
    }

    private static SVG.Length parseFontSize(String str) throws SAXException {
        SVG.Length length = fontSizeKeywords.get(str);
        return length == null ? parseLength(str) : length;
    }

    private static Integer parseFontWeight(String str) throws SAXException {
        Integer num = fontWeightKeywords.get(str);
        if (num != null) {
            return num;
        }
        throw new SAXException("Invalid font-weight property: " + str);
    }

    private static SVG.Style.FontStyle parseFontStyle(String str) throws SAXException {
        SVG.Style.FontStyle fontStyle = fontStyleKeywords.get(str);
        if (fontStyle != null) {
            return fontStyle;
        }
        throw new SAXException("Invalid font-style property: " + str);
    }

    private static SVG.Style.TextDecoration parseTextDecoration(String str) throws SAXException {
        if ("none".equals(str)) {
            return SVG.Style.TextDecoration.None;
        }
        if ("underline".equals(str)) {
            return SVG.Style.TextDecoration.Underline;
        }
        if ("overline".equals(str)) {
            return SVG.Style.TextDecoration.Overline;
        }
        if ("line-through".equals(str)) {
            return SVG.Style.TextDecoration.LineThrough;
        }
        if ("blink".equals(str)) {
            return SVG.Style.TextDecoration.Blink;
        }
        throw new SAXException("Invalid text-decoration property: " + str);
    }

    private static SVG.Style.TextDirection parseTextDirection(String str) throws SAXException {
        if ("ltr".equals(str)) {
            return SVG.Style.TextDirection.LTR;
        }
        if ("rtl".equals(str)) {
            return SVG.Style.TextDirection.RTL;
        }
        throw new SAXException("Invalid direction property: " + str);
    }

    private static SVG.Style.FillRule parseFillRule(String str) throws SAXException {
        if ("nonzero".equals(str)) {
            return SVG.Style.FillRule.NonZero;
        }
        if ("evenodd".equals(str)) {
            return SVG.Style.FillRule.EvenOdd;
        }
        throw new SAXException("Invalid fill-rule property: " + str);
    }

    private static SVG.Style.LineCaps parseStrokeLineCap(String str) throws SAXException {
        if ("butt".equals(str)) {
            return SVG.Style.LineCaps.Butt;
        }
        if ("round".equals(str)) {
            return SVG.Style.LineCaps.Round;
        }
        if ("square".equals(str)) {
            return SVG.Style.LineCaps.Square;
        }
        throw new SAXException("Invalid stroke-linecap property: " + str);
    }

    private static SVG.Style.LineJoin parseStrokeLineJoin(String str) throws SAXException {
        if ("miter".equals(str)) {
            return SVG.Style.LineJoin.Miter;
        }
        if ("round".equals(str)) {
            return SVG.Style.LineJoin.Round;
        }
        if ("bevel".equals(str)) {
            return SVG.Style.LineJoin.Bevel;
        }
        throw new SAXException("Invalid stroke-linejoin property: " + str);
    }

    private static SVG.Length[] parseStrokeDashArray(String str) throws SAXException {
        SVG.Length nextLength;
        TextScanner textScanner = new TextScanner(str);
        textScanner.skipWhitespace();
        if (textScanner.empty() || (nextLength = textScanner.nextLength()) == null) {
            return null;
        }
        if (!nextLength.isNegative()) {
            float floatValue = nextLength.floatValue();
            ArrayList arrayList = new ArrayList();
            arrayList.add(nextLength);
            while (!textScanner.empty()) {
                textScanner.skipCommaWhitespace();
                SVG.Length nextLength2 = textScanner.nextLength();
                if (nextLength2 == null) {
                    throw new SAXException("Invalid stroke-dasharray. Non-Length content found: " + str);
                } else if (!nextLength2.isNegative()) {
                    arrayList.add(nextLength2);
                    floatValue += nextLength2.floatValue();
                } else {
                    throw new SAXException("Invalid stroke-dasharray. Dash segemnts cannot be negative: " + str);
                }
            }
            if (floatValue == 0.0f) {
                return null;
            }
            return (SVG.Length[]) arrayList.toArray(new SVG.Length[arrayList.size()]);
        }
        throw new SAXException("Invalid stroke-dasharray. Dash segemnts cannot be negative: " + str);
    }

    private static SVG.Style.TextAnchor parseTextAnchor(String str) throws SAXException {
        if ("start".equals(str)) {
            return SVG.Style.TextAnchor.Start;
        }
        if ("middle".equals(str)) {
            return SVG.Style.TextAnchor.Middle;
        }
        if ("end".equals(str)) {
            return SVG.Style.TextAnchor.End;
        }
        throw new SAXException("Invalid text-anchor property: " + str);
    }

    private static Boolean parseOverflow(String str) throws SAXException {
        if ("visible".equals(str) || "auto".equals(str)) {
            return Boolean.TRUE;
        }
        if ("hidden".equals(str) || "scroll".equals(str)) {
            return Boolean.FALSE;
        }
        throw new SAXException("Invalid toverflow property: " + str);
    }

    private static SVG.CSSClipRect parseClip(String str) throws SAXException {
        if ("auto".equals(str)) {
            return null;
        }
        if (str.toLowerCase(Locale.US).startsWith("rect(")) {
            TextScanner textScanner = new TextScanner(str.substring(5));
            textScanner.skipWhitespace();
            SVG.Length parseLengthOrAuto = parseLengthOrAuto(textScanner);
            textScanner.skipCommaWhitespace();
            SVG.Length parseLengthOrAuto2 = parseLengthOrAuto(textScanner);
            textScanner.skipCommaWhitespace();
            SVG.Length parseLengthOrAuto3 = parseLengthOrAuto(textScanner);
            textScanner.skipCommaWhitespace();
            SVG.Length parseLengthOrAuto4 = parseLengthOrAuto(textScanner);
            textScanner.skipWhitespace();
            if (textScanner.consume(')')) {
                return new SVG.CSSClipRect(parseLengthOrAuto, parseLengthOrAuto2, parseLengthOrAuto3, parseLengthOrAuto4);
            }
            throw new SAXException("Bad rect() clip definition: " + str);
        }
        throw new SAXException("Invalid clip attribute shape. Only rect() is supported.");
    }

    private static SVG.Length parseLengthOrAuto(TextScanner textScanner) {
        if (textScanner.consume("auto")) {
            return new SVG.Length(0.0f);
        }
        return textScanner.nextLength();
    }

    private static SVG.Style.VectorEffect parseVectorEffect(String str) throws SAXException {
        if ("none".equals(str)) {
            return SVG.Style.VectorEffect.None;
        }
        if ("non-scaling-stroke".equals(str)) {
            return SVG.Style.VectorEffect.NonScalingStroke;
        }
        throw new SAXException("Invalid vector-effect property: " + str);
    }

    private static SVG.PathDefinition parsePath(String str) throws SAXException {
        TextScanner textScanner = new TextScanner(str);
        SVG.PathDefinition pathDefinition = new SVG.PathDefinition();
        if (textScanner.empty()) {
            return pathDefinition;
        }
        int intValue = textScanner.nextChar().intValue();
        if (intValue != 77 && intValue != 109) {
            return pathDefinition;
        }
        int i = intValue;
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        float f4 = 0.0f;
        float f5 = 0.0f;
        float f6 = 0.0f;
        while (true) {
            textScanner.skipWhitespace();
            int i2 = R$styleable.Constraint_transitionEasing;
            switch (i) {
                case 65:
                case R$styleable.Constraint_layout_editor_absoluteY /* 97 */:
                    Float nextFloat = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat2 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat3 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Boolean nextFlag = textScanner.nextFlag();
                    textScanner.skipCommaWhitespace();
                    Boolean nextFlag2 = textScanner.nextFlag();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat4 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat5 = textScanner.nextFloat();
                    if (nextFloat5 != null && nextFloat.floatValue() >= 0.0f && nextFloat2.floatValue() >= 0.0f) {
                        if (i == 97) {
                            nextFloat4 = Float.valueOf(nextFloat4.floatValue() + f);
                            nextFloat5 = Float.valueOf(nextFloat5.floatValue() + f3);
                        }
                        pathDefinition.arcTo(nextFloat.floatValue(), nextFloat2.floatValue(), nextFloat3.floatValue(), nextFlag.booleanValue(), nextFlag2.booleanValue(), nextFloat4.floatValue(), nextFloat5.floatValue());
                        f = nextFloat4.floatValue();
                        f3 = nextFloat5.floatValue();
                        f2 = f;
                        f4 = f3;
                        break;
                    } else {
                        Log.e("SVGParser", "Bad path coords for " + i + " path segment");
                        break;
                    }
                case 67:
                case R$styleable.Constraint_layout_goneMarginEnd /* 99 */:
                    Float nextFloat6 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat7 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat8 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat9 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat10 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat11 = textScanner.nextFloat();
                    if (nextFloat11 != null) {
                        if (i == 99) {
                            nextFloat10 = Float.valueOf(nextFloat10.floatValue() + f);
                            nextFloat11 = Float.valueOf(nextFloat11.floatValue() + f3);
                            nextFloat6 = Float.valueOf(nextFloat6.floatValue() + f);
                            nextFloat7 = Float.valueOf(nextFloat7.floatValue() + f3);
                            nextFloat8 = Float.valueOf(nextFloat8.floatValue() + f);
                            nextFloat9 = Float.valueOf(nextFloat9.floatValue() + f3);
                        }
                        pathDefinition.cubicTo(nextFloat6.floatValue(), nextFloat7.floatValue(), nextFloat8.floatValue(), nextFloat9.floatValue(), nextFloat10.floatValue(), nextFloat11.floatValue());
                        f2 = nextFloat8.floatValue();
                        f4 = nextFloat9.floatValue();
                        f = nextFloat10.floatValue();
                        f3 = nextFloat11.floatValue();
                        break;
                    } else {
                        Log.e("SVGParser", "Bad path coords for " + i + " path segment");
                        return pathDefinition;
                    }
                case 72:
                case R$styleable.Constraint_motionStagger /* 104 */:
                    Float nextFloat12 = textScanner.nextFloat();
                    if (nextFloat12 != null) {
                        if (i == 104) {
                            nextFloat12 = Float.valueOf(nextFloat12.floatValue() + f);
                        }
                        pathDefinition.lineTo(nextFloat12.floatValue(), f3);
                        f = nextFloat12.floatValue();
                        f2 = f;
                        break;
                    } else {
                        Log.e("SVGParser", "Bad path coords for " + i + " path segment");
                        return pathDefinition;
                    }
                case 76:
                case R$styleable.Constraint_transitionEasing /* 108 */:
                    Float nextFloat13 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat14 = textScanner.nextFloat();
                    if (nextFloat14 != null) {
                        if (i == 108) {
                            nextFloat13 = Float.valueOf(nextFloat13.floatValue() + f);
                            nextFloat14 = Float.valueOf(nextFloat14.floatValue() + f3);
                        }
                        pathDefinition.lineTo(nextFloat13.floatValue(), nextFloat14.floatValue());
                        f = nextFloat13.floatValue();
                        f3 = nextFloat14.floatValue();
                        f2 = f;
                        f4 = f3;
                        break;
                    } else {
                        Log.e("SVGParser", "Bad path coords for " + i + " path segment");
                        return pathDefinition;
                    }
                case 77:
                case R$styleable.Constraint_transitionPathRotate /* 109 */:
                    Float nextFloat15 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat16 = textScanner.nextFloat();
                    if (nextFloat16 != null) {
                        if (i == 109 && !pathDefinition.isEmpty()) {
                            nextFloat15 = Float.valueOf(nextFloat15.floatValue() + f);
                            nextFloat16 = Float.valueOf(nextFloat16.floatValue() + f3);
                        }
                        pathDefinition.moveTo(nextFloat15.floatValue(), nextFloat16.floatValue());
                        f = nextFloat15.floatValue();
                        f3 = nextFloat16.floatValue();
                        if (i != 109) {
                            i2 = 76;
                        }
                        f2 = f;
                        f5 = f2;
                        f4 = f3;
                        f6 = f4;
                        i = i2;
                        break;
                    } else {
                        Log.e("SVGParser", "Bad path coords for " + i + " path segment");
                        return pathDefinition;
                    }
                case 81:
                case 113:
                    Float nextFloat17 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat18 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat19 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat20 = textScanner.nextFloat();
                    if (nextFloat20 != null) {
                        if (i == 113) {
                            nextFloat19 = Float.valueOf(nextFloat19.floatValue() + f);
                            nextFloat20 = Float.valueOf(nextFloat20.floatValue() + f3);
                            nextFloat17 = Float.valueOf(nextFloat17.floatValue() + f);
                            nextFloat18 = Float.valueOf(nextFloat18.floatValue() + f3);
                        }
                        pathDefinition.quadTo(nextFloat17.floatValue(), nextFloat18.floatValue(), nextFloat19.floatValue(), nextFloat20.floatValue());
                        f2 = nextFloat17.floatValue();
                        f4 = nextFloat18.floatValue();
                        f = nextFloat19.floatValue();
                        f3 = nextFloat20.floatValue();
                        break;
                    } else {
                        Log.e("SVGParser", "Bad path coords for " + i + " path segment");
                        return pathDefinition;
                    }
                case 83:
                case 115:
                    Float valueOf = Float.valueOf((f * 2.0f) - f2);
                    Float valueOf2 = Float.valueOf((2.0f * f3) - f4);
                    Float nextFloat21 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat22 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat23 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat24 = textScanner.nextFloat();
                    if (nextFloat24 != null) {
                        if (i == 115) {
                            nextFloat23 = Float.valueOf(nextFloat23.floatValue() + f);
                            nextFloat24 = Float.valueOf(nextFloat24.floatValue() + f3);
                            nextFloat21 = Float.valueOf(nextFloat21.floatValue() + f);
                            nextFloat22 = Float.valueOf(nextFloat22.floatValue() + f3);
                        }
                        pathDefinition.cubicTo(valueOf.floatValue(), valueOf2.floatValue(), nextFloat21.floatValue(), nextFloat22.floatValue(), nextFloat23.floatValue(), nextFloat24.floatValue());
                        f2 = nextFloat21.floatValue();
                        f4 = nextFloat22.floatValue();
                        f = nextFloat23.floatValue();
                        f3 = nextFloat24.floatValue();
                        break;
                    } else {
                        Log.e("SVGParser", "Bad path coords for " + i + " path segment");
                        return pathDefinition;
                    }
                case 84:
                case androidx.appcompat.R$styleable.AppCompatTheme_viewInflaterClass /* 116 */:
                    Float valueOf3 = Float.valueOf((f * 2.0f) - f2);
                    Float valueOf4 = Float.valueOf((2.0f * f3) - f4);
                    Float nextFloat25 = textScanner.nextFloat();
                    textScanner.skipCommaWhitespace();
                    Float nextFloat26 = textScanner.nextFloat();
                    if (nextFloat26 != null) {
                        if (i == 116) {
                            nextFloat25 = Float.valueOf(nextFloat25.floatValue() + f);
                            nextFloat26 = Float.valueOf(nextFloat26.floatValue() + f3);
                        }
                        pathDefinition.quadTo(valueOf3.floatValue(), valueOf4.floatValue(), nextFloat25.floatValue(), nextFloat26.floatValue());
                        f2 = valueOf3.floatValue();
                        f4 = valueOf4.floatValue();
                        f = nextFloat25.floatValue();
                        f3 = nextFloat26.floatValue();
                        break;
                    } else {
                        Log.e("SVGParser", "Bad path coords for " + i + " path segment");
                        return pathDefinition;
                    }
                case 86:
                case androidx.appcompat.R$styleable.AppCompatTheme_windowActionBarOverlay /* 118 */:
                    Float nextFloat27 = textScanner.nextFloat();
                    if (nextFloat27 != null) {
                        if (i == 118) {
                            nextFloat27 = Float.valueOf(nextFloat27.floatValue() + f3);
                        }
                        pathDefinition.lineTo(f, nextFloat27.floatValue());
                        f3 = nextFloat27.floatValue();
                        f4 = f3;
                        break;
                    } else {
                        Log.e("SVGParser", "Bad path coords for " + i + " path segment");
                        return pathDefinition;
                    }
                case R$styleable.Constraint_layout_constraintVertical_chainStyle /* 90 */:
                case androidx.appcompat.R$styleable.AppCompatTheme_windowFixedWidthMajor /* 122 */:
                    pathDefinition.close();
                    f = f5;
                    f2 = f;
                    f3 = f6;
                    f4 = f3;
                    break;
                default:
                    return pathDefinition;
            }
            textScanner.skipWhitespace();
            if (textScanner.empty()) {
                return pathDefinition;
            }
            if (textScanner.hasLetter()) {
                i = textScanner.nextChar().intValue();
            }
        }
        Log.e("SVGParser", "Bad path coords for " + i + " path segment");
        return pathDefinition;
    }

    private static Set<String> parseRequiredFeatures(String str) throws SAXException {
        TextScanner textScanner = new TextScanner(str);
        HashSet hashSet = new HashSet();
        while (!textScanner.empty()) {
            String nextToken = textScanner.nextToken();
            if (nextToken.startsWith("http://www.w3.org/TR/SVG11/feature#")) {
                hashSet.add(nextToken.substring(35));
            } else {
                hashSet.add("UNSUPPORTED");
            }
            textScanner.skipWhitespace();
        }
        return hashSet;
    }

    private static Set<String> parseSystemLanguage(String str) throws SAXException {
        TextScanner textScanner = new TextScanner(str);
        HashSet hashSet = new HashSet();
        while (!textScanner.empty()) {
            String nextToken = textScanner.nextToken();
            int indexOf = nextToken.indexOf(45);
            if (indexOf != -1) {
                nextToken = nextToken.substring(0, indexOf);
            }
            hashSet.add(new Locale(nextToken, "", "").getLanguage());
            textScanner.skipWhitespace();
        }
        return hashSet;
    }

    private static Set<String> parseRequiredFormats(String str) throws SAXException {
        TextScanner textScanner = new TextScanner(str);
        HashSet hashSet = new HashSet();
        while (!textScanner.empty()) {
            hashSet.add(textScanner.nextToken());
            textScanner.skipWhitespace();
        }
        return hashSet;
    }

    private static String parseFunctionalIRI(String str, String str2) throws SAXException {
        if (str.equals("none")) {
            return null;
        }
        if (str.startsWith("url(") && str.endsWith(")")) {
            return str.substring(4, str.length() - 1).trim();
        }
        throw new SAXException("Bad " + str2 + " attribute. Expected \"none\" or \"url()\" format");
    }

    private void style(Attributes attributes) throws SAXException {
        debug("<style>", new Object[0]);
        if (this.currentElement != null) {
            String str = "all";
            boolean z = true;
            for (int i = 0; i < attributes.getLength(); i++) {
                String trim = attributes.getValue(i).trim();
                int i2 = $SWITCH_TABLE$com$caverock$androidsvg$SVGParser$SVGAttr()[SVGAttr.fromString(attributes.getLocalName(i)).ordinal()];
                if (i2 == 39) {
                    str = trim;
                } else if (i2 == 78) {
                    z = trim.equals("text/css");
                }
            }
            if (!z || !CSSParser.mediaMatches(str, CSSParser.MediaType.screen)) {
                this.ignoring = true;
                this.ignoreDepth = 1;
                return;
            }
            this.inStyleElement = true;
            return;
        }
        throw new SAXException("Invalid document. Root element must be <svg>");
    }

    private void parseCSSStyleSheet(String str) throws SAXException {
        this.svgDocument.addCSSRules(new CSSParser(CSSParser.MediaType.screen).parse(str));
    }
}
