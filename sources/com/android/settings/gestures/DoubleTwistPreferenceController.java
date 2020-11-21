package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.slices.SliceBackgroundWorker;

public class DoubleTwistPreferenceController extends GesturePreferenceController {
    private static final String PREF_KEY_VIDEO = "gesture_double_twist_video";
    private final int OFF = 0;
    private final int ON = 1;
    private final String mDoubleTwistPrefKey;
    private final UserManager mUserManager;

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

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.gestures.GesturePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DoubleTwistPreferenceController(Context context, String str) {
        super(context, str);
        this.mDoubleTwistPrefKey = str;
        this.mUserManager = (UserManager) context.getSystemService("user");
    }

    public static boolean isSuggestionComplete(Context context, SharedPreferences sharedPreferences) {
        if (!isGestureAvailable(context) || sharedPreferences.getBoolean("pref_double_twist_suggestion_complete", false)) {
            return true;
        }
        return false;
    }

    public static boolean isGestureAvailable(Context context) {
        Resources resources = context.getResources();
        String string = resources.getString(C0017R$string.gesture_double_twist_sensor_name);
        String string2 = resources.getString(C0017R$string.gesture_double_twist_sensor_vendor);
        if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string2)) {
            return false;
        }
        for (Sensor sensor : ((SensorManager) context.getSystemService("sensor")).getSensorList(-1)) {
            if (string.equals(sensor.getName()) && string2.equals(sensor.getVendor())) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isGestureAvailable(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "gesture_double_twist");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public String getPreferenceKey() {
        return this.mDoubleTwistPrefKey;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        setDoubleTwistPreference(this.mContext, this.mUserManager, z ? 1 : 0);
        return true;
    }

    public static void setDoubleTwistPreference(Context context, UserManager userManager, int i) {
        Settings.Secure.putInt(context.getContentResolver(), "camera_double_twist_to_flip_enabled", i);
        int managedProfileId = getManagedProfileId(userManager);
        if (managedProfileId != -10000) {
            Settings.Secure.putIntForUser(context.getContentResolver(), "camera_double_twist_to_flip_enabled", i, managedProfileId);
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), "camera_double_twist_to_flip_enabled", 1) != 0;
    }

    public static int getManagedProfileId(UserManager userManager) {
        return Utils.getManagedProfileId(userManager, UserHandle.myUserId());
    }
}
