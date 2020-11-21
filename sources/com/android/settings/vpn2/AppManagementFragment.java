package com.android.settings.vpn2;

import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.IConnectivityManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import com.android.internal.net.VpnConfig;
import com.android.internal.util.ArrayUtils;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.vpn2.AppDialogFragment;
import com.android.settings.vpn2.ConfirmLockdownFragment;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.RestrictedSwitchPreference;

public class AppManagementFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, ConfirmLockdownFragment.ConfirmLockdownListener {
    private ConnectivityManager mConnectivityManager;
    private IConnectivityManager mConnectivityService;
    private DevicePolicyManager mDevicePolicyManager;
    private final AppDialogFragment.Listener mForgetVpnDialogFragmentListener = new AppDialogFragment.Listener() {
        /* class com.android.settings.vpn2.AppManagementFragment.AnonymousClass1 */

        @Override // com.android.settings.vpn2.AppDialogFragment.Listener
        public void onCancel() {
        }

        @Override // com.android.settings.vpn2.AppDialogFragment.Listener
        public void onForget() {
            if (AppManagementFragment.this.isVpnAlwaysOn()) {
                AppManagementFragment.this.setAlwaysOnVpn(false, false);
            }
            AppManagementFragment.this.finish();
        }
    };
    private PackageInfo mPackageInfo;
    private PackageManager mPackageManager;
    private String mPackageName;
    private RestrictedSwitchPreference mPreferenceAlwaysOn;
    private RestrictedPreference mPreferenceForget;
    private RestrictedSwitchPreference mPreferenceLockdown;
    private Preference mPreferenceVersion;
    private final int mUserId = UserHandle.myUserId();
    private String mVpnLabel;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 100;
    }

    public static void show(Context context, AppPreference appPreference, int i) {
        Bundle bundle = new Bundle();
        bundle.putString("package", appPreference.getPackageName());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(context);
        subSettingLauncher.setDestination(AppManagementFragment.class.getName());
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setTitleText(appPreference.getLabel());
        subSettingLauncher.setSourceMetricsCategory(i);
        subSettingLauncher.setUserHandle(new UserHandle(appPreference.getUserId()));
        subSettingLauncher.launch();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.vpn_app_management);
        this.mPackageManager = getContext().getPackageManager();
        this.mDevicePolicyManager = (DevicePolicyManager) getContext().getSystemService(DevicePolicyManager.class);
        this.mConnectivityManager = (ConnectivityManager) getContext().getSystemService(ConnectivityManager.class);
        this.mConnectivityService = IConnectivityManager.Stub.asInterface(ServiceManager.getService("connectivity"));
        this.mPreferenceVersion = findPreference("version");
        this.mPreferenceAlwaysOn = (RestrictedSwitchPreference) findPreference("always_on_vpn");
        this.mPreferenceLockdown = (RestrictedSwitchPreference) findPreference("lockdown_vpn");
        this.mPreferenceForget = (RestrictedPreference) findPreference("forget_vpn");
        this.mPreferenceAlwaysOn.setOnPreferenceChangeListener(this);
        this.mPreferenceLockdown.setOnPreferenceChangeListener(this);
        this.mPreferenceForget.setOnPreferenceClickListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        if (loadInfo()) {
            this.mPreferenceVersion.setTitle(getPrefContext().getString(C0017R$string.vpn_version, this.mPackageInfo.versionName));
            updateUI();
            return;
        }
        finish();
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (((key.hashCode() == -591389790 && key.equals("forget_vpn")) ? (char) 0 : 65535) == 0) {
            return onForgetVpnClick();
        }
        Log.w("AppManagementFragment", "unknown key is clicked: " + key);
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x002c  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x005a  */
    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onPreferenceChange(androidx.preference.Preference r6, java.lang.Object r7) {
        /*
        // Method dump skipped, instructions count: 107
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.vpn2.AppManagementFragment.onPreferenceChange(androidx.preference.Preference, java.lang.Object):boolean");
    }

    private boolean onForgetVpnClick() {
        updateRestrictedViews();
        if (!this.mPreferenceForget.isEnabled()) {
            return false;
        }
        AppDialogFragment.show(this, this.mForgetVpnDialogFragmentListener, this.mPackageInfo, this.mVpnLabel, true, true);
        return true;
    }

    private boolean onAlwaysOnVpnClick(boolean z, boolean z2) {
        boolean isAnotherVpnActive = isAnotherVpnActive();
        boolean isAnyLockdownActive = VpnUtils.isAnyLockdownActive(getActivity());
        if (!ConfirmLockdownFragment.shouldShow(isAnotherVpnActive, isAnyLockdownActive, z2)) {
            return setAlwaysOnVpnByUI(z, z2);
        }
        ConfirmLockdownFragment.show(this, isAnotherVpnActive, z, isAnyLockdownActive, z2, null);
        return false;
    }

    @Override // com.android.settings.vpn2.ConfirmLockdownFragment.ConfirmLockdownListener
    public void onConfirmLockdown(Bundle bundle, boolean z, boolean z2) {
        setAlwaysOnVpnByUI(z, z2);
    }

    private boolean setAlwaysOnVpnByUI(boolean z, boolean z2) {
        updateRestrictedViews();
        if (!this.mPreferenceAlwaysOn.isEnabled()) {
            return false;
        }
        if (this.mUserId == 0) {
            VpnUtils.clearLockdownVpn(getContext());
        }
        boolean alwaysOnVpn = setAlwaysOnVpn(z, z2);
        if (!z || (alwaysOnVpn && isVpnAlwaysOn())) {
            updateUI();
        } else {
            CannotConnectFragment.show(this, this.mVpnLabel);
        }
        return alwaysOnVpn;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean setAlwaysOnVpn(boolean z, boolean z2) {
        return this.mConnectivityManager.setAlwaysOnVpnPackageForUser(this.mUserId, z ? this.mPackageName : null, z2, null);
    }

    private void updateUI() {
        if (isAdded()) {
            boolean isVpnAlwaysOn = isVpnAlwaysOn();
            boolean z = isVpnAlwaysOn && VpnUtils.isAnyLockdownActive(getActivity());
            this.mPreferenceAlwaysOn.setChecked(isVpnAlwaysOn);
            this.mPreferenceLockdown.setChecked(z);
            updateRestrictedViews();
        }
    }

    private void updateRestrictedViews() {
        if (isAdded()) {
            this.mPreferenceAlwaysOn.checkRestrictionAndSetDisabled("no_config_vpn", this.mUserId);
            this.mPreferenceLockdown.checkRestrictionAndSetDisabled("no_config_vpn", this.mUserId);
            this.mPreferenceForget.checkRestrictionAndSetDisabled("no_config_vpn", this.mUserId);
            if (this.mPackageName.equals(this.mDevicePolicyManager.getAlwaysOnVpnPackage())) {
                RestrictedLockUtils.EnforcedAdmin profileOrDeviceOwner = RestrictedLockUtils.getProfileOrDeviceOwner(getContext(), UserHandle.of(this.mUserId));
                this.mPreferenceAlwaysOn.setDisabledByAdmin(profileOrDeviceOwner);
                this.mPreferenceForget.setDisabledByAdmin(profileOrDeviceOwner);
                if (this.mDevicePolicyManager.isAlwaysOnVpnLockdownEnabled()) {
                    this.mPreferenceLockdown.setDisabledByAdmin(profileOrDeviceOwner);
                }
            }
            if (this.mConnectivityManager.isAlwaysOnVpnPackageSupportedForUser(this.mUserId, this.mPackageName)) {
                this.mPreferenceAlwaysOn.setSummary(C0017R$string.vpn_always_on_summary);
                return;
            }
            this.mPreferenceAlwaysOn.setEnabled(false);
            this.mPreferenceLockdown.setEnabled(false);
            this.mPreferenceAlwaysOn.setSummary(C0017R$string.vpn_always_on_summary_not_supported);
        }
    }

    private String getAlwaysOnVpnPackage() {
        return this.mConnectivityManager.getAlwaysOnVpnPackageForUser(this.mUserId);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean isVpnAlwaysOn() {
        return this.mPackageName.equals(getAlwaysOnVpnPackage());
    }

    private boolean loadInfo() {
        Bundle arguments = getArguments();
        if (arguments == null) {
            Log.e("AppManagementFragment", "empty bundle");
            return false;
        }
        String string = arguments.getString("package");
        this.mPackageName = string;
        if (string == null) {
            Log.e("AppManagementFragment", "empty package name");
            return false;
        }
        try {
            this.mPackageInfo = this.mPackageManager.getPackageInfo(string, 0);
            this.mVpnLabel = VpnConfig.getVpnLabel(getPrefContext(), this.mPackageName).toString();
            if (this.mPackageInfo.applicationInfo == null) {
                Log.e("AppManagementFragment", "package does not include an application");
                return false;
            } else if (appHasVpnPermission(getContext(), this.mPackageInfo.applicationInfo)) {
                return true;
            } else {
                Log.e("AppManagementFragment", "package didn't register VPN profile");
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("AppManagementFragment", "package not found", e);
            return false;
        }
    }

    static boolean appHasVpnPermission(Context context, ApplicationInfo applicationInfo) {
        return !ArrayUtils.isEmpty(((AppOpsManager) context.getSystemService("appops")).getOpsForPackage(applicationInfo.uid, applicationInfo.packageName, new int[]{47, 94}));
    }

    private boolean isAnotherVpnActive() {
        try {
            VpnConfig vpnConfig = this.mConnectivityService.getVpnConfig(this.mUserId);
            if (vpnConfig == null || TextUtils.equals(vpnConfig.user, this.mPackageName)) {
                return false;
            }
            return true;
        } catch (RemoteException e) {
            Log.w("AppManagementFragment", "Failure to look up active VPN", e);
            return false;
        }
    }

    public static class CannotConnectFragment extends InstrumentedDialogFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 547;
        }

        public static void show(AppManagementFragment appManagementFragment, String str) {
            if (appManagementFragment.getFragmentManager().findFragmentByTag("CannotConnect") == null) {
                Bundle bundle = new Bundle();
                bundle.putString("label", str);
                CannotConnectFragment cannotConnectFragment = new CannotConnectFragment();
                cannotConnectFragment.setArguments(bundle);
                cannotConnectFragment.show(appManagementFragment.getFragmentManager(), "CannotConnect");
            }
        }

        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            String string = getArguments().getString("label");
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getActivity().getString(C0017R$string.vpn_cant_connect_title, new Object[]{string}));
            builder.setMessage(getActivity().getString(C0017R$string.vpn_cant_connect_message));
            builder.setPositiveButton(C0017R$string.okay, (DialogInterface.OnClickListener) null);
            return builder.create();
        }
    }
}
