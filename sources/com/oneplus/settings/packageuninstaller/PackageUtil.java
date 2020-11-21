package com.oneplus.settings.packageuninstaller;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.C0010R$id;

public class PackageUtil {
    public static View initSnippet(View view, CharSequence charSequence, Drawable drawable) {
        ((ImageView) view.findViewById(C0010R$id.app_icon)).setImageDrawable(drawable);
        ((TextView) view.findViewById(C0010R$id.app_name)).setText(charSequence);
        return view;
    }

    public static View initSnippetForInstalledApp(Context context, ApplicationInfo applicationInfo, View view) {
        initSnippetForInstalledApp(context, applicationInfo, view, null);
        return view;
    }

    public static View initSnippetForInstalledApp(Context context, ApplicationInfo applicationInfo, View view, UserHandle userHandle) {
        PackageManager packageManager = context.getPackageManager();
        Drawable loadIcon = applicationInfo.loadIcon(packageManager);
        if (userHandle != null) {
            loadIcon = context.getPackageManager().getUserBadgedIcon(loadIcon, userHandle);
        }
        initSnippet(view, applicationInfo.loadLabel(packageManager), loadIcon);
        return view;
    }

    static int getMaxTargetSdkVersionForUid(Context context, int i) {
        PackageManager packageManager = context.getPackageManager();
        String[] packagesForUid = packageManager.getPackagesForUid(i);
        int i2 = -1;
        if (packagesForUid != null) {
            for (String str : packagesForUid) {
                try {
                    i2 = Math.max(i2, packageManager.getApplicationInfo(str, 0).targetSdkVersion);
                } catch (PackageManager.NameNotFoundException unused) {
                }
            }
        }
        return i2;
    }
}
