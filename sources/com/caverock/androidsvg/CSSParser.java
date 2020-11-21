package com.caverock.androidsvg;

import android.util.Log;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParser;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXException;

public class CSSParser {
    private boolean inMediaRule = false;
    private MediaType rendererMediaType = null;

    /* access modifiers changed from: private */
    public enum AttribOp {
        EXISTS,
        EQUALS,
        INCLUDES,
        DASHMATCH
    }

    /* access modifiers changed from: private */
    public enum Combinator {
        DESCENDANT,
        CHILD,
        FOLLOWS
    }

    public enum MediaType {
        all,
        aural,
        braille,
        embossed,
        handheld,
        print,
        projection,
        screen,
        tty,
        tv
    }

    public static class Attrib {
        public String name = null;
        public AttribOp operation;
        public String value = null;

        public Attrib(String str, AttribOp attribOp, String str2) {
            this.name = str;
            this.operation = attribOp;
            this.value = str2;
        }
    }

    /* access modifiers changed from: private */
    public static class SimpleSelector {
        private static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$CSSParser$AttribOp;
        public List<Attrib> attribs = null;
        public Combinator combinator = null;
        public List<String> pseudos = null;
        public String tag = null;

        /* JADX WARNING: Can't wrap try/catch for region: R(11:3|4|5|6|7|8|9|10|11|12|14) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x0027 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:6:0x0015 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:8:0x001e */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        static /* synthetic */ int[] $SWITCH_TABLE$com$caverock$androidsvg$CSSParser$AttribOp() {
            /*
                int[] r0 = com.caverock.androidsvg.CSSParser.SimpleSelector.$SWITCH_TABLE$com$caverock$androidsvg$CSSParser$AttribOp
                if (r0 == 0) goto L_0x0005
                return r0
            L_0x0005:
                com.caverock.androidsvg.CSSParser$AttribOp[] r0 = com.caverock.androidsvg.CSSParser.AttribOp.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                com.caverock.androidsvg.CSSParser$AttribOp r1 = com.caverock.androidsvg.CSSParser.AttribOp.DASHMATCH     // Catch:{ NoSuchFieldError -> 0x0015 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0015 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0015 }
            L_0x0015:
                com.caverock.androidsvg.CSSParser$AttribOp r1 = com.caverock.androidsvg.CSSParser.AttribOp.EQUALS     // Catch:{ NoSuchFieldError -> 0x001e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001e }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001e }
            L_0x001e:
                com.caverock.androidsvg.CSSParser$AttribOp r1 = com.caverock.androidsvg.CSSParser.AttribOp.EXISTS     // Catch:{ NoSuchFieldError -> 0x0027 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0027 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0027 }
            L_0x0027:
                com.caverock.androidsvg.CSSParser$AttribOp r1 = com.caverock.androidsvg.CSSParser.AttribOp.INCLUDES     // Catch:{ NoSuchFieldError -> 0x0030 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0030 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0030 }
            L_0x0030:
                com.caverock.androidsvg.CSSParser.SimpleSelector.$SWITCH_TABLE$com$caverock$androidsvg$CSSParser$AttribOp = r0
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.CSSParser.SimpleSelector.$SWITCH_TABLE$com$caverock$androidsvg$CSSParser$AttribOp():int[]");
        }

        public SimpleSelector(Combinator combinator2, String str) {
            this.combinator = combinator2 == null ? Combinator.DESCENDANT : combinator2;
            this.tag = str;
        }

        public void addAttrib(String str, AttribOp attribOp, String str2) {
            if (this.attribs == null) {
                this.attribs = new ArrayList();
            }
            this.attribs.add(new Attrib(str, attribOp, str2));
        }

        public void addPseudo(String str) {
            if (this.pseudos == null) {
                this.pseudos = new ArrayList();
            }
            this.pseudos.add(str);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            Combinator combinator2 = this.combinator;
            if (combinator2 == Combinator.CHILD) {
                sb.append("> ");
            } else if (combinator2 == Combinator.FOLLOWS) {
                sb.append("+ ");
            }
            String str = this.tag;
            if (str == null) {
                str = "*";
            }
            sb.append(str);
            List<Attrib> list = this.attribs;
            if (list != null) {
                for (Attrib attrib : list) {
                    sb.append('[');
                    sb.append(attrib.name);
                    int i = $SWITCH_TABLE$com$caverock$androidsvg$CSSParser$AttribOp()[attrib.operation.ordinal()];
                    if (i == 2) {
                        sb.append('=');
                        sb.append(attrib.value);
                    } else if (i == 3) {
                        sb.append("~=");
                        sb.append(attrib.value);
                    } else if (i == 4) {
                        sb.append("|=");
                        sb.append(attrib.value);
                    }
                    sb.append(']');
                }
            }
            List<String> list2 = this.pseudos;
            if (list2 != null) {
                for (String str2 : list2) {
                    sb.append(':');
                    sb.append(str2);
                }
            }
            return sb.toString();
        }
    }

    public static class Ruleset {
        private List<Rule> rules = null;

        public void add(Rule rule) {
            if (this.rules == null) {
                this.rules = new ArrayList();
            }
            for (int i = 0; i < this.rules.size(); i++) {
                if (this.rules.get(i).selector.specificity > rule.selector.specificity) {
                    this.rules.add(i, rule);
                    return;
                }
            }
            this.rules.add(rule);
        }

        public void addAll(Ruleset ruleset) {
            if (ruleset.rules != null) {
                if (this.rules == null) {
                    this.rules = new ArrayList(ruleset.rules.size());
                }
                for (Rule rule : ruleset.rules) {
                    this.rules.add(rule);
                }
            }
        }

        public List<Rule> getRules() {
            return this.rules;
        }

        public boolean isEmpty() {
            List<Rule> list = this.rules;
            return list == null || list.isEmpty();
        }

        public String toString() {
            if (this.rules == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (Rule rule : this.rules) {
                sb.append(rule.toString());
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    public static class Rule {
        public Selector selector = null;
        public SVG.Style style = null;

        public Rule(Selector selector2, SVG.Style style2) {
            this.selector = selector2;
            this.style = style2;
        }

        public String toString() {
            return this.selector + " {}";
        }
    }

    public static class Selector {
        public List<SimpleSelector> selector = null;
        public int specificity = 0;

        public void add(SimpleSelector simpleSelector) {
            if (this.selector == null) {
                this.selector = new ArrayList();
            }
            this.selector.add(simpleSelector);
        }

        public int size() {
            List<SimpleSelector> list = this.selector;
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        public SimpleSelector get(int i) {
            return this.selector.get(i);
        }

        public boolean isEmpty() {
            List<SimpleSelector> list = this.selector;
            if (list == null) {
                return true;
            }
            return list.isEmpty();
        }

        public void addedIdAttribute() {
            this.specificity += 10000;
        }

        public void addedAttributeOrPseudo() {
            this.specificity += 100;
        }

        public void addedElement() {
            this.specificity++;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (SimpleSelector simpleSelector : this.selector) {
                sb.append(simpleSelector);
                sb.append(' ');
            }
            sb.append('(');
            sb.append(this.specificity);
            sb.append(')');
            return sb.toString();
        }
    }

    public CSSParser(MediaType mediaType) {
        this.rendererMediaType = mediaType;
    }

    public Ruleset parse(String str) throws SAXException {
        CSSTextScanner cSSTextScanner = new CSSTextScanner(str);
        cSSTextScanner.skipWhitespace();
        return parseRuleset(cSSTextScanner);
    }

    public static boolean mediaMatches(String str, MediaType mediaType) throws SAXException {
        CSSTextScanner cSSTextScanner = new CSSTextScanner(str);
        cSSTextScanner.skipWhitespace();
        List<MediaType> parseMediaList = parseMediaList(cSSTextScanner);
        if (cSSTextScanner.empty()) {
            return mediaMatches(parseMediaList, mediaType);
        }
        throw new SAXException("Invalid @media type list");
    }

    private static void warn(String str, Object... objArr) {
        Log.w("AndroidSVG CSSParser", String.format(str, objArr));
    }

    /* access modifiers changed from: private */
    public static class CSSTextScanner extends SVGParser.TextScanner {
        public CSSTextScanner(String str) {
            super(str.replaceAll("(?s)/\\*.*?\\*/", ""));
        }

        public String nextIdentifier() {
            int scanForIdentifier = scanForIdentifier();
            int i = this.position;
            if (scanForIdentifier == i) {
                return null;
            }
            String substring = this.input.substring(i, scanForIdentifier);
            this.position = scanForIdentifier;
            return substring;
        }

        private int scanForIdentifier() {
            int i;
            if (empty()) {
                return this.position;
            }
            int i2 = this.position;
            int charAt = this.input.charAt(i2);
            if (charAt == 45) {
                charAt = advanceChar();
            }
            if ((charAt < 65 || charAt > 90) && ((charAt < 97 || charAt > 122) && charAt != 95)) {
                i = i2;
            } else {
                int advanceChar = advanceChar();
                while (true) {
                    if ((advanceChar < 65 || advanceChar > 90) && ((advanceChar < 97 || advanceChar > 122) && !((advanceChar >= 48 && advanceChar <= 57) || advanceChar == 45 || advanceChar == 95))) {
                        break;
                    }
                    advanceChar = advanceChar();
                }
                i = this.position;
            }
            this.position = i2;
            return i;
        }

        /* JADX WARNING: Removed duplicated region for block: B:14:0x0036  */
        /* JADX WARNING: Removed duplicated region for block: B:15:0x003c  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x0056  */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x015a  */
        /* JADX WARNING: Removed duplicated region for block: B:85:0x015e  */
        /* JADX WARNING: Removed duplicated region for block: B:90:0x0158 A[EDGE_INSN: B:90:0x0158->B:82:0x0158 ?: BREAK  , SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean nextSimpleSelector(com.caverock.androidsvg.CSSParser.Selector r11) throws org.xml.sax.SAXException {
            /*
            // Method dump skipped, instructions count: 353
            */
            throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.CSSParser.CSSTextScanner.nextSimpleSelector(com.caverock.androidsvg.CSSParser$Selector):boolean");
        }

        private String nextAttribValue() {
            if (empty()) {
                return null;
            }
            String nextQuotedString = nextQuotedString();
            if (nextQuotedString != null) {
                return nextQuotedString;
            }
            return nextIdentifier();
        }

        public String nextPropertyValue() {
            if (empty()) {
                return null;
            }
            int i = this.position;
            int charAt = this.input.charAt(i);
            int i2 = i;
            while (charAt != -1 && charAt != 59 && charAt != 125 && charAt != 33 && !isEOL(charAt)) {
                if (!isWhitespace(charAt)) {
                    i2 = this.position + 1;
                }
                charAt = advanceChar();
            }
            if (this.position > i) {
                return this.input.substring(i, i2);
            }
            this.position = i;
            return null;
        }
    }

