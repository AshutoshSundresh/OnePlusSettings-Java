package com.android.settings.accessibility;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import com.android.settings.C0003R$array;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.google.common.primitives.Ints;

public class MagnificationSettingsFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.accessibility_magnification_service_settings);
    private int mCapabilities = 0;
    private CheckBox mMagnifyFullScreenCheckBox;
    private CheckBox mMagnifyWindowCheckBox;
    private Preference mModePreference;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        return i != 1 ? 0 : 1816;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "MagnificationSettingsFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1815;
    }

    static String getMagnificationCapabilitiesSummary(Context context) {
        String[] stringArray = context.getResources().getStringArray(C0003R$array.magnification_mode_summaries);
        int indexOf = Ints.indexOf(context.getResources().getIntArray(C0003R$array.magnification_mode_values), getMagnificationCapabilities(context));
        if (indexOf == -1) {
            indexOf = 0;
        }
        return stringArray[indexOf];
    }

    private static int getMagnificationCapabilities(Context context) {
        return getSecureIntValue(context, "master_mono", 1);
    }

    private static int getSecureIntValue(Context context, String str, int i) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), str, i, context.getContentResolver().getUserId());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("capability", this.mCapabilities);
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle != null) {
            this.mCapabilities = bundle.getInt("capability", 0);
        }
        if (this.mCapabilities == 0) {
            this.mCapabilities = getMagnificationCapabilities(getPrefContext());
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.dashboard.DashboardFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Preference findPreference = findPreference("magnification_mode");
        this.mModePreference = findPreference;
        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.accessibility.$$Lambda$MagnificationSettingsFragment$f6gdrAzhkt25X_6Jca6AFx7BEI */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return MagnificationSettingsFragment.this.lambda$onCreate$0$MagnificationSettingsFragment(preference);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreate$0 */
    public /* synthetic */ boolean lambda$onCreate$0$MagnificationSettingsFragment(Preference preference) {
        this.mCapabilities = getMagnificationCapabilities(getPrefContext());
        showDialog(1);
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settings.dashboard.DashboardFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.accessibility_magnification_service_settings;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 1) {
            AlertDialog showMagnificationModeDialog = AccessibilityEditDialogUtils.showMagnificationModeDialog(getPrefContext(), getPrefContext().getString(C0017R$string.accessibility_magnification_mode_title), new DialogInterface.OnClickListener() {
                /* class com.android.settings.accessibility.$$Lambda$MagnificationSettingsFragment$4k2Vn9yf8NU68ZSOysqll2ad6Fg */

                public final void onClick(DialogInterface dialogInterface, int i) {
                    MagnificationSettingsFragment.this.callOnAlertDialogCheckboxClicked(dialogInterface, i);
                }
            });
            initializeDialogCheckBox(showMagnificationModeDialog);
            return showMagnificationModeDialog;
        }
        throw new IllegalArgumentException("Unsupported dialogId " + i);
    }

    /* access modifiers changed from: private */
    public void callOnAlertDialogCheckboxClicked(DialogInterface dialogInterface, int i) {
        updateCapabilities(true);
        this.mModePreference.setSummary(getMagnificationCapabilitiesSummary(getPrefContext()));
    }

    private void initializeDialogCheckBox(AlertDialog alertDialog) {
        this.mMagnifyFullScreenCheckBox = (CheckBox) alertDialog.findViewById(C0010R$id.magnify_full_screen).findViewById(C0010R$id.checkbox);
        this.mMagnifyWindowCheckBox = (CheckBox) alertDialog.findViewById(C0010R$id.magnify_window_screen).findViewById(C0010R$id.checkbox);
        updateAlertDialogCheckState();
        updateAlertDialogEnableState();
    }

    private void updateAlertDialogCheckState() {
        updateCheckStatus(this.mMagnifyWindowCheckBox, 2);
        updateCheckStatus(this.mMagnifyFullScreenCheckBox, 1);
    }

    private void updateCheckStatus(CheckBox checkBox, int i) {
        checkBox.setChecked((i & this.mCapabilities) != 0);
        checkBox.setOnClickListener(new View.OnClickListener() {
            /* class com.android.settings.accessibility.$$Lambda$MagnificationSettingsFragment$RNHtVYEOF_LVDzImVCPdPs3hpJM */

            public final void onClick(View view) {
                MagnificationSettingsFragment.this.lambda$updateCheckStatus$1$MagnificationSettingsFragment(view);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateCheckStatus$1 */
    public /* synthetic */ void lambda$updateCheckStatus$1$MagnificationSettingsFragment(View view) {
        updateCapabilities(false);
        updateAlertDialogEnableState();
    }

    private void updateAlertDialogEnableState() {
        if (this.mCapabilities != 3) {
            disableEnabledMagnificationModePreference();
        } else {
            enableAllPreference();
        }
    }

    private void enableAllPreference() {
        this.mMagnifyFullScreenCheckBox.setEnabled(true);
        this.mMagnifyWindowCheckBox.setEnabled(true);
    }

    private void disableEnabledMagnificationModePreference() {
        if (!this.mMagnifyFullScreenCheckBox.isChecked()) {
            this.mMagnifyWindowCheckBox.setEnabled(false);
        } else if (!this.mMagnifyWindowCheckBox.isChecked()) {
            this.mMagnifyFullScreenCheckBox.setEnabled(false);
        }
    }

    private void updateCapabilities(boolean z) {
        int i = 0;
        int isChecked = this.mMagnifyFullScreenCheckBox.isChecked() | 0;
        if (this.mMagnifyWindowCheckBox.isChecked()) {
            i = 2;
        }
        int i2 = isChecked | i;
        this.mCapabilities = i2;
        if (z) {
            setMagnificationCapabilities(i2);
        }
    }

    private void setSecureIntValue(String str, int i) {
        Settings.Secure.putIntForUser(getPrefContext().getContentResolver(), str, i, getPrefContext().getContentResolver().getUserId());
    }

    private void setMagnificationCapabilities(int i) {
        setSecureIntValue("master_mono", i);
    }
}
