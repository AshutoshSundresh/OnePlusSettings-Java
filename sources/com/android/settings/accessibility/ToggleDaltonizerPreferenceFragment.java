package com.android.settings.accessibility;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.settings.C0003R$array;
import com.android.settings.C0017R$string;
import com.android.settings.C0019R$xml;
import com.android.settings.accessibility.DaltonizerRadioButtonPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ToggleDaltonizerPreferenceFragment extends ToggleFeaturePreferenceFragment implements DaltonizerRadioButtonPreferenceController.OnChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(C0019R$xml.accessibility_daltonizer_settings);
    private static final List<AbstractPreferenceController> sControllers = new ArrayList();
    private final Handler mHandler = new Handler();
    private SettingsContentObserver mSettingsContentObserver;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable, com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public int getMetricsCategory() {
        return 5;
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        String[] stringArray;
        if (sControllers.size() == 0) {
            for (String str : context.getResources().getStringArray(C0003R$array.daltonizer_mode_keys)) {
                sControllers.add(new DaltonizerRadioButtonPreferenceController(context, lifecycle, str));
            }
        }
        return sControllers;
    }

    @Override // com.android.settings.accessibility.DaltonizerRadioButtonPreferenceController.OnChangeListener
    public void onCheckedChanged(Preference preference) {
        for (AbstractPreferenceController abstractPreferenceController : sControllers) {
            abstractPreferenceController.updateState(preference);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mComponentName = AccessibilityShortcutController.DALTONIZER_COMPONENT_NAME;
        this.mPackageName = getText(C0017R$string.accessibility_display_daltonizer_preference_title);
        this.mHtmlDescription = getText(C0017R$string.accessibility_display_daltonizer_preference_subtitle);
        ArrayList arrayList = new ArrayList(1);
        arrayList.add("accessibility_display_daltonizer_enabled");
        this.mSettingsContentObserver = new SettingsContentObserver(this.mHandler, arrayList) {
            /* class com.android.settings.accessibility.ToggleDaltonizerPreferenceFragment.AnonymousClass1 */

            public void onChange(boolean z, Uri uri) {
                ToggleDaltonizerPreferenceFragment.this.updateSwitchBarToggleSwitch();
            }
        };
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        updatePreferenceOrder();
    }

    private List<String> getPreferenceOrderList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("daltonizer_preview");
        arrayList.add("use_service");
        arrayList.add("daltonizer_mode_category");
        arrayList.add("general_categories");
        arrayList.add("introduction_categories");
        return arrayList;
    }

    private void updatePreferenceOrder() {
        List<String> preferenceOrderList = getPreferenceOrderList();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.setOrderingAsAdded(false);
        int size = preferenceOrderList.size();
        for (int i = 0; i < size; i++) {
            Preference findPreference = preferenceScreen.findPreference(preferenceOrderList.get(i));
            if (findPreference != null) {
                findPreference.setOrder(i);
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onResume() {
        super.onResume();
        updateSwitchBarToggleSwitch();
        this.mSettingsContentObserver.register(getContentResolver());
        Iterator<AbstractPreferenceController> it = buildPreferenceControllers(getPrefContext(), getSettingsLifecycle()).iterator();
        while (it.hasNext()) {
            DaltonizerRadioButtonPreferenceController daltonizerRadioButtonPreferenceController = (DaltonizerRadioButtonPreferenceController) it.next();
            daltonizerRadioButtonPreferenceController.setOnChangeListener(this);
            daltonizerRadioButtonPreferenceController.displayPreference(getPreferenceScreen());
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onPause() {
        this.mSettingsContentObserver.unregister(getContentResolver());
        Iterator<AbstractPreferenceController> it = buildPreferenceControllers(getPrefContext(), getSettingsLifecycle()).iterator();
        while (it.hasNext()) {
            ((DaltonizerRadioButtonPreferenceController) it.next()).setOnChangeListener(null);
        }
        super.onPause();
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return C0017R$string.help_url_color_correction;
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return C0019R$xml.accessibility_daltonizer_settings;
    }

    /* access modifiers changed from: protected */
    public void onPreferenceToggled(String str, boolean z) {
        AccessibilityStatsLogUtils.logAccessibilityServiceEnabled(this.mComponentName, z);
        Settings.Secure.putInt(getContentResolver(), "accessibility_display_daltonizer_enabled", z ? 1 : 0);
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
        switchPreference.setTitle(C0017R$string.accessibility_daltonizer_master_switch_title);
    }

    /* access modifiers changed from: protected */
    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    public void onInstallSwitchPreferenceToggleSwitch() {
        super.onInstallSwitchPreferenceToggleSwitch();
        updateSwitchBarToggleSwitch();
        this.mToggleServiceDividerSwitchPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            /* class com.android.settings.accessibility.$$Lambda$ToggleDaltonizerPreferenceFragment$9ERKh2G0qgPiWJri_uVUZWOO2g */

            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return ToggleDaltonizerPreferenceFragment.this.lambda$onInstallSwitchPreferenceToggleSwitch$0$ToggleDaltonizerPreferenceFragment(preference);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onInstallSwitchPreferenceToggleSwitch$0 */
    public /* synthetic */ boolean lambda$onInstallSwitchPreferenceToggleSwitch$0$ToggleDaltonizerPreferenceFragment(Preference preference) {
        onPreferenceToggled(this.mPreferenceKey, ((SwitchPreference) preference).isChecked());
        return false;
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
        if (Settings.Secure.getInt(getContentResolver(), "accessibility_display_daltonizer_enabled", 0) == 1) {
            z = true;
        }
        if (this.mToggleServiceDividerSwitchPreference.isChecked() != z) {
            this.mToggleServiceDividerSwitchPreference.setChecked(z);
        }
    }
}
