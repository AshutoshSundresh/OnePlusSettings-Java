package androidx.core.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.StateSet;
import android.util.TypedValue;
import android.util.Xml;
import androidx.core.R$attr;
import androidx.core.R$styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class ColorStateListInflaterCompat {
    private static final ThreadLocal<TypedValue> sTempTypedValue = new ThreadLocal<>();

    public static ColorStateList inflate(Resources resources, int i, Resources.Theme theme) {
        try {
            return createFromXml(resources, resources.getXml(i), theme);
        } catch (Exception e) {
            Log.e("CSLCompat", "Failed to inflate ColorStateList.", e);
            return null;
        }
    }

    public static ColorStateList createFromXml(Resources resources, XmlPullParser xmlPullParser, Resources.Theme theme) throws XmlPullParserException, IOException {
        int next;
        AttributeSet asAttributeSet = Xml.asAttributeSet(xmlPullParser);
        do {
            next = xmlPullParser.next();
            if (next == 2) {
                break;
            }
        } while (next != 1);
        if (next == 2) {
            return createFromXmlInner(resources, xmlPullParser, asAttributeSet, theme);
        }
        throw new XmlPullParserException("No start tag found");
    }

    public static ColorStateList createFromXmlInner(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        String name = xmlPullParser.getName();
        if (name.equals("selector")) {
            return inflate(resources, xmlPullParser, attributeSet, theme);
        }
        throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": invalid color state list tag " + name);
    }

    private static ColorStateList inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        int depth;
        int i;
        int i2 = 1;
        int depth2 = xmlPullParser.getDepth() + 1;
        int[][] iArr = new int[20][];
        int[] iArr2 = new int[20];
        int i3 = 0;
        while (true) {
            int next = xmlPullParser.next();
            if (next == i2 || ((depth = xmlPullParser.getDepth()) < depth2 && next == 3)) {
                int[] iArr3 = new int[i3];
                int[][] iArr4 = new int[i3][];
                System.arraycopy(iArr2, 0, iArr3, 0, i3);
                System.arraycopy(iArr, 0, iArr4, 0, i3);
            } else {
                if (next == 2 && depth <= depth2 && xmlPullParser.getName().equals("item")) {
                    TypedArray obtainAttributes = obtainAttributes(resources, theme, attributeSet, R$styleable.ColorStateListItem);
                    int resourceId = obtainAttributes.getResourceId(R$styleable.ColorStateListItem_android_color, -1);
                    if (resourceId == -1 || isColorInt(resources, resourceId)) {
                        i = obtainAttributes.getColor(R$styleable.ColorStateListItem_android_color, -65281);
                    } else {
                        try {
                            i = createFromXml(resources, resources.getXml(resourceId), theme).getDefaultColor();
                        } catch (Exception unused) {
                            i = obtainAttributes.getColor(R$styleable.ColorStateListItem_android_color, -65281);
                        }
                    }
                    float f = 1.0f;
                    if (obtainAttributes.hasValue(R$styleable.ColorStateListItem_android_alpha)) {
                        f = obtainAttributes.getFloat(R$styleable.ColorStateListItem_android_alpha, 1.0f);
                    } else if (obtainAttributes.hasValue(R$styleable.ColorStateListItem_alpha)) {
                        f = obtainAttributes.getFloat(R$styleable.ColorStateListItem_alpha, 1.0f);
                    }
                    obtainAttributes.recycle();
                    int attributeCount = attributeSet.getAttributeCount();
                    int[] iArr5 = new int[attributeCount];
                    int i4 = 0;
                    for (int i5 = 0; i5 < attributeCount; i5++) {
                        int attributeNameResource = attributeSet.getAttributeNameResource(i5);
                        if (!(attributeNameResource == 16843173 || attributeNameResource == 16843551 || attributeNameResource == R$attr.alpha)) {
                            int i6 = i4 + 1;
                            if (!attributeSet.getAttributeBooleanValue(i5, false)) {
                                attributeNameResource = -attributeNameResource;
                            }
                            iArr5[i4] = attributeNameResource;
                            i4 = i6;
                        }
                    }
                    int[] trimStateSet = StateSet.trimStateSet(iArr5, i4);
                    iArr2 = GrowingArrayUtils.append(iArr2, i3, modulateColorAlpha(i, f));
                    iArr = (int[][]) GrowingArrayUtils.append(iArr, i3, trimStateSet);
                    i3++;
                }
                i2 = 1;
            }
        }
        int[] iArr32 = new int[i3];
        int[][] iArr42 = new int[i3][];
        System.arraycopy(iArr2, 0, iArr32, 0, i3);
        System.arraycopy(iArr, 0, iArr42, 0, i3);
        return new ColorStateList(iArr42, iArr32);
    }

    private static boolean isColorInt(Resources resources, int i) {
        TypedValue typedValue = getTypedValue();
        resources.getValue(i, typedValue, true);
        int i2 = typedValue.type;
        if (i2 < 28 || i2 > 31) {
            return false;
        }
        return true;
    }

    private static TypedValue getTypedValue() {
        TypedValue typedValue = sTempTypedValue.get();
        if (typedValue != null) {
            return typedValue;
        }
        TypedValue typedValue2 = new TypedValue();
        sTempTypedValue.set(typedValue2);
        return typedValue2;
    }

    private static TypedArray obtainAttributes(Resources resources, Resources.Theme theme, AttributeSet attributeSet, int[] iArr) {
        if (theme == null) {
            return resources.obtainAttributes(attributeSet, iArr);
        }
        return theme.obtainStyledAttributes(attributeSet, iArr, 0, 0);
    }

    private static int modulateColorAlpha(int i, float f) {
        return (i & 16777215) | (Math.round(((float) Color.alpha(i)) * f) << 24);
    }
}
