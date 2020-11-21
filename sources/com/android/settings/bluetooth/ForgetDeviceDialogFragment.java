package com.android.settings.bluetooth;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.LocalBluetoothManager;

public class ForgetDeviceDialogFragment extends InstrumentedDialogFragment {
    private CachedBluetoothDevice mDevice;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1031;
    }

    public static ForgetDeviceDialogFragment newInstance(String str) {
        Bundle bundle = new Bundle(1);
        bundle.putString("device_address", str);
        ForgetDeviceDialogFragment forgetDeviceDialogFragment = new ForgetDeviceDialogFragment();
        forgetDeviceDialogFragment.setArguments(bundle);
        return forgetDeviceDialogFragment;
    }

    /* access modifiers changed from: package-private */
    public CachedBluetoothDevice getDevice(Context context) {
        String string = getArguments().getString("device_address");
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(context);
        return localBtManager.getCachedDeviceManager().findDevice(localBtManager.getBluetoothAdapter().getRemoteDevice(string));
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        int i;
        $$Lambda$ForgetDeviceDialogFragment$EDf2UTKPcHIZGnJUVoyf7QwuxfU r5 = new DialogInterface.OnClickListener() {
            /* class com.android.settings.bluetooth.$$Lambda$ForgetDeviceDialogFragment$EDf2UTKPcHIZGnJUVoyf7QwuxfU */

            public final void onClick(DialogInterface dialogInterface, int i) {
                ForgetDeviceDialogFragment.this.lambda$onCreateDialog$0$ForgetDeviceDialogFragment(dialogInterface, i);
            }
        };
        Context context = getContext();
        CachedBluetoothDevice device = getDevice(context);
        this.mDevice = device;
        boolean booleanMetaData = BluetoothUtils.getBooleanMetaData(device.getDevice(), 6);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(C0017R$string.bluetooth_unpair_dialog_forget_confirm_button, r5);
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        create.setTitle(C0017R$string.bluetooth_unpair_dialog_title);
        if (booleanMetaData) {
            i = C0017R$string.bluetooth_untethered_unpair_dialog_body;
        } else {
            i = C0017R$string.bluetooth_unpair_dialog_body;
        }
        create.setMessage(context.getString(i, this.mDevice.getName()));
        return create;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$0 */
    public /* synthetic */ void lambda$onCreateDialog$0$ForgetDeviceDialogFragment(DialogInterface dialogInterface, int i) {
        this.mDevice.unpair();
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.finish();
        }
    }
}
