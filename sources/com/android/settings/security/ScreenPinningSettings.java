package com.android.settings.security;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.widget.Switch;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SwitchBar;
import com.android.settingslib.widget.FooterPreference;
import java.util.Arrays;
import java.util.List;

public class ScreenPinningSettings extends SettingsPreferenceFragment implements SwitchBar.OnSwitchChangeListener, DialogInterface.OnClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() {
        /* class com.android.settings.security.ScreenPinningSettings.AnonymousClass2 */

        @Override // com.android.settingslib.search.Indexable$SearchIndexProvider, com.android.settings.search.BaseSearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = C0019R$xml.screen_pinning_settings;
            return Arrays.asList(searchIndexableResource);
        }
    };
    private FooterPreference mFooterPreference;
    private LockPatternUtils mLockPatternUtils;
    private SwitchBar mSwitchBar;
    private SwitchPreference mUseScreenLock;
    private UserManager mUserManager;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 86;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        settingsActivity.setTitle(C0017R$string.screen_pinning_title);
        this.mLockPatternUtils = new LockPatternUtils(settingsActivity);
        this.mUserManager = (UserManager) settingsActivity.getSystemService(UserManager.class);
        addPreferencesFromResource(C0019R$xml.screen_pinning_settings);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mUseScreenLock = (SwitchPreference) preferenceScreen.findPreference("use_screen_lock");
        this.mFooterPreference = (FooterPreference) preferenceScreen.findPreference("screen_pinning_settings_screen_footer");
        SwitchBar switchBar = settingsActivity.getSwitchBar();
        this.mSwitchBar = switchBar;
        switchBar.show();
        this.mSwitchBar.setChecked(isLockToAppEnabled(getActivity()));
        this.mSwitchBar.addOnSwitchChangeListener(this);
        updateDisplay();
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_screen_pinning;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mSwitchBar.removeOnSwitchChangeListener(this);
        this.mSwitchBar.hide();
    }

    private static boolean isLockToAppEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "lock_to_app_enabled", 0) != 0;
    }

    private void setLockToAppEnabled(boolean z) {
        Settings.System.putInt(getContentResolver(), "lock_to_app_enabled", z ? 1 : 0);
        if (z) {
            setScreenLockUsedSetting(isScreenLockUsed());
        }
    }

    private boolean isScreenLockUsed() {
        return Settings.Secure.getInt(getContentResolver(), "lock_to_app_exit_locked", this.mLockPatternUtils.isSecure(UserHandle.myUserId()) ? 1 : 0) != 0;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private boolean setScreenLockUsed(boolean z) {
        if (!z || new LockPatternUtils(getActivity()).getKeyguardStoredPasswordQuality(UserHandle.myUserId()) != 0) {
            setScreenLockUsedSetting(z);
            return true;
        }
        Intent intent = new Intent("android.app.action.SET_NEW_PASSWORD");
        intent.putExtra("minimum_quality", 65536);
        startActivityForResult(intent, 43);
        return false;
    }

    private void setScreenLockUsedSetting(boolean z) {
        Settings.Secure.putInt(getContentResolver(), "lock_to_app_exit_locked", z ? 1 : 0);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 43) {
            boolean z = new LockPatternUtils(getActivity()).getKeyguardStoredPasswordQuality(UserHandle.myUserId()) != 0;
            setScreenLockUsed(z);
            this.mUseScreenLock.setChecked(z);
        }
    }

    private int getCurrentSecurityTitle() {
        int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(UserHandle.myUserId());
        if (keyguardStoredPasswordQuality != 65536) {
            if (keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608) {
                return C0017R$string.screen_pinning_unlock_pin;
            }
            if (keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288) {
                return C0017R$string.screen_pinning_unlock_password;
            }
        } else if (this.mLockPatternUtils.isLockPatternEnabled(UserHandle.myUserId())) {
            return C0017R$string.screen_pinning_unlock_pattern;
        }
        return C0017R$string.screen_pinning_unlock_none;
    }

    @Override // com.android.settings.widget.SwitchBar.OnSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        if (z) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(C0017R$string.screen_pinning_dialog_message);
            builder.setPositiveButton(C0017R$string.dlg_ok, this);
            builder.setNegativeButton(C0017R$string.dlg_cancel, this);
            builder.setCancelable(false);
            builder.show();
            return;
        }
        setLockToAppEnabled(false);
        updateDisplay();
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            setLockToAppEnabled(true);
        } else {
            this.mSwitchBar.setChecked(false);
        }
        updateDisplay();
    }

    private void updateDisplay() {
        if (isLockToAppEnabled(getActivity())) {
            this.mUseScreenLock.setVisible(true);
            this.mUseScreenLock.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                /* class com.android.settings.security.ScreenPinningSettings.AnonymousClass1 */

                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    return ScreenPinningSettings.this.setScreenLockUsed(((Boolean) obj).booleanValue());
                }
            });
            this.mUseScreenLock.setChecked(isScreenLockUsed());
            this.mUseScreenLock.setTitle(getCurrentSecurityTitle());
            return;
        }
        this.mFooterPreference.setSummary(getAppPinningContent());
        this.mUseScreenLock.setVisible(false);
    }

    private boolean isGuestModeSupported() {
        return UserManager.supportsMultipleUsers() && !this.mUserManager.hasUserRestriction("no_user_switch");
    }

    private CharSequence getAppPinningContent() {
        if (isGuestModeSupported()) {
            return getActivity().getText(C0017R$string.screen_pinning_guest_user_description);
        }
        return getActivity().getText(C0017R$string.screen_pinning_description);
    }
}
