package com.android.settings.password;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SetupEncryptionInterstitial;
import com.android.settings.SetupWizardUtils;
import com.android.settings.password.ChooseLockGeneric;
import com.android.settings.utils.SettingsDividerItemDecoration;
import com.google.android.setupdesign.GlifPreferenceLayout;

public class SetupChooseLockGeneric extends ChooseLockGeneric {
    /* access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.password.ChooseLockGeneric
    public boolean isValidFragment(String str) {
        return SetupChooseLockGenericFragment.class.getName().equals(str);
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.password.ChooseLockGeneric
    public Class<? extends PreferenceFragmentCompat> getFragmentClass() {
        return SetupChooseLockGenericFragment.class;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity
    public void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        super.onApplyThemeResource(theme, SetupWizardUtils.getTheme(getIntent()), z);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.android.settings.core.SettingsBaseActivity, com.android.settings.SettingsActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getIntent().hasExtra("requested_min_complexity")) {
            IBinder activityToken = getActivityToken();
            if (!PasswordUtils.isCallingAppPermitted(this, activityToken, "android.permission.REQUEST_PASSWORD_COMPLEXITY")) {
                PasswordUtils.crashCallingApplication(activityToken, "Must have permission android.permission.REQUEST_PASSWORD_COMPLEXITY to use extra android.app.extra.PASSWORD_COMPLEXITY");
                finish();
                return;
            }
        }
        findViewById(C0010R$id.content_parent).setFitsSystemWindows(true);
    }

    public static class SetupChooseLockGenericFragment extends ChooseLockGeneric.ChooseLockGenericFragment {
        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        public boolean canRunBeforeDeviceProvisioned() {
            return true;
        }

        @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onViewCreated(View view, Bundle bundle) {
            super.onViewCreated(view, bundle);
            GlifPreferenceLayout glifPreferenceLayout = (GlifPreferenceLayout) view;
            glifPreferenceLayout.setDividerItemDecoration(new SettingsDividerItemDecoration(getContext()));
            glifPreferenceLayout.setDividerInset(getContext().getResources().getDimensionPixelSize(C0007R$dimen.sud_items_glif_text_divider_inset));
            glifPreferenceLayout.setIcon(getContext().getDrawable(C0008R$drawable.ic_lock));
            int i = isForBiometric() ? C0017R$string.lock_settings_picker_title : C0017R$string.setup_lock_settings_picker_title;
            if (getActivity() != null) {
                getActivity().setTitle(i);
            }
            glifPreferenceLayout.setHeaderText(i);
            setDivider(null);
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        public void addHeaderView() {
            if (isForBiometric()) {
                setHeaderView(C0012R$layout.setup_choose_lock_generic_biometrics_header);
            } else {
                setHeaderView(C0012R$layout.setup_choose_lock_generic_header);
            }
        }

        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment, androidx.fragment.app.Fragment
        public void onActivityResult(int i, int i2, Intent intent) {
            if (intent == null) {
                intent = new Intent();
            }
            intent.putExtra(":settings:password_quality", new LockPatternUtils(getActivity()).getKeyguardStoredPasswordQuality(UserHandle.myUserId()));
            super.onActivityResult(i, i2, intent);
        }

        @Override // androidx.preference.PreferenceFragmentCompat
        public RecyclerView onCreateRecyclerView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            return ((GlifPreferenceLayout) viewGroup).onCreateRecyclerView(layoutInflater, viewGroup, bundle);
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        public Class<? extends ChooseLockGeneric.InternalActivity> getInternalActivityClass() {
            return InternalActivity.class;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        public void disableUnusablePreferences(int i, boolean z) {
            super.disableUnusablePreferencesImpl(Math.max(i, 65536), true);
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        public void addPreferences() {
            if (isForBiometric()) {
                super.addPreferences();
            } else {
                addPreferencesFromResource(C0019R$xml.setup_security_settings_picker);
            }
        }

        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
        public boolean onPreferenceTreeClick(Preference preference) {
            if (!"unlock_set_do_later".equals(preference.getKey())) {
                return super.onPreferenceTreeClick(preference);
            }
            SetupSkipDialog.newInstance(getActivity().getIntent().getBooleanExtra(":settings:frp_supported", false), false, false, false, false).show(getFragmentManager());
            return true;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        public Intent getLockPasswordIntent(int i) {
            Context context = getContext();
            Intent lockPasswordIntent = super.getLockPasswordIntent(i);
            SetupChooseLockPassword.modifyIntentForSetup(context, lockPasswordIntent);
            SetupWizardUtils.copySetupExtras(getActivity().getIntent(), lockPasswordIntent);
            return lockPasswordIntent;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        public Intent getLockPatternIntent() {
            Context context = getContext();
            Intent lockPatternIntent = super.getLockPatternIntent();
            SetupChooseLockPattern.modifyIntentForSetup(context, lockPatternIntent);
            SetupWizardUtils.copySetupExtras(getActivity().getIntent(), lockPatternIntent);
            return lockPatternIntent;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        public Intent getEncryptionInterstitialIntent(Context context, int i, boolean z, Intent intent) {
            Intent createStartIntent = SetupEncryptionInterstitial.createStartIntent(context, i, z, intent);
            SetupWizardUtils.copySetupExtras(getActivity().getIntent(), createStartIntent);
            return createStartIntent;
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
        public Intent getBiometricEnrollIntent(Context context) {
            Intent biometricEnrollIntent = super.getBiometricEnrollIntent(context);
            SetupWizardUtils.copySetupExtras(getActivity().getIntent(), biometricEnrollIntent);
            return biometricEnrollIntent;
        }

        private boolean isForBiometric() {
            return this.mForFingerprint || this.mForFace;
        }
    }

    public static class InternalActivity extends ChooseLockGeneric.InternalActivity {

        public static class InternalSetupChooseLockGenericFragment extends ChooseLockGeneric.ChooseLockGenericFragment {
            /* access modifiers changed from: protected */
            @Override // com.android.settings.password.ChooseLockGeneric.ChooseLockGenericFragment
            public boolean canRunBeforeDeviceProvisioned() {
                return true;
            }
        }

        /* access modifiers changed from: protected */
        @Override // com.android.settings.SettingsActivity, com.android.settings.password.ChooseLockGeneric
        public boolean isValidFragment(String str) {
            return InternalSetupChooseLockGenericFragment.class.getName().equals(str);
        }

        /* access modifiers changed from: package-private */
        @Override // com.android.settings.password.ChooseLockGeneric
        public Class<? extends Fragment> getFragmentClass() {
            return InternalSetupChooseLockGenericFragment.class;
        }
    }
}
