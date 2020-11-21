package com.android.settings.wifi;

import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0012R$layout;
import com.android.settings.SettingsPreferenceFragment;

public class WifiAPITest extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener {
    private Preference mWifiDisableNetwork;
    private Preference mWifiDisconnect;
    private Preference mWifiEnableNetwork;
    private WifiManager mWifiManager;
    private int netid;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 89;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mWifiManager = (WifiManager) getSystemService("wifi");
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment
    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(C0012R$layout.wifi_api_test);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference findPreference = preferenceScreen.findPreference("disconnect");
        this.mWifiDisconnect = findPreference;
        findPreference.setOnPreferenceClickListener(this);
        Preference findPreference2 = preferenceScreen.findPreference("disable_network");
        this.mWifiDisableNetwork = findPreference2;
        findPreference2.setOnPreferenceClickListener(this);
        Preference findPreference3 = preferenceScreen.findPreference("enable_network");
        this.mWifiEnableNetwork = findPreference3;
        findPreference3.setOnPreferenceClickListener(this);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        super.onPreferenceTreeClick(preference);
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mWifiDisconnect) {
            this.mWifiManager.disconnect();
            return true;
        } else if (preference == this.mWifiDisableNetwork) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Input");
            builder.setMessage("Enter Network ID");
            final EditText editText = new EditText(getPrefContext());
            builder.setView(editText);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                /* class com.android.settings.wifi.WifiAPITest.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    Editable text = editText.getText();
                    try {
                        WifiAPITest.this.netid = Integer.parseInt(text.toString());
                        WifiAPITest.this.mWifiManager.disableNetwork(WifiAPITest.this.netid);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(this) {
                /* class com.android.settings.wifi.WifiAPITest.AnonymousClass2 */

                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();
            return true;
        } else if (preference != this.mWifiEnableNetwork) {
            return true;
        } else {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
            builder2.setTitle("Input");
            builder2.setMessage("Enter Network ID");
            final EditText editText2 = new EditText(getPrefContext());
            builder2.setView(editText2);
            builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                /* class com.android.settings.wifi.WifiAPITest.AnonymousClass3 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    Editable text = editText2.getText();
                    WifiAPITest.this.netid = Integer.parseInt(text.toString());
                    WifiAPITest.this.mWifiManager.enableNetwork(WifiAPITest.this.netid, false);
                }
            });
            builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener(this) {
                /* class com.android.settings.wifi.WifiAPITest.AnonymousClass4 */

                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder2.show();
            return true;
        }
    }
}
