package com.android.settings.network.telephony;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.ims.ImsManager;
import android.text.TextUtils;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class ContactDiscoveryDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private CharSequence mCarrierName;
    private ImsManager mImsManager;
    private int mSubId;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    public static ContactDiscoveryDialogFragment newInstance(int i, CharSequence charSequence) {
        ContactDiscoveryDialogFragment contactDiscoveryDialogFragment = new ContactDiscoveryDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("sub_id_key", i);
        bundle.putCharSequence("carrier_name_key", charSequence);
        contactDiscoveryDialogFragment.setArguments(bundle);
        return contactDiscoveryDialogFragment;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle arguments = getArguments();
        this.mSubId = arguments.getInt("sub_id_key");
        this.mCarrierName = arguments.getCharSequence("carrier_name_key");
        this.mImsManager = getImsManager(context);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String str;
        String str2;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (!TextUtils.isEmpty(this.mCarrierName)) {
            str2 = getContext().getString(C0017R$string.contact_discovery_opt_in_dialog_title, this.mCarrierName);
            str = getContext().getString(C0017R$string.contact_discovery_opt_in_dialog_message, this.mCarrierName);
        } else {
            str2 = getContext().getString(C0017R$string.contact_discovery_opt_in_dialog_title_no_carrier_defined);
            str = getContext().getString(C0017R$string.contact_discovery_opt_in_dialog_message_no_carrier_defined);
        }
        builder.setMessage(str);
        builder.setTitle(str2);
        builder.setIconAttribute(16843605);
        builder.setPositiveButton(C0017R$string.confirmation_turn_on, this);
        builder.setNegativeButton(17039360, this);
        return builder.create();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            MobileNetworkUtils.setContactDiscoveryEnabled(this.mImsManager, this.mSubId, true);
        }
    }

    public ImsManager getImsManager(Context context) {
        return (ImsManager) context.getSystemService(ImsManager.class);
    }

    public static String getFragmentTag(int i) {
        return "discovery_dialog:" + i;
    }
}
