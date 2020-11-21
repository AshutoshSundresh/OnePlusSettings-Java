package com.android.settings.wallpaper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.display.WallpaperPreferenceController;
import com.google.android.setupcompat.util.WizardManagerHelper;

public abstract class StyleSuggestionActivityBase extends Activity {
    /* access modifiers changed from: protected */
    public void addExtras(Intent intent) {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PackageManager packageManager = getPackageManager();
        Intent addFlags = new Intent().setComponent(new WallpaperPreferenceController(this, "dummy key").getComponentName()).addFlags(33554432);
        WizardManagerHelper.copyWizardManagerExtras(getIntent(), addFlags);
        addExtras(addFlags);
        if (packageManager.resolveActivity(addFlags, 0) != null) {
            startActivity(addFlags);
        } else {
            startFallbackSuggestion();
        }
        finish();
    }

    /* access modifiers changed from: package-private */
    public void startFallbackSuggestion() {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this);
        subSettingLauncher.setDestination(WallpaperTypeSettings.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.wallpaper_suggestion_title);
        subSettingLauncher.setSourceMetricsCategory(35);
        subSettingLauncher.addFlags(33554432);
        subSettingLauncher.launch();
    }

    protected static boolean isWallpaperServiceEnabled(Context context) {
        return context.getResources().getBoolean(17891456);
    }
}
