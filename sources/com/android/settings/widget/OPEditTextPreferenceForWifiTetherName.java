package com.android.settings.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settingslib.CustomEditTextPreferenceCompat;
import com.oneplus.settings.utils.OPUtils;

public class OPEditTextPreferenceForWifiTetherName extends CustomEditTextPreferenceCompat {
    private EditText dialogEditText;
    private CheckBox mCheckBox;
    private Context mContext;
    private boolean mIsPassword;
    private boolean mIsSummaryPassword;
    private final EditTextWatcher mTextWatcher = new EditTextWatcher();
    private Validator mValidator;
    private String nameTemp = null;
    private CheckBox noShowCheckBox;
    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        /* class com.android.settings.widget.OPEditTextPreferenceForWifiTetherName.AnonymousClass2 */

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                Settings.Secure.putIntForUser(OPEditTextPreferenceForWifiTetherName.this.mContext.getContentResolver(), "oneplus__broadcat_wifi_no_show_dialog", OPEditTextPreferenceForWifiTetherName.this.noShowCheckBox.isChecked() ? 1 : 0, -2);
            }
        }
    };

    public interface Validator {
        boolean isTextValid(String str);
    }

    public OPEditTextPreferenceForWifiTetherName(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mContext = context;
    }

    public OPEditTextPreferenceForWifiTetherName(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    public OPEditTextPreferenceForWifiTetherName(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public OPEditTextPreferenceForWifiTetherName(Context context) {
        super(context);
        this.mContext = context;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settingslib.CustomEditTextPreferenceCompat
    public void onBindDialogView(View view) {
        super.onBindDialogView(view);
        EditText editText = (EditText) view.findViewById(16908291);
        this.dialogEditText = editText;
        if (editText != null && !TextUtils.isEmpty(editText.getText())) {
            editText.setSelection(editText.getText().length());
        }
        boolean z = true;
        if (!(this.mValidator == null || editText == null)) {
            editText.removeTextChangedListener(this.mTextWatcher);
            if (this.mIsPassword) {
                editText.setInputType(145);
                editText.setMaxLines(1);
            }
            editText.addTextChangedListener(this.mTextWatcher);
        }
        if (OPUtils.isSupportUstMode()) {
            LinearLayout linearLayout = (LinearLayout) editText.getParent();
            View inflate = LayoutInflater.from(this.mContext).inflate(C0012R$layout.op_wifi_broadcast_checkbox, (ViewGroup) null, false);
            this.mCheckBox = (CheckBox) inflate.findViewById(C0010R$id.checkbox);
            if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "oneplus_is_broadcat_wifi_name", 0, -2) == 0) {
                z = false;
            }
            this.mCheckBox.setChecked(z);
            this.mCheckBox.setText(C0017R$string.oneplus_broadcat_wifi_name);
            this.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                /* class com.android.settings.widget.OPEditTextPreferenceForWifiTetherName.AnonymousClass1 */

                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    if (!z) {
                        boolean z2 = true;
                        if (Settings.Secure.getIntForUser(OPEditTextPreferenceForWifiTetherName.this.mContext.getContentResolver(), "oneplus__broadcat_wifi_no_show_dialog", 0, -2) == 1) {
                            z2 = false;
                        }
                        if (z2) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(OPEditTextPreferenceForWifiTetherName.this.mContext);
                            View inflate = LayoutInflater.from(OPEditTextPreferenceForWifiTetherName.this.mContext).inflate(C0012R$layout.op_wifi_broadcast_isvisible_dialog, (ViewGroup) null, false);
                            builder.setTitle(C0017R$string.oneplus_wifi_name_dialog_title);
                            builder.setView(inflate);
                            builder.setPositiveButton(17039370, OPEditTextPreferenceForWifiTetherName.this.onClickListener);
                            builder.create();
                            OPEditTextPreferenceForWifiTetherName.this.noShowCheckBox = (CheckBox) inflate.findViewById(C0010R$id.isvisible_checkbox);
                            builder.show();
                        }
                    }
                }
            });
            linearLayout.addView(inflate);
        }
    }

    @Override // com.android.settingslib.CustomEditTextPreferenceCompat
    public void onClick(DialogInterface dialogInterface, int i) {
        if (OPUtils.isSupportUstMode()) {
            Log.d("OPValidatedEditTextPreference", "onClick  hide soft first");
            InputMethodManager inputMethodManager = null;
            if (getContext() != null) {
                inputMethodManager = (InputMethodManager) getContext().getSystemService("input_method");
            }
            if (!(getEditText() == null || inputMethodManager == null)) {
                inputMethodManager.hideSoftInputFromWindow(getEditText().getWindowToken(), 0);
            }
            if (i == -1) {
                Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "oneplus_is_broadcat_wifi_name", this.mCheckBox.isChecked() ? 1 : 0, -2);
            }
        }
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(16908304);
        if (textView != null) {
            if (this.mIsSummaryPassword) {
                textView.setInputType(129);
            } else {
                textView.setInputType(524289);
            }
        }
    }

    public boolean isPassword() {
        return this.mIsPassword;
    }

    public void setValidator(Validator validator) {
        this.mValidator = validator;
    }

    private class EditTextWatcher implements TextWatcher {
        int num;

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        private EditTextWatcher() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            OPEditTextPreferenceForWifiTetherName.this.nameTemp = charSequence.toString();
        }

        public void afterTextChanged(Editable editable) {
            EditText editText = OPEditTextPreferenceForWifiTetherName.this.dialogEditText;
            if (OPEditTextPreferenceForWifiTetherName.this.mValidator != null && editText != null) {
                AlertDialog alertDialog = (AlertDialog) OPEditTextPreferenceForWifiTetherName.this.getDialog();
                boolean isTextValid = OPEditTextPreferenceForWifiTetherName.this.mValidator.isTextValid(editText.getText().toString());
                if (alertDialog != null) {
                    alertDialog.getButton(-1).setEnabled(isTextValid);
                }
                int length = editable.toString().getBytes().length;
                this.num = length;
                if (length > 32) {
                    editText.setText(OPEditTextPreferenceForWifiTetherName.this.nameTemp);
                    Editable text = editText.getText();
                    if (text instanceof Spannable) {
                        Selection.setSelection(text, text.length());
                    }
                }
            }
        }
    }
}
