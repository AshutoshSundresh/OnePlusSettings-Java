package com.google.android.setupdesign.items;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.view.InflateException;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class SimpleInflater<T> {
    protected final Resources resources;

    /* access modifiers changed from: protected */
    public abstract void onAddChildItem(T t, T t2);

    /* access modifiers changed from: protected */
    public abstract T onCreateItem(String str, AttributeSet attributeSet);

    /* access modifiers changed from: protected */
    public boolean onInterceptCreateItem(XmlPullParser xmlPullParser, T t, AttributeSet attributeSet) throws XmlPullParserException {
        return false;
    }

    protected SimpleInflater(Resources resources2) {
        this.resources = resources2;
    }

    public Resources getResources() {
        return this.resources;
    }

    public T inflate(int i) {
        XmlResourceParser xml = getResources().getXml(i);
        try {
            return inflate(xml);
        } finally {
            xml.close();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x001d A[Catch:{ XmlPullParserException -> 0x005b, IOException -> 0x0038 }] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0011 A[Catch:{ XmlPullParserException -> 0x005b, IOException -> 0x0038 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public T inflate(org.xmlpull.v1.XmlPullParser r5) {
        /*
        // Method dump skipped, instructions count: 102
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.setupdesign.items.SimpleInflater.inflate(org.xmlpull.v1.XmlPullParser):java.lang.Object");
    }

    private T createItemFromTag(String str, AttributeSet attributeSet) {
        try {
            return onCreateItem(str, attributeSet);
        } catch (InflateException e) {
            throw e;
        } catch (Exception e2) {
            throw new InflateException(attributeSet.getPositionDescription() + ": Error inflating class " + str, e2);
        }
    }

    private void rInflate(XmlPullParser xmlPullParser, T t, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                return;
            }
            if (next == 2 && !onInterceptCreateItem(xmlPullParser, t, attributeSet)) {
                T createItemFromTag = createItemFromTag(xmlPullParser.getName(), attributeSet);
                onAddChildItem(t, createItemFromTag);
                rInflate(xmlPullParser, createItemFromTag, attributeSet);
            }
        }
    }
}
