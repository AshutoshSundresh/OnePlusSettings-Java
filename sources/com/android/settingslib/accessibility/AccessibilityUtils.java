package com.android.settingslib.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AccessibilityUtils {
    public static Set<ComponentName> getEnabledServicesFromSettings(Context context) {
        return getEnabledServicesFromSettings(context, UserHandle.myUserId());
    }

    public static Set<ComponentName> getEnabledServicesFromSettings(Context context, int i) {
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "enabled_accessibility_services", i);
        if (TextUtils.isEmpty(stringForUser)) {
            return Collections.emptySet();
        }
        HashSet hashSet = new HashSet();
        TextUtils.SimpleStringSplitter<String> simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
        simpleStringSplitter.setString(stringForUser);
        for (String str : simpleStringSplitter) {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
            if (unflattenFromString != null) {
                hashSet.add(unflattenFromString);
            }
        }
        return hashSet;
    }

    public static CharSequence getTextForLocale(Context context, Locale locale, int i) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration).getText(i);
    }

    public static void setAccessibilityServiceState(Context context, ComponentName componentName, boolean z) {
        setAccessibilityServiceState(context, componentName, z, UserHandle.myUserId());
    }

    /* JADX WARNING: Removed duplicated region for block: B:8:0x0027  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void setAccessibilityServiceState(android.content.Context r3, android.content.ComponentName r4, boolean r5, int r6) {
        /*
        // Method dump skipped, instructions count: 109
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.accessibility.AccessibilityUtils.setAccessibilityServiceState(android.content.Context, android.content.ComponentName, boolean, int):void");
    }

    public static String getShortcutTargetServiceComponentNameString(Context context, int i) {
        String stringForUser = Settings.Secure.getStringForUser(context.getContentResolver(), "accessibility_shortcut_target_service", i);
        if (stringForUser != null) {
            return stringForUser;
        }
        return context.getString(17039865);
    }

    private static Set<ComponentName> getInstalledServices(Context context) {
        HashSet hashSet = new HashSet();
        hashSet.clear();
        List<AccessibilityServiceInfo> installedAccessibilityServiceList = AccessibilityManager.getInstance(context).getInstalledAccessibilityServiceList();
        if (installedAccessibilityServiceList == null) {
            return hashSet;
        }
        for (AccessibilityServiceInfo accessibilityServiceInfo : installedAccessibilityServiceList) {
            ServiceInfo serviceInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo;
            hashSet.add(new ComponentName(serviceInfo.packageName, serviceInfo.name));
        }
        return hashSet;
    }
}
