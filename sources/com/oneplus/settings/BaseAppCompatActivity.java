package com.oneplus.settings;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.oneplus.settings.utils.OPUtils;

public class BaseAppCompatActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle) {
        OPUtils.setLightNavigationBar(getWindow(), OPUtils.getThemeMode(getContentResolver()));
        super.onCreate(bundle);
    }
}
