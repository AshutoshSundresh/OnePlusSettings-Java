package com.android.settings.core;

import android.os.Bundle;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.android.settingslib.core.instrumentation.VisibilityLoggerMixin;
import com.android.settingslib.core.lifecycle.ObservableActivity;

public abstract class InstrumentedActivity extends ObservableActivity implements Instrumentable {
    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getSettingsLifecycle().addObserver(new VisibilityLoggerMixin(getMetricsCategory(), FeatureFactory.getFactory(this).getMetricsFeatureProvider()));
    }
}
