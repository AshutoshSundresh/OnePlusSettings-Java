package com.android.settings.development;

import android.content.Context;
import android.content.Intent;
import android.debug.PairDevice;
import android.os.Parcelable;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.ActionButtonsPreference;

public class AdbDeviceDetailsActionController extends AbstractPreferenceController {
    static final String KEY_BUTTONS_PREF = "buttons";
    private final Fragment mFragment;
    private PairDevice mPairedDevice;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_BUTTONS_PREF;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public AdbDeviceDetailsActionController(PairDevice pairDevice, Context context, Fragment fragment) {
        super(context);
        this.mPairedDevice = pairDevice;
        this.mFragment = fragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ActionButtonsPreference actionButtonsPreference = (ActionButtonsPreference) preferenceScreen.findPreference(getPreferenceKey());
        actionButtonsPreference.setButton1Visible(false);
        actionButtonsPreference.setButton2Icon(C0008R$drawable.ic_settings_delete);
        actionButtonsPreference.setButton2Text(C0017R$string.adb_device_forget);
        actionButtonsPreference.setButton2OnClickListener(new View.OnClickListener() {
            /* class com.android.settings.development.$$Lambda$AdbDeviceDetailsActionController$gVt_Slc1PEZiDx3tO8rh_4w_huc */

            public final void onClick(View view) {
                AdbDeviceDetailsActionController.this.lambda$displayPreference$0$AdbDeviceDetailsActionController(view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$displayPreference$0 */
    public /* synthetic */ void lambda$displayPreference$0$AdbDeviceDetailsActionController(View view) {
        forgetDevice();
    }

    private void forgetDevice() {
        Intent intent = new Intent();
        intent.putExtra("request_type", 0);
        intent.putExtra("paired_device", (Parcelable) this.mPairedDevice);
        this.mFragment.getActivity().setResult(-1, intent);
        this.mFragment.getActivity().finish();
    }
}