    private static boolean mediaMatches(List<MediaType> list, MediaType mediaType) {
        for (MediaType mediaType2 : list) {
            if (mediaType2 == MediaType.all) {
                return true;
            }
            if (mediaType2 == mediaType) {
                return true;
            }
        }
        return false;
    }

    private static List<MediaType> parseMediaList(CSSTextScanner cSSTextScanner) throws SAXException {
        ArrayList arrayList = new ArrayList();
        while (!cSSTextScanner.empty()) {
            try {
                arrayList.add(MediaType.valueOf(cSSTextScanner.nextToken(',')));
                if (!cSSTextScanner.skipCommaWhitespace()) {
                    break;
                }
            } catch (IllegalArgumentException unused) {
                throw new SAXException("Invalid @media type list");
            }
        }
        return arrayList;
    }

    private void parseAtRule(Ruleset ruleset, CSSTextScanner cSSTextScanner) throws SAXException {
        String nextIdentifier = cSSTextScanner.nextIdentifier();
        cSSTextScanner.skipWhitespace();
        if (nextIdentifier != null) {
            if (this.inMediaRule || !nextIdentifier.equals("media")) {
                warn("Ignoring @%s rule", nextIdentifier);
                skipAtRule(cSSTextScanner);
            } else {
                List<MediaType> parseMediaList = parseMediaList(cSSTextScanner);
                if (cSSTextScanner.consume('{')) {
                    cSSTextScanner.skipWhitespace();
                    if (mediaMatches(parseMediaList, this.rendererMediaType)) {
                        this.inMediaRule = true;
                        ruleset.addAll(parseRuleset(cSSTextScanner));
                        this.inMediaRule = false;
                    } else {
                        parseRuleset(cSSTextScanner);
                    }
                    if (!cSSTextScanner.consume('}')) {
                        throw new SAXException("Invalid @media rule: expected '}' at end of rule set");
                    }
                } else {
                    throw new SAXException("Invalid @media rule: missing rule set");
                }
            }
            cSSTextScanner.skipWhitespace();
            return;
        }
        throw new SAXException("Invalid '@' rule in <style> element");
    }

