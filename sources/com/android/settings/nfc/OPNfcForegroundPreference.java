package com.android.settings.nfc;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.nfc.OPNfcForegroundPreference;
import com.android.settings.nfc.PaymentBackend;
import com.android.settingslib.CustomDialogPreferenceCompat;

public class OPNfcForegroundPreference extends CustomDialogPreferenceCompat implements PaymentBackend.Callback {
    private NfcForegroundAdapter mAdapter;
    private LayoutInflater mLayoutInflater;
    private String[] mList = new String[2];
    private PaymentBackend mPaymentBackend;

    public OPNfcForegroundPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setTitle(getContext().getString(C0017R$string.nfc_payment_use_default));
        setDialogTitle(context.getString(C0017R$string.nfc_payment_use_default));
        this.mLayoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mList[0] = getContext().getString(C0017R$string.nfc_payment_favor_open);
        this.mList[1] = getContext().getString(C0017R$string.nfc_payment_favor_default);
        this.mAdapter = new NfcForegroundAdapter();
    }

    public void setPaymentBackend(PaymentBackend paymentBackend) {
        this.mPaymentBackend = paymentBackend;
        paymentBackend.registerCallback(this);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomDialogPreferenceCompat
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        super.onPrepareDialogBuilder(builder, onClickListener);
        builder.setSingleChoiceItems(this.mAdapter, 0, onClickListener);
    }

    @Override // com.android.settings.nfc.PaymentBackend.Callback
    public void onPaymentAppsChanged() {
        refresh();
    }

    /* access modifiers changed from: package-private */
    public void refresh() {
        boolean isForegroundMode = this.mPaymentBackend.isForegroundMode();
        Log.d("OPNfcForegroundPreference", "refresh foregroundMode:" + isForegroundMode);
        if (isForegroundMode) {
            persistString("1");
        } else {
            persistString("0");
        }
        setSummary(getUISummary(isForegroundMode));
    }

    public String getEntry() {
        if (this.mPaymentBackend.isForegroundMode()) {
            return this.mList[0];
        }
        return this.mList[1];
    }

    private String getUISummary(boolean z) {
        if (z) {
            return this.mList[0];
        }
        return this.mList[1];
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private String transferSummaryToValue(String str) {
        if (!this.mList[0].equals(str) && this.mList[1].equals(str)) {
            return "0";
        }
        return "1";
    }

    class NfcForegroundAdapter extends BaseAdapter {
        public long getItemId(int i) {
            return (long) i;
        }

        NfcForegroundAdapter() {
        }

        public int getCount() {
            return OPNfcForegroundPreference.this.mList.length;
        }

        public String getItem(int i) {
            return OPNfcForegroundPreference.this.mList[i];
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            String str = OPNfcForegroundPreference.this.mList[i];
            if (view == null) {
                view = OPNfcForegroundPreference.this.mLayoutInflater.inflate(C0012R$layout.op_item_nfc_foreground_option, viewGroup, false);
                viewHolder = new ViewHolder(this);
                viewHolder.root = view.findViewById(C0010R$id.nfc_foreground_pref);
                viewHolder.title = (CheckedTextView) view.findViewById(C0010R$id.foreground_title);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.title.setText(str);
            if (!OPNfcForegroundPreference.this.mPaymentBackend.isForegroundMode() ? i == 1 : i == 0) {
                viewHolder.title.setChecked(true);
            } else {
                viewHolder.title.setChecked(false);
            }
            viewHolder.root.setOnClickListener(new View.OnClickListener(str) {
                /* class com.android.settings.nfc.$$Lambda$OPNfcForegroundPreference$NfcForegroundAdapter$qNvWbnsR7zJv3EuWuzylUkURco4 */
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    OPNfcForegroundPreference.NfcForegroundAdapter.this.lambda$getView$0$OPNfcForegroundPreference$NfcForegroundAdapter(this.f$1, view);
                }
            });
            return view;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$getView$0 */
        public /* synthetic */ void lambda$getView$0$OPNfcForegroundPreference$NfcForegroundAdapter(String str, View view) {
            setForegroundMode(str);
        }

        public class ViewHolder {
            public View root;
            public CheckedTextView title;

            public ViewHolder(NfcForegroundAdapter nfcForegroundAdapter) {
            }
        }

        /* access modifiers changed from: package-private */
        public void setForegroundMode(String str) {
            String transferSummaryToValue = OPNfcForegroundPreference.this.transferSummaryToValue(str);
            OPNfcForegroundPreference.this.setSummary(str);
            boolean z = Integer.parseInt(transferSummaryToValue) != 0;
            OPNfcForegroundPreference.this.mPaymentBackend.setForegroundMode(z);
            if (z) {
                OPNfcForegroundPreference.this.persistString("1");
            } else {
                OPNfcForegroundPreference.this.persistString("0");
            }
            Log.i("OPNfcForegroundPreference", "setForegroundMode newValue:" + transferSummaryToValue + " foregroundMode:" + z);
            Dialog dialog = OPNfcForegroundPreference.this.getDialog();
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }
}
