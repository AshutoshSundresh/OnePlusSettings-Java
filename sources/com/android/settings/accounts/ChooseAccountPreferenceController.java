package com.android.settings.accounts;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SyncAdapterType;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChooseAccountPreferenceController extends BasePreferenceController {
    private static final String TAG = "ChooseAccountPrefCtrler";
    private Map<String, List<String>> mAccountTypeToAuthorities;
    private Set<String> mAccountTypesFilter;
    private Activity mActivity;
    private AuthenticatorDescription[] mAuthDescs;
    private String[] mAuthorities;
    private final List<ProviderEntry> mProviderList = new ArrayList();
    private PreferenceScreen mScreen;
    private final Map<String, AuthenticatorDescription> mTypeToAuthDescription = new HashMap();
    private UserHandle mUserHandle;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    public ChooseAccountPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void initialize(String[] strArr, String[] strArr2, UserHandle userHandle, Activity activity) {
        this.mActivity = activity;
        this.mAuthorities = strArr;
        this.mUserHandle = userHandle;
        if (strArr2 != null) {
            this.mAccountTypesFilter = new HashSet();
            for (String str : strArr2) {
                this.mAccountTypesFilter.add(str);
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
        updateAuthDescriptions();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController, com.android.settings.core.BasePreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!(preference instanceof ProviderPreference)) {
            return false;
        }
        ProviderPreference providerPreference = (ProviderPreference) preference;
        if (Log.isLoggable(TAG, 2)) {
            Log.v(TAG, "Attempting to add account of type " + providerPreference.getAccountType());
        }
        finishWithAccountType(providerPreference.getAccountType());
        return true;
    }

    private void updateAuthDescriptions() {
        this.mAuthDescs = AccountManager.get(this.mContext).getAuthenticatorTypesAsUser(this.mUserHandle.getIdentifier());
        int i = 0;
        while (true) {
            AuthenticatorDescription[] authenticatorDescriptionArr = this.mAuthDescs;
            if (i < authenticatorDescriptionArr.length) {
                this.mTypeToAuthDescription.put(authenticatorDescriptionArr[i].type, authenticatorDescriptionArr[i]);
                i++;
            } else {
                onAuthDescriptionsUpdated();
                return;
            }
        }
    }

    private void onAuthDescriptionsUpdated() {
        Set<String> set;
        int i = 0;
        while (true) {
            AuthenticatorDescription[] authenticatorDescriptionArr = this.mAuthDescs;
            boolean z = true;
            if (i >= authenticatorDescriptionArr.length) {
                break;
            }
            String str = authenticatorDescriptionArr[i].type;
            CharSequence labelForType = getLabelForType(str);
            List<String> authoritiesForAccountType = getAuthoritiesForAccountType(str);
            String[] strArr = this.mAuthorities;
            if (strArr != null && strArr.length > 0 && authoritiesForAccountType != null) {
                int i2 = 0;
                while (true) {
                    String[] strArr2 = this.mAuthorities;
                    if (i2 >= strArr2.length) {
                        z = false;
                        break;
                    } else if (authoritiesForAccountType.contains(strArr2[i2])) {
                        break;
                    } else {
                        i2++;
                    }
                }
            }
            if (z && (set = this.mAccountTypesFilter) != null && !set.contains(str)) {
                z = false;
            }
            if (z) {
                if (!"com.oneplus.account".equals(str)) {
                    this.mProviderList.add(new ProviderEntry(labelForType, str));
                }
            } else if (Log.isLoggable(TAG, 2)) {
                Log.v(TAG, "Skipped pref " + ((Object) labelForType) + ": has no authority we need");
            }
            i++;
        }
        Context context = this.mScreen.getContext();
        if (this.mProviderList.size() == 1) {
            RestrictedLockUtils.EnforcedAdmin checkIfAccountManagementDisabled = RestrictedLockUtilsInternal.checkIfAccountManagementDisabled(context, this.mProviderList.get(0).getType(), this.mUserHandle.getIdentifier());
            if (checkIfAccountManagementDisabled != null) {
                this.mActivity.setResult(0, RestrictedLockUtils.getShowAdminSupportDetailsIntent(context, checkIfAccountManagementDisabled));
                this.mActivity.finish();
                return;
            }
            finishWithAccountType(this.mProviderList.get(0).getType());
        } else if (this.mProviderList.size() > 0) {
            Collections.sort(this.mProviderList);
            for (ProviderEntry providerEntry : this.mProviderList) {
                ProviderPreference providerPreference = new ProviderPreference(context, providerEntry.getType(), getDrawableForType(providerEntry.getType()), providerEntry.getName());
                providerPreference.setKey(providerEntry.getType().toString());
                providerPreference.checkAccountManagementAndSetDisabled(this.mUserHandle.getIdentifier());
                if (!TextUtils.isEmpty(providerEntry.getName()) && providerEntry.getName() != null) {
                    this.mScreen.addPreference(providerPreference);
                }
            }
        } else {
            if (Log.isLoggable(TAG, 2)) {
                StringBuilder sb = new StringBuilder();
                for (String str2 : this.mAuthorities) {
                    sb.append(str2);
                    sb.append(' ');
                }
                Log.v(TAG, "No providers found for authorities: " + ((Object) sb));
            }
            this.mActivity.setResult(0);
            this.mActivity.finish();
        }
    }

    private List<String> getAuthoritiesForAccountType(String str) {
        if (this.mAccountTypeToAuthorities == null) {
            this.mAccountTypeToAuthorities = Maps.newHashMap();
            SyncAdapterType[] syncAdapterTypesAsUser = ContentResolver.getSyncAdapterTypesAsUser(this.mUserHandle.getIdentifier());
            for (SyncAdapterType syncAdapterType : syncAdapterTypesAsUser) {
                List<String> list = this.mAccountTypeToAuthorities.get(syncAdapterType.accountType);
                if (list == null) {
                    list = new ArrayList<>();
                    this.mAccountTypeToAuthorities.put(syncAdapterType.accountType, list);
                }
                if (Log.isLoggable(TAG, 2)) {
                    Log.v(TAG, "added authority " + syncAdapterType.authority + " to accountType " + syncAdapterType.accountType);
                }
                list.add(syncAdapterType.authority);
            }
        }
        return this.mAccountTypeToAuthorities.get(str);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x005c A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x005d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.drawable.Drawable getDrawableForType(java.lang.String r7) {
        /*
        // Method dump skipped, instructions count: 104
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.accounts.ChooseAccountPreferenceController.getDrawableForType(java.lang.String):android.graphics.drawable.Drawable");
    }

    /* access modifiers changed from: package-private */
    public CharSequence getLabelForType(String str) {
        if (this.mTypeToAuthDescription.containsKey(str)) {
            try {
                AuthenticatorDescription authenticatorDescription = this.mTypeToAuthDescription.get(str);
                return this.mActivity.createPackageContextAsUser(authenticatorDescription.packageName, 0, this.mUserHandle).getResources().getText(authenticatorDescription.labelId);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w(TAG, "No label name for account type " + str);
            } catch (Resources.NotFoundException unused2) {
                Log.w(TAG, "No label resource for account type " + str);
            }
        }
        return null;
    }

    private void finishWithAccountType(String str) {
        Intent intent = new Intent();
        intent.putExtra("selected_account", str);
        intent.putExtra("android.intent.extra.USER", this.mUserHandle);
        this.mActivity.setResult(-1, intent);
        this.mActivity.finish();
    }
}
