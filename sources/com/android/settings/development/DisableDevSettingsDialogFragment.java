package com.android.settings.development;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class DisableDevSettingsDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1591;
    }

    static DisableDevSettingsDialogFragment newInstance() {
        return new DisableDevSettingsDialogFragment();
    }

    public static void show(DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment) {
        DisableDevSettingsDialogFragment disableDevSettingsDialogFragment = new DisableDevSettingsDialogFragment();
        disableDevSettingsDialogFragment.setTargetFragment(developmentSettingsDashboardFragment, 0);
        disableDevSettingsDialogFragment.show(developmentSettingsDashboardFragment.getActivity().getSupportFragmentManager(), "DisableDevSettingDlg");
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(C0017R$string.bluetooth_disable_a2dp_hw_offload_dialog_message);
        builder.setTitle(C0017R$string.bluetooth_disable_a2dp_hw_offload_dialog_title);
        builder.setPositiveButton(C0017R$string.bluetooth_disable_a2dp_hw_offload_dialog_confirm, this);
        builder.setNegativeButton(C0017R$string.bluetooth_disable_a2dp_hw_offload_dialog_cancel, this);
        return builder.create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Fragment targetFragment = getTargetFragment();
        if (!(targetFragment instanceof DevelopmentSettingsDashboardFragment)) {
            Log.e("DisableDevSettingDlg", "getTargetFragment return unexpected type");
        }
        DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment = (DevelopmentSettingsDashboardFragment) targetFragment;
        if (i == -1) {
            developmentSettingsDashboardFragment.onDisableDevelopmentOptionsConfirmed();
            ((PowerManager) getContext().getSystemService(PowerManager.class)).reboot(null);
            return;
        }
        developmentSettingsDashboardFragment.onDisableDevelopmentOptionsRejected();
    }
}
