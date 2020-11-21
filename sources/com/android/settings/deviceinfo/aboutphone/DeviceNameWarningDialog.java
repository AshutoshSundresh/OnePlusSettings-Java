package com.android.settings.deviceinfo.aboutphone;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class DeviceNameWarningDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1219;
    }

    public static void show(MyDeviceInfoFragment myDeviceInfoFragment) {
        FragmentManager supportFragmentManager = myDeviceInfoFragment.getActivity().getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag("DeviceNameWarningDlg") == null) {
            DeviceNameWarningDialog deviceNameWarningDialog = new DeviceNameWarningDialog();
            deviceNameWarningDialog.setTargetFragment(myDeviceInfoFragment, 0);
            deviceNameWarningDialog.show(supportFragmentManager, "DeviceNameWarningDlg");
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0017R$string.my_device_info_device_name_preference_title);
        builder.setMessage(C0017R$string.about_phone_device_name_warning);
        builder.setCancelable(false);
        builder.setPositiveButton(17039370, this);
        builder.setNegativeButton(17039360, this);
        return builder.create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        MyDeviceInfoFragment myDeviceInfoFragment = (MyDeviceInfoFragment) getTargetFragment();
        if (i == -1) {
            myDeviceInfoFragment.onSetDeviceNameConfirm(true);
        } else {
            myDeviceInfoFragment.onSetDeviceNameConfirm(false);
        }
    }
}
