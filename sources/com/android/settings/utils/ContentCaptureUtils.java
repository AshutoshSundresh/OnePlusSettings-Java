package com.android.settings.utils;

import android.content.ComponentName;
import android.content.Context;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.contentcapture.ContentCaptureManager;

public final class ContentCaptureUtils {
    private static final int MY_USER_ID = UserHandle.myUserId();
    private static final String TAG = "ContentCaptureUtils";

    public static boolean isEnabledForUser(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "content_capture_enabled", 1, MY_USER_ID) == 1;
    }

    public static void setEnabledForUser(Context context, boolean z) {
        Settings.Secure.putIntForUser(context.getContentResolver(), "content_capture_enabled", z ? 1 : 0, MY_USER_ID);
    }

    public static boolean isFeatureAvailable() {
        return ServiceManager.checkService("content_capture") != null;
    }

    public static ComponentName getServiceSettingsComponentName() {
        try {
            return ContentCaptureManager.getServiceSettingsComponentName();
        } catch (RuntimeException e) {
            String str = TAG;
            Log.w(str, "Could not get service settings: " + e);
            return null;
        }
    }
}
