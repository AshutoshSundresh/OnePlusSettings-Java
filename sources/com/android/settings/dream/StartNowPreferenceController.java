package com.android.settings.dream;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0010R$id;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.dream.DreamBackend;
import com.android.settingslib.widget.LayoutPreference;

public class StartNowPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final DreamBackend mBackend;
    private final MetricsFeatureProvider mMetricsFeatureProvider;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "dream_start_now_button_container";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public StartNowPreferenceController(Context context) {
        super(context);
        this.mBackend = DreamBackend.getInstance(context);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        ((Button) layoutPreference.findViewById(C0010R$id.dream_start_now_button)).setOnClickListener(new View.OnClickListener(layoutPreference) {
            /* class com.android.settings.dream.$$Lambda$StartNowPreferenceController$jHhaL9YNqQfg4aZuushndV2Tc_Y */
            public final /* synthetic */ LayoutPreference f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                StartNowPreferenceController.this.lambda$displayPreference$0$StartNowPreferenceController(this.f$1, view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$StartNowPreferenceController(LayoutPreference layoutPreference, View view) {
        this.mMetricsFeatureProvider.logClickedPreference(layoutPreference, layoutPreference.getExtras().getInt("category"));
        this.mBackend.startDreaming();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ((Button) ((LayoutPreference) preference).findViewById(C0010R$id.dream_start_now_button)).setEnabled(this.mBackend.getWhenToDreamSetting() != 3);
    }
}
