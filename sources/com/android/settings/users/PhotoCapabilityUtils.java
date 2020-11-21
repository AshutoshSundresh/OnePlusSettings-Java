package com.android.settings.users;

import android.content.Context;
import android.content.Intent;

/* access modifiers changed from: package-private */
public class PhotoCapabilityUtils {
    static boolean canTakePhoto(Context context) {
        return context.getPackageManager().queryIntentActivities(new Intent("android.media.action.IMAGE_CAPTURE"), 65536).size() > 0;
    }

    static boolean canChoosePhoto(Context context) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        return context.getPackageManager().queryIntentActivities(intent, 0).size() > 0;
    }

    static boolean canCropPhoto(Context context) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        return context.getPackageManager().queryIntentActivities(intent, 0).size() > 0;
    }
}
