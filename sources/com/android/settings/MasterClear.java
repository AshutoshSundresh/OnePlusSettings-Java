package com.android.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.euicc.EuiccManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.google.android.setupcompat.template.FooterButton;
import com.oneplus.settings.ui.OPFactoryResetConfirmCategory;
import com.oneplus.settings.utils.ProductUtils;

public class MasterClear extends SettingsPreferenceFragment implements ViewTreeObserver.OnGlobalLayoutListener, Preference.OnPreferenceChangeListener, OPFactoryResetConfirmCategory.OnFactoryResetConfirmListener {
    static final int CREDENTIAL_CONFIRM_REQUEST = 56;
    static final int KEYGUARD_REQUEST = 55;
    private int isDemoModeEnabled;
    private Context mContext;
    private AlertDialog mDialog;
    CheckBox mEsimStorage;
    View mEsimStorageContainer;
    CheckBox mExternalStorage;
    FooterButton mInitiateButton;
    protected final View.OnClickListener mInitiateListener = new View.OnClickListener() {
        /* class com.android.settings.MasterClear.AnonymousClass1 */

        public void onClick(View view) {
            Context context = view.getContext();
            if (Utils.isDemoUser(context)) {
                ComponentName deviceOwnerComponent = Utils.getDeviceOwnerComponent(context);
                if (deviceOwnerComponent != null) {
                    context.startActivity(new Intent().setPackage(deviceOwnerComponent.getPackageName()).setAction("android.intent.action.FACTORY_RESET"));
                }
            } else if (!MasterClear.this.runKeyguardConfirmation(55)) {
                Intent accountConfirmationIntent = MasterClear.this.getAccountConfirmationIntent();
                if (accountConfirmationIntent != null) {
                    MasterClear.this.showAccountCredentialConfirmation(accountConfirmationIntent);
                } else {
                    MasterClear.this.showFinalConfirmation("");
                }
            }
        }
    };
    private OPFactoryResetConfirmCategory mOPFactoryResetConfirmCategory;
    private SwitchPreference mOptionalSwitchPreference;
    ScrollView mScrollView;
    DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
        /* class com.android.settings.MasterClear.AnonymousClass2 */

        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == -1) {
                if (MasterClear.this.runKeyguardConfirmation(55)) {
                    return;
                }
                if (!ProductUtils.isUsvMode()) {
                    MasterClear.this.showFinalConfirmation("");
                } else if (MasterClear.this.isDemoModeEnabled == 1) {
                    MasterClear.this.vzwDemoModePasswordDialog();
                } else {
                    MasterClear.this.showFinalConfirmation("");
                }
            } else if (i == -2) {
                MasterClear.this.mDialog.dismiss();
            }
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 66;
    }

    /* access modifiers changed from: package-private */
    public boolean isValidRequestCode(int i) {
        return i == 55 || i == 56;
    }

    public void onGlobalLayout() {
        this.mInitiateButton.setEnabled(hasReachedBottom(this.mScrollView));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.op_reset_all_data_settings);
        establishInitialState();
        getActivity().setTitle(C0017R$string.master_clear_short_title);
        this.mContext = getActivity();
        this.isDemoModeEnabled = Settings.Secure.getInt(getActivity().getContentResolver(), "verizonwireless_store_demo_mode", 0);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean runKeyguardConfirmation(int i) {
        return new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(i, getActivity().getResources().getText(C0017R$string.master_clear_short_title));
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        onActivityResultInternal(i, i2, intent);
    }

    /* access modifiers changed from: package-private */
    public void onActivityResultInternal(int i, int i2, Intent intent) {
        Intent accountConfirmationIntent;
        if (isValidRequestCode(i)) {
            if (i2 != -1) {
                establishInitialState();
            } else if (56 != i && (accountConfirmationIntent = getAccountConfirmationIntent()) != null) {
                showAccountCredentialConfirmation(accountConfirmationIntent);
            } else if (intent != null) {
                showFinalConfirmation(intent.getStringExtra("power_on_psw"));
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void showFinalConfirmation(String str) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("erase_sd", this.mOptionalSwitchPreference.isChecked());
        bundle.putString("power_on_psw", str);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getActivity());
        subSettingLauncher.setDestination(MasterClearConfirm.class.getName());
        subSettingLauncher.setSourceMetricsCategory(66);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.launch();
    }

    /* access modifiers changed from: package-private */
    public void showFinalConfirmation() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("erase_sd", this.mExternalStorage.isChecked());
        bundle.putBoolean("erase_esim", this.mEsimStorage.isChecked());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(getContext());
        subSettingLauncher.setDestination(MasterClearConfirm.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setTitleRes(C0017R$string.master_clear_confirm_title);
        subSettingLauncher.setSourceMetricsCategory(getMetricsCategory());
        subSettingLauncher.launch();
    }

    /* access modifiers changed from: package-private */
    public void showAccountCredentialConfirmation(Intent intent) {
        startActivityForResult(intent, 56);
    }

    /* access modifiers changed from: package-private */
    public Intent getAccountConfirmationIntent() {
        ActivityInfo activityInfo;
        FragmentActivity activity = getActivity();
        String string = activity.getString(C0017R$string.account_type);
        String string2 = activity.getString(C0017R$string.account_confirmation_package);
        String string3 = activity.getString(C0017R$string.account_confirmation_class);
        if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string2) || TextUtils.isEmpty(string3)) {
            Log.i("MasterClear", "Resources not set for account confirmation.");
            return null;
        }
        Account[] accountsByType = AccountManager.get(activity).getAccountsByType(string);
        if (accountsByType == null || accountsByType.length <= 0) {
            Log.d("MasterClear", "No " + string + " accounts installed!");
        } else {
            Intent component = new Intent().setPackage(string2).setComponent(new ComponentName(string2, string3));
            ResolveInfo resolveActivity = activity.getPackageManager().resolveActivity(component, 0);
            if (resolveActivity != null && (activityInfo = resolveActivity.activityInfo) != null && string2.equals(activityInfo.packageName)) {
                return component;
            }
            Log.i("MasterClear", "Unable to resolve Activity: " + string2 + "/" + string3);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public void establishInitialState() {
        SwitchPreference switchPreference = (SwitchPreference) findPreference("op_optional_reset");
        this.mOptionalSwitchPreference = switchPreference;
        switchPreference.setChecked(true);
        this.mOptionalSwitchPreference.setOnPreferenceChangeListener(this);
        OPFactoryResetConfirmCategory oPFactoryResetConfirmCategory = (OPFactoryResetConfirmCategory) findPreference("op_factory_reset_confirm");
        this.mOPFactoryResetConfirmCategory = oPFactoryResetConfirmCategory;
        oPFactoryResetConfirmCategory.setOnFactoryResetConfirmListener(this);
    }

    /* access modifiers changed from: package-private */
    public boolean showWipeEuicc() {
        Context context = getContext();
        if (!isEuiccEnabled(context)) {
            return false;
        }
        if (Settings.Global.getInt(context.getContentResolver(), "euicc_provisioned", 0) != 0 || DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean showWipeEuiccCheckbox() {
        return SystemProperties.getBoolean("masterclear.allow_retain_esim_profiles_after_fdr", false);
    }

    /* access modifiers changed from: protected */
    public boolean isEuiccEnabled(Context context) {
        return ((EuiccManager) context.getSystemService("euicc")).isEnabled();
    }

    /* access modifiers changed from: package-private */
    public boolean hasReachedBottom(ScrollView scrollView) {
        return scrollView.getChildCount() < 1 || scrollView.getChildAt(0).getBottom() - (scrollView.getHeight() + scrollView.getScrollY()) <= 0;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mOptionalSwitchPreference) {
            if (!((Boolean) obj).booleanValue()) {
                this.mOPFactoryResetConfirmCategory.setConfirmButtonText(C0017R$string.oneplus_erase_system_data);
            } else {
                this.mOPFactoryResetConfirmCategory.setConfirmButtonText(C0017R$string.master_clear_button_text);
            }
        }
        return true;
    }

    @Override // com.oneplus.settings.ui.OPFactoryResetConfirmCategory.OnFactoryResetConfirmListener
    public void onFactoryResetConfirmClick() {
        if (this.mOptionalSwitchPreference.isChecked()) {
            if (this.mDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
                builder.setTitle(C0017R$string.master_clear_confirm_summary);
                builder.setPositiveButton(17041197, this.onClickListener);
                builder.setNegativeButton(17039360, this.onClickListener);
                this.mDialog = builder.create();
            }
            this.mDialog.show();
        } else if (runKeyguardConfirmation(55)) {
        } else {
            if (!ProductUtils.isUsvMode()) {
                showFinalConfirmation("");
            } else if (this.isDemoModeEnabled == 1) {
                vzwDemoModePasswordDialog();
            } else {
                showFinalConfirmation("");
            }
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void vzwDemoModePasswordDialog() {
        final View inflate = LayoutInflater.from(this.mContext).inflate(C0012R$layout.vzw_alert_dialog_password_entry, (ViewGroup) null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(C0017R$string.verizon_store_demo_mode_title);
        builder.setView(inflate);
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            /* class com.android.settings.MasterClear.AnonymousClass4 */

            public void onClick(DialogInterface dialogInterface, int i) {
                if ("#VerizonDemoUnit#".equals(((EditText) inflate.findViewById(C0010R$id.password)).getText().toString())) {
                    MasterClear.this.showFinalConfirmation("");
                } else {
                    MasterClear.this.vzwDemoModePasswordDialog();
                }
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener(this) {
            /* class com.android.settings.MasterClear.AnonymousClass3 */

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog create = builder.create();
        if (create != null) {
            create.show();
        }
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onDestroy() {
        AlertDialog alertDialog;
        FragmentActivity activity = getActivity();
        if (!(activity == null || activity.isFinishing() || (alertDialog = this.mDialog) == null)) {
            alertDialog.dismiss();
            this.mDialog = null;
        }
        super.onDestroy();
    }
}
