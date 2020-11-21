package com.android.settings;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.io.File;

public class ManualDisplayActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!getResources().getBoolean(C0005R$bool.config_show_manual)) {
            finish();
        }
        File file = new File("/system/etc/MANUAL.html.gz");
        if (!file.exists() || file.length() == 0) {
            Log.e("SettingsManualActivity", "Manual file /system/etc/MANUAL.html.gz does not exist");
            showErrorAndFinish();
            return;
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "text/html");
        intent.putExtra("android.intent.extra.TITLE", getString(C0017R$string.settings_manual_activity_title));
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setPackage("com.android.htmlviewer");
        try {
            startActivity(intent);
            finish();
        } catch (ActivityNotFoundException e) {
            Log.e("SettingsManualActivity", "Failed to find viewer", e);
            showErrorAndFinish();
        }
    }

    private void showErrorAndFinish() {
        Toast.makeText(this, C0017R$string.settings_manual_activity_unavailable, 1).show();
        finish();
    }
}
