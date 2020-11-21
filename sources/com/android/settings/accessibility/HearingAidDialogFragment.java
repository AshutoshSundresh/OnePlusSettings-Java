package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.bluetooth.BluetoothPairingDetail;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class HearingAidDialogFragment extends InstrumentedDialogFragment {
    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1512;
    }

    public static HearingAidDialogFragment newInstance() {
        return new HearingAidDialogFragment();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0017R$string.accessibility_hearingaid_pair_instructions_message);
        builder.setPositiveButton(C0017R$string.accessibility_hearingaid_instruction_continue_button, new DialogInterface.OnClickListener() {
            /* class com.android.settings.accessibility.HearingAidDialogFragment.AnonymousClass2 */

            public void onClick(DialogInterface dialogInterface, int i) {
                HearingAidDialogFragment.this.launchBluetoothAddDeviceSetting();
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener(this) {
            /* class com.android.settings.accessibility.HearingAidDialogFragment.AnonymousClass1 */

            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return builder.create();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void launchBluetoothAddDeviceSetting() {
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getActivity());
        subSettingLauncher.setDestination(BluetoothPairingDetail.class.getName());
        subSettingLauncher.setSourceMetricsCategory(2);
        subSettingLauncher.launch();
    }
}