    private void skipAtRule(CSSTextScanner cSSTextScanner) {
        int i = 0;
        while (!cSSTextScanner.empty()) {
            int intValue = cSSTextScanner.nextChar().intValue();
            if (intValue != 59 || i != 0) {
                if (intValue == 123) {
                    i++;
                } else if (intValue == 125 && i > 0 && i - 1 == 0) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private Ruleset parseRuleset(CSSTextScanner cSSTextScanner) throws SAXException {
        Ruleset ruleset = new Ruleset();
        while (!cSSTextScanner.empty()) {
            if (!cSSTextScanner.consume("<!--") && !cSSTextScanner.consume("-->")) {
                if (!cSSTextScanner.consume('@')) {
                    if (!parseRule(ruleset, cSSTextScanner)) {
                        break;
                    }
                } else {
                    parseAtRule(ruleset, cSSTextScanner);
                }
            }
        }
        return ruleset;
    }

    private boolean parseRule(Ruleset ruleset, CSSTextScanner cSSTextScanner) throws SAXException {
        List<Selector> parseSelectorGroup = parseSelectorGroup(cSSTextScanner);
        if (parseSelectorGroup == null || parseSelectorGroup.isEmpty()) {
            return false;
        }
        if (cSSTextScanner.consume('{')) {
            cSSTextScanner.skipWhitespace();
            SVG.Style parseDeclarations = parseDeclarations(cSSTextScanner);
            cSSTextScanner.skipWhitespace();
            for (Selector selector : parseSelectorGroup) {
                ruleset.add(new Rule(selector, parseDeclarations));
            }
            return true;
        }
        throw new SAXException("Malformed rule block in <style> element: missing '{'");
    }

    private List<Selector> parseSelectorGroup(CSSTextScanner cSSTextScanner) throws SAXException {
        if (cSSTextScanner.empty()) {
            return null;
        }
        ArrayList arrayList = new ArrayList(1);
        Selector selector = new Selector();
        while (!cSSTextScanner.empty() && cSSTextScanner.nextSimpleSelector(selector)) {
            if (cSSTextScanner.skipCommaWhitespace()) {
                arrayList.add(selector);
                selector = new Selector();
            }
        }
        if (!selector.isEmpty()) {
            arrayList.add(selector);
        }
        return arrayList;
    }

    private SVG.Style parseDeclarations(CSSTextScanner cSSTextScanner) throws SAXException {
        SVG.Style style = new SVG.Style();
        do {
            String nextIdentifier = cSSTextScanner.nextIdentifier();
            cSSTextScanner.skipWhitespace();
            if (!cSSTextScanner.consume(':')) {
                break;
            }
            cSSTextScanner.skipWhitespace();
            String nextPropertyValue = cSSTextScanner.nextPropertyValue();
            if (nextPropertyValue == null) {
                break;
            }
            cSSTextScanner.skipWhitespace();
            if (cSSTextScanner.consume('!')) {
                cSSTextScanner.skipWhitespace();
                if (cSSTextScanner.consume("important")) {
                    cSSTextScanner.skipWhitespace();
                } else {
                    throw new SAXException("Malformed rule set in <style> element: found unexpected '!'");
                }
            }
            cSSTextScanner.consume(';');
            SVGParser.processStyleProperty(style, nextIdentifier, nextPropertyValue);
            cSSTextScanner.skipWhitespace();
            if (cSSTextScanner.consume('}')) {
                return style;
            }
        } while (!cSSTextScanner.empty());
        throw new SAXException("Malformed rule set in <style> element");
    }

    protected static List<String> parseClassAttribute(String str) throws SAXException {
        CSSTextScanner cSSTextScanner = new CSSTextScanner(str);
        ArrayList arrayList = null;
        while (!cSSTextScanner.empty()) {
            String nextIdentifier = cSSTextScanner.nextIdentifier();
            if (nextIdentifier != null) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(nextIdentifier);
                cSSTextScanner.skipWhitespace();
            } else {
                throw new SAXException("Invalid value for \"class\" attribute: " + str);
            }
        }
        return arrayList;
    }

    protected static boolean ruleMatch(Selector selector, SVG.SvgElementBase svgElementBase) {
        ArrayList arrayList = new ArrayList();
        for (SVG.SvgContainer svgContainer = svgElementBase.parent; svgContainer != null; svgContainer = ((SVG.SvgObject) svgContainer).parent) {
            arrayList.add(0, svgContainer);
        }
        int size = arrayList.size() - 1;
        if (selector.size() == 1) {
            return selectorMatch(selector.get(0), arrayList, size, svgElementBase);
        }
        return ruleMatch(selector, selector.size() - 1, arrayList, size, svgElementBase);
    }

    private static boolean ruleMatch(Selector selector, int i, List<SVG.SvgContainer> list, int i2, SVG.SvgElementBase svgElementBase) {
        SimpleSelector simpleSelector = selector.get(i);
        if (!selectorMatch(simpleSelector, list, i2, svgElementBase)) {
            return false;
        }
        Combinator combinator = simpleSelector.combinator;
        if (combinator == Combinator.DESCENDANT) {
            if (i == 0) {
                return true;
            }
            while (i2 >= 0) {
                if (ruleMatchOnAncestors(selector, i - 1, list, i2)) {
                    return true;
                }
                i2--;
            }
            return false;
        } else if (combinator == Combinator.CHILD) {
            return ruleMatchOnAncestors(selector, i - 1, list, i2);
        } else {
            int childPosition = getChildPosition(list, i2, svgElementBase);
            if (childPosition <= 0) {
                return false;
            }
            return ruleMatch(selector, i - 1, list, i2, (SVG.SvgElementBase) svgElementBase.parent.getChildren().get(childPosition - 1));
        }
    }

    private static boolean ruleMatchOnAncestors(Selector selector, int i, List<SVG.SvgContainer> list, int i2) {
        SimpleSelector simpleSelector = selector.get(i);
        SVG.SvgElementBase svgElementBase = (SVG.SvgElementBase) list.get(i2);
        if (!selectorMatch(simpleSelector, list, i2, svgElementBase)) {
            return false;
        }
        Combinator combinator = simpleSelector.combinator;
        if (combinator == Combinator.DESCENDANT) {
            if (i == 0) {
                return true;
            }
            while (i2 > 0) {
                i2--;
                if (ruleMatchOnAncestors(selector, i - 1, list, i2)) {
                    return true;
                }
            }
            return false;
        } else if (combinator == Combinator.CHILD) {
            return ruleMatchOnAncestors(selector, i - 1, list, i2 - 1);
        } else {
            int childPosition = getChildPosition(list, i2, svgElementBase);
            if (childPosition <= 0) {
                return false;
            }
            return ruleMatch(selector, i - 1, list, i2, (SVG.SvgElementBase) svgElementBase.parent.getChildren().get(childPosition - 1));
        }
    }

    private static int getChildPosition(List<SVG.SvgContainer> list, int i, SVG.SvgElementBase svgElementBase) {
        SVG.SvgContainer svgContainer;
        if (i < 0 || list.get(i) != (svgContainer = svgElementBase.parent)) {
            return -1;
        }
        int i2 = 0;
        for (SVG.SvgObject svgObject : svgContainer.getChildren()) {
            if (svgObject == svgElementBase) {
                return i2;
            }
            i2++;
        }
        return -1;
    }

    /* JADX WARNING: Removed duplicated region for block: B:33:0x0070  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean selectorMatch(com.caverock.androidsvg.CSSParser.SimpleSelector r5, java.util.List<com.caverock.androidsvg.SVG.SvgContainer> r6, int r7, com.caverock.androidsvg.SVG.SvgElementBase r8) {
        /*
        // Method dump skipped, instructions count: 135
        */
        throw new UnsupportedOperationException("Method not decompiled: com.caverock.androidsvg.CSSParser.selectorMatch(com.caverock.androidsvg.CSSParser$SimpleSelector, java.util.List, int, com.caverock.androidsvg.SVG$SvgElementBase):boolean");
    }
}
