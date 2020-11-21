package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;

public class AssistGestureSettingsPreferenceController extends GesturePreferenceController {
    private static final int OFF = 0;
    private static final int ON = 1;
    private static final String PREF_KEY_VIDEO = "gesture_assist_video";
    private static final String SECURE_KEY_ASSIST = "assist_gesture_enabled";
    private static final String SECURE_KEY_SILENCE = "assist_gesture_silence_alerts_enabled";
    private static final String TAG = "AssistGesture";
    boolean mAssistOnly;
    private final AssistGestureFeatureProvider mFeatureProvider;
    private Preference mPreference;
    private PreferenceScreen mScreen;
    private boolean mWasAvailable = isAvailable();

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.gestures.GesturePreferenceController
    public String getVideoPrefKey() {
        return PREF_KEY_VIDEO;
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AssistGestureSettingsPreferenceController(Context context, String str) {
        super(context, str);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getAssistGestureFeatureProvider();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        boolean isSupported = this.mFeatureProvider.isSupported(this.mContext);
        boolean isSensorAvailable = this.mFeatureProvider.isSensorAvailable(this.mContext);
        boolean z = this.mAssistOnly ? isSupported : isSensorAvailable;
        Log.d(TAG, "mAssistOnly:" + this.mAssistOnly + ", isSupported:" + isSupported + ", isSensorAvailable:" + isSensorAvailable);
        return z ? 0 : 3;
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mScreen = preferenceScreen;
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (this.mWasAvailable != isAvailable()) {
            updatePreference();
            this.mWasAvailable = isAvailable();
        }
    }

    public AssistGestureSettingsPreferenceController setAssistOnly(boolean z) {
        this.mAssistOnly = z;
        return this;
    }

    private void updatePreference() {
        if (this.mPreference != null) {
            if (!isAvailable()) {
                this.mScreen.removePreference(this.mPreference);
            } else if (this.mScreen.findPreference(getPreferenceKey()) == null) {
                this.mScreen.addPreference(this.mPreference);
            }
        }
    }

    private boolean isAssistGestureEnabled() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), SECURE_KEY_ASSIST, 1) != 0;
    }

    private boolean isSilenceGestureEnabled() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), SECURE_KEY_SILENCE, 1) != 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return Settings.Secure.putInt(this.mContext.getContentResolver(), SECURE_KEY_ASSIST, z ? 1 : 0);
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        boolean z = true;
        boolean z2 = isAssistGestureEnabled() && this.mFeatureProvider.isSupported(this.mContext);
        if (!this.mAssistOnly) {
            if (!z2 && !isSilenceGestureEnabled()) {
                z = false;
            }
            z2 = z;
        }
        return this.mContext.getText(z2 ? C0017R$string.gesture_setting_on : C0017R$string.gesture_setting_off);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), SECURE_KEY_ASSIST, 0) == 1;
    }
}
