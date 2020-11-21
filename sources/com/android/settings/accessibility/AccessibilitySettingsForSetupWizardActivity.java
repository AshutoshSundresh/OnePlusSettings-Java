package com.android.settings.accessibility;

import android.content.ComponentName;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.android.settings.C0010R$id;
import com.android.settings.SettingsActivity;
import com.android.settings.SetupWizardUtils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.display.FontSizePreferenceFragmentForSetupWizard;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.google.android.setupcompat.util.WizardManagerHelper;

public class AccessibilitySettingsForSetupWizardActivity extends SettingsActivity {
    static final String CLASS_NAME_FONT_SIZE_SETTINGS_FOR_SUW = "com.android.settings.FontSizeSettingsForSetupWizardActivity";

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.android.settings.SettingsActivity
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putCharSequence("activity_title", getTitle());
        super.onSaveInstanceState(bundle);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        setTitle(bundle.getCharSequence("activity_title"));
    }

    @Override // com.android.settings.core.SettingsBaseActivity
    public boolean onNavigateUp() {
        onBackPressed();
        getWindow().getDecorView().sendAccessibilityEvent(32);
        return true;
    }

    @Override // androidx.preference.PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, com.android.settings.SettingsActivity
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat preferenceFragmentCompat, Preference preference) {
        Bundle extras = preference.getExtras();
        if (extras == null) {
            extras = new Bundle();
        }
        int i = 0;
        extras.putInt("help_uri_resource", 0);
        extras.putBoolean("need_search_icon_in_action_bar", false);
        SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this);
        subSettingLauncher.setDestination(preference.getFragment());
        subSettingLauncher.setArguments(extras);
        if (preferenceFragmentCompat instanceof Instrumentable) {
            i = ((Instrumentable) preferenceFragmentCompat).getMetricsCategory();
        }
        subSettingLauncher.setSourceMetricsCategory(i);
        subSettingLauncher.launch();
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity, com.android.settings.core.SettingsBaseActivity, com.android.settings.SettingsActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        tryLaunchFontSizeSettings();
        findViewById(C0010R$id.content_parent).setFitsSystemWindows(true);
    }

    /* access modifiers changed from: package-private */
    public void tryLaunchFontSizeSettings() {
        if (WizardManagerHelper.isAnySetupWizard(getIntent()) && new ComponentName(getPackageName(), CLASS_NAME_FONT_SIZE_SETTINGS_FOR_SUW).equals(getIntent().getComponent())) {
            Bundle bundle = new Bundle();
            bundle.putInt("help_uri_resource", 0);
            bundle.putBoolean("need_search_icon_in_action_bar", false);
            SubSettingLauncher subSettingLauncher = new SubSettingLauncher(this);
            subSettingLauncher.setDestination(FontSizePreferenceFragmentForSetupWizard.class.getName());
            subSettingLauncher.setArguments(bundle);
            subSettingLauncher.setSourceMetricsCategory(0);
            Bundle extras = getIntent().getExtras();
            Bundle bundle2 = new Bundle();
            SetupWizardUtils.copyLifecycleExtra(extras, bundle2);
            subSettingLauncher.setExtras(bundle2);
            Log.d("A11ySettingsForSUW", "Launch font size settings");
            subSettingLauncher.launch();
            finish();
        }
    }
}
