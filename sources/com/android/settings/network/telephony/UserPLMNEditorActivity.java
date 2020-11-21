package com.android.settings.network.telephony;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import androidx.constraintlayout.widget.R$styleable;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;

public class UserPLMNEditorActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener, TextWatcher {
    private boolean mAirplaneModeOn = false;
    private IntentFilter mIntentFilter;
    private AlertDialog mNWIDDialog = null;
    private Preference mNWIDPref = null;
    private DialogInterface.OnClickListener mNWIDPrefListener = new DialogInterface.OnClickListener() {
        /* class com.android.settings.network.telephony.UserPLMNEditorActivity.AnonymousClass2 */

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                UserPLMNEditorActivity userPLMNEditorActivity = UserPLMNEditorActivity.this;
                String genText = userPLMNEditorActivity.genText(userPLMNEditorActivity.mNWIDText.getText().toString());
                Log.d("UserPLMNEditorActivity", "input network id is " + genText);
                UserPLMNEditorActivity.this.mNWIDPref.setSummary(genText);
                UserPLMNEditorActivity.this.mNWMPref.setEntries(UserPLMNEditorActivity.this.getResources().getTextArray(UserPLMNEditorActivity.this.selectNetworkChoices(genText)));
            }
        }
    };
    private EditText mNWIDText;
    private ListPreference mNWMPref = null;
    private String mNoSet = null;
    private EditTextPreference mPRIpref = null;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.android.settings.network.telephony.UserPLMNEditorActivity.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.AIRPLANE_MODE".equals(intent.getAction())) {
                UserPLMNEditorActivity.this.mAirplaneModeOn = intent.getBooleanExtra("state", false);
                UserPLMNEditorActivity.this.setScreenEnabled();
            }
        }
    };

    public static int convertApMode2EF(int i) {
        if (i == 3) {
            return 13;
        }
        if (i == 2) {
            return 8;
        }
        return i == 1 ? 4 : 1;
    }

    public static int convertEFMode2Ap(int i) {
        if (i == 13) {
            return 3;
        }
        if (i == 4) {
            return 1;
        }
        return i == 8 ? 2 : 0;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.uplmn_editor);
        this.mNoSet = getResources().getString(C0017R$string.voicemail_number_not_set);
        this.mNWIDPref = findPreference("network_id_key");
        this.mPRIpref = (EditTextPreference) findPreference("priority_key");
        this.mNWMPref = (ListPreference) findPreference("network_mode_key");
        this.mPRIpref.setOnPreferenceChangeListener(this);
        this.mNWMPref.setOnPreferenceChangeListener(this);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.AIRPLANE_MODE");
        this.mIntentFilter = intentFilter;
        registerReceiver(this.mReceiver, intentFilter);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        displayNetworkInfo(getIntent());
        boolean z = false;
        if (Settings.System.getInt(getContentResolver(), "airplane_mode_on", 0) == 1) {
            z = true;
        }
        this.mAirplaneModeOn = z;
        setScreenEnabled();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mReceiver);
    }

    public boolean onPreferenceChange(Preference preference, Object obj) {
        String obj2 = obj.toString();
        EditTextPreference editTextPreference = this.mPRIpref;
        if (preference == editTextPreference) {
            editTextPreference.setSummary(genText(obj2));
            return true;
        }
        ListPreference listPreference = this.mNWMPref;
        if (preference != listPreference) {
            return true;
        }
        listPreference.setValue(obj2);
        this.mNWMPref.setSummary(getResources().getStringArray(selectNetworkChoices(this.mNWIDPref.getSummary().toString()))[Integer.parseInt(obj2)]);
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (!getIntent().getBooleanExtra("uplmn_add", false)) {
            menu.add(0, 1, 0, 17040056);
        }
        menu.add(0, 2, 0, C0017R$string.save);
        menu.add(0, 3, 0, 17039360);
        return true;
    }

    public boolean onMenuOpened(int i, Menu menu) {
        super.onMenuOpened(i, menu);
        boolean z = false;
        boolean z2 = this.mNoSet.equals(this.mNWIDPref.getSummary()) || this.mNoSet.equals(this.mPRIpref.getSummary());
        if (menu != null) {
            menu.setGroupEnabled(0, !this.mAirplaneModeOn);
            if (getIntent().getBooleanExtra("uplmn_add", true)) {
                MenuItem item = menu.getItem(0);
                if (!this.mAirplaneModeOn && !z2) {
                    z = true;
                }
                item.setEnabled(z);
            } else {
                MenuItem item2 = menu.getItem(1);
                if (!this.mAirplaneModeOn && !z2) {
                    z = true;
                }
                item2.setEnabled(z);
            }
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            setRemovedNWInfo();
        } else if (itemId == 2) {
            setSavedNWInfo();
        } else if (itemId == 16908332) {
            finish();
            return true;
        }
        finish();
        return super.onOptionsItemSelected(menuItem);
    }

    private void setSavedNWInfo() {
        Intent intent = new Intent(this, UserPLMNListActivity.class);
        genNWInfoToIntent(intent);
        setResult(R$styleable.Constraint_layout_goneMarginRight, intent);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x0033, code lost:
        if (r2 > r1) goto L_0x003c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void genNWInfoToIntent(android.content.Intent r7) {
        /*
        // Method dump skipped, instructions count: 108
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.network.telephony.UserPLMNEditorActivity.genNWInfoToIntent(android.content.Intent):void");
    }

    private void setRemovedNWInfo() {
        Intent intent = new Intent(this, UserPLMNListActivity.class);
        genNWInfoToIntent(intent);
        setResult(R$styleable.Constraint_layout_goneMarginStart, intent);
    }

    private void displayNetworkInfo(Intent intent) {
        String stringExtra = intent.getStringExtra("uplmn_code");
        this.mNWIDPref.setSummary(genText(stringExtra));
        int i = 0;
        int intExtra = intent.getIntExtra("uplmn_priority", 0);
        this.mPRIpref.setSummary(String.valueOf(intExtra));
        this.mPRIpref.setText(String.valueOf(intExtra));
        int intExtra2 = intent.getIntExtra("uplmn_service", 0);
        Log.d("UserPLMNEditorActivity", "act = " + intExtra2);
        int convertEFMode2Ap = convertEFMode2Ap(intExtra2);
        if (convertEFMode2Ap >= 0 && convertEFMode2Ap <= 3) {
            i = convertEFMode2Ap;
        }
        this.mNWMPref.setEntries(getResources().getTextArray(selectNetworkChoices(stringExtra)));
        this.mNWMPref.setSummary(getResources().getStringArray(selectNetworkChoices(stringExtra))[i]);
        this.mNWMPref.setValue(String.valueOf(i));
    }

    public int selectNetworkChoices(String str) {
        Log.d("UserPLMNEditorActivity", "plmn = " + str);
        for (String str2 : getResources().getStringArray(C0003R$array.uplmn_cu_mcc_mnc_values)) {
            if (str.equals(str2)) {
                return C0003R$array.uplmn_prefer_network_mode_w_choices;
            }
        }
        return C0003R$array.uplmn_prefer_network_mode_td_choices;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private String genText(String str) {
        if (str == null || str.length() == 0) {
            return this.mNoSet;
        }
        return str;
    }

    public void buttonEnabled() {
        int length = this.mNWIDText.getText().toString().length();
        boolean z = length >= 5 && length <= 6;
        AlertDialog alertDialog = this.mNWIDDialog;
        if (alertDialog != null) {
            alertDialog.getButton(-1).setEnabled(z);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setScreenEnabled() {
        getPreferenceScreen().setEnabled(!this.mAirplaneModeOn);
        invalidateOptionsMenu();
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == this.mNWIDPref) {
            removeDialog(0);
            showDialog(0);
            buttonEnabled();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public Dialog onCreateDialog(int i) {
        if (i != 0) {
            return null;
        }
        this.mNWIDText = new EditText(this);
        if (!this.mNoSet.equals(this.mNWIDPref.getSummary())) {
            this.mNWIDText.setText(this.mNWIDPref.getSummary());
        }
        this.mNWIDText.addTextChangedListener(this);
        this.mNWIDText.setInputType(2);
        AlertDialog create = new AlertDialog.Builder(this).setTitle(getResources().getString(C0017R$string.network_id)).setView(this.mNWIDText).setPositiveButton(getResources().getString(17039370), this.mNWIDPrefListener).setNegativeButton(getResources().getString(17039360), (DialogInterface.OnClickListener) null).create();
        this.mNWIDDialog = create;
        create.getWindow().setSoftInputMode(4);
        return this.mNWIDDialog;
    }

    public void afterTextChanged(Editable editable) {
        buttonEnabled();
    }
}
