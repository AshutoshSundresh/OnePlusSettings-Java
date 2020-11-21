package com.android.settings.notification.zen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ZenSuggestionActivity extends Activity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        startActivity(new Intent("android.settings.ZEN_MODE_SETTINGS"));
        startActivity(new Intent("android.settings.ZEN_MODE_ONBOARDING"));
        finish();
    }
}
