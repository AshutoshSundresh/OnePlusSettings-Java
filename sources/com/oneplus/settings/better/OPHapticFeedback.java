package com.oneplus.settings.better;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.C0012R$layout;
import com.android.settings.C0019R$xml;
import com.android.settings.SettingsPreferenceFragment;
import com.oneplus.settings.SettingsBaseApplication;
import com.oneplus.settings.utils.OPUtils;
import java.util.ArrayList;
import java.util.List;

public class OPHapticFeedback extends SettingsPreferenceFragment {
    private Context mContext;
    private List<String> mHapticFeedbackAppList = new ArrayList();
    public List<PackageInfo> mHapticFeedbackInstalledApps = new ArrayList();
    private PreferenceCategory mHapticFeedbackSupportCategory;
    private PackageManager mPackageManager;
    private SwitchPreference mSwitchPreference;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 9999;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        loadConfig();
        PackageManager packageManager = this.mContext.getPackageManager();
        this.mPackageManager = packageManager;
        for (PackageInfo packageInfo : packageManager.getInstalledPackages(0)) {
            if (this.mHapticFeedbackAppList.contains(packageInfo.packageName)) {
                this.mHapticFeedbackInstalledApps.add(packageInfo);
            }
        }
    }

    private void loadConfig() {
        this.mHapticFeedbackAppList.clear();
        String[] stringArray = this.mContext.getResources().getStringArray(84017172);
        if (stringArray != null && stringArray.length > 0) {
            for (String str : stringArray) {
                try {
                    String[] split = str.split(";");
                    if (split != null && split.length == 3) {
                        this.mHapticFeedbackAppList.add(split[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        addPreferencesFromResource(C0019R$xml.op_haptic_feedback);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mHapticFeedbackSupportCategory = (PreferenceCategory) preferenceScreen.findPreference("op_haptic_feedback_support_category");
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference("op_haptic_feedback_switch");
        this.mSwitchPreference = switchPreference;
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /* class com.oneplus.settings.better.OPHapticFeedback.AnonymousClass1 */

            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                OPHapticFeedback.setHapticFeedbackState(OPHapticFeedback.this.mContext, ((Boolean) obj).booleanValue());
                return true;
            }
        });
        refreshUI();
        super.onViewCreated(view, bundle);
    }

    private void refreshUI() {
        this.mSwitchPreference.setChecked(getHapticFeedbackState(this.mContext));
        if (!this.mHapticFeedbackInstalledApps.isEmpty()) {
            Preference findPreference = this.mHapticFeedbackSupportCategory.findPreference("op_haptic_feedback_no_app");
            if (findPreference != null) {
                findPreference.setVisible(false);
            }
            for (PackageInfo packageInfo : this.mHapticFeedbackInstalledApps) {
                Preference preference = new Preference(this.mContext);
                preference.setLayoutResource(C0012R$layout.op_preference_material);
                preference.setIconSpaceReserved(true);
                preference.setSelectable(false);
                preference.setKey(packageInfo.packageName);
                preference.setIcon(packageInfo.applicationInfo.loadIcon(this.mPackageManager));
                preference.setTitle(packageInfo.applicationInfo.loadLabel(this.mPackageManager).toString());
                this.mHapticFeedbackSupportCategory.addPreference(preference);
            }
        }
    }

    public static boolean getHapticFeedbackState(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(), "op_game_mode_vibrate_feedback", 1, -2) == 1;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: boolean */
    /* JADX WARN: Multi-variable type inference failed */
    public static void setHapticFeedbackState(Context context, boolean z) {
        Settings.System.putIntForUser(context.getContentResolver(), "op_game_mode_vibrate_feedback", z ? 1 : 0, -2);
        OPUtils.sendAppTracker("game_mode_haptic", (int) z);
    }

    public static void sendDefaultAppTracker() {
        OPUtils.sendAppTracker("game_mode_haptic", getHapticFeedbackState(SettingsBaseApplication.mApplication) ? 1 : 0);
    }
}
