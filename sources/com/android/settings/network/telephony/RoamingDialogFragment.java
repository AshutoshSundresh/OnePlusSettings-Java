package com.android.settings.network.telephony;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyManager;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class RoamingDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private CarrierConfigManager mCarrierConfigManager;
    private int mSubId;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1583;
    }

    public static RoamingDialogFragment newInstance(int i) {
        RoamingDialogFragment roamingDialogFragment = new RoamingDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("sub_id_key", i);
        roamingDialogFragment.setArguments(bundle);
        return roamingDialogFragment;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mSubId = getArguments().getInt("sub_id_key");
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        int i = C0017R$string.roaming_alert_title;
        int i2 = C0017R$string.roaming_warning;
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(this.mSubId);
        if (configForSubId != null && configForSubId.getBoolean("check_pricing_with_carrier_data_roaming_bool")) {
            i2 = C0017R$string.roaming_check_price_warning;
        }
        builder.setMessage(getResources().getString(i2));
        builder.setTitle(i);
        builder.setIconAttribute(16843605);
        builder.setPositiveButton(17039379, this);
        builder.setNegativeButton(17039369, this);
        return builder.create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        TelephonyManager createForSubscriptionId;
        if (i == -1 && (createForSubscriptionId = ((TelephonyManager) getContext().getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId)) != null) {
            createForSubscriptionId.setDataRoamingEnabled(true);
        }
    }
}
