package com.android.settings;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.app.admin.FactoryResetProtectionPolicy;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.service.oemlock.OemLockManager;
import android.service.persistentdata.PersistentDataBlockManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.core.InstrumentedFragment;
import com.android.settings.enterprise.ActionDisabledByAdminDialogHelper;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.Utils;
import com.google.android.setupcompat.template.FooterBarMixin;
import com.google.android.setupcompat.template.FooterButton;
import com.google.android.setupcompat.util.WizardManagerHelper;
import com.google.android.setupdesign.GlifLayout;
import com.oneplus.settings.OPRebootWipeUserdata;
import com.oneplus.settings.utils.OPUtils;
import com.oneplus.settings.utils.ProductUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class MasterClearConfirm extends InstrumentedFragment {
    View mContentView;
    boolean mEraseEsims;
    private boolean mEraseSdCard;
    private View.OnClickListener mFinalClickListener = new View.OnClickListener() {
        /* class com.android.settings.MasterClearConfirm.AnonymousClass1 */

        public void onClick(View view) {
            if (!Utils.isMonkeyRunning()) {
                final PersistentDataBlockManager persistentDataBlockManager = (PersistentDataBlockManager) MasterClearConfirm.this.getActivity().getSystemService("persistent_data_block");
                if (MasterClearConfirm.this.shouldWipePersistentDataBlock(persistentDataBlockManager)) {
                    new AsyncTask<Void, Void, Void>() {
                        /* class com.android.settings.MasterClearConfirm.AnonymousClass1.AnonymousClass1 */
                        int mOldOrientation;
                        ProgressDialog mProgressDialog;

                        /* access modifiers changed from: protected */
                        public Void doInBackground(Void... voidArr) {
                            persistentDataBlockManager.wipe();
                            return null;
                        }

                        /* access modifiers changed from: protected */
                        public void onPostExecute(Void r2) {
                            this.mProgressDialog.hide();
                            if (MasterClearConfirm.this.getActivity() != null) {
                                MasterClearConfirm.this.getActivity().setRequestedOrientation(this.mOldOrientation);
                                MasterClearConfirm.this.doMasterClear();
                            }
                        }

                        /* access modifiers changed from: protected */
                        public void onPreExecute() {
                            ProgressDialog progressDialog = AnonymousClass1.this.getProgressDialog();
                            this.mProgressDialog = progressDialog;
                            progressDialog.show();
                            this.mOldOrientation = MasterClearConfirm.this.getActivity().getRequestedOrientation();
                            MasterClearConfirm.this.getActivity().setRequestedOrientation(14);
                        }
                    }.execute(new Void[0]);
                } else {
                    MasterClearConfirm.this.doMasterClear();
                }
            }
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private ProgressDialog getProgressDialog() {
            ProgressDialog progressDialog = new ProgressDialog(MasterClearConfirm.this.getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(MasterClearConfirm.this.getActivity().getString(C0017R$string.master_clear_progress_title));
            progressDialog.setMessage(MasterClearConfirm.this.getActivity().getString(C0017R$string.master_clear_progress_text));
            return progressDialog;
        }
    };
    private String mPowerOnPsw;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 67;
    }

    private void storeLastFactoryResetTime() {
        File file = new File("/mnt/vendor/op2/last_factory_reset");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.d("MasterClearConfirm", "Exception", e);
            }
        }
        Date date = new Date(System.currentTimeMillis());
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(date.toString());
            fileWriter.close();
        } catch (IOException e2) {
            Log.d("MasterClearConfirm", "Exception", e2);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean shouldWipePersistentDataBlock(PersistentDataBlockManager persistentDataBlockManager) {
        if (persistentDataBlockManager == null || isDeviceStillBeingProvisioned() || isOemUnlockedAllowed()) {
            return false;
        }
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getActivity().getSystemService("device_policy");
        if (!devicePolicyManager.isFactoryResetProtectionPolicySupported()) {
            return false;
        }
        FactoryResetProtectionPolicy factoryResetProtectionPolicy = devicePolicyManager.getFactoryResetProtectionPolicy(null);
        if (!devicePolicyManager.isOrganizationOwnedDeviceWithManagedProfile() || factoryResetProtectionPolicy == null || !factoryResetProtectionPolicy.isNotEmpty()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isOemUnlockedAllowed() {
        return ((OemLockManager) getActivity().getSystemService("oem_lock")).isOemUnlockAllowed();
    }

    /* access modifiers changed from: package-private */
    public boolean isDeviceStillBeingProvisioned() {
        return !WizardManagerHelper.isDeviceProvisioned(getActivity());
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void doMasterClear() {
        if (ProductUtils.isUsvMode()) {
            storeLastFactoryResetTime();
        }
        try {
            if (this.mEraseSdCard) {
                if (OPUtils.isSurportNoNeedPowerOnPassword(getActivity())) {
                    OPRebootWipeUserdata.rebootWipeUserData(getActivity(), false, "MasterClearConfirm", "--wipe_data", this.mPowerOnPsw);
                    throw null;
                } else if (checkIfNeedPasswordToPowerOn()) {
                    OPRebootWipeUserdata.rebootWipeUserData(getActivity(), false, "MasterClearConfirm", "--wipe_data", this.mPowerOnPsw);
                    throw null;
                } else {
                    OPRebootWipeUserdata.rebootWipeUserData(getActivity(), false, "MasterClearConfirm", "--wipe_data", "");
                    throw null;
                }
            } else if (OPUtils.isSurportNoNeedPowerOnPassword(getActivity())) {
                OPRebootWipeUserdata.rebootWipeUserData(getActivity(), false, "OPMasterClearConfirm", "--delete_data", this.mPowerOnPsw);
                throw null;
            } else if (checkIfNeedPasswordToPowerOn()) {
                OPRebootWipeUserdata.rebootWipeUserData(getActivity(), false, "OPMasterClearConfirm", "--delete_data", this.mPowerOnPsw);
                throw null;
            } else {
                OPRebootWipeUserdata.rebootWipeUserData(getActivity(), false, "OPMasterClearConfirm", "--delete_data", "");
                throw null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MasterClearConfim", "bootCommand Reboot failed (no permissions?)");
        }
    }

    public boolean checkIfNeedPasswordToPowerOn() {
        return Settings.Global.getInt(getActivity().getContentResolver(), "require_password_to_decrypt", 0) == 1;
    }

    private void establishFinalConfirmationState() {
        GlifLayout glifLayout = (GlifLayout) this.mContentView.findViewById(C0010R$id.setup_wizard_layout);
        if (glifLayout != null) {
            glifLayout.setHeaderText(this.mEraseSdCard ? C0017R$string.master_clear_confirm_title : C0017R$string.oneplus_erase_system_data_title);
        }
        FooterBarMixin footerBarMixin = (FooterBarMixin) glifLayout.getMixin(FooterBarMixin.class);
        FooterButton.Builder builder = new FooterButton.Builder(getActivity());
        builder.setText(this.mEraseSdCard ? C0017R$string.master_clear_button_text : C0017R$string.oneplus_erase_system_data);
        builder.setListener(this.mFinalClickListener);
        builder.setButtonType(0);
        builder.setTheme(C0018R$style.OnePlusPrimaryButtonStyle);
        footerBarMixin.setPrimaryButton(builder.build());
    }

    private void setUpActionBarAndTitle() {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.e("MasterClearConfirm", "No activity attached, skipping setUpActionBarAndTitle");
            return;
        }
        ActionBar actionBar = activity.getActionBar();
        if (actionBar == null) {
            Log.e("MasterClearConfirm", "No actionbar, skipping setUpActionBarAndTitle");
            return;
        }
        actionBar.hide();
        activity.getWindow().setStatusBarColor(0);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_factory_reset", UserHandle.myUserId());
        if (RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), "no_factory_reset", UserHandle.myUserId())) {
            return layoutInflater.inflate(C0012R$layout.master_clear_disallowed_screen, (ViewGroup) null);
        }
        if (checkIfRestrictionEnforced != null) {
            AlertDialog.Builder prepareDialogBuilder = new ActionDisabledByAdminDialogHelper(getActivity()).prepareDialogBuilder("no_factory_reset", checkIfRestrictionEnforced);
            prepareDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                /* class com.android.settings.$$Lambda$MasterClearConfirm$weRgiuD2TQnm7jx9NX_qHWwsHU */

                public final void onDismiss(DialogInterface dialogInterface) {
                    MasterClearConfirm.this.lambda$onCreateView$0$MasterClearConfirm(dialogInterface);
                }
            });
            prepareDialogBuilder.show();
            return new View(getActivity());
        }
        View inflate = layoutInflater.inflate(C0012R$layout.master_clear_confirm, (ViewGroup) null);
        this.mContentView = inflate;
        inflate.findViewById(C0010R$id.setup_wizard_layout).setFitsSystemWindows(true);
        setUpActionBarAndTitle();
        establishFinalConfirmationState();
        setAccessibilityTitle();
        setSubtitle();
        return this.mContentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateView$0 */
    public /* synthetic */ void lambda$onCreateView$0$MasterClearConfirm(DialogInterface dialogInterface) {
        getActivity().finish();
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (OPUtils.isAndroidModeOn(getActivity().getContentResolver())) {
            getActivity().getWindow().setStatusBarColor(Utils.getColorAttrDefaultColor(getActivity(), C0004R$attr.onePlusActionbarBackgroundColor));
        }
    }

    private void setAccessibilityTitle() {
        CharSequence title = getActivity().getTitle();
        TextView textView = (TextView) this.mContentView.findViewById(C0010R$id.sud_layout_description);
        if (textView != null) {
            if (!this.mEraseEsims) {
                textView.setText(this.mEraseSdCard ? C0017R$string.master_clear_final_desc : C0017R$string.oneplus_erase_system_data_summary);
            }
            getActivity().setTitle(Utils.createAccessibleSequence(title, title + "," + textView.getText()));
        }
    }

    /* access modifiers changed from: package-private */
    public void setSubtitle() {
        if (this.mEraseEsims) {
            ((TextView) this.mContentView.findViewById(C0010R$id.sud_layout_description)).setText(C0017R$string.master_clear_final_desc_esim);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        OPUtils.setLightNavigationBar(getActivity().getWindow(), OPUtils.getThemeMode(getActivity().getContentResolver()));
        Bundle arguments = getArguments();
        boolean z = true;
        this.mEraseSdCard = arguments != null && arguments.getBoolean("erase_sd");
        if (arguments == null || !arguments.getBoolean("erase_esim")) {
            z = false;
        }
        this.mEraseEsims = z;
        if (arguments != null) {
            this.mPowerOnPsw = arguments.getString("power_on_psw");
        }
    }
}
