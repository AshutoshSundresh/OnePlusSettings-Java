package com.oneplus.settings;

import android.bluetooth.BluetoothAdapter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.utils.OPUtils;

public class OPDeviceName extends SettingsPreferenceFragment implements View.OnClickListener {
    private static EditText mDeviceName;
    private static TextView mOKView;
    private static View mView;
    private MenuItem mOKMenuItem;
    private String nameTemp = null;

    private boolean isNotEmojiCharacter(char c) {
        return c == 0 || c == '\t' || c == '\n' || c == '\r' || (c >= ' ' && c <= 55295) || ((c >= 57344 && c <= 65533) || (c >= 0 && c <= 65535));
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
        getActivity().getWindow().setSoftInputMode(5);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(C0012R$layout.op_device_name, viewGroup, false);
        mView = inflate;
        mDeviceName = (EditText) inflate.findViewById(C0010R$id.device_name);
        String string = Settings.System.getString(getActivity().getContentResolver(), "oem_oneplus_modified_devicename");
        Settings.System.getString(getActivity().getContentResolver(), "oem_oneplus_devicename");
        String resetDeviceNameIfInvalid = OPUtils.resetDeviceNameIfInvalid(getActivity());
        if (string == null && (resetDeviceNameIfInvalid == null || resetDeviceNameIfInvalid.equals("oneplus") || resetDeviceNameIfInvalid.equals("ONE E1001") || resetDeviceNameIfInvalid.equals("ONE E1003") || resetDeviceNameIfInvalid.equals("ONE E1005"))) {
            resetDeviceNameIfInvalid = SystemProperties.get("ro.display.series");
            Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_devicename", resetDeviceNameIfInvalid);
            Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_modified_devicename", "1");
        }
        if (resetDeviceNameIfInvalid.length() > 32) {
            resetDeviceNameIfInvalid = resetDeviceNameIfInvalid.substring(0, 31);
            Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_devicename", resetDeviceNameIfInvalid);
        }
        mDeviceName.setText(resetDeviceNameIfInvalid);
        if (resetDeviceNameIfInvalid != null) {
            mDeviceName.setSelection(resetDeviceNameIfInvalid.length());
        }
        mDeviceName.selectAll();
        mDeviceName.addTextChangedListener(new TextWatcher() {
            /* class com.oneplus.settings.OPDeviceName.AnonymousClass1 */
            String name;
            int num;

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (OPDeviceName.mDeviceName != null && OPDeviceName.mDeviceName.length() != 0 && (OPDeviceName.mDeviceName.getText() instanceof Spannable)) {
                    OPDeviceName.this.nameTemp = OPDeviceName.mDeviceName.getText().toString();
                }
            }

            public void afterTextChanged(Editable editable) {
                if (!(OPDeviceName.mDeviceName == null || OPDeviceName.mDeviceName.length() == 0)) {
                    String obj = OPDeviceName.mDeviceName.getText().toString();
                    this.name = obj;
                    int length = obj.getBytes().length;
                    this.num = length;
                    if (length > 32) {
                        OPDeviceName.mDeviceName.setText(OPDeviceName.this.nameTemp);
                        Editable text = OPDeviceName.mDeviceName.getText();
                        if (text instanceof Spannable) {
                            Selection.setSelection(text, text.length());
                        }
                    }
                }
                boolean z = editable.length() != 0 && !editable.toString().trim().isEmpty();
                OPDeviceName.mOKView.setEnabled(z);
                if (OPDeviceName.this.mOKMenuItem != null) {
                    OPDeviceName.this.mOKMenuItem.setEnabled(z);
                }
            }
        });
        TextView textView = (TextView) mView.findViewById(C0010R$id.ok);
        mOKView = textView;
        textView.setOnClickListener(this);
        setHasOptionsMenu(true);
        return mView;
    }

    public void onClick(View view) {
        if (view.getId() == C0010R$id.ok) {
            String trim = mDeviceName.getText().toString().trim();
            if (trim.length() != 0) {
                Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_devicename", trim);
            }
            finish();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 1, 0, C0017R$string.oneplus_device_name_ok);
        this.mOKMenuItem = add;
        add.setEnabled(true).setShowAsAction(2);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            String trim = mDeviceName.getText().toString().trim();
            if (trim.length() != 0) {
                if (trim.equalsIgnoreCase("null")) {
                    Toast.makeText(getActivity(), C0017R$string.wifi_p2p_failed_rename_message, 0).show();
                    return true;
                }
                for (int i = 0; i < trim.length(); i++) {
                    if (!isNotEmojiCharacter(trim.charAt(i))) {
                        Toast.makeText(getActivity(), C0017R$string.wifi_p2p_failed_rename_message, 0).show();
                        return true;
                    }
                }
                Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_devicename", trim);
                Settings.System.putString(getActivity().getContentResolver(), "oem_oneplus_modified_devicename", "1");
                BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                if (defaultAdapter != null) {
                    defaultAdapter.setName(trim);
                }
                WifiP2pManager wifiP2pManager = (WifiP2pManager) getSystemService("wifip2p");
                if (wifiP2pManager != null) {
                    wifiP2pManager.setDeviceName(wifiP2pManager.initialize(getActivity(), getActivity().getMainLooper(), null), trim, null);
                }
            }
            finish();
            return true;
        } else if (itemId != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            finish();
            return true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
    }
}
