package com.android.settings;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.android.settingslib.HelpUtils;

public class HelpTrampoline extends Activity {
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            String stringExtra = getIntent().getStringExtra("android.intent.extra.TEXT");
            if (TextUtils.isEmpty(stringExtra)) {
                finishAndRemoveTask();
                return;
            }
            Intent helpIntent = HelpUtils.getHelpIntent(this, getResources().getString(getResources().getIdentifier(stringExtra, "string", getPackageName())), null);
            if (helpIntent != null) {
                startActivityForResult(helpIntent, 0);
            }
            finish();
        } catch (ActivityNotFoundException | Resources.NotFoundException e) {
            Log.w("HelpTrampoline", "Failed to resolve help", e);
        }
    }
}
