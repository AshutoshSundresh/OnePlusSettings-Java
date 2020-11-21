package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.SystemProperties;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.widget.RadioButtonPreference;
import java.util.LinkedHashMap;
import java.util.Map;

public class UsbDetailsFunctionsController extends UsbDetailsController implements RadioButtonPreference.OnClickListener {
    static final Map<Long, Integer> FUNCTIONS_MAP;
    private ConnectivityManager mConnectivityManager;
    OnStartTetheringCallback mOnStartTetheringCallback = new OnStartTetheringCallback();
    long mPreviousFunction = this.mUsbBackend.getCurrentFunctions();
    private PreferenceCategory mProfilesContainer;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "usb_details_functions";
    }

    static {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        FUNCTIONS_MAP = linkedHashMap;
        linkedHashMap.put(4L, Integer.valueOf(C0017R$string.usb_use_file_transfers));
        FUNCTIONS_MAP.put(32L, Integer.valueOf(C0017R$string.usb_use_tethering));
        FUNCTIONS_MAP.put(8L, Integer.valueOf(C0017R$string.usb_use_MIDI));
        FUNCTIONS_MAP.put(16L, Integer.valueOf(C0017R$string.usb_use_photo_transfers));
        FUNCTIONS_MAP.put(0L, Integer.valueOf(C0017R$string.usb_use_charging_only));
    }

    public UsbDetailsFunctionsController(Context context, UsbDetailsFragment usbDetailsFragment, UsbBackend usbBackend) {
        super(context, usbDetailsFragment, usbBackend);
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mProfilesContainer = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
    }

    private RadioButtonPreference getProfilePreference(String str, int i) {
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) this.mProfilesContainer.findPreference(str);
        if (radioButtonPreference != null) {
            return radioButtonPreference;
        }
        RadioButtonPreference radioButtonPreference2 = new RadioButtonPreference(this.mProfilesContainer.getContext());
        radioButtonPreference2.setKey(str);
        radioButtonPreference2.setTitle(i);
        radioButtonPreference2.setOnClickListener(this);
        this.mProfilesContainer.addPreference(radioButtonPreference2);
        return radioButtonPreference2;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.connecteddevice.usb.UsbDetailsController
    public void refresh(boolean z, long j, int i, int i2) {
        if (z) {
            this.mProfilesContainer.setEnabled(true);
        } else {
            this.mProfilesContainer.setEnabled(false);
        }
        for (Long l : FUNCTIONS_MAP.keySet()) {
            long longValue = l.longValue();
            RadioButtonPreference profilePreference = getProfilePreference(UsbBackend.usbFunctionsToString(longValue), FUNCTIONS_MAP.get(Long.valueOf(longValue)).intValue());
            if (this.mUsbBackend.areFunctionsSupported(longValue)) {
                profilePreference.setChecked(j == longValue);
            } else {
                this.mProfilesContainer.removePreference(profilePreference);
            }
            String str = SystemProperties.get("sys.debug.watchdog");
            int i3 = SystemProperties.getInt("ro.boot.qe", 0);
            if ("true".equals(str) || 1 == i3) {
                profilePreference.setEnabled(false);
            }
        }
    }

    @Override // com.android.settings.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        long usbFunctionsFromString = UsbBackend.usbFunctionsFromString(radioButtonPreference.getKey());
        long currentFunctions = this.mUsbBackend.getCurrentFunctions();
        if (usbFunctionsFromString != currentFunctions && !Utils.isMonkeyRunning()) {
            this.mPreviousFunction = currentFunctions;
            RadioButtonPreference radioButtonPreference2 = (RadioButtonPreference) this.mProfilesContainer.findPreference(UsbBackend.usbFunctionsToString(currentFunctions));
            if (radioButtonPreference2 != null) {
                radioButtonPreference2.setChecked(false);
                radioButtonPreference.setChecked(true);
            }
            if (usbFunctionsFromString == 32) {
                this.mConnectivityManager.startTethering(1, true, this.mOnStartTetheringCallback);
            } else {
                this.mUsbBackend.setCurrentFunctions(usbFunctionsFromString);
            }
        }
    }

    @Override // com.android.settings.connecteddevice.usb.UsbDetailsController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !Utils.isMonkeyRunning();
    }

    final class OnStartTetheringCallback extends ConnectivityManager.OnStartTetheringCallback {
        OnStartTetheringCallback() {
        }

        public void onTetheringFailed() {
            UsbDetailsFunctionsController.super.onTetheringFailed();
            UsbDetailsFunctionsController usbDetailsFunctionsController = UsbDetailsFunctionsController.this;
            usbDetailsFunctionsController.mUsbBackend.setCurrentFunctions(usbDetailsFunctionsController.mPreviousFunction);
        }
    }
}
