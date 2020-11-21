package com.oneplus.security.database;

import android.net.Uri;

public class Const {
    public static final Uri AUTHORITY_URI;
    public static Uri URI_NETWORK_RESTRICT = Uri.withAppendedPath(AUTHORITY_URI, "network_restrict");

    static {
        Uri parse = Uri.parse("content://com.oneplus.security.database.SafeProvider");
        AUTHORITY_URI = parse;
        Uri.withAppendedPath(parse, "tm_network_control");
        Uri.withAppendedPath(AUTHORITY_URI, "intercept_logs");
    }
}
