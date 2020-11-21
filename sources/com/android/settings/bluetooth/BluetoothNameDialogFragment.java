package com.android.settings.bluetooth;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.Utils;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;

/* access modifiers changed from: package-private */
public abstract class BluetoothNameDialogFragment extends InstrumentedDialogFragment implements TextWatcher, TextView.OnEditorActionListener {
    AlertDialog mAlertDialog;
    private boolean mDeviceNameEdited;
    private boolean mDeviceNameUpdated;
    EditText mDeviceNameView;
    private Button mOkButton;

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    /* access modifiers changed from: protected */
    public abstract String getDeviceName();

    /* access modifiers changed from: protected */
    public abstract int getDialogTitle();

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    /* access modifiers changed from: protected */
    public abstract void setDeviceName(String str);

    BluetoothNameDialogFragment() {
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        String deviceName = getDeviceName();
        if (bundle != null) {
            deviceName = bundle.getString("device_name", deviceName);
            this.mDeviceNameEdited = bundle.getBoolean("device_name_edited", false);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getDialogTitle());
        builder.setView(createDialogView(deviceName));
        builder.setPositiveButton(C0017R$string.bluetooth_rename_button, new DialogInterface.OnClickListener() {
            /* class com.android.settings.bluetooth.$$Lambda$BluetoothNameDialogFragment$pGuotXbZkr5ej_7pdbB840goZcw */

            public final void onClick(DialogInterface dialogInterface, int i) {
                BluetoothNameDialogFragment.this.lambda$onCreateDialog$0$BluetoothNameDialogFragment(dialogInterface, i);
            }
        });
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        this.mAlertDialog = create;
        create.setOnShowListener(new DialogInterface.OnShowListener() {
            /* class com.android.settings.bluetooth.$$Lambda$BluetoothNameDialogFragment$UwiP0mVIJKgl9XIciCSL5BjtkO4 */

            public final void onShow(DialogInterface dialogInterface) {
                BluetoothNameDialogFragment.this.lambda$onCreateDialog$1$BluetoothNameDialogFragment(dialogInterface);
            }
        });
        return this.mAlertDialog;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$0 */
    public /* synthetic */ void lambda$onCreateDialog$0$BluetoothNameDialogFragment(DialogInterface dialogInterface, int i) {
        setDeviceName(this.mDeviceNameView.getText().toString().trim());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateDialog$1 */
    public /* synthetic */ void lambda$onCreateDialog$1$BluetoothNameDialogFragment(DialogInterface dialogInterface) {
        InputMethodManager inputMethodManager;
        EditText editText = this.mDeviceNameView;
        if (editText != null && editText.requestFocus() && (inputMethodManager = (InputMethodManager) getContext().getSystemService("input_method")) != null) {
            inputMethodManager.showSoftInput(this.mDeviceNameView, 1);
        }
    }

    @Override // androidx.fragment.app.Fragment, androidx.fragment.app.DialogFragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString("device_name", this.mDeviceNameView.getText().toString());
        bundle.putBoolean("device_name_edited", this.mDeviceNameEdited);
    }

    private View createDialogView(String str) {
        View inflate = ((LayoutInflater) getActivity().getSystemService("layout_inflater")).inflate(C0012R$layout.dialog_edittext, (ViewGroup) null);
        EditText editText = (EditText) inflate.findViewById(C0010R$id.edittext);
        this.mDeviceNameView = editText;
        editText.setFilters(new InputFilter[]{new BluetoothLengthDeviceNameFilter()});
        this.mDeviceNameView.setText(str);
        if (!TextUtils.isEmpty(str)) {
            this.mDeviceNameView.setSelection(str.length());
        }
        this.mDeviceNameView.addTextChangedListener(this);
        Utils.setEditTextCursorPosition(this.mDeviceNameView);
        this.mDeviceNameView.setOnEditorActionListener(this);
        return inflate;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        if (textView.length() != 0 && !textView.getText().toString().trim().isEmpty()) {
            setDeviceName(textView.getText().toString());
        }
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            return true;
        }
        this.mAlertDialog.dismiss();
        return true;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment
    public void onDestroy() {
        super.onDestroy();
        this.mAlertDialog = null;
        this.mDeviceNameView = null;
        this.mOkButton = null;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment
    public void onResume() {
        super.onResume();
        if (this.mOkButton == null) {
            Button button = this.mAlertDialog.getButton(-1);
            this.mOkButton = button;
            button.setEnabled(this.mDeviceNameEdited);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateDeviceName() {
        String deviceName = getDeviceName();
        if (deviceName != null) {
            this.mDeviceNameUpdated = true;
            this.mDeviceNameEdited = false;
            this.mDeviceNameView.setText(deviceName);
        }
    }

    public void afterTextChanged(Editable editable) {
        boolean z = false;
        if (this.mDeviceNameUpdated) {
            this.mDeviceNameUpdated = false;
            this.mOkButton.setEnabled(false);
            return;
        }
        this.mDeviceNameEdited = true;
        Button button = this.mOkButton;
        if (button != null) {
            if (editable.toString().trim().length() != 0) {
                z = true;
            }
            button.setEnabled(z);
        }
    }
}
