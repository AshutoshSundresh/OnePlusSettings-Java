package com.android.settings.nfc;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.nfc.NfcPaymentPreference;
import com.android.settings.nfc.NfcPaymentPreferenceController;
import com.android.settings.nfc.PaymentBackend;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import java.util.List;

public class NfcPaymentPreferenceController extends BasePreferenceController implements PaymentBackend.Callback, View.OnClickListener, NfcPaymentPreference.Listener, LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "NfcPaymentController";
    private final NfcPaymentAdapter mAdapter;
    private PaymentBackend mPaymentBackend;
    private NfcPaymentPreference mPreference;
    private ImageView mSettingsButtonView;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public NfcPaymentPreferenceController(Context context, String str) {
        super(context, str);
        this.mAdapter = new NfcPaymentAdapter(context);
    }

    public void setPaymentBackend(PaymentBackend paymentBackend) {
        this.mPaymentBackend = paymentBackend;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        PaymentBackend paymentBackend = this.mPaymentBackend;
        if (paymentBackend != null) {
            paymentBackend.registerCallback(this);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        PaymentBackend paymentBackend = this.mPaymentBackend;
        if (paymentBackend != null) {
            paymentBackend.unregisterCallback(this);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.nfc") || NfcAdapter.getDefaultAdapter(this.mContext) == null) {
            return 3;
        }
        if (this.mPaymentBackend == null) {
            this.mPaymentBackend = new PaymentBackend(this.mContext);
        }
        List<PaymentBackend.PaymentAppInfo> paymentAppInfos = this.mPaymentBackend.getPaymentAppInfos();
        if (paymentAppInfos == null || paymentAppInfos.isEmpty()) {
            return 3;
        }
        return 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        NfcPaymentPreference nfcPaymentPreference = (NfcPaymentPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = nfcPaymentPreference;
        if (nfcPaymentPreference != null) {
            nfcPaymentPreference.initialize(this);
        }
    }

    @Override // com.android.settings.nfc.NfcPaymentPreference.Listener
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        ImageView imageView = (ImageView) preferenceViewHolder.findViewById(C0010R$id.settings_button);
        this.mSettingsButtonView = imageView;
        imageView.setOnClickListener(this);
        updateSettingsVisibility();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        List<PaymentBackend.PaymentAppInfo> paymentAppInfos = this.mPaymentBackend.getPaymentAppInfos();
        if (paymentAppInfos != null) {
            this.mAdapter.updateApps((PaymentBackend.PaymentAppInfo[]) paymentAppInfos.toArray(new PaymentBackend.PaymentAppInfo[paymentAppInfos.size()]));
        }
        super.updateState(preference);
        updateSettingsVisibility();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        PaymentBackend.PaymentAppInfo defaultApp = this.mPaymentBackend.getDefaultApp();
        if (defaultApp != null) {
            return defaultApp.label;
        }
        return this.mContext.getText(C0017R$string.nfc_payment_default_not_set);
    }

    @Override // com.android.settings.nfc.NfcPaymentPreference.Listener
    public void onPrepareDialogBuilder(AlertDialog.Builder builder, DialogInterface.OnClickListener onClickListener) {
        builder.setSingleChoiceItems(this.mAdapter, 0, onClickListener);
    }

    @Override // com.android.settings.nfc.PaymentBackend.Callback
    public void onPaymentAppsChanged() {
        updateState(this.mPreference);
    }

    public void onClick(View view) {
        PaymentBackend.PaymentAppInfo defaultApp = this.mPaymentBackend.getDefaultApp();
        if (defaultApp != null && defaultApp.settingsComponent != null) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setComponent(defaultApp.settingsComponent);
            intent.addFlags(268435456);
            try {
                this.mContext.startActivity(intent);
            } catch (ActivityNotFoundException unused) {
                Log.e(TAG, "Settings activity not found.");
            }
        }
    }

    private void updateSettingsVisibility() {
        if (this.mSettingsButtonView != null) {
            PaymentBackend.PaymentAppInfo defaultApp = this.mPaymentBackend.getDefaultApp();
            if (defaultApp == null || defaultApp.settingsComponent == null) {
                this.mSettingsButtonView.setVisibility(8);
            } else {
                this.mSettingsButtonView.setVisibility(0);
            }
        }
    }

    /* access modifiers changed from: private */
    public class NfcPaymentAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
        private PaymentBackend.PaymentAppInfo[] appInfos;
        private final LayoutInflater mLayoutInflater;

        public NfcPaymentAdapter(Context context) {
            this.mLayoutInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        public void updateApps(PaymentBackend.PaymentAppInfo[] paymentAppInfoArr) {
            this.appInfos = paymentAppInfoArr;
            notifyDataSetChanged();
        }

        public int getCount() {
            PaymentBackend.PaymentAppInfo[] paymentAppInfoArr = this.appInfos;
            if (paymentAppInfoArr != null) {
                return paymentAppInfoArr.length;
            }
            return 0;
        }

        public PaymentBackend.PaymentAppInfo getItem(int i) {
            return this.appInfos[i];
        }

        public long getItemId(int i) {
            return (long) this.appInfos[i].componentName.hashCode();
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            PaymentBackend.PaymentAppInfo paymentAppInfo = this.appInfos[i];
            if (view == null) {
                view = this.mLayoutInflater.inflate(C0012R$layout.nfc_payment_option, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.root = view.findViewById(C0010R$id.nfc_payment_pref);
                viewHolder.title = (CheckedTextView) view.findViewById(C0010R$id.payment_title);
                viewHolder.imageView = (ImageView) view.findViewById(C0010R$id.banner);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.imageView.setImageDrawable(paymentAppInfo.icon);
            viewHolder.imageView.setTag(paymentAppInfo);
            viewHolder.imageView.setContentDescription(paymentAppInfo.label);
            viewHolder.root.setOnClickListener(this);
            if (TextUtils.isEmpty(paymentAppInfo.description)) {
                viewHolder.title.setText(paymentAppInfo.label);
            } else {
                viewHolder.title.setText(paymentAppInfo.description);
            }
            viewHolder.title.setChecked(paymentAppInfo.isDefault);
            viewHolder.root.setOnClickListener(new View.OnClickListener(paymentAppInfo) {
                /* class com.android.settings.nfc.$$Lambda$NfcPaymentPreferenceController$NfcPaymentAdapter$1O_ueURy_L8vAJNwiw6CODVX88A */
                public final /* synthetic */ PaymentBackend.PaymentAppInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    NfcPaymentPreferenceController.NfcPaymentAdapter.this.lambda$getView$0$NfcPaymentPreferenceController$NfcPaymentAdapter(this.f$1, view);
                }
            });
            return view;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$getView$0 */
        public /* synthetic */ void lambda$getView$0$NfcPaymentPreferenceController$NfcPaymentAdapter(PaymentBackend.PaymentAppInfo paymentAppInfo, View view) {
            makeDefault(paymentAppInfo);
        }

        private class ViewHolder {
            public ImageView imageView;
            public View root;
            public CheckedTextView title;

            private ViewHolder(NfcPaymentAdapter nfcPaymentAdapter) {
            }
        }

        public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
            makeDefault((PaymentBackend.PaymentAppInfo) compoundButton.getTag());
        }

        public void onClick(View view) {
            makeDefault((PaymentBackend.PaymentAppInfo) view.getTag());
        }

        private void makeDefault(PaymentBackend.PaymentAppInfo paymentAppInfo) {
            if (!paymentAppInfo.isDefault) {
                NfcPaymentPreferenceController.this.mPaymentBackend.setDefaultPaymentApp(paymentAppInfo.componentName);
            }
            Dialog dialog = NfcPaymentPreferenceController.this.mPreference.getDialog();
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }
}
