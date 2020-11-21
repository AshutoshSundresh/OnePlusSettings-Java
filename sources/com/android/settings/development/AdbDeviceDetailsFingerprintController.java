package com.android.settings.development;

import android.content.Context;
import android.debug.PairDevice;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.FooterPreference;

public class AdbDeviceDetailsFingerprintController extends AbstractPreferenceController {
    static final String KEY_FINGERPRINT_CATEGORY = "fingerprint_category";
    private PreferenceCategory mFingerprintCategory;
    private FooterPreference mFingerprintPref;
    private PairDevice mPairedDevice;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_FINGERPRINT_CATEGORY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AdbDeviceDetailsFingerprintController(PairDevice pairDevice, Context context, Fragment fragment) {
        super(context);
        this.mPairedDevice = pairDevice;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
        this.mFingerprintCategory = preferenceCategory;
        this.mFingerprintPref = new FooterPreference(preferenceCategory.getContext());
        CharSequence text = this.mContext.getText(C0017R$string.adb_device_fingerprint_title_format);
        this.mFingerprintPref.setTitle(String.format(text.toString(), this.mPairedDevice.getGuid()));
        this.mFingerprintCategory.addPreference(this.mFingerprintPref);
    }
}
