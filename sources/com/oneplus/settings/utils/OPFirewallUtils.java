package com.oneplus.settings.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class OPFirewallUtils {
    public static final Uri URI_NETWORK_RESTRICT;
    public static final Uri URI_OPSAFE_BASE;

    static {
        Uri parse = Uri.parse("content://com.oneplus.security.database.SafeProvider");
        URI_OPSAFE_BASE = parse;
        URI_NETWORK_RESTRICT = Uri.withAppendedPath(parse, "network_restrict");
    }

    public static void addOrUpdateRole(Context context, OPFirewallRule oPFirewallRule) {
        ContentValues contentValues = new ContentValues();
        if (oPFirewallRule.getWlan() != null) {
            contentValues.put("wlan", Integer.valueOf(oPFirewallRule.getWlan().intValue() == 0 ? 0 : 1));
        }
        if (oPFirewallRule.getMobile() != null) {
            contentValues.put("mobile", Integer.valueOf(oPFirewallRule.getMobile().intValue() == 0 ? 0 : 1));
        }
        if (selectFirewallRuleByPkg(context, oPFirewallRule.getPkg()) == null) {
            try {
                contentValues.put("pkg", oPFirewallRule.getPkg());
                context.getContentResolver().insert(URI_NETWORK_RESTRICT, contentValues);
            } catch (Exception e) {
                Log.e("OPFirewallUtils", e.getMessage());
            }
        } else {
            context.getContentResolver().update(URI_NETWORK_RESTRICT, contentValues, "pkg = ? ", new String[]{oPFirewallRule.getPkg()});
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0064, code lost:
        if (r7 != null) goto L_0x0076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0074, code lost:
        if (r7 != null) goto L_0x0076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0076, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0079, code lost:
        return null;
     */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x007e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.oneplus.settings.utils.OPFirewallRule selectFirewallRuleByPkg(android.content.Context r7, java.lang.String r8) {
        /*
        // Method dump skipped, instructions count: 130
        */
        throw new UnsupportedOperationException("Method not decompiled: com.oneplus.settings.utils.OPFirewallUtils.selectFirewallRuleByPkg(android.content.Context, java.lang.String):com.oneplus.settings.utils.OPFirewallRule");
    }
}
