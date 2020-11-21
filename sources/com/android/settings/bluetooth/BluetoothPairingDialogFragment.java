package com.android.settings.bluetooth;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

public class BluetoothPairingDialogFragment extends InstrumentedDialogFragment implements TextWatcher, DialogInterface.OnClickListener {
    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;
    private BluetoothPairingController mPairingController;
    private BluetoothPairingDialog mPairingDialogActivity;
    private EditText mPairingView;

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 613;
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        if (!isPairingControllerSet()) {
            throw new IllegalStateException("Must call setPairingController() before showing dialog");
        } else if (isPairingDialogActivitySet()) {
            this.mBuilder = new AlertDialog.Builder(getActivity());
            AlertDialog alertDialog = setupDialog();
            this.mDialog = alertDialog;
            alertDialog.setCanceledOnTouchOutside(false);
            return this.mDialog;
        } else {
            throw new IllegalStateException("Must call setPairingDialogActivity() before showing dialog");
        }
    }

    public void afterTextChanged(Editable editable) {
        Button button = this.mDialog.getButton(-1);
        if (button != null) {
            button.setEnabled(this.mPairingController.isPasskeyValid(editable));
        }
        this.mPairingController.updateUserInput(editable.toString());
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            this.mPairingController.onDialogPositiveClick(this);
        } else if (i == -2) {
            this.mPairingController.onDialogNegativeClick(this);
        }
        this.mPairingDialogActivity.dismiss();
    }

    /* access modifiers changed from: package-private */
    public void setPairingController(BluetoothPairingController bluetoothPairingController) {
        if (!isPairingControllerSet()) {
            this.mPairingController = bluetoothPairingController;
            return;
        }
        throw new IllegalStateException("The controller can only be set once. Forcibly replacing it will lead to undefined behavior");
    }

    /* access modifiers changed from: package-private */
    public boolean isPairingControllerSet() {
        return this.mPairingController != null;
    }

    /* access modifiers changed from: package-private */
    public void setPairingDialogActivity(BluetoothPairingDialog bluetoothPairingDialog) {
        if (!isPairingDialogActivitySet()) {
            this.mPairingDialogActivity = bluetoothPairingDialog;
            return;
        }
        throw new IllegalStateException("The pairing dialog activity can only be set once");
    }

    /* access modifiers changed from: package-private */
    public boolean isPairingDialogActivitySet() {
        return this.mPairingDialogActivity != null;
    }

    private AlertDialog setupDialog() {
        int dialogType = this.mPairingController.getDialogType();
        if (dialogType == 0) {
            return createUserEntryDialog();
        }
        if (dialogType == 1) {
            return createConsentDialog();
        }
        if (dialogType == 2) {
            return createDisplayPasskeyOrPinDialog();
        }
        Log.e("BTPairingDialogFragment", "Incorrect pairing type received, not showing any dialog");
        return null;
    }

    /* access modifiers changed from: package-private */
    public CharSequence getPairingViewText() {
        EditText editText = this.mPairingView;
        if (editText != null) {
            return editText.getText();
        }
        return null;
    }

    private AlertDialog createUserEntryDialog() {
        this.mBuilder.setTitle(getString(C0017R$string.bluetooth_pairing_request, this.mPairingController.getDeviceName()));
        this.mBuilder.setView(createPinEntryView());
        this.mBuilder.setPositiveButton(getString(17039370), this);
        this.mBuilder.setNegativeButton(getString(17039360), this);
        AlertDialog create = this.mBuilder.create();
        create.setOnShowListener(new DialogInterface.OnShowListener() {
            /* class com.android.settings.bluetooth.$$Lambda$BluetoothPairingDialogFragment$ItV61WjNe_T4YaZN6BYGTBHLdZc */

            public final void onShow(DialogInterface dialogInterface) {
                BluetoothPairingDialogFragment.this.lambda$createUserEntryDialog$0$BluetoothPairingDialogFragment(dialogInterface);
            }
        });
        return create;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createUserEntryDialog$0 */
    public /* synthetic */ void lambda$createUserEntryDialog$0$BluetoothPairingDialogFragment(DialogInterface dialogInterface) {
        InputMethodManager inputMethodManager;
        if (TextUtils.isEmpty(getPairingViewText())) {
            this.mDialog.getButton(-1).setEnabled(false);
        }
        EditText editText = this.mPairingView;
        if (editText != null && editText.requestFocus() && (inputMethodManager = (InputMethodManager) getContext().getSystemService("input_method")) != null) {
            inputMethodManager.showSoftInput(this.mPairingView, 1);
        }
    }

    private View createPinEntryView() {
        View inflate = getActivity().getLayoutInflater().inflate(C0012R$layout.bluetooth_pin_entry, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(C0010R$id.pin_values_hint);
        TextView textView2 = (TextView) inflate.findViewById(C0010R$id.message_below_pin);
        CheckBox checkBox = (CheckBox) inflate.findViewById(C0010R$id.alphanumeric_pin);
        CheckBox checkBox2 = (CheckBox) inflate.findViewById(C0010R$id.phonebook_sharing_message_entry_pin);
        checkBox2.setText(getString(C0017R$string.bluetooth_pairing_shares_phonebook, this.mPairingController.getDeviceName()));
        EditText editText = (EditText) inflate.findViewById(C0010R$id.text);
        checkBox2.setVisibility(this.mPairingController.isProfileReady() ? 8 : 0);
        this.mPairingController.setContactSharingState();
        checkBox2.setOnCheckedChangeListener(this.mPairingController);
        checkBox2.setChecked(this.mPairingController.getContactSharingState());
        this.mPairingView = editText;
        editText.setInputType(2);
        editText.addTextChangedListener(this);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /* class com.android.settings.bluetooth.$$Lambda$BluetoothPairingDialogFragment$r7iz4I0mbAZSn1yrbFsqcyiwC0 */

            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                BluetoothPairingDialogFragment.this.lambda$createPinEntryView$1$BluetoothPairingDialogFragment(compoundButton, z);
            }
        });
        int deviceVariantMessageId = this.mPairingController.getDeviceVariantMessageId();
        int deviceVariantMessageHintId = this.mPairingController.getDeviceVariantMessageHintId();
        int deviceMaxPasskeyLength = this.mPairingController.getDeviceMaxPasskeyLength();
        checkBox.setVisibility(this.mPairingController.pairingCodeIsAlphanumeric() ? 0 : 8);
        if (deviceVariantMessageId != -1) {
            textView2.setText(deviceVariantMessageId);
        } else {
            textView2.setVisibility(8);
        }
        if (deviceVariantMessageHintId != -1) {
            textView.setText(deviceVariantMessageHintId);
        } else {
            textView.setVisibility(8);
        }
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(deviceMaxPasskeyLength)});
        return inflate;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createPinEntryView$1 */
    public /* synthetic */ void lambda$createPinEntryView$1$BluetoothPairingDialogFragment(CompoundButton compoundButton, boolean z) {
        if (z) {
            this.mPairingView.setInputType(1);
        } else {
            this.mPairingView.setInputType(2);
        }
    }

    private AlertDialog createConfirmationDialog() {
        this.mBuilder.setTitle(getString(C0017R$string.bluetooth_pairing_request, this.mPairingController.getDeviceName()));
        this.mBuilder.setView(createView());
        this.mBuilder.setPositiveButton(getString(C0017R$string.bluetooth_pairing_accept), this);
        this.mBuilder.setNegativeButton(getString(C0017R$string.bluetooth_pairing_decline), this);
        return this.mBuilder.create();
    }

    private AlertDialog createConsentDialog() {
        return createConfirmationDialog();
    }

    private AlertDialog createDisplayPasskeyOrPinDialog() {
        this.mBuilder.setTitle(getString(C0017R$string.bluetooth_pairing_request, this.mPairingController.getDeviceName()));
        this.mBuilder.setView(createView());
        this.mBuilder.setNegativeButton(getString(17039360), this);
        AlertDialog create = this.mBuilder.create();
        this.mPairingController.notifyDialogDisplayed();
        return create;
    }

    private View createView() {
        View inflate = getActivity().getLayoutInflater().inflate(C0012R$layout.bluetooth_pin_confirm, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(C0010R$id.pairing_caption);
        TextView textView2 = (TextView) inflate.findViewById(C0010R$id.pairing_subhead);
        TextView textView3 = (TextView) inflate.findViewById(C0010R$id.pairing_code_message);
        CheckBox checkBox = (CheckBox) inflate.findViewById(C0010R$id.phonebook_sharing_message_confirm_pin);
        checkBox.setText(getString(C0017R$string.bluetooth_pairing_shares_phonebook, this.mPairingController.getDeviceName()));
        int i = 8;
        checkBox.setVisibility(this.mPairingController.isProfileReady() ? 8 : 0);
        this.mPairingController.setContactSharingState();
        checkBox.setChecked(this.mPairingController.getContactSharingState());
        checkBox.setOnCheckedChangeListener(this.mPairingController);
        if (this.mPairingController.isDisplayPairingKeyVariant()) {
            i = 0;
        }
        textView3.setVisibility(i);
        if (this.mPairingController.hasPairingContent()) {
            textView.setVisibility(0);
            textView2.setVisibility(0);
            textView2.setText(this.mPairingController.getPairingContent());
        }
        return inflate;
    }
}
