package com.oneplus.settings.widget;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.util.PathParser;
import com.android.settings.C0006R$color;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class OPAdaptiveIconShapeDrawable extends ShapeDrawable {
    public OPAdaptiveIconShapeDrawable() {
    }

    public OPAdaptiveIconShapeDrawable(Resources resources) {
        init(resources);
    }

    @Override // android.graphics.drawable.ShapeDrawable, android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        init(resources);
    }

    private void init(Resources resources) {
        Path path = new Path(PathParser.createPathFromPathData(resources.getString(17039916)));
        getPaint().setAntiAlias(true);
        getPaint().setStyle(Paint.Style.STROKE);
        getPaint().setStrokeWidth(3.0f);
        getPaint().setColor(resources.getColor(C0006R$color.oneplus_accent_color));
        setShape(new PathShape(path, 100.0f, 100.0f));
    }
}
