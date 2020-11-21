package com.android.settings.deviceinfo;

import android.accounts.Account;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.C0005R$bool;
import com.android.settings.C0017R$string;
import com.android.settings.accounts.AccountDetailDashboardFragment;
import com.android.settings.accounts.AccountFeatureProvider;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;

public class BrandedAccountPreferenceController extends BasePreferenceController {
    private final AccountFeatureProvider mAccountFeatureProvider;
    private Account[] mAccounts;

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

    public BrandedAccountPreferenceController(Context context, String str) {
        super(context, str);
        AccountFeatureProvider accountFeatureProvider = FeatureFactory.getFactory(this.mContext).getAccountFeatureProvider();
        this.mAccountFeatureProvider = accountFeatureProvider;
        this.mAccounts = accountFeatureProvider.getAccounts(this.mContext);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (!this.mContext.getResources().getBoolean(C0005R$bool.config_show_branded_account_in_device_info)) {
            return 3;
        }
        Account[] accountArr = this.mAccounts;
        return (accountArr == null || accountArr.length <= 0) ? 4 : 0;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        Account[] accountArr;
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        if (findPreference == null || !((accountArr = this.mAccounts) == null || accountArr.length == 0)) {
            findPreference.setSummary(this.mAccounts[0].name);
        } else {
            preferenceScreen.removePreference(findPreference);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return false;
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable("account", this.mAccounts[0]);
        bundle.putParcelable("user_handle", Process.myUserHandle());
        bundle.putString("account_type", this.mAccountFeatureProvider.getAccountType());
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this.mContext);
        subSettingLauncher.setDestination(AccountDetailDashboardFragment.class.getName());
        subSettingLauncher.setTitleRes(C0017R$string.account_sync_title);
        subSettingLauncher.setArguments(bundle);
        subSettingLauncher.setSourceMetricsCategory(40);
        subSettingLauncher.launch();
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        Account[] accounts = this.mAccountFeatureProvider.getAccounts(this.mContext);
        this.mAccounts = accounts;
        if (accounts == null || accounts.length == 0) {
            preference.setVisible(false);
        }
    }
}
