package com.android.settings.accessibility;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.settings.C0008R$drawable;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import java.util.ArrayList;

public class ToggleColorInversionPreferenceFragment extends ToggleFeaturePreferenceFragment {
    private final Handler mHandler = new Handler();
    private SettingsContentObserver mSettingsContentObserver;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable, com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getMetricsCategory() {
        return 1817;
    }

    /* access modifiers changed from: protected */
    public void onPreferenceToggled(String str, boolean z) {
        AccessibilityStatsLogUtils.logAccessibilityServiceEnabled(this.mComponentName, z);
        Settings.Secure.putInt(getContentResolver(), "accessibility_display_inversion_enabled", z ? 1 : 0);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.accessibility_color_inversion_settings;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onRemoveSwitchPreferenceToggleSwitch() {
        super.onRemoveSwitchPreferenceToggleSwitch();
        this.mToggleServiceDividerSwitchPreference.setOnPreferenceClickListener(null);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void updateToggleServiceTitle(SwitchPreference switchPreference) {
        switchPreference.setTitle(C0017R$string.accessibility_display_inversion_switch_title);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onInstallSwitchPreferenceToggleSwitch() {
        super.onInstallSwitchPreferenceToggleSwitch();
        updateSwitchBarToggleSwitch();
        this.mToggleServiceDividerSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.accessibility.$$Lambda$ToggleColorInversionPreferenceFragment$UzbPsmKYXKJ7P49gBa_EeVAXlQg */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return ToggleColorInversionPreferenceFragment.this.lambda$onInstallSwitchPreferenceToggleSwitch$0$ToggleColorInversionPreferenceFragment(preference);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onInstallSwitchPreferenceToggleSwitch$0 */
    public /* synthetic */ boolean lambda$onInstallSwitchPreferenceToggleSwitch$0$ToggleColorInversionPreferenceFragment(Preference preference) {
        onPreferenceToggled(this.mPreferenceKey, ((SwitchPreference) preference).isChecked());
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mComponentName = AccessibilityShortcutController.COLOR_INVERSION_COMPONENT_NAME;
        this.mPackageName = getText(C0017R$string.accessibility_display_inversion_preference_title);
        this.mHtmlDescription = getText(C0017R$string.accessibility_display_inversion_preference_subtitle);
        this.mImageUri = new Uri.Builder().scheme("android.resource").authority(getPrefContext().getPackageName()).appendPath(String.valueOf(C0008R$drawable.accessibility_color_inversion_banner)).build();
        ArrayList arrayList = new ArrayList(1);
        arrayList.add("accessibility_display_inversion_enabled");
        this.mSettingsContentObserver = new SettingsContentObserver(this.mHandler, arrayList) {
            /* class com.android.settings.accessibility.ToggleColorInversionPreferenceFragment.AnonymousClass1 */

            public void onChange(boolean z, Uri uri) {
                ToggleColorInversionPreferenceFragment.this.updateSwitchBarToggleSwitch();
            }
        };
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateSwitchBarToggleSwitch();
        this.mSettingsContentObserver.register(getContentResolver());
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        this.mSettingsContentObserver.unregister(getContentResolver());
        super.onPause();
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_color_inversion;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.accessibility.ShortcutPreference.OnClickCallback
    public void onSettingsClicked(ShortcutPreference shortcutPreference) {
        super.onSettingsClicked(shortcutPreference);
        showDialog(1);
    }

    /* access modifiers changed from: package-private */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getUserShortcutTypes() {
        return AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), this.mComponentName);
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void updateSwitchBarToggleSwitch() {
        boolean z = false;
        if (Settings.Secure.getInt(getContentResolver(), "accessibility_display_inversion_enabled", 0) == 1) {
            z = true;
        }
        if (this.mToggleServiceDividerSwitchPreference.isChecked() != z) {
            this.mToggleServiceDividerSwitchPreference.setChecked(z);
        }
    }
}
