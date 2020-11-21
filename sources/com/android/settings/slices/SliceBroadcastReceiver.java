package com.android.settings.slices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.bluetooth.BluetoothSliceBuilder;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SliderPreferenceController;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.notification.zen.ZenModeSliceBuilder;
import com.android.settings.overlay.FeatureFactory;

public class SliceBroadcastReceiver extends BroadcastReceiver {
    private static String TAG = "SettSliceBroadcastRec";

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    public void onReceive(Context context, Intent intent) {
        char c;
        String action = intent.getAction();
        String stringExtra = intent.getStringExtra("com.android.settings.slice.extra.key");
        if (CustomSliceRegistry.isValidAction(action)) {
            CustomSliceable.createInstance(context, CustomSliceRegistry.getSliceClassByUri(Uri.parse(action))).onNotifyChange(intent);
            return;
        }
        Uri data = intent.getData();
        switch (action.hashCode()) {
            case -2075790298:
                if (action.equals("com.android.settings.slice.action.TOGGLE_CHANGED")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -2013863560:
                if (action.equals("com.android.settings.mobilenetwork.action.ENHANCED_4G_LTE_CHANGED")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -932197342:
                if (action.equals("com.android.settings.bluetooth.action.BLUETOOTH_MODE_CHANGED")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -362341757:
                if (action.equals("com.android.settings.wifi.calling.action.WIFI_CALLING_CHANGED")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -86230637:
                if (action.equals("com.android.settings.slice.action.WIFI_CALLING_PREFERENCE_WIFI_PREFERRED")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 17552563:
                if (action.equals("com.android.settings.slice.action.SLIDER_CHANGED")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 176882490:
                if (action.equals("com.android.settings.slice.action.WIFI_CALLING_PREFERENCE_WIFI_ONLY")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 495970216:
                if (action.equals("com.android.settings.slice.action.WIFI_CALLING_PREFERENCE_CELLULAR_PREFERRED")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 902935346:
                if (action.equals("com.android.settings.slice.action.COPY")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 1913359032:
                if (action.equals("com.android.settings.notification.ZEN_MODE_CHANGED")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                handleToggleAction(context, data, stringExtra, intent.getBooleanExtra("android.app.slice.extra.TOGGLE_STATE", false));
                return;
            case 1:
                handleSliderAction(context, data, stringExtra, intent.getIntExtra("android.app.slice.extra.RANGE_VALUE", -1));
                return;
            case 2:
                BluetoothSliceBuilder.handleUriChange(context, intent);
                return;
            case 3:
                FeatureFactory.getFactory(context).getSlicesFeatureProvider().getNewWifiCallingSliceHelper(context).handleWifiCallingChanged(intent);
                return;
            case 4:
                ZenModeSliceBuilder.handleUriChange(context, intent);
                return;
            case 5:
                FeatureFactory.getFactory(context).getSlicesFeatureProvider().getNewEnhanced4gLteSliceHelper(context).handleEnhanced4gLteChanged(intent);
                return;
            case 6:
            case 7:
            case '\b':
                FeatureFactory.getFactory(context).getSlicesFeatureProvider().getNewWifiCallingSliceHelper(context).handleWifiCallingPreferenceChanged(intent);
                return;
            case '\t':
                handleCopyAction(context, data, stringExtra);
                return;
            default:
                return;
        }
    }

    private void handleToggleAction(Context context, Uri uri, String str, boolean z) {
        if (!TextUtils.isEmpty(str)) {
            BasePreferenceController preferenceController = getPreferenceController(context, str);
            if (!(preferenceController instanceof TogglePreferenceController)) {
                throw new IllegalStateException("Toggle action passed for a non-toggle key: " + str);
            } else if (!preferenceController.isAvailable()) {
                String str2 = TAG;
                Log.w(str2, "Can't update " + str + " since the setting is unavailable");
                if (!preferenceController.hasAsyncUpdate()) {
                    context.getContentResolver().notifyChange(uri, null);
                }
            } else {
                ((TogglePreferenceController) preferenceController).setChecked(z);
                logSliceValueChange(context, str, z ? 1 : 0);
                if (!preferenceController.hasAsyncUpdate()) {
                    context.getContentResolver().notifyChange(uri, null);
                }
            }
        } else {
            throw new IllegalStateException("No key passed to Intent for toggle controller");
        }
    }

    private void handleSliderAction(Context context, Uri uri, String str, int i) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("No key passed to Intent for slider controller. Use extra: com.android.settings.slice.extra.key");
        } else if (i != -1) {
            BasePreferenceController preferenceController = getPreferenceController(context, str);
            if (!(preferenceController instanceof SliderPreferenceController)) {
                throw new IllegalArgumentException("Slider action passed for a non-slider key: " + str);
            } else if (!preferenceController.isAvailable()) {
                String str2 = TAG;
                Log.w(str2, "Can't update " + str + " since the setting is unavailable");
                context.getContentResolver().notifyChange(uri, null);
            } else {
                SliderPreferenceController sliderPreferenceController = (SliderPreferenceController) preferenceController;
                int min = sliderPreferenceController.getMin();
                int max = sliderPreferenceController.getMax();
                if (i < min || i > max) {
                    throw new IllegalArgumentException("Invalid position passed to Slider controller. Expected between " + min + " and " + max + " but found " + i);
                }
                sliderPreferenceController.setSliderPosition(i);
                logSliceValueChange(context, str, i);
                context.getContentResolver().notifyChange(uri, null);
            }
        } else {
            throw new IllegalArgumentException("Invalid position passed to Slider controller");
        }
    }

    private void handleCopyAction(Context context, Uri uri, String str) {
        if (!TextUtils.isEmpty(str)) {
            BasePreferenceController preferenceController = getPreferenceController(context, str);
            if (!(preferenceController instanceof Sliceable)) {
                throw new IllegalArgumentException("Copyable action passed for a non-copyable key:" + str);
            } else if (!preferenceController.isAvailable()) {
                String str2 = TAG;
                Log.w(str2, "Can't update " + str + " since the setting is unavailable");
                if (!preferenceController.hasAsyncUpdate()) {
                    context.getContentResolver().notifyChange(uri, null);
                }
            } else {
                preferenceController.copy();
            }
        } else {
            throw new IllegalArgumentException("No key passed to Intent for controller");
        }
    }

    private void logSliceValueChange(Context context, String str, int i) {
        FeatureFactory.getFactory(context).getMetricsFeatureProvider().action(0, 1372, 0, str, i);
    }

    private BasePreferenceController getPreferenceController(Context context, String str) {
        return SliceBuilderUtils.getPreferenceController(context, new SlicesDatabaseAccessor(context).getSliceDataFromKey(str));
    }
}
