package com.android.settings.bluetooth;

import android.os.Bundle;
import com.android.settings.C0012R$layout;
import com.oneplus.settings.BaseAppCompatActivity;
import com.oneplus.settings.utils.OPUtils;

public final class DevicePickerActivity extends BaseAppCompatActivity {
    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        OPUtils.setLightNavigationBar(getWindow(), OPUtils.getThemeMode(getContentResolver()));
        setContentView(C0012R$layout.bluetooth_device_picker);
    }
}
