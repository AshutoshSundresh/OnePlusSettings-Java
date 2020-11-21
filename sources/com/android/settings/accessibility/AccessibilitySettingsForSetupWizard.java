package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;
import androidx.preference.Preference;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;

public class AccessibilitySettingsForSetupWizard extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private Preference mDisplayMagnificationPreference;
    private Preference mScreenReaderPreference;
    private Preference mSelectToSpeakPreference;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 367;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(C0019R$xml.accessibility_settings_for_setup_wizard);
        this.mDisplayMagnificationPreference = findPreference("screen_magnification_preference");
        this.mScreenReaderPreference = findPreference("screen_reader_preference");
        this.mSelectToSpeakPreference = findPreference("select_to_speak_preference");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateAccessibilityServicePreference(this.mScreenReaderPreference, "com.google.android.marvin.talkback", "com.google.android.marvin.talkback.TalkBackService", VolumeShortcutToggleScreenReaderPreferenceFragmentForSetupWizard.class.getName());
        updateAccessibilityServicePreference(this.mSelectToSpeakPreference, "com.google.android.marvin.talkback", "com.google.android.accessibility.selecttospeak.SelectToSpeakService", VolumeShortcutToggleSelectToSpeakPreferenceFragmentForSetupWizard.class.getName());
        configureMagnificationPreferenceIfNeeded(this.mDisplayMagnificationPreference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(false);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        Preference preference2 = this.mDisplayMagnificationPreference;
        if (preference2 == preference) {
            preference2.getExtras().putBoolean("from_suw", true);
        }
        return super.onPreferenceTreeClick(preference);
    }

    private AccessibilityServiceInfo findService(String str, String str2) {
        for (AccessibilityServiceInfo accessibilityServiceInfo : ((AccessibilityManager) getActivity().getSystemService(AccessibilityManager.class)).getInstalledAccessibilityServiceList()) {
            ServiceInfo serviceInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo;
            if (str.equals(serviceInfo.packageName) && str2.equals(serviceInfo.name)) {
                return accessibilityServiceInfo;
            }
        }
        return null;
    }

    private void updateAccessibilityServicePreference(Preference preference, String str, String str2, String str3) {
        AccessibilityServiceInfo findService = findService(str, str2);
        if (findService == null) {
            getPreferenceScreen().removePreference(preference);
            return;
        }
        ServiceInfo serviceInfo = findService.getResolveInfo().serviceInfo;
        String charSequence = findService.getResolveInfo().loadLabel(getPackageManager()).toString();
        preference.setTitle(charSequence);
        ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
        preference.setKey(componentName.flattenToString());
        if (AccessibilityUtil.getAccessibilityServiceFragmentType(findService) == 0) {
            preference.setFragment(str3);
        }
        Bundle extras = preference.getExtras();
        extras.putParcelable("component_name", componentName);
        extras.putString("preference_key", preference.getKey());
        extras.putString("title", charSequence);
        String loadDescription = findService.loadDescription(getPackageManager());
        if (TextUtils.isEmpty(loadDescription)) {
            loadDescription = getString(C0017R$string.accessibility_service_default_description);
        }
        extras.putString("summary", loadDescription);
        extras.putString("html_description", findService.loadHtmlDescription(getPackageManager()));
    }

    private static void configureMagnificationPreferenceIfNeeded(Preference preference) {
        Context context = preference.getContext();
        preference.setFragment(ToggleScreenMagnificationPreferenceFragmentForSetupWizard.class.getName());
        MagnificationGesturesPreferenceController.populateMagnificationGesturesPreferenceExtras(preference.getExtras(), context);
    }
}
