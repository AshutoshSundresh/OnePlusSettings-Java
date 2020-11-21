package com.android.settings.security;

import android.content.Context;
import android.os.UserHandle;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0017R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.users.OwnerInfoSettings;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.ObservablePreferenceFragment;
import com.android.settingslib.core.lifecycle.events.OnResume;

public class OwnerInfoPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnResume {
    static final String KEY_OWNER_INFO = "owner_info_settings";
    private static final int MY_USER_ID = UserHandle.myUserId();
    private final LockPatternUtils mLockPatternUtils;
    private RestrictedPreference mOwnerInfoPref;
    private final ObservablePreferenceFragment mParent;

    public interface OwnerInfoCallback {
        void onOwnerInfoUpdated();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_OWNER_INFO;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public OwnerInfoPreferenceController(Context context, ObservablePreferenceFragment observablePreferenceFragment) {
        super(context);
        this.mParent = observablePreferenceFragment;
        this.mLockPatternUtils = new LockPatternUtils(context);
        if (observablePreferenceFragment != null) {
            observablePreferenceFragment.getSettingsLifecycle().addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mOwnerInfoPref = (RestrictedPreference) preferenceScreen.findPreference(KEY_OWNER_INFO);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        updateEnableState();
        updateSummary();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(getPreferenceKey(), preference.getKey())) {
            return false;
        }
        OwnerInfoSettings.show(this.mParent);
        return true;
    }

    public void updateEnableState() {
        if (this.mOwnerInfoPref != null) {
            if (isDeviceOwnerInfoEnabled()) {
                this.mOwnerInfoPref.setDisabledByAdmin(getDeviceOwner());
                return;
            }
            this.mOwnerInfoPref.setDisabledByAdmin(null);
            this.mOwnerInfoPref.setEnabled(!this.mLockPatternUtils.isLockScreenDisabled(MY_USER_ID));
        }
    }

    public void updateSummary() {
        String str;
        if (this.mOwnerInfoPref == null) {
            return;
        }
        if (isDeviceOwnerInfoEnabled()) {
            this.mOwnerInfoPref.setSummary(getDeviceOwnerInfo());
            return;
        }
        RestrictedPreference restrictedPreference = this.mOwnerInfoPref;
        if (isOwnerInfoEnabled()) {
            str = getOwnerInfo();
        } else {
            str = this.mContext.getString(C0017R$string.owner_info_settings_summary);
        }
        restrictedPreference.setSummary(str);
    }

    /* access modifiers changed from: package-private */
    public boolean isDeviceOwnerInfoEnabled() {
        return this.mLockPatternUtils.isDeviceOwnerInfoEnabled();
    }

    /* access modifiers changed from: package-private */
    public String getDeviceOwnerInfo() {
        return this.mLockPatternUtils.getDeviceOwnerInfo();
    }

    /* access modifiers changed from: package-private */
    public boolean isOwnerInfoEnabled() {
        return this.mLockPatternUtils.isOwnerInfoEnabled(MY_USER_ID);
    }

    /* access modifiers changed from: package-private */
    public String getOwnerInfo() {
        return this.mLockPatternUtils.getOwnerInfo(MY_USER_ID);
    }

    /* access modifiers changed from: package-private */
    public RestrictedLockUtils.EnforcedAdmin getDeviceOwner() {
        return RestrictedLockUtilsInternal.getDeviceOwner(this.mContext);
    }
}
