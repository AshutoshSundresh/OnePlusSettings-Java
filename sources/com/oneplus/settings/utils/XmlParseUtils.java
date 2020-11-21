package com.oneplus.settings.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.settings.R$styleable;
import java.util.Arrays;
import java.util.List;

public class XmlParseUtils {
    private static final List<String> SKIP_NODES = Arrays.asList("intent", "extra");

    private static String getData(Context context, AttributeSet attributeSet, int[] iArr, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, iArr);
        String string = obtainStyledAttributes.getString(i);
        obtainStyledAttributes.recycle();
        return string;
    }

    public static String getDataKey(Context context, AttributeSet attributeSet) {
        return getData(context, attributeSet, R$styleable.Preference, R$styleable.Preference_android_key);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00a0, code lost:
        if (r2 == null) goto L_0x00c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00be, code lost:
        if (0 == 0) goto L_0x00c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00c0, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00c3, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<java.lang.String> parsePreferenceKeyFromResource(int r8, android.content.Context r9) {
        /*
        // Method dump skipped, instructions count: 202
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.utils.XmlParseUtils.parsePreferenceKeyFromResource(int, android.content.Context):java.util.List");
    }
}
