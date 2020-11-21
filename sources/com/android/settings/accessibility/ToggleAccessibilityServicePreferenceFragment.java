package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0010R$id;
import com.android.settings.C0017R$string;
import com.android.settings.password.ConfirmDeviceCredentialActivity;
import com.android.settingslib.accessibility.AccessibilityUtils;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ToggleAccessibilityServicePreferenceFragment extends ToggleFeaturePreferenceFragment {
    private Dialog mDialog;
    private AtomicBoolean mIsDialogShown = new AtomicBoolean(false);
    private LockPatternUtils mLockPatternUtils;
    private final SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(new Handler()) {
        /* class com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment.AnonymousClass1 */

        public void onChange(boolean z, Uri uri) {
            ToggleAccessibilityServicePreferenceFragment.this.updateSwitchBarToggleSwitch();
        }
    };

    @Override // com.android.settingslib.core.instrumentation.Instrumentable, com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getMetricsCategory() {
        return 4;
    }

    @Override // androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mLockPatternUtils = new LockPatternUtils(getPrefContext());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateSwitchBarToggleSwitch();
        this.mSettingsContentObserver.register(getContentResolver());
    }

    public void onPreferenceToggled(String str, boolean z) {
        ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
        AccessibilityStatsLogUtils.logAccessibilityServiceEnabled(unflattenFromString, z);
        AccessibilityUtils.setAccessibilityServiceState(getPrefContext(), unflattenFromString, z);
    }

    /* access modifiers changed from: package-private */
    public AccessibilityServiceInfo getAccessibilityServiceInfo() {
        List<AccessibilityServiceInfo> installedAccessibilityServiceList = AccessibilityManager.getInstance(getPrefContext()).getInstalledAccessibilityServiceList();
        int size = installedAccessibilityServiceList.size();
        for (int i = 0; i < size; i++) {
            AccessibilityServiceInfo accessibilityServiceInfo = installedAccessibilityServiceList.get(i);
            ResolveInfo resolveInfo = accessibilityServiceInfo.getResolveInfo();
            if (this.mComponentName.getPackageName().equals(resolveInfo.serviceInfo.packageName) && this.mComponentName.getClassName().equals(resolveInfo.serviceInfo.name)) {
                return accessibilityServiceInfo;
            }
        }
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        switch (i) {
            case 1002:
                AccessibilityServiceInfo accessibilityServiceInfo = getAccessibilityServiceInfo();
                if (accessibilityServiceInfo != null) {
                    this.mDialog = AccessibilityServiceWarning.createCapabilitiesDialog(getPrefContext(), accessibilityServiceInfo, new View.OnClickListener() {
                        /* class com.android.settings.accessibility.$$Lambda$ToggleAccessibilityServicePreferenceFragment$SnUGD4ts_YwSIcwwVM_bFsc6ys8 */

                        public final void onClick(View view) {
                            ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromEnableToggleClicked(view);
                        }
                    });
                    break;
                } else {
                    return null;
                }
            case 1003:
                AccessibilityServiceInfo accessibilityServiceInfo2 = getAccessibilityServiceInfo();
                if (accessibilityServiceInfo2 != null) {
                    this.mDialog = AccessibilityServiceWarning.createCapabilitiesDialog(getPrefContext(), accessibilityServiceInfo2, new View.OnClickListener() {
                        /* class com.android.settings.accessibility.$$Lambda$c9pnez0JrK86T1it1LYXu2cpAlY */

                        public final void onClick(View view) {
                            ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromShortcutClicked(view);
                        }
                    });
                    break;
                } else {
                    return null;
                }
            case 1004:
                AccessibilityServiceInfo accessibilityServiceInfo3 = getAccessibilityServiceInfo();
                if (accessibilityServiceInfo3 != null) {
                    this.mDialog = AccessibilityServiceWarning.createCapabilitiesDialog(getPrefContext(), accessibilityServiceInfo3, new View.OnClickListener() {
                        /* class com.android.settings.accessibility.$$Lambda$UAyrFU1BXFerRIYREdfP0q_bgo4 */

                        public final void onClick(View view) {
                            ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromShortcutToggleClicked(view);
                        }
                    });
                    break;
                } else {
                    return null;
                }
            case 1005:
                AccessibilityServiceInfo accessibilityServiceInfo4 = getAccessibilityServiceInfo();
                if (accessibilityServiceInfo4 != null) {
                    this.mDialog = AccessibilityServiceWarning.createDisableDialog(getPrefContext(), accessibilityServiceInfo4, new DialogInterface.OnClickListener() {
                        /* class com.android.settings.accessibility.$$Lambda$ToggleAccessibilityServicePreferenceFragment$aATlirkNK_S2WQ1NBifJArmYKj0 */

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ToggleAccessibilityServicePreferenceFragment.this.onDialogButtonFromDisableToggleClicked(dialogInterface, i);
                        }
                    });
                    break;
                } else {
                    return null;
                }
            default:
                this.mDialog = super.onCreateDialog(i);
                break;
        }
        return this.mDialog;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i == 1008) {
            return 1810;
        }
        switch (i) {
            case 1002:
            case 1003:
            case 1004:
                return 583;
            case 1005:
                return 584;
            default:
                return super.getDialogMetricsCategory(i);
        }
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getUserShortcutTypes() {
        return AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), this.mComponentName);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void updateToggleServiceTitle(SwitchPreference switchPreference) {
        String str;
        AccessibilityServiceInfo accessibilityServiceInfo = getAccessibilityServiceInfo();
        if (accessibilityServiceInfo == null) {
            str = "";
        } else {
            str = getString(C0017R$string.accessibility_service_master_switch_title, accessibilityServiceInfo.getResolveInfo().loadLabel(getPackageManager()));
        }
        switchPreference.setTitle(str);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSwitchBarToggleSwitch() {
        boolean contains = AccessibilityUtils.getEnabledServicesFromSettings(getPrefContext()).contains(this.mComponentName);
        if (this.mToggleServiceDividerSwitchPreference.isChecked() != contains) {
            this.mToggleServiceDividerSwitchPreference.setChecked(contains);
        }
    }

    private boolean isFullDiskEncrypted() {
        return StorageManager.isNonDefaultBlockEncrypted();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != 1) {
            return;
        }
        if (i2 == -1) {
            handleConfirmServiceEnabled(true);
            if (isFullDiskEncrypted()) {
                this.mLockPatternUtils.clearEncryptionPassword();
                Settings.Global.putInt(getContentResolver(), "require_password_to_decrypt", 0);
                return;
            }
            return;
        }
        handleConfirmServiceEnabled(false);
    }

    private boolean isServiceSupportAccessibilityButton() {
        ServiceInfo serviceInfo;
        for (AccessibilityServiceInfo accessibilityServiceInfo : ((AccessibilityManager) getPrefContext().getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList()) {
            if (!((accessibilityServiceInfo.flags & 256) == 0 || (serviceInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo) == null || !TextUtils.equals(serviceInfo.name, getAccessibilityServiceInfo().getResolveInfo().serviceInfo.name))) {
                return true;
            }
        }
        return false;
    }

    private void handleConfirmServiceEnabled(boolean z) {
        this.mToggleServiceDividerSwitchPreference.setChecked(z);
        getArguments().putBoolean("checked", z);
        onPreferenceToggled(this.mPreferenceKey, z);
    }

    private String createConfirmCredentialReasonMessage() {
        int i = C0017R$string.enable_service_password_reason;
        int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(UserHandle.myUserId());
        if (keyguardStoredPasswordQuality == 65536) {
            i = C0017R$string.enable_service_pattern_reason;
        } else if (keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608) {
            i = C0017R$string.enable_service_pin_reason;
        }
        return getString(i, getAccessibilityServiceInfo().getResolveInfo().loadLabel(getPackageManager()));
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onInstallSwitchPreferenceToggleSwitch() {
        super.onInstallSwitchPreferenceToggleSwitch();
        this.mToggleServiceDividerSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.accessibility.$$Lambda$ToggleAccessibilityServicePreferenceFragment$hklejte6z615DlETCqt5jDEKtII */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return ToggleAccessibilityServicePreferenceFragment.this.onPreferenceClick(preference);
            }
        });
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onToggleClicked(ShortcutPreference shortcutPreference) {
        int userShortcutTypes = getUserShortcutTypes(getPrefContext(), 1);
        if (!shortcutPreference.isChecked()) {
            AccessibilityUtil.optOutAllValuesFromSettings(getPrefContext(), userShortcutTypes, this.mComponentName);
        } else if (!this.mToggleServiceDividerSwitchPreference.isChecked()) {
            shortcutPreference.setChecked(false);
            showPopupDialog(1004);
        } else {
            AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), userShortcutTypes, this.mComponentName);
            showPopupDialog(1008);
        }
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onSettingsClicked(ShortcutPreference shortcutPreference) {
        super.onSettingsClicked(shortcutPreference);
        int i = 1;
        if (!(this.mShortcutPreference.isChecked() || this.mToggleServiceDividerSwitchPreference.isChecked())) {
            i = 1003;
        }
        showPopupDialog(i);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onProcessArguments(Bundle bundle) {
        super.onProcessArguments(bundle);
        String string = bundle.getString("settings_title");
        String string2 = bundle.getString("settings_component_name");
        if (!TextUtils.isEmpty(string) && !TextUtils.isEmpty(string2)) {
            Intent component = new Intent("android.intent.action.MAIN").setComponent(ComponentName.unflattenFromString(string2.toString()));
            if (!getPackageManager().queryIntentActivities(component, 0).isEmpty()) {
                this.mSettingsTitle = string;
                this.mSettingsIntent = component;
                setHasOptionsMenu(true);
            }
        }
        this.mComponentName = (ComponentName) bundle.getParcelable("component_name");
        this.mImageUri = new Uri.Builder().scheme("android.resource").authority(this.mComponentName.getPackageName()).appendPath(String.valueOf(bundle.getInt("animated_image_res"))).build();
        this.mPackageName = getAccessibilityServiceInfo().getResolveInfo().loadLabel(getPackageManager());
    }

    /* access modifiers changed from: private */
    public void onDialogButtonFromDisableToggleClicked(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            handleConfirmServiceEnabled(true);
        } else if (i == -1) {
            handleConfirmServiceEnabled(false);
        } else {
            throw new IllegalArgumentException("Unexpected button identifier");
        }
    }

    /* access modifiers changed from: private */
    public void onDialogButtonFromEnableToggleClicked(View view) {
        int id = view.getId();
        if (id == C0010R$id.permission_enable_allow_button) {
            onAllowButtonFromEnableToggleClicked();
        } else if (id == C0010R$id.permission_enable_deny_button) {
            onDenyButtonFromEnableToggleClicked();
        } else {
            throw new IllegalArgumentException("Unexpected view id");
        }
    }

    private void onAllowButtonFromEnableToggleClicked() {
        if (isFullDiskEncrypted()) {
            startActivityForResult(ConfirmDeviceCredentialActivity.createIntent(createConfirmCredentialReasonMessage(), null), 1);
        } else {
            handleConfirmServiceEnabled(true);
            if (isServiceSupportAccessibilityButton()) {
                this.mIsDialogShown.set(false);
                showPopupDialog(1008);
            }
        }
        this.mDialog.dismiss();
    }

    private void onDenyButtonFromEnableToggleClicked() {
        handleConfirmServiceEnabled(false);
        this.mDialog.dismiss();
    }

    /* access modifiers changed from: package-private */
    public void onDialogButtonFromShortcutToggleClicked(View view) {
        int id = view.getId();
        if (id == C0010R$id.permission_enable_allow_button) {
            onAllowButtonFromShortcutToggleClicked();
        } else if (id == C0010R$id.permission_enable_deny_button) {
            onDenyButtonFromShortcutToggleClicked();
        } else {
            throw new IllegalArgumentException("Unexpected view id");
        }
    }

    private void onAllowButtonFromShortcutToggleClicked() {
        this.mShortcutPreference.setChecked(true);
        AccessibilityUtil.optInAllValuesToSettings(getPrefContext(), getUserShortcutTypes(getPrefContext(), 1), this.mComponentName);
        this.mIsDialogShown.set(false);
        showPopupDialog(1008);
        this.mDialog.dismiss();
        this.mShortcutPreference.setSummary(getShortcutTypeSummary(getPrefContext()));
    }

    private void onDenyButtonFromShortcutToggleClicked() {
        this.mShortcutPreference.setChecked(false);
        this.mDialog.dismiss();
    }

    /* access modifiers changed from: package-private */
    public void onDialogButtonFromShortcutClicked(View view) {
        int id = view.getId();
        if (id == C0010R$id.permission_enable_allow_button) {
            onAllowButtonFromShortcutClicked();
        } else if (id == C0010R$id.permission_enable_deny_button) {
            onDenyButtonFromShortcutClicked();
        } else {
            throw new IllegalArgumentException("Unexpected view id");
        }
    }

    private void onAllowButtonFromShortcutClicked() {
        this.mIsDialogShown.set(false);
        showPopupDialog(1);
        this.mDialog.dismiss();
    }

    private void onDenyButtonFromShortcutClicked() {
        this.mDialog.dismiss();
    }

    /* access modifiers changed from: private */
    public boolean onPreferenceClick(Preference preference) {
        if (((DividerSwitchPreference) preference).isChecked()) {
            this.mToggleServiceDividerSwitchPreference.setChecked(false);
            getArguments().putBoolean("checked", false);
            if (!this.mShortcutPreference.isChecked()) {
                showPopupDialog(1002);
            } else {
                handleConfirmServiceEnabled(true);
                if (isServiceSupportAccessibilityButton()) {
                    showPopupDialog(1008);
                }
            }
        } else {
            this.mToggleServiceDividerSwitchPreference.setChecked(true);
            getArguments().putBoolean("checked", true);
            showDialog(1005);
        }
        return true;
    }

    private void showPopupDialog(int i) {
        if (this.mIsDialogShown.compareAndSet(false, true)) {
            showDialog(i);
            setOnDismissListener(new DialogInterface.OnDismissListener() {
                /* class com.android.settings.accessibility.$$Lambda$ToggleAccessibilityServicePreferenceFragment$rsnwOut3xJQX3ZBJNVzHwQGqLyY */

                public final void onDismiss(DialogInterface dialogInterface) {
                    ToggleAccessibilityServicePreferenceFragment.this.lambda$showPopupDialog$0$ToggleAccessibilityServicePreferenceFragment(dialogInterface);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showPopupDialog$0 */
    public /* synthetic */ void lambda$showPopupDialog$0$ToggleAccessibilityServicePreferenceFragment(DialogInterface dialogInterface) {
        this.mIsDialogShown.compareAndSet(true, false);
    }
}
