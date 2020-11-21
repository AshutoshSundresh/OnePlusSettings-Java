package com.oneplus.settings.defaultapp.apptype;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

public class DefaultAppTypeGallery extends DefaultAppTypeInfo {
    private static final String[] MIME_TYPE = {"image/x-ms-bmp", "image/jpeg", "image/gif", "image/png", "image/webp", "image/jp2", "image/pjpeg", "image/bmp", "image/icon", "image/tiff", "image/x-icon", "image/x-portable-pixmap", "image/pcx", "image/x-photoshop", "image/x-cmu-raster", "image/svg+xml", "image/vnd.wap.wbmp"};

    private Intent makeContentIntent(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.parse("content://"), str);
        return intent;
    }

    private Intent makeFileIntent(String str) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.parse("file://"), str);
        return intent;
    }

    @Override // com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo
    public List<Intent> getAppIntent() {
        ArrayList arrayList = new ArrayList();
        String[] strArr = MIME_TYPE;
        for (String str : strArr) {
            arrayList.add(makeContentIntent(str));
            arrayList.add(makeFileIntent(str));
        }
        return arrayList;
    }

    private IntentFilter makeContentIntentFilter(String str) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.VIEW");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        intentFilter.addDataScheme("content");
        try {
            intentFilter.addDataType(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intentFilter;
    }

    private IntentFilter makeFileIntentFilter(String str) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.VIEW");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        intentFilter.addDataScheme("file");
        try {
            intentFilter.addDataType(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intentFilter;
    }

    @Override // com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo
    public List<IntentFilter> getAppFilter() {
        ArrayList arrayList = new ArrayList();
        String[] strArr = MIME_TYPE;
        for (String str : strArr) {
            arrayList.add(makeContentIntentFilter(str));
            arrayList.add(makeFileIntentFilter(str));
        }
        return arrayList;
    }

    @Override // com.oneplus.settings.defaultapp.apptype.DefaultAppTypeInfo
    public List<Integer> getAppMatchParam() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < MIME_TYPE.length; i++) {
            arrayList.add(6291456);
            arrayList.add(6291456);
        }
        return arrayList;
    }
}
