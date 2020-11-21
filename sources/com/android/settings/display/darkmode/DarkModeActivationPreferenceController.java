package com.android.settings.display.darkmode;

import android.app.UiModeManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.widget.LayoutPreference;
import java.time.LocalTime;

public class DarkModeActivationPreferenceController extends BasePreferenceController {
    private TimeFormatter mFormat;
    private final View.OnClickListener mListener;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private PowerManager mPowerManager;
    private LayoutPreference mPreference;
    private Button mTurnOffButton;
    private Button mTurnOnButton;
    private final UiModeManager mUiModeManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DarkModeActivationPreferenceController(Context context, String str) {
        super(context, str);
        this.mListener = new View.OnClickListener() {
            /* class com.android.settings.display.darkmode.DarkModeActivationPreferenceController.AnonymousClass1 */

            public void onClick(View view) {
                DarkModeActivationPreferenceController.this.mMetricsFeatureProvider.logClickedPreference(DarkModeActivationPreferenceController.this.mPreference, DarkModeActivationPreferenceController.this.getMetricsCategory());
                boolean z = (((AbstractPreferenceController) DarkModeActivationPreferenceController.this).mContext.getResources().getConfiguration().uiMode & 32) != 0;
                DarkModeActivationPreferenceController.this.mUiModeManager.setNightModeActivated(!z);
                DarkModeActivationPreferenceController.this.updateNightMode(!z);
            }
        };
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
        this.mFormat = new TimeFormatter(context);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    public DarkModeActivationPreferenceController(Context context, String str, TimeFormatter timeFormatter) {
        this(context, str);
        this.mFormat = timeFormatter;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public final void updateState(Preference preference) {
        if (this.mPowerManager.isPowerSaveMode()) {
            this.mTurnOnButton.setVisibility(8);
            this.mTurnOffButton.setVisibility(8);
            return;
        }
        updateNightMode((this.mContext.getResources().getConfiguration().uiMode & 32) != 0);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateNightMode(boolean z) {
        String str;
        int i;
        LocalTime localTime;
        int i2;
        int i3;
        int nightMode = this.mUiModeManager.getNightMode();
        if (nightMode == 0) {
            Context context = this.mContext;
            if (z) {
                i3 = C0017R$string.dark_ui_activation_off_auto;
            } else {
                i3 = C0017R$string.dark_ui_activation_on_auto;
            }
            str = context.getString(i3);
        } else if (nightMode == 3) {
            if (z) {
                localTime = this.mUiModeManager.getCustomNightModeStart();
            } else {
                localTime = this.mUiModeManager.getCustomNightModeEnd();
            }
            String of = this.mFormat.of(localTime);
            Context context2 = this.mContext;
            if (z) {
                i2 = C0017R$string.dark_ui_activation_off_custom;
            } else {
                i2 = C0017R$string.dark_ui_activation_on_custom;
            }
            str = context2.getString(i2, of);
        } else {
            Context context3 = this.mContext;
            if (z) {
                i = C0017R$string.dark_ui_activation_off_manual;
            } else {
                i = C0017R$string.dark_ui_activation_on_manual;
            }
            str = context3.getString(i);
        }
        if (z) {
            this.mTurnOnButton.setVisibility(8);
            this.mTurnOffButton.setVisibility(0);
            this.mTurnOffButton.setText(str);
            return;
        }
        this.mTurnOnButton.setVisibility(0);
        this.mTurnOffButton.setVisibility(8);
        this.mTurnOnButton.setText(str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i;
        LocalTime localTime;
        int i2;
        int i3;
        boolean z = (this.mContext.getResources().getConfiguration().uiMode & 32) != 0;
        int nightMode = this.mUiModeManager.getNightMode();
        if (nightMode == 0) {
            Context context = this.mContext;
            if (z) {
                i3 = C0017R$string.dark_ui_summary_on_auto_mode_auto;
            } else {
                i3 = C0017R$string.dark_ui_summary_off_auto_mode_auto;
            }
            return context.getString(i3);
        } else if (nightMode == 3) {
            if (z) {
                localTime = this.mUiModeManager.getCustomNightModeEnd();
            } else {
                localTime = this.mUiModeManager.getCustomNightModeStart();
            }
            String of = this.mFormat.of(localTime);
            Context context2 = this.mContext;
            if (z) {
                i2 = C0017R$string.dark_ui_summary_on_auto_mode_custom;
            } else {
                i2 = C0017R$string.dark_ui_summary_off_auto_mode_custom;
            }
            return context2.getString(i2, of);
        } else {
            Context context3 = this.mContext;
            if (z) {
                i = C0017R$string.dark_ui_summary_on_auto_mode_never;
            } else {
                i = C0017R$string.dark_ui_summary_off_auto_mode_never;
            }
            return context3.getString(i);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        LayoutPreference layoutPreference = (LayoutPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = layoutPreference;
        Button button = (Button) layoutPreference.findViewById(C0010R$id.dark_ui_turn_on_button);
        this.mTurnOnButton = button;
        button.setOnClickListener(this.mListener);
        Button button2 = (Button) this.mPreference.findViewById(C0010R$id.dark_ui_turn_off_button);
        this.mTurnOffButton = button2;
        button2.setOnClickListener(this.mListener);
    }
}
