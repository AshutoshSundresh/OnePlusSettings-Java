package com.android.settings.network.telephony;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class MobileDataDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private int mSubId;
    private SubscriptionManager mSubscriptionManager;
    private int mType;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1582;
    }

    public static MobileDataDialogFragment newInstance(int i, int i2) {
        MobileDataDialogFragment mobileDataDialogFragment = new MobileDataDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("dialog_type", i);
        bundle.putInt("subId", i2);
        mobileDataDialogFragment.setArguments(bundle);
        return mobileDataDialogFragment;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSubscriptionManager = (SubscriptionManager) getContext().getSystemService(SubscriptionManager.class);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String str;
        String str2;
        Bundle arguments = getArguments();
        Context context = getContext();
        this.mType = arguments.getInt("dialog_type");
        int i = arguments.getInt("subId");
        this.mSubId = i;
        int i2 = this.mType;
        if (i2 == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(C0017R$string.data_usage_disable_mobile);
            builder.setPositiveButton(17039370, this);
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            return builder.create();
        } else if (i2 == 1) {
            SubscriptionInfo activeSubscriptionInfo = this.mSubscriptionManager.getActiveSubscriptionInfo(i);
            SubscriptionInfo activeSubscriptionInfo2 = this.mSubscriptionManager.getActiveSubscriptionInfo(SubscriptionManager.getDefaultDataSubscriptionId());
            if (activeSubscriptionInfo2 == null) {
                str = getContext().getResources().getString(C0017R$string.sim_selection_required_pref);
            } else {
                str = activeSubscriptionInfo2.getDisplayName().toString();
            }
            if (activeSubscriptionInfo == null) {
                str2 = getContext().getResources().getString(C0017R$string.sim_selection_required_pref);
            } else {
                str2 = activeSubscriptionInfo.getDisplayName().toString();
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
            builder2.setTitle(context.getString(C0017R$string.sim_change_data_title, str2));
            builder2.setMessage(context.getString(C0017R$string.sim_change_data_message, str2, str));
            builder2.setPositiveButton(context.getString(C0017R$string.sim_change_data_ok, str2), this);
            builder2.setNegativeButton(C0017R$string.cancel, (DialogInterface.OnClickListener) null);
            return builder2.create();
        } else {
            throw new IllegalArgumentException("unknown type " + this.mType);
        }
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        int i2 = this.mType;
        if (i2 == 0) {
            MobileNetworkUtils.setMobileDataEnabled(getContext(), this.mSubId, false, false);
        } else if (i2 == 1) {
            this.mSubscriptionManager.setDefaultDataSubId(this.mSubId);
            MobileNetworkUtils.setMobileDataEnabled(getContext(), this.mSubId, true, true);
        } else {
            throw new IllegalArgumentException("unknown type " + this.mType);
        }
    }
}
