package com.android.settings.slices;

import android.net.Uri;
import android.util.ArrayMap;
import com.android.settings.display.AdaptiveSleepPreferenceController;
import com.android.settings.display.AlwaysOnDisplaySlice;
import com.android.settings.flashlight.FlashlightSlice;
import com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController;
import com.android.settings.homepage.contextualcards.deviceinfo.StorageSlice;
import com.android.settings.homepage.contextualcards.slices.BatteryFixSlice;
import com.android.settings.homepage.contextualcards.slices.BluetoothDevicesSlice;
import com.android.settings.homepage.contextualcards.slices.ContextualAdaptiveSleepSlice;
import com.android.settings.homepage.contextualcards.slices.DarkThemeSlice;
import com.android.settings.homepage.contextualcards.slices.FaceSetupSlice;
import com.android.settings.homepage.contextualcards.slices.LowStorageSlice;
import com.android.settings.location.LocationSlice;
import com.android.settings.media.MediaOutputGroupSlice;
import com.android.settings.media.MediaOutputIndicatorSlice;
import com.android.settings.media.MediaOutputSlice;
import com.android.settings.media.RemoteMediaSlice;
import com.android.settings.network.telephony.MobileDataSlice;
import com.android.settings.nfc.NfcPreferenceController;
import com.android.settings.wifi.slice.ContextualWifiSlice;
import com.android.settings.wifi.slice.WifiSlice;
import java.util.Map;

public class CustomSliceRegistry {
    public static final Uri ALWAYS_ON_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("always_on_display").build();
    public static final Uri BATTERY_FIX_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendEncodedPath("intent").appendPath(BatteryTipPreferenceController.PREF_NAME).build();
    public static final Uri BLUETOOTH_DEVICES_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("bluetooth_devices").build();
    public static final Uri BLUETOOTH_URI = new Uri.Builder().scheme("content").authority("android.settings.slices").appendPath("action").appendPath("bluetooth").build();
    public static final Uri CONTEXTUAL_ADAPTIVE_SLEEP_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("intent").appendPath(AdaptiveSleepPreferenceController.PREF_NAME).build();
    public static final Uri CONTEXTUAL_WIFI_SLICE_URI = new Uri.Builder().scheme("content").authority("android.settings.slices").appendPath("action").appendPath("contextual_wifi").build();
    public static final Uri DARK_THEME_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("dark_theme").build();
    public static final Uri ENHANCED_4G_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("enhanced_4g_lte").build();
    public static final Uri FACE_ENROLL_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("face_unlock_greeting_card").build();
    public static final Uri FLASHLIGHT_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("flashlight").build();
    public static final Uri LOCATION_SLICE_URI = new Uri.Builder().scheme("content").authority("android.settings.slices").appendPath("action").appendPath("location").build();
    public static final Uri LOW_STORAGE_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendEncodedPath("intent").appendPath("low_storage").build();
    public static Uri MEDIA_OUTPUT_GROUP_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("media_output_group").build();
    public static Uri MEDIA_OUTPUT_INDICATOR_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("intent").appendPath("media_output_indicator").build();
    public static Uri MEDIA_OUTPUT_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("media_output").build();
    public static final Uri MOBILE_DATA_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendEncodedPath("action").appendPath("mobile_data").build();
    public static final Uri NFC_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath(NfcPreferenceController.KEY_TOGGLE_NFC).build();
    public static Uri REMOTE_MEDIA_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("remote_media").build();
    public static final Uri STORAGE_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("intent").appendPath("storage_card").build();
    public static final Uri VOLUME_ALARM_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("alarm_volume").build();
    public static final Uri VOLUME_CALL_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("call_volume").build();
    public static final Uri VOLUME_MEDIA_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("media_volume").build();
    public static final Uri VOLUME_RINGER_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("ring_volume").build();
    public static final Uri WIFI_CALLING_PREFERENCE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("wifi_calling_preference").build();
    public static final Uri WIFI_CALLING_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("intent").appendPath("wifi_calling").build();
    public static final Uri WIFI_SLICE_URI = new Uri.Builder().scheme("content").authority("android.settings.slices").appendPath("action").appendPath("wifi").build();
    public static final Uri ZEN_MODE_SLICE_URI = new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath("zen_mode_toggle").build();
    static final Map<Uri, Class<? extends CustomSliceable>> sUriToSlice;

    static {
        ArrayMap arrayMap = new ArrayMap();
        sUriToSlice = arrayMap;
        arrayMap.put(BATTERY_FIX_SLICE_URI, BatteryFixSlice.class);
        sUriToSlice.put(BLUETOOTH_DEVICES_SLICE_URI, BluetoothDevicesSlice.class);
        sUriToSlice.put(CONTEXTUAL_ADAPTIVE_SLEEP_URI, ContextualAdaptiveSleepSlice.class);
        sUriToSlice.put(CONTEXTUAL_WIFI_SLICE_URI, ContextualWifiSlice.class);
        sUriToSlice.put(FACE_ENROLL_SLICE_URI, FaceSetupSlice.class);
        sUriToSlice.put(FLASHLIGHT_SLICE_URI, FlashlightSlice.class);
        sUriToSlice.put(LOCATION_SLICE_URI, LocationSlice.class);
        sUriToSlice.put(LOW_STORAGE_SLICE_URI, LowStorageSlice.class);
        sUriToSlice.put(MEDIA_OUTPUT_INDICATOR_SLICE_URI, MediaOutputIndicatorSlice.class);
        sUriToSlice.put(MEDIA_OUTPUT_SLICE_URI, MediaOutputSlice.class);
        sUriToSlice.put(MOBILE_DATA_SLICE_URI, MobileDataSlice.class);
        sUriToSlice.put(STORAGE_SLICE_URI, StorageSlice.class);
        sUriToSlice.put(WIFI_SLICE_URI, WifiSlice.class);
        sUriToSlice.put(DARK_THEME_SLICE_URI, DarkThemeSlice.class);
        sUriToSlice.put(REMOTE_MEDIA_SLICE_URI, RemoteMediaSlice.class);
        sUriToSlice.put(MEDIA_OUTPUT_GROUP_SLICE_URI, MediaOutputGroupSlice.class);
        sUriToSlice.put(ALWAYS_ON_SLICE_URI, AlwaysOnDisplaySlice.class);
    }

    public static Class<? extends CustomSliceable> getSliceClassByUri(Uri uri) {
        return sUriToSlice.get(removeParameterFromUri(uri));
    }

    public static Uri removeParameterFromUri(Uri uri) {
        if (uri != null) {
            return uri.buildUpon().clearQuery().build();
        }
        return null;
    }

    public static boolean isValidUri(Uri uri) {
        return sUriToSlice.containsKey(removeParameterFromUri(uri));
    }

    public static boolean isValidAction(String str) {
        return isValidUri(Uri.parse(str));
    }
}
