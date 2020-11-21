package com.android.settings.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.android.settings.SubSettings;
import com.android.settings.overlay.FeatureFactory;

public class SearchResultTrampoline extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FeatureFactory.getFactory(this).getSearchFeatureProvider().verifyLaunchSearchResultPageCaller(this, getCallingActivity());
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra(":settings:fragment_args_key");
        int intExtra = intent.getIntExtra(":settings:show_fragment_tab", 0);
        Bundle bundle2 = new Bundle();
        bundle2.putString(":settings:fragment_args_key", stringExtra);
        bundle2.putInt(":settings:show_fragment_tab", intExtra);
        intent.putExtra(":settings:show_fragment_args", bundle2);
        intent.setClass(this, SubSettings.class).addFlags(33554432);
        startActivity(intent);
        finish();
    }
}
