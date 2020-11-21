package com.android.settings.applications;

import android.content.Intent;
import android.net.Uri;

public enum EnterpriseDefaultApps {
    BROWSER(new Intent[]{buildIntent("android.intent.action.VIEW", "android.intent.category.BROWSABLE", "http:", null)}),
    CALENDAR(new Intent[]{buildIntent("android.intent.action.INSERT", null, null, "vnd.android.cursor.dir/event")}),
    CAMERA(new Intent[]{new Intent("android.media.action.IMAGE_CAPTURE"), new Intent("android.media.action.VIDEO_CAPTURE")}),
    CONTACTS(new Intent[]{buildIntent("android.intent.action.PICK", null, null, "vnd.android.cursor.dir/contact")}),
    EMAIL(new Intent[]{new Intent("android.intent.action.SENDTO"), new Intent("android.intent.action.SEND"), new Intent("android.intent.action.SEND_MULTIPLE")}),
    MAP(new Intent[]{buildIntent("android.intent.action.VIEW", null, "geo:", null)}),
    PHONE(new Intent[]{new Intent("android.intent.action.DIAL"), new Intent("android.intent.action.CALL")});
    
    private final Intent[] mIntents;

    private EnterpriseDefaultApps(Intent[] intentArr) {
        this.mIntents = intentArr;
    }

    public Intent[] getIntents() {
        return this.mIntents;
    }

    private static Intent buildIntent(String str, String str2, String str3, String str4) {
        Intent intent = new Intent(str);
        if (str2 != null) {
            intent.addCategory(str2);
        }
        if (str3 != null) {
            intent.setData(Uri.parse(str3));
        }
        if (str4 != null) {
            intent.setType(str4);
        }
        return intent;
    }
}
