package com.oneplus.settings.utils;

import android.text.TextUtils;
import android.util.Log;

public class OPDeviceHelper {
    public static final CharSequence[] DEFAULT_AT_LEAST_OP8_ICON_PACK_CANDIDATE_LIST = {"com.oneplus.iconpack.oneplush2", "com.oneplus.iconpack.onepluso2", "com.oneplus.iconpack.oneplus", "com.oneplus.iconpack.h2default", "com.oneplus.iconpack.o2default"};
    public static final CharSequence[] DEFAULT_ICON_PACK_CANDIDATE_LIST = {"com.oneplus.iconpack.oneplus", "com.oneplus.iconpack.onepluso2", "com.oneplus.iconpack.oneplush2", "com.oneplus.iconpack.o2default", "com.oneplus.iconpack.h2default"};
    public static final CharSequence[] NOT_DEFAULT_ICON_PACK_ORDER_LIST = {"com.oneplus.iconpack.mclaren", "com.oneplus.iconpack.diwali", "com.oneplus.iconpack.circle", "com.oneplus.iconpack.square", "com.oneplus.iconpack.dives", "com.oneplus.iconpack.rifon", "com.oneplus.iconpack.h2light", "com.oneplus.iconpack.h2folio"};
    private static String sDevice;

    /* JADX WARNING: Removed duplicated region for block: B:169:0x0302 A[SYNTHETIC, Splitter:B:169:0x0302] */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0346 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0353 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x0355 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0361 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x036d A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0379 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0385 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x0391 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x039d A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x03a9 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x03b5 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x03c1 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x03cd A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:216:0x03d9 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x03e5 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x03f1 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x03fb A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0407 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0411 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x041b A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x0425 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x042f A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x0438 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x0441 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x044a A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x0453 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x045c A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x0467 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x0472 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x047d A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0488 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0495 A[Catch:{ Exception -> 0x04c6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0496 A[Catch:{ Exception -> 0x04c6 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String getDeviceTag() {
        /*
        // Method dump skipped, instructions count: 1666
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.utils.OPDeviceHelper.getDeviceTag():java.lang.String");
    }

    public static boolean isAtLeastOP8DeviceVersion() {
        String deviceTag = getDeviceTag();
        if (TextUtils.isEmpty(deviceTag)) {
            Log.w("DeviceHelper", "isAtLeastOP8DeviceVersion, empty string");
            return false;
        }
        char c = 65535;
        try {
            switch (deviceTag.hashCode()) {
                case 46849326:
                    if (deviceTag.equals("14001")) {
                        c = 0;
                        break;
                    }
                    break;
                case 46849458:
                    if (deviceTag.equals("14049")) {
                        c = 1;
                        break;
                    }
                    break;
                case 46879276:
                    if (deviceTag.equals("15055")) {
                        c = 2;
                        break;
                    }
                    break;
                case 46886805:
                    if (deviceTag.equals("15801")) {
                        c = 3;
                        break;
                    }
                    break;
                case 46886836:
                    if (deviceTag.equals("15811")) {
                        c = 4;
                        break;
                    }
                    break;
                case 46916759:
                    if (deviceTag.equals("16859")) {
                        c = 5;
                        break;
                    }
                    break;
                case 46946387:
                    if (deviceTag.equals("17801")) {
                        c = 6;
                        break;
                    }
                    break;
                case 46946426:
                    if (deviceTag.equals("17819")) {
                        c = 7;
                        break;
                    }
                    break;
                case 46976178:
                    if (deviceTag.equals("18801")) {
                        c = '\b';
                        break;
                    }
                    break;
                case 46976209:
                    if (deviceTag.equals("18811")) {
                        c = '\t';
                        break;
                    }
                    break;
                case 46976240:
                    if (deviceTag.equals("18821")) {
                        c = '\n';
                        break;
                    }
                    break;
                case 46976244:
                    if (deviceTag.equals("18825")) {
                        c = 11;
                        break;
                    }
                    break;
                case 46976246:
                    if (deviceTag.equals("18827")) {
                        c = '\f';
                        break;
                    }
                    break;
                case 46976271:
                    if (deviceTag.equals("18831")) {
                        c = '\r';
                        break;
                    }
                    break;
                case 46976339:
                    if (deviceTag.equals("18857")) {
                        c = 14;
                        break;
                    }
                    break;
                case 46976368:
                    if (deviceTag.equals("18865")) {
                        c = 15;
                        break;
                    }
                    break;
                case 47005969:
                    if (deviceTag.equals("19801")) {
                        c = 16;
                        break;
                    }
                    break;
                case 47006061:
                    if (deviceTag.equals("19830")) {
                        c = 17;
                        break;
                    }
                    break;
                case 47006062:
                    if (deviceTag.equals("19831")) {
                        c = 18;
                        break;
                    }
                    break;
                case 47006064:
                    if (deviceTag.equals("19833")) {
                        c = 19;
                        break;
                    }
                    break;
                case 47006123:
                    if (deviceTag.equals("19850")) {
                        c = 20;
                        break;
                    }
                    break;
                case 47006124:
                    if (deviceTag.equals("19851")) {
                        c = 21;
                        break;
                    }
                    break;
                case 47006155:
                    if (deviceTag.equals("19861")) {
                        c = 22;
                        break;
                    }
                    break;
                case 47006157:
                    if (deviceTag.equals("19863")) {
                        c = 23;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case '\b':
                case '\t':
                case '\n':
                case 11:
                case '\f':
                case '\r':
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                    Log.d("DeviceHelper", "isAtLeastOP8DeviceVersion: this= " + deviceTag + " , is at least OP8: false");
                    return false;
                default:
                    Log.d("DeviceHelper", "isAtLeastOP8DeviceVersion: this= " + deviceTag + " , is at least OP8: true");
                    return true;
            }
        } catch (NumberFormatException e) {
            Log.w("DeviceHelper", "isAtLeastOP8DeviceVersion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
