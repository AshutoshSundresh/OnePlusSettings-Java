package com.google.android.setupcompat.template;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

/* access modifiers changed from: package-private */
public class FooterButtonInflater {
    protected final Context context;

    public FooterButtonInflater(Context context2) {
        this.context = context2;
    }

    public Resources getResources() {
        return this.context.getResources();
    }

    public FooterButton inflate(int i) {
        XmlResourceParser xml = getResources().getXml(i);
        try {
            return inflate(xml);
        } finally {
            xml.close();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0040 A[Catch:{ XmlPullParserException -> 0x007e, IOException -> 0x005b }] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0011 A[Catch:{ XmlPullParserException -> 0x007e, IOException -> 0x005b }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.google.android.setupcompat.template.FooterButton inflate(org.xmlpull.v1.XmlPullParser r5) {
        /*
        // Method dump skipped, instructions count: 137
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.setupcompat.template.FooterButtonInflater.inflate(org.xmlpull.v1.XmlPullParser):com.google.android.setupcompat.template.FooterButton");
    }
}
