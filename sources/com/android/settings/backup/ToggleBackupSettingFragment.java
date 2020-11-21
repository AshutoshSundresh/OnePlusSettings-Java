package com.android.settings.backup;

import android.app.Dialog;
import android.app.backup.IBackupManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SwitchBar;
import com.android.settings.widget.ToggleSwitch;

public class ToggleBackupSettingFragment extends SettingsPreferenceFragment implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private IBackupManager mBackupManager;
    private Dialog mConfirmDialog;
    private Preference mSummaryPreference;
    protected SwitchBar mSwitchBar;
    protected ToggleSwitch mToggleSwitch;
    private boolean mWaitingForConfirmationDialog = false;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 81;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
        PreferenceScreen createPreferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
        setPreferenceScreen(createPreferenceScreen);
        AnonymousClass1 r0 = new Preference(this, getPrefContext()) {
            /* class com.android.settings.backup.ToggleBackupSettingFragment.AnonymousClass1 */

            @Override // androidx.preference.Preference
            public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
                super.onBindViewHolder(preferenceViewHolder);
                ((TextView) preferenceViewHolder.findViewById(16908304)).setText(getSummary());
            }
        };
        this.mSummaryPreference = r0;
        r0.setPersistent(false);
        this.mSummaryPreference.setLayoutResource(C0012R$layout.text_description_preference);
        createPreferenceScreen.addPreference(this.mSummaryPreference);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        boolean z;
        super.onViewCreated(view, bundle);
        SwitchBar switchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        this.mSwitchBar = switchBar;
        this.mToggleSwitch = switchBar.getSwitch();
        if (Settings.Secure.getInt(getContentResolver(), "user_full_data_backup_aware", 0) != 0) {
            this.mSummaryPreference.setSummary(C0017R$string.fullbackup_data_summary);
        } else {
            this.mSummaryPreference.setSummary(C0017R$string.backup_data_summary);
        }
        try {
            if (this.mBackupManager == null) {
                z = false;
            } else {
                z = this.mBackupManager.isBackupEnabled();
            }
            this.mSwitchBar.setCheckedInternal(z);
        } catch (RemoteException unused) {
            this.mSwitchBar.setEnabled(false);
        }
        getActivity().setTitle(C0017R$string.backup_data_title);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mToggleSwitch.setOnBeforeCheckedChangeListener(null);
        this.mSwitchBar.hide();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mToggleSwitch.setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener() {
            /* class com.android.settings.backup.ToggleBackupSettingFragment.AnonymousClass2 */

            @Override // com.android.settings.widget.ToggleSwitch.OnBeforeCheckedChangeListener
            public boolean onBeforeCheckedChanged(ToggleSwitch toggleSwitch, boolean z) {
                if (!z) {
                    ToggleBackupSettingFragment.this.showEraseBackupDialog();
                    return true;
                }
                ToggleBackupSettingFragment.this.setBackupEnabled(true);
                ToggleBackupSettingFragment.this.mSwitchBar.setCheckedInternal(true);
                return true;
            }
        });
        this.mSwitchBar.show();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onStop() {
        Dialog dialog = this.mConfirmDialog;
        if (dialog != null && dialog.isShowing()) {
            this.mConfirmDialog.dismiss();
        }
        this.mConfirmDialog = null;
        super.onStop();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            this.mWaitingForConfirmationDialog = false;
            setBackupEnabled(false);
            this.mSwitchBar.setCheckedInternal(false);
        } else if (i == -2) {
            this.mWaitingForConfirmationDialog = false;
            setBackupEnabled(true);
            this.mSwitchBar.setCheckedInternal(true);
        }
    }

    public void onDismiss(DialogInterface dialogInterface) {
        if (this.mWaitingForConfirmationDialog) {
            setBackupEnabled(true);
            this.mSwitchBar.setCheckedInternal(true);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showEraseBackupDialog() {
        CharSequence charSequence;
        if (Settings.Secure.getInt(getContentResolver(), "user_full_data_backup_aware", 0) != 0) {
            charSequence = getResources().getText(C0017R$string.fullbackup_erase_dialog_message);
        } else {
            charSequence = getResources().getText(C0017R$string.backup_erase_dialog_message);
        }
        this.mWaitingForConfirmationDialog = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(charSequence);
        builder.setTitle(C0017R$string.backup_erase_dialog_title);
        builder.setPositiveButton(17039370, this);
        builder.setNegativeButton(17039360, this);
        builder.setOnDismissListener(this);
        this.mConfirmDialog = builder.show();
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void setBackupEnabled(boolean z) {
        IBackupManager iBackupManager = this.mBackupManager;
        if (iBackupManager != null) {
            try {
                iBackupManager.setBackupEnabled(z);
            } catch (RemoteException e) {
                Log.e("ToggleBackupSettingFragment", "Error communicating with BackupManager", e);
            }
        }
    }
}
