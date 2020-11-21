package com.android.settings.applications.specialaccess;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0017R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.nfc.PaymentBackend;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class DefaultPaymentSettingsPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private Fragment mFragment;
    private final NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(this.mContext);
    private final PackageManager mPackageManager;
    private PaymentBackend mPaymentBackend;
    private PaymentSettingsEnabler mPaymentSettingsEnabler;
    private final UserManager mUserManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public DefaultPaymentSettingsPreferenceController(Context context, String str) {
        super(context, str);
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (!isAvailable()) {
            this.mPaymentSettingsEnabler = null;
            return;
        }
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        if (this.mNfcAdapter != null) {
            this.mPaymentSettingsEnabler = new PaymentSettingsEnabler(this.mContext, findPreference);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        PaymentSettingsEnabler paymentSettingsEnabler = this.mPaymentSettingsEnabler;
        if (paymentSettingsEnabler != null) {
            paymentSettingsEnabler.resume();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        PaymentSettingsEnabler paymentSettingsEnabler = this.mPaymentSettingsEnabler;
        if (paymentSettingsEnabler != null) {
            paymentSettingsEnabler.pause();
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!this.mPackageManager.hasSystemFeature("android.hardware.nfc") || !this.mPackageManager.hasSystemFeature("android.hardware.nfc.hce")) {
            return 3;
        }
        if (!this.mUserManager.isAdminUser()) {
            return 4;
        }
        NfcAdapter nfcAdapter = this.mNfcAdapter;
        return (nfcAdapter == null || !nfcAdapter.isEnabled()) ? 2 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mPaymentBackend == null) {
            if (this.mNfcAdapter != null) {
                PaymentBackend paymentBackend = new PaymentBackend(this.mContext);
                this.mPaymentBackend = paymentBackend;
                Fragment fragment = this.mFragment;
                if (fragment != null) {
                    paymentBackend.setFragment(fragment);
                }
            } else {
                this.mPaymentBackend = null;
            }
        }
        PaymentBackend paymentBackend2 = this.mPaymentBackend;
        if (paymentBackend2 != null) {
            paymentBackend2.refresh();
            PaymentBackend.PaymentAppInfo defaultApp = this.mPaymentBackend.getDefaultApp();
            Settings.Secure.getString(this.mContext.getContentResolver(), "nfc_payment_default_component");
            if (defaultApp != null) {
                CharSequence charSequence = defaultApp.label;
                if (!TextUtils.isEmpty(charSequence)) {
                    preference.setSummary(charSequence);
                    return;
                }
                return;
            }
            preference.setSummary(C0017R$string.app_list_preference_none);
        }
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }
}
