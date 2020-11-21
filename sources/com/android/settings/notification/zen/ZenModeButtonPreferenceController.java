package com.android.settings.notification.zen;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import com.android.settings.C0010R$id;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.SettingsEnableZenModeDialog;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.widget.LayoutPreference;

public class ZenModeButtonPreferenceController extends AbstractZenModePreferenceController implements PreferenceControllerMixin {
    private final FragmentManager mFragment;
    private boolean mRefocusButton = false;
    private Button mZenButtonOff;
    private Button mZenButtonOn;

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.notification.zen.AbstractZenModePreferenceController
    public String getPreferenceKey() {
        return "zen_mode_toggle";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return false;
    }

    public ZenModeButtonPreferenceController(Context context, Lifecycle lifecycle, FragmentManager fragmentManager) {
        super(context, "zen_mode_toggle", lifecycle);
        this.mFragment = fragmentManager;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mZenButtonOn == null) {
            this.mZenButtonOn = (Button) ((LayoutPreference) preference).findViewById(C0010R$id.zen_mode_settings_turn_on_button);
            updateZenButtonOnClickListener(preference);
        }
        if (this.mZenButtonOff == null) {
            Button button = (Button) ((LayoutPreference) preference).findViewById(C0010R$id.zen_mode_settings_turn_off_button);
            this.mZenButtonOff = button;
            button.setOnClickListener(new View.OnClickListener(preference) {
                /* class com.android.settings.notification.zen.$$Lambda$ZenModeButtonPreferenceController$zkkVIDQ6jv3UDSHqPhDVNyIzCIg */
                public final /* synthetic */ Preference f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    ZenModeButtonPreferenceController.this.lambda$updateState$0$ZenModeButtonPreferenceController(this.f$1, view);
                }
            });
        }
        updatePreference(preference);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateState$0 */
    public /* synthetic */ void lambda$updateState$0$ZenModeButtonPreferenceController(Preference preference, View view) {
        this.mRefocusButton = true;
        writeMetrics(preference, false);
        this.mBackend.setZenMode(0);
    }

    private void updatePreference(Preference preference) {
        int zenMode = getZenMode();
        if (zenMode == 1 || zenMode == 2 || zenMode == 3) {
            this.mZenButtonOff.setVisibility(0);
            this.mZenButtonOn.setVisibility(8);
            if (this.mRefocusButton) {
                this.mRefocusButton = false;
                this.mZenButtonOff.sendAccessibilityEvent(8);
                return;
            }
            return;
        }
        this.mZenButtonOff.setVisibility(8);
        updateZenButtonOnClickListener(preference);
        this.mZenButtonOn.setVisibility(0);
        if (this.mRefocusButton) {
            this.mRefocusButton = false;
            this.mZenButtonOn.sendAccessibilityEvent(8);
        }
    }

    private void updateZenButtonOnClickListener(Preference preference) {
        this.mZenButtonOn.setOnClickListener(new View.OnClickListener(preference) {
            /* class com.android.settings.notification.zen.$$Lambda$ZenModeButtonPreferenceController$aWUCvlWjezNFqsdFvxW7if76XBs */
            public final /* synthetic */ Preference f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                ZenModeButtonPreferenceController.this.lambda$updateZenButtonOnClickListener$1$ZenModeButtonPreferenceController(this.f$1, view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateZenButtonOnClickListener$1 */
    public /* synthetic */ void lambda$updateZenButtonOnClickListener$1$ZenModeButtonPreferenceController(Preference preference, View view) {
        this.mRefocusButton = true;
        writeMetrics(preference, true);
        int zenDuration = getZenDuration();
        if (zenDuration == -1) {
            new SettingsEnableZenModeDialog().show(this.mFragment, "EnableZenModeButton");
        } else if (zenDuration != 0) {
            this.mBackend.setZenModeForDuration(zenDuration);
        } else {
            this.mBackend.setZenMode(1);
        }
    }

    private void writeMetrics(Preference preference, boolean z) {
        this.mMetricsFeatureProvider.logClickedPreference(preference, preference.getExtras().getInt("category"));
        this.mMetricsFeatureProvider.action(this.mContext, 1268, z);
    }
}
