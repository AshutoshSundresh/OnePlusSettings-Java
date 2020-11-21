package com.android.settings;

import android.util.Log;

public class SubSettings extends SettingsActivity {
    @Override // com.android.settings.core.SettingsBaseActivity
    public boolean onNavigateUp() {
        finish();
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public boolean isValidFragment(String str) {
        Log.d("SubSettings", "Launching fragment " + str);
        return true;
    }
}
