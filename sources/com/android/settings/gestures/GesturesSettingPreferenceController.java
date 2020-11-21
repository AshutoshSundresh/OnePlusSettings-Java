package com.android.settings.gestures;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.AmbientDisplayConfiguration;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.settings.C0017R$string;
import com.android.settings.aware.AwareFeatureProvider;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GesturesSettingPreferenceController extends BasePreferenceController {
    private static final String FAKE_PREF_KEY = "fake_key_only_for_get_available";
    private static final String KEY_GESTURES_SETTINGS = "gesture_settings";
    private final AwareFeatureProvider mAwareFeatureProvider;
    private final AssistGestureFeatureProvider mFeatureProvider;
    private List<AbstractPreferenceController> mGestureControllers;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
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

    public GesturesSettingPreferenceController(Context context) {
        super(context, KEY_GESTURES_SETTINGS);
        this.mFeatureProvider = FeatureFactory.getFactory(context).getAssistGestureFeatureProvider();
        this.mAwareFeatureProvider = FeatureFactory.getFactory(context).getAwareFeatureProvider();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mGestureControllers == null) {
            this.mGestureControllers = buildAllPreferenceControllers(this.mContext);
        }
        Iterator<AbstractPreferenceController> it = this.mGestureControllers.iterator();
        while (true) {
            boolean z = false;
            while (true) {
                if (!it.hasNext()) {
                    return 3;
                }
                AbstractPreferenceController next = it.next();
                if (z || next.isAvailable()) {
                    z = true;
                }
            }
        }
    }

    private static List<AbstractPreferenceController> buildAllPreferenceControllers(Context context) {
        AmbientDisplayConfiguration ambientDisplayConfiguration = new AmbientDisplayConfiguration(context);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new AssistGestureSettingsPreferenceController(context, FAKE_PREF_KEY).setAssistOnly(false));
        arrayList.add(new SwipeToNotificationPreferenceController(context, FAKE_PREF_KEY));
        arrayList.add(new DoubleTwistPreferenceController(context, FAKE_PREF_KEY));
        arrayList.add(new DoubleTapPowerPreferenceController(context, FAKE_PREF_KEY));
        arrayList.add(new PickupGesturePreferenceController(context, FAKE_PREF_KEY).setConfig(ambientDisplayConfiguration));
        arrayList.add(new DoubleTapScreenPreferenceController(context, FAKE_PREF_KEY).setConfig(ambientDisplayConfiguration));
        arrayList.add(new PreventRingingParentPreferenceController(context, FAKE_PREF_KEY));
        return arrayList;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (!this.mFeatureProvider.isSensorAvailable(this.mContext)) {
            return "";
        }
        ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean z = true;
        boolean z2 = Settings.Secure.getInt(contentResolver, "assist_gesture_enabled", 1) != 0;
        if (Settings.Secure.getInt(contentResolver, "assist_gesture_silence_alerts_enabled", 1) == 0) {
            z = false;
        }
        boolean isSupported = this.mFeatureProvider.isSupported(this.mContext);
        CharSequence gestureSummary = this.mAwareFeatureProvider.getGestureSummary(this.mContext, isSupported, z2, z);
        if (!TextUtils.isEmpty(gestureSummary)) {
            return gestureSummary;
        }
        if (isSupported && z2) {
            return this.mContext.getText(C0017R$string.language_input_gesture_summary_on_with_assist);
        }
        if (z) {
            return this.mContext.getText(C0017R$string.language_input_gesture_summary_on_non_assist);
        }
        return this.mContext.getText(C0017R$string.language_input_gesture_summary_off);
    }
}
