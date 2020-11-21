package com.android.settings.display;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.ColorDisplayManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.widget.LayoutPreference;
import java.time.LocalTime;

public class NightDisplayActivationPreferenceController extends TogglePreferenceController {
    private ColorDisplayManager mColorDisplayManager;
    private final View.OnClickListener mListener = new View.OnClickListener() {
        /* class com.android.settings.display.NightDisplayActivationPreferenceController.AnonymousClass1 */

        public void onClick(View view) {
            NightDisplayActivationPreferenceController.this.mMetricsFeatureProvider.logClickedPreference(NightDisplayActivationPreferenceController.this.mPreference, NightDisplayActivationPreferenceController.this.getMetricsCategory());
            NightDisplayActivationPreferenceController.this.mColorDisplayManager.setNightDisplayActivated(!NightDisplayActivationPreferenceController.this.mColorDisplayManager.isNightDisplayActivated());
            NightDisplayActivationPreferenceController.this.updateStateInternal(true);
        }
    };
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private LayoutPreference mPreference;
    private NightDisplayTimeFormatter mTimeFormatter;
    private Button mTurnOffButton;
    private Button mTurnOnButton;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public NightDisplayActivationPreferenceController(Context context, String str) {
        super(context, str);
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
        this.mTimeFormatter = new NightDisplayTimeFormatter(context);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return ColorDisplayManager.isNightDisplayAvailable(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "night_display_activated");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = layoutPreference;
        Button button = (Button) layoutPreference.findViewById(C0010R$id.night_display_turn_on_button);
        this.mTurnOnButton = button;
        button.setOnClickListener(this.mListener);
        Button button2 = (Button) this.mPreference.findViewById(C0010R$id.night_display_turn_off_button);
        this.mTurnOffButton = button2;
        button2.setOnClickListener(this.mListener);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.TogglePreferenceController
    public final void updateState(Preference preference) {
        updateStateInternal(false);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mColorDisplayManager.isNightDisplayActivated();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return this.mColorDisplayManager.setNightDisplayActivated(z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mTimeFormatter.getAutoModeSummary(this.mContext, this.mColorDisplayManager);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateStateInternal(boolean z) {
        String str;
        int i;
        int i2;
        int i3;
        LocalTime localTime;
        if (this.mTurnOnButton != null && this.mTurnOffButton != null) {
            boolean isNightDisplayActivated = this.mColorDisplayManager.isNightDisplayActivated();
            int nightDisplayAutoMode = this.mColorDisplayManager.getNightDisplayAutoMode();
            if (nightDisplayAutoMode == 1) {
                Context context = this.mContext;
                if (isNightDisplayActivated) {
                    i3 = C0017R$string.night_display_activation_off_custom;
                } else {
                    i3 = C0017R$string.night_display_activation_on_custom;
                }
                Object[] objArr = new Object[1];
                NightDisplayTimeFormatter nightDisplayTimeFormatter = this.mTimeFormatter;
                if (isNightDisplayActivated) {
                    localTime = this.mColorDisplayManager.getNightDisplayCustomStartTime();
                } else {
                    localTime = this.mColorDisplayManager.getNightDisplayCustomEndTime();
                }
                objArr[0] = nightDisplayTimeFormatter.getFormattedTimeString(localTime);
                str = context.getString(i3, objArr);
            } else if (nightDisplayAutoMode == 2) {
                Context context2 = this.mContext;
                if (isNightDisplayActivated) {
                    i2 = C0017R$string.night_display_activation_off_twilight;
                } else {
                    i2 = C0017R$string.night_display_activation_on_twilight;
                }
                str = context2.getString(i2);
            } else {
                Context context3 = this.mContext;
                if (isNightDisplayActivated) {
                    i = C0017R$string.night_display_activation_off_manual;
                } else {
                    i = C0017R$string.night_display_activation_on_manual;
                }
                str = context3.getString(i);
            }
            if (isNightDisplayActivated) {
                this.mTurnOnButton.setVisibility(8);
                this.mTurnOffButton.setVisibility(0);
                this.mTurnOffButton.setText(str);
                if (z) {
                    this.mTurnOffButton.sendAccessibilityEvent(8);
                    return;
                }
                return;
            }
            this.mTurnOnButton.setVisibility(0);
            this.mTurnOffButton.setVisibility(8);
            this.mTurnOnButton.setText(str);
            if (z) {
                this.mTurnOnButton.sendAccessibilityEvent(8);
            }
        }
    }
}
