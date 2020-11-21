package com.android.settings.utils;

import android.content.Context;
import android.text.BidiFormatter;
import android.text.format.Formatter;

public final class FileSizeFormatter {
    public static String formatFileSize(Context context, long j, int i, long j2) {
        if (context == null) {
            return "";
        }
        Formatter.BytesResult formatBytes = formatBytes(context.getResources(), j, i, j2);
        return BidiFormatter.getInstance().unicodeWrap(context.getString(getFileSizeSuffix(context), formatBytes.value, formatBytes.units));
    }

    private static int getFileSizeSuffix(Context context) {
        return context.getResources().getIdentifier("fileSizeSuffix", "string", "android");
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0032  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.text.format.Formatter.BytesResult formatBytes(android.content.res.Resources r5, long r6, int r8, long r9) {
        /*
            r0 = 0
            int r0 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            r1 = 0
            r2 = 1
            if (r0 >= 0) goto L_0x000a
            r0 = r2
            goto L_0x000b
        L_0x000a:
            r0 = r1
        L_0x000b:
            if (r0 == 0) goto L_0x000e
            long r6 = -r6
        L_0x000e:
            float r6 = (float) r6
            float r7 = (float) r9
            float r6 = r6 / r7
            r3 = 1
            int r7 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            java.lang.String r3 = "%.0f"
            if (r7 != 0) goto L_0x001b
        L_0x0019:
            r7 = r2
            goto L_0x0030
        L_0x001b:
            r7 = 1065353216(0x3f800000, float:1.0)
            int r7 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r7 >= 0) goto L_0x0026
            r7 = 100
            java.lang.String r3 = "%.2f"
            goto L_0x0030
        L_0x0026:
            r7 = 1092616192(0x41200000, float:10.0)
            int r7 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
            if (r7 >= 0) goto L_0x0019
            r7 = 10
            java.lang.String r3 = "%.1f"
        L_0x0030:
            if (r0 == 0) goto L_0x0033
            float r6 = -r6
        L_0x0033:
            java.lang.Object[] r0 = new java.lang.Object[r2]
            java.lang.Float r2 = java.lang.Float.valueOf(r6)
            r0[r1] = r2
            java.lang.String r0 = java.lang.String.format(r3, r0)
            float r1 = (float) r7
            float r6 = r6 * r1
            int r6 = java.lang.Math.round(r6)
            long r1 = (long) r6
            long r1 = r1 * r9
            long r6 = (long) r7
            long r1 = r1 / r6
            java.lang.String r5 = r5.getString(r8)
            android.text.format.Formatter$BytesResult r6 = new android.text.format.Formatter$BytesResult
            r6.<init>(r0, r5, r1)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.utils.FileSizeFormatter.formatBytes(android.content.res.Resources, long, int, long):android.text.format.Formatter$BytesResult");
    }
}
