package com.android.settings;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Proxy;
import android.net.ProxyInfo;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.InstrumentedFragment;

public class ProxySelector extends InstrumentedFragment implements DialogCreatable {
    Button mClearButton;
    View.OnClickListener mClearHandler = new View.OnClickListener() {
        /* class com.android.settings.ProxySelector.AnonymousClass2 */

        public void onClick(View view) {
            ProxySelector.this.mHostnameField.setText("");
            ProxySelector.this.mPortField.setText("");
            ProxySelector.this.mExclusionListField.setText("");
        }
    };
    Button mDefaultButton;
    View.OnClickListener mDefaultHandler = new View.OnClickListener() {
        /* class com.android.settings.ProxySelector.AnonymousClass3 */

        public void onClick(View view) {
            ProxySelector.this.populateFields();
        }
    };
    private SettingsPreferenceFragment.SettingsDialogFragment mDialogFragment;
    EditText mExclusionListField;
    EditText mHostnameField;
    Button mOKButton;
    View.OnClickListener mOKHandler = new View.OnClickListener() {
        /* class com.android.settings.ProxySelector.AnonymousClass1 */

        public void onClick(View view) {
            if (ProxySelector.this.saveToDb()) {
                ProxySelector.this.getActivity().onBackPressed();
            }
        }
    };
    View.OnFocusChangeListener mOnFocusChangeHandler = new View.OnFocusChangeListener(this) {
        /* class com.android.settings.ProxySelector.AnonymousClass4 */

        public void onFocusChange(View view, boolean z) {
            if (z) {
                Selection.selectAll((Spannable) ((TextView) view).getText());
            }
        }
    };
    EditText mPortField;
    private View mView;

    @Override // com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return 574;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 82;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0012R$layout.proxy, viewGroup, false);
        this.mView = inflate;
        initView(inflate);
        populateFields();
        return this.mView;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        boolean z = ((DevicePolicyManager) getActivity().getSystemService("device_policy")).getGlobalProxyAdmin() == null;
        this.mHostnameField.setEnabled(z);
        this.mPortField.setEnabled(z);
        this.mExclusionListField.setEnabled(z);
        this.mOKButton.setEnabled(z);
        this.mClearButton.setEnabled(z);
        this.mDefaultButton.setEnabled(z);
    }

    @Override // com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i != 0) {
            return null;
        }
        String string = getActivity().getString(validate(this.mHostnameField.getText().toString().trim(), this.mPortField.getText().toString().trim(), this.mExclusionListField.getText().toString().trim()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(C0017R$string.proxy_error);
        builder.setPositiveButton(C0017R$string.proxy_error_dismiss, (DialogInterface.OnClickListener) null);
        builder.setMessage(string);
        return builder.create();
    }

    private void showDialog(int i) {
        if (this.mDialogFragment != null) {
            Log.e("ProxySelector", "Old dialog fragment not null!");
        }
        SettingsPreferenceFragment.SettingsDialogFragment newInstance = SettingsPreferenceFragment.SettingsDialogFragment.newInstance(this, i);
        this.mDialogFragment = newInstance;
        newInstance.show(getActivity().getSupportFragmentManager(), Integer.toString(i));
    }

    private void initView(View view) {
        EditText editText = (EditText) view.findViewById(C0010R$id.hostname);
        this.mHostnameField = editText;
        editText.setOnFocusChangeListener(this.mOnFocusChangeHandler);
        EditText editText2 = (EditText) view.findViewById(C0010R$id.port);
        this.mPortField = editText2;
        editText2.setOnClickListener(this.mOKHandler);
        this.mPortField.setOnFocusChangeListener(this.mOnFocusChangeHandler);
        EditText editText3 = (EditText) view.findViewById(C0010R$id.exclusionlist);
        this.mExclusionListField = editText3;
        editText3.setOnFocusChangeListener(this.mOnFocusChangeHandler);
        Button button = (Button) view.findViewById(C0010R$id.action);
        this.mOKButton = button;
        button.setOnClickListener(this.mOKHandler);
        Button button2 = (Button) view.findViewById(C0010R$id.clear);
        this.mClearButton = button2;
        button2.setOnClickListener(this.mClearHandler);
        Button button3 = (Button) view.findViewById(C0010R$id.defaultView);
        this.mDefaultButton = button3;
        button3.setOnClickListener(this.mDefaultHandler);
    }

    /* access modifiers changed from: package-private */
    public void populateFields() {
        int i;
        String str;
        String str2;
        FragmentActivity activity = getActivity();
        ProxyInfo globalProxy = ((ConnectivityManager) getActivity().getSystemService("connectivity")).getGlobalProxy();
        String str3 = "";
        if (globalProxy != null) {
            str = globalProxy.getHost();
            i = globalProxy.getPort();
            str2 = globalProxy.getExclusionListAsString();
        } else {
            i = -1;
            str2 = str3;
            str = str2;
        }
        if (str == null) {
            str = str3;
        }
        this.mHostnameField.setText(str);
        if (i != -1) {
            str3 = Integer.toString(i);
        }
        this.mPortField.setText(str3);
        this.mExclusionListField.setText(str2);
        Intent intent = activity.getIntent();
        String stringExtra = intent.getStringExtra("button-label");
        if (!TextUtils.isEmpty(stringExtra)) {
            this.mOKButton.setText(stringExtra);
        }
        String stringExtra2 = intent.getStringExtra("title");
        if (!TextUtils.isEmpty(stringExtra2)) {
            activity.setTitle(stringExtra2);
        } else {
            activity.setTitle(C0017R$string.proxy_settings_title);
        }
    }

    public static int validate(String str, String str2, String str3) {
        int validate = Proxy.validate(str, str2, str3);
        if (validate == 0) {
            return 0;
        }
        if (validate == 1) {
            return C0017R$string.proxy_error_empty_host_set_port;
        }
        if (validate == 2) {
            return C0017R$string.proxy_error_invalid_host;
        }
        if (validate == 3) {
            return C0017R$string.proxy_error_empty_port;
        }
        if (validate == 4) {
            return C0017R$string.proxy_error_invalid_port;
        }
        if (validate == 5) {
            return C0017R$string.proxy_error_invalid_exclusion_list;
        }
        Log.e("ProxySelector", "Unknown proxy settings error");
        return -1;
    }

    /* access modifiers changed from: package-private */
    public boolean saveToDb() {
        String trim = this.mHostnameField.getText().toString().trim();
        String trim2 = this.mPortField.getText().toString().trim();
        String trim3 = this.mExclusionListField.getText().toString().trim();
        int i = 0;
        if (validate(trim, trim2, trim3) != 0) {
            showDialog(0);
            return false;
        }
        if (trim2.length() > 0) {
            try {
                i = Integer.parseInt(trim2);
            } catch (NumberFormatException unused) {
                return false;
            }
        }
        ((ConnectivityManager) getActivity().getSystemService("connectivity")).setGlobalProxy(new ProxyInfo(trim, i, trim3));
        return true;
    }
}
