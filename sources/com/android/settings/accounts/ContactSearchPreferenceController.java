package com.android.settings.accounts;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.oneplus.settings.ui.OPRestrictedSwitchPreference;

public class ContactSearchPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    private UserHandle mManagedUser;

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

    @Override // com.android.settings.core.BasePreferenceController
    public int getSliceType() {
        return 1;
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

    public ContactSearchPreferenceController(Context context, String str) {
        super(context, str);
    }

    public void setManagedUser(UserHandle userHandle) {
        this.mManagedUser = userHandle;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mManagedUser != null ? 0 : 4;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference instanceof OPRestrictedSwitchPreference) {
            OPRestrictedSwitchPreference oPRestrictedSwitchPreference = (OPRestrictedSwitchPreference) preference;
            oPRestrictedSwitchPreference.setChecked(isChecked());
            UserHandle userHandle = this.mManagedUser;
            if (userHandle != null) {
                oPRestrictedSwitchPreference.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfRemoteContactSearchDisallowed(this.mContext, userHandle.getIdentifier()));
            }
        }
    }

    private boolean isChecked() {
        if (this.mManagedUser == null || Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "managed_profile_contact_remote_search", 0, this.mManagedUser.getIdentifier()) == 0) {
            return false;
        }
        return true;
    }

    private boolean setChecked(boolean z) {
        if (this.mManagedUser == null) {
            return true;
        }
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "managed_profile_contact_remote_search", z ? 1 : 0, this.mManagedUser.getIdentifier());
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public final boolean onPreferenceChange(Preference preference, Object obj) {
        return setChecked(((Boolean) obj).booleanValue());
    }
}
