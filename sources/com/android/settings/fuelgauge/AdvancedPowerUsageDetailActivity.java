package com.android.settings.fuelgauge;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.android.settings.C0017R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settingslib.Utils;
import com.oneplus.settings.BaseAppCompatActivity;

public class AdvancedPowerUsageDetailActivity extends BaseAppCompatActivity {
    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        Uri uri;
        super.onCreate(bundle);
        Intent intent = getIntent();
        String str = null;
        if (intent == null) {
            uri = null;
        } else {
            uri = intent.getData();
        }
        if (uri != null) {
            str = uri.getSchemeSpecificPart();
        }
        if (str != null) {
            Bundle bundle2 = new Bundle(4);
            PackageManager packageManager = getPackageManager();
            bundle2.putString("extra_package_name", str);
            bundle2.putString("extra_power_usage_percent", Utils.formatPercentage(0));
            if (intent.getBooleanExtra("request_ignore_background_restriction", false)) {
                bundle2.putString(":settings:fragment_args_key", "background_activity");
            }
            try {
                bundle2.putInt("extra_uid", packageManager.getPackageUid(str, 0));
            } catch (PackageManager.NameNotFoundException e) {
                Log.w("AdvancedPowerDetailActivity", "Cannot find package: " + str, e);
            }
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this);
            subSettingLauncher.setDestination(AdvancedPowerUsageDetail.class.getName());
            subSettingLauncher.setTitleRes(C0017R$string.battery_details_title);
            subSettingLauncher.setArguments(bundle2);
            subSettingLauncher.setSourceMetricsCategory(20);
            subSettingLauncher.launch();
        }
        finish();
    }
}
